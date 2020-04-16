package pust.ice.eilish.api.Ainterface.Objects;

public class Info{
    private int id;
    private String updated_at;

    public Info(int id, String updated_at) {
        this.id = id;
        this.updated_at = updated_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}