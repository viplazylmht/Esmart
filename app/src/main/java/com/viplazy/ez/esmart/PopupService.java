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

    private View collapsedView;
    private View expandedView;

    DatabaseRawData databaseRawData;

    RelativeLayout container;

    ImageView close_btn;

    Point defaultPoint;

    Context context;

    WindowManager.LayoutParams popup_params;

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

        context = getApplication().getApplicationContext();

        //Language language = new Language();
        //Inflate the chat head layout we created

        mPopupView = LayoutInflater.from(this).inflate(R.layout.popup_window, null);

        collapsedView = mPopupView.findViewById(R.id.collapse_view);
        expandedView = mPopupView.findViewById(R.id.expanded_container);

        collapsedView.setVisibility(View.VISIBLE);
        expandedView.setVisibility(View.GONE);

        close_btn = mPopupView.findViewById(R.id.close_btn);



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

        ReadEasyQuest();
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