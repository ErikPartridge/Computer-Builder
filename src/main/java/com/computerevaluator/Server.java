package com.computerevaluator;

import com.computerevaluator.controllers.ApiController;
import com.computerevaluator.controllers.ViewController;


import static spark.Spark.*;
import static spark.Spark.after;
import static spark.Spark.get;

import static spark.Spark.*;

/**
 * Created by erik on 6/19/16.
 */
public class Server{

    public static void main(String[] args){
        staticFiles.location("/public");
        //staticFiles.expireTime(600);
        int maxThreads = 12;
        int minThreads = 1;
        int timeOutMillis = 30000;
        threadPool(maxThreads, minThreads, timeOutMillis);

        get("/api/:id", ApiController::getResult);
        get("/", ViewController::getIndex);
        post("/process", ViewController::process);
        get("/result/:id", ViewController::getResult);
        after((request, response) -> response.header("Content-Encoding", "gzip"));


    }
}
