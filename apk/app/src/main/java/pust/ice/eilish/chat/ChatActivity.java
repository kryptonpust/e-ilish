package pust.ice.eilish.chat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.commons.models.MessageContentType;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pust.ice.eilish.Database.Tables.Chats;
import pust.ice.eilish.R;
import pust.ice.eilish.ServerViewModel;


public class ChatActivity extends AppCompatActivity implements ServerViewModel.EventListener {

    private static final int PICK_PHOTO_FOR_AVATAR = 420;
    private ServerViewModel server;
    private RecyclerView recyclerView;
    private TextView status;
    private MessagesList messagesList;
    private MessagesListAdapter<Message> adapter;
    private MessageInput inputView;


    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(ServerViewModel.KEY_TEXT_REPLY);
        }
        return null;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        server=ServerViewModel.getinstance(getApplicationContext());
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.cancel(ServerViewModel.NOTIFICATION_ID);


        status=findViewById(R.id.status);
        messagesList=findViewById(R.id.messagesList);
        inputView=findViewById(R.id.input);
//        Picasso.get().setLoggingEnabled(true);
        ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                    Picasso.get().load(url).resize(600, 700).into(imageView);

            }
        };
        adapter = new MessagesListAdapter<>("1", imageLoader);
        messagesList.setAdapter(adapter);

        inputView.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                Chats c=new Chats(0,input.toString(),"text",1,1,"00");
                server.send(c);
                return true;
            }
        });
        inputView.setAttachmentsListener(new MessageInput.AttachmentsListener() {
            @Override
            public void onAddAttachments() {
                pickImage();
            }
        });
        adapter.setOnMessageClickListener(new MessagesListAdapter.OnMessageClickListener<Message>() {
            @Override
            public void onMessageClick(final Message message) {
                if(message.getImageUrl()!=null)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    final AlertDialog dialog = builder.create();
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogLayout = inflater.inflate(R.layout.details_image_view, null);
                    dialog.setView(dialogLayout);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface d) {
                            ImageView image = (ImageView) dialog.findViewById(R.id.detail_image);
                            Picasso.get().load(message.getImageUrl()).into(image);
                        }
                    });
                    dialog.show();
                }
            }
        });

    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            final int takeFlags = data.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            try {

                InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());

                Bitmap bm = BitmapFactory.decodeStream(inputStream);
                if(bm!=null){
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG,75,baos);
                    byte[] b = baos.toByteArray();
                    String encImage = "data:image/jpeg;base64,"+Base64.encodeToString(b, Base64.DEFAULT);
                    server.sendImage(new Chats(0,encImage,"image",1,1,"0"),data.getData());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        getContentResolver().takePersistableUriPermission(data.getData(),takeFlags);
                    }
                }else {
                    server.sendImage(new Chats(0,"data:image/xml+svg;base64,","image",1,1,"0"),null);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        server.setOnEventListener(this);
        new LoadChat().execute();
    }

    class LoadChat extends AsyncTask<Void, Void, List<Message>> {
        private List<Message> messages;
        @Override
        protected List<Message> doInBackground(Void... voids) {
            messages=new ArrayList<>();
            for (Chats c:server.getAllChats())
            {
                messages.add(new Message(c));
            }
            return messages;
        }

        @Override
        protected void onPostExecute(List<Message> chats) {
            adapter.addToEnd(chats,true);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        adapter.clear();
        server.resetNotificationData();
        server.removeOnEventListener(this);
    }


    @Override
    public void chatConnStatus(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(View.VISIBLE!=status.getVisibility())
                {
                    status.setVisibility(View.VISIBLE);
                }
                if (s.equals("Connected"))
                {
                    status.setText(s);
                    status.setBackgroundColor(Color.rgb(62, 201, 71));
                    new Handler(getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            status.setVisibility(View.GONE);
                        }
                    },2000);

                }else
                {
                    status.setText(s);
                    status.setBackgroundColor(Color.rgb(229, 20, 86));
                }
            }
        });

    }

    @Override
    public void onNewMsg(final Chats c) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.addToStart(new Message(c),true);
            }
        });

    }

    @Override
    public void onUnreadmessage(int i) {

    }

    class Message implements IMessage, MessageContentType.Image {

        private String id;
        private String msg;
        private User user;
        private Date date;
        private int image=0;
        public Message(Chats c) {
            id=String.valueOf(c.getId());
            msg=c.getMessage();
            user=new User(c.getOrigin());
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            DateTime dateTime=DateTime.parse(c.getTime(),formatter);
            date=dateTime.toDate();
            if(c.getMsg_type().equals("image"))
            {
                image=1;
            }
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getText() {
            if (image==0)
                return msg;
            return null;
        }

        @Override
        public IUser getUser() {
            return user;
        }

        @Override
        public Date getCreatedAt() {
            return date;
        }

        @Nullable
        @Override
        public String getImageUrl() {
            if(image==1)
                return msg;
            return null;
        }
    }
    class User implements IUser{

        private String id;
        public User(int origin) {
            id=String.valueOf(origin);
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getAvatar() {
            return null;
        }
    }
}
