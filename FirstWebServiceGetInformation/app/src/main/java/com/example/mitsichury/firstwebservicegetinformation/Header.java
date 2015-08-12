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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
     *  Constructor, start the asynck downloading of thumbnails automatically at the instanciation
     * @param title
     *      Title
     * @param date
     *      Date of publication
     * @param linkToImage
     *     Link to the image
     * @param childDescription
     *      Show description
     * @param linkToPage
     *      Link to web Page
     */
    public Header(String title, String date, String linkToImage, String childDescription, String linkToPage, String dateParution) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.FRENCH);
        SimpleDateFormat formaterDatePub = new SimpleDateFormat("yyyy-MM-dd", Locale.FRENCH);

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

        this.linkToPage = linkToPage;
        startDlThumb();

        dateTmp = null;
        if (dateParution != null) {
            try {
                dateTmp = formaterDatePub.parse(dateParution);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else {
            dateTmp = Calendar.getInstance().getTime();
        }

        this.dateParution = dateTmp;

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

    /**
     * Method which specify what has to be save
     */
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
            return dlBitmap((String) params[0]);
        }

        @Override
        protected void onPostExecute(Object o) {
            image = ((Bitmap)o);
            // If the expandableListAdapter doesn't exist means that the adapter is not created and will take the bitmap automatically
            // Else the adpater already exist and we need to notify that the data has changed
            if(expandableListAdapter != null)expandableListAdapter.notifyDataSetChanged();
        }

        private Bitmap dlBitmap(String linkToImage) {
            Bitmap bmp = null;
            try {
                bmp = BitmapFactory.decodeStream((InputStream) new URL(linkToImage).getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmp;
        }
    }
}


