package com.viplazy.ez.esmart;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import java.util.Locale;

public class PopupService extends Service {

    public static final int POPUP_MAIN = 1;
    public static final int POPUP_QUESTION_LAYOUT = 2;

    private WindowManager mWindowManager;

    private View mPopupView;

    private RelativeLayout mQuestionView;

    private QuestionLayout mQuestionChild;

    private View collapsedView;
    private View expandedView;

    private String id;
    Long days;

    DatabaseRawData databaseRawData;

    RelativeLayout container;

    ImageView close_btn;

    private TextView tv_next_question;

    Point defaultPoint;

    Context context;
    private Question curQuestion;

    private String email;

    private boolean writted = false;

    WindowManager.LayoutParams popup_params;

    private ScrollView answer_filed;

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

        context = getApplication().getApplicationContext();

        //Language language = new Language();
        //Inflate the chat head layout we created

        mPopupView = LayoutInflater.from(this).inflate(R.layout.popup_window, null);

        collapsedView = mPopupView.findViewById(R.id.collapse_view);
        expandedView = mPopupView.findViewById(R.id.expanded_container);

        answer_filed = mPopupView.findViewById(R.id.answer_field);

        collapsedView.setVisibility(View.VISIBLE);
        expandedView.setVisibility(View.GONE);

        close_btn = mPopupView.findViewById(R.id.close_btn);

        tv_next_question = mPopupView.findViewById(R.id.tv_next_question);
        tv_next_question.setVisibility(View.GONE);

        tv_next_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mQuestionChild.getQuestionTitle().setVisibility(View.VISIBLE);
                answer_filed.setVisibility(View.VISIBLE);
                mQuestionChild.getSubmit().setVisibility(View.VISIBLE);

                //mQuestionChild.getImageView().setBackgroundColor(Color.TRANSPARENT);

                mQuestionChild.getImageView().setVisibility(View.GONE);

                tv_next_question.setVisibility(View.GONE);
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
        });

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSelf();
            }
        });


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

        defaultPoint = new Point(popup_params.x, popup_params.y);

        databaseRawData = new DatabaseRawData();

        //for get database
        //databaseRawData.setEasyQuestionDB(new DatabaseReference(FirebaseDatabase.getInstance().getReference("Question/Medium")));
        //databaseRawData.getEasyQuestionDB().keepSynced(true);

//        mQuestionView = getIncludeLayout(POPUP_QUESTION_LAYOUT);


        mQuestionView = mPopupView.findViewById(R.id.question_field);

        mQuestionChild = new QuestionLayout(mQuestionView);

        mQuestionChild.setContext(context);

        mQuestionChild.getSubmit().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            try {
                if (mQuestionChild.getSelectedView().getText().toString().equals(mQuestionChild.getQuestionData().getRA())) {
                    msg("Correct!");
                    addHistoryAnswerId(mQuestionChild.getQuestionData().getId());
                    writted = false;
                    UpdateUser(true);
                    showResult(true);

                }
                else {
                    msg("Wrong Answer!");
                    writted = false;
                    UpdateUser(false);
                    showResult(false);
                }
                mQuestionChild.setCurrentState(QuestionLayout.SELECTED_ITEM_NONE);

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
                if(!isViewCollapsed()) {
                    expandedView.setVisibility(View.GONE);
                    collapsedView.setVisibility(View.VISIBLE);
                }
            }
        });


        mPopupView.measure(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);


        final int screenWidth = mPopupView.getMeasuredWidth();
        final int screenHeight = mPopupView.getMeasuredHeight();

        mPopupView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;



            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = popup_params.x;
                        initialY = popup_params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);


                        //The check for Xdiff <5 && YDiff< 5 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 5 && Ydiff < 5) {
                            if (isViewCollapsed()) {
                                //When user clicks on the image view of the collapsed layout,
                                //visibility of the collapsed layout will be changed to "View.GONE"
                                //and expanded view will become visible.

                                popup_params.x = defaultPoint.x;
                                popup_params.y = defaultPoint.y;


                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);

                                mWindowManager.updateViewLayout(mPopupView, popup_params);

                            }
                        }

                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if (isViewCollapsed()) {
                            //Calculate the X and Y coordinates of the view.
                            popup_params.x = initialX + (int) (event.getRawX() - initialTouchX);
                            popup_params.y = initialY + (int) (event.getRawY() - initialTouchY);

                            //Update the layout with new X & Y coordinate
                            mWindowManager.updateViewLayout(mPopupView, popup_params);
                        }
                        return true;

                    default:
                        if (popup_params.x < screenWidth / 2) {
                            while (popup_params.x > 0) {
                                popup_params.x -= 10;
                                mWindowManager.updateViewLayout(mPopupView, popup_params);
                            }
                        } else {
                            while (popup_params.x <= screenWidth) {
                                popup_params.x += 10;
                                mWindowManager.updateViewLayout(mPopupView, popup_params);
                            }
                        }
                        return false;
                }}
        });
    }

    private boolean isViewCollapsed() {
        return mPopupView == null || mPopupView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
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
        Question ez = new Question("audio","","What did she say?","https://raw.githubusercontent.com/viplazylmht/Esmart/QuestionResource/audio/find.mp3",
                 "find", "fine", "my", "hight");
        databaseRawData.getEasyQuestionDB().child(id).setValue(ez);

        id = databaseRawData.getEasyQuestionDB().push().getKey();
        ez = new Question("audio","","What did she say?","https://raw.githubusercontent.com/viplazylmht/Esmart/QuestionResource/audio/knight.mp3",
                 "knight", "nice", "kind", "buy");
        databaseRawData.getEasyQuestionDB().child(id).setValue(ez);

        id = databaseRawData.getEasyQuestionDB().push().getKey();
        ez = new Question("audio","","What did she say?","https://raw.githubusercontent.com/viplazylmht/Esmart/QuestionResource/audio/life.mp3",
                 "life", "light", "lie", "nice");
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
        if (historyAnswerId != null && historyAnswerId.indexOf(id) >= 0) return true;
        else return false;
    }

    public void addHistoryAnswerId(String nextID) {
        if (!isInHistoryAnswerId(nextID)) historyAnswerId.add(nextID);
    }


    public void showResult(boolean isWin) {
        mQuestionChild.getQuestionTitle().setVisibility(View.GONE);
        answer_filed.setVisibility(View.GONE);
        mQuestionChild.getSubmit().setVisibility(View.GONE);

        mQuestionChild.getImageView().setVisibility(View.VISIBLE);
        if (isWin) {
            mQuestionChild.getImageView().setBackgroundResource(R.drawable.ic_win);
        }
        else {
            mQuestionChild.getImageView().setBackgroundResource(R.drawable.ic_wlose);
        }

        tv_next_question.setVisibility(View.VISIBLE);
    }
}