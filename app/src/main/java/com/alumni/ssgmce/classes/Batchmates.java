package com.alumni.ssgmce.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by USer on 13-10-2016.
 */

public class Batchmates implements Parcelable{
    String memberID,FullName,CourseName,CurrentLocation,profilepic, Branch,JoiningYear;

    public Batchmates(String memberID, String fullName, String courseName, String currentLocation, String profilepic, String Branch, String JoiningYear) {
        this.memberID = memberID;
        FullName = fullName;
        CourseName = courseName;
        CurrentLocation = currentLocation;
        this.profilepic = profilepic;
        this.Branch = Branch;
        this.JoiningYear = JoiningYear;
    }

    protected Batchmates(Parcel in) {
        memberID = in.readString();
        FullName = in.readString();
        CourseName = in.readString();
        CurrentLocation = in.readString();
        profilepic = in.readString();
        Branch = in.readString();
        JoiningYear = in.readString();
    }

    public static final Creator<Batchmates> CREATOR = new Creator<Batchmates>() {
        @Override
        public Batchmates createFromParcel(Parcel in) {
            return new Batchmates(in);
        }

        @Override
        public Batchmates[] newArray(int size) {
            return new Batchmates[size];
        }
    };

    public String getMemberID() {
        return memberID;
    }

    public String getFullName() {
        return FullName;
    }

    public String getCourseName() {
        return CourseName;
    }

    public String getCurrentLocation() {
        return CurrentLocation;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public String getBranch() {
        return Branch;
    }

    public String getJoiningYear() {
        return JoiningYear;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(memberID);
        dest.writeString(FullName);
        dest.writeString(CourseName);
        dest.writeString(CurrentLocation);
        dest.writeString(profilepic);
        dest.writeString(Branch);
        dest.writeString(JoiningYear);
    }
}
