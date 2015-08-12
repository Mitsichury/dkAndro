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


    private XmlPullParserFactory xmlFactoryObject; // Allow to create new instance a a parser
    public volatile boolean parsingComplete = false; // volatile means that the class can be used by multiple thraed and so not save this boolean in the main memory

    /**
     * Contructor with the link to dl
     * @param url
     *      The link
     */
    public ParserXML(String url){
        this.urlString = url;
    }

    /**
     * The parser
     * @param myParser
     *      The XmlPullParser wich contains the XML data to parse
     */
    public void parse(XmlPullParser myParser){
        int event;
        String text = null;

        try {
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
                        if(name.equals("item")){link=null;}
                        Log.i("parseName", name);
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
                if(title != null && description!= null && link!= null) {
                    headers.add(new Header(title.replace(title.substring(title.indexOf("("), title.lastIndexOf(")") + 1), ""),
                            title.substring(title.indexOf("(") + 1, title.lastIndexOf(")")),
                            description.substring(description.indexOf("<img src=") + 10, description.indexOf("\" ")),
                            Html.fromHtml(description.replaceAll("<img.+?>", "")).toString(),
                            link,
                            date
                    ));
                    title = null;
                    date = null;
                    description = null;
                    link = null;
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
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Set the connection
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setReadTimeout(10000 /* milliseconds */);
                    connection.setConnectTimeout(15000 /* milliseconds */);
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);

                    connection.connect();

                    // Get stream
                    InputStream stream = connection.getInputStream();

                    // Set the parser
                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser myparse = xmlFactoryObject.newPullParser();

                    myparse.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    myparse.setInput(stream, null);

                    // Parse the file
                    parse(myparse);

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
