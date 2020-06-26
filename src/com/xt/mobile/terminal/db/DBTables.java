package com.xt.mobile.terminal.db;

import java.util.HashMap;

/**
 * 创建数据库中的表
 */

public class DBTables {

    /**
     * 创建人员表
     * @param dbHelper
     * @param tableName  表名
     */
    public static void createPeopleTable(DBHelper dbHelper,
                                         String tableName) {
        HashMap<String, String> tableField = new HashMap<String, String>();
        //姓名
        tableField.put(DbConstant.PeopleDB.COLUMN_PEOPLE_XM, DbDataType.PeopleType.PEOPLE_XM);
        //性别
        tableField.put(DbConstant.PeopleDB.COLUMN_PEOPLE_XB, DbDataType.PeopleType.PEOPLE_XB);
        //民族
        tableField.put(DbConstant.PeopleDB.COLUMN_PEOPLE_MZ, DbDataType.PeopleType.PEOPLE_MZ);
        //出生日期
        tableField.put(DbConstant.PeopleDB.COLUMN_PEOPLE_CSRQ, DbDataType.PeopleType.PEOPLE_CSRQ);
        //住址
        tableField.put(DbConstant.PeopleDB.COLUMN_PEOPLE_ZZXZ, DbDataType.PeopleType.PEOPLE_ZZXZ);
        //身份证号
        tableField.put(DbConstant.PeopleDB.COLUMN_PEOPLE_SFZH, DbDataType.PeopleType.PEOPLE_SFZH);
        //相似度
        tableField.put(DbConstant.PeopleDB.COLUMN_PEOPLE_XSD, DbDataType.PeopleType.PEOPLE_XSD);
        //盘查时间
        tableField.put(DbConstant.PeopleDB.COLUMN_PEOPLE_PCSJ, DbDataType.PeopleType.PEOPLE_PCSJ);
        //获取身份证上的照片
        tableField.put(DbConstant.PeopleDB.COLUMN_PEOPLE_ZP1, DbDataType.PeopleType.PEOPLE_ZP1);
        //现场拍摄的照片近照
        tableField.put(DbConstant.PeopleDB.COLUMN_PEOPLE_ZP2, DbDataType.PeopleType.PEOPLE_ZP2);
        //现场拍摄的照片远照
        tableField.put(DbConstant.PeopleDB.COLUMN_PEOPLE_ZP3, DbDataType.PeopleType.PEOPLE_ZP3);
        //人员状态是否上传
        tableField.put(DbConstant.PeopleDB.COLUMN_PEOPLE_RYZT, DbDataType.PeopleType.PEOPLE_RYZT);
        //是否是重点人员
        tableField.put(DbConstant.PeopleDB.COLUMN_PEOPLE_RYLX, DbDataType.PeopleType.PEOPLE_RYLX);
        //盘查车辆的时候，绑定的绑定当前的车牌号，如果只是盘查人员信息，就为空
        tableField.put(DbConstant.PeopleDB.COLUMN_PEOPLE_PCCPH, DbDataType.PeopleType.PEOPLE_PCCPH);
        dbHelper.creatTB(tableName, tableField);
    }
    /**
     * 创建车辆表
     * @param dbHelper
     * @param tableName  表名
     */
    public static void createCarTable(DBHelper dbHelper,
                                         String tableName) {
        HashMap<String, String> tableField = new HashMap<String, String>();
        //车牌号码
        tableField.put(DbConstant.CarDB.COLUMN_CAR_HPHM, DbDataType.CarType.CAR_HPHM);
        //车牌种类
        tableField.put(DbConstant.CarDB.COLUMN_CAR_CPZL, DbDataType.CarType.CAR_CPZL);
        //车底照片1(base64格式)
        tableField.put(DbConstant.CarDB.COLUMN_CAR_ZP1, DbDataType.CarType.CAR_ZP1);
        //车底照片2(base64格式)
        tableField.put(DbConstant.CarDB.COLUMN_CAR_ZP2, DbDataType.CarType.CAR_ZP2);
        //其他照片(base64格式)
        tableField.put(DbConstant.CarDB.COLUMN_CAR_ZP3, DbDataType.CarType.CAR_ZP3);
        //盘查时间
        tableField.put(DbConstant.CarDB.COLUMN_CAR_PCSJ, DbDataType.CarType.CAR_PCSJ);
        //是否是重点车辆：逃逸等等
        tableField.put(DbConstant.CarDB.COLUMN_CAR_CLLX, DbDataType.CarType.CAR_CLLX);
        //是否上传
        tableField.put(DbConstant.CarDB.COLUMN_CAR_CLZT, DbDataType.CarType.CAR_CLZT);
        //是否稽查
        tableField.put(DbConstant.CarDB.COLUMN_CAR_CLJCZT, DbDataType.CarType.CAR_CLJCZT);
        dbHelper.creatTB(tableName, tableField);
    }

}
