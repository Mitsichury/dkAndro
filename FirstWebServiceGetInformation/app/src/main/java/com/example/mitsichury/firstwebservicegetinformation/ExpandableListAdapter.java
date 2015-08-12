package com.example.mitsichury.firstwebservicegetinformation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by MITSICHURY on 07/08/2015.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter{

    private LayoutInflater inflater;
    private ArrayList<Header> headers;
    private Context context;

    public ExpandableListAdapter(Context context, ArrayList<Header> headers)
    {

        this.context = context;
        this.headers = headers;

        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parentView)
    {
        final Header parent = headers.get(groupPosition);

        convertView = inflater.inflate(R.layout.list_group, parentView, false);

        ((TextView) convertView.findViewById(R.id.tv_title)).setText(parent.getTitle());

        if(parent.getTitle().contains("Annul")){((TextView) convertView.findViewById(R.id.tv_title)).setTextColor(0xffaa0000);}

        SimpleDateFormat formater = new SimpleDateFormat("dd MMM yyyy");

        ((TextView) convertView.findViewById(R.id.tv_pubDate)).setText(formater.format(parent.getDate()));
        ImageView image=(ImageView)convertView.findViewById(R.id.imageView);

        final ImageButton addCalendar = (ImageButton) convertView.findViewById(R.id.imgButton);
        addCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar beginTime = Calendar.getInstance();
                beginTime.setTime(parent.getDate());
                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                        .putExtra(CalendarContract.Events.TITLE, parent.getTitle())
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, "Zenith Limoges")
                        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                        .putExtra(CalendarContract.Events.ALL_DAY, true)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });

        // Allow the group to be click
        addCalendar.setFocusable(false);

        if (parent.getImage()==null){
            parent.setExpandableListAdapter(this);
        }

        image.setImageBitmap(parent.getImage());

        return convertView;
    }


    // This Function used to inflate child rows view
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parentView)
    {
        final Header parent = headers.get(groupPosition);
        final String child = parent.getChildDescription()+"Pour plus d'information : "+parent.getLinkToPage();

        convertView = inflater.inflate(R.layout.list_item, parentView, false);

        TextView desc = (TextView) convertView.findViewById(R.id.tv_description);

        Pattern p = Pattern.compile("([0-9]{2}[ .-]){4}[0-9]{2}");
        final Matcher m = p.matcher(child);

        final Pattern url = Pattern.compile(String.valueOf(Patterns.WEB_URL));
        final Matcher urlGo = url.matcher(child);

        SpannableString text = new SpannableString(child);

        while(m.find()) {
            final String tmp = m.group(0);
            text.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                    Intent i = new Intent(Intent.ACTION_DIAL);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setData(Uri.parse("tel:" + tmp));
                    context.startActivity(i);
                }
            }
                    , child.indexOf(tmp), child.indexOf(tmp) + tmp.toString().length(), 0);
        }

        while (urlGo.find()){
            final String tmp = urlGo.group(0);
            text.setSpan(
                new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if(tmp.startsWith("http://") || tmp.startsWith("https://")){intent.setData(Uri.parse(tmp));}
                        else{intent.setData(Uri.parse("https://www.google.fr/#q="+tmp));}


                        context.startActivity(intent);
                    }
                }
                , child.indexOf(tmp), child.indexOf(tmp)+tmp.length(), 0);
        }

        desc.setMovementMethod(LinkMovementMethod.getInstance());
        desc.setText(text, TextView.BufferType.SPANNABLE);

        return convertView;
    }


    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return headers.get(groupPosition).getChildDescription();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return 1;
    }


    @Override
    public Object getGroup(int groupPosition)
    {
        return headers.get(groupPosition);
    }

    @Override
    public int getGroupCount()
    {
        return headers.size();
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public void notifyDataSetChanged()
    {
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

