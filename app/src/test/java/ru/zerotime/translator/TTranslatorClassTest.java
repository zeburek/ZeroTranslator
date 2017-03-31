package ru.zerotime.translator;

import org.junit.Test;

import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by zeburek on 25.03.2017.
 * Created for ZeroTranslator, ${PACKAGE_NAME}
 * Please ask, before fork!
 */

public class TTranslatorClassTest {
    //@Test
    public void getTranslate_test() throws Exception{
        Activity activity = new Activity();
        THTTPProvider mockThttpProvider = mock(THTTPProvider.class);
        TTranslatorClass translatorClass = mock((new TTranslatorClass(mockThttpProvider)).getClass());
        when(translatorClass.isInternetAvailable(activity)).thenReturn(true);
        EditText editText = new EditText(activity);
        TextView textView = new TextView(activity);
        editText.setText("test");

        translatorClass.getTranslate(editText,textView);
        Thread.sleep(5000);
        String translate = (String) textView.getText();

        assertEquals("тест",translate);
    }
}
