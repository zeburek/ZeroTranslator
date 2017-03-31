package ru.zerotime.translator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static ru.zerotime.translator.MainActivity.TAG_ZT;
import static ru.zerotime.translator.THistoryBookmarksProvider.APP_PATH;

/**
 * Created by zeburek on 18.03.2017.
 */

public class TTranslatorClass {
    /*Strings declaration*/
    private static String appIdForSDKString =
            "trnsl.1.1.20170318T125113Z.0e9c689ca30159be.6ce4b9da56c5d2b2ca033b69c6d5a32f810375d0";
    private static String getLangsFromSDKString =
            "https://translate.yandex.net/api/v1.5/tr/getLangs";
    private static String detectLangsFromSDKString =
            "https://translate.yandex.net/api/v1.5/tr.json/detect";
    private static String translateTextFromSDKString =
            "https://translate.yandex.net/api/v1.5/tr.json/translate";
    private String langBegin = "";
    private String langEnd = "";

    private THTTPProvider thttpProvider=new THTTPProvider();
    private Map<String, String> mainMapLanguages = new HashMap<String, String>();
    private Map<String, String> subMapLanguages;

    public TTranslatorClass(){}

    public TTranslatorClass(THTTPProvider thttpProvider){
        this.thttpProvider = thttpProvider;
    }

    public void getTranslate(final EditText textField, final TextView exportView){
        if (!isInternetAvailable(textField.getContext())){
            Toast.makeText(textField.getContext(),
                    exportView.getContext().getString(R.string.response_no_internet),
                    Toast.LENGTH_LONG);
            return;
        }
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

                thttpProvider.sendPostRequestToTranslate(translateTextFromSDKString,
                        nameValuePairs,
                        exportView);
            }
        });


    }
    public void getLangsList(final Spinner beginLangs,
                             final Spinner endLangs,
                             final Activity activity){
        Log.d(TAG_ZT, "System lang: " + Locale.getDefault().getLanguage());
        loadLanguageFromDiskIfAvailabled(beginLangs,endLangs,activity);
        if (!isInternetAvailable(endLangs.getContext())){
            Toast.makeText(endLangs.getContext(),
                    beginLangs.getContext().getString(R.string.response_no_internet),
                    Toast.LENGTH_LONG);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<BasicNameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("key", appIdForSDKString));
                nameValuePairs.add(new BasicNameValuePair("ui",
                        Locale.getDefault().getLanguage()));
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
                            mainMapLanguages.put(parser.getAttributeValue(1),
                                    parser.getAttributeValue(0));
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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,
                        android.R.layout.simple_spinner_item,
                        simpleArray);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                setLangListToUI(adapter, beginLangs, endLangs);
            }
        }).start();






    }

    private void setLangListToUI(final ArrayAdapter<String> adapter,
                                 final Spinner beginLangs,
                                 final Spinner endLangs) {
        Activity act = (Activity) beginLangs.getContext();
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                beginLangs.setAdapter(adapter);
                beginLangs.setSelection(60);
                endLangs.setAdapter(adapter);
                endLangs.setSelection(3);
            }
        });
    }

    private void loadLanguageFromDiskIfAvailabled(Spinner beginLangs,
                                                  Spinner endLangs,
                                                  Activity activity) {
        if (!isFileLanguageMapExists()) return;
        loadLanguageMapFromDisk();
        Set setOfLangs = mainMapLanguages.keySet();
        String[] arrayOfLangs = (String[]) setOfLangs.toArray(new String[setOfLangs.size()]);
        Arrays.sort(arrayOfLangs);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_spinner_item,
                arrayOfLangs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setLangListToUI(adapter, beginLangs, endLangs);
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

    public boolean isInternetAvailable(final Context context) {
        final ConnectivityManager connectivityManager =
                ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void saveLanguageMapOnDisk() {
        try {
            FileOutputStream fileOut = new FileOutputStream(APP_PATH + "mainMapLanguages.ztr");
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
            objOut.writeObject(mainMapLanguages);
            objOut.close();
        }catch (Exception e){
            Log.e(TAG_ZT,e.getLocalizedMessage());
        }
    }

    private boolean isFileLanguageMapExists(){
        File file = new File(APP_PATH+"mainMapLanguages.ztr");
        return file.exists();
    }

    private void loadLanguageMapFromDisk() {
        try {
            FileInputStream fileOut = new FileInputStream(APP_PATH+"mainMapLanguages.ztr");
            ObjectInputStream objOut= new ObjectInputStream(fileOut);
            mainMapLanguages = (HashMap) objOut.readObject();
            objOut.close();
        }catch (Exception e){
            Log.e(TAG_ZT,"Stack:"+e.getLocalizedMessage());
        }
    }
}
