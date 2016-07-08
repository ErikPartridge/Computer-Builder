package com.computerevaluator.controllers;

import com.computerevaluator.ai.Engine;
import com.computerevaluator.models.*;
import org.apache.commons.io.IOUtils;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.DoubleSummaryStatistics;

import static spark.Spark.halt;

/**
 * Created by erik on 6/19/16.
 */

public class ViewController{

    public static Response getIndex(Request req, Response res){
        try {
            InputStream stream = ViewController.class.getClassLoader().getResourceAsStream("index.html");
            String html = IOUtils.toString(stream, "UTF-8");
            stream.close();
            res.status(200);
            res.body(html);
            return res;
        } catch (IOException e){
            System.out.println(e);
            halt(500, "<html>Unable to read the html file, error thrown. Please try again soon.</html>");
            return res;
        }
    }

    public static Response process(Request req, Response res){
        //Do some parsing here
        double cpu = Double.parseDouble(req.queryParams("cpu"));
        double gpu = Double.parseDouble(req.queryParams("gpu"));
        double disk = Double.parseDouble(req.queryParams("disk"));
        double multi = Double.parseDouble(req.queryParams("multi"));
        double space = Double.parseDouble(req.queryParams("space"));
        double newBias = Double.parseDouble(req.queryParams("new"));
        int budget = Integer.parseInt(req.queryParams("budget"));
        String caseSize = req.queryParams("case");
        String monitor = req.queryParams("monitor");
        Case c = Case.findCheapest(caseSize);
        Resolution resolution = Resolution.UNK;
        Monitor m = null;
        switch (monitor) {
            case "tens": resolution = Resolution.FHDSmall; break;
            case "tenl": resolution = Resolution.FHDLarge; break;
            case "fourk": resolution = Resolution.UHD; break;
        }
        if(!monitor.equals("no")){
            m = Monitor.lowCost(resolution);
        }
        if(c != null)
            budget -= c.lowPrice().price.doubleValue();
        if(m != null)
            budget -= m.lowPrice().price.doubleValue();

        Result result = new Result(m,c);
        //Then start the thread
        Result.save(result);
        Thread t = new Thread(new Engine(new Settings(cpu, gpu, budget, budget + 50, disk, multi, newBias, space, Size.ATX), result.id));
        t.start();
        res.redirect("/result/c" + result.id);
        return res;
    }

    public static Response getResult(Request req, Response res){
        try {
            InputStream stream = ViewController.class.getClassLoader().getResourceAsStream("result.html");
            String html = IOUtils.toString(stream, "UTF-8");
            stream.close();
            res.status(200);
            res.body(html);
            return res;
        } catch (IOException e){
            System.out.println(e);
            halt(500, "<html>Unable to read the html file, error thrown. Please try again soon.</html>");
            return res;
        }
    }

    public static Response getTerms(Request req, Response res){
        try {
            InputStream stream = ViewController.class.getClassLoader().getResourceAsStream("terms.html");
            String html = IOUtils.toString(stream, "UTF-8");
            stream.close();
            res.status(200);
            res.body(html);
            return res;
        } catch (IOException e){
            System.out.println(e);
            halt(500, "<html>Unable to read the html file, error thrown. Please try again soon.</html>");
            return res;
        }
    }
}
