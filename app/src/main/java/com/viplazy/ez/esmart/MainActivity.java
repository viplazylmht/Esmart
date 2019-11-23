package com.viplazy.ez.esmart;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class MainActivity extends AppCompatActivity {

    private static final int PUSH_NOTIFY = Menu.FIRST;
    private static final int PUSH_POPUP = Menu.FIRST + 2;
    private static final int ABOUT = Menu.FIRST + 4;
    private static final int HO_TRO = Menu.FIRST + 6;
    private static final String CHANNEL_ID = "PUSH NOTIFICATION";

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;

    RelativeLayout container;

    private String id;
    Long days;

    private ViewPager pager;
    private TabLayout tabLayout;

    private Toolbar toolbar;

    /*//use for firebase database
    private DatabaseReference easyQuestionDB;
    private ArrayList<Questions> easyQuestions = new ArrayList<>();
    private DatabaseReference userDB;
    private User curUser;*/

    //use for storage
    //private FirebaseStorage storage;
    //private StorageReference storageRef;

    String email;

    private ImageView image;


    com.github.lzyzsd.circleprogress.ArcProgress bar1, bar2, bar3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        email = getIntent().getStringExtra("email");

        toolbar = findViewById(R.id.app_tool_bar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        image = findViewById(R.id.image);

        container = findViewById(R.id.container);

        createNotificationChannel();

        addControl();

        //ReadEasyQuest();

        //for storage
        //storage = FirebaseStorage.getInstance();
        //storageRef = storage.getReference("images/");

        FirebaseStorage a;

        ReadImage();

        Add(email);

        //UpdateUser();

        id = email;
        id = id.replace('.', ',');
    }

    //to test
    public void Add(String s){
        TextView title = toolbar.findViewById(R.id.user_name);

        title.setText(title.getText() + s);

    }

    public void ReadImage(){

        /*StorageReference islandRef = storageRef.child("19627.jpg");

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                image.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });*/

        //image.setImageResource(R.drawable.notification_icon);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
        }
        else {
            Intent serviceIntent = new Intent(this, PopupService.class);
            serviceIntent.putExtra("email", email);

            startService(serviceIntent);
        }


    }
    private Intent getPopupIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);

            return null;
        }
        else {
            return new Intent(this, PopupService.class);
        }
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

        Intent popupIntent = getPopupIntent();

        NotificationCompat.Builder mBuilder;

        if (popupIntent == null) {
            mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
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
        }
        else {
            mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle("Incoming notification")
                    .setContentText("This is a demo text that remind you to checkout ur achievements of last week")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("This is a demo text that remind you to checkout ur achievements of last week"))
                    .setPriority(NotificationCompat.PRIORITY_MAX)

                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .addAction(R.drawable.ic_launcher_foreground, "OPEN POPUP", PendingIntent.getActivity(this, 0, popupIntent, 0))
                    .setAutoCancel(true);
        }



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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            showSnackbar(getResources().getString(R.string.permission_granted), 1000);
        }
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
    }
    private void showSnackbar(String message, int duration)
    {
        // Create snackbar
        final Snackbar snackbar = Snackbar.make(container , message, duration);

        // Set an action on it, and a handler

        /*
        snackbar.setAction("DISMISS", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        */
        snackbar.show();
    }
    private void showSnackbarOpenAppSetting(String message, int duration)
    {
        // Create snackbar
        final Snackbar snackbar = Snackbar.make(container , message, duration);

        // Set an action on it, and a handler
        snackbar.setAction(getResources().getString(R.string.open_setting), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openSetting = new Intent();
                openSetting.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                openSetting.setData(uri);
                startActivity(openSetting);
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    private void addControl() {
        pager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        FragmentManager manager = getSupportFragmentManager();
        PagerAdapter adapter = new PagerAdapter(manager);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(adapter);//deprecated
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));

        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(getResources().getColor(R.color.divider_line_color));
            drawable.setSize(2, 1);
            ((LinearLayout) root).setDividerPadding(10);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }

    }
}
