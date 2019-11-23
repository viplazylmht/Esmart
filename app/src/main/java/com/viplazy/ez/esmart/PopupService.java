package com.viplazy.ez.esmart;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PopupService extends Service {

    public static final int POPUP_MAIN = 1;
    public static final int POPUP_QUESTION_LAYOUT = 2;

    private WindowManager mWindowManager;

    private View mPopupView;

    private RelativeLayout mQuestionView;

    private QuestionLayout mQuestionChild;

    private TextView tv_title;

    private String id;
    Long days;

    private Button btn_close;

    DatabaseRawData databaseRawData;

    RelativeLayout container;

    private Question curQuestion;

    private String email;

    private boolean writted = false;

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
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);


        email = intent.getStringExtra("email");
        id = email.replace('.',',');
        ReadUser();

        ReadEasyQuest();
        ReadMediumQuest();
        ReadHardQuest();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Intent intent = new Intent();
        String message = intent.getStringExtra("email");

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

//        mQuestionView = getIncludeLayout(POPUP_QUESTION_LAYOUT);


        mQuestionView = mPopupView.findViewById(R.id.question_field);

        mQuestionChild = new QuestionLayout(mQuestionView);

        mQuestionChild.getSubmit().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            try {
                if (mQuestionChild.getSelectedView().getText().toString().equals(mQuestionChild.getQuestionData().getRA())) {
                    msg("Correct!");
                    addHistoryAnswerId(mQuestionChild.getQuestionData().getId());
                    writted = false;
                    UpdateUser(true);

                }
                else {
                    msg("Wrong Answer!");
                    writted = false;
                    UpdateUser(false);
                }
                mQuestionChild.setCurrentState(QuestionLayout.SELECTED_ITEM_NONE);

                boolean isOut;
                isOut = popAEasyQuestion();

                if (isOut){
                    isOut = popAMediumQuestion();
                    if (isOut){
                        isOut = popAHardQuestion();
                        if (isOut){
                            //congratulation
                            Question cur = new Question();
                            cur.setType("Win");
                            mQuestionChild.setQuestionData(cur);
                        }
                    }
                }
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

    public void ReadUser(){
        databaseRawData.getUserDB().child(id).child("Day").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> idPassed;
                for (DataSnapshot dts : dataSnapshot.getChildren()) {
                    idPassed = dts.getValue(User.class).getPassQuestID();
                    SetIDPassed(idPassed);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void SetIDPassed(ArrayList<String> a){
        historyAnswerId = a;
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
    public void UpdateUser(final boolean right){
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            final String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String pointDate = "2019-11-18";
            String id = email;
            id = id.replace('.', ',');

            //detect when to slip week
            Date now = df.parse(currentDate);
            Date point = df.parse(pointDate);

            days = now.getTime() - point.getTime();
            days /= (1000 * 60 *60 * 24);
            //new week
            if (days % 7 == 0){
                databaseRawData.getUserDB().child(id).child("Day").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int numQuest = 0;
                        int numRight = 0;
                        String email;
                        for (DataSnapshot dts : dataSnapshot.getChildren()) {
                            numQuest += dts.getValue(User.class).getNumQuestAnswered();
                            numRight += dts.getValue(User.class).getPercent() * numQuest;
                        }
                        //finish
                        numQuest++;
                        if (right){
                            numRight++;
                        }
                        User a = new User(0, numQuest, 1.0f * numRight / numQuest, new ArrayList<String>());

                        WriteNewUser(a, "Week", days.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            //new month
            if (days % 30 == 0){
                databaseRawData.getUserDB().child(id).child("Week").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int numQuest = 0;
                        int numRight = 0;
                        String email;
                        for (DataSnapshot dts : dataSnapshot.getChildren()) {
                            numQuest += dts.getValue(User.class).getNumQuestAnswered();
                            numRight += dts.getValue(User.class).getPercent() * numQuest;
                        }
                        //finish
                        numQuest++;
                        if (right){
                            numRight++;
                        }
                        User a = new User(0, numQuest, 1.0f * numRight / numQuest, new ArrayList<String>());

                        WriteNewUser(a, "Month", days.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            databaseRawData.getUserDB().child(id).child("Day").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int numQuest = 0;
                    int numRight = 0;
                    ArrayList<String> idPassed = new ArrayList<String>();
                    String email;
                    for (DataSnapshot dts : dataSnapshot.getChildren()) {
                        numQuest += dts.getValue(User.class).getNumQuestAnswered();
                        numRight += dts.getValue(User.class).getPercent() * numQuest;
                        idPassed = dts.getValue(User.class).getPassQuestID();
                    }
                    //finish
                    numQuest++;
                    if (right) {
                        numRight++;
                        if (idPassed.indexOf(historyAnswerId.get(historyAnswerId.size() - 1)) < 0) {
                            idPassed.add(historyAnswerId.get(historyAnswerId.size() - 1));
                        }
                    }
                    if (idPassed != null) {
                        User a = new User(0, numQuest, 1.0f * numRight / numQuest, idPassed);

                        WriteNewUser(a, "Day", currentDate);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //databaseRawData.getUserDB().child(id).child("Day").child(currentDate);
            //User a = new User(12, 2, 0.5f, ids);

            //databaseRawData.getUserDB().child(id).child("Day").child(currentDate).setValue(a);
        }

        catch (ParseException e) {

        }
    }

    public void WriteNewUser(User a, String parent, String key){
        if (!writted) {
            databaseRawData.getUserDB().child(id).child(parent).child(key).setValue(a);
            writted = true;
        }
    }

    public void ReadEasyQuest(){

        /*String id = databaseRawData.getEasyQuestionDB().push().getKey();
        Question ez = new Question("img",""
                ,"What is it?","https://icon-library.net/images/icq-icon.png","lotus"
                ,"catus", "rose","orchid");
        databaseRawData.getEasyQuestionDB().child(id).setValue(ez);

        id = databaseRawData.getEasyQuestionDB().push().getKey();
        ez = new Question("img",""
                ,"What is it?","https://www.planetminecraft.com/files/resource_media/screenshot/1241/fridge_3824223_thumb.jpg",
                "fridge","washing machine", "freeze machine","air conditioner");
        databaseRawData.getEasyQuestionDB().child(id).setValue(ez);


        id = databaseRawData.getEasyQuestionDB().push().getKey();
        ez = new Question("img",""
                ,"What is it?","https://images-na.ssl-images-amazon.com/images/I/51FarxoqT1L._SX466_.jpg",
                "fork","spoon", "pitchforks","javelin");
        databaseRawData.getEasyQuestionDB().child(id).setValue(ez);


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
                //popAEasyQuestion(true);
                //curQuestion here
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void ReadMediumQuest(){

        /*String id = databaseRawData.getEasyQuestionDB().push().getKey();
        //id = databaseRawData.getEasyQuestionDB().push().getKey();
        Question ez = new Question("text", "",
                "The graduation ball promises to be the social _____ of the year.",
                "", "event", "festival", "programme", "activity");
        databaseRawData.getMediumQuestionDB().child(id).setValue(ez);

        id = databaseRawData.getMediumQuestionDB().push().getKey();
        ez = new Question("text", "",
                "The college offers a _____ range of evening courses.",
                "", "wide", "distinct", "changeable", "various");
        databaseRawData.getMediumQuestionDB().child(id).setValue(ez);*/

        databaseRawData.getMediumQuestionDB().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                databaseRawData.getMediumQuestions().clear();

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

                    databaseRawData.getMediumQuestions().add(result);
                }
                //finish;

                //popAMediumQuestion();

                //curQuestion here
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void ReadHardQuest(){

        /*String id = databaseRawData.getHardQuestionDB().push().getKey();
        //id = databaseRawData.getMediumQuestionDB().push().getKey();
        Question ez = new Question("text", "",
                "Sammy _____ his father to buy him a new mountain bike for Christmas.",
                "", "implored", "consumed", "persisted", "demanded");
        databaseRawData.getHardQuestionDB().child(id).setValue(ez);

        id = databaseRawData.getHardQuestionDB().push().getKey();
        ez = new Question("text", "",
                "There was a time when the _____ person could not afford a mobile phone.",
                "", "ordinary", "normal", "usual", "regular");
        databaseRawData.getHardQuestionDB().child(id).setValue(ez);

        id = databaseRawData.getHardQuestionDB().push().getKey();
        ez = new Question("text", "",
                "At the moment, the country has a number of _____ economic problems.",
                "", "serious", "hard", "tricky", "difficult");
        databaseRawData.getHardQuestionDB().child(id).setValue(ez);*/

        databaseRawData.getHardQuestionDB().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                databaseRawData.getHardQuestions().clear();

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

                    databaseRawData.getHardQuestions().add(result);
                }
                //finish;
                //popAHardQuestion();
                boolean isOut;
                isOut = popAEasyQuestion();

                if (isOut){
                    isOut = popAMediumQuestion();
                    if (isOut){
                        isOut = popAHardQuestion();
                        if (isOut){
                            //congratulation
                            Question cur = new Question();
                            cur.setType("Win");
                            mQuestionChild.setQuestionData(cur);
                        }
                    }
                }

                //curQuestion here
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean popAEasyQuestion() {
        if (databaseRawData.getEasyQuestions().size() != 0) {
            // Add(databaseRawData.getQuestion(DatabaseRawData.EASY_QUESTION).getDetail());
            //Question get here
            ArrayList<String> arrString = new ArrayList<String>();
            while (true) {

                Question q = databaseRawData.getQuestion(DatabaseRawData.EASY_QUESTION);
                if (arrString.indexOf(q.getId()) < 0)
                    arrString.add(q.getId());
                if (!isInHistoryAnswerId(q.getId())) {
                    mQuestionChild.setQuestionData(q);
                    return false;
                }
                if (arrString.size() >= databaseRawData.getEasyQuestions().size()){
                    return true;
                }
            }
        }
        return true;
    }

    private boolean popAMediumQuestion() {
        if (databaseRawData.getMediumQuestions().size() != 0) {
            // Add(databaseRawData.getQuestion(DatabaseRawData.EASY_QUESTION).getDetail());
            //Question get here
            ArrayList<String> arrString = new ArrayList<String>();
            while (true) {

                Question q = databaseRawData.getQuestion(DatabaseRawData.MEDIUM_QUESTION);
                if (arrString.indexOf(q.getId()) < 0)
                    arrString.add(q.getId());
                if (!isInHistoryAnswerId(q.getId())) {
                    mQuestionChild.setQuestionData(q);
                    return false;
                }
                if (arrString.size() >= databaseRawData.getMediumQuestions().size()){
                    return true;
                }
            }
        }
        return true;
    }

    private boolean popAHardQuestion() {
        if (databaseRawData.getHardQuestions().size() != 0) {
            // Add(databaseRawData.getQuestion(DatabaseRawData.EASY_QUESTION).getDetail());
            //Question get here
            ArrayList<String> arrString = new ArrayList<String>();
            while (true) {

                Question q = databaseRawData.getQuestion(DatabaseRawData.HARD_QUESTION);
                if (arrString.indexOf(q.getId()) < 0)
                    arrString.add(q.getId());
                if (!isInHistoryAnswerId(q.getId())) {
                    mQuestionChild.setQuestionData(q);
                    return false;
                }
                if (arrString.size() >= databaseRawData.getHardQuestions().size()){
                    return true;
                }
            }
        }
        return true;
    }

    public ArrayList<String> getHistoryAnswerId() {
        return historyAnswerId;
    }

    public void setHistoryAnswerId(ArrayList<String> historyAnswerId) {
        this.historyAnswerId = historyAnswerId;
    }

    public boolean isInHistoryAnswerId(String id) {
        if (historyAnswerId == null || historyAnswerId.indexOf(id) >= 0) return true;
        else return false;
    }

    public void addHistoryAnswerId(String nextID) {
        if (!isInHistoryAnswerId(nextID)) historyAnswerId.add(nextID);
    }




}