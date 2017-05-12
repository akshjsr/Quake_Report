package com.example.android.quakereport;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Akash on 21-03-2017.
 */

public class Earthquake_info {
    private double mag;
    private String place;
    private long date;
    private String url;

    public Earthquake_info(double m_mag, String p_place, long d_date , String u_url)
    {
        mag = m_mag;
        place = p_place;
        date = d_date;
        url=u_url;
    }


    public double getmag() {
        return mag;
    }


    public String getPlace() {
        return place;
    }


    public long gettimeinmillisec() {
        return date;
    }

    public String getUrl(){ return url;}

}
