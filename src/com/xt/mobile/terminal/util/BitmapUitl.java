package com.xt.mobile.terminal.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * @Author:pengyongshun
 * @Desc:
 * @Time:2017/8/28
 */
public class BitmapUitl {
    //////////////////////////////////////图片压缩/////////////////////
    /**
     * 计算图片的缩放值
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;//代表不缩放

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
    /**
     * 质量压缩方法
     *
     * @param bitmap
     * @return
     */
    public static Bitmap compressQuality(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        while (baos.toByteArray().length /1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            options -= 10;// 每次都减少10
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中

        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap newBitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        if(newBitmap!=null){
            return newBitmap;
        }else{
            return bitmap;
        }

    }

    ///////////////////////////////////////既进行图片大小压缩有进行质量压缩//////////////////

    /**
     * 图片按比例大小压缩方法
     *
     * @param srcPath （根据路径获取图片并压缩）
     * @return
     */
    public static Bitmap compressImage(String srcPath, int width, int height) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
        newOpts.inSampleSize = calculateInSampleSize(newOpts, width, height);// 设置缩放比例
        newOpts.inJustDecodeBounds = false;
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressQuality(bitmap);// 压缩好比例大小后再进行质量压缩
    }
    /**
     * 图片按比例大小压缩方法
     *
     * @param image （根据Bitmap图片压缩）
     * @return
     */
    public static Bitmap compressImage(Bitmap image, int width, int height) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//100代表不压缩

        // 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
        if (baos.toByteArray().length / 1024 > 1024) {
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inSampleSize = calculateInSampleSize(newOpts, width, height);// 设置缩放比例
        newOpts.inJustDecodeBounds = false;

        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressQuality(bitmap);// 压缩好比例大小后再进行质量压缩
    }
    ///////////////////////////////////////只进行大小压缩//////////////////////////////
    /**
     * 图片按比例大小压缩方法
     *
     * @param srcPath （根据路径获取图片并压缩）
     * @return
     */
    public static Bitmap compressScale(String srcPath, int width, int height) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
        newOpts.inSampleSize = calculateInSampleSize(newOpts, width, height);// 设置缩放比例
        newOpts.inJustDecodeBounds = false;
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return bitmap;// 压缩好比例大小
    }
    /**
     * 图片按比例大小压缩方法
     *
     * @param image （根据Bitmap图片压缩）
     * @return
     */
    public static Bitmap compressScale(Bitmap image, int width, int height) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//100代表不压缩

        // 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
        if (baos.toByteArray().length / 1024 > 1024) {
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inSampleSize = calculateInSampleSize(newOpts, width, height);// 设置缩放比例
        newOpts.inJustDecodeBounds = false;

        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);

        return bitmap;// 压缩好比例大小

    }
    ///////////////////////相片角度处理//////////////////////////////////////
    /**
     *旋转照片
     * **/
    public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degress);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }
    /**
     * 判断照片角度
     * */

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 压缩图片，处理某些手机拍照角度旋转的问题
     *压缩处理后的图片存放的地址： "/DCIM/Camera"
     * @param filePath  原图片的地址
     * @param targetPath  处理后的图片存放地址
     * @param quality 压缩质量0到100，100代表不压缩
     * @param width
     * @param weight
     * @return
     */
    public static String dealImageDegree(String filePath, String targetPath, int quality, int width, int weight)  {
        Bitmap bm = compressImage(filePath,width,weight);//获取一定尺寸的图片
        int degree = readPictureDegree(filePath);//获取相片拍摄角度
        if(degree!=0){//旋转照片角度，防止头像横着显示
            bm=rotateBitmap(bm,degree);
        }
        File outputFile=new File(targetPath);
        try {
            if (!outputFile.exists()) {
                outputFile.getParentFile().mkdirs();
                //outputFile.createNewFile();
            }else{
                outputFile.delete();
            }
            FileOutputStream out = new FileOutputStream(outputFile);
            bm.compress(Bitmap.CompressFormat.JPEG, quality, out);
        }catch (Exception e){}
        return outputFile.getPath();
    }


    /**
     * 计算缩放比
     *
     * @param bitWidth
     *            图片宽度
     * @param bitHeight
     *            图片高度
     * @return 比例
     */
    public static int getRatioSize(int bitWidth, int bitHeight) {
        // 图片最大分辨率
        int imageHeight = 1280;
        int imageWidth = 960;
        // 缩放比
        int ratio = 1;
        // 缩放比,由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        if (bitWidth > bitHeight && bitWidth > imageWidth) {
            // 如果图片宽度比高度大,以宽度为基准
            ratio = bitWidth / imageWidth;
        } else if (bitWidth < bitHeight && bitHeight > imageHeight) {
            // 如果图片高度比宽度大，以高度为基准
            ratio = bitHeight / imageHeight;
        }
        // 最小比率为1
        if (ratio <= 0)
            ratio = 1;
        return ratio;
    }
    ///////////////////////////////////类型转换//////////////////////////////
    /**
     * 把bitmap转换成String
     * @param filePath
     * @return
     */
    public static String bitmapToString(String filePath, int width, int weight) {

        Bitmap bm = compressImage(filePath,width,weight);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
    /**
     * 将字节数组转换成bitmap
     * @param temp
     * @return
     */
    public static Bitmap byteToBitmap(byte[] temp){
        if(temp != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
            return bitmap;
        }
        return null;
    }
    /**
     * 将bitmap转换成字节数组
     * @param photo
     * @return
     */
    public static byte[] bitmapToByte(Bitmap photo){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if(photo != null){
            photo.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
        return null;
    }

    /**
     * string转成bitmap
     *
     * @param imagePath
     */
    public static Bitmap stringToBitmap(String imagePath) {
        //获取图片角度
        int degree = readPictureDegree(imagePath);
        Bitmap bitmap = null;
        try {
            //把图片转化为字节流
            FileInputStream fis = new FileInputStream(imagePath);
            //把流转化图片
             bitmap = BitmapFactory.decodeStream(fis);
            //修复图片的角度
            bitmap=rotateBitmap(bitmap,degree);

        } catch (Exception e) {
            e.getStackTrace();
        }
        return bitmap;
    }

    /**
     * 64位字符串转换为Bitmap
     * @param base64
     * @return
     */
    public static Bitmap base64ToBitmap(String base64) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(base64, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;

//        // Get bitmap through image path
//        BitmapFactory.Options newOpts = new BitmapFactory.Options();
//        newOpts.inJustDecodeBounds = false;
//        newOpts.inPurgeable = true;
//        newOpts.inInputShareable = true;
//        // Do not compress
//        newOpts.inSampleSize = 1;
//        newOpts.inPreferredConfig = Config.RGB_565;
//        return BitmapFactory.decodeFile(imgPath, newOpts);
    }

    /**
     * Bitmap转换为64位字符串
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } else {
            return null;
        }
    }

    ////////////////////////////////////////图片读取与写入////////////////////////////

    /**
     * 通过文件路径读获取Bitmap防止OOM以及解决图片旋转问题
     * @param imagePath
     * @return
     */
    public static Bitmap readImageFromSD(String imagePath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;// 只读边,不读内容
        BitmapFactory.decodeFile(imagePath, newOpts);
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 获取尺寸压缩倍数
        newOpts.inSampleSize = getRatioSize(w,h);
        newOpts.inJustDecodeBounds = false;// 读取所有内容
        newOpts.inDither = false;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;
        newOpts.inTempStorage = new byte[32 * 1024];
        Bitmap bitmap = null;
        File file = new File(imagePath);
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (fs != null) {
                bitmap = BitmapFactory.decodeFileDescriptor(fs.getFD(), null,
                        newOpts);
                // 旋转图片
                int photoDegree = readPictureDegree(imagePath);
                if (photoDegree != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(photoDegree);
                    // 创建新的图片
                    bitmap = Bitmap
                            .createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                                    bitmap.getHeight(), matrix, true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }
    /**
     *  创建图片路径
     * @param imageName  图片名称
     */
    public static String creatImagePathToSD(String dir , String imageName ) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            Log.i("TestFile", "SD card is not avaiable/writeable right now.");
            return"";
        }
        @SuppressWarnings("static-access")
        String name = imageName
                + new DateFormat().format("yyyyMMdd_hhmmss",
                Calendar.getInstance(Locale.CHINA)) + ".jpg";
        // 新建自己存放图片的目录
        File file = new File(dir);
        if( !file.exists() )
            file.mkdirs();
        else if( !file.isDirectory() && file.canWrite() ){
            file.delete();
            file.mkdirs();
        }
        File file1=new File(file,name);
        String filePath = file1.getAbsolutePath();
        return filePath;
    }
    /**
     * 将SD卡文件删除
     * @param file
     * @param context
     * @return
     */
    public static boolean  deleteImageFromSD(File file, Context context) {
        String sdState = Environment.getExternalStorageState();
        if (sdState.equals(Environment.MEDIA_MOUNTED)) {
            if (file.exists()) {
                //如果它是一个文件
                if (file.isFile()) {
                    file.delete();
                    return true;
                }
                // 如果它是一个目录
                else if (file.isDirectory()) {
                    // 声明目录下所有的文件 files[];
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                        deleteImageFromSD(files[i],context); // 把每个文件 用这个方法进行迭代
                    }
                    return true;
                }
            }else {
                Toast.makeText(context, "没有文件可以删除", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return false;
    }
    /**
     * 使用FileOutputStream写入文件,写到sd卡中
     * @param filePath
     * @param bytes
     * @return
     */
    public static boolean writeImageToSDcard(String filePath , String fileDir, byte[] bytes){
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)){
            try {
                File file = new File(filePath);
                if (file.isDirectory()){
                    file.delete();
                    Log.i("writeFileToSDcard", "e:--> 此文件夹是目录");
                    return false;
                }
                //判断目录是否存在
                File dir=new File(fileDir);
                if (!dir.exists()){
                    Log.i("writeFileToSDcard", "e:--> 此文件路径不存在");
                    return false;
                }
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bytes,0,bytes.length);
                fos.flush();
                closeStream(fos);
                return true;
            } catch (Exception e) {
                Log.i("writeFileToSDcard", "e:--> 此文件夹是目录");
                e.printStackTrace();
                return false;
            }
        }else {
            Log.i("writeFileToSDcard", "e:--> SDcard不存在");
            return false;
        }
    }

    /**
     * 转换文件大小
     * @param fileS
     * @return
     */
    private static String FormetFileSize(long fileS)
    {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize="0B";
        if(fileS==0){
            return wrongSize;
        }
        if (fileS < 1024){
            fileSizeString = df.format((double) fileS) + "B";
        }
        else if (fileS < 1048576){
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        }
        else if (fileS < 1073741824){
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        }
        else{
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }
    /**
     * 得到bitmap的大小
     */
    public static String getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            int byteCount = bitmap.getAllocationByteCount();
            String fileSize = FormetFileSize(byteCount);
            return fileSize;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            int byteCount = bitmap.getByteCount();
            String fileSize = FormetFileSize(byteCount);
            return fileSize;
        }
        // 在低版本中用一行的字节x高度
        int byteCount = bitmap.getRowBytes() * bitmap.getHeight();
        String fileSize = FormetFileSize(byteCount);
        return fileSize;
    }
    /**
     * 关闭流
     * @param stream
     */
    public static void closeStream(Closeable stream){
        if(stream !=null){
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    ///////////////////////////////////压缩读取图片///////////////////
    /**
     * 图片按比例大小压缩方法
     *
     * @param srcPath （根据路径获取图片并压缩）
     * @return
     */
    public static Bitmap getBitmap(String srcPath) {

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;

        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // float hh = 1920f;
        // float ww = 1080f;
        float hh = 640f;
        float ww = 480f;
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        int orientation = readPictureDegree(srcPath);
        if (Math.abs(orientation) > 0) {
            bitmap = rotateBitmap(bitmap,orientation);
        }

        if (bitmap == null) {
            return null;
        }

        return bitmap;
    }

    ///////////////////////////////////给图片添加水印///////////////////////////
    /**
     * 给图片添加文字到左上角
     *
     * @param bitmap
     * @param text
     * @return
     */
    public static Bitmap drawTextToLeftTop(Bitmap bitmap, String text, int size,
                                           int color, int paddingLeft, int paddingTop) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(size);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap( bitmap, text, paint, bounds, paddingLeft,
                paddingTop + bounds.height());
    }

    /**
     * 绘制文字到右下角
     *
     * @param bitmap
     * @param text
     * @param size
     * @param color
     * @param paddingRight
     * @param paddingBottom
     * @return
     */
    public static Bitmap drawTextToRightBottom(Bitmap bitmap, String text, int size,
                                               int color, int paddingRight, int paddingBottom) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(size);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap( bitmap, text, paint, bounds,
                bitmap.getWidth() - bounds.width() - paddingRight, bitmap.getHeight() - paddingBottom);
    }

    /**
     * 绘制文字到右上方
     *
     * @param bitmap
     * @param text
     * @param size
     * @param color
     * @param paddingRight
     * @param paddingTop
     * @return
     */
    public static Bitmap drawTextToRightTop(Bitmap bitmap, String text, int size,
                                            int color, int paddingRight, int paddingTop) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(size);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap( bitmap, text, paint, bounds,
                bitmap.getWidth() - bounds.width() - paddingRight,
                paddingTop + bounds.height());
    }

    /**
     * 绘制文字到左下方
     *
     * @param bitmap
     * @param text
     * @param size
     * @param color
     * @param paddingLeft
     * @param paddingBottom
     * @return
     */
    public static Bitmap drawTextToLeftBottom(Bitmap bitmap, String text, int size,
                                              int color, int paddingLeft, int paddingBottom) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(size);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap( bitmap, text, paint, bounds, paddingLeft,
                bitmap.getHeight() - paddingBottom);
    }

    /**
     * 绘制文字到中间
     *
     * @param bitmap
     * @param text
     * @param size
     * @param color
     * @return
     */
    public static Bitmap drawTextToCenter(Bitmap bitmap, String text, int size,
                                          int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(size);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(bitmap, text, paint, bounds,
                (bitmap.getWidth() - bounds.width()) / 2, (bitmap.getHeight() + bounds.height()) / 2);
    }

    /**
     * 图片上绘制文字
     *
     * @param bitmap
     * @param text
     * @param paint
     * @param bounds
     * @param paddingLeft
     * @param paddingTop
     * @return
     */
    private static Bitmap drawTextToBitmap(Bitmap bitmap, String text, Paint paint,
                                           Rect bounds, int paddingLeft, int paddingTop) {
        Bitmap.Config bitmapConfig = bitmap.getConfig();

        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤一些
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawText(text, paddingLeft, paddingTop, paint);
        return bitmap;
    }




}
