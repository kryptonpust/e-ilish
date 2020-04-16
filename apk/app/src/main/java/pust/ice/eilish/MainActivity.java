package pust.ice.eilish;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.makovsky.badgemenucreator.creator.BadgeMenu;

import java.util.List;
import java.util.concurrent.TimeUnit;

import pust.ice.eilish.Database.Tables.Chats;
import pust.ice.eilish.Database.Tables.InfoTable;
import pust.ice.eilish.api.Workers.BackgroundWorker;
import pust.ice.eilish.chat.ChatActivity;


public class MainActivity extends AppCompatActivity implements ServerViewModel.EventListener {

    private ServerViewModel server;
    private TextView textView;
    private Menu menu;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.warning);
        server = ServerViewModel.getinstance(getApplicationContext());
        sp = getApplicationContext().getSharedPreferences("store", Context.MODE_PRIVATE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        server.setOnEventListener(this);
        server.getLiveIngo().observe(this, new Observer<List<InfoTable>>() {
            @Override
            public void onChanged(List<InfoTable> datalist) {
               if(datalist.size()!=0)
               {
                   InfoTable infoTable=datalist.get(0);
                   if (infoTable.getEnable() == 1) {
                       textView.setText(infoTable.getInfotxt());
                       try{
                           textView.setTextColor(Color.parseColor(infoTable.getTxtcolor()));
                           textView.setBackgroundColor(Color.parseColor(infoTable.getBgcolor()));
                       }catch (IllegalArgumentException e)
                       {
                           Toast.makeText(MainActivity.this,"Invalid Info color code",Toast.LENGTH_SHORT).show();
                       }

                       textView.setVisibility(View.VISIBLE);
                       textView.setSelected(true);
                   } else {
                       if (textView.getVisibility() == View.VISIBLE)
                           textView.setVisibility(View.GONE);
                   }
               }
            }
        });

        PeriodicWorkRequest fetchtask=new PeriodicWorkRequest.Builder(BackgroundWorker.class,20,TimeUnit.MINUTES)
                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build();
        WorkManager.getInstance().enqueueUniquePeriodicWork("fetchtaks",ExistingPeriodicWorkPolicy.REPLACE,fetchtask);

    }

    @Override
    protected void onPause() {
        super.onPause();
        server.removeOnEventListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        this.menu=menu;
        if(sp.contains("unreadmsg"))
        {
            BadgeMenu.update(MainActivity.this, menu.findItem(R.id.realtime_chat), new BadgeMenu.Builder()
                    .menuIconDrawable(getResources().getDrawable(R.drawable.ic_forum_white_24dp))
                    .badgeCount(sp.getInt("unreadmsg",0)));
        }else {
            BadgeMenu.update(MainActivity.this, menu.findItem(R.id.realtime_chat), new BadgeMenu.Builder()
                    .menuIconDrawable(getResources().getDrawable(R.drawable.ic_forum_white_24dp))
                    .badgeCount(0));
        }

//        BadgeCounter.hide(menu.findItem(R.id.realtime_chat));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about) {
            builddialog();
        } else if (item.getItemId() == R.id.realtime_chat) {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            this.startActivity(intent);
        }
        return true;
    }

    private void builddialog() {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        WebView wv = new WebView(this);
        wv.loadUrl("file:///android_asset/about.html");
        ab.setView(wv);
        ab.create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }


    @Override
    public void chatConnStatus(String s) {

    }

    @Override
    public void onNewMsg(Chats c) {

    }

    @Override
    public void onUnreadmessage(final int i) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(menu!=null){
                    BadgeMenu.update(MainActivity.this, menu.findItem(R.id.realtime_chat), new BadgeMenu.Builder()
                            .menuIconDrawable(getResources().getDrawable(R.drawable.ic_forum_white_24dp))
                            .badgeCount(i));
                }
            }
        });

    }


}
