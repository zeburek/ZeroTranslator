package ru.zerotime.translator;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String TAG_ZT = "ZeroTranslator";

    private TLayoutChanger layoutChanger;
    private TTranslatorClass tTranslatorClass = new TTranslatorClass();
    private THistoryBookmarksProvider tHistoryBookmarksProvider = new THistoryBookmarksProvider();

    public Button submitButton;
    public Button replaceButton;
    public Spinner spinnerLangBegin;
    public Spinner spinnerLangEnd;
    public ToggleButton bookmarksToggleButton;
    public EditText editText;
    public TextView outputTextView;
    public TextView copyRightTextView;
    public ListView historyListView;

    private ArrayList historyArrayList;
    private CustomAdapter historyCustomAdapter;


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
                    return true;
            }
            return false;
        }

    };

    private void setHistoryToView() {
        historyListView = (ListView) findViewById(R.id.historyListView);
        historyArrayList = new ArrayList<TListItem>();
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
        historyCustomAdapter = new CustomAdapter(this,historyArrayList);
        historyListView.setAdapter(historyCustomAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        layoutChanger = new TLayoutChanger(MainActivity.this);

        submitButton = (Button)findViewById(R.id.id_execute);
        replaceButton = (Button)findViewById(R.id.id_replacebutton);
        spinnerLangBegin = (Spinner)findViewById(R.id.spinnerLangBegin);
        spinnerLangEnd = (Spinner)findViewById(R.id.spinnerLangEnd);
        bookmarksToggleButton = (ToggleButton)findViewById(R.id.id_adtobookmarks);
        editText = (EditText)findViewById(R.id.inputTextField);
        outputTextView = (TextView)findViewById(R.id.outputTextField);
        copyRightTextView = (TextView)findViewById(R.id.copyRight);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            copyRightTextView.setText(Html.fromHtml("Переведено сервисом " +
                    "<a href='http://translate.yandex.ru/'>«Яндекс.Переводчик»</a>",
                    Html.FROM_HTML_MODE_COMPACT));
        } else {
            copyRightTextView.setText(Html.fromHtml("Переведено сервисом " +
                    "<a href='http://translate.yandex.ru/'>«Яндекс.Переводчик»</a>"));
        }
        copyRightTextView.setClickable(true);
        copyRightTextView.setMovementMethod(LinkMovementMethod.getInstance());

        tTranslatorClass.getLangsList(spinnerLangBegin,spinnerLangEnd,MainActivity.this);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tTranslatorClass.getTranslate(editText,outputTextView);
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
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
