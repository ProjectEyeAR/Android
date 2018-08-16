package org.team.eye;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class User implements Parcelable {
    private int mId;
    private String mEmail;
    private String mUsername;

    public User(int id, String email, String username) {
        mId = id;
        mEmail = email;
        mUsername = username;
    }

    public User(JSONObject jsonObject) {
        if (jsonObject != null) {
            mId = jsonObject.optInt("id", 0);
            mEmail = jsonObject.optString("email", null);
            mUsername = jsonObject.optString("username", null);
        }
    }

    public User(Parcel parcel) {
        String[] strings = new String[3];

        parcel.readStringArray(strings);

        mId = Integer.parseInt(strings[0]);
        mEmail = strings[1];
        mUsername = strings[2];
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public void setId(int mId) {
        this.mId = mId;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public int getId() {
        return mId;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getUsername() {
        return mUsername;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{Integer.toString(mId), mEmail, mUsername});
    }
}
