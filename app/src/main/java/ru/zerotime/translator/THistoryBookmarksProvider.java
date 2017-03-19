package ru.zerotime.translator;

import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static ru.zerotime.translator.MainActivity.TAG_ZT;

/**
 * Created by zeburek on 19.03.2017.
 */

public class THistoryBookmarksProvider {

    public static Map<String,Boolean> mainBookmarksMap = new HashMap<String,Boolean>();
    public static Map<String,String> mainHistoryTransMap = new LinkedHashMap<String,String>();
    public static Map<String,String> mainHistoryLangPairMap = new HashMap<String,String>();
    private static String APP_PATH;

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

    public void setAppPath(String path){
        APP_PATH = path+"/";
        Log.d(TAG_ZT,"AppPathDir: "+APP_PATH);
    }

    public void saveAllMapsOnDisk() {
        try {
            saveMainBookmarksMap();
            saveMainHistoryTransMap();
            saveMainHistoryLangPairMap();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAllMapsOnDisk() {
        try {
            loadMainBookmarksMap();
            loadMainHistoryTransMap();
            loadMainHistoryLangPairMap();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void clearHistoryOnly(){
        int count = mainHistoryTransMap.size()-1;
        int toDelete = 0;
        Map<Integer,String> saveToDelete = new HashMap<>();
        for(int i = 0; i <= count; i++){
            boolean b = getBookmarkIfExistByKey(getHistoryKeyById(i));
            if(b){} else {
                saveToDelete.put(toDelete,getHistoryKeyById(i));
                toDelete++;
            }
        }
        for (int i = 0; i<=toDelete; i++) {
            mainHistoryTransMap.remove(saveToDelete.get(i));
            mainHistoryLangPairMap.remove(saveToDelete.get(i));
        }
        saveAllMapsOnDisk();
    }

    public void clearAllData(){
        mainHistoryTransMap.clear();
        mainHistoryLangPairMap.clear();
        mainBookmarksMap.clear();
        saveAllMapsOnDisk();
    }

    private void saveMainBookmarksMap() throws IOException {
        FileOutputStream fileOut = new FileOutputStream(APP_PATH+"mainBookmarksMap.ztr");
        ObjectOutputStream objOut= new ObjectOutputStream(fileOut);
        objOut.writeObject(mainBookmarksMap);
        objOut.close();
    }
    private void saveMainHistoryTransMap() throws IOException {
        FileOutputStream fileOut = new FileOutputStream(APP_PATH+"mainHistoryTransMap.ztr");
        ObjectOutputStream objOut= new ObjectOutputStream(fileOut);
        objOut.writeObject(mainHistoryTransMap);
        objOut.close();
    }
    private void saveMainHistoryLangPairMap() throws IOException {
        FileOutputStream fileOut = new FileOutputStream(APP_PATH+"mainHistoryLangPairMap.ztr");
        ObjectOutputStream objOut= new ObjectOutputStream(fileOut);
        objOut.writeObject(mainHistoryLangPairMap);
        objOut.close();
    }
    private void loadMainBookmarksMap() throws IOException, ClassNotFoundException {
        FileInputStream fileOut = new FileInputStream(APP_PATH+"mainBookmarksMap.ztr");
        ObjectInputStream objOut= new ObjectInputStream(fileOut);
        mainBookmarksMap = (HashMap) objOut.readObject();
        objOut.close();
    }
    private void loadMainHistoryTransMap() throws IOException, ClassNotFoundException {
        FileInputStream fileOut = new FileInputStream(APP_PATH+"mainHistoryTransMap.ztr");
        ObjectInputStream objOut= new ObjectInputStream(fileOut);
        mainHistoryTransMap = (LinkedHashMap) objOut.readObject();
        objOut.close();
    }
    private void loadMainHistoryLangPairMap() throws IOException, ClassNotFoundException {
        FileInputStream fileOut = new FileInputStream(APP_PATH+"mainHistoryLangPairMap.ztr");
        ObjectInputStream objOut= new ObjectInputStream(fileOut);
        mainHistoryLangPairMap = (HashMap) objOut.readObject();
        objOut.close();
    }
}
