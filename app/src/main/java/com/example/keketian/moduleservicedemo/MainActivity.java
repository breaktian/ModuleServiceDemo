package com.example.keketian.moduleservicedemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.biz_login.LoginActivity;
import com.example.biz_usercenter.UserCenterActivity;
import com.example.keketian.moduleservicedemo.moduleservice.ToastService;
import com.example.shell.moduleservice.core.ModuleServicer;
import com.example.shell.moduleservice.stub.LoginServiceStub;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        findViewById(R.id.btn_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userCenter();
            }
        });

    }

    private void userCenter() {
        startActivity(new Intent(this, UserCenterActivity.class));
    }

    private void login() {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
