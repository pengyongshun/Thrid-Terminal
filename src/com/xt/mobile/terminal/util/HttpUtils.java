package com.xt.mobile.terminal.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @Author:pengyongshun
 * @Desc:
 * @Time:2017/3/6
 */
public class HttpUtils
{

    /**
     * 判断网络是否链接
     */
    public static boolean isNetWorkConn(Context context)
    {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null)
        {
            return info.isConnected();
        } else
        {
            return false;
        }
    }


    /**
     * 获取网路数据
     *
     * @throws IOException get请求
     */

    public static byte[] getResultByGet(String urlString) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] result = null;
        InputStream is = null;

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(5 * 1000);
        connection.setReadTimeout(5 * 1000);
        int responseCode = connection.getResponseCode();
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            is = connection.getInputStream();
            byte[] buffer = new byte[1024];
            int length = 0;

            while ((length = is.read(buffer)) != -1)
            {
                baos.write(buffer, 0, length);
                baos.flush();
            }

            result = baos.toByteArray();
        }

        closeStream(is);
        closeStream(baos);
        return result;

    }

    /**
     * 获取网路数据(带进度条)
     *
     * @throws IOException
     */

    public static byte[] getResultByGetWithProgess(CallProgress callProgress, String urlString) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] result = null;
        InputStream is = null;

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(5 * 1000);
        connection.setReadTimeout(5 * 1000);

        int responseCode = connection.getResponseCode();
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
        {
            long total_length = connection.getContentLength();
            long cur_lenght = 0;
            Log.i("android", "getResultByGetWithProgess: total_length=" + total_length);
            is = connection.getInputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            int progress = 0;
            while ((length = is.read(buffer)) != -1)
            {
                cur_lenght += length;
                progress = (int) ((cur_lenght * 100) / total_length);
                callProgress.callPg(progress);
                //更新进度条
                baos.write(buffer, 0, length);
                baos.flush();
            }
            result = baos.toByteArray();
        } else
        {
            Log.i("unistrong", "getResultByGetWithProgess: 网络异常:404");
        }

        closeStream(is);
        closeStream(baos);
        return result;

    }

    /**
     * 获取网路数据
     *
     * @throws IOException post请求
     */

    public static byte[] getResultByPost(String urlString) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] result = null;
        InputStream is = null;

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(5 * 1000);
        connection.setReadTimeout(5 * 1000);
        connection.setDoOutput(true);
        connection.setDoInput(true); //允许输入流，即允许下载

        connection.setDoOutput(true); //允许输出流，即允许上传

        connection.setUseCaches(false); //不使用缓冲

        connection.setRequestMethod("POST");
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
        {
            is = connection.getInputStream();
            byte[] buffer = new byte[1024];
            int length = 0;

            while ((length = is.read(buffer)) != -1)
            {
                baos.write(buffer, 0, length);
                baos.flush();
            }

            result = baos.toByteArray();
        }

        closeStream(is);
        closeStream(baos);
        return result;

    }

    /***
     * post请求  带参数的
     * @param urlPath  网址（接口）
     * @param key 参数
     * @param value
     * @return**/
    public static byte[] getResultByPostWithPar(String urlPath, String key, String value)
    {
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try
        {
            URL url = new URL(urlPath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(3000);
            urlConnection.setReadTimeout(3000);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            //写入参数
            urlConnection.getOutputStream().write((key + "=" + value).getBytes());
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                is = urlConnection.getInputStream();
                baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1)
                {
                    baos.write(buffer, 0, len);
                }
                baos.flush();
                byte[] byteArray = baos.toByteArray();
                return byteArray;
            }

        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            closeStream(is);
            closeStream(baos);
        }
        return null;
    }

    /**
     * 获取bitmap
     *
     * @throws IOException get请求
     */

    public static Bitmap getBitmapsByGet(String urlString) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] result = null;
        InputStream is = null;

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(5 * 1000);
        connection.setReadTimeout(5 * 1000);

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
        {
            is = connection.getInputStream();
            byte[] buffer = new byte[1024];
            int length = 0;

            while ((length = is.read(buffer)) != -1)
            {
                baos.write(buffer, 0, length);
                baos.flush();
            }

            result = baos.toByteArray();

        }
        closeStream(is);
        closeStream(baos);
        if (result.length>0 && result!=null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
            return bitmap;
        }
        return null;

    }

    /**
     * 关闭流
     *
     * @param stream
     */
    public static void closeStream(Closeable stream)
    {
        if (stream != null)
        {
            try
            {
                stream.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 返回进度
     */
    public interface CallProgress
    {
        void callPg(int p);
    }


}
