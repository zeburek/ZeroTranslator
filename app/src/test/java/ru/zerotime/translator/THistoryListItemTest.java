package ru.zerotime.translator;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by zeburek on 01.04.2017.
 * Created for ZeroTranslator, ru.zerotime.translator
 * Plaese ask, before fork!
 */
public class THistoryListItemTest {
    @Test
    public void setMainText() throws Exception {
        THistoryListItem tHistoryListItem = new THistoryListItem();
        tHistoryListItem.setMainText("test");
        Assert.assertEquals("test",tHistoryListItem.getMainText());
    }

    @Test
    public void setTranslatedText() throws Exception {
        THistoryListItem tHistoryListItem = new THistoryListItem();
        tHistoryListItem.setTranslatedText("test");
        Assert.assertEquals("test",tHistoryListItem.getTranslateText());
    }

    @Test
    public void setLangPair() throws Exception {
        THistoryListItem tHistoryListItem = new THistoryListItem();
        tHistoryListItem.setLangPair("test");
        Assert.assertEquals("test",tHistoryListItem.getLangPair());
    }

    @Test
    public void setIsBookmark() throws Exception {
        THistoryListItem tHistoryListItem = new THistoryListItem();
        tHistoryListItem.setIsBookmark(true);
        Assert.assertTrue(tHistoryListItem.getIsBookmark());
    }

}