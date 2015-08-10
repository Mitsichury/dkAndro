package com.example.mitsichury.firstwebservicegetinformation;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by MITSICHURY on 07/08/2015.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter{
    /**
     * A Custom adapter to create Parent view (Used grouprow.xml) and Child View((Used childrow.xml).
     */

    private LayoutInflater inflater;
    private ArrayList<Header> headers;
    private Context context;

    public ExpandableListAdapter(Context context, ArrayList<Header> headers)
    {
        // Create Layout Inflator
        this.context = context;
        this.headers = headers;
        this.inflater = LayoutInflater.from(context);
    }


    // This Function used to inflate parent rows view

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parentView)
    {
        final Header parent = headers.get(groupPosition);

        // Inflate grouprow.xml file for parent rows
        convertView = inflater.inflate(R.layout.list_group, parentView, false);

        // Get grouprow.xml file elements and set values
        ((TextView) convertView.findViewById(R.id.tv_title)).setText(parent.getTitle());
        if(parent.getTitle().contains("Annul")){((TextView) convertView.findViewById(R.id.tv_title)).setTextColor(0xffaa0000);}

        SimpleDateFormat formater = new SimpleDateFormat("dd MMM yyyy");

        ((TextView) convertView.findViewById(R.id.tv_pubDate)).setText(formater.format(parent.getDate()));
        ImageView image=(ImageView)convertView.findViewById(R.id.imageView);

         // TODO Make an asynchronous dl
        if (parent.getImage()==null){
            parent.setExpandableListAdapter(this);
            //parent.startDlThumb();
        }

        image.setImageBitmap(parent.getImage());
        //Log.i("EXP-IMG", ""+parent.getImage());
        return convertView;
    }


    // This Function used to inflate child rows view
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parentView)
    {
        final Header parent = headers.get(groupPosition);
        final String child = parent.getChildDescription();

        // Inflate childrow.xml file for child rows
        convertView = inflater.inflate(R.layout.list_item, parentView, false);

        // Get childrow.xml file elements and set values
        ((TextView) convertView.findViewById(R.id.tv_description)).setText(child);

        return convertView;
    }


    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        //Log.i("Childs", groupPosition+"=  getChild =="+childPosition);
        return headers.get(groupPosition).getChildDescription();
    }

    //Call when child row clicked
    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        /****** When Child row clicked then this function call *******/

        //Log.i("Noise", "parent == "+groupPosition+"=  child : =="+childPosition);


        //Toast.makeText(context, "Parent :" + groupPosition + " Child :" + childPosition, Toast.LENGTH_LONG).show();

        return childPosition;
    }

    // TODO make a function to calculate the number of child if layout evolve
    @Override
    public int getChildrenCount(int groupPosition)
    {
        return 1;
    }


    @Override
    public Object getGroup(int groupPosition)
    {
        Log.i("Parent", groupPosition + "=  getGroup ");

        return headers.get(groupPosition);
    }

    @Override
    public int getGroupCount()
    {
        return headers.size();
    }

    //Call when parent row clicked
    // TODO : understand the function
    @Override
    public long getGroupId(int groupPosition)
    {
        /*Log.i("Parent", groupPosition+"=  getGroupId "+ParentClickStatus);

        if(groupPosition==2 && ParentClickStatus!=groupPosition){

            //Alert to user
            Toast.makeText(getApplicationContext(), "Parent :"+groupPosition ,
                    Toast.LENGTH_LONG).show();
        }

        ParentClickStatus=groupPosition;
        if(ParentClickStatus==0)
            ParentClickStatus=-1;*/

        return groupPosition;
    }

    @Override
    public void notifyDataSetChanged()
    {
        // Refresh List rows
        super.notifyDataSetChanged();
    }

    @Override
    public boolean isEmpty()
    {
        return ((headers == null) || headers.isEmpty());
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }

    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }

}

