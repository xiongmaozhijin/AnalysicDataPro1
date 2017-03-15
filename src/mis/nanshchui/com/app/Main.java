package mis.nanshchui.com.app;

import org.apache.commons.io.FileUtils;
import sun.rmi.runtime.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static mis.nanshchui.com.app.StrHelperUtils.getBytes;

/**
 * Created by baoxing on 2017/3/14.
 */
public class Main {

    public static void main(String[] args) {

        List<String> linesAll = null;

        try {
            linesAll = FileUtils.readLines(new File("./cookies.txt"), "utf-8");

        } catch (IOException e) {
            LogUtils.logE("读取文件失败，没有找到cookies.txt");
            AppUtils.readLine();
            return;
        }

        if (linesAll == null || linesAll.isEmpty()) {
            LogUtils.logE("文件为空，请写入cookies值");
            AppUtils.readLine();
            return;
        }

        String strQQ = null;
        String strCK = null;
        String cookies = linesAll.get(0);
        String[] ckArray;

        ckArray = cookies.split(";");

        for (String ckItem : ckArray) {
            if (ckItem.contains("uin")) {
                strQQ = ckItem.trim().substring(6);
                continue;
            }

            if (ckItem.contains("skey")) {
                strCK = ckItem.trim().substring(5);
            }
        }

        if (strQQ ==null || strCK == null) {
            LogUtils.logE("格式错误，没有读取到CK值，请重新登录");
            AppUtils.readLine();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        String dirFileName = sdf.format(new Date());
        dirFileName = "./" + dirFileName;

        boolean r;
        r = analysyContribution(dirFileName, strQQ, strCK);

        if (!r) {
            LogUtils.logE("获取家族大厅信息失败V1---------------------");
        }

        LogUtils.logI("\r\n");

        AppUtils.sleep(500);

        r = analysyContribution2(dirFileName, strQQ, strCK);

        if (!r) {
            LogUtils.logE("获取家族大厅信息失败V2---------------------");
        }

        LogUtils.logI("\r\n");

        AppUtils.sleep(500);

        r = getNoticeAndEvents(dirFileName, strQQ, strCK);

        if (!r) {
            LogUtils.logE("获取家族公告信息失败---------------------");
        }

        AppUtils.readLine();
    }

    private static boolean analysyContribution(String dirFileName, String strQQ, String strCK) {
        LogUtils.logI("获取家族大厅信息中V1---QQ=%s, skey=%s", strQQ, strCK);

        String qqHex, ckHex;
        qqHex = StrHelperUtils.getQQHex(strQQ);
        ckHex = StrHelperUtils.getCkHex(strCK);


        String cmdJzGx = "CA 00 00 00 3A 00 00 00 1B 01 02 01 " + qqHex + " 00 00 00 0B " + ckHex + " 00 56 00 17 00 86 27 75 " + qqHex + " 00 00 00 00 00 04 00 00 00 01 00 00 " + qqHex;
        byte[] execBytes = StrHelperUtils.hexStr2Bytes(cmdJzGx);
        byte[] validateCmd = SocketHelper.getValidateCmd(qqHex, ckHex);


        byte[] rBytes = SocketHelper.requestCmd(execBytes, validateCmd);

        if (!SocketHelper.isValidated(rBytes)) {
            LogUtils.logE(SocketHelper.getErrorMsg(rBytes));
            return false;
        }

//        StrHelperUtils.debugBytes("家族贡献", rBytes);


        EntityFamilyInfo familyInfo = new EntityFamilyInfo();
        int intValue;
        long longValue;
        String strValue;
        int len;
        int curPos;
        byte[] valueBytes;

        //get family name
        len = 4;
        curPos = 0;
        curPos += 64;
        longValue =  StrHelperUtils.getLongValue(rBytes, curPos, len);
        try {
            valueBytes = getBytes(rBytes, curPos + 4, (int)longValue);

        } catch (Exception e) {
//            e.printStackTrace();
            LogUtils.logE(e.getLocalizedMessage());
            return false;
        }

        strValue = StrHelperUtils.convertToStrUtf8(valueBytes);
        familyInfo.setFamilyName(strValue);

        //family account
        len = 4;
        curPos += 4;
        curPos += longValue;
        longValue = StrHelperUtils.getLongValue(rBytes, curPos, len);
        familyInfo.setFamilyAccount(longValue);

        // family declaration
        curPos += 4;
        longValue = StrHelperUtils.getLongValue(rBytes, curPos, 4);

        try {
            valueBytes = getBytes(rBytes, curPos + 4, (int)longValue);

        } catch (IOException e) {
//            e.printStackTrace();
            LogUtils.logE(e.getLocalizedMessage());
            return false;
        }

        strValue = StrHelperUtils.convertToStrUtf8(valueBytes);
        familyInfo.setFamilyDeclaration(strValue);

        //family tips
        curPos += 4;
        curPos += longValue;
        longValue = StrHelperUtils.getLongValue(rBytes, curPos, 4);
        try {
            valueBytes = getBytes(rBytes, curPos+4, (int)longValue);
        } catch (IOException e) {
//            e.printStackTrace();
            LogUtils.logE(e.getLocalizedMessage());
            return false;
        }

        strValue = StrHelperUtils.convertToStrUtf8(valueBytes);
        familyInfo.setFamilyTips(strValue);

        //family member
        curPos += 4;
        curPos += longValue;
        curPos += 172;
        longValue = StrHelperUtils.getLongValue(rBytes, curPos, 4);

        List<EntityFamilyInfo.FamilyMember> listMembers = new ArrayList<>();
        familyInfo.setmListMembers(listMembers);


        if (longValue >= 2000) {
            LogUtils.logI("检测到成员数量大于2000，存在错误");
            return false;
        }

        curPos += 4;

        EntityFamilyInfo.FamilyMember memberItem;
        for (int i=0, memberLen=(int)longValue, limit=2000; i<memberLen && limit > 0; i++, limit--) {
            memberItem = new EntityFamilyInfo.FamilyMember();
            listMembers.add(memberItem);

            longValue = StrHelperUtils.getLongValue(rBytes, curPos, 4);
            memberItem.strQQ = longValue + "";

            curPos += 4;
            longValue = StrHelperUtils.getLongValue(rBytes, curPos, 4);
            memberItem.rank = longValue;

            curPos += 4;
            //...
            curPos += 4;
            longValue = StrHelperUtils.getLongValue(rBytes, curPos, 4);
            memberItem.contributionValue = longValue;

            curPos += 4;
            longValue = StrHelperUtils.getLongValue(rBytes, curPos, 4);
            memberItem.fightingCapability = longValue;

            curPos += 4;
            longValue = StrHelperUtils.getLongValue(rBytes, curPos, 4);
            memberItem.timeStamp1 = longValue;

            curPos += 4;
            longValue = StrHelperUtils.getLongValue(rBytes, curPos, 4);
            memberItem.timeStamp2 = longValue;

            curPos += 4;
        }


//        LogUtils.logI("familyInfo=%s", familyInfo);

        try {
            FileUtils.writeLines(new File(dirFileName, "familyDescV1.txt"), familyInfo.getFamilyDescc());

        } catch (IOException e) {
//            e.printStackTrace();
            LogUtils.logE("写入文件familyDescV1出错");
        }

        try {
            FileUtils.writeLines(new File(dirFileName, "familyMemberDescV1.txt"), familyInfo.getFamilyMemberDesc());

        } catch (IOException e) {
//            e.printStackTrace();
            LogUtils.logE("写入文件familyDescV1出错");
            return false;
        }

        LogUtils.logI("抓取家族大厅信息保存完毕");

        return true;
    }


    private static boolean analysyContribution2(String dirFileName, String strQQ, String strCK) {
        LogUtils.logI("获取家族大厅信息中V2---QQ=%s, skey=%s", strQQ, strCK);

        String qqHex, ckHex;
        qqHex = StrHelperUtils.getQQHex(strQQ);
        ckHex = StrHelperUtils.getCkHex(strCK);


        String cmdJzGx = "CA 00 00 00 3A 00 00 00 1B 01 02 01 " + qqHex + " 00 00 00 0B " + ckHex + " 00 56 00 17 00 86 27 75 " + qqHex + " 00 00 00 00 00 04 00 00 00 01 00 00 " + qqHex;
//        byte[] execBytes = StrHelperUtils.hexStr2Bytes(cmdJzGx);
//        byte[] validateCmd = SocketHelper.getValidateCmd(qqHex, ckHex);

        Object[] validateClientSocket = SocketHelper.getValidateClientSocket(qqHex, ckHex);

        if (!SocketHelper.isVertifyCorrect(validateClientSocket)) {
            LogUtils.logE(SocketHelper.getConnErrorMsg(validateClientSocket));
            return false;
        }

//        byte[] rBytes = SocketHelper.requestCmd(execBytes, validateCmd);

        Socket socket;
        OutputStream os;
        InputStream is;
        byte[] rBytes;

        socket = (Socket) validateClientSocket[0];
        os = (OutputStream) validateClientSocket[1];
        is = (InputStream) validateClientSocket[2];

        rBytes = SocketHelper.writeCmd(os, is, cmdJzGx, null, 1);

        if (rBytes == null) {
            LogUtils.logI("\r\n接收数据失败！[275]");
            return false;
        }

//        StrHelperUtils.debugBytes("家族贡献", rBytes);


        EntityFamilyInfo familyInfo = new EntityFamilyInfo();
        int intValue;
        long longValue;
        String strValue;
        int len;
        int curPos;
        byte[] valueBytes;

        //get family name
        len = 4;
        curPos = 0;
        curPos += 64;
        longValue =  StrHelperUtils.getLongValue(rBytes, curPos, len);
        try {
            valueBytes = getBytes(rBytes, curPos + 4, (int)longValue);

        } catch (Exception e) {
//            e.printStackTrace();
            LogUtils.logE(e.getLocalizedMessage());
            return false;
        }

        strValue = StrHelperUtils.convertToStrUtf8(valueBytes);
        familyInfo.setFamilyName(strValue);

        //family account
        len = 4;
        curPos += 4;
        curPos += longValue;
        longValue = StrHelperUtils.getLongValue(rBytes, curPos, len);
        familyInfo.setFamilyAccount(longValue);

        // family declaration
        curPos += 4;
        longValue = StrHelperUtils.getLongValue(rBytes, curPos, 4);

        try {
            valueBytes = getBytes(rBytes, curPos + 4, (int)longValue);

        } catch (IOException e) {
//            e.printStackTrace();
            LogUtils.logE(e.getLocalizedMessage());
            return false;
        }

        strValue = StrHelperUtils.convertToStrUtf8(valueBytes);
        familyInfo.setFamilyDeclaration(strValue);

        //family tips
        curPos += 4;
        curPos += longValue;
        longValue = StrHelperUtils.getLongValue(rBytes, curPos, 4);
        try {
            valueBytes = getBytes(rBytes, curPos+4, (int)longValue);

        } catch (IOException e) {
//            e.printStackTrace();
            LogUtils.logE(e.getLocalizedMessage());
            return false;
        }

        strValue = StrHelperUtils.convertToStrUtf8(valueBytes);
        familyInfo.setFamilyTips(strValue);

        //family member
        curPos += 4;
        curPos += longValue;
        curPos += 172;
        longValue = StrHelperUtils.getLongValue(rBytes, curPos, 4);

        List<EntityFamilyInfo.FamilyMember> listMembers = new ArrayList<>();
        familyInfo.setmListMembers(listMembers);


        if (longValue >= 2000) {
            LogUtils.logE("检测到成员数量大于2000，存在错误");
            return false;
        }

        //检测是否要继续接包
        int memberCnt = (int)longValue;
        int restBytes = rBytes.length - (curPos + 4 - 1);
        int needBytes = 28 * memberCnt;
        List<byte[]> restBytesList = new ArrayList<>();

        for (int i=0; i<5; i++) {
            if (needBytes > restBytes) {
                //continue accpet data
                byte[] newBytes = SocketHelper.readBytesSaft(is, null, 1);
                if (newBytes == null) {
                    LogUtils.logE("接收数据失败![371]");
                    return false;
                }

                restBytes += newBytes.length;
                restBytesList.add(newBytes);

            } else {
                break;
            }
        }


        int rTotalSize = 0;
        int resultSize = 0;

        rTotalSize += rBytes.length;
        for (byte[] bItem : restBytesList) {
            rTotalSize += bItem.length;
        }

        byte[] newResultBytes = new byte[rTotalSize];

        try {
            System.arraycopy(rBytes, 0, newResultBytes, resultSize, rBytes.length);
            resultSize = rBytes.length;

            for (byte[] bItem : restBytesList) {
                System.arraycopy(bItem, 0, newResultBytes, resultSize, bItem.length);
                resultSize += bItem.length;
            }

        } catch (Exception e) {
            LogUtils.logE(e.getLocalizedMessage());
            return false;
        }

        SocketHelper.cloesScoke(socket, os, is);

        rBytes = newResultBytes;

        curPos += 4;

        EntityFamilyInfo.FamilyMember memberItem;
        for (int i=0, memberLen=(int)longValue, limit=2000; i<memberLen && limit > 0; i++, limit--) {
            memberItem = new EntityFamilyInfo.FamilyMember();
            listMembers.add(memberItem);

            longValue = StrHelperUtils.getLongValue(rBytes, curPos, 4);
            memberItem.strQQ = longValue + "";

            curPos += 4;
            longValue = StrHelperUtils.getLongValue(rBytes, curPos, 4);
            memberItem.rank = longValue;

            curPos += 4;
            //...
            curPos += 4;
            longValue = StrHelperUtils.getLongValue(rBytes, curPos, 4);
            memberItem.contributionValue = longValue;

            curPos += 4;
            longValue = StrHelperUtils.getLongValue(rBytes, curPos, 4);
            memberItem.fightingCapability = longValue;

            curPos += 4;
            longValue = StrHelperUtils.getLongValue(rBytes, curPos, 4);
            memberItem.timeStamp1 = longValue;

            curPos += 4;
            longValue = StrHelperUtils.getLongValue(rBytes, curPos, 4);
            memberItem.timeStamp2 = longValue;

            curPos += 4;
        }


//        LogUtils.logI("familyInfo=%s", familyInfo);

        try {
            FileUtils.writeLines(new File(dirFileName, "familyDescV2.txt"), familyInfo.getFamilyDescc());

        } catch (IOException e) {
//            e.printStackTrace();
            LogUtils.logE("写入文件familyDescV2.txt出错");
        }

        try {
            FileUtils.writeLines(new File(dirFileName, "familyMemberDescV2.txt"), familyInfo.getFamilyMemberDesc());

        } catch (IOException e) {
//            e.printStackTrace();
            LogUtils.logE("写入文件familyDescV2.txt出错");
            return false;
        }

        LogUtils.logI("抓取家族大厅信息保存完毕");

        return true;
    }




    private static boolean getNoticeAndEvents(String dirFile, String strQQ, String strCk) {
        LogUtils.logI("获取家族公告信息中---QQ=%s, skey=%s", strQQ, strCk);

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

            List<Long> otherList  = new ArrayList<>();
            for (int index=0, maxSize=10; index<maxSize; index++) {
                curPos += 4;
                longValue = StrHelperUtils.getLongValue(resultBytes, curPos, 4);
                otherList.add(longValue);

                if ( (longValue&0x0000000058000000) == 0x0000000058000000)  {
                    break;
                }
            }

            eventItem.otherInfos = otherList;

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

        LogUtils.logI("抓取家族公告信息保存完毕");
        return true;
    }




}
