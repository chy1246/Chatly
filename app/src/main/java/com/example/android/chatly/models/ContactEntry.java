package com.example.android.chatly.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chy on 5/31/17.
 */

public class ContactEntry implements Parcelable{
    String _contactUID;
    String _displayName;
    String _firebaseToken;
    String _latestMsg;
    Uri _photoUri;
    long _timestamp;

    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public ContactEntry createFromParcel(Parcel in) {
            return new ContactEntry(in);
        }

        public ContactEntry[] newArray(int size) {
            return new ContactEntry[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_contactUID);
        dest.writeString(_displayName);
        dest.writeString(_firebaseToken);
        dest.writeString(_latestMsg);
        dest.writeString(_photoUri == null ? "" : _photoUri.toString());
        dest.writeLong(_timestamp);

    }

    public ContactEntry() {
    }

    public ContactEntry(Parcel in){
        _contactUID = in.readString();
        _displayName = in.readString();
        _firebaseToken = in.readString();
        _latestMsg = in.readString();
        _photoUri = Uri.parse(in.readString());
        _timestamp = in.readLong();


    }

    public ContactEntry(String _contactUID, String _displayName, String _firebaseToken, String _latestMsg, Uri _photoUri, long _timestamp) {
        this._contactUID = _contactUID;
        this._displayName = _displayName;
        this._firebaseToken = _firebaseToken;
        this._latestMsg = _latestMsg;
        this._photoUri = _photoUri;
        this._timestamp = _timestamp;
    }

    public String get_contactUID() {
        return _contactUID;
    }

    public void set_contactUID(String _contactUID) {
        this._contactUID = _contactUID;
    }

    public long get_timestamp() {
        return _timestamp;
    }

    public void set_timestamp(long _timestamp) {
        this._timestamp = _timestamp;
    }

    public String get_latestMsg() {
        return _latestMsg;
    }

    public void set_latestMsg(String _latestMsg) {
        this._latestMsg = _latestMsg;
    }

    public String get_displayName() {
        return _displayName;
    }

    public void set_displayName(String _displayName) {
        this._displayName = _displayName;
    }

    public Uri get_photoUri() {
        return _photoUri;
    }

    public void set_photoUri(Uri _photoUri) {
        this._photoUri = _photoUri;
    }

    public String get_firebaseToken() {
        return _firebaseToken;
    }

    public void set_firebaseToken(String _firebaseToken) {
        this._firebaseToken = _firebaseToken;
    }


}
