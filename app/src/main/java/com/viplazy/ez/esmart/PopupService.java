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

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PopupService extends Service {

    public static final int POPUP_MAIN = 1;
    public static final int POPUP_QUESTION_LAYOUT = 2;

    private WindowManager mWindowManager;

    private View mPopupView;

    private RelativeLayout mQuestionView;

    private QuestionLayout mQuestionChild;

    private TextView tv_title;

    private Button btn_close;

    DatabaseRawData databaseRawData;

    RelativeLayout container;

    Question curQuestion;

    WindowManager.LayoutParams popup_params;

    private int state;

    private ArrayList<String> historyAnswerId = new ArrayList<>();

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

        databaseRawData = new DatabaseRawData();

        //for get database
        //databaseRawData.setEasyQuestionDB(new DatabaseReference(FirebaseDatabase.getInstance().getReference("Question/Medium")));
        //databaseRawData.getEasyQuestionDB().keepSynced(true);

        ReadEasyQuest();
//        mQuestionView = getIncludeLayout(POPUP_QUESTION_LAYOUT);


        mQuestionView = mPopupView.findViewById(R.id.question_field);

        mQuestionChild = new QuestionLayout(mQuestionView);

        mQuestionChild.getSubmit().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (mQuestionChild.getSelectedView().getText().toString().equals(mQuestionChild.getQuestionData().getRA())) {
                        msg("Correct!");

                    }
                    else msg("Wrong Answer!");
                    addHistoryAnswerId(mQuestionChild.getQuestionData().getId());
                    mQuestionChild.setCurrentState(QuestionLayout.SELECTED_ITEM_NONE);

                    popAEasyQuestion();
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
                    result.setPath(dts.getValue(Question.class).getPath());
                    result.setId(dts.getKey());
                    result.setRA(dts.getValue(Question.class).getRA());
                    result.setWA1(dts.getValue(Question.class).getWA1());
                    result.setWA2(dts.getValue(Question.class).getWA2());
                    result.setWA3(dts.getValue(Question.class).getWA3());

                    databaseRawData.getEasyQuestions().add(result);
                }
                //finish;

                popAEasyQuestion();

                //curQuestion here
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void popAEasyQuestion() {
        if (databaseRawData.getEasyQuestions().size() != 0) {
            // Add(databaseRawData.getQuestion(DatabaseRawData.EASY_QUESTION).getDetail());
            //Question get here
            int k = 0;

            while (true) {

                Question q = databaseRawData.getQuestion(DatabaseRawData.EASY_QUESTION);
                if (!isInHistoryAnswerId(q.getId())) {
                    mQuestionChild.setQuestionData(q);
                    return;
                }
                else {
                    k++;
                    if (k > databaseRawData.getEasyQuestions().size()) break;
                };
            }
        }
    }

    public ArrayList<String> getHistoryAnswerId() {
        return historyAnswerId;
    }

    public void setHistoryAnswerId(ArrayList<String> historyAnswerId) {
        this.historyAnswerId = historyAnswerId;
    }

    public boolean isInHistoryAnswerId(String id) {
        if (historyAnswerId.indexOf(id) >= 0) return true;
        else return false;
    }

    public void addHistoryAnswerId(String nextID) {
        if (!isInHistoryAnswerId(nextID)) historyAnswerId.add(nextID);
    }




}