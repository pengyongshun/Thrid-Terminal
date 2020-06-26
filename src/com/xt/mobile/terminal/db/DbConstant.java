package com.xt.mobile.terminal.db;


/**
 * 数据库相关信息
 */
public class DbConstant {
	/**
	 * 共同拥有的
	 */
	public class BaseDB{
		//数据库后缀
		public static final String DB_HZ="-journal";
		//行id  这个必须要
		public static final String COLUMN_SIGNIN_USER_ID="_id";
		//数据库名
		public static final String DB_NAME="peoCar.db";
		//数据库版本号
		public static final int DB_VERSION=1;
	}
	/**
	 * 人员信息
	 */
	public class PeopleDB{

		//数据库中创建的表名
		public static final String PEOPLE_TABLE_NAME="people";
		//表中对应的字段名
		//姓名
		public static final String COLUMN_PEOPLE_XM="name";
		//性别
		public static final String COLUMN_PEOPLE_XB="sex";
		//民族
		public static final String COLUMN_PEOPLE_MZ="nation";
		//出生日期
		public static final String COLUMN_PEOPLE_CSRQ="borth";
		//住址
		public static final String COLUMN_PEOPLE_ZZXZ="address";
		//身份证号
		public static final String COLUMN_PEOPLE_SFZH="card";
		//相似度
		public static final String COLUMN_PEOPLE_XSD="xsd";
		//盘查时间
		public static final String COLUMN_PEOPLE_PCSJ="pcsj";
		//获取身份证上的照片(base64格式)
		public static final String COLUMN_PEOPLE_ZP1="zp1";
		//现场拍摄的照片近照(base64格式)
		public static final String COLUMN_PEOPLE_ZP2="zp2";
		//现场拍摄的照片远照(base64格式)
		public static final String COLUMN_PEOPLE_ZP3="zp3";
		//人员状态是否上传
		public static final String COLUMN_PEOPLE_RYZT="ryzt";
		//是否是重点人员
		public static final String COLUMN_PEOPLE_RYLX="rylx";
		//盘查车辆的时候，绑定的绑定当前的车牌号，如果只是盘查人员信息，就为空
		public static final String COLUMN_PEOPLE_PCCPH="pccph";
	}

	/**
	 * 车辆信息
	 */
	public class CarDB{
		//数据库中创建的表名
		public static final String CAR_TABLE_NAME="car";
		//表中对应的字段名
		//车牌号码
		public static final String COLUMN_CAR_HPHM="hphm";
		//车牌种类
		public static final String COLUMN_CAR_CPZL="cpzl";
		//车底照片1(base64格式)
		public static final String COLUMN_CAR_ZP1="zp1";
		//车底照片2(base64格式)
		public static final String COLUMN_CAR_ZP2="zp2";
		//其他照片(base64格式)
		public static final String COLUMN_CAR_ZP3="zp3";
		//盘查时间
		public static final String COLUMN_CAR_PCSJ="pcsj";
		//是否是重点车辆：逃逸等等
		public static final String COLUMN_CAR_CLLX="cllx";
		//是否上传
		public static final String COLUMN_CAR_CLZT="clzt";
		//是否稽查
		public static final String COLUMN_CAR_CLJCZT="cljczt";

	}

}
