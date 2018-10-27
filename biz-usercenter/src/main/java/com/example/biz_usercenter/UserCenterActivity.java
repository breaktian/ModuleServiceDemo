package com.example.biz_usercenter;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.shell.moduleservice.core.ModuleServicer;
import com.example.shell.moduleservice.stub.LoginServiceStub;
import com.example.shell.moduleservice.stub.ToastServiceStub;

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
