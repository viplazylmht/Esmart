package com.viplazy.ez.esmart;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class MainActivity extends AppCompatActivity {

    private static final int PUSH_NOTIFY = Menu.FIRST;
    private static final int PUSH_POPUP = Menu.FIRST + 2;
    private static final int ABOUT = Menu.FIRST + 4;
    private static final int HO_TRO = Menu.FIRST + 6;
    private static final String CHANNEL_ID = "PUSH NOTIFICATION";

    com.github.lzyzsd.circleprogress.ArcProgress bar1, bar2, bar3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();




    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, PUSH_NOTIFY, 0,"Push notification").setIcon(android.R.drawable.ic_notification_overlay);
        menu.add(0, PUSH_POPUP, 0,"Pus popup").setIcon(android.R.drawable.ic_popup_reminder);
        menu.add(0, ABOUT, 0,"Info App").setIcon(android.R.drawable.ic_notification_clear_all);
        menu.add(0, HO_TRO, 0,"Support").setIcon(android.R.drawable.ic_dialog_info);
        return true;
    }
    //Xử lý sự kiện khi các option trong Option Menu được lựa chọn
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case PUSH_NOTIFY: {
                pushNotify();
                break;
            }
            case ABOUT: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("EZ TEAM");
                builder.setMessage("AUTHOR: " + "\n" + "This app is a test version for Education Project\n" + "\n");
                builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.show();
                break;
            }
            case PUSH_POPUP: {
                pushPopup();
                break;
            }
            case HO_TRO: {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/viplazylmht/Esmart"));
                startActivity(i);
                break;
            }
        }
        return true;
    }

    private void pushPopup() {
    }

    private void pushNotify() {

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Intent snoozeIntent = new Intent(this, MainActivity.class);
        snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, PUSH_NOTIFY);
        PendingIntent snoozePendingIntent =
                PendingIntent.getActivity(this, 0, snoozeIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Incoming notification")
                .setContentText("This is a demo text that remind you to checkout ur achievements of last week")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("This is a demo text that remind you to checkout ur achievements of last week"))
                .setPriority(NotificationCompat.PRIORITY_MAX)

                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_launcher_foreground, "OPEN APP", snoozePendingIntent)
                .setAutoCancel(true);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(PUSH_NOTIFY, mBuilder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
