package com.computerevaluator.models;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONObject;
import com.computerevaluator.services.Connection;
import sun.plugin2.gluegen.runtime.CPU;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by erik on 5/15/16.
 */
public class Cpu extends Priced{

    private static List<Cpu> cpus = Collections.synchronizedList(new ArrayList<>());

    private static long timestamp = 0;

    public final String name;

    public final String shortname;

    public final int cores;

    public final int threads;

    public final BigDecimal frequency;

    public final String memory;

    public final int maxmemory;

    public final boolean integratedGraphics;

    public final String socket;

    public final long multicore;

    public final long singlecore;

    public final String ram;

    public final int power;

    private Cpu(String name, String shortname, int cores, int threads, BigDecimal frequency, String memory, int maxmemory, boolean integratedGraphics, String socket, long multicore, long singlecore, List<Price> prices, String ram, int power){
        super(prices);
        this.name = name;
        this.shortname = shortname;
        this.cores = cores;
        this.threads = threads;
        this.frequency = frequency;
        this.memory = memory;
        this.maxmemory = maxmemory;
        this.integratedGraphics = integratedGraphics;
        this.socket = socket;
        this.multicore = multicore;
        this.ram = ram;
        this.singlecore = singlecore;
        this.power = power;
    }


    private static final MongoDatabase database = Connection.database;

    public static List<Cpu> list(){

        if(cpus.size() > 0 && System.currentTimeMillis() - timestamp < 10000){
            return cpus;
        }else{
            timestamp = System.currentTimeMillis();
            MongoCollection<Document> collection = database.getCollection("cpus");
            List<Cpu> cpuList = new ArrayList<>();
            try (MongoCursor<Document> cursor = collection.find().iterator()){
                while (cursor.hasNext()){
                    Cpu cpu = parseFromJson(cursor.next().toJson());
                    if (cpu.prices.size() > 0)
                        cpuList.add(cpu);
                }
            }
            cpus = cpuList;
            return cpuList;
        }
    }


    public static Cpu find (String shortName){
        MongoCollection<Document> cpus = database.getCollection("cpus");
        Document raw = cpus.find(eq("short", shortName)).first();
        return parseFromJson(raw.toJson());
    }


    private static Cpu parseFromJson(String raw){
        JSONObject json = new JSONObject(raw);
        Priced priced = Priced.parsePrice(raw);
        return new Cpu(json.getString("name"), json.getString("short"), json.getInt("cores"), json.getInt("threads"), json.getBigDecimal("frequency"),
                       json.getString("memory"), (int)json.getDouble("maxmemory"), json.getBoolean("integratedGraphics"), json.getString("socket"),
                       (long)json.getDouble("multicore"), (long)json.getDouble("singlecore"), priced.prices, json.getString("memory"), json.getInt("power"));
    }

    public static BigDecimal medianPrice(String shortname){
        if(shortname.equals("already")){
            return new BigDecimal(0);
        }
        MongoCollection<Document> cpus = database.getCollection("cpus");
        Document cpuDoc = cpus.find(eq("short", shortname)).first();
        Cpu cpu = parseFromJson(cpuDoc.toJson());
        List<Price> prices = cpu.prices;
        return Price.median(prices);
    }

    private BigDecimal medianPrice(){
        return Price.median(prices);
    }

    public static Price lowPrice(String shortname){
        if(shortname.equals("already")){
            return new Price("--Already Purchased--", System.currentTimeMillis(), "#", new BigDecimal(0), "");
        }
        MongoCollection<Document> cpus = database.getCollection("cpus");
        Document cpuDoc = cpus.find(eq("short", shortname)).first();
        Cpu cpu = parseFromJson(cpuDoc.toJson());
        return cpu.lowPrice();
    }

    public String toString(){
        return "{\"name\" :\"" + name + "\", \"short\":\"" + shortname + "\"}";

    }

    public String render(){
        String json =  "\"cpu\":{" +
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
