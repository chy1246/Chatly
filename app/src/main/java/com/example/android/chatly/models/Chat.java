package com.example.android.chatly.models;

import android.net.Uri;

/**
 * Created by chy on 5/31/17.
 */

public class Chat {
    private String _senderUID;
    private String _receiverUID;
    private String _message;
    private String _imageURI;
    private String _videoURI;
    private long _timestamp;

    public Chat(){

    }

    public Chat(String _senderUID, String _receiverUID, String _message, String _imageURI, String _videoURI, long _timestamp) {

        this._senderUID = _senderUID;
        this._receiverUID = _receiverUID;
        this._message = _message;
        this._imageURI = _imageURI;
        this._videoURI = _videoURI;
        this._timestamp = _timestamp;
    }


    public String get_senderUID() {
        return _senderUID;
    }

    public void set_senderUID(String _senderUID) {
        this._senderUID = _senderUID;
    }

    public String get_receiverUID() {
        return _receiverUID;
    }

    public void set_receiverUID(String _receiverUID) {
        this._receiverUID = _receiverUID;
    }

    public String get_message() {
        return _message;
    }

    public void set_message(String _message) {
        this._message = _message;
    }

    public long get_timestamp() {
        return _timestamp;
    }

    public void set_timestamp(long _timestamp) {
        this._timestamp = _timestamp;
    }

    public String get_imageURI() {
        return _imageURI;
    }

    public void set_imageURI(String _imageURI) {
        this._imageURI = _imageURI;
    }

    public String get_videoURI() {
        return _videoURI;
    }

    public void set_videoURI(String _videoURI) {
        this._videoURI = _videoURI;
    }
}
