package com.example.shell.moduleservice.core;

import android.content.Context;
import android.util.Log;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 模块服务者
 *
 * 利用临时文件和动态代理实现解耦
 *
 * ModuleServicer.getDefault().create(ModuleStub.class).testMethod("oh this from mainActivity!",getApplicationContext(), textView);
 *
 * */
public class ModuleServicer {
    private static final String TAG = "ModuleServicer";
    private Map<Class<?>, InvocationHandler> mInvocationHandlerMap = new HashMap<>();
    private Map<Class<?>, Object> mShadowBeanMap = new HashMap<>();
    private HashMap<String,String> mProtocols;

    static public ModuleServicer getDefault() {
        return Holder.instance;
    }

    static class Holder {
        static ModuleServicer instance = new ModuleServicer();
    }

    private ModuleServicer() {
    }

    /**
     * 加载协议表
     * @param context
     * @param assetFile
     * */
    public void init(Context context, String assetFile){
        try {
            mProtocols = ProtocolParser.parse(context,assetFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用入口
     *
     * @param stub interface
     * @param <T>  clazz
     * @return obj clazz
     */
    public <T> T create(Class<T> stub) {
        if (mShadowBeanMap.get(stub) != null) {
            return (T) mShadowBeanMap.get(stub);
        }
        InvocationHandler handler = null;
        try {
            handler = findHandler(stub);
        } catch (Exception e) {
            Log.e(TAG,"findHandler Exception:" + e.getMessage());
            throw new RuntimeException("error! findHandler!");
        }
        T result = (T) Proxy.newProxyInstance(stub.getClassLoader(), new Class[]{stub}, handler);
        mShadowBeanMap.put(stub, result);
        return result;
    }

    private InvocationHandler findHandler(Class stub) throws ClassNotFoundException, RuntimeException {
        if (mInvocationHandlerMap.keySet().contains(stub)) {
            return mInvocationHandlerMap.get(stub);
        }

        //目标类的全路径
        String targetClazzName;
        if(stub.isAnnotationPresent(ServiceTarget.class)){
            ServiceTarget serviceTarget = (ServiceTarget) stub.getAnnotation(ServiceTarget.class);
            targetClazzName = serviceTarget.value();
        }else{
            targetClazzName = mProtocols.get(stub.getName());
        }
        if (targetClazzName == null
                || targetClazzName.equals("")
                || targetClazzName.equals("null")) {
            throw new RuntimeException("error! targetClazzName null");
        }
        Log.d(TAG,"==>find target Class: :" + targetClazzName);
        final Class targetClazz = Class.forName(targetClazzName);
        Object targetObj = getObjectByClazz(targetClazz);
        if (targetObj == null) {
            throw new RuntimeException("error! targetObj is null!");
        }

        //checkMethods
        if(!checkMethod(stub, targetClazz)){
            throw new RuntimeException("error! checkMethod find wrong Method, please check your method!");
        }

        final Object action = targetObj;
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //拦截替换方法;方法名称必须一样
                Method realMethod = targetClazz.getDeclaredMethod(method.getName(), method.getParameterTypes());
                realMethod.setAccessible(true);
                return realMethod.invoke(action, args);
            }
        };
        mInvocationHandlerMap.put(stub, handler);
        return handler;
    }


    //提供方法校验;
    private boolean checkMethod(Class stub, Class clazz){
        //checkMethods
        List<String> listCheckedWrongMethods = new ArrayList<>();
        Method[] listMethods = stub.getMethods();
        Method[] listTargetMethods = clazz.getMethods();
        if(listMethods!=null&& listMethods.length>0){
            if(listTargetMethods==null || listTargetMethods.length==0){
                Log.e(TAG,"You maybe miss all methods Imp,Please check it");
                return false;
            }
            for(Method method :listMethods){
                String methodName = method.getName();
                Class<?>[] methodParams =method.getParameterTypes();
                //开始匹配
                boolean bFind =false;
                for(Method methodTarget:listTargetMethods){
                    String methodNameTarget = methodTarget.getName();
                    Class<?>[] methodParamsTarget =methodTarget.getParameterTypes();
                    //方法名一致;
                    if(methodName.equals(methodNameTarget)){
                        //参数一致
                        if((methodParams==null && methodParamsTarget==null)){
                            bFind =true;
                            break;
                        }
                        //参数一致
                        if(methodParams!=null && methodParamsTarget!=null &&  methodParams.length==methodParamsTarget.length){
                            int length =  methodParams.length;
                            if(length==0){
                                bFind =true;
                                break;
                            }
                            if(length>0){
                                boolean bFetchWrongParams = false;
                                for(int i=0;i<length;i++){
                                    if(!methodParams[i].getName().equals(methodParamsTarget[i].getName())){
                                        bFetchWrongParams = true;
                                        break;
                                    }
                                }
                                if(!bFetchWrongParams){
                                    bFind =true;
                                    break;
                                }
                            }
                        }
                    }
                }
                if(!bFind){
                    listCheckedWrongMethods.add(methodName);
                }
            }

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("You maybe miss these methods Imp:\n\n");
            for(String methodName:listCheckedWrongMethods){
                stringBuilder.append(methodName).append("\n");
            }
            stringBuilder.append("\nPlease check it!\n");
            Log.e(TAG,stringBuilder.toString());
        }
        return listCheckedWrongMethods.size()==0 ? true : false;
    }

    //缺省采用 无参数构造bean
    private Object getObjectByClazz(Class clazz) {
        try {
            Constructor constructor = clazz.getConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


}