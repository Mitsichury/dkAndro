package com.example.mitsichury.firstwebservicegetinformation;

import android.location.LocationListener;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by MITSICHURY on 07/08/2015.
 */
public class ParserXML {
    private String title = null;
    private String link = null;
    private String date = null;
    private String description = null;
    private String urlString = null;

    ArrayList<Header> headers = new ArrayList<>();

    // Don't know how it works ???
    // TODO : understand
    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parsingComplete = false;

    public ParserXML(String url){
        this.urlString = url;;
        Log.i("parser", "entry");
    }

    public void parse(XmlPullParser myParser){
        Log.i("parser", "Entry parse");
        int event;
        String text = null;

        try {
            while (myParser.getName()==null || !myParser.getName().equals("item")){myParser.next();}
            event = myParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                String name = myParser.getName();

                switch (event) {
                    case XmlPullParser.START_TAG:
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        //Log.i("parseName", name);
                        if (name.equals("title")) {
                            title = text;
                        } else if (name.equals("link")) {
                            link = text;
                        } else if (name.equals("description")) {
                            description = text;
                        } else if(name.equals("pubDate")) {
                            date = text;
                        }
                        break;
                }

                event = myParser.next();
                if(title != null && description!= null){
                    Log.i("parseName", title + " " + link);
                    Log.i("description", description);
                    /*Pattern p = Pattern.compile("([0-9]{2}[ .-]){4}[0-9]{2}");

                    Matcher m = p.matcher(description);
                    if(m.find()) {
                        for (int i=0; i<m.groupCount(); i++){
                            description = description.replace(m.group(0), "<a href=tel:\""+m.group(0)+"\"/>");
                        }
                    }*/
                    headers.add(new Header(title.replace(title.substring(title.indexOf("("), title.lastIndexOf(")")+1), ""),
                            title.substring(title.indexOf("(")+1, title.lastIndexOf(")")),
                            description.substring(description.indexOf("<img src=")+10, description.indexOf("\" ")),
                            Html.fromHtml(description.replaceAll("<img.+?>", "")).toString(),
                            link,
                            date
                    ));
                    title=null;
                    date=null;
                    description=null;
                    link=null;
                }
            }

            parsingComplete = true;
        }catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void dlXMLfile(){
        Log.i("parser", "Entry dl");
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setReadTimeout(10000 /* milliseconds */);
                    connection.setConnectTimeout(15000 /* milliseconds */);
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);

                    connection.connect();

                    InputStream stream = connection.getInputStream();

                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser myparse = xmlFactoryObject.newPullParser();

                    myparse.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    myparse.setInput(stream, null);

                    Log.i("parser", "call parse"+myparse.getText());
                    parse(myparse);
                    Log.i("parser", "end call parse");

                    stream.close();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
    }

    ArrayList<Header> getData(){
        return this.headers;
    }

    boolean isParsingComplete(){
        return this.parsingComplete;
    }

}
