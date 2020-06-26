package com.xt.mobile.terminal.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

/**
 * @Author:pengyongshun
 * @Desc:
 * 内置SD卡路径：/storage/emulated/0/
 * 外置SD卡路径：/storage/sdcard1/
 * 内部存储路径：/data/data/包名/...
 * getFilesDir()   /data/data/com.unistrong.functiontest/files
 * getDir("data", this.MODE_PRIVATE)  /data/data/com.unistrong.functiontest/app_data
 * 在获取大图片时注意压缩
 * @Time:2017/3/16
 */
public class FileUtils {
    public static final int SIZETYPE_B = 1;//获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;//获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;//获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;//获取文件大小单位为GB的double值


    //内置sd卡路径
    public static final String N_SDCARD_PATH= Environment
            .getExternalStorageDirectory()
            .getAbsolutePath();
    //外置sd卡路径
    public static final String W_SDCARD_PATH="/storage/sdcard1";//根据手机的不同，这个路径也会不同,需要授权才可以
    /**
     * 内置SD卡 媒体默认保存路径
     * **/

    public static final String N_DEFULT_IMAGE=N_SDCARD_PATH+"/DCIM/Camera";
    public static final String N_DEFULT_RECORD=N_SDCARD_PATH+"/DCIM/AUDIO";
    public static final String N_DEFULT_VIDIO=N_SDCARD_PATH+"/DCIM/Video";



    ////////////////////存储在android系统默认的位置，内部存储/////////////////////
    /**
     * 往文件写内容
     * 一般存在data/data/..../files路径下
     * @param fileName
     * @param content
     * @param context
     * @return
     */
    public static boolean writeFileByInternal(String fileName, String content, Context context){
        try {
            FileOutputStream fos=context.openFileOutput(fileName,context.MODE_PRIVATE);
            fos.write(content.getBytes());
            closeStream(fos);
            return true;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从文件中读取内容
     * 一般存在data/data/..../files路径下
     * @param fileName
     * @param context
     * @return
     */
    public static String readFileByInternal(String fileName, Context context){
        String content="";
        try {
            FileInputStream fis=context.openFileInput(fileName);
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            byte[] buffer=new byte[1024];
            int len=0;
            while((len=fis.read(buffer))!=-1){
                baos.write(buffer,0, len);
            }
            content=baos.toString();
            closeStream(fis);
            closeStream(baos);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 获取内部存储data/data/包名下的所有文件
     * @param context
     * @return
     */
    public static String[] getAllFilesByInternal(Context context){
        String[] fileList = context.fileList();
        return fileList;
    }

    /**
     * 删除内部存储data/data/包名下的某个文件
     * @param fileName
     * @param context
     * @return
     */
    public static boolean deleFileByInternal(String fileName, Context context){
        //首先获取内部存储中的所有文件
        String[] allFilesByInternal = getAllFilesByInternal(context);
        if (allFilesByInternal!=null){
            for (int i = 0; i < allFilesByInternal.length; i++) {
                String file = allFilesByInternal[i];
                if (file.equals(fileName)){
                    //删除文件
                    boolean result = context.deleteFile(fileName);
                    return result;
                }else {
                    return false;
                }
            }
        }else {
            return false;
        }
        return false;
    }
    /**
     * 删除内部存储data/data/包名下的所有文件
     * @param context
     * @return
     */
    public static boolean deleFilesByInternal(Context context){
        //首先获取内部存储中的所有文件
        String[] allFilesByInternal = getAllFilesByInternal(context);
        boolean delete=false;
        if (allFilesByInternal!=null){
            for (int i = 0; i < allFilesByInternal.length; i++) {
                String file = allFilesByInternal[i];
                delete=context.deleteFile(file);
            }
            return delete;
        }else {
            return delete;
        }

    }
    ////////////////////////////怎么将文件放在外部存储内置SD卡/storage/emulated/0/...进行操作////////////////
    /**
     * 判断SDCard是否存在 [当没有外挂SD卡时，内置ROM也被识别为存在sd卡]
     *
     * @return
     */
    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }
    /**
     * 获取内置SD卡根目录路径相对路径
     *
     * @return
     */
    public static String getSdCardPath() {
        boolean exist = isSdCardExist();
        String sdpath = "";
        if (exist) {
            sdpath = N_SDCARD_PATH;
        } else {
            sdpath = "";
        }
        return sdpath;

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

    //////////////////////////////////////文件的大小//////////////////////
    /**
     * 获取文件指定文件的指定单位的大小
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    public static double getFileOrFilesSize(String filePath, int sizeType){
        File file=new File(filePath);
        long blockSize=0;
        try {
            if(file.isDirectory()){
                blockSize = getFileSizes(file);
            }else{
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("获取文件大小","获取失败!");
        }
        return formetFileSize(blockSize, sizeType);
    }
    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     * @param filePath 文件路径
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    public static String getAutoFileOrFilesSize(String filePath){
        File file=new File(filePath);
        long blockSize=0;
        try {
            if(file.isDirectory()){
                blockSize = getFileSizes(file);
            }else{
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("获取文件大小","获取失败!");
        }
        return formetFileSize(blockSize);
    }

    /**
     * 获取指定文件大小
     * @param file
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception
    {
        long size = 0;
        if (file.exists()){
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();

        }else{
            Log.e("获取文件大小","文件不存在!");
        }
        return size;
    }

    /**
     * 获取指定文件夹的大小
     * @param f
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++){
            if (flist[i].isDirectory()){
                size = size + getFileSizes(flist[i]);
            }
            else{
                size =size + getFileSize(flist[i]);
            }
        }
        return size;
    }
    /**
     * 转换文件大小
     * @param fileS
     * @return
     */
    private static String formetFileSize(long fileS)
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
     * 转换文件大小,指定转换的类型
     * @param fileS
     * @param sizeType
     * @return
     */
    private static double formetFileSize(long fileS,int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong= Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong= Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong= Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong= Double.valueOf(df.format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    ////////////////////////////////////////文件复制////////////////////////////
    /**
     *
     * @param fromFile   源文件路径
     * @param toFile   复制到那个文件下路径
     */
    public static void copyFile(File fromFile, File toFile)
    {

        if (!fromFile.exists())
        {
            return;
        }
        if (!fromFile.isFile())
        {
            return;
        }
        if (!fromFile.canRead())
        {
            return;
        }
        try
        {
            FileInputStream fosfrom = new FileInputStream(fromFile);
            FileOutputStream fosto = new FileOutputStream(toFile);

            byte[] bt = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0)
            {
                fosto.write(bt, 0, c);
            }
            // 关闭输入、输出流
            fosfrom.close();
            fosto.close();
            fromFile.delete();
        } catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @param fromFile 源文件路径
     * @param toFile 复制到那个文件下路径
     * @return
     */
    public static int copy(String fromFile, String toFile) {
        // 要复制的文件目录
        File[] currentFiles;
        File root = new File(fromFile);
        // 如同判断SD卡是否存在或者文件是否存在
        // 如果不存在则 return出去
        if (!root.exists())
        {
            return -1;
        }
        // 如果存在则获取当前目录下的全部文件 填充数组
        currentFiles = root.listFiles();

        // 目标目录
        File targetDir = new File(toFile);
        // 创建目录
        if( !targetDir.exists() )
            targetDir.mkdirs();
        else if( !targetDir.isDirectory() && targetDir.canWrite() ){
            targetDir.delete();
            targetDir.mkdirs();
        }
        // 遍历要复制该目录下的全部文件
        for (int i = 0; i < currentFiles.length; i++)
        {
            if (currentFiles[i].isDirectory())// 如果当前项为子目录 进行递归
            {
                copy(currentFiles[i].getPath() + "/",
                        toFile + currentFiles[i].getName() + "/");

            } else
            // 如果当前项为文件则进行文件拷贝
            {
                copySdcardFile(currentFiles[i].getPath(), toFile
                        + currentFiles[i].getName());
            }
        }
        return 0;
    }

    // 文件拷贝
    // 要复制的目录下的所有非子目录(文件夹)文件拷贝
    public static int copySdcardFile(String fromFile, String toFile) {

        try
        {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0)
            {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return 0;

        } catch (Exception ex)
        {
            return -1;
        }
    }

    /**
     * 复制单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    /**
     * 获取文件夹下的所有文件
     * @param dirPath
     * @return
     */
    public static String[] getFileNameInDir(String dirPath){
        File a=new File(dirPath);
        String[] file=a.list();
        return file;
    }

    /**
     * 复制整个文件夹内容
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a=new File(oldPath);
            String[] file=a.list();
            File temp=null;
            for (int i = 0; i < file.length; i++) {
                if(oldPath.endsWith(File.separator)){
                    temp=new File(oldPath+file[i]);
                }
                else{
                    temp=new File(oldPath+ File.separator+file[i]);
                }

                if(temp.isFile()){
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ( (len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if(temp.isDirectory()){//如果是子文件夹
                    copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);
                }
            }
        }
        catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();

        }

    }

    /**
     * 获取某个目录下的所有文件
     * @param dirPath
     * @return
     */
public static String[] getFilesInDir(String dirPath){
    File file=new File(dirPath);
    File[] files = file.listFiles();
    if (files!=null&&files.length>0){
        int length = files.length;
        String[] result=new String[length];
        for (int i = 0; i < length; i++) {
            File file1 = files[i];
            result[i]=file1.getName();
        }
        return result;
    }else {
        return null;
    }

}

    /**
     * 获取文件流里面的内容
     * @param fis
     * @return
     * @throws IOException
     */
public static String getValue(FileInputStream fis) throws IOException {
        //字节的输出流对象
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte [] buffer =  new byte[1024];
        int length=1;
        while ((length = fis.read(buffer)) != -1) {
            stream.write(buffer, 0, length);
        }
        stream.flush();
        stream.close();
        String value= stream.toString();
        return value;
    }

    /**
     * 读取文本数据
     * 
     * @param context
     *            程序上下文
     * @param fileName
     *            文件名
     * @return String, 读取到的文本内容，失败返回null
     */
    public static String readAssets(Context context, String fileName)
    {
        InputStream is = null;
        String content = null;
        try
        {
            is = context.getResources().getAssets().open(fileName);
            if (is != null)
            {

                byte[] buffer = new byte[1024];
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                while (true)
                {
                    int readLength = is.read(buffer);
                    if (readLength == -1) break;
                    arrayOutputStream.write(buffer, 0, readLength);
                }
                is.close();
                arrayOutputStream.close();
                content = new String(arrayOutputStream.toByteArray());

            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            content = null;
        }
        finally
        {
            try
            {
                if (is != null) is.close();
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
        }
        return content;
    }
    //////////////////////////////////sd卡内存大小//////////////////
    /**
     * 获取外部存储的总空间大小（G）
     * @return
     */
    public static String getSDToalSize(){
        long totalSize = 0;
        String sdCardPath = getSdCardPath();
        if (!sdCardPath.equals("")){
            StatFs fs = new StatFs(sdCardPath);
            long count = fs.getBlockCount();
            long size = fs.getBlockSize();
            totalSize = count * size ;
        }

        return formetFileSize(totalSize);
    }

    /**
     * 获取SD卡可用空间大小（G）
     * @return
     */
    public static String getSDAvailableSize(){
        long availableSize = 0;
        String sdCardPath = getSdCardPath();
        if (!sdCardPath.equals("")){
            StatFs fs = new StatFs(sdCardPath);
            long count = fs.getFreeBlocks();
            long size = fs.getBlockSize();
            availableSize = count * size;
        }

        return formetFileSize(availableSize);
    }


    ///////////////////////////////文件是否存在//////////////////////////

    /**
     * 判断一个文件是否存在
     * @param filePath  例如："/storage/sdcard0/Manual/test.pdf"
     * @return
     */

    public static boolean fileExists(String filePath, Context c){
        try{
            File file=new File(filePath);
            if( !file.exists() ) {
                return false;
            }else if( file.isDirectory() ){
                deleteFileFromSDcard(file,c);
                return false;

            }

        }catch (Exception e) {
            // TODO: handle exception
            //发生异常
            return false;
        }
        return true;
    }
    /**
     * 判断一个文件是否存在
     * @param fileDir  例如："/storage/sdcard0/Manual/test.pdf"
     * @return
     */

    public static boolean fileExists(String fileDir, String fileName, Context c){
        try{
            File file=new File(fileDir,fileName);
            if( !file.exists() ) {
                return false;
            }else if( file.isDirectory() ){
                deleteFileFromSDcard(file,c);
                return false;

            }

        }catch (Exception e) {
            // TODO: handle exception
            //发生异常
            return false;
        }
        return true;
    }

    //////////////////////////////////文件储存、删除、读取、创建路径/////////

    /**
     * 使用FileOutputStream写入文件,写到sd卡中
     * @param filePath
     * @param bytes
     * @return
     */
    public static boolean writeFileToSDcard(String dir, String fileName, String filePath , byte[] bytes , Context c){
        if (bytes!=null&&bytes.length>0){
            boolean exist = isSdCardExist();
            if (exist){
                try {
                    File file = new File(filePath);
                    if (file.isDirectory()){
                        File f=new File(dir);
                        deleteFileFromSDcard(f,c);
                        String path = createFileFromSDcard(dir,fileName);
                        file=new File(path);
                    }
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bytes,0,bytes.length);
                    fos.flush();
                    closeStream(fos);
                    return true;
                } catch (Exception e) {
                    Log.i("writeFileToSDcard", "e:--> 此文件夹是目录");
                    e.printStackTrace();
                }
            }else {
                Log.i("writeFileToSDcard", "e:--> SDcard不存在");
                return false;
            }
        }else {
            Log.i("writeFileToSDcard", "e:--> 没有下载到数据，数据为空");
            return false;
        }

        return false;

    }

    /**
     * 创建文件目录
     */
    public static String createFileDir(String fileDir) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED))
        { // 检测sd是否可用
            Log.i("TestFile", "SD card is not avaiable/writeable right now.");
            return "";
        }
        // 新建自己存放图片的目录

        File file = new File(fileDir);
        if( !file.exists() )
            file.mkdirs();
        else if( !file.isDirectory() && file.canWrite() ){
            file.delete();
            file.mkdirs();
        }
        return file.getAbsolutePath() ;
    }

    /**
     * 创建文件路径
     */
    public static String createFileFromSDcard(String fileDir, String fileName ) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED))
        { // 检测sd是否可用
            Log.i("TestFile", "SD card is not avaiable/writeable right now.");
            return "";
        }
        String name=fileName;
        // 新建自己存放图片的目录

        File file = new File(fileDir);
        if( !file.exists() )
            file.mkdirs();
        else if( !file.isDirectory() && file.canWrite() ){
            file.delete();
            file.mkdirs();
        }
        return file.getAbsolutePath()+ File.separator+name ;
    }
    /**
     * 将SD卡文件删除
     * @param file
     * @param context
     * @return
     */
    public static boolean  deleteFileFromSDcard(File file, Context context) {
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
                        deleteFileFromSDcard(files[i],context); // 把每个文件 用这个方法进行迭代
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
     * 使用fileInputstream来读取sd卡中的文件
     * @param filePath
     * @param context
     * @return
     */
    public static byte[] readFileBySDcard(String filePath, Context context){
        byte[] content ;
        boolean exist = isSdCardExist();
        if (exist){
            try {
                File file = new File(filePath);
                FileInputStream is = new FileInputStream(file);
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                byte[] buffer=new byte[1024];
                int len=0;
                while((len=is.read(buffer))!=-1){
                    baos.write(buffer,0, len);
                }
                content=baos.toByteArray();
                closeStream(is);
                closeStream(baos);
                return content;
            } catch (Exception e) {
                Toast.makeText(context, "读取失败", Toast.LENGTH_SHORT).show();
                e.printStackTrace();

            }
        }else {
            Toast.makeText(context, "SDcard不存在", Toast.LENGTH_SHORT).show();
        }
        return null;

    }


}
