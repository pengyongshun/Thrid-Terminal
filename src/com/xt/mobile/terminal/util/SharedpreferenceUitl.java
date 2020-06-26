package com.xt.mobile.terminal.util;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by 彭永顺 on 2018/6/13.
 */
public class SharedpreferenceUitl {
    private  SharedPreferences sp;

    public SharedpreferenceUitl(String fileName, Context context) {
        sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     * @param key
     * @param object
     */
    public void write(String key, Object object){

        String type = object.getClass().getSimpleName();

        SharedPreferences.Editor editor = sp.edit();

        if("String".equals(type)){

            editor.putString(key, (String)object);

        } else if("Integer".equals(type)){
            editor.putInt(key, (Integer)object);

        } else if("Boolean".equals(type)){

            editor.putBoolean(key, (Boolean)object);

        } else if("Float".equals(type)){

            editor.putFloat(key, (Float)object);

        } else if("Long".equals(type)){

            editor.putLong(key, (Long)object);
        }

        editor.commit();
    }


    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     * @param key
     * @param defaultObject
     * @return
     */
    public  Object read( String key, Object defaultObject){
        String type = defaultObject.getClass().getSimpleName();

        if("String".equals(type)){
            return (String)(sp.getString(key, (String)defaultObject));
        }

        else if("Integer".equals(type)){
            return (int)(sp.getInt(key, (Integer)defaultObject));
        }

        else if("Boolean".equals(type)){
            return (boolean)(sp.getBoolean(key, (Boolean)defaultObject));
        }

        else if("Float".equals(type)){
            return (float)(sp.getFloat(key, (Float)defaultObject));
        }

        else if("Long".equals(type)){
            return (long)(sp.getLong(key, (Long)defaultObject));
        }

        return null;
    }

    /**
     * 清除所有数据
     */
    public void clearAll() {
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().commit();
    }

    /**
     * 清除指定数据
     */
    public void clearKey(String key) {
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }
}
