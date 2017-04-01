package ru.zerotime.translator;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

/*Created for Yandex.Mobilization by Parviz Khavari (havari@yandex-team.ru).
* It is my first Android App ever. I hope it'll work fine.*/

public class MainActivity extends AppCompatActivity {

    public static final String TAG_ZT = "ZeroTranslator";

    private TLayoutChanger layoutChanger;
    private TTranslatorClass tTranslatorClass = new TTranslatorClass();
    private THistoryBookmarksProvider tHistoryBookmarksProvider = new THistoryBookmarksProvider();

    public ImageButton submitButton;
    public ImageButton replaceButton;
    public Spinner spinnerLangBegin;
    public Spinner spinnerLangEnd;
    public ToggleButton bookmarksToggleButton;
    public EditText inputEditText;
    public TextView outputTextView;
    public TextView copyRightTextView;
    public ListView historyListView;
    public ListView settingsListView;
    public BottomNavigationView navigation;

    private HistoryCustomAdapter historyHistoryCustomAdapter;
    private SettingsCustomAdapter settingsCustomAdapter;
    private static CharSequence emptyOutputFieldCharSequence;
    private static Editable emptyInputFieldEditable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initApplicationElements();
        tTranslatorClass.getLangsList(spinnerLangBegin,spinnerLangEnd,MainActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG_ZT, "onPause");
        tTranslatorClass.saveLanguageMapOnDisk();
        tHistoryBookmarksProvider.saveAllMapsOnDisk();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG_ZT, "onDestroy");
        tTranslatorClass.saveLanguageMapOnDisk();
        tHistoryBookmarksProvider.saveAllMapsOnDisk();
    }

    /*Added logic to carefully work with orientation change.
    Before it orientation breaked the current view.*/
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        /*Remember everything that can be changed by user.*/
        int id = navigation.getSelectedItemId();
        SpinnerAdapter adapter = spinnerLangBegin.getAdapter();
        int beginId = spinnerLangBegin.getSelectedItemPosition();
        int endId = spinnerLangEnd.getSelectedItemPosition();
        Editable input = inputEditText.getText();
        CharSequence output = outputTextView.getText();
        /*Remove old views*/
        ((LinearLayout)findViewById(R.id.container)).removeAllViews();

        super.onConfigurationChanged(newConfig);
        /*Re-Init UI*/
        initApplicationElements();

        /*Set back, that we've remembered.*/
        spinnerLangBegin.setAdapter(adapter);
        spinnerLangEnd.setAdapter(adapter);
        spinnerLangBegin.setSelection(beginId);
        spinnerLangEnd.setSelection(endId);
        inputEditText.setText(input);
        outputTextView.setText(output);

        Log.d(TAG_ZT, "Config changed.");

        /*Restore the selected page of View*/
        navigation.setSelectedItemId(id);
    }

    /*Listener for Bottom navigation panel*/
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_translator:
                    layoutChanger.setSelectedLayout("main");
                    return true;
                case R.id.navigation_history:
                    layoutChanger.setSelectedLayout("history");
                    setHistoryToView();
                    return true;
                case R.id.navigation_settings:
                    layoutChanger.setSelectedLayout("settings");
                    setSettingsToView();
                    setSettingsActionListener();
                    return true;
            }
            return false;
        }

    };

    /*In case not to add new Settings Activity, I've added a custom Layout, to suit my wishes*/
    private void setSettingsToView() {
        /*Getting ListView to operate with it*/
        settingsListView = (ListView) findViewById(R.id.settingsListView);
        ArrayList<TSettingsListItem> settingsArrayList = new ArrayList<TSettingsListItem>();

        /*Adding 3 TSettingsListItem to ArrayList*/
        TSettingsListItem clearHistoryListItem = new TSettingsListItem();
        clearHistoryListItem.setMainText(getString(R.string.settings_clear_history));
        clearHistoryListItem.setDescText(getString(R.string.settings_clear_history_desc));
        settingsArrayList.add(clearHistoryListItem);

        TSettingsListItem clearAllListItem = new TSettingsListItem();
        clearAllListItem.setMainText(getString(R.string.settings_clear_all));
        clearAllListItem.setDescText(getString(R.string.settings_clear_all_desc));
        settingsArrayList.add(clearAllListItem);

        TSettingsListItem aboutListItem = new TSettingsListItem();
        aboutListItem.setMainText(getString(R.string.settings_about));
        aboutListItem.setDescText(getString(R.string.settings_about_desc)+BuildConfig.VERSION_NAME+"."+BuildConfig.VERSION_CODE);
        settingsArrayList.add(aboutListItem);

        /*Set ArrayList to Adapter and add it to ListView*/
        settingsCustomAdapter = new SettingsCustomAdapter(this,settingsArrayList);
        settingsListView.setAdapter(settingsCustomAdapter);
    }

    /*In case not to add new Settings Activity, I've added a custom Layout, to suit my wishes
    * Here is my custom listener to work with ListItems*/
    private void setSettingsActionListener(){
        settingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        tHistoryBookmarksProvider.clearHistoryOnly();
                        inputEditText.setText("");
                        outputTextView.setText("");
                        bookmarksToggleButton.setChecked(false);
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.settings_toast_historycleared),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        tHistoryBookmarksProvider.clearAllData();
                        inputEditText.setText("");
                        outputTextView.setText("");
                        bookmarksToggleButton.setChecked(false);
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.settings_toast_allcleared),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://parviz.pw"));
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    /*Here is my custom View of History
    * Works pretty good, I think*/
    private void setHistoryToView() {
        /*Getting ListView to operate with it*/
        historyListView = (ListView) findViewById(R.id.historyListView);
        ArrayList<THistoryListItem> historyArrayList = new ArrayList<THistoryListItem>();
        /*Create ArrayList with all history and checked bookmarks*/
        int count = tHistoryBookmarksProvider.getHistoryLength()-1;
        for (int i = count;i >= 0;i--){
            String name = tHistoryBookmarksProvider.getHistoryKeyById(i);
            String translate = tHistoryBookmarksProvider.getHistoryTranslationByKey(name);
            String pair = tHistoryBookmarksProvider.getHistoryLangPairByKey(name);
            boolean isBook = tHistoryBookmarksProvider.getBookmarkIfExistByKey(name);
            THistoryListItem listItem = new THistoryListItem();
            listItem.setMainText(name);
            listItem.setTranslatedText(translate);
            listItem.setLangPair(pair.toUpperCase());
            listItem.setIsBookmark(isBook);

            historyArrayList.add(listItem);
        }
        /*Set ArrayList to Adapter and add it to ListView
        * Also added listener*/
        historyHistoryCustomAdapter = new HistoryCustomAdapter(this, historyArrayList);
        historyListView.setAdapter(historyHistoryCustomAdapter);
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*OnItemClick it sets inputText to Input Field and asks to translate it
                * in current Langs Pair*/
                TextView mainTextView = (TextView) view.findViewById(R.id.mainNameView);
                CharSequence inputText = mainTextView.getText();

                navigation.setSelectedItemId(R.id.navigation_translator);

                inputEditText.setText(inputText);
                submitButton.performClick();
            }
        });
    }

    /*Class to init main application elements, that need to be known onStart*/
    private void initApplicationElements() {
        setDrawableColor();

        setContentView(R.layout.activity_main);

        layoutChanger = new TLayoutChanger(MainActivity.this);
        tHistoryBookmarksProvider.setAppPath(MainActivity.this.getApplicationInfo().dataDir);
        tHistoryBookmarksProvider.loadAllMapsFromDisk();

        setVariablesFromView();
        /*Need to know the Text from empty input and output fields*/
        emptyOutputFieldCharSequence = outputTextView.getText();
        emptyInputFieldEditable = inputEditText.getText();

        /*Setting a HTML code to copyright TextView and set it clickable*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            copyRightTextView.setText(Html.fromHtml(getString(R.string.copyright_text),
                    Html.FROM_HTML_MODE_COMPACT));
        } else {
            copyRightTextView.setText(Html.fromHtml(getString(R.string.copyright_text)));
        }
        copyRightTextView.setClickable(true);
        copyRightTextView.setMovementMethod(LinkMovementMethod.getInstance());

        initAllListeners();
    }

    /*Method to init all variables from views*/
    private void setVariablesFromView() {
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        submitButton = (ImageButton)findViewById(R.id.id_execute);
        replaceButton = (ImageButton)findViewById(R.id.id_replacebutton);
        spinnerLangBegin = (Spinner)findViewById(R.id.spinnerLangBegin);
        spinnerLangEnd = (Spinner)findViewById(R.id.spinnerLangEnd);
        bookmarksToggleButton = (ToggleButton)findViewById(R.id.id_adtobookmarks);
        inputEditText = (EditText)findViewById(R.id.inputTextField);
        outputTextView = (TextView)findViewById(R.id.outputTextField);
        copyRightTextView = (TextView)findViewById(R.id.copyRight);
    }

    /*Method to add Tint to drawable on a ToggleButton.
    * It seems to be easier, then implement my own ToggleButton class*/
    private void setDrawableColor() {
        Drawable normalDrawable = getResources().getDrawable(R.drawable.icon_on_bookmarks);
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, getResources().getColor(R.color.colorPrimary));

        Drawable normalDrawable1 = getResources().getDrawable(R.drawable.icon_off_bookmarks);
        Drawable wrapDrawable1 = DrawableCompat.wrap(normalDrawable1);
        DrawableCompat.setTint(wrapDrawable1, getResources().getColor(R.color.colorPrimary));
    }

    /*Method to init all Listeners on elements*/
    private void initAllListeners() {
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTranslate();
            }
        });
        spinnerLangBegin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = spinnerLangBegin.getSelectedItem().toString();
                tTranslatorClass.setLangBegin(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerLangEnd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = spinnerLangEnd.getSelectedItem().toString();
                tTranslatorClass.setLangEnd(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        replaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int beg = spinnerLangBegin.getSelectedItemPosition();
                int end = spinnerLangEnd.getSelectedItemPosition();

                spinnerLangBegin.setSelection(end,true);
                spinnerLangEnd.setSelection(beg,true);
            }
        });

        bookmarksToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    Log.d(TAG_ZT,"Try to add bookmark");
                    Log.d(TAG_ZT, "Output field text: ["+outputTextView.getText().toString()+"]");
                    if (outputTextView.getText().toString() == "" ||
                            outputTextView.getText() == emptyOutputFieldCharSequence){
                        Log.d(TAG_ZT, "Output field is empty, wouldn't add bookmark");
                        bookmarksToggleButton.setChecked(false);
                        return;
                    }
                    tHistoryBookmarksProvider.setNewBookmarksItem(inputEditText.getText().toString());
                }
                else
                {
                    Log.d(TAG_ZT,"Try to remove bookmark");
                    tHistoryBookmarksProvider.removeBookmarksItem(inputEditText.getText().toString());
                }
            }
        });

        outputTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tHistoryBookmarksProvider.setNewHistoryItem(inputEditText.getText().toString(),s.toString(),tTranslatorClass.getLangPair());
                tHistoryBookmarksProvider.saveAllMapsOnDisk();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        outputTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = MainActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getTranslate();
            }

            @Override
            public void afterTextChanged(Editable s) {
                bookmarksToggleButton.setChecked(false);
                if(tHistoryBookmarksProvider
                        .getBookmarkIfExistByKey(inputEditText.getText().toString())){
                    bookmarksToggleButton.setChecked(true);
                }
            }
        });
    }

    private void getTranslate() {
        if(tHistoryBookmarksProvider.checkIfHistoryItemExists(inputEditText.getText().toString())){
            String translate = tHistoryBookmarksProvider.getHistoryTranslationByKey(inputEditText.getText().toString());
            outputTextView.setText(translate);
        }
        tTranslatorClass.getTranslate(inputEditText,outputTextView);
    }
}
