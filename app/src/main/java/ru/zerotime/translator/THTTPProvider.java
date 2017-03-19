package ru.zerotime.translator;

import android.util.Log;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by zeburek on 18.03.2017.
 */

public class THTTPProvider {
    /*Strings declaration*/
    private String responsePost;


    public THTTPProvider(){}

    public String sendPostRequest(String url, final List<BasicNameValuePair> queryParams){
        String respText="";
        final HttpClient httpclient = new DefaultHttpClient();
        final HttpPost http = new HttpPost(url);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    http.setEntity(new UrlEncodedFormEntity(queryParams,"UTF-8"));
                    //получаем ответ от сервера
                    responsePost = httpclient.execute(http, new BasicResponseHandler());
                    Log.e("ZeroTranslator",responsePost);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONObject dataJsonObj = null;
        try {
            dataJsonObj = new JSONObject(responsePost);
            respText = dataJsonObj.getJSONArray("text").getString(0);
        //Log.e("ZeroTranslator",respText);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return respText;
    }

    static {
        final TrustManager[] trustAllCertificates = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null; // Not relevant.
                    }
                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        // Do nothing. Just allow them all.
                    }
                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        // Do nothing. Just allow them all.
                    }
                }
        };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCertificates, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (GeneralSecurityException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
