package com.example.android.chatly;

import android.graphics.Bitmap;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by chy on 5/23/17.
 */

public class User {
    private String userName;
    private String email;
    private String uid;
    private String habit;
    private String gender;
    private Bitmap portrait;


    public User(){}

    public User(String userName, String email, String uid,  String habit, String gender){
        this.userName = userName;
        this.email = email;
        this.uid = uid;
        this.habit = habit;
        this.gender = gender;
    }

    public User(String email, String uid){
        this.email = email;
        this.uid = uid;
    }

    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }

    public String getUid(){
        return uid;
    }
    public void setUid(String id){
        uid = id;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getHabit() {
        return habit;
    }
    public void setHabit(String habit){
        this.habit = habit;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    //public
}
