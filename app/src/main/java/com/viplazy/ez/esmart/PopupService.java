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

public class PopupService extends Service {

    private WindowManager mWindowManager;

    private View mPopupView;

    private View mMainView, mListenView, mReadView, mSpeakView;

    private TextView tv_title;

    private LinearLayout mIncludeLayout;

    private Button btn_close;

    public static final int POPUP_MAIN = 1;
    public static final int POPUP_QUESTION_LISTEN = 2;
    public static final int POPUP_QUESTION_READ = 3;
    public static final int POPUP_QUESTION_SPEAK = 4;

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
        mIncludeLayout = mPopupView.findViewById(R.id.include_layout_popup);


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

        mMainView = getIncludeLayout(POPUP_MAIN);
        mListenView = getIncludeLayout(POPUP_QUESTION_LISTEN);
        mSpeakView = getIncludeLayout(POPUP_QUESTION_SPEAK);
        mReadView = getIncludeLayout(POPUP_QUESTION_READ);



        mIncludeLayout.removeAllViews();

        mIncludeLayout.addView(mMainView);


        Button mainSwapButton = mMainView.findViewById(R.id.btn_swap);

        mainSwapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                state = POPUP_QUESTION_LISTEN;
                mIncludeLayout.removeAllViews();

                mIncludeLayout.addView(mListenView);
            }
        });

        Button listenSwapButton = mListenView.findViewById(R.id.btn_swap);

        listenSwapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                state = POPUP_QUESTION_READ;
                mIncludeLayout.removeAllViews();

                mIncludeLayout.addView(mReadView);
            }
        });

        Button readSwapButton = mReadView.findViewById(R.id.btn_swap);

        readSwapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                state = POPUP_QUESTION_SPEAK;
                mIncludeLayout.removeAllViews();

                mIncludeLayout.addView(mSpeakView);
            }
        });

        Button speakSwapButton = mSpeakView.findViewById(R.id.btn_swap);

        speakSwapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                state = POPUP_MAIN;
                mIncludeLayout.removeAllViews();

                mIncludeLayout.addView(mMainView);
            }
        });


        tv_title = mPopupView.findViewById(R.id.tv_popup_title);
        btn_close = mPopupView.findViewById(R.id.btn_close_popup);

        tv_title.setText("Hello world!");
        tv_title.setTextColor(getResources().getColor(android.R.color.white));

        btn_close.setOnClickListener(new View.OnClickListener() {
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

        switch (type) {
            case  POPUP_QUESTION_LISTEN: {
                return LayoutInflater.from(this).inflate(R.layout.popup_question_listen, null);
            }
            case  POPUP_QUESTION_READ: {
                return LayoutInflater.from(this).inflate(R.layout.popup_question_read, null);
            }
            case  POPUP_QUESTION_SPEAK: {
                return LayoutInflater.from(this).inflate(R.layout.popup_question_speak, null);
            }

            case POPUP_MAIN:
            default: return LayoutInflater.from(this).inflate(R.layout.popup_main, null);
        }
    }
}
