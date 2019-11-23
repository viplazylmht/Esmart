package com.viplazy.ez.esmart;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

public class StartPopupActivitiy extends AppCompatActivity {
    public static String TRANSFER_CALENDAR_KEY_1 = "key_calendar1";
    public static String TRANSFER_CALENDAR_KEY_2 = "key_calendar2";
    public static String TRANSFER_CALENDAR_KEY_3 = "key_calendar3";

    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_popup_activitiy);

        Bundle extras = getIntent().getExtras();

        if (extras!=null)
            email = extras.getString("email");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, MainActivity.ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
        }
        else {

            Intent serviceIntent = new Intent(this, PopupService.class);

            serviceIntent.putExtra("email", email);

            startService(serviceIntent);



            finish();
        }
    }
}
