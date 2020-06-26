package com.xt.mobile.terminal.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;


import com.xt.mobile.terminal.util.TimeUitls;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 数据库的帮助类
 */
public class DBHelper {
    // Database's name
    private String DB_NAME;
    // Table's name
    private String TB_NAME;
    //version
    private int DB_VERSION;
    // A Context object that we passed in from a calling class
    private static Context mContext;
    //
    private SQLiteDatabase mSqLiteDatabase = null;
    //
    private DataBaseHelper mDataBaseHelper = null;

    public DBHelper(Context context) {
        // TODO Auto-generated constructor stub
        mContext = context;
    }

    public DBHelper(Context context, String db_dir ,String db_name, String tb_name , int version) {
        // TODO Auto-generated constructor stub
        this.mContext = context;
        this.DB_NAME = createDdPath(db_dir,db_name);
        this.TB_NAME = tb_name;
        this.DB_VERSION=version;
    }

    /***
     *
     * 升级数据库
     * 参数1: db  数据库
     * 参数2: oldVersion 上一个版本号
     * 参数3: newVersion 升级版本号
     *
     * */

    /**
     * 打开数据库
     * @return 打开状态  ture-成功
     */
    public boolean openDB() {
        mDataBaseHelper = new DataBaseHelper(mContext, DB_NAME, null,
                DB_VERSION);
        mSqLiteDatabase = mDataBaseHelper.getWritableDatabase();
        if (mSqLiteDatabase.isOpen()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 关闭数据库
     */
    public void closeDB() {
        if (mDataBaseHelper != null) {
            mDataBaseHelper.close();
        }
        if (mSqLiteDatabase != null) {
            mSqLiteDatabase.close();
        }
    }


    /**
     * 创建一个数据表
     * @param tb_name  表名
     * @param field 字段名与对应的数据类型
     */
    public void creatTB(String tb_name, HashMap<String, String> field) {
        StringBuilder sqlStringBuilder = new StringBuilder();
        sqlStringBuilder.append("create table if not exists " + tb_name + " (");
        Iterator iter = field.entrySet().iterator();
        //行id 必须要
        sqlStringBuilder.append(" " + DbConstant.BaseDB.COLUMN_SIGNIN_USER_ID + " " + "integer primary key" + ",");
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String fieldType = entry.getValue().toString();
            String fieldName = entry.getKey().toString();

            sqlStringBuilder.append(" " + fieldName + " " + fieldType + ",");
        }
        int start = sqlStringBuilder.length();
        String ss = sqlStringBuilder.substring(0, start - 1);
        ss += ")";

        mSqLiteDatabase.execSQL(ss);
        System.out.println("表创建成功");
    }

    /**
     * 删除数据库中一个表
     * @param tb_name 表名
     */
    public void deleteTB(String tb_name) {
        mSqLiteDatabase.execSQL("drop table " + tb_name);
    }


    /**
     * 删除数据库
     * @param db_name 数据库名（路径）
     */
    public void deleteDB(String db_dir , String db_name) {
        //删除数据库
        File dbFile = new File(DbPath.BASE_PATH+db_dir,db_name);
        if (dbFile.exists()) {
            dbFile.delete();
        }
        //删除数据库后缀的产物
        File dbHFile = new File(DbPath.BASE_PATH+db_dir,db_name+DbConstant.BaseDB.DB_HZ);
        if (dbHFile.exists()) {
            dbHFile.delete();
        }
    }

    /**
     * 更新表中的数据
     *
     * @param tb_name 要更新的表名
     * @param values  更新的字段及内容  字段名和对应的值
     * @param filter  过滤条件字段 （只有一个）,对应的值可以多个
     */
    public int updata(String tb_name, HashMap<String, String> values,
             HashMap<String,String> filter) {
        int result=-1;
        ContentValues cv=null;
        //更新数据
        if ((values!=null) && (mSqLiteDatabase!=null)){
            cv = new ContentValues();
            Iterator iter = values.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String field = entry.getKey().toString();
                String value = entry.getValue().toString();
                cv.put(field, value);
            }
            //更新条件
            if (filter !=null){
                Iterator<Map.Entry<String, String>> fiter_iter = filter.entrySet().iterator();
                while (fiter_iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) fiter_iter.next();
                    String fiterKey = entry.getKey().toString();
                    String fiterValues =entry.getValue().toString();
                    mSqLiteDatabase.update(tb_name, cv, fiterKey+"= ?",new String[]{fiterValues});
                }

               result=0;
            }else {
                //无更新条件，则默认将表中所有的该字段的内容全部更新
                mSqLiteDatabase.update(tb_name, cv, null,null);
                result=0;

            }

        }

        return result;

    }

    /**
     * 根据条件删除数据库中的数据
     *
     * @param tb_name 表名
     * @param filter  过滤条件
     */
    public int delete(String tb_name,HashMap<String,String> filter) {
        int result=-1;
        if (mSqLiteDatabase!=null ) {
            //删除条件
            if (filter !=null){
                Iterator<Map.Entry<String, String>> fiter_iter = filter.entrySet().iterator();
                while (fiter_iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) fiter_iter.next();
                    String fiterKey = entry.getKey().toString();
                    String fiterValues = entry.getValue().toString();

                    mSqLiteDatabase.delete(tb_name, fiterKey+" = ?", new String[]{fiterValues});
                }

            }else {
                mSqLiteDatabase.delete(tb_name, null, null);
            }
            result=0;

        }
        return result;
    }


    /**
     * 根据条件删除数据库中的数据
     *
     * @param tb_name 表名
     * @param filter  过滤条件
     */
    public int delete1(String tb_name,HashMap<String,String> filter) {
        int result=-1;
        if (mSqLiteDatabase!=null ) {
            //删除条件
            if (filter !=null){
                Iterator<Map.Entry<String, String>> fiter_iter = filter.entrySet().iterator();
                while (fiter_iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) fiter_iter.next();
                    String fiterKey = entry.getKey().toString();
                    String fiterValues = entry.getValue().toString();

                    mSqLiteDatabase.delete(tb_name, fiterKey+" < ?", new String[]{fiterValues});
                }

            }else {
                mSqLiteDatabase.delete(tb_name, null, null);
            }
            result=0;

        }
        return result;
    }


    /**
     * 插入新记录
     *
     * @param tb_name    表名
     * @param key_values 字段与值得映射HashMap
     */
    public void insert(String tb_name, HashMap<String, String> key_values) {
        if (key_values !=null){
                ContentValues cv = new ContentValues();
                Iterator iter = key_values.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String field = entry.getKey().toString();
                    Object value1 = entry.getValue();
                    String value="";
                    if (value1 !=null){
                         value = entry.getValue().toString();
                    }else {
                        value="";
                    }

                    cv.put(field, value);
                }

                mSqLiteDatabase.insert(tb_name, null, cv);
                System.out.println("insert SUCCESS");

        }else {
            System.out.println("insert FAILL");
        }

    }



    /**
     * 获取表的指定字段的全部记录
     *
     * @param tb_name 表名
     * @param columns   筛选的行数    如 筛选3行  new String[]{3+""}
     * @return 返回Cursor对象
     */
    public List<Cursor> query(String tb_name, String[] columns, HashMap<String,String> filter
    , String orderBy) {
        List<Cursor> list=new ArrayList<Cursor>();
        if (filter !=null){
            Iterator<Map.Entry<String, String>> fiter_iter = filter.entrySet().iterator();
            while (fiter_iter.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry<String, String>) fiter_iter.next();
                String fiterKey = entry.getKey().toString();
                String fiterValues =  entry.getValue().toString();
                Cursor cursor=mSqLiteDatabase.query(tb_name,columns, fiterKey+" = ?", new String[]{fiterValues},null,null,orderBy);
                list.add(cursor);
            }
        }else {
            Cursor cursor=mSqLiteDatabase.query(tb_name,columns, null, null,null,null,orderBy);
            list.add(cursor);
        }
        return list;
    }

    /**
     * 查询两个时间点的数据
     *
     * @param
     * **/
    public Cursor SelectDataTimeData(String startTime,String endTime ,String tableName) {
        Cursor cursor = null;
        if(tableName.equals("people")){
            cursor = mSqLiteDatabase.rawQuery("select * from  people where pcsj >= ? and pcsj<= ?",
                    new String[] {startTime,endTime});
            return cursor;
        }else{
            cursor = mSqLiteDatabase.rawQuery("select * from  car where pcsj>= ? and pcsj<= ?",
                    new String[] {startTime,endTime });
            return cursor;
        }
    }



//    select * from car where pcsj>='2019-03-20 00:00:00' and pcsj<='2019-03-20 24:00:00'
    /**
     * 查询当天的数据
     *
     * @param
     * **/
    public Cursor selectDayData(String tableName) {
        String time = TimeUitls.parseLongToString(TimeUitls.DATE_YEAR_MOTH_DAY);
        Cursor cursor = null;
        if(tableName.equals("people")){
            cursor = mSqLiteDatabase.rawQuery(
                    "select * from  people  where pcsj>=? and pcsj<=?",
                    new String[] { time+" 00:00:00",time+" 24:00:00"});
            return cursor;
        }else{
            cursor = mSqLiteDatabase.rawQuery(
                    "select * from  car  where pcsj>=? and pcsj<=?",
                    new String[] { time+" 00:00:00",time+" 24:00:00"});
            return cursor;
        }
    }


    /**
     * 查询当天总数数据总条数
     *
     * @param
     * **/
    public String selectSumData(String tableName) {
        String time = TimeUitls.parseLongToString(TimeUitls.DATE_YEAR_MOTH_DAY);
        Cursor cursor = null;
        String number ="";
        if(tableName.equals("people")){
            cursor = mSqLiteDatabase.rawQuery(
                    " select count(_id) from people  where pcsj>=? and pcsj<=?",new String[] { time+" 00:00:00",time+" 24:00:00"});
            //游标移到第一条记录准备获取数据
            cursor.moveToFirst();
            number = cursor.getString(0);
            return number;
        }else{
            cursor = mSqLiteDatabase.rawQuery(
                    "select  count(_id) from  car  where pcsj>=? and pcsj<=?",new String[] { time+" 00:00:00",time+" 24:00:00"});
            //游标移到第一条记录准备获取数据
            cursor.moveToFirst();
            number = cursor.getString(0);
            return number;
        }
    }

    /**
     * 查询总数数据总条数
     *
     * @param
     * **/
    public String selectAllData(String tableName) {
        Cursor cursor = null;
        String number ="";
        if(tableName.equals("people")){
            cursor = mSqLiteDatabase.rawQuery(
                    " select count(_id) from people",null);
            //游标移到第一条记录准备获取数据
            cursor.moveToFirst();
            number = cursor.getString(0);
            return number;
        }else{
            cursor = mSqLiteDatabase.rawQuery(
                    "select  count(_id) from  car",null);
            //游标移到第一条记录准备获取数据
            cursor.moveToFirst();
            number = cursor.getString(0);
            return number;
        }
    }
    /**
     * 创建一个存储数据库的一个路径
     * @param dbName  数据库名称
     */
    private static String createDdPath(String dbDir , String dbName ) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED))
        { // 检测sd是否可用
            Log.i("TestFile", "SD card is not avaiable/writeable right now.");
            return "";
        }
        @SuppressWarnings("static-access")
        String name = dbName;

        // 新建自己存放图片的目录
        File file = new File(DbPath.BASE_PATH+dbDir);
        if( !file.exists() )
            file.mkdirs();
        else if( !file.isDirectory() && file.canWrite() ){
            file.delete();
            file.mkdirs();
        }
        return file.getAbsolutePath() + File.separator + name;
    }


    private static class DataBaseHelper extends SQLiteOpenHelper {

        public DataBaseHelper(Context context, String name,
                              CursorFactory factory, int version) {
            super(context, name, factory, version);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

    }


}
