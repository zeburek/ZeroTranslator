package ru.zerotime.translator;

/**
 * Created by zeburek on 19.03.2017.
 * Class for custom ListItem
 * This class is for Settings items
 */

public class TSettingsListItem {

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    public String getDescText() {
        return descText;
    }

    public void setDescText(String descText) {
        this.descText = descText;
    }

    private String mainText;
    private String descText;
}