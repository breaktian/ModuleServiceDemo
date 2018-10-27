package com.example.biz_login.moduleservice;

import com.example.shell.moduleservice.stub.LoginServiceStub;

public class LoginService implements LoginServiceStub {

    @Override
    public void login(String name, String password, final LoginCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(System.currentTimeMillis()%2==0){
                    callback.onSuccess(new LoginData("登录成功"));
                }else{
                    callback.onFail(-1,"登录失败");
                }

            }
        }).start();
    }
}
