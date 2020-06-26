package com.xt.mobile.terminal.network.sysim;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.xt.mobile.terminal.contants.ConstantsValues;
import com.xt.mobile.terminal.log.PLog;

import android.os.Build;
import android.util.Log;

public class HttpMethod {

	private static final int CONNECTION_TIMEOUT = 10000;
	// XT的类型，不同公司或许会不一样
	private static final String ContentHeader = "application/x-www-form-urlencoded; charset=UTF-8";

	public static String doHttpGet(String serverURL) throws Exception {
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParameters, CONNECTION_TIMEOUT);
		HttpClient hc = new DefaultHttpClient();
		HttpGet get = new HttpGet(serverURL);
		get.addHeader("Content-Type", ContentHeader);
		get.setParams(httpParameters);
		HttpResponse response = null;
		try {
			response = hc.execute(get);
		} catch (Exception e) {
			throw new Exception("Unable to access " + e.getLocalizedMessage());
		}
		int sCode = response.getStatusLine().getStatusCode();
		HttpEntity entity = response.getEntity();
		if (sCode == HttpStatus.SC_OK && entity != null) {
			return EntityUtils.toString(entity);
		} else {
			throw new Exception("StatusCode is : " + sCode + " , entity : " + entity);
		}
	}

	public static String doHttpsGet(String serverURL) throws Exception {
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParameters, CONNECTION_TIMEOUT);
		HttpClient hc = initHttpClient(httpParameters);
		HttpGet get = new HttpGet(serverURL);
		get.addHeader("Content-Type", ContentHeader);
		get.setParams(httpParameters);
		HttpResponse response = null;
		try {
			response = hc.execute(get);
		} catch (Exception e) {
			PLog.d("doHttpsGet","--------HttpsGet------>url:"+serverURL);
			PLog.d("doHttpsGet","--------HttpsGet------>error:"+e.getLocalizedMessage());
			throw new Exception("Unable to access " + e.getLocalizedMessage());
		}
		int sCode = response.getStatusLine().getStatusCode();
		HttpEntity entity = response.getEntity();
		if (sCode == HttpStatus.SC_OK && entity != null) {
			return EntityUtils.toString(entity);
		} else {
			throw new Exception("StatusCode is : " + sCode + " , entity : " + entity);
		}
	}

	public static String doHttpPost(String serverURL, String xmlString) throws Exception {
		Log.d("doHttpPost", "serverURL=" + serverURL);
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParameters, CONNECTION_TIMEOUT);
		HttpProtocolParams.setVersion(httpParameters, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(httpParameters, HTTP.UTF_8);
		HttpClient hc = new DefaultHttpClient();
		HttpPost post = new HttpPost(serverURL);
		post.addHeader("Content-Type", ContentHeader);
		post.setEntity(new StringEntity(xmlString, "UTF-8"));
		post.setParams(httpParameters);
		HttpResponse response = null;
		try {
			response = hc.execute(post);
		} catch (Exception e) {
			PLog.d("doHttpsGet","--------HttpsGet------>url:"+serverURL);
			PLog.d("doHttpsGet","--------HttpsGet------>error:"+e.getLocalizedMessage());
			throw new Exception("Unable to access " + e.getLocalizedMessage());
		}
		int sCode = response.getStatusLine().getStatusCode();
		HttpEntity entity = response.getEntity();
		if (sCode == HttpStatus.SC_OK && entity != null) {
			return EntityUtils.toString(entity);
		} else {
			throw new Exception("StatusCode is : " + sCode + " , entity : " + entity);
		}
	}

	public static String doHttpsPost(String serverURL, String xmlString) throws Exception {
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParameters, CONNECTION_TIMEOUT);
		HttpClient hc = initHttpClient(httpParameters);
		HttpPost post = new HttpPost(serverURL);
		post.addHeader("Content-Type", ContentHeader);
		post.setEntity(new StringEntity(xmlString, "UTF-8"));
		post.setParams(httpParameters);
		HttpResponse response = null;
		try {
			response = hc.execute(post);
		} catch (Exception e) {
			PLog.d("doHttpsGet","--------HttpsGet------>url:"+serverURL);
			PLog.d("doHttpsGet","--------HttpsGet------>error:"+e.getLocalizedMessage());
			throw new Exception("Unable to access " + e.getLocalizedMessage());
		}
		int sCode = response.getStatusLine().getStatusCode();
		HttpEntity entity = response.getEntity();
		if (sCode == HttpStatus.SC_OK && entity != null) {
			return EntityUtils.toString(entity);
		} else {
			throw new Exception("StatusCode is : " + sCode + " , entity : " + entity);
		}
	}

	public static HttpClient initHttpClient(HttpParams params) {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new SSLSocketFactoryImp(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, ConstantsValues.v_CORE_PORT));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient(params);
		}
	}

	public static class SSLSocketFactoryImp extends SSLSocketFactory {
		final SSLContext sslContext = SSLContext.getInstance("TLS");
		private static final String[] TLS_V12_ONLY = {"TLSv1.2"};

		public SSLSocketFactoryImp(KeyStore truststore) throws NoSuchAlgorithmException,
				KeyManagementException, KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
						String authType) throws java.security.cert.CertificateException {
				}

				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
						String authType) throws java.security.cert.CertificateException {
				}
			};
			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		private Socket enableTls12(Socket socket) {
	        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 20) {
	            if (socket instanceof SSLSocket) {
	                ((SSLSocket) socket).setEnabledProtocols(TLS_V12_ONLY);
	            }
	        }
	        return socket;
	    }
		
		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
				throws IOException, UnknownHostException {
			Socket tmpSocket = sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
			return enableTls12(tmpSocket);
		}

		@Override
		public Socket createSocket() throws IOException {
			Socket tmpSocket = sslContext.getSocketFactory().createSocket();
			return enableTls12(tmpSocket);
		}
	}
}
