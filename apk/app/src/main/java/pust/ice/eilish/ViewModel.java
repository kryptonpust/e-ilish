package pust.ice.eilish;

import android.app.Application;
import android.graphics.Color;
import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import pust.ice.eilish.Database.DBRepository;
import pust.ice.eilish.Database.Tables.DataTable;

public class ViewModel extends AndroidViewModel {
    private final DBRepository dbRepository;
    private final WebView wv;

    public ViewModel(@NonNull Application application) {
        super(application);
        dbRepository=new DBRepository(application);
        wv=new WebView(getApplication());
        //wv.setWebContentsDebuggingEnabled(true);
        wv.getSettings().setLoadWithOverviewMode(true);
//        wv.getSettings().setJavaScriptEnabled(true);


       // wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setBuiltInZoomControls(true);
        wv.getSettings().setDisplayZoomControls(false);
        wv.setBackgroundColor(Color.TRANSPARENT);
        wv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        wv.getSettings().setAllowFileAccess(true);
        wv.getSettings().setAllowContentAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            wv.getSettings().setAllowFileAccessFromFileURLs(true);
            wv.getSettings().setAllowUniversalAccessFromFileURLs(true);

        }
    }
    public LiveData<List<DataTable>> getallTitles()
    {
        return  dbRepository.getallTitles();
    }
    public LiveData<String> gethtml(int id)
    {
        return  dbRepository.gethtml(id);
    }

    public WebView getWv() {
        return wv;
    }

}
