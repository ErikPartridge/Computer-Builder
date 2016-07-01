package com.computerevaluator.models;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONObject;
import com.computerevaluator.services.Connection;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by erik on 5/15/16.
 */
public class Gpu extends Priced{


    private static List<Gpu> gpus = Collections.synchronizedList(new ArrayList<>());
    private static long timestamp = 0;

    public Gpu(String name, String shortname, List<Price> prices, int fps, int threedmark, int power){
        super (prices);
        this.name = name;
        this.shortname = shortname;
        this.fps = fps;
        this.threedmark = threedmark;
        this.power = power;
    }

    public final String name;

    public final String shortname;

    public final int fps;

    public final int threedmark;

    public final int power;

    private static final MongoDatabase database = Connection.database;

    private static Gpu parseFromJson(String json){
        JSONObject obj = new JSONObject(json);
        Priced priced = Priced.parsePrice(json);
        return new Gpu(obj.getString("name"), obj.getString("short"), priced.prices, obj.getInt("fps"), obj.getInt("threedmark"), obj.getInt("power"));
    }

    public String toString(){
        return "{\"name\" :\"" + name + "\", \"short\":\"" + shortname + "\"}";
    }

    public static List<Gpu> list(){
        if(gpus.size() > 0 && System.currentTimeMillis() - timestamp < 10000){
            return gpus;
        }else{
            timestamp = System.currentTimeMillis();
            String collName = "gpus";
            MongoCollection<Document> collection = database.getCollection(collName);
            List<Gpu> list = new ArrayList<>();
            try (MongoCursor<Document> cursor = collection.find().iterator()){
                while (cursor.hasNext()){
                    Gpu gpu = parseFromJson(cursor.next().toJson());
                    if (gpu.prices.size() > 0)
                        list.add(gpu);
                }
            }
            gpus =list;
            return list;
        }
    }

    public static Price lowPrice(String shortname){
        if(shortname.equals("already")){
            return new Price("--Already Purchased--", System.currentTimeMillis(), "#", new BigDecimal(0), "");
        }
        MongoCollection<Document> gpus = database.getCollection("gpus");
        Document gpuDoc = gpus.find(eq("short", shortname)).first();
        Gpu gpu = parseFromJson(gpuDoc.toJson());
        return gpu.lowPrice();
    }

    public static Gpu find (String shortname){
        MongoCollection<Document> gpus = database.getCollection("gpus");
        Document raw = gpus.find(eq("short", shortname)).first();
        return parseFromJson(raw.toJson());
    }

    public static BigDecimal medianPrice(String shortname){
        if(shortname.equals("already")){
            return new BigDecimal(0);
        }
        MongoCollection<Document> gpus = database.getCollection("gpus");
        Document gpuDoc = gpus.find(eq("short", shortname)).first();
        Gpu gpu = parseFromJson(gpuDoc.toJson());
        List<Price> prices = gpu.prices;
        return Price.median(prices);
    }

    public String render(){
        String json =  "\"gpu\":{" +
                "\"name\":\"" + name + "\"" +
                ",\"median\":" + Math.round(medianPrice(shortname).doubleValue()) +
                ",\"low\":" + Math.round(lowPrice(shortname).price.doubleValue()) +
                ",\"source\":\"" + lowPrice(shortname).sourceStamp() + "\"" +

                ",\"url\":\"";
        if(!lowPrice(shortname).affiliate.equals(""))
            json += lowPrice(shortname).affiliate + "\"";
        else
            json += lowPrice(shortname).url + "\"";
        json +=  "}";
        return json;
    }
}

