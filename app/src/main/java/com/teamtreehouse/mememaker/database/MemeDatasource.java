package com.teamtreehouse.mememaker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.teamtreehouse.mememaker.models.Meme;
import com.teamtreehouse.mememaker.models.MemeAnnotation;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Evan Anger on 8/17/14.
 */
public class MemeDatasource {




    private Context mContext;
    private MemeSQLiteHelper mMemeSqlLiteHelper;

    public MemeDatasource(Context context) {
        mContext = context;
        mMemeSqlLiteHelper = new MemeSQLiteHelper( context );
    }

    private SQLiteDatabase open(){
        return mMemeSqlLiteHelper.getWritableDatabase();
    }

    private void close( SQLiteDatabase database ){
        database.close();
    }

    public ArrayList<Meme> read(){
        ArrayList<Meme> memes = readMemes();
        addMemeAnnotation( memes );
        return memes;
    }

    public ArrayList<Meme> readMemes(){
        SQLiteDatabase sqLiteDatabase = open();

        Cursor cursor = sqLiteDatabase.query(
                MemeSQLiteHelper.MEME_TABLE,
                new String[]{ MemeSQLiteHelper.COLLUM_MEME_NAME, BaseColumns._ID, MemeSQLiteHelper.COLLUM_MEME_ASSET },
                null, //selection
                null, //selection args
                null, //group by
                null, //having
                MemeSQLiteHelper.COLLUM_MEME_CREATE_TABLE + " DESC"  //order by
        );

        ArrayList<Meme> memes = new ArrayList<Meme>();

        if ( cursor.moveToFirst() ){
            do{
                Meme meme = new Meme(
                        getIntFromColumnName( cursor, BaseColumns._ID ),
                        getStringFromColumnName( cursor, MemeSQLiteHelper.COLLUM_MEME_ASSET ),
                        getStringFromColumnName( cursor, MemeSQLiteHelper.COLLUM_MEME_NAME ),
                        null);

                memes.add( meme );
            }while ( cursor.moveToNext() );
        }

        cursor.close();
        close( sqLiteDatabase );

        return memes;
    }

    private void addMemeAnnotation(ArrayList<Meme> memes){
        SQLiteDatabase sqLiteDatabase = open();

        for ( Meme meme : memes ){
            ArrayList<MemeAnnotation> annotations = new ArrayList<MemeAnnotation>();
            Cursor cursor = sqLiteDatabase.rawQuery(
                    "SELECT * FROM " + MemeSQLiteHelper.ANNOTATIONS_TABLE + " WHERE " +
                            MemeSQLiteHelper.COLUMN_FOREIGN_KEY_MEME + " = " + meme.getId(),
                    null
            );

            if ( cursor.moveToFirst() ){
                do{
                    MemeAnnotation annotation = new MemeAnnotation(
                            getIntFromColumnName( cursor, BaseColumns._ID ),
                            getStringFromColumnName( cursor, MemeSQLiteHelper.COLUMN_ANNOTATION_COLOR ),
                            getStringFromColumnName( cursor, MemeSQLiteHelper.COLUMN_ANNOTATION_TITLE ),
                            getIntFromColumnName( cursor, MemeSQLiteHelper.COLUMN_ANNOTATION_X ),
                            getIntFromColumnName( cursor, MemeSQLiteHelper.COLUMN_ANNOTATION_Y )
                            );

                   annotations.add( annotation );
                }while ( cursor.moveToNext() );
            }

            meme.setAnnotations( annotations );
            cursor.close();
        }

        close( sqLiteDatabase );
    }

    private int getIntFromColumnName( Cursor cursor, String columnName ){
        int columnIndex = cursor.getColumnIndex( columnName );
        return cursor.getInt( columnIndex );
    }

    private String getStringFromColumnName( Cursor cursor, String columnName ){
        int columnIndex = cursor.getColumnIndex( columnName );
        return cursor.getString( columnIndex );
    }

    public void create(Meme meme){
        SQLiteDatabase sqLiteDatabase = open();
        sqLiteDatabase.beginTransaction();

        ContentValues memeValues = new ContentValues();
        memeValues.put( MemeSQLiteHelper.COLLUM_MEME_NAME, meme.getName() );
        memeValues.put( MemeSQLiteHelper.COLLUM_MEME_ASSET, meme.getAssetLocation() );
        memeValues.put( MemeSQLiteHelper.COLLUM_MEME_CREATE_TABLE, new Date().getTime() );
        long memeId = sqLiteDatabase.insert( MemeSQLiteHelper.MEME_TABLE, null, memeValues );

        try{
            for ( MemeAnnotation annotation : meme.getAnnotations() ){
                sqLiteDatabase.insert( MemeSQLiteHelper.ANNOTATIONS_TABLE, null, annotation.getContentValues( memeId ) );
            }
        }catch ( RuntimeException e ){
            e.printStackTrace();
        }


        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
        close( sqLiteDatabase );
    }

    public void update(Meme meme){
        SQLiteDatabase sqLiteDatabase = open();
        sqLiteDatabase.beginTransaction();

        ContentValues updateMemeValues = new ContentValues();
        updateMemeValues.put( MemeSQLiteHelper.COLLUM_MEME_NAME, meme.getName() );
        sqLiteDatabase.update( MemeSQLiteHelper.MEME_TABLE, updateMemeValues, String.format( "%s=%d", BaseColumns._ID, meme.getId() ), null );

        for( MemeAnnotation annotation : meme.getAnnotations() ){
            ContentValues annotationContentValues = annotation.getContentValues( meme.getId() );

            if ( annotation.hasBeenSaved() ){
                sqLiteDatabase.update( MemeSQLiteHelper.ANNOTATIONS_TABLE,
                        annotationContentValues,
                        String.format("%s=%d", BaseColumns._ID, annotation.getId()),
                        null );
            }
            else{
                sqLiteDatabase.insert( MemeSQLiteHelper.ANNOTATIONS_TABLE, null, annotationContentValues );
            }
        }

        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
        close( sqLiteDatabase );
    }

    public void delete( int memeId ){
        SQLiteDatabase sqLiteDatabase = open();
        sqLiteDatabase.beginTransaction();

        sqLiteDatabase.delete( MemeSQLiteHelper.ANNOTATIONS_TABLE,
                String.format( "%s=%s", MemeSQLiteHelper.COLUMN_FOREIGN_KEY_MEME, String.valueOf( memeId ) ),
                null );

        sqLiteDatabase.delete( MemeSQLiteHelper.MEME_TABLE,
                String.format( "%s=%s", BaseColumns._ID, String.valueOf( memeId ) ),
                null);


        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
        close( sqLiteDatabase );
    }
}
