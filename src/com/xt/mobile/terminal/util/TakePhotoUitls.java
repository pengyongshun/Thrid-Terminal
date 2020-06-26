package com.xt.mobile.terminal.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;


/**
 * Created by 彭永顺 on 2019/3/15.
 */
public class TakePhotoUitls {

    //内置sd卡路径
    public static final String N_SDCARD_PATH=Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String N_DEFULT_IMAGE=N_SDCARD_PATH+"/unistrong/peocarquery/peoplePic";
    public static final String IMAGE=MediaStore.ACTION_IMAGE_CAPTURE;

    private  String curFilePath="";

    private static TakePhotoUitls takePhotoUitls;
    private static Activity activity;

    public static TakePhotoUitls newInatance(Activity activity){
        if (takePhotoUitls ==null){
            takePhotoUitls=new TakePhotoUitls(activity);
        }
        return takePhotoUitls;
    }

    public TakePhotoUitls(Activity activity) {
        this.activity = activity;
    }

    /**
     * 调用系统摄像头拍照
     * @return
     */
    public  Intent takePhoto() {
        //创建文件路径
        String filePath =createBitmapPath();
        //调用系统的照相机
        Intent intent = new Intent(IMAGE);
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        File file = new File(filePath);
        if (file == null ) {
            return null;
        }
        Uri uri = null;
        try {
            uri = Uri.fromFile(file);
        } catch (Exception e) {
            return null;
        }
        if (uri == null) {
            return null;
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
       // activity.startActivityForResult(intent,Constans.TAKE_PHOTO);
        curFilePath=filePath;
        return intent;
    }

    /**
     * 保存图片  格式：jpg
     * 图片保存在sd卡的"/DCIM/Camera/"
     */
    private  String createBitmapPath() {
        String sdStatus = Environment.getExternalStorageState();
        // 获得sd卡目录
        String baseDir = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED))
        { // 检测sd是否可用
            Log.i("TestFile", "SD card is not avaiable/writeable right now.");
            return "";
        }
        @SuppressWarnings("static-access")
        String name = new DateFormat().format("yyyyMMdd_hhmmss",
                Calendar.getInstance(Locale.CHINA)) + ".jpg";

        // 新建自己存放图片的目录
        File file = new File(N_DEFULT_IMAGE);
        if( !file.exists() )
            file.mkdirs();
        else if( !file.isDirectory() && file.canWrite() ){
            file.delete();
            file.mkdirs();
        }
        return file.getAbsolutePath() + File.separator + name;
    }

    /**
     * 获取文件路径
     * @return
     */
    public  String getFilePath(){
        return curFilePath;
    }


    /**
     * 删除目录及目录下的文件
     *
     * @param dir
     *            要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            System.out.println("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            System.out.println("删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            System.out.println("删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }

    }




    /**
     * 删除单个文件
     *
     * @param fileName
     *            要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }


}
