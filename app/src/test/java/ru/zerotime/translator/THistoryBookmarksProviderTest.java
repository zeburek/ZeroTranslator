package ru.zerotime.translator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by zeburek on 25.03.2017.
 */

public class THistoryBookmarksProviderTest {
    @Test
    public void setNewHistoryItem_New() throws Exception {
        THistoryBookmarksProvider prov = new THistoryBookmarksProvider();
        prov.setNewHistoryItem("test","тест","en-ru");

        String name = prov.getHistoryKeyById(0);
        String translate = prov.getHistoryTranslationByKey(name);
        String pair = prov.getHistoryLangPairByKey(name);

        assertEquals("test",name);
        assertEquals("тест",translate);
        assertEquals("en-ru",pair);
    }

    @Test
    public void setNewHistoryItem_Exists() throws Exception {
        THistoryBookmarksProvider prov = new THistoryBookmarksProvider();
        prov.setNewHistoryItem("test","тест","en-ru");
        prov.setNewHistoryItem("test","тест","en-ru");

        int size = prov.getHistoryLength();

        assertEquals(1,size);
    }

    @Test
    public void setNewHistoryItem_Empty() throws Exception {
        THistoryBookmarksProvider prov = new THistoryBookmarksProvider();
        prov.setNewHistoryItem("","","en-ru");

        boolean exist = prov.checkIfHistoryItemExists("");

        assertEquals(false,exist);
    }

    @Test
    public void setNewBookmarksItem_New() throws Exception {
        THistoryBookmarksProvider prov = new THistoryBookmarksProvider();
        prov.setNewHistoryItem("test","тест","en-ru");
        prov.setNewBookmarksItem("test");

        boolean bookmark = prov.isBookmarkWithThisKeyExists("test");

        assertEquals(true,bookmark);
    }

    @Test
    public void setNewBookmarksItem_Exist() throws Exception {
        THistoryBookmarksProvider prov = new THistoryBookmarksProvider();
        prov.setNewHistoryItem("test","тест","en-ru");
        prov.setNewBookmarksItem("test");
        prov.setNewBookmarksItem("test");

        boolean bookmark = prov.isBookmarkWithThisKeyExists("test");

        assertEquals(true,bookmark);
    }

    @Test
    public void removeBookmarksItem_Remove() throws Exception {
        THistoryBookmarksProvider prov = new THistoryBookmarksProvider();
        prov.setNewHistoryItem("test","тест","en-ru");
        prov.setNewBookmarksItem("test");
        prov.removeBookmarksItem("test");

        boolean bookmark = prov.isBookmarkWithThisKeyExists("test");

        assertEquals(false,bookmark);
    }

    @Test
    public void getHistoryLength_1() throws Exception {
        THistoryBookmarksProvider prov = new THistoryBookmarksProvider();
        prov.setNewHistoryItem("test","тест","en-ru");

        int length = prov.getHistoryLength();

        assertEquals(1,length);
    }

    @Test
    public void getHistoryKeyById_test() throws Exception {
        THistoryBookmarksProvider prov = new THistoryBookmarksProvider();
        prov.setNewHistoryItem("test","тест","en-ru");

        String name = prov.getHistoryKeyById(0);

        assertEquals("test",name);
    }

    @Test
    public void getHistoryTranslationByKey_test() throws Exception{
        THistoryBookmarksProvider prov = new THistoryBookmarksProvider();
        prov.setNewHistoryItem("test","тест","en-ru");

        String name = prov.getHistoryTranslationByKey("test");

        assertEquals("тест",name);
    }

    @Test
    public void getHistoryLangPairByKey_test() throws Exception{
        THistoryBookmarksProvider prov = new THistoryBookmarksProvider();
        prov.setNewHistoryItem("test","тест","en-ru");

        String name = prov.getHistoryLangPairByKey("test");

        assertEquals("en-ru",name);
    }

    @Test
    public void getBookmarkIfExistByKey_test() throws Exception{
        THistoryBookmarksProvider prov = new THistoryBookmarksProvider();
        prov.setNewHistoryItem("test","тест","en-ru");
        prov.setNewBookmarksItem("test");

        boolean exist = prov.isBookmarkWithThisKeyExists("test");

        assertEquals(true,exist);
    }

    @Test
    public void checkIfHistoryItemExists_test() throws Exception{
        THistoryBookmarksProvider prov = new THistoryBookmarksProvider();
        prov.setNewHistoryItem("test","тест","en-ru");

        boolean exist = prov.checkIfHistoryItemExists("test");

        assertEquals(true,exist);
    }

    @Test
    public void setAppPath_testAppPath() throws Exception{
        THistoryBookmarksProvider prov = new THistoryBookmarksProvider();
        prov.setAppPath("test");

        String appPath = prov.APP_PATH;
        /*Добавляем / к проверке, т.к. команда сама его добавляет*/
        assertEquals("test/",appPath);
    }

    @Test
    public void clearHistoryOnly_test1_test2() throws Exception{
        THistoryBookmarksProvider prov = new THistoryBookmarksProvider();
        prov.setNewHistoryItem("test1","тест1","en-ru");
        prov.setNewHistoryItem("test2","тест2","en-ru");
        prov.setNewBookmarksItem("test2");
        prov.clearHistoryOnly();

        boolean exists1 = prov.checkIfHistoryItemExists("test1");
        boolean exists2 = prov.checkIfHistoryItemExists("test2");
        int length = prov.getHistoryLength();

        assertEquals(false,exists1);
        assertEquals(true,exists2);
        assertEquals(1,length);
    }

    @Test
    public void clearAllData_test() throws Exception{
        THistoryBookmarksProvider prov = new THistoryBookmarksProvider();
        prov.setNewHistoryItem("test1","тест1","en-ru");
        prov.setNewHistoryItem("test2","тест2","en-ru");
        prov.setNewBookmarksItem("test2");
        prov.clearAllData();

        boolean exists1 = prov.checkIfHistoryItemExists("test1");
        boolean exists2 = prov.checkIfHistoryItemExists("test2");
        int length = prov.getHistoryLength();

        assertEquals(false,exists1);
        assertEquals(false,exists2);
        assertEquals(0,length);
    }
}
