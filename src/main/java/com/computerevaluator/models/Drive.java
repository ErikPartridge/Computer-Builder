package com.computerevaluator.models;

import com.mongodb.Block;
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

import static com.mongodb.client.model.Filters.*;

/**
 * Created by erik on 5/17/16.
 */
public class Drive extends Priced{


    private static long timestamp = 0;

    private static List<Drive> drives = Collections.synchronizedList(new ArrayList<>());

    public final String name;

    private final String type;

    public final int size;

    public final int reads;

    public final int writes;

    private Drive (List<Price> prices, String name, String type, int size, int reads, int writes){
        super( prices);
        this.name = name;
        this.type = type;
        this.size = size;
        this.reads = reads;
        this.writes = writes;
    }

    private static final MongoDatabase database = Connection.database;

    private Drive (){
        super(new ArrayList<>());
        this.name = "Already Purchased";
        this.type = "N/A";
        this.size = 0;
        this.reads = 0;
        this.writes = 0;
    }



    public static List<Drive> list(){
        if(drives.size() > 0 && System.currentTimeMillis() - timestamp < 10000){
            return drives;
        }else{
            timestamp = System.currentTimeMillis();
            MongoCollection<Document> collection = database.getCollection("drives");
            List<Drive> driveList = new ArrayList<>();
            try (MongoCursor<Document> cursor = collection.find().iterator()){
                while (cursor.hasNext()){
                    Drive drive = parseFromJson(cursor.next().toJson());
                    if (drive.prices.size() > 0)
                        driveList.add(drive);
                }
            }
            drives = driveList;
            return driveList;
        }
    }

    private static Drive parseFromJson (String raw){
        JSONObject obj = new JSONObject(raw);

        Priced priced = Priced.parsePrice(raw);
        return new Drive(priced.prices, obj.getString("name"), obj.getString("type"), obj.getInt("size"), obj.getInt("read"), obj.getInt("write"));

    }

    @Override
    public String toString(){
        return "{\"name\" :\"" + name + "\"" + "}";
    }

    public static Drive find(String name){
        if(name.equals("already")){
            return new Drive();
        }
        if(name.startsWith("stock")){
            return lowInGroup(name.replaceAll("stock-", ""));
        }
        MongoCollection<Document> collection =  database.getCollection("drives");
        Document raw = collection.find(eq("name", name)).first();
        return parseFromJson(raw.toJson());
    }


    public static Price lowPrice(String name){
        if(name.startsWith("stock")){
            return lowInGroup(name.replaceAll("stock-", "")).lowPrice();
        }else if(name.equals("already")){
            return new Price("--Already Purchased--", System.currentTimeMillis(), "#", new BigDecimal(0),"");
        }else{
            MongoCollection<Document> drives = database.getCollection("drives");
            Document driveDoc = drives.find(eq("name", name)).first();
            Drive drive = parseFromJson(driveDoc.toJson());
            return drive.lowPrice();
        }
    }

    public static BigDecimal medianPrice(String name){
        if(name.startsWith("stock")){
            List<Drive> drives = drivesByGroup(name.replaceAll("stock-", ""));
            List<Price> prices = new ArrayList<>();
            drives.forEach(m -> prices.addAll(m.prices));
            return Price.median(prices);
        }else if(name.equals("already")){
            return new BigDecimal(0);
        }else{
            MongoCollection<Document> drives = database.getCollection("drives");
            Document driveDoc = drives.find(eq("name", name)).first();
            Drive drive = parseFromJson(driveDoc.toJson());
            List<Price> prices = drive.prices;
            return Price.median(prices);
        }
    }

    private static List<Drive> drivesByGroup(String group){
        int size = Integer.parseInt(group.split("-")[0]);
        if(size < 128)
            size *= 1000;
        String type= group.split("-")[1];
        MongoCollection<Document> collection = database.getCollection("drives");
        List<Drive> drives = new ArrayList<>();
        collection.find(and(gte("size", size - 50), lte("size", size+50), eq("type", type))).forEach((Block<Document>) document -> drives.add(parseFromJson(document.toJson())));
        return drives;
    }

    private static Drive lowInGroup(String s){
        List<Drive> drives = drivesByGroup(s);
        drives.sort((o1, o2) -> Price.low(o1.prices).price.intValue() - Price.low(o2.prices).price.intValue());
        return drives.get(0);
    }

    public String render(){
        String json =  "\"drive\":{" +
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
