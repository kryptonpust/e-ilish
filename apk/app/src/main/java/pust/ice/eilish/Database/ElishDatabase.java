package pust.ice.eilish.Database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import pust.ice.eilish.Database.Tables.Chats;
import pust.ice.eilish.Database.Tables.DataTable;
import pust.ice.eilish.Database.Tables.InfoTable;
import pust.ice.eilish.Database.Tables.NotificationTable;

@Database(entities = {DataTable.class, Chats.class, NotificationTable.class, InfoTable.class},version = 1,exportSchema = false)
public abstract class ElishDatabase extends RoomDatabase {
    public abstract DbDao databaseDao();
    private static ElishDatabase INSTANCE;

     public static ElishDatabase getINSTANCE(final Context context) {
        if(INSTANCE==null)
        {
            synchronized (ElishDatabase.class)
            {
                if(INSTANCE==null)
                {

                    INSTANCE=Room.databaseBuilder(context.getApplicationContext(), ElishDatabase.class,"elisa").build();
                }
            }
        }
        return INSTANCE;
    }
}
