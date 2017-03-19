package ru.zerotime.translator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    private TLayoutChanger layoutChanger;
    private TTranslatorClass tTranslatorClass = new TTranslatorClass();

    public Button submitButton;
    public Button replaceButton;
    public ToggleButton bookmarksToggleButton;
    public EditText editText;
    public TextView outputTextView;


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
                    return true;
                case R.id.navigation_settings:
                    layoutChanger.setSelectedLayout("settings");
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        layoutChanger = new TLayoutChanger(MainActivity.this);

        submitButton = (Button)findViewById(R.id.id_execute);
        replaceButton = (Button)findViewById(R.id.id_replacebutton);
        bookmarksToggleButton = (ToggleButton)findViewById(R.id.id_adtobookmarks);
        editText = (EditText)findViewById(R.id.inputTextField);
        outputTextView = (TextView)findViewById(R.id.outputTextField);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tTranslatorClass.getTranslate(editText,outputTextView);
            }
        });
    }

}
