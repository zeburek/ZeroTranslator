package ru.zerotime.translator;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by zeburek on 18.03.2017.
 */

public class TLayoutChanger {
    /*Strings declaration*/
    private View main;
    private View history;
    private View settings;
    private Activity mainActivity;

    public TLayoutChanger(MainActivity activity){
        mainActivity = activity;
        main = mainActivity.findViewById(R.id.id_translator);
        history = mainActivity.findViewById(R.id.id_history);
        settings = mainActivity.findViewById(R.id.id_settings);
    }

    public void setSelectedLayout(String layout){
        if(layout.equals("main")){
            main.setVisibility(View.VISIBLE);
            history.setVisibility(View.GONE);
            settings.setVisibility(View.GONE);
        }else if(layout.equals("history")){
            main.setVisibility(View.GONE);
            history.setVisibility(View.VISIBLE);
            settings.setVisibility(View.GONE);
        }else if(layout.equals("settings")){
            main.setVisibility(View.GONE);
            history.setVisibility(View.GONE);
            settings.setVisibility(View.VISIBLE);
        }
    }
}
