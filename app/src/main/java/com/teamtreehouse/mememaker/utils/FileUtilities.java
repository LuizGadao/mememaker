package com.teamtreehouse.mememaker.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Evan Anger on 7/28/14.
 */
public class FileUtilities {

    public static void saveAssetImage(Context context, String assetName) {
        File fileDirectory = getFileDirectory( context );
        File fileToWrite = new File(fileDirectory, assetName);

        if ( fileToWrite.exists() )
            return;

        AssetManager assetManager = context.getAssets();
        try{
            InputStream in = assetManager.open( assetName );
            FileOutputStream out = new FileOutputStream( fileToWrite );
            copyFile( in, out );

            in.close();
            out.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public static File getFileDirectory(Context context){
        String storageType = StorageType.PUBLIC_EXTERNAL;
        if ( storageType.equals( StorageType.INTERNAL ) )
            return context.getFilesDir();
        else{
            if ( isExternalStorageAvailable() ){
                if ( storageType.equals( StorageType.PRIVATE_EXTERNAL ) )
                    return context.getExternalFilesDir( null );
                else
                    return Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES );
            }
            else{
                return context.getFilesDir();
            }
        }
    }

    public static boolean isExternalStorageAvailable(){
        return Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED );
    }


    private static void copyFile(InputStream in, OutputStream out) throws IOException{
        byte[] buffer = new byte[1024];
        int read;
        while ( (read = in.read( buffer )) != -1 ){
            out.write( buffer, 0, read );
        }
    }

    public static File[] listFiles(Context context){
        File fileDir = getFileDirectory( context );
        File[] filesFiltered = fileDir.listFiles( new FileFilter() {
            @Override
            public boolean accept( File pathname ) {
                if ( pathname.getAbsolutePath().contains( ".jpg" ) )
                    return true;
                else
                    return false;
            }
        } );

        return filesFiltered;
    }

    public static Uri saveImageForSharing(Context context, Bitmap bitmap,  String assetName) {
        File fileToWrite = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), assetName);

        try {
            FileOutputStream outputStream = new FileOutputStream(fileToWrite);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return Uri.fromFile(fileToWrite);
        }
    }


    public static void saveImage(Context context, Bitmap bitmap, String name) {
        File fileDirectory = getFileDirectory( context );
        File fileToWrite = new File(fileDirectory, name);

        try {
            FileOutputStream outputStream = new FileOutputStream(fileToWrite);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
