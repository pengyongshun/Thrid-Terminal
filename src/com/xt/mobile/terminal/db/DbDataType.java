package com.xt.mobile.terminal.db;

/**
 * @Author:pengyongshun
 * @Desc: 数据库属性数据类型
 * @Time:2017/9/25
 */
public class DbDataType {

    /**
     * 人员信息
     */
    public class PeopleType{
        //姓名
        public static final String PEOPLE_XM="char[12]";
        //性别
        public static final String PEOPLE_XB="char[12]";
        //民族
        public static final String PEOPLE_MZ="char[12]";
        //出生日期
        public static final String PEOPLE_CSRQ="char[12]";
        //住址
        public static final String PEOPLE_ZZXZ="char[12]";
        //身份证号
        public static final String PEOPLE_SFZH="char[12]";
        //相似度
        public static final String PEOPLE_XSD="char[12]";
        //盘查时间
        public static final String PEOPLE_PCSJ="char[12]";
        //获取身份证上的照片(base64格式)
        public static final String PEOPLE_ZP1="char[255]";
        //现场拍摄的照片近照(base64格式)
        public static final String PEOPLE_ZP2="char[255]";
        //现场拍摄的照片远照(base64格式)
        public static final String PEOPLE_ZP3="char[255]";
        //人员状态是否上传
        public static final String PEOPLE_RYZT="char[12]";
        //是否是重点人员
        public static final String PEOPLE_RYLX="char[12]";
        //盘查车辆的时候，绑定的绑定当前的车牌号，如果只是盘查人员信息，就为空
        public static final String PEOPLE_PCCPH="char[12]";

    }

    /**
     * 车辆信息
     */
    public class CarType {
        //车牌号码
        public static final String CAR_HPHM="char[12]";
        //车牌种类
        public static final String CAR_CPZL="char[12]";
        //车底照片1(base64格式)
        public static final String CAR_ZP1="char[12]";
        //车底照片2(base64格式)
        public static final String CAR_ZP2="char[12]";
        //其他照片(base64格式)
        public static final String CAR_ZP3="char[12]";
        //盘查时间
        public static final String CAR_PCSJ="char[12]";
        //是否是重点车辆：逃逸等等
        public static final String CAR_CLLX="char[12]";
        //是否上传
        public static final String CAR_CLZT="char[12]";
        //是否稽查
        public static final String CAR_CLJCZT="char[12]";
    }

}
