package com.example.android.chatly;

/**
 * Created by chy on 6/9/17.
 */

public class Notification {
    private boolean accepted;
    private String from;
    private String message;
    private String fromID;
    public Notification(){}
    public Notification(boolean accepted, String from, String fromID){
        this.accepted = accepted;
        this.from = from;
        this.fromID = fromID;
        this.message = from + " sends a friend request to you";
    }

    public void setAccepted(boolean accepted){
        this.accepted = accepted;
    }
    public boolean getAccepted(){
        return accepted;
    }

    public void setFrom(String from){
        this.from = from;
    }
    public String getFrom(){
        return from;
    }

    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return message;
    }


    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }
}
