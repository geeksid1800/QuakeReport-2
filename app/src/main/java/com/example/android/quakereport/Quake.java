package com.example.android.quakereport;

public class Quake {
    private Double mMag;
    private String mLocation;
    private long mTimeInMilliseconds;
    private String mLink;

    public Quake(Double Mag, String Location, long TimeInMilliseconds, String link){
        mMag = Mag;
        mLocation = Location;
        mTimeInMilliseconds = TimeInMilliseconds;
        mLink = link;
    }

    public Double getMag(){
        return mMag;
    }

    public String getLocation(){ return mLocation; }

    public long getTimeInMilliseconds(){
        return mTimeInMilliseconds;
    }

    public String getLink(){
        return mLink;
    }

}
