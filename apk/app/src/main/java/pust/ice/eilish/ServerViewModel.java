package pust.ice.eilish;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;
import androidx.lifecycle.LiveData;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import pust.ice.eilish.Database.DbDao;
import pust.ice.eilish.Database.ElishDatabase;
import pust.ice.eilish.Database.Tables.Chats;
import pust.ice.eilish.Database.Tables.InfoTable;
import pust.ice.eilish.api.Workers.BackgroundWorker;
import pust.ice.eilish.chat.ChatActivity;


public class ServerViewModel {
    private static ServerViewModel INSTANCE;

    public static Integer ORIGIN_ANDROID = 1;
    //    private static Set<Context> registered;
    private final Gson gson;
    private static String LINK="http://e-ilish.org:3000";
//    public static String LINK = "http://103.108.140.72:3000";
//    public static String LINK = "http://message.thebrosltd.tk";

    private List<EventListener> listeners;
    private static DbDao dbDao;
    private Context context;
    private Socket mSocket;
    private SharedPreferences sp;
    public static final String KEY_TEXT_REPLY = "key_text_reply";
    public static int NOTIFICATION_ID = 100;

    private int UNREADMSG=0;
    public static ServerViewModel getinstance(Context application) {
        if (INSTANCE == null) {
            synchronized (ServerViewModel.class) {
                if (INSTANCE == null) {
            INSTANCE = new ServerViewModel(application);
                }
            }
        }
        return INSTANCE;
    }


    private final NotificationCompat.Builder notification;
    private NotificationCompat.MessagingStyle notistyle;

    public ServerViewModel(@NonNull final Context context) {
        listeners = new ArrayList<>();
        this.context = context;
        dbDao = ElishDatabase.getINSTANCE(context).databaseDao();
        gson = new Gson();
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel("Message to Admin")
                .build();
        final NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_send_24dp,
                "Reply", replyPendingIntent())
                .addRemoteInput(remoteInput)
                .setAllowGeneratedReplies(true)
                .build();

        notistyle = new NotificationCompat.MessagingStyle("Me");

        notification = new NotificationCompat.Builder(context, FlashActivity.CHANNEL_ID)
                .addAction(action)
                .setSmallIcon(R.drawable.ic_opeelish)
                .setContentTitle("EILISH Messenger")
                .setColor(Color.RED)
                .setContentIntent(PendingIntent.getActivity(context, 101, new Intent(context, ChatActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setAutoCancel(true)
//                .setOnlyAlertOnce(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setStyle(notistyle);

        sp = context.getSharedPreferences("store", Context.MODE_PRIVATE);
        UNREADMSG=sp.getInt("unreadmsg",0);
        final IO.Options options = new IO.Options();
        options.forceNew = true;
        if (!sp.contains("API")) {
            try {
                mSocket = IO.socket(LINK, options);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            mSocket.connect();
            mSocket.emit("register", new Ack() {
                @Override
                public void call(Object... args) {
                    System.out.println(args.length);
                    sp.edit().putString("API", args[0].toString()).apply();
                    sp.edit().putString("CHATTIME", args[1].toString()).apply();
                    try {
                        mSocket.close();
                        mSocket = IO.socket(LINK + "/android?token=" + args[0].toString(), options);
                        setUpListeners();
                        mSocket.connect();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            try {
                mSocket = IO.socket(LINK + "/android?token=" + sp.getString("API", "abc"), options);
                setUpListeners();
                mSocket.connect();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

    }

    private void setUpListeners() {
        mSocket.on("admin", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Chats data = gson.fromJson((String) args[0], Chats.class);
                sp.edit().putString("CHATTIME", data.getTime()).apply();
                new ChatInsert().execute(data);
                generateNotification(data, "Admin");
                UNREADMSG++;
                sp.edit().putInt("unreadmsg",UNREADMSG).apply();
                for(EventListener listener:listeners)
                {
                    if(listener instanceof MainActivity)
                    {
                        listener.onUnreadmessage(UNREADMSG);
                    }
                }
            }
        });
        mSocket.on("update", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                PeriodicWorkRequest fetchtask=new PeriodicWorkRequest.Builder(BackgroundWorker.class,20, TimeUnit.MINUTES)
                        .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                        .build();
                WorkManager.getInstance().enqueueUniquePeriodicWork("fetchtaks", ExistingPeriodicWorkPolicy.REPLACE,fetchtask);
            }
        });
        mSocket.on("error", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args[0].toString().equals("auth_error")) {
                    clearappdata();
                }
            }
        });

        mSocket.on("delete", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                clearappdata();
            }
        });
        mSocket.on("connect", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                if (sp.contains("CHATTIME")) {
                    mSocket.emit("timechat", gson.toJson(sp.getString("CHATTIME", "00")), new Ack() {
                        class TempChat {
                            List<Chats> dataPackets;

                            public TempChat(List<Chats> dataPackets) {
                                this.dataPackets = dataPackets;
                            }

                            public List<Chats> getDataPackets() {
                                return dataPackets;
                            }

                            public void setDataPackets(List<Chats> dataPackets) {
                                this.dataPackets = dataPackets;
                            }
                        }
                        @Override
                        public void call(Object... args) {

                            TempChat tempChat = gson.fromJson(args[0].toString(), TempChat.class);
                            final List<Chats> data = tempChat.getDataPackets();

                            UNREADMSG=UNREADMSG+data.size();
                            sp.edit().putInt("unreadmsg",UNREADMSG).apply();
                            for(EventListener listener:listeners)
                            {
                                if(listener instanceof MainActivity)
                                {
                                    listener.onUnreadmessage(UNREADMSG);
                                }
                            }
                            new ChatInsert().execute(new Runnable() {
                                @Override
                                public void run() {
                                    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                                    DateTime MAX = DateTime.parse(sp.getString("CHATTIME", "00"), formatter), temp;
                                    for (Chats t : data) {
                                        temp = DateTime.parse(t.getTime(), formatter);
                                        if (MAX.isBefore(temp)) {
                                            MAX = temp;
                                        }
                                        dbDao.insert(t);
                                        generateNotification(t, "Admin");
                                    }
                                    sp.edit().putString("CHATTIME", formatter.print(MAX)).apply();


                                }
                            });
                        }
                    });
                }
                for (EventListener listener : listeners) {
                    listener.chatConnStatus("Connected");
                }
            }
        });
        mSocket.on("reconnect_attempt", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                for (EventListener listener : listeners) {
                    listener.chatConnStatus("reconnecting..");
                }
            }
        });
        mSocket.on("connect_error", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //System.out.println("Connection error");
                for (EventListener listener : listeners) {
                    listener.chatConnStatus("Connection error");
                }
            }
        });
    }

    private void clearappdata() {
        sp.edit().clear().apply();
        dbDao.deleteAllchats();
        Intent restart = new Intent(context, MainActivity.class);
        restart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(restart);
        INSTANCE = null;
    }

    private PendingIntent replyPendingIntent() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // start a
            // (i)  broadcast receiver which runs on the UI thread or
            // (ii) service for a background task to b executed , but for the purpose of
            // this codelab, will be doing a broadcast receiver
            intent = new Intent(context, NotificationReceiver.class);
//                                intent.setAction(REPLY_ACTION);
//                                intent.putExtra(KEY_NOTIFICATION_ID, notificationId);
//                                intent.putExtra(KEY_MESSAGE_ID, messageId);
            return PendingIntent.getBroadcast(context, 100, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            // start your activity for Android M and below
            intent = new Intent(context, ChatActivity.class);
//                                intent.setAction(REPLY_ACTION);
//                                intent.putExtra(KEY_MESSAGE_ID, messageId);
//                                intent.putExtra(KEY_NOTIFICATION_ID, notifyId);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return PendingIntent.getActivity(context, 101, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    public List<Chats> getAllChats() {
        return dbDao.getAllchats();
    }


    public void send(final Chats chat) {
        String json = gson.toJson(chat);
        if (mSocket.connected()) {
            mSocket.emit("message", json, new Ack() {
                @Override
                public void call(Object... args) {
                    Chats c = gson.fromJson(args[0].toString(), Chats.class);
                    sp.edit().putString("CHATTIME", c.getTime()).apply();
                    new ChatInsert().execute(c);
                    generateNotification(c, "Me");
                    UNREADMSG=0;
                    sp.edit().putInt("unreadmsg",UNREADMSG).apply();
                    for(EventListener listener:listeners)
                    {
                        if(listener instanceof MainActivity)
                        {
                            listener.onUnreadmessage(UNREADMSG);
                        }
                    }
                }
            });
        } else {
            new Handler(context.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Sorry, Message sent failed!!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void generateNotification(Chats c, String s) {
        if (listeners.size() == 0) {
            if (c.getMsg_type().equals("text")) {
                notistyle.addMessage(c.getMessage(), System.currentTimeMillis(), s);
            } else {
                notistyle.addMessage("New Image Received\nClick on notification to view", System.currentTimeMillis(), s);
            }
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(NOTIFICATION_ID, notification.build());
        }

        for (EventListener listener : listeners) {
            if (!(listener instanceof ChatActivity)) {
                if (c.getMsg_type().equals("text")) {
                    notistyle.addMessage(c.getMessage(), System.currentTimeMillis(), s);
                } else {
                    notistyle.addMessage("New Image Received\nClick on notification to view", System.currentTimeMillis(), s);
                }
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(NOTIFICATION_ID, notification.build());
            }
            listener.onNewMsg(c);
        }
    }

    public void sendImage(Chats chat, final Uri data) {
        String json = gson.toJson(chat);
        mSocket.emit("imagemsg", json, new Ack() {
            @Override
            public void call(Object... args) {
                Chats c = gson.fromJson(args[0].toString(), Chats.class);
                if(data!=null)
                {
                    c.setMessage(data.toString());

                }
                sp.edit().putString("CHATTIME", c.getTime()).apply();
                new ChatInsert().execute(c);
                for (EventListener listener : listeners) {
                    listener.onNewMsg(c);
                }
            }
        });

    }


    public void setOnEventListener(EventListener eventListener) {
        listeners.add(eventListener);
    }

    public void removeOnEventListener(EventListener eventListener) {
        if (listeners.contains(eventListener)) {
            listeners.remove(eventListener);
//            if (listeners.size()==0)
//            {
//                INSTANCE=null;
//            }
        }
    }

    public void resetNotificationData() {
        notistyle=new NotificationCompat.MessagingStyle("Me");
        notification.setStyle(notistyle);
        UNREADMSG=0;
        sp.edit().putInt("unreadmsg",UNREADMSG).apply();
        for(EventListener listener:listeners)
        {
            if(listener instanceof MainActivity)
            {
                listener.onUnreadmessage(UNREADMSG);
            }
        }
    }

    public LiveData<List<InfoTable>> getLiveIngo() {
        return dbDao.getinfo();
    }


    public interface EventListener {

        void chatConnStatus(String s);

        void onNewMsg(Chats c);
        void onUnreadmessage(int i);
    }


    private static class ChatInsert extends AsyncTask<Chats, Void, Chats> {

        @Override
        protected Chats doInBackground(Chats... chats) {
            dbDao.insert(chats[0]);
            return chats[0];
        }

        @Override
        protected void onPostExecute(Chats chat) {
        }
    }

}
