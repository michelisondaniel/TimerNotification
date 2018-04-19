package net.michelison.timernotification;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private ProgressDialog progressDialog;
    private MessageHandler messageHandler;
    private Button startButton;

    public static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set widgets
        startButton = (Button) findViewById(R.id.startButton);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Counting");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(true);

        messageHandler = new MessageHandler();

    }

    public void startCounter(View view) {
        progressDialog.show();

        Thread thread = new Thread(new Timer());
        thread.start();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void doNotify() {

        // imported from notificationExample.java
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // create the channel
        String channelIdOne = "net.michelison.timernotification";
        CharSequence nameOne = "channelOne";
        String descriptionOne = "Because we have to have one";

        int importance = NotificationManager.IMPORTANCE_HIGH;

        // constructing the channel
        NotificationChannel channelOne = new NotificationChannel(channelIdOne, nameOne, importance);
        // these all must be set regardless of being used
        channelOne.setDescription(descriptionOne);
        channelOne.enableLights(true);
        channelOne.setLightColor(Color.GREEN);
        channelOne.enableVibration(false);
        notificationManager.createNotificationChannel(channelOne);

        // end of import notificationExample.java

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        PendingIntent intent = PendingIntent.getActivity(this, NOTIFICATION_ID, new Intent(this, BoomActivity.class), 0);

        //building the notificiation
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelIdOne);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(intent);
        builder.setAutoCancel(true);
        builder.setContentTitle("Knock, knock.");
        builder.setContentText("Delivery at the door.");
        builder.setSubText("Take a peek at the package...");

        //display the notification in the notification drawer
        notificationManager.notify(NOTIFICATION_ID, builder.build());


    }

    private class Timer implements Runnable {

        @Override
        public void run() {
            for (int i = 5; i >= 0; i--) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }

                Bundle bundle = new Bundle();
                bundle.putInt("current count", i);

                Message message = new Message();
                message.setData(bundle);

                messageHandler.sendMessage(message);
            }
            progressDialog.dismiss();
        }
    }

    private class MessageHandler extends Handler {

        @Override
        public void handleMessage(Message message) {
            int currentCount = message.getData().getInt("current count");
            progressDialog.setMessage("Please wait in... " + currentCount);

            if (currentCount == 0) {
                doNotify();
            }
        }
    }

}
