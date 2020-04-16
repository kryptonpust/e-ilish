package pust.ice.eilish.Database.Tables;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notification")
public class NotificationTable {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name ="id")
    private int id;
    @NonNull
    @ColumnInfo(name ="date")
    private String date;
    @NonNull
    @ColumnInfo(name ="startTime")
    private String startTime;
    @ColumnInfo(name ="header")
    private String header;
    @ColumnInfo(name ="body")
    private String body;
    @NonNull
    @ColumnInfo(name = "updated_at")
    private String updated_at;

    public NotificationTable(int id, @NonNull String date, @NonNull String startTime, String header, String body, @NonNull String updated_at) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.header = header;
        this.body = body;
        this.updated_at = updated_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    @NonNull
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(@NonNull String startTime) {
        this.startTime = startTime;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @NonNull
    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(@NonNull String updated_at) {
        this.updated_at = updated_at;
    }
}
