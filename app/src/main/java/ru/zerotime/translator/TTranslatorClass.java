package ru.zerotime.translator;

import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zeburek on 18.03.2017.
 */

public class TTranslatorClass {
    /*Strings declaration*/
    private static String appIdForSDKString = "trnsl.1.1.20170318T125113Z.0e9c689ca30159be.6ce4b9da56c5d2b2ca033b69c6d5a32f810375d0";
    private static String getLangsFromSDKString = "https://translate.yandex.net/api/v1.5/tr.json/getLangs";
    private static String detectLangsFromSDKString = "https://translate.yandex.net/api/v1.5/tr.json/detect";
    private static String translateTextFromSDKString = "https://translate.yandex.net/api/v1.5/tr.json/translate";
    private String langBegin;
    private String langEnd;

    private THTTPProvider thttpProvider=new THTTPProvider();

    public TTranslatorClass(){}

    public void getTranslate(EditText textField, TextView exportView){
        String textFromField = textField.getText().toString();
        Log.e("ZeroTranslator",textFromField);
        List<BasicNameValuePair> nameValuePairs = new ArrayList<>(2);
        nameValuePairs.add(new BasicNameValuePair("key", appIdForSDKString));
        nameValuePairs.add(new BasicNameValuePair("text", textFromField));
        nameValuePairs.add(new BasicNameValuePair("lang","en"));
        String response = thttpProvider.sendPostRequest(translateTextFromSDKString,nameValuePairs);
        exportView.setText(response);
    }

    public void setLangBegin(String langBegin){
        this.langBegin = langBegin;
    }

    public void setLangEnd(String langEnd){
        this.langEnd = langEnd;
    }

    public String[] getLangEndList(String langBegin){

        return null;
    }
}
