package com.example.keketian.moduleservicedemo.moduleservice;

import android.content.Context;
import android.widget.Toast;

import com.example.shell.moduleservice.stub.ToastServiceStub;

public class ToastService implements ToastServiceStub{
    @Override
    public void toast(Context context, String msg) {
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }
}
