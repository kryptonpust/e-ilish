package pust.ice.eilish.Database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import pust.ice.eilish.Database.Tables.DataTable;


public class DBRepository {


    private DbDao databaseDao;

    public DBRepository(Application application) {
        ElishDatabase db = ElishDatabase.getINSTANCE(application);
        databaseDao = db.databaseDao();

    }


    public LiveData<List<DataTable>> getallTitles() {

        return databaseDao.getallTitles();
    }

    public LiveData<String> gethtml(int id) {

        return databaseDao.gethtml(id);
    }


}
