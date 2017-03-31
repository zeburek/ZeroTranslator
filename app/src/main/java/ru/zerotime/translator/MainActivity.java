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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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

import org.w3c.dom.Text;

import java.util.ArrayList;

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
    public EditText editText;
    public TextView outputTextView;
    public TextView copyRightTextView;
    public ListView historyListView;
    public ListView settingsListView;
    public BottomNavigationView navigation;

    private CustomAdapter historyCustomAdapter;
    private SettingsCustomAdapter settingsCustomAdapter;
    private static CharSequence emptyOutputFieldCharSequence;
    private static Editable emptyInputFieldEditable;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        int id = navigation.getSelectedItemId();
        SpinnerAdapter adapter = spinnerLangBegin.getAdapter();
        int beginId = spinnerLangBegin.getSelectedItemPosition();
        int endId = spinnerLangEnd.getSelectedItemPosition();
        Editable input = editText.getText();
        CharSequence output = outputTextView.getText();

        ((LinearLayout)findViewById(R.id.container)).removeAllViews();

        super.onConfigurationChanged(newConfig);
        initUI();

        spinnerLangBegin.setAdapter(adapter);
        spinnerLangEnd.setAdapter(adapter);
        spinnerLangBegin.setSelection(beginId);
        spinnerLangEnd.setSelection(endId);
        editText.setText(input);
        outputTextView.setText(output);

        Log.d(TAG_ZT, "Config changed.");
        navigation.setSelectedItemId(id);
    }

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

    private void setSettingsToView() {
        settingsListView = (ListView) findViewById(R.id.settingsListView);
        ArrayList<TSettingsListItem> settingsArrayList = new ArrayList<TSettingsListItem>();
        int count = tHistoryBookmarksProvider.getHistoryLength()-1;
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

        settingsCustomAdapter = new SettingsCustomAdapter(this,settingsArrayList);
        settingsListView.setAdapter(settingsCustomAdapter);
    }

    private void setSettingsActionListener(){
        settingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        tHistoryBookmarksProvider.clearHistoryOnly();
                        editText.setText("");
                        outputTextView.setText("");
                        bookmarksToggleButton.setChecked(false);
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.settings_toast_historycleared),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        tHistoryBookmarksProvider.clearAllData();
                        editText.setText("");
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

    private void setHistoryToView() {
        historyListView = (ListView) findViewById(R.id.historyListView);
        ArrayList<TListItem> historyArrayList = new ArrayList<TListItem>();
        int count = tHistoryBookmarksProvider.getHistoryLength()-1;
        for (int i = count;i >= 0;i--){
            String name = tHistoryBookmarksProvider.getHistoryKeyById(i);
            String translate = tHistoryBookmarksProvider.getHistoryTranslationByKey(name);
            String pair = tHistoryBookmarksProvider.getHistoryLangPairByKey(name);
            boolean isBook = tHistoryBookmarksProvider.getBookmarkIfExistByKey(name);
            TListItem listItem = new TListItem();
            listItem.setMainText(name);
            listItem.setTranslatedText(translate);
            listItem.setLangPair(pair.toUpperCase());
            listItem.setIsBookmark(isBook);

            historyArrayList.add(listItem);
        }
        historyCustomAdapter = new CustomAdapter(this, historyArrayList);
        historyListView.setAdapter(historyCustomAdapter);
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView beginTextView = (TextView) view.findViewById(R.id.mainNameView);
                CharSequence beginText = beginTextView.getText();

                navigation.setSelectedItemId(R.id.navigation_translator);

                editText.setText(beginText);
                submitButton.performClick();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();
        tTranslatorClass.getLangsList(spinnerLangBegin,spinnerLangEnd,MainActivity.this);
    }

    private void initUI() {
        setDrawableColor();

        setContentView(R.layout.activity_main);

        layoutChanger = new TLayoutChanger(MainActivity.this);
        tHistoryBookmarksProvider.setAppPath(MainActivity.this.getApplicationInfo().dataDir);
        tHistoryBookmarksProvider.loadAllMapsFromDisk();

        setVariablesFromView();
        emptyOutputFieldCharSequence = outputTextView.getText();
        emptyInputFieldEditable = editText.getText();

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

    private void setVariablesFromView() {
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        submitButton = (ImageButton)findViewById(R.id.id_execute);
        replaceButton = (ImageButton)findViewById(R.id.id_replacebutton);
        spinnerLangBegin = (Spinner)findViewById(R.id.spinnerLangBegin);
        spinnerLangEnd = (Spinner)findViewById(R.id.spinnerLangEnd);
        bookmarksToggleButton = (ToggleButton)findViewById(R.id.id_adtobookmarks);
        editText = (EditText)findViewById(R.id.inputTextField);
        outputTextView = (TextView)findViewById(R.id.outputTextField);
        copyRightTextView = (TextView)findViewById(R.id.copyRight);
    }

    private void setDrawableColor() {
        Drawable normalDrawable = getResources().getDrawable(R.drawable.icon_on_bookmarks);
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, getResources().getColor(R.color.colorPrimary));

        Drawable normalDrawable1 = getResources().getDrawable(R.drawable.icon_off_bookmarks);
        Drawable wrapDrawable1 = DrawableCompat.wrap(normalDrawable1);
        DrawableCompat.setTint(wrapDrawable1, getResources().getColor(R.color.colorPrimary));
    }

    private void initAllListeners() {
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
                    tHistoryBookmarksProvider.setNewBookmarksItem(editText.getText().toString());
                }
                else
                {
                    Log.d(TAG_ZT,"Try to remove bookmark");
                    tHistoryBookmarksProvider.removeBookmarksItem(editText.getText().toString());
                }
            }
        });

        outputTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tHistoryBookmarksProvider.setNewHistoryItem(editText.getText().toString(),s.toString(),tTranslatorClass.getLangPair());
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

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                bookmarksToggleButton.setChecked(false);
                if(tHistoryBookmarksProvider
                        .getBookmarkIfExistByKey(editText.getText().toString())){
                    bookmarksToggleButton.setChecked(true);
                }
            }
        });
    }

    private void getTranslate() {
        if(tHistoryBookmarksProvider.checkIfHistoryItemExists(editText.getText().toString())){
            String translate = tHistoryBookmarksProvider.getHistoryTranslationByKey(editText.getText().toString());
            outputTextView.setText(translate);
        }
        tTranslatorClass.getTranslate(editText,outputTextView);
    }
}
