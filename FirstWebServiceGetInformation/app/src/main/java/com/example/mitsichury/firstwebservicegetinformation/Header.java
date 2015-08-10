package com.example.mitsichury.firstwebservicegetinformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by MITSICHURY on 07/08/2015.
 */
public class Header implements Parcelable{
    private String title;
    private Date date;
    private Date dateParution;
    private String linkToImage;
    private Bitmap image;
    private String childDescription;
    private ExpandableListAdapter expandableListAdapter;


    @Override
    public String toString() {
        return "Header{" +
                "title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", linkToImage='" + linkToImage + '\'' +
                ", childDescription='" + childDescription + '\'' +
                ", linkToPage='" + linkToPage + '\'' +
                '}';
    }

    private String linkToPage;

    public String getTitle() {
        return title;
    }

    public Date getDate() {
        return date;
    }

    public String getLinkToImage() {
        return linkToImage;
    }

    public Bitmap getImage() {
        return image;
    }

    /**
     *  Constructor
     * @param title
     *      Title
     * @param date
     *      Date of publication
     * @param linkToImage
 *      Link to the image
     * @param childDescription
     * @param linkToPage
     */
    public Header(String title, String date, String linkToImage, String childDescription, String linkToPage, String dateParution) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        //SimpleDateFormat formaterDatePub = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        Date dateTmp = null;
        try {
            dateTmp = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.title = title;
        this.date = dateTmp;
        this.linkToImage = linkToImage;
        this.childDescription = childDescription;
        Log.i("TITLE", childDescription.toString());
        this.linkToPage = linkToPage;
        startDlThumb();

        /*dateTmp = null;
        try {
            dateTmp = formaterDatePub.parse(dateParution);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.dateParution = dateTmp;*/
    }

    public String getChildDescription() {
        return childDescription;
    }

    public String getLinkToPage() {
        return linkToPage;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public ExpandableListAdapter getExpandableListAdapter() {
        return expandableListAdapter;
    }

    public void setExpandableListAdapter(ExpandableListAdapter expandableListAdapter) {
        this.expandableListAdapter = expandableListAdapter;
    }

    public void startDlThumb(){
        if(image==null){new AsyncDl().execute(linkToImage);}
    }

    public Date getDateParution() {
        return dateParution;
    }














    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(childDescription);
        dest.writeString(linkToImage);
        dest.writeString(linkToPage);
        dest.writeString(title);
        dest.writeValue(image);
        
        dest.writeValue(date);
    }


















    private class AsyncDl extends AsyncTask {

        @Override
        protected Bitmap doInBackground(Object[] params) {
            //Log.i("ASYNC", "DEBUT de "+ title);
            return dlBitmap((String) params[0]);
        }

        @Override
        protected void onPostExecute(Object o) {
            //super.onPostExecute(o);
            image = ((Bitmap)o);
            if(expandableListAdapter != null)expandableListAdapter.notifyDataSetChanged();
            //Log.i("ASYNC", "FIN de "+title+" "+((Bitmap)o==null));
        }

        private Bitmap dlBitmap(String linkToImage) {
            Bitmap bmp = null;
            try {
                bmp = BitmapFactory.decodeStream((InputStream) new URL(linkToImage).getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("DLL", "DLL de "+title+" "+(bmp==null));
            return bmp;
        }
    }
}


