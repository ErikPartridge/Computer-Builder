package com.computerevaluator.models;

import com.jcabi.aspects.Cacheable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONObject;
import com.computerevaluator.services.Connection;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by erik on 5/16/16.
 */
public class Motherboard extends Priced{

    public final String name;

    public final String formFactor;

    public final String socket;

    public final String ram;


    private Motherboard(String name, String formFactor, List<Price> prices, String socket, String ram){
        super(prices);
        this.name = name;

        this.formFactor = formFactor;
        this.socket = socket;
        this.ram = ram;
    }


    private static final MongoDatabase database = Connection.database;


    @Cacheable(lifetime = 30, unit = TimeUnit.SECONDS)
    public static List<Motherboard> list(){
        MongoCollection<Document> mobos = database.getCollection("motherboards");
        List<Motherboard> motherboards = new ArrayList<>();
        try (MongoCursor<Document> cursor = mobos.find().iterator()){
            while (cursor.hasNext()){
                Motherboard mobo = parseFromJson(cursor.next().toJson());
                if(mobo.prices.size() > 0)
                    motherboards.add(mobo);
            }
        }
        return motherboards;

    }

    public static Motherboard find(String name){
        MongoCollection<Document> mobos = database.getCollection("motherboards");
        Document raw = mobos.find(eq("name", name)).first();
        return parseFromJson(raw.toJson());
    }

    public static Price lowPrice(String name){
        if(name.startsWith("stock")){
            return lowInGroup(name.replaceAll("stock-", "")).lowPrice();
        }else if(name.equals("already")){
            return new Price("--Already Purchased--", System.currentTimeMillis(), "#", new BigDecimal(0), "");
        }else{
            return Price.low(find(name).prices);
        }
    }

    public static BigDecimal medianPrice(String name){
        if(name.startsWith("stock")){
            List<Motherboard> mobos = motherboardsByGroup(name.replaceAll("stock-", ""));
            List<Price> prices = new ArrayList<>();
            mobos.forEach(m -> prices.addAll(m.prices));
            return Price.median(prices);
        }else if(name.equals("already")){
            return new BigDecimal(0);
        }else{
            MongoCollection<Document> motherboards = database.getCollection("motherboards");
            Document moboDoc = motherboards.find(eq("name", name)).first();
            Motherboard motherboard = parseFromJson(moboDoc.toJson());
            List<Price> prices = motherboard.prices;
            return Price.median(prices);
        }

    }

    private static List<Motherboard> motherboardsByGroup(String group){
        MongoCollection<Document> mobos = database.getCollection("motherboards");
        String expanded = "";
        switch(group) {
            case "matx" : expanded = "MicroATX";break;
            case "mitx" : expanded = "MicroITX";break;
            case "atx"  : expanded = "ATX"; break;
        }
        List<Motherboard> matching = new ArrayList<>();
        mobos.find(eq("formFactor", expanded)).forEach((Consumer<Document>) document -> matching.add(parseFromJson(document.toJson())));
        return matching;
    }

    private static Motherboard lowInGroup(String group){
        List<Motherboard> matching = motherboardsByGroup(group);
        matching.sort((o1, o2) -> (int)(o1.lowPrice().price.doubleValue() - o2.lowPrice().price.doubleValue()));
        return matching.get(0);
    }

    private static Motherboard parseFromJson(String json){
        JSONObject obj = new JSONObject(json);
        Priced priced = Priced.parsePrice(json);
        return new Motherboard(obj.getString("name"), obj.getString("formFactor"), priced.prices, obj.getString("socket").trim(), obj.getString("ram"));
    }


    public String toString(){
        return "{\"name\" :\"" + name + "\"}";
    }

    public int getQuality(){
        if(name.toUpperCase().contains("GIGABYTE")){
            return 1;
        }else if(name.toUpperCase().contains("ASUS")){
            return 1;
        }else if(name.toUpperCase().contains("MSI")){
            return 2;
        }else{
            return 3;
        }
    }

    public String render(){
        String json =  "\"motherboard\":{" +
                "\"name\":\"" + name + "\"" +
                ",\"median\":" + Math.round(medianPrice(name).doubleValue()) +
                ",\"low\":" + Math.round(lowPrice(name).price.doubleValue()) +
                ",\"source\":\"" + lowPrice(name).sourceStamp() + "\"" +

                ",\"url\":\"";
        if(!lowPrice(name).affiliate.equals(""))
            json += lowPrice(name).affiliate + "\"";
        else
            json += lowPrice(name).url + "\"";
        json +=  "}";
        return json;

        }
}
