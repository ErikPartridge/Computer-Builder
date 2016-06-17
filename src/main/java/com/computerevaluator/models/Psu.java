package com.computerevaluator.models;

import com.jcabi.aspects.Cacheable;
import com.mongodb.Block;
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

import static com.mongodb.client.model.Filters.*;

/**
 * Holds the primary methods for the model of a power supply, represented in the database in the collection powersupplies
 */
public class Psu extends Priced {

    public final String name;

    public final int watts;

    public final int tier;


    private Psu (String name, int power,int tier, List<Price> prices) {
        super(prices);
        this.name = name;
        this.watts = power;
        this.tier = tier;
    }



    private static final MongoDatabase database = Connection.database;


    @Override
    public String toString () {
        return "{\"name\" :\"" + name + "\"" + "}";
    }

    private static Psu parseFromJson (String json) {
        JSONObject obj = new JSONObject(json);

        Priced priced = Priced.parsePrice(json);
        return new Psu(obj.getString("name"), obj.getInt("watts"), obj.getInt("tier"), priced.prices);
    }

    @Cacheable(lifetime = 30, unit = TimeUnit.SECONDS)
    public static List<Psu> list () {
        String collName = "powersupplies";
        MongoCollection<Document> collection = database.getCollection(collName);
        List<Psu> list = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Psu psu = parseFromJson(cursor.next().toJson());
                //if(psu.prices.size() > 0){
                    list.add(psu);
                //}
            }
        }
        return list;
    }

    public static BigDecimal medianPrice (String name) {
        if (name.equals("already")) {
            return new BigDecimal(0);
        } else if (name.startsWith("stock")) {
            List<Psu> psus = listInGroup(name.replaceAll("stock-", ""));
            List<Price> prices = new ArrayList<>();
            psus.forEach(psu -> prices.addAll(psu.prices));
            return Price.median(prices);
        } else {
            return Price.median(find(name).prices);
        }
    }

    public static Price lowPrice (String name) {
        if (name.equals("already")) {
            return new Price("--Already Purchased--", System.currentTimeMillis(), "#", new BigDecimal(0), "");
        } else if (name.startsWith("stock")) {
            return lowInGroup(name.replaceAll("stock-", "")).lowPrice();
        } else {
            return Price.low(find(name).prices);
        }
    }

    public String render(){
        String json =  "\"psu\":{" +
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

    public static Psu find (String name) {
        MongoCollection<Document> collection = database.getCollection("powersupplies");
        if (name.startsWith("stock")) {
            return lowInGroup(name.replace("stock", ""));
        }
        Document raw = collection.find(eq("name", name)).first();
        return parseFromJson(raw.toJson());
    }

    private static Psu lowInGroup (String group) {
        List<Psu> list = listInGroup(group);
        list.sort((o1, o2) -> Price.low(o1.prices).price.intValue() - Price.low(o2.prices).price.intValue());
        return list.get(0);
    }

    private static List<Psu> listInGroup (String group) {
        group = group.replaceAll("-", "");
        MongoCollection<Document> collection = database.getCollection("powersupplies");
        List<Psu> matching = new ArrayList<>();
        int start = Integer.parseInt(group);
        int end = 0;
        switch (start) {
            case 400:
                start = 250;
                end = 400;
                break;
            case 500:
                start = 400;
                end = 600;
                break;
            case 600:
                start = 600;
                end = 800;
                break;
            case 800:
                start = 800;
                end = 1200;
                break;
            case 1200:
                start = 1200;
                end = 2000;
                break;
        }
        collection.find(and(gte("watts", start), lte("watts", end))).forEach((Block<Document>) document -> matching.add(parseFromJson(document.toJson())));
        return matching;
    }
}
