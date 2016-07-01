package com.computerevaluator.models;

import com.computerevaluator.services.Connection;
import org.bson.Document;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

/**
 * Created by erik on 6/15/16.
 */
public class Result{


    private volatile static long safeId = System.currentTimeMillis();

    public final long id;

    public final boolean finished;

    public final Computer result;

    public final long timestamp;

    public Result(){
        safeId ++;
        this.id = safeId;
        this.finished = false;
        this.result = null;
        this.timestamp = System.currentTimeMillis();

    }

    public Result(long id, boolean done, Computer res){
        this.id = id;
        this.finished = done;
        this.result = res;
        this.timestamp = System.currentTimeMillis();
    }

    public Result(long id){
        this.id = id;
        this.finished = false;
        this.result = null;
        this.timestamp = System.currentTimeMillis();
    }

    public static Result find(long id){
        return parseFromBson(Connection.resultDb.getCollection("results").find(eq("uuid", id)).first());
    }

    private static Result parseFromBson(Document document){
        String json = document.toJson();
        JSONObject obj = new JSONObject(json);
        if(!document.getBoolean("done"))
            return new Result(document.getLong("uuid"));
        Computer computer = Computer.fromJson(obj.getJSONObject("computer"));
        return new Result(document.getLong("uuid"), true, computer);
    }

    public static void addComputer(long id, Computer c){
        Document comp = new Document();
        comp.append("cpu", c.cpu.shortname).append("gpu", c.gpu.shortname).append("motherboard", c.motherboard.name).append("ram", c.ram.getName()).append("power", c.power.name);
        comp.append("boot", c.bootDrive.name);
        if(c.secondaryDrive != null){
            comp.append("secondary", c.secondaryDrive.name);
        }
        Connection.resultDb.getCollection("results").updateOne(eq("uuid", id), set("computer", comp));
        Connection.resultDb.getCollection("results").updateOne(eq("uuid", id), set("done", true));
        System.out.println("Updated status");
    }

    public static void save(Result result){
        final Computer c = result.result;
        Document comp = new Document();
        Document res = new Document("uuid", result.id).append("done", result.finished);
        if(c == null){
            Connection.resultDb.getCollection("results").insertOne(res);
            return;
        }
        comp.append("cpu", c.cpu.shortname).append("gpu", c.gpu.shortname).append("motherboard", c.motherboard.name).append("ram", c.ram.getName()).append("power", c.power.name);
        comp.append("boot", c.bootDrive.name);
        if(c.secondaryDrive != null){
            comp.append("secondary", c.secondaryDrive.name);
        }
        Connection.resultDb.getCollection("results").insertOne(res.append("computer", comp));
    }

    public String render(){
        if(!finished || result == null){
            return "{\"done\":false}";
        }else{
            String json = "{" + "\"done\":true,";
            assert result != null;
            json += result.render() + "}";
            return json;
        }
    }
}
