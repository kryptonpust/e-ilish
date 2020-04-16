package pust.ice.eilish.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;



import java.util.List;

import pust.ice.eilish.Database.Tables.Chats;
import pust.ice.eilish.Database.Tables.DataTable;
import pust.ice.eilish.Database.Tables.InfoTable;
import pust.ice.eilish.Database.Tables.NotificationTable;
import pust.ice.eilish.api.Ainterface.Objects.Info;

@Dao
public interface DbDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DataTable dataTable);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Chats chat);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NotificationTable notificationTable);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(InfoTable infoTable);


    @Query("DELETE FROM data where id=:id")
    void deleteData(int id);


    @Query("DELETE FROM notification where id=:id")
    void deleteNotification(int id);

    @Query("DELETE FROM info where id=:id")
    void deleteinfo(int id);


    @Query("DELETE FROM chats")
    void deleteAllchats();

    @Query("SELECT * from info")
    LiveData<List<InfoTable>> getinfo();

    @Query("SELECT * from chats where pending=1")
    LiveData<List<Chats>> getPendingChats();

    @Query("SELECT * from chats")
    List<Chats> getAllchats();


    @Query("SELECT `id`,`updated_at` from data")
    List<Info> getlocaldata();

    @Query("SELECT * from data WHERE id=:id")
    DataTable getdata(int id);

    @Query("SELECT `id`,`updated_at` from notification")
    List<Info> getlocalnotifications();

    @Query("SELECT `id`,`updated_at` from info")
    List<Info> getlocalinfo();


    @Query("SELECT * from data")
    LiveData<List<DataTable>> getallTitles();

    @Query("SELECT html from data where id=:id ")
    LiveData<String> gethtml(int id);

}
