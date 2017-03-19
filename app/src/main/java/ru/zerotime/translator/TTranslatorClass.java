package ru.zerotime.translator;

import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.zerotime.translator.MainActivity.TAG_ZT;

/**
 * Created by zeburek on 18.03.2017.
 */

public class TTranslatorClass {
    /*Strings declaration*/
    private static String appIdForSDKString = "trnsl.1.1.20170318T125113Z.0e9c689ca30159be.6ce4b9da56c5d2b2ca033b69c6d5a32f810375d0";
    private static String getLangsFromSDKString = "https://translate.yandex.net/api/v1.5/tr/getLangs";
    private static String detectLangsFromSDKString = "https://translate.yandex.net/api/v1.5/tr.json/detect";
    private static String translateTextFromSDKString = "https://translate.yandex.net/api/v1.5/tr.json/translate";
    private String langBegin = "";
    private String langEnd = "";

    private THTTPProvider thttpProvider=new THTTPProvider();
    private List<String> mainListLanguages = new ArrayList<String>();
    Map<String, String> mainMapLanguages = new HashMap<String, String>();

    public TTranslatorClass(){}

    public void getTranslate(final EditText textField, final TextView exportView){
        Activity act = (Activity)textField.getContext();
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String textFromField = textField.getText().toString();
                Log.e(TAG_ZT,textFromField);
                List<BasicNameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("key", appIdForSDKString));
                nameValuePairs.add(new BasicNameValuePair("text", textFromField));
                if(!langBegin.equals("") && !langBegin.equals("")){
                    nameValuePairs.add(new BasicNameValuePair("lang",langBegin+"-"+langEnd));
                }else if(langBegin.equals("") && !langBegin.equals("")){
                    nameValuePairs.add(new BasicNameValuePair("lang","ru-"+langEnd));
                }else if(!langBegin.equals("") && langBegin.equals("")){
                    nameValuePairs.add(new BasicNameValuePair("lang",langBegin+"-en"));
                }else{
                    nameValuePairs.add(new BasicNameValuePair("lang","ru-en"));
                }

                thttpProvider.sendPostRequestToTranslate(translateTextFromSDKString,nameValuePairs);
                int secs = 0;
                while(thttpProvider.responsePostTranslate.equals("")){
                    secs++;
                }
                Log.d(TAG_ZT, "Last: "+secs);
                exportView.setText(thttpProvider.responsePostTranslate);
                thttpProvider.responsePostTranslate = "";
            }
        });


    }
    public void getLangsList(final Spinner beginLangs, final Spinner endLangs, final Activity activity){
        Activity act = (Activity)endLangs.getContext();
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<BasicNameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("key", appIdForSDKString));
                nameValuePairs.add(new BasicNameValuePair("ui","ru"));
                thttpProvider.sendPostRequestToGetLangList(getLangsFromSDKString,nameValuePairs);
                int secs = 0;
                while(thttpProvider.langListXML == null){
                    secs++;
                }
                Log.d(TAG_ZT, "Last: "+secs);
                XmlPullParser parser =thttpProvider.langListXML;
                List<String> where = new ArrayList<String>();
                try {
                    while (parser.getEventType()!= XmlPullParser.END_DOCUMENT) {
                        if (parser.getEventType() == XmlPullParser.START_TAG
                                && parser.getName().equals("Item")) {
                            mainMapLanguages.put(parser.getAttributeValue(1),parser.getAttributeValue(0));
                            where.add(parser.getAttributeValue(1));
                        }
                        parser.next();
                    }
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String[] simpleArray = new String[ where.size() ];
                where.toArray( simpleArray );
                Arrays.sort(simpleArray);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, simpleArray);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                beginLangs.setAdapter(adapter);
                beginLangs.setSelection(60);
                endLangs.setAdapter(adapter);
                endLangs.setSelection(3);
            }
        });

    }

    public void setLangBegin(String langSelected){
        this.langBegin = checkLangNameFromList(langSelected);
    }

    public void setLangEnd(String langSelected){
        this.langEnd = checkLangNameFromList(langSelected);
    }

    private String checkLangNameFromList(String listName){
        Log.d(TAG_ZT, "SelectedLang: "+listName);
        Log.d(TAG_ZT, "CheckLang: "+mainMapLanguages.get(listName));

        return mainMapLanguages.get(listName);
    }

    public String getLangPair(){
        String pair = langBegin+"-"+langEnd;
        return pair;
    }
}
