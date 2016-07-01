package com.computerevaluator.models;

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

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * Created by erik on 5/25/16.
 */
public class Ram extends Priced{

    public final String type;

    public final int number;

    public final int size;

    private static List<Ram> ramList = new ArrayList<>();

    private static long timestamp = 0;

    public Ram (List<Price> prices, String type, int number, int size) {
        super(prices);
        this.type = type;
        this.number = number;
        this.size = size;
    }

    private static final MongoDatabase database = Connection.database;

    public static List<Ram> list(){
        if(System.currentTimeMillis() - timestamp < 10000 && ramList.size() > 0){
            return ramList;
        }else{
            MongoCollection<Document> rams = database.getCollection("ram");
            List<Ram> ram = new ArrayList<>();
            try (MongoCursor<Document> cursor = rams.find().iterator()){
                while (cursor.hasNext()){
                    Ram r = parseFromJson(cursor.next().toJson());
                    if (r.prices.size() > 0)
                        ram.add(r);
                }
            }
            ramList = ram;
            timestamp = System.currentTimeMillis();
            return ram;
        }

    }

    public static Price lowPrice (String ram) {
        Ram parsed = find(ram);
        System.out.println(parsed);
        System.out.println(parsed.prices.size());
        System.out.println(parsed.prices);
        Price price = Price.low(parsed.prices);

        return price;
    }

    public static BigDecimal medianPrice (String ram) {
        Ram parsed = find(ram);
        BigDecimal price = Price.median(parsed.prices);
        return price;
    }

    public static Ram find (String ram){
        int quantity = Integer.parseInt(ram.split("x")[0]);
        int size = Integer.parseInt(ram.split("x")[1].split("-")[0]);
        String type = "DDR" + ram.split("x")[1].split("-")[1];
        MongoCollection<Document> collection = database.getCollection("ram");
        Document raw = collection.find(and(eq("number", quantity), eq("size", size), eq("type", type))).first();
        return parseFromJson(raw.toJson());
    }

    private static Ram parseFromJson(String json){
        JSONObject obj = new JSONObject(json);
        Priced priced = Priced.parsePrice(json);
        return new Ram(priced.prices, obj.getString("type"), obj.getInt("number"), obj.getInt("size"));
    }

    public String getName(){
        return number + "x" + size + "-" + type.replace("DDR", "");
    }

    public String render(){
        String name = number + "x" + size + "GB" + " " + type;
        String shorter = number + "x" + size + "-" + type.replace("DDR","");
        System.out.println("Rendering prices");
        String json =  "\"ram\":{" +
                "\"name\":\"" + name + "\"";
        json +=
                ",\"median\":" + Math.round(medianPrice(shorter).doubleValue());
        System.out.println("Rendering pricesb");
        json +=        ",\"low\":" + Math.round(lowPrice(shorter).price.doubleValue());
        System.out.println("Rendering pricesc");
        json +=        ",\"source\":\"" + lowPrice(shorter).sourceStamp() + "\"" +

                ",\"url\":\"";
        System.out.println("affiliate code");
        if(lowPrice(shorter).affiliate != null && !lowPrice(shorter).affiliate.equals(""))
            json += lowPrice(shorter).affiliate + "\"";
        else
            json += lowPrice(shorter).url + "\"";
        json +=  "}";
        return json;
    }
}
