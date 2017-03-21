package ru.zerotime.translator;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

/**
 * Created by zeburek on 21.03.2017.
 */

public class TranslatorApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

}
