package mis.nanshchui.com.app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by baoxing on 2017/3/15.
 */
public class EntitiyFamilyNoticeEvent {

    public static class FamilyEvent {
        public String strQQ;
        public long info1;
        public long infoType;
        public List<Long> otherInfos = new ArrayList<>();

    }

    private String notice = "";
    private List<FamilyEvent> listNoticeEvents;

    public List<String> getEventsDesc() {
        List<String> lines = new ArrayList<>();

        if (listNoticeEvents != null) {
            StringBuilder sBuilder;
            SimpleDateFormat sdf = new SimpleDateFormat("(yyyy-MM-dd HH:mm:ss)");
            for (FamilyEvent item : listNoticeEvents) {
                sBuilder = new StringBuilder("");

                sBuilder.append(item.strQQ);
                sBuilder.append(";");
                sBuilder.append(item.info1);
                sBuilder.append(";");
                sBuilder.append(item.infoType);

                Long longValue;
                for (int i=0, len=item.otherInfos.size(); i<len; i++) {
                    longValue = item.otherInfos.get(i);
                    sBuilder.append(";");
                    sBuilder.append(longValue);
                    if (i==len-1) {
                        sBuilder.append(sdf.format(new Date(longValue*1000)));
                    }
                }

                lines.add(sBuilder.toString());
            }

        }

        return lines;
    }


    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public List<FamilyEvent> getListNoticeEvents() {
        return listNoticeEvents;
    }

    public void setListNoticeEvents(List<FamilyEvent> listNoticeEvents) {
        this.listNoticeEvents = listNoticeEvents;
    }

    @Override
    public String toString() {
        return "EntitiyFamilyNoticeEvent{" +
                "notice='" + notice + '\'' +
                ", listNoticeEvents=" + listNoticeEvents +
                '}';
    }
}
