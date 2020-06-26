package com.xt.mobile.terminal.db;

import android.os.Environment;

/**
 * @Author:pengyongshun
 * @Desc:  专门存放路径
 * @Time:2017/9/22
 */
public class DbPath {
//==========================================基本路径==========================================================//
    //内置sd卡路径
    public static final String N_SDCARD_PATH= Environment.
            getExternalStorageDirectory().getAbsolutePath();

    //基本路径
    public static final String BASE_PATH=N_SDCARD_PATH+"/unistrong/peocarquery";


//==========================================数据库路径==========================================================//
    //人员车辆
    public static final String DB_DBPATH =  "/PeopleCarDB/";


}
