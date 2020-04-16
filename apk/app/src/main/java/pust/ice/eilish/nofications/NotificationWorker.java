package pust.ice.eilish.nofications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import pust.ice.eilish.FlashActivity;
import pust.ice.eilish.R;


public class NotificationWorker extends Worker {
    private Context context;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, FlashActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_opeelish)
                .setContentTitle(getInputData().getString("header"))
                .setContentText(getInputData().getString("body"))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getInputData().getString("body")))
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setContentIntent(PendingIntent.getActivity(context,1001,new Intent(context,FlashActivity.class),PendingIntent.FLAG_UPDATE_CURRENT))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(getInputData().getInt("id", 0), builder.build());

        return Result.success();
    }
}
