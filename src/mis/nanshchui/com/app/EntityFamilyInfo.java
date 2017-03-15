package mis.nanshchui.com.app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by baoxing on 2017/3/14.
 */
public class EntityFamilyInfo {

    public static class FamilyMember {
        public String strQQ = "";
        public long rank;
        public long contributionValue;
        public long fightingCapability;
        public long timeStamp1;
        public long timeStamp2;

        @Override
        public String toString() {
            return "FamilyMember{" +
                    "strQQ='" + strQQ + '\'' +
                    ", rank=" + rank +
                    ", contributionValue=" + contributionValue +
                    ", fightingCapability=" + fightingCapability +
                    ", timeStamp1=" + timeStamp1 +
                    ", timeStamp2=" + timeStamp2 +
                    '}';
        }
    }



    private String familyName = "";
    private long familyAccount;
    private String familyDeclaration = "";
    private String familyTips = "";

    private List<FamilyMember> mListMembers;


    public List<String> getFamilyDescc() {
        List<String> lines = new ArrayList<>();
        lines.add("家族名称：" + this.familyName);
        lines.add("成员数量：" + this.familyAccount);
        lines.add("家族描述：" + this.familyDeclaration);
        lines.add("提示：" + familyTips);

        return lines;
    }

    public List<String> getFamilyMemberDesc() {
        List<String> lines = new ArrayList<>();
        if (this.mListMembers != null) {
            StringBuilder strBuilder;
            SimpleDateFormat sdf = new SimpleDateFormat("(yyyy-MM-dd HH:mm:ss)");
            for (FamilyMember memberItem : mListMembers) {
                strBuilder = new StringBuilder("");

                strBuilder.append(memberItem.strQQ);
                strBuilder.append(";");
                strBuilder.append(memberItem.rank);
                strBuilder.append(";");
                strBuilder.append(memberItem.contributionValue);
                strBuilder.append(";");
                strBuilder.append(memberItem.fightingCapability);
                strBuilder.append(";");
                strBuilder.append(memberItem.timeStamp1);
                strBuilder.append(sdf.format(new Date(memberItem.timeStamp1 * 1000)));
                strBuilder.append(";");
                strBuilder.append(memberItem.timeStamp2);
                strBuilder.append(sdf.format(new Date(memberItem.timeStamp2 * 1000)));

                lines.add(strBuilder.toString());
            }
        }

        return lines;
    }



    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public long getFamilyAccount() {
        return familyAccount;
    }

    public void setFamilyAccount(long familyAccount) {
        this.familyAccount = familyAccount;
    }


    public String getFamilyDeclaration() {
        return familyDeclaration;
    }

    public void setFamilyDeclaration(String familyDeclaration) {
        this.familyDeclaration = familyDeclaration;
    }

    public String getFamilyTips() {
        return familyTips;
    }

    public void setFamilyTips(String familyTips) {
        this.familyTips = familyTips;
    }


    public List<FamilyMember> getmListMembers() {
        return mListMembers;
    }

    public void setmListMembers(List<FamilyMember> mListMembers) {
        this.mListMembers = mListMembers;
    }


    @Override
    public String toString() {
        return "EntityFamilyInfo{" +
                "familyName='" + familyName + '\'' +
                ", familyAccount=" + familyAccount +
                ", familyDeclaration='" + familyDeclaration + '\'' +
                ", familyTips='" + familyTips + '\'' +
                ", mListMembers=" + mListMembers +
                '}';
    }
}
