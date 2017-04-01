package ru.zerotime.translator;

/**
 * Created by zeburek on 19.03.2017.
 * Class for custom ListItem
 * This class is for History items
 */
public class THistoryListItem {

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    public String getTranslateText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }

    public String getLangPair(){
        return langPair;
    }

    public void setLangPair(String langPair){
        this.langPair = langPair;
    }

    public boolean getIsBookmark() {
        return isBookmark;
    }

    public void setIsBookmark(boolean bookmark) {
        this.isBookmark = bookmark;
    }

    private String mainText;
    private String translatedText;
    private String langPair;
    private boolean isBookmark;
}