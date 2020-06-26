package com.xt.mobile.terminal.util;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 类名        ：FastJsonTools
 *
 * 描述        ：fastjson 工具类
 *
 * 创建人    ：pengyongshun
 *
 * 日期        ：2017-07-28
 *
 * */
 
public class FastJsonTools {
    private final static String TAG = "FastJsonTools";
    
    static {
        //初始化Fastjson的参数，防止对大小写转换
        TypeUtils.compatibleWithJavaBean = true;
    }
    
    private static void Loge(String log) {
        Log.e(TAG, log);
    }
    
    // 把JSON文本parse为JavaBean
    public static <T> T json2BeanObject(String json, Type type) {
        T bean = null;
        try {
            bean = JSON.parseObject(json,type);
        } catch (Exception e) {
            Loge("FastJsonTools -- Exception : " + e.getMessage() + ",json = " + json);
        }

        return bean;
    }
    
    public static <T> List<T> jsonToList(String json, Class<T> type) {
        return JSONArray.parseArray(json,type);
    }
    
     //将JavaBean序列化为JSON文本
    public static <T> String bean2Json(T bean) {
        String json = JSON.toJSONString(bean);
        return json;
    }
    
    //移除多余字段的数据
    public static String removeJsonData(String jsonData, String[] keys) {
        try
        {
            JSONObject jsonObject = JSON.parseObject(jsonData);
            for(String key :  keys) {
                jsonObject.remove(key);
            }
            String newJson = jsonObject.toString();
            return newJson;
        } catch (Exception e)
        {
            // 需要移除的字段不存在
            e.printStackTrace();
            // 跳过该字段，继续处理
            return jsonData;
        }
    }

    /**
     * 转换一个xml格式的字符串到json格式
     *
     * @param xml
     *            xml格式的字符串
     * @return 成功返回json 格式的字符串;失败反回null
     */
    @SuppressWarnings("unchecked")
    public static  String xml2JSON(String xml) {
        JSONObject obj = new JSONObject();
        try {
            InputStream is = new ByteArrayInputStream(xml.getBytes("utf-8"));
            SAXBuilder sb = new SAXBuilder();
            Document doc = sb.build(is);
            Element root = doc.getRootElement();
            obj.put(root.getName(), iterateElement(root));
            return obj.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 一个迭代方法
     *
     * @param element
     *            : org.jdom.Element
     * @return java.util.Map 实例
     */
    @SuppressWarnings("unchecked")
    private static Map iterateElement(Element element) {
        List jiedian = element.getChildren();
        Element et = null;
        Map obj = new HashMap();
        List list = null;
        for (int i = 0; i < jiedian.size(); i++) {
            list = new LinkedList();
            et = (Element) jiedian.get(i);
            if (et.getTextTrim().equals("")) {
                if (et.getChildren().size() == 0)
                    continue;
                if (obj.containsKey(et.getName())) {
                    list = (List) obj.get(et.getName());
                }
                list.add(iterateElement(et));
                obj.put(et.getName(), list);
            } else {
                if (obj.containsKey(et.getName())) {
                    list = (List) obj.get(et.getName());
                }
                list.add(et.getTextTrim());
                obj.put(et.getName(), list);
            }
        }
        return obj;
    }
    

}
