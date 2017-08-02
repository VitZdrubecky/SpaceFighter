package cz.zdrubecky.spacefighter.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import cz.zdrubecky.spacefighter.R;

public class Boom {

    //mBitmap object
    private Bitmap mBitmap;

    //coordinate variables
    private int x;
    private int y;

    //constructor
    public Boom(Context context) {
        //getting boom image from drawable resource
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.boom);

        //setting the coordinate outside the screen
        //so that it won't shown up in the screen
        //it will be only visible for a fraction of second
        //after collission
        x = -250;
        y = -250;
    }

    //setters for x and y to make it visible at the place of collision
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    //getters
    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
