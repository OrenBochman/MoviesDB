package com.example.oren.moviesdb.helpers;

import android.util.Log;

public class ColorUtils {


    public static String  TAG = ColorUtils.class.getName();

    public static int getScaledColor(float normed_rating){
        int A,R,G,B;
        R=B=G=0;
        A=254;
        B=85;
        if (normed_rating<0.5){
            R= 255;
            G= (int) (normed_rating*100*5.1);
        }else{
            G= 255;
            R= (int) (510-5.1*normed_rating*100);
        }
        //return argb(1,R,G,B);
        int color = (A & 0xff) << 24 | (R & 0xff) << 16 | (G & 0xff) << 8 | (B & 0xff);
        Log.i(TAG, "getScaledColor: normed rating " + normed_rating + " color " + color);
        return color;

    }
}
