package com.viplazy.ez.esmart;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.lang.annotation.Inherited;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class MainActivity extends AppCompatActivity {

    private static final int PUSH_NOTIFY = Menu.FIRST;
    private static final int PUSH_POPUP = Menu.FIRST + 2;
    private static final int ABOUT = Menu.FIRST + 4;
    private static final int HO_TRO = Menu.FIRST + 6;
    private static final String CHANNEL_ID = "PUSH NOTIFICATION";

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;

    RelativeLayout container;

    private ViewPager pager;
    private TabLayout tabLayout;

    private Toolbar toolbar;

    //use for sigin with google
    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    /*//use for firebase database
    private DatabaseReference easyQuestionDB;
    private ArrayList<Questions> easyQuestions = new ArrayList<>();
    private DatabaseReference userDB;
    private User curUser;*/

    //use for storage
    //private FirebaseStorage storage;
    //private StorageReference storageRef;

    DatabaseRawData databaseRawData;

    private ImageView image;


    com.github.lzyzsd.circleprogress.ArcProgress bar1, bar2, bar3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.app_tool_bar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        image = findViewById(R.id.image);

        container = findViewById(R.id.container);

        databaseRawData = new DatabaseRawData();

        createNotificationChannel();

        addControl();

        //for google sign in
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        signIn();

        //for get database
        //databaseRawData.setEasyQuestionDB(new DatabaseReference(FirebaseDatabase.getInstance().getReference("Question/Medium")));
        databaseRawData.getEasyQuestionDB().keepSynced(true);

        //databaseRawData.setUserDB(FirebaseDatabase.getInstance().getReference("User"));
        databaseRawData.getUserDB().keepSynced(true);

        ReadEasyQuest();

        ReadUser();

        //for storage
        //storage = FirebaseStorage.getInstance();
        //storageRef = storage.getReference("images/");

        FirebaseStorage a;

        ReadImage();

        ShowLognInResult();

    }

    public void ReadUser(){
        //String id = userDB.push().getKey();
        if (mAuth!= null && mAuth.getCurrentUser() != null)
        {
        String id = mAuth.getCurrentUser().getEmail();
        id = id.replace('.', ',');

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        ArrayList<String> ids = new ArrayList<String>();
        ids.add("11");
        ids.add("45");
        User a = new User(12, 2, 0.5f, ids);
        //if (mAuth != null && mAuth.getCurrentUser() != null) {
            databaseRawData.getUserDB().child(id).child(currentDate).setValue(a);
        //}
    }}

    public void ReadEasyQuest(){

        /*String id = databaseRawData.getEasyQuestionDB().push().getKey();
        Question ez = new Question("text", "The annual general meeting was in the conference centre", "Meo", "Cho", "Ga", "Vit");
        databaseRawData.getEasyQuestionDB().child(id).setValue(ez);
        id = databaseRawData.getEasyQuestionDB().push().getKey();
        ez = new Question("text", "Cat la gi", "Meo", "Cho", "Ga", "Vit");
        databaseRawData.getEasyQuestionDB().child(id).setValue(ez);*/

            databaseRawData.getEasyQuestionDB().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    databaseRawData.getEasyQuestions().clear();

                    for (DataSnapshot dts : dataSnapshot.getChildren()) {
                        Question result = new Question();
                        result.setDetail(dts.getValue(Question.class).getDetail());
                        result.setType(dts.getValue(Question.class).getType());
                        result.setRA(dts.getValue(Question.class).getRA());
                        result.setWA1(dts.getValue(Question.class).getWA1());
                        result.setWA2(dts.getValue(Question.class).getWA2());
                        result.setWA3(dts.getValue(Question.class).getWA3());

                        databaseRawData.getEasyQuestions().add(result);
                    }
                    //finish();
                    if (databaseRawData.getEasyQuestions().size() != 0) {
                        Add(databaseRawData.getQuestion(DatabaseRawData.EASY_QUESTION).getDetail());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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

    public void ShowLognInResult(){
        if (mAuth != null && mAuth.getCurrentUser() != null) {
            msg("Wellcome " + mAuth.getCurrentUser().getEmail());
        }
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

    //==============google sign in=========
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            showSnackbar(getResources().getString(R.string.permission_granted), 1000);
        }
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...

            }
            //prevent from using without sign in
            if (mAuth == null || mAuth.getCurrentUser() == null) {
                signIn();
                return;
            }
        }
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

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            msg("Wellcome " + mAuth.getCurrentUser().getEmail());
                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            if (mAuth == null) {
                                msg("Logn in failed");
                            }
                        }

                        // ...
                    }
                });
    }
    //=====================================

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
