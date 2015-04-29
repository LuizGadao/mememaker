package com.teamtreehouse.mememaker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.teamtreehouse.mememaker.utils.StorageType;

/**
 * Created by Evan Anger on 8/13/14.
 */
public class MemeMakerApplicationSettings {

    SharedPreferences mSharedPreferences;

    public MemeMakerApplicationSettings(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );
    }

    public String getStoragePreference(){
        return mSharedPreferences.getString( "Preference", StorageType.INTERNAL );
    }

    public void setStoragePreference(String storageType){
        mSharedPreferences
                .edit()
                .putString( "Preference", storageType )
                .apply();
    }
}
