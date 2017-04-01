package ru.zerotime.translator;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
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
    private View hOrBFab;
    private Activity mainActivity;

    public TLayoutChanger(MainActivity activity){
        mainActivity = activity;
        main = mainActivity.findViewById(R.id.id_translator);
        history = mainActivity.findViewById(R.id.id_history);
        settings = mainActivity.findViewById(R.id.id_settings);
        hOrBFab = mainActivity.findViewById(R.id.bookmarks_or_history_fab);
    }

    public void setSelectedLayout(String layout){
        if(layout.equals("main")){
            main.setVisibility(View.VISIBLE);
            history.setVisibility(View.GONE);
            hOrBFab.setVisibility(View.GONE);
            settings.setVisibility(View.GONE);
        }else if(layout.equals("history")){
            main.setVisibility(View.GONE);
            history.setVisibility(View.VISIBLE);
            hOrBFab.setVisibility(View.VISIBLE);
            settings.setVisibility(View.GONE);
        }else if(layout.equals("settings")){
            main.setVisibility(View.GONE);
            history.setVisibility(View.GONE);
            hOrBFab.setVisibility(View.GONE);
            settings.setVisibility(View.VISIBLE);
        }
    }
}
