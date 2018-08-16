package org.team.eye;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageMemo implements Parcelable {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.US
    );

    private User mUser;
    private String mImage;
    private String mImageFilter;
    private String mMemo;
    private Date mTimestamp;

    public ImageMemo(JSONObject jsonObject) {
        mUser = new User(jsonObject.optJSONObject("user"));
        mImage = jsonObject.optString("image", null);
        mImageFilter = jsonObject.optString("imageFilter", null);
        mMemo = jsonObject.optString("memo",  null);

        try {
            mTimestamp = SIMPLE_DATE_FORMAT.parse(jsonObject.getString("timestamp"));
        } catch (JSONException | ParseException e) {
            mTimestamp = null;
        }
    }

    public ImageMemo(Parcel parcel) {
        String[] strings = new String[7];

        parcel.readStringArray(strings);

        mUser = new User(Integer.parseInt(strings[0]), strings[1], strings[2]);
        mImage = strings[3];
        mImageFilter = strings[4];
        mMemo = strings[5];

        try {
            mTimestamp = SIMPLE_DATE_FORMAT.parse(strings[6]);
        } catch (ParseException e) {
            mTimestamp = null;
        }
    }

    public static final Creator<ImageMemo> CREATOR = new Creator<ImageMemo>() {
        @Override
        public ImageMemo createFromParcel(Parcel in) {
            return new ImageMemo(in);
        }

        @Override
        public ImageMemo[] newArray(int size) {
            return new ImageMemo[size];
        }
    };

    public User getUser() {
        return mUser;
    }

    public String getImage() {
        return mImage;
    }

    public String getImageFilter() {
        return mImageFilter;
    }

    public String getMemo() {
        return mMemo;
    }

    public Date getTimestamp() {
        return mTimestamp;
    }

    public void setUser(User mUser) {
        this.mUser = mUser;
    }

    public void setImage(String mImage) {
        this.mImage = mImage;
    }

    public void setImageFilter(String mImageFilter) {
        this.mImageFilter = mImageFilter;
    }

    public void setMemo(String mMemo) {
        this.mMemo = mMemo;
    }

    public void setTimestamp(Date mTimestamp) {
        this.mTimestamp = mTimestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String[] strings = new String[]{
                Integer.toString(mUser.getId()),
                mUser.getEmail(),
                mUser.getUsername(),
                mImage,
                mImageFilter,
                mMemo,
                SIMPLE_DATE_FORMAT.format(mTimestamp)
        };
        dest.writeStringArray(strings);
    }
}
