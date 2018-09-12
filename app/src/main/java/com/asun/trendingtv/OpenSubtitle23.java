//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

//package Opensubs;
package com.asun.trendingtv;



import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import Opensubs.SubtitleInfo;

public class OpenSubtitle23 {
    private XmlRpcClient xmlRpcClient = new XmlRpcClient();
    private String strToken = "";

    OpenSubtitle23() {
        String MOVIE_EXTENSIONS = "mp4,mkv,avi,mov";
        String[] movieExtensionArray = MOVIE_EXTENSIONS.split(",");
        int var3 = movieExtensionArray.length;

        for (String extn : movieExtensionArray) {
            ArrayList movieFileExtensions = new ArrayList();
            movieFileExtensions.add(extn);
        }

        try {
            String OPEN_SUBTITLES_SERVER = "http://api.opensubtitles.org/xml-rpc";
            XmlRpcClientConfigImpl xmlRpcClientConfig = new XmlRpcClientConfigImpl();
            xmlRpcClientConfig.setServerURL(new URL(OPEN_SUBTITLES_SERVER));
            this.xmlRpcClient.setConfig(xmlRpcClientConfig);
        } catch (MalformedURLException var6) {
            var6.printStackTrace();
        }

    }

    String getMovieSubsByName(String moviename, String limit, String language) throws XmlRpcException {
        ArrayList infos = new ArrayList();
        ArrayList params = new ArrayList();
        String link = "";
        params.add(this.strToken);
        HashMap query = new HashMap();
        query.put("query", moviename);
        query.put("sublanguageid", language);
        HashMap query2 = new HashMap();
        query2.put("limit", limit);
        Object[] paramsArray = new Object[]{this.strToken, new Object[]{query}, query2};
        HashMap retVal = (HashMap)this.xmlRpcClient.execute("SearchSubtitles", paramsArray);
        System.out.println("Status code is " + retVal.get("status"));
        if(retVal.get("data") instanceof Object[]) {
            Object[] data = (Object[])retVal.get("data");

            for (Object aData : data) {
                SubtitleInfo info = new SubtitleInfo((HashMap) aData);
                System.out.println("Id is " + info.getIDMovieImdb());
                System.out.println("title is " + info.getMovieName());
                System.out.println("Link is " + info.getSubDownloadLink());
                System.out.println("Language is " + info.getLanguageName());
                System.out.println("IMDB rating is " + info.getMovieImdbRating());
                System.out.println("Year is " + info.getMovieYear());
                System.out.println("Sub file name " + info.getSubFileName());
                System.out.println("Date is " + info.getSubAddDate());
                System.out.println("Rating is " + info.getSubRating());
                System.out.println("Downloads is " + info.getSubDownloadsCnt());
                System.out.println("Actual CD name " + info.getSubActualCD());
                System.out.println("Bad is " + info.getSubBad());
                link = info.getSubDownloadLink();
                infos.add(info);
            }
        }

        return link;
    }

    public String getTvSeriesSubs(String TvseriesName, String season, String episode, String limit, String language) throws XmlRpcException {
        ArrayList infos = new ArrayList();
        ArrayList params = new ArrayList();
        String link= "";
        params.add(this.strToken);
        HashMap query = new HashMap();
        query.put("query", TvseriesName);
        query.put("season", season);
        query.put("episode", episode);
        query.put("sublanguageid", language);
        HashMap query2 = new HashMap();
        query2.put("limit", limit);
        Object[] paramsArray = new Object[]{this.strToken, new Object[]{query}, query2};
        HashMap retVal = (HashMap)this.xmlRpcClient.execute("SearchSubtitles", paramsArray);
        System.out.println("Status code is " + retVal.get("status"));
        if(retVal.get("data") instanceof Object[]) {
            Object[] data = (Object[])retVal.get("data");
            for (Object aData : data) {
                SubtitleInfo info = new SubtitleInfo((HashMap) aData);
                //  System.out.println("Id is " + info.IDMovieImdb);
                System.out.println("title is " + info.getMovieName());
                System.out.println(info.getSubDownloadLink());
                System.out.print("language " + info.getLanguageName());
                link = info.getSubDownloadLink();
                infos.add(info);
            }
        }

        return link;
    }

    String login() throws XmlRpcException {
        ArrayList params = new ArrayList();
        params.add("");
        params.add("");
        params.add("eng");
        params.add("moviejukebox 1.0.15");
        HashMap retVal = (HashMap)this.xmlRpcClient.execute("LogIn", params);
        this.strToken = (String)retVal.get("token");
        return this.strToken;
    }

    void logOut() {
        ArrayList params = new ArrayList();
        params.add(this.strToken);

        try {
            this.xmlRpcClient.execute("LogOut", params);
        } catch (XmlRpcException var3) {
            var3.printStackTrace();
        }

    }


}
