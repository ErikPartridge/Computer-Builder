package com.computerevaluator.controllers;

import com.computerevaluator.models.Result;
import spark.Request;
import spark.Response;

/**
 * Created by erik on 6/19/16.
 */
public class ApiController{

    public static Response getResult(Request req, Response res){
        long id = Long.parseLong(req.url().split("\\/api\\/")[1]);
        System.out.println(id);
        res.type("application/json");
        res.status(200);
        Result result = Result.find(id);
        res.body(result.render());
        return res;
    }

}
