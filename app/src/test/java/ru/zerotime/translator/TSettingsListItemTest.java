package ru.zerotime.translator;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by zeburek on 01.04.2017.
 * Created for ZeroTranslator, ru.zerotime.translator
 * Plaese ask, before fork!
 */
public class TSettingsListItemTest {
    @Test
    public void setMainText() throws Exception {
        TSettingsListItem tSettingsListItem = new TSettingsListItem();
        tSettingsListItem.setMainText("test");
        Assert.assertEquals("test",tSettingsListItem.getMainText());
    }

    @Test
    public void setDescText() throws Exception {
        TSettingsListItem tSettingsListItem = new TSettingsListItem();
        tSettingsListItem.setDescText("test");
        Assert.assertEquals("test",tSettingsListItem.getDescText());
    }

}