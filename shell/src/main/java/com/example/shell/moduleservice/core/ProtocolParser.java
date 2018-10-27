package com.example.shell.moduleservice.core;

import android.content.Context;
import android.content.res.AssetManager;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by keke.tian on 2017/7/31.
 * xml解析器
 */

public class ProtocolParser {

    public static HashMap<String,String> parse(Context context, String assetName) throws Exception {
        //获取AssetManager管理器对象
        AssetManager as = context.getAssets();
        //通过AssetManager的open方法获取到beauties.xml文件的输入流
        InputStream is = as.open(assetName);
        //通过获取到的InputStream来得到InputSource实例
        InputSource is2 = new InputSource(is);
        //使用工厂方法初始化SAXParserFactory变量spf
        SAXParserFactory spf = SAXParserFactory.newInstance();
        //通过SAXParserFactory得到SAXParser的实例
        SAXParser sp = spf.newSAXParser();
        //通过SAXParser得到XMLReader的实例
        XMLReader xr = sp.getXMLReader();
        //初始化自定义的类MySaxHandler的变量msh，将beautyList传递给它，以便装载数据
        SaxHandler saxHandler = new SaxHandler();
        //把对象saxHandler传给xr
        xr.setContentHandler(saxHandler);
        //调用xr的parse方法解析输入流
        xr.parse(is2);

        return saxHandler.getProtocols();
    }


}
