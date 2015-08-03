package com.example.mitsichury.simplemp3player;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by MITSICHURY on 30/07/2015.
 */
public class Scanner {


    /** To store the available media files */
    private ArrayList<String> trackList = new ArrayList<String>();
    private ArrayList<String> trackListPath = new ArrayList<String>();

    public Scanner() {
        String externalStoragePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath();

        File targetDir = new File(externalStoragePath);

        Log.i("externalStoragePath :::", targetDir.getAbsolutePath());
        File[] trackList = targetDir.listFiles();

        scanFiles(trackList);

        if(new File("/storage/sdcard1").exists()){
            externalStoragePath = "/storage/sdcard1";

            targetDir = new File(externalStoragePath);

            trackList = targetDir.listFiles();

            scanFiles(trackList);
        }

    }

    /**
     * scanFiles
     *
     * @param scanFiles
     */
    public void scanFiles(File[] scanFiles) {

        if (scanFiles != null) {
            for (File file : scanFiles) {

                /*if(trackList.size() > 4){
                    return;
                }*/

                if (file.isDirectory()) {
                    scanFiles(file.listFiles());

                } else {

                    addToMediaList(file);

                }

            }
        } else {

            Log.d("SCANNER",
                    " *************** No file  is available ***************");

        }
    }




    /**
     *
     * @param file
     */

    private void addToMediaList(File file) {

        if (file != null) {

            String path = file.getAbsolutePath();

            String name = file.getName();

            int index = path.lastIndexOf(".");

            String extn = path.substring(index + 1, path.length());

            if (extn.equalsIgnoreCase("mp3") || extn.equalsIgnoreCase("m4a")) {

                Log.d(" scanned File ::: ", file.getAbsolutePath()
                        + "  file.getPath( )  " + file.getPath());
                Log.d("SCANNER", " ***** above file is added to list ");
                trackList.add(name);
                trackListPath.add(path);
            }
        }

    }

    public ArrayList<String> getTrackList(){
        return trackList;
    }

    public ArrayList<String> getTrackListPath(){
        return trackListPath;
    }
}
