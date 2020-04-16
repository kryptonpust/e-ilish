package pust.ice.eilish.Database.Tables;

import org.joda.time.DateTime;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "info")
public class InfoTable {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name ="id")
    private int id;
    @NonNull
    @ColumnInfo(name ="infotxt")
    private String infotxt;
    @NonNull
    @ColumnInfo(name ="txtcolor")
    private String txtcolor;
    @NonNull
    @ColumnInfo(name ="bgcolor")
    private String bgcolor;
    @NonNull
    @ColumnInfo(name ="enable")
    private int enable;
    @NonNull
    @ColumnInfo(name = "updated_at")
    private String updated_at;

    public InfoTable(int id, @NonNull String infotxt, @NonNull String txtcolor, @NonNull String bgcolor, int enable, @NonNull String updated_at) {
        this.id = id;
        this.infotxt = infotxt;
        this.txtcolor = txtcolor;
        this.bgcolor = bgcolor;
        this.enable = enable;
        this.updated_at = updated_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getInfotxt() {
        return infotxt;
    }

    public void setInfotxt(@NonNull String infotxt) {
        this.infotxt = infotxt;
    }

    @NonNull
    public String getTxtcolor() {
        return txtcolor;
    }

    public void setTxtcolor(@NonNull String txtcolor) {
        this.txtcolor = txtcolor;
    }

    @NonNull
    public String getBgcolor() {
        return bgcolor;
    }

    public void setBgcolor(@NonNull String bgcolor) {
        this.bgcolor = bgcolor;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    @NonNull
    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(@NonNull String updated_at) {
        this.updated_at = updated_at;
    }
}
