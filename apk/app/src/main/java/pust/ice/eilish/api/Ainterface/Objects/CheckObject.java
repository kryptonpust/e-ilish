package pust.ice.eilish.api.Ainterface.Objects;

import java.util.List;

public class CheckObject{
    private List<Info> avail_data;
    private List<Info> avail_notifications;
    private List<Info> avail_info;

    public CheckObject(List<Info> avail_data, List<Info> avail_notifications, List<Info> avail_info) {
        this.avail_data = avail_data;
        this.avail_notifications = avail_notifications;
        this.avail_info = avail_info;
    }

    public List<Info> getAvail_data() {
        return avail_data;
    }

    public void setAvail_data(List<Info> avail_data) {
        this.avail_data = avail_data;
    }

    public List<Info> getAvail_notifications() {
        return avail_notifications;
    }

    public void setAvail_notifications(List<Info> avail_notifications) {
        this.avail_notifications = avail_notifications;
    }

    public List<Info> getAvail_info() {
        return avail_info;
    }

    public void setAvail_info(List<Info> avail_info) {
        this.avail_info = avail_info;
    }
}