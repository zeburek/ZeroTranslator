package ru.zerotime.translator;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
 * This class works with all translations,
 * accesses THTTPProvider and send all needed information to it.
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

    /*Different variable declaration*/
    private THTTPProvider thttpProvider=new THTTPProvider();
    private Map<String, String> mainMapLanguages = new HashMap<String, String>();
    private Thread getLangListThread;
    private Thread getTranslateThread;


    public TTranslatorClass(){}

    public TTranslatorClass(THTTPProvider thttpProvider){
        this.thttpProvider = thttpProvider;
    }

    /*Method to access translations*/
    public void getTranslate(final EditText textField, final TextView exportView){
        /*Check if Thread is already running*/
        getTranslateThread = null;
        /*Check internet connection*/
        if (!isInternetAvailable(textField.getContext())){
            Toast.makeText(textField.getContext(),
                    exportView.getContext().getString(R.string.response_no_internet),
                    Toast.LENGTH_LONG).show();
            return;
        }
        /*Init new Thread and run it*/
        getTranslateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                /*Collect all data to ArrayList*/
                String textFromField = textField.getText().toString();
                Log.e(TAG_ZT,textFromField);
                List<BasicNameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("key", appIdForSDKString));
                nameValuePairs.add(new BasicNameValuePair("text", textFromField));
                /*A simple check, if something is not selected
                * in case not to send empty values*/
                nameValuePairs.add(new BasicNameValuePair("lang",getLangPair()));

                /*Send request to THTTPProvider*/
                thttpProvider.sendPostRequestToTranslate(translateTextFromSDKString,
                        nameValuePairs,
                        exportView);
            }
        });
        getTranslateThread.start();

    }
    public void getLangsList(final Spinner beginLangs,
                             final Spinner endLangs,
                             final Activity activity){
        /*Check if Thread is already running*/
        getLangListThread = null;
        Log.d(TAG_ZT, "System lang: " + Locale.getDefault().getLanguage());
        /*Loading lang list from disk, if File exists*/
        loadLanguageFromDiskIfAvailabled(beginLangs,endLangs,activity);
        /*Check internet connection*/
        if (!isInternetAvailable(endLangs.getContext())){
            Toast.makeText(endLangs.getContext(),
                    beginLangs.getContext().getString(R.string.response_no_internet),
                    Toast.LENGTH_LONG).show();
            return;
        }
        /*Init new Thread and run it
        * I do it in one Method with loading from disk,
        * because I want it to load only on first start*/
        getLangListThread = new Thread(new Runnable() {
            @Override
            public void run() {
                /*Collect all data to ArrayList*/
                List<BasicNameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("key", appIdForSDKString));
                nameValuePairs.add(new BasicNameValuePair("ui",
                        Locale.getDefault().getLanguage()));
                /*Send request to THTTPProvider*/
                thttpProvider.sendPostRequestToGetLangList(getLangsFromSDKString,nameValuePairs);
                /*Wait while THTTPProvider is getting the response from server.
                * We are in additional Thread, so it shouldn't freez app and make a problem for us*/
                int secs = 0;
                while(thttpProvider.langListXML == null){
                    secs++;
                }
                Log.d(TAG_ZT, "Last: "+secs);
                /*Now we got our XML data and we should parse it and add to spinner*/
                XmlPullParser parser =thttpProvider.langListXML;
                try {
                    while (parser.getEventType()!= XmlPullParser.END_DOCUMENT) {
                        if (parser.getEventType() == XmlPullParser.START_TAG
                                && parser.getName().equals("Item")) {
                            mainMapLanguages.put(parser.getAttributeValue(1),
                                    parser.getAttributeValue(0));
                        }
                        parser.next();
                    }
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ArrayAdapter<String> adapter = getLanguagesArrayAdapter(activity);
                /*Couldn't change Activity View in additional Thread, should make it on UI thread, so Method for it*/
                setLangListToUI(adapter, beginLangs, endLangs);
            }
        });
        getLangListThread.start();
    }

    /*Method to set adapter for Spinners on main activity*/
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

    /*Method to load language map into spinners using Adapter*/
    private void loadLanguageFromDiskIfAvailabled(Spinner beginLangs,
                                                  Spinner endLangs,
                                                  Activity activity) {
        if (!isFileLanguageMapExists()) return;
        loadLanguageMapFromDisk();
        ArrayAdapter<String> adapter = getLanguagesArrayAdapter(activity);
        setLangListToUI(adapter, beginLangs, endLangs);
    }

    /*Method to compute an ArrayAdapter for spinners*/
    @NonNull
    private ArrayAdapter<String> getLanguagesArrayAdapter(Activity activity) {
        Set setOfLangs = mainMapLanguages.keySet();
        String[] arrayOfLangs = (String[]) setOfLangs.toArray(new String[setOfLangs.size()]);
        Arrays.sort(arrayOfLangs);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_spinner_item,
                arrayOfLangs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    /*Setting begin lang String*/
    public void setLangBegin(String langSelected){
        this.langBegin = checkLangNameFromList(langSelected);
    }

    /*Setting end lang String*/
    public void setLangEnd(String langSelected){
        this.langEnd = checkLangNameFromList(langSelected);
    }

    /*Get 2 character code of lang using it's name*/
    private String checkLangNameFromList(String listName){
        Log.d(TAG_ZT, "SelectedLang: "+listName);
        Log.d(TAG_ZT, "CheckLang: "+mainMapLanguages.get(listName));

        return mainMapLanguages.get(listName);
    }

    /*Get lang Pair. Also checks if something is null - in this case we will use default values*/
    public String getLangPair(){
        if(!langBegin.equals("") && !langEnd.equals("")){
            return langBegin+"-"+langEnd;
        }else if(langBegin.equals("") && !langEnd.equals("")){
            return "ru-"+langEnd;
        }else if(!langBegin.equals("") && langEnd.equals("")){
            return langBegin+"-en";
        }else{
            return "ru-en";
        }
    }

    /*Checking internet connection*/
    public boolean isInternetAvailable(final Context context) {
        final ConnectivityManager connectivityManager =
                ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    /*Method to save map with languages to disk*/
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

    /*Method to load map with languages from disk*/
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
