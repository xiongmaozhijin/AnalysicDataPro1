package mis.nanshchui.com.test;

import mis.nanshchui.com.app.EntitiyFamilyNoticeEvent;
import mis.nanshchui.com.app.LogUtils;
import mis.nanshchui.com.app.SocketHelper;
import mis.nanshchui.com.app.StrHelperUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasee on 2017/3/14.
 */
public class TestMain {

    public static void main(String[] args) throws UnsupportedEncodingException {
        LogUtils.logI("hello world!!");


        String xx = "0b 7d 94 62 00 00 00 05 00 00 00 00 00 00 bd 6d 00 01 13 1c 58 c6 c6 b7 54 d1 6c a8";

        LogUtils.logI("length.=%d", xx.split(" ").length);

        long value = 0x68c7a14c;
        if ( (value&0x0000000058000000) == 0x0000000058000000)  {
            LogUtils.logI("true");
        }

//        doTest();
    }

    private static boolean doTest(String dirFile, String strQQ, String strCk) {
        String qqHex = StrHelperUtils.getQQHex(strQQ);
        String ckHex = StrHelperUtils.getCkHex(strCk);

        String reqCmd = "CA 00 00 00 3A 00 00 00 1B 01 02 01 " + qqHex + " 00 00 00 0B " + ckHex + " 00 56 00 17 00 86 27 7C " + qqHex + " 00 00 00 00 00 04 00 00 00 01 00 00 " + qqHex;
        byte[] execCmd = StrHelperUtils.hexStr2Bytes(reqCmd);
        byte[] validateCmd = SocketHelper.getValidateCmd(qqHex, ckHex);

        byte[] resultBytes = SocketHelper.requestCmd(execCmd, validateCmd);

//        StrHelperUtils.debugBytes("公告: ", resultBytes);
        int valueLen;
        long longValue;
        int curPos = 0;

        //get total length
        curPos += 56;
        longValue = StrHelperUtils.getLongValue(resultBytes, curPos, 4);

        if (longValue >= 2000) {
            LogUtils.logI("检测到事件数量大于2000，可能存在错误");
        }

        curPos += 12;

        EntitiyFamilyNoticeEvent familyNoticeEvent = new EntitiyFamilyNoticeEvent();

        List<EntitiyFamilyNoticeEvent.FamilyEvent> listNoticeEvents  = new ArrayList<>();
        EntitiyFamilyNoticeEvent.FamilyEvent eventItem;
        for (int i=0, eventCnt=(int)longValue, limit=10000; i<eventCnt && limit>0; i++, limit--) {
            eventItem = new EntitiyFamilyNoticeEvent.FamilyEvent();
            listNoticeEvents.add(eventItem);

            longValue = StrHelperUtils.getLongValue(resultBytes, curPos, 4);
            eventItem.strQQ = longValue + "";

            curPos += 4;
            longValue = StrHelperUtils.getLongValue(resultBytes, curPos, 4);
            eventItem.info1 = longValue;

            curPos += 4;
            longValue = StrHelperUtils.getLongValue(resultBytes, curPos, 4);
            eventItem.infoType = longValue;



            curPos += 4;
        }

        familyNoticeEvent.setListNoticeEvents(listNoticeEvents);

        try {
            FileUtils.writeLines(new File(dirFile, "familyEvents.txt"), familyNoticeEvent.getEventsDesc());
        } catch (IOException e) {
//            e.printStackTrace();
            LogUtils.logE("写入文件familyEvents.txt错误");
            return false;
        }

        return true;
    }



}


