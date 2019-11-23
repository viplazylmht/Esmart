package com.viplazy.ez.esmart;

import android.text.style.QuoteSpan;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;


public class DatabaseRawData implements Serializable {
    public static final int EASY_QUESTION = 1;
    public static final int MEDIUM_QUESTION = 2;
    public static final int HARD_QUESTION = 3;

    private DatabaseReference easyQuestionDB;
    private DatabaseReference mediumQuestionDB;
    private DatabaseReference hardQuestionDB;
    private ArrayList<Question> easyQuestions = new ArrayList<>();
    private ArrayList<Question> mediumQuestions = new ArrayList<>();
    private ArrayList<Question> hardQuestions = new ArrayList<>();
    private DatabaseReference userDB;
    public Question question = new Question();
    private User curUser;

    public Question getQuestion(int level){
        Random random = new Random();

        if (level == EASY_QUESTION){
            int i = random.nextInt(easyQuestions.size());
            return easyQuestions.get(i);
        }
        if (level == MEDIUM_QUESTION){
            int i = random.nextInt(mediumQuestions.size());
            return mediumQuestions.get(i);
        }
        if (level == HARD_QUESTION) {
            int i = random.nextInt(hardQuestions.size());
            return hardQuestions.get(i);
        }

        return new Question();
    }

    private void getQuestionFromDataRef(DatabaseReference dref){

        dref.push();
        DatabaseReference a = FirebaseDatabase.getInstance().getReference(dref.getParent().getKey());
        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Question result = new Question();
                result.setDetail(dataSnapshot.getValue(Question.class).getDetail());
                result.setType(dataSnapshot.getValue(Question.class).getType());
                result.setRA(dataSnapshot.getValue(Question.class).getRA());
                result.setWA1(dataSnapshot.getValue(Question.class).getWA1());
                result.setWA2(dataSnapshot.getValue(Question.class).getWA2());
                result.setWA3(dataSnapshot.getValue(Question.class).getWA3());

                AddQuestToQuest(result);
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void AddQuestToQuest(Question a){
        String type = a.getType();
        String detail = a.getDetail();
        String ra = a.getRA();
        String wa1 = a.getWA1();
        String wa2 = a.getWA2();
        String wa3 = a.getWA3();
        question = new Question(type,detail,ra,wa1,wa2,wa3);
    }

    public DatabaseReference getMediumQuestionDB() {
        return mediumQuestionDB;
    }

    public void setMediumQuestionDB(DatabaseReference mediumQuestionDB) {
        this.mediumQuestionDB = mediumQuestionDB;
    }

    public DatabaseReference getHardQuestionDB() {
        return hardQuestionDB;
    }

    public void setHardQuestionDB(DatabaseReference hardQuestionDB) {
        this.hardQuestionDB = hardQuestionDB;
    }

    public ArrayList<Question> getMediumQuestions() {
        return mediumQuestions;
    }

    public void setMediumQuestions(ArrayList<Question> mediumQuestions) {
        this.mediumQuestions = mediumQuestions;
    }

    public ArrayList<Question> getHardQuestions() {
        return hardQuestions;
    }

    public void setHardQuestions(ArrayList<Question> hardQuestions) {
        this.hardQuestions = hardQuestions;
    }

    public DatabaseRawData() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        easyQuestionDB = FirebaseDatabase.getInstance().getReference("Question/Easy");
        mediumQuestionDB = FirebaseDatabase.getInstance().getReference("Question/Medium");
        hardQuestionDB = FirebaseDatabase.getInstance().getReference("Question/Hard");
        userDB = FirebaseDatabase.getInstance().getReference("User");
    }

    public DatabaseRawData(DatabaseReference easyQuestionDB, ArrayList<Question> easyQuestions, DatabaseReference userDB, User curUser) {
        this.easyQuestionDB = easyQuestionDB;
        this.easyQuestions = easyQuestions;
        this.userDB = userDB;
        this.curUser = curUser;
    }

    public DatabaseReference getEasyQuestionDB() {
        return easyQuestionDB;
    }

    public void setEasyQuestionDB(DatabaseReference easyQuestionDB) {
        this.easyQuestionDB = easyQuestionDB;
    }

    public ArrayList<Question> getEasyQuestions() {
        return easyQuestions;
    }

    public void setEasyQuestions(ArrayList<Question> easyQuestions) {
        this.easyQuestions = easyQuestions;
    }

    public DatabaseReference getUserDB() {
        return userDB;
    }

    public void setUserDB(DatabaseReference userDB) {
        this.userDB = userDB;
    }

    public User getCurUser() {
        return curUser;
    }

    public void setCurUser(User curUser) {
        this.curUser = curUser;
    }

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
