package com.viplazy.ez.esmart;

import java.util.ArrayList;

public class User {
    private int rank;
    private int numQuestAnswered;
    private float percent;
    private ArrayList<String> passQuestID;

    public User(int rank, int numQuestAnswered, float percent, ArrayList<String> passQuestID) {
        this.rank = rank;
        this.numQuestAnswered = numQuestAnswered;
        this.percent = percent;
        this.passQuestID = passQuestID;
    }

    public int getNumQuestAnswered() {
        return numQuestAnswered;
    }

    public void setNumQuestAnswered(int numQuestAnswered) {
        this.numQuestAnswered = numQuestAnswered;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public User() {
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public ArrayList<String> getPassQuestID() {
        return passQuestID;
    }

    public void setPassQuestID(ArrayList<String> passQuestID) {
        this.passQuestID = passQuestID;
    }
}
