package com.stephenfacentedev.com.monkeydefense;

/**
 * Created by stephenfacente on 4/13/15.
 */
public class lineItem {
    public String username;
    public int score;
    public int index;

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}