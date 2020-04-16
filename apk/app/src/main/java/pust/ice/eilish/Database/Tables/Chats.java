package pust.ice.eilish.Database.Tables;

import org.joda.time.DateTime;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chats")
public class Chats {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @NonNull
    @ColumnInfo(name ="message")
    private String message;
    @NonNull
    @ColumnInfo(name ="msg_type")
    private String msg_type;
    @ColumnInfo(name ="origin")
    private int origin;
    @ColumnInfo(name ="pending")
    private int pending;
    @ColumnInfo(name ="time")
    private String time;

    public Chats(int id, @NonNull String message, @NonNull String msg_type, int origin, int pending, String time) {
        this.id = id;
        this.message = message;
        this.msg_type = msg_type;
        this.origin = origin;
        this.pending = pending;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getMessage() {
        return message;
    }

    public void setMessage(@NonNull String message) {
        this.message = message;
    }

    @NonNull
    public String getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(@NonNull String msg_type) {
        this.msg_type = msg_type;
    }

    public int getOrigin() {
        return origin;
    }

    public void setOrigin(int origin) {
        this.origin = origin;
    }

    public int getPending() {
        return pending;
    }

    public void setPending(int pending) {
        this.pending = pending;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
