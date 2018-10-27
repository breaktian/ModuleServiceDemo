# ModuleService
模块间解耦库，解决module之间的依赖问题

### 问题背景
项目大了之后，module增多了，尤其是业务模块之间的依赖关系变得复杂，而且gradle不允许双向依赖，
比如说biz-a模块依赖biz-b模块，如果biz-b模块想使用biz-a模块的服务怎么办？biz-b再去依赖biz-a  gradle编译会出错

### 模块关系
demo项目的模块：App、biz-login、biz-usercenter、shell。其中shell模块是公共模块，他们的依赖关系是App依赖biz-login、biz-usercenter、biz-shell，
biz-login依赖shell，biz-usercenter依赖shell。其实shell就是一个公共模块。biz-login和biz-usercenter没有依赖关系。
### 使用
 * 比如说biz-Login模块想要提供一个login的服务, 按照如下步骤即可：
  1. 在shell中写一个接口
  ```
  public interface LoginServiceStub {

    void login(String name, String password, LoginCallback callback);

    interface LoginCallback{
        void onSuccess(LoginData data);
        void onFail(int code, String msg);
    }

    class LoginData{
        public String result;

        public LoginData(String result) {
            this.result = result;
        }
    }


}
  ```
 
2. 在biz-login模块中实现这个接口
```
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

```
4. 在app的assets目录下新建一个module-service.xml,并且把这个服务注册进去
```
<?xml version="1.0" encoding="utf-8" ?>
<root>
    <module-service>
        <stub-class>com.breaktian.shell.moduleservice.stub.LoginServiceStub</stub-class>
        <target-class>com.breaktian.login.moduleservice.LoginService</target-class>
    </module-service>
    

</root>

```
5. 在biz-usercenter模块中使用这个接口
```
public class UserCenterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);

        ModuleServicer.getDefault().create(ToastServiceStub.class).toast(this,"测试moduleService");

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    private void login() {
        ModuleServicer.getDefault().create(LoginServiceStub.class).login("breaktian", "123456", new LoginServiceStub.LoginCallback() {
            @Override
            public void onSuccess(final LoginServiceStub.LoginData data) {
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UserCenterActivity.this,data.result,Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public void onFail(int code, final String msg) {
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UserCenterActivity.this,msg,Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }
}
```
6. 最后，别忘了在app的application加载协议表（这个是应该在第5步之前就应该做的）
```
public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initModuleService();
    }

    private void initModuleService() {
        ModuleServicer.getDefault().init(getApplicationContext(),"module-service.xml");
    }
}
```
当然，任何模块都可以提供服务，而且任何模块都能使用协议中已经存在的服务。这样之前繁杂冗余的业务模块之间的依赖就没有了，还可以提高编译的速度。
