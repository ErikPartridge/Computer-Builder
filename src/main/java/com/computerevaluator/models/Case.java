package com.computerevaluator.models;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by erik on 7/6/16.
 */
public class Case extends Priced{


    public String size;

    public String name;

    public Case(List<Price> prices, String taille, String nom){
        super(prices);
        this.size = taille;
        this.name = nom;
    }

    private static final MongoDatabase database = com.computerevaluator.services.Connection.database;


    public static Case findCheapest(String size){
        String collName = "cases";
        MongoCollection<Document> collection = database.getCollection(collName);
        List<Case> list = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().iterator()){
            while (cursor.hasNext()){
                Case c = parseFromJson(cursor.next().toJson());
                if (c.prices.size() > 0){
                    list.add(c);
                }
            }
        }
        list.sort((o1, o2) -> (int) (o1.lowPrice().price.doubleValue() - o2.lowPrice().price.doubleValue()));
        return list.get(0);
    }


    public static Case find(String name){
        MongoCollection<Document> cases = database.getCollection("cases");
        Document raw = cases.find(eq("name", name)).first();
        return parseFromJson(raw.toJson());

    }

    public static Case parseFromJson(String json){
        JSONObject obj = new JSONObject(json);
        Priced priced = Priced.parsePrice(json);
        return new Case( priced.prices, obj.getString("size"), obj.getString("name"));
    }

    public String render(){
        String json = "{";
        json += "\"size\":\"" + size + "\"";
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
