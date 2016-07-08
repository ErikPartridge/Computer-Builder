package com.computerevaluator.models;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.connection.Connection;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by erik on 7/6/16.
 */
public class Monitor extends Priced{

    public final String name;

    public Resolution resolution = Resolution.UNK;

    public Monitor(String name, String res, List<Price> prices){
        super(prices);
        switch (res) {
            case "tens": resolution = Resolution.FHDSmall; break;
            case "tenl": resolution = Resolution.FHDLarge; break;
            case "fourk": resolution = Resolution.UHD; break;
        }
        this.name = name;
    }

    private static  final MongoDatabase database = com.computerevaluator.services.Connection.database;


    public static Monitor find(String name){
        MongoCollection<Document> monitors = database.getCollection("monitors");
        Document raw = monitors.find(eq("name", name)).first();
        return parseFromJson(raw.toJson());

    }

    public static Monitor lowCost(Resolution resolution){
        String collName = "monitors";
        MongoCollection<Document> collection = database.getCollection(collName);
        List<Monitor> list = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().iterator()){
            while (cursor.hasNext()){
                Monitor monitor = parseFromJson(cursor.next().toJson());
                if (monitor.prices.size() > 0){
                    list.add(monitor);
                }
            }
        }
        list.sort((o1, o2) -> (int) (o1.lowPrice().price.doubleValue() - o2.lowPrice().price.doubleValue()));
        return list.stream().filter(monitor -> resolution == monitor.resolution).collect(Collectors.toList()).get(0);
    }


    public static Monitor parseFromJson(String json){
        JSONObject obj = new JSONObject(json);
        Priced priced = Priced.parsePrice(json);
        return new Monitor(obj.getString("name"), obj.getString("resolution"), priced.prices);
    }

    public String render(){
        String json = "{";
        if(resolution == Resolution.UNK || resolution == Resolution.FHDLarge || resolution == Resolution.FHDSmall)
            json += "\"resolution\": \"ten\"";
        else{
            json += "\"resolution\": \"four\"";
        }
        json += ",\"name\":\"" + name + "\"";

        json += ",\"low\":" + Math.round(lowPrice().price.doubleValue()) +
                ",\"source\":\"" + lowPrice().sourceStamp() + "\"" +
                ",\"url\":\"";
        if(!lowPrice().affiliate.equals(""))
            json += lowPrice().affiliate + "\"";
        else
            json += lowPrice().url + "\"";
        json +=  "}";
        return json;
    }
}


