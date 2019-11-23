package com.viplazy.ez.esmart;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PopupService extends Service {

    public static final int POPUP_MAIN = 1;
    public static final int POPUP_QUESTION_LAYOUT = 2;

    private WindowManager mWindowManager;

    private View mPopupView;

    private RelativeLayout mQuestionView;

    private QuestionLayout mQuestionChild;

    private TextView tv_title;

    private Button btn_close;


    RelativeLayout container;

    WindowManager.LayoutParams popup_params;

    private int state;

    public PopupService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        state = POPUP_MAIN;

        //Language language = new Language();
        //Inflate the chat head layout we created

        mPopupView = LayoutInflater.from(this).inflate(R.layout.popup_window, null);

        container = mPopupView.findViewById(R.id.container);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            popup_params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);

            popup_params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
            popup_params.x = 0;
            popup_params.y = 100;
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(mPopupView, popup_params);

        } else {
            popup_params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);


            popup_params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
            popup_params.x = 0;
            popup_params.y = 100;
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(mPopupView, popup_params);
        }

//        mQuestionView = getIncludeLayout(POPUP_QUESTION_LAYOUT);

        mQuestionView = mPopupView.findViewById(R.id.question_field);

        mQuestionChild = new QuestionLayout(mQuestionView);

        mQuestionChild.getSubmit().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    msg(mQuestionChild.getSelectedView().getText().toString());
                }
                catch (NullPointerException e) {
                    //msg("Please choose the answer first!");
                }
            }
        });

        final TextView tv_menu = mPopupView.findViewById(R.id.menu_profile_popup);
        //tv_menu.setBackgroundColor(Color.TRANSPARENT);

        tv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSelf();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mPopupView != null) mWindowManager.removeView(mPopupView);
    }

    View getIncludeLayout(int type) {

        View v;
        switch (type) {
            case  POPUP_QUESTION_LAYOUT: {
                v = LayoutInflater.from(this).inflate(R.layout.popup_question_layout, null);
                break;
            }
            case POPUP_MAIN:
            default: v = LayoutInflater.from(this).inflate(R.layout.popup_main, null);
        }

        LinearLayout.LayoutParams layoutParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

        v.setLayoutParams(layoutParams);

        return v;
    }

    // fast way to call Toast
    private void msg(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private void undoSelectLastView(View v) {

    }

}
