package pust.ice.eilish;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.RemoteInput;

import pust.ice.eilish.Database.Tables.Chats;

public class NotificationReceiver extends BroadcastReceiver {

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(ServerViewModel.KEY_TEXT_REPLY);
        }
        return null;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String msg= (String) getMessageText(intent);
        if(msg!=null)
        {
            ServerViewModel server=ServerViewModel.getinstance(context);
            Chats c=new Chats(0,msg,"text",1,1,"00");
            server.send(c);
        }
    }
}
