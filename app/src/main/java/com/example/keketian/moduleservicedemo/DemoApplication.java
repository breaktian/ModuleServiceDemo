package com.example.keketian.moduleservicedemo;

import android.app.Application;

import com.example.shell.moduleservice.core.ModuleServicer;

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
