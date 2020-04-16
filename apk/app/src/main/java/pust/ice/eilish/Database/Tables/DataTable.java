package pust.ice.eilish.Database.Tables;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "data")
public class DataTable {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;
    @NonNull
    @ColumnInfo(name = "title")
    private String title;
    @NonNull
    @ColumnInfo(name = "titlecolor")
    private String titlecolor;
    @NonNull
    @ColumnInfo(name = "html")
    private String html;
    @NonNull
    @ColumnInfo(name = "index")
    private int index;
    @NonNull
    @ColumnInfo(name = "updated_at")
    private String updated_at;

    public DataTable(int id, @NonNull String title, @NonNull String titlecolor, @NonNull String html, int index, @NonNull String updated_at) {
        this.id = id;
        this.title = title;
        this.titlecolor = titlecolor;
        this.html = html;
        this.index = index;
        this.updated_at = updated_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getTitlecolor() {
        return titlecolor;
    }

    public void setTitlecolor(@NonNull String titlecolor) {
        this.titlecolor = titlecolor;
    }

    @NonNull
    public String getHtml() {
        return html;
    }

    public void setHtml(@NonNull String html) {
        this.html = html;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @NonNull
    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(@NonNull String updated_at) {
        this.updated_at = updated_at;
    }
}
