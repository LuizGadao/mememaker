package com.teamtreehouse.mememaker.models;

import android.content.ContentValues;

import com.teamtreehouse.mememaker.database.MemeSQLiteHelper;

import java.io.Serializable;

/**
 * Created by Evan Anger on 8/17/14.
 */
public class MemeAnnotation implements Serializable {
    private int mId = -1;
    private String mColor;
    private String mTitle;
    private int mLocationX;
    private int mLocationY;

    public MemeAnnotation() {

    }
    public MemeAnnotation(int id, String color, String title, int locationX, int locationY) {
        mId = id;
        mColor = color;
        mTitle = title;
        mLocationX = locationX;
        mLocationY = locationY;
    }

    public int getId() { return mId; }
    public boolean hasBeenSaved() { return (getId() != -1); }

    public String getColor() { return mColor; }
    public void setColor(String color) {
        mColor = color;
    }

    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String text) { mTitle = text; }

    public int getLocationX() {
        return mLocationX;
    }
    public void setLocationX(int x) {
        mLocationX = x;
    }

    public int getLocationY() {
        return mLocationY;
    }
    public void setLocationY(int y) {
        mLocationY = y;
    }

    public ContentValues getContentValues(){
        ContentValues annotationValues = new ContentValues();
        annotationValues.put( MemeSQLiteHelper.COLUMN_ANNOTATION_TITLE, this.getTitle() );
        annotationValues.put( MemeSQLiteHelper.COLUMN_ANNOTATION_COLOR, this.getColor() );
        annotationValues.put( MemeSQLiteHelper.COLUMN_ANNOTATION_X, this.getLocationX() );
        annotationValues.put( MemeSQLiteHelper.COLUMN_ANNOTATION_Y, this.getLocationY() );

        return annotationValues;
    }

    public ContentValues getContentValues( long memeId ){
        ContentValues annotationValues = getContentValues();
        annotationValues.put( MemeSQLiteHelper.COLUMN_FOREIGN_KEY_MEME, memeId );

        return annotationValues;
    }
}

