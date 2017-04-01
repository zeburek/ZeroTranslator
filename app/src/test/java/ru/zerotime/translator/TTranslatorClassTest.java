package ru.zerotime.translator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by zeburek on 25.03.2017.
 * Tests for TTranslatorClass
 */
@RunWith(MockitoJUnitRunner.class)
public class TTranslatorClassTest{

    @Test
    public void getLangPair_test_onlyBeginLangChanged() throws Exception {
        TTranslatorClass tTranslatorClass = new TTranslatorClass();
        tTranslatorClass.langBegin = "de";
        Assert.assertEquals("de-en",tTranslatorClass.getLangPair());
    }

    @Test
    public void getLangPair_test_onlyEndLangChanged() throws Exception {
        TTranslatorClass tTranslatorClass = new TTranslatorClass();
        tTranslatorClass.langEnd = "de";
        Assert.assertEquals("ru-de",tTranslatorClass.getLangPair());
    }

    @Test
    public void getLangPair_test_bothLangChanged() throws Exception {
        TTranslatorClass tTranslatorClass = new TTranslatorClass();
        tTranslatorClass.langBegin = "de";
        tTranslatorClass.langEnd = "de";
        Assert.assertEquals("de-de",tTranslatorClass.getLangPair());
    }

    @Test
    public void getLangPair_test_noLangChanged() throws Exception {
        TTranslatorClass tTranslatorClass = new TTranslatorClass();
        Assert.assertEquals("ru-en",tTranslatorClass.getLangPair());
    }
}
