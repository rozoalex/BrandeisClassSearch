package brandeisclasssearchproject.cs.brandies.edu.brandeisclasssearch.producers;
//package com.ZexiJin.JsoupGetHTMLTest.Test1;

/**
 * Created by Zexi Jin on 2016/11/6.
 *
 * INCOMPLETE!!!
 */

/**
 * recently undated, catches nullpointer exception
 *
 * to be tested
 *
 * josh 2016/11/11, happy single dog's day!
 */

import android.util.Log;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class ProducersTearcherInfo extends ProducersAbstract {
    private Document document;
    private ArrayList<String> Results;
    private String inputURL;
    private org.jsoup.nodes.Element content;
    private Elements list;

    public ProducersTearcherInfo(String URL) {
        this.inputURL = URL;
        this.Results = new ArrayList<>();
        CalcResult();
    }

    @Override
    public ArrayList<String> getResult() {
        return Results;
    }

    @Override
    public String getInput() {
        return inputURL;
    }

    private String convertSimpleString(String keyWord, int numTmp){
        content = document.getElementById(keyWord);
        if(content != null)
            if (content.text().length() <= numTmp)
                return content.text().substring(numTmp) + "\n";
            else
                return content.text() + "\n";
        else
            return "no information about " + keyWord + " found\n";
    }

    private String convertStringList(String keyWord, String tagName, int numTmp){
        content = document.getElementById(keyWord);
        if(content != null){
            list = content.getElementsByTag(tagName);
            String tmpString = "";
            for (org.jsoup.nodes.Element tmpNode : list){
                tmpString = tmpString + tmpNode.text() + "\n";
            }
            return tmpString.substring(numTmp);
        } else
            return "no information about " + keyWord + " found\n";
    }

    private String convertTeacherName(String keyWord, String tagName){
        content = document.getElementById(keyWord);
        if(content != null){
            list = content.getElementsByTag(tagName);
            org.jsoup.nodes.Element node = list.get(0);
            return node.text() + "\n";
        }
        else
            return "no information about " + keyWord + " found\n";
    }

    private void CalcResult(){
        try {
            this.document = Jsoup.connect(inputURL).get();
            Results.add(convertTeacherName("content", "a"));
            Results.add(convertStringList("depts", "a", 0));
            Results.add(convertSimpleString("degrees", 8));
            Results.add(convertSimpleString("expertise", 10));
            Results.add(convertSimpleString("profile", 8));
            Results.add(convertStringList("courses", "tr", 0));
            Results.add(convertStringList("awards", "p", 18));
            Results.add(convertStringList("scholarship", "p", 15));
        } catch (UnknownHostException e) {
            System.err.println("invalid URL");
        } catch (IOException e) {
            System.out.println("Construction failed");
        }
    }

}