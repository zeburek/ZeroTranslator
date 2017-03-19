package ru.zerotime.translator;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static ru.zerotime.translator.MainActivity.TAG_ZT;

/**
 * Created by zeburek on 19.03.2017.
 */

public class THistoryBookmarksProvider {

    public static Map<String,Boolean> mainBookmarksMap = new HashMap<String,Boolean>();
    public static Map<String,String> mainHistoryTransMap = new HashMap<String,String>();
    public static Map<String,String> mainHistoryLangPairMap = new HashMap<String,String>();

    public THistoryBookmarksProvider(){}

    public void setNewHistoryItem(String text, String translatedText, String langPair){
        if (checkIfHistoryItemExists(text)){return;}
        mainHistoryTransMap.put(text,translatedText);
        mainHistoryLangPairMap.put(text,langPair);
        Log.d(TAG_ZT, "Text: "+text
        +"\nTranslation: "+translatedText
        +"\nLangPair: "+langPair);
    }

    public void setNewBookmarksItem(String text){
        if (checkIfBookmarksItemExists(text)){return;}
        mainBookmarksMap.put(text,true);
        Log.d(TAG_ZT,"Bookmark added: "+text);
    }

    public void removeBookmarksItem(String text){
        if (!checkIfBookmarksItemExists(text)){return;}
        mainBookmarksMap.remove(text);
        Log.d(TAG_ZT,"Bookmark removed: "+text);
    }

    public int getHistoryLength(){
        return mainHistoryTransMap.size();
    }

    public String getHistoryKeyById(int id){
        Set<String> keysSet = mainHistoryTransMap.keySet();
        String keyName = keysSet.toArray()[id].toString();
        Log.d(TAG_ZT, "KeyName: "+keyName);
        return  keyName;
    }

    public String getHistoryTranslationByKey(String key){
        return mainHistoryTransMap.get(key);
    }

    public String getHistoryLangPairByKey(String key){
        return mainHistoryLangPairMap.get(key);
    }

    public Boolean getBookmarkIfExistByKey(String key){
        boolean contains = mainBookmarksMap.containsKey(key);
        if(contains){
            return mainBookmarksMap.get(key);
        }
        return false;
    }

    private boolean checkIfHistoryItemExists(String text){
        return mainHistoryTransMap.containsKey(text);
    }

    private boolean checkIfBookmarksItemExists(String text){
        return mainBookmarksMap.containsKey(text);
    }
}
