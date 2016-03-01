package com.weiguan.kejian.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;

import android.util.Log;

public class HttpsHelper {
	public static byte[] readData(InputStream is) throws IOException {
		InputStreamReader isr = new InputStreamReader(is, "GBK");
		StringBuilder sb = new StringBuilder();
		int len = 0;
		while ((len = isr.read()) != -1) {
			sb.append((char) len);
		}
		return sb.toString().getBytes();
	}

	/** The Constant HTTP_TIME_OUT.请求超时 */
	private static final int HTTP_TIME_OUT = 15 * 1000;

	/** The Constant SO_TIMEOUT. 等待数据超时 */
	private static final int SO_TIMEOUT = 15 * 1000;
	
	private static X509TrustManager goTrustMgr = new X509TrustManager() {

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	};
	
	private static X509TrustManager[] gaTrustMgr = new X509TrustManager[] { goTrustMgr };
	/**
	 * 
	 * @param urlPath https://121.8.254.242/
	 * @return "-1" bks not exist
	 */
	public static ArrayList getHttpsContent(Map<String, String> paramsMap, String urlPath){
		ArrayList list = new ArrayList();
		list.add(paramsMap);
		try{
//				InputStream keyStoreInput = new FileInputStream(bksFile);
//				
//				String keyStoreType = KeyStore.getDefaultType();
//				KeyStore keyStore = KeyStore.getInstance(keyStoreType);
//				keyStore.load(keyStoreInput, KEYSTORE_PASSWORD.toCharArray());
//				
//				// Create a TrustManager that trusts the CAs in our KeyStore
//				String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//				TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//				tmf.init(keyStore);
//				
//				KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
//				kmf.init(keyStore, KEYSTORE_PASSWORD.toCharArray());

				// Create an SSLContext that uses our TrustManager
//				SSLContext sslContext = SSLContext.getInstance("TLS");
//				sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
				SSLContext sslContext = SSLContext.getInstance("SSL");
				sslContext.init(null, gaTrustMgr, new java.security.SecureRandom());

		        X509HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
		        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

		        StringBuilder entityBuilder = new StringBuilder("");
		        if(paramsMap!=null && !paramsMap.isEmpty()){
		        for(Entry<String, String> entry : paramsMap.entrySet()){
		        entityBuilder.append(entry.getKey()).append('=');
		        entityBuilder.append(URLEncoder.encode(entry.getValue(), "GBK"));
		        entityBuilder.append('&');
		        }
		        entityBuilder.deleteCharAt(entityBuilder.length() - 1);
		        }
				Log.i("vinci", "请求参数 = " + paramsMap.toString());
		        byte[] entity = entityBuilder.toString().getBytes();
		        
				// Tell the URLConnection to use a SocketFactory from our SSLContext
				//URL url = new URL("https://172.16.18.109");
				URL url = new URL(urlPath);
//				HttpsURLConnection urlConnection = (HttpsURLConnection) url
//						.openConnection();
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//				urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());
				urlConnection.setConnectTimeout(5 * 1000);
				urlConnection.setRequestMethod("POST");
				urlConnection.setDoOutput(true);//允许输出数据
				urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				urlConnection.setRequestProperty("Content-Length", String.valueOf(entity.length));
				OutputStream outStream = urlConnection.getOutputStream();
				outStream.write(entity);
				outStream.flush();
				outStream.close();
				
				InputStream is = urlConnection.getInputStream();
				InputStreamReader in = new InputStreamReader(is,"GBK");  
				StringBuffer sb = new StringBuffer();

				String line = null;
				char ch = '\u0000';
				int temp = 0 ;
				while ((temp = in.read()) != -1) {
					ch = (char) temp;
					sb.append((char) temp);
				}
				String result = sb.toString();
		        
				Log.i("response", "-->"+result);

				list.add(result);
		        return list;
			
		}catch(Exception e){
			e.printStackTrace();
			Log.i("response", "-->-2");
			list.add("-2");
			return list;
		}
        
	}
	public static String getHttpsContentUTF(Map<String, String> paramsMap, String urlPath){
		try{
//				InputStream keyStoreInput = new FileInputStream(bksFile);
//
//				String keyStoreType = KeyStore.getDefaultType();
//				KeyStore keyStore = KeyStore.getInstance(keyStoreType);
//				keyStore.load(keyStoreInput, KEYSTORE_PASSWORD.toCharArray());
//
//				// Create a TrustManager that trusts the CAs in our KeyStore
//				String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//				TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//				tmf.init(keyStore);
//
//				KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
//				kmf.init(keyStore, KEYSTORE_PASSWORD.toCharArray());

			// Create an SSLContext that uses our TrustManager
//				SSLContext sslContext = SSLContext.getInstance("TLS");
//				sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, gaTrustMgr, new java.security.SecureRandom());

			X509HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
			HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

			StringBuilder entityBuilder = new StringBuilder("");
			if(paramsMap!=null && !paramsMap.isEmpty()){
				for(Entry<String, String> entry : paramsMap.entrySet()){
					entityBuilder.append(entry.getKey()).append('=');
					entityBuilder.append(entry.getValue());
//					entityBuilder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
					entityBuilder.append('&');
				}
				entityBuilder.deleteCharAt(entityBuilder.length() - 1);
			}
//			Log.i("params", entityBuilder.toString());
			Log.i("vinci", "请求参数 = " + paramsMap.toString());
			byte[] entity = entityBuilder.toString().getBytes();

			// Tell the URLConnection to use a SocketFactory from our SSLContext
			//URL url = new URL("https://172.16.18.109");
			URL url = new URL(urlPath);
			HttpsURLConnection urlConnection = (HttpsURLConnection) url
					.openConnection();
//				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());
			urlConnection.setConnectTimeout(5 * 1000);
			urlConnection.setRequestMethod("POST");
			urlConnection.setDoOutput(true);//允许输出数据
			urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			urlConnection.setRequestProperty("Content-Length", String.valueOf(entity.length));
			OutputStream outStream = urlConnection.getOutputStream();
			outStream.write(entity);
			outStream.flush();
			outStream.close();

			InputStream is = urlConnection.getInputStream();
			InputStreamReader in = new InputStreamReader(is,"UTF-8");
			StringBuffer sb = new StringBuffer();

			String line = null;
			char ch = '\u0000';
			int temp = 0 ;
			while ((temp = in.read()) != -1) {
				ch = (char) temp;
				sb.append((char) temp);
			}

			String result = sb.toString();

			Log.i("response", result);

			return result;

		}catch(Exception e){
			e.printStackTrace();
			Log.i("response", "-->-2");
			return "-2";
		}

	}

	public static String httpsGet(String urlPath){
		try{
//				InputStream keyStoreInput = new FileInputStream(bksFile);
//
//				String keyStoreType = KeyStore.getDefaultType();
//				KeyStore keyStore = KeyStore.getInstance(keyStoreType);
//				keyStore.load(keyStoreInput, KEYSTORE_PASSWORD.toCharArray());
//
//				// Create a TrustManager that trusts the CAs in our KeyStore
//				String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//				TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//				tmf.init(keyStore);
//
//				KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
//				kmf.init(keyStore, KEYSTORE_PASSWORD.toCharArray());

			// Create an SSLContext that uses our TrustManager
//				SSLContext sslContext = SSLContext.getInstance("TLS");
//				sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, gaTrustMgr, new java.security.SecureRandom());

			X509HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
			HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

			URL url = new URL(urlPath);
			HttpsURLConnection urlConnection = (HttpsURLConnection) url
					.openConnection();
			urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());
			urlConnection.setConnectTimeout(5 * 1000);
			urlConnection.setRequestMethod("GET");

			InputStream is = urlConnection.getInputStream();
			InputStreamReader in = new InputStreamReader(is,"UTF-8");
			StringBuffer sb = new StringBuffer();

			String line = null;
			char ch = '\u0000';
			int temp = 0 ;
			while ((temp = in.read()) != -1) {
				ch = (char) temp;
				sb.append((char) temp);
			}
			String result = sb.toString();

			return result;

		}catch(Exception e){
			e.printStackTrace();
			Log.i("response", "-->-2");
			return "-2";
		}

	}
}
