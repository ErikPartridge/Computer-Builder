package com.computerevaluator.controllers;

import com.computerevaluator.ai.Engine;
import com.computerevaluator.models.Result;
import com.computerevaluator.models.Settings;
import com.computerevaluator.models.Size;
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
        Result result = new Result();
        //Do some parsing here
        double cpu = Double.parseDouble(req.queryParams("cpu"));
        double gpu = Double.parseDouble(req.queryParams("gpu"));
        double disk = Double.parseDouble(req.queryParams("disk"));
        double multi = Double.parseDouble(req.queryParams("multi"));
        double space = Double.parseDouble(req.queryParams("space"));
        double newBias = Double.parseDouble(req.queryParams("new"));
        int budget = Integer.parseInt(req.queryParams("budget"));

        //Then start the thread
        new Thread(new Engine(new Settings(cpu, gpu, budget, budget + 50, disk, multi, Size.ATX), result.id)).start();
        Result.save(result);
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
