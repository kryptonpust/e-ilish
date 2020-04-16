package pust.ice.eilish.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;

import pust.ice.eilish.Database.Tables.DataTable;
import pust.ice.eilish.R;
import pust.ice.eilish.ViewModel;


public class WebFragment extends Fragment {

    private WebView wv;
    private ViewModel mViewModel;
    private List<DataTable> dataTableList;
    private int ID;
    private ConstraintLayout cl;

    public WebFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ID=getArguments().getInt("id");
        View v=inflater.inflate(R.layout.frabment_web, container, false);
        cl=v.findViewById(R.id.web_parent);

//        wv=v.findViewById(R.id.webview);
//        //wv.setWebContentsDebuggingEnabled(true);
//        wv.getSettings().setLoadWithOverviewMode(true);
//        wv.getSettings().setUseWideViewPort(true);
//        wv.getSettings().setBuiltInZoomControls(true);
//        wv.getSettings().setDisplayZoomControls(false);
//        wv.getSettings();
//        wv.setBackgroundColor(Color.TRANSPARENT);
//        wv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        //wv.loadUrl("http://192.168.25.100:8080/test.html");
        //wv.loadUrl(path);
        return v;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ViewModel.class);
        wv=mViewModel.getWv();
        cl.addView(wv,0,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mViewModel.gethtml(ID).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                wv.loadDataWithBaseURL("",s,"text/html","UTF-8",null);
            }
        });
    }

    public boolean cangoback()
    {
        return wv.canGoBack();
    }
    public void goback()
    {
        wv.goBack();
    }
}
