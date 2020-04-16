package pust.ice.eilish.api.Ainterface.Objects;

import java.util.List;

import pust.ice.eilish.Database.Tables.DataTable;
import pust.ice.eilish.Database.Tables.InfoTable;
import pust.ice.eilish.Database.Tables.NotificationTable;


public class FetchObject {

    List<DataTable> data;
    List<NotificationTable> notifications;
    List<InfoTable> info;

    public FetchObject(List<DataTable> data, List<NotificationTable> notifications, List<InfoTable> info) {
        this.data = data;
        this.notifications = notifications;
        this.info = info;
    }

    public List<DataTable> getData() {
        return data;
    }

    public void setData(List<DataTable> data) {
        this.data = data;
    }

    public List<NotificationTable> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationTable> notifications) {
        this.notifications = notifications;
    }

    public List<InfoTable> getInfo() {
        return info;
    }

    public void setInfo(List<InfoTable> info) {
        this.info = info;
    }
}


