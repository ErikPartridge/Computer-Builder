package com.computerevaluator.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by erik on 5/15/16.
 */
public class Price implements Comparable{

    private final String name;

    private final long time;

    public final String url;

    public final BigDecimal price;

    public final String affiliate;

    public Price(String name, long time, String url, BigDecimal price, String affiliate){
        this.name = name;
        this.time = time;
        this.url = url;
        this.price = price;
        this.affiliate = affiliate;
    }

    static Price priceFromJson(JSONObject object){
        String name = object.getString("name");
        long time = object.getLong("time");
        String url = object.getString("url");
        if(object.get("price") == null)
            return null;
        double price = object.getDouble("price");
        String affiliate;
        try{
            affiliate = object.getString("affiliate");
        }catch(JSONException e){
            affiliate = "";
        }
        return new Price(name, time, url, new BigDecimal(price), affiliate);
    }

    public static BigDecimal median(List<Price> prices){
        prices.sort((o1, o2) -> (int) (o1.price.doubleValue() - o2.price.doubleValue()));
        if (prices.size() % 2 == 0){
            if(prices.size() > 1){
                BigDecimal lower = prices.get(prices.size() / 2 - 1).price;
                BigDecimal higher = prices.get(prices.size() / 2).price;
                return lower.add(higher).divide(new BigDecimal(2), BigDecimal.ROUND_HALF_EVEN);
            }else if(prices.size() == 1){
                return prices.get(0).price;
            }else{
                return new BigDecimal(0);
            }
        }else{
            return prices.get(prices.size() / 2).price;
        }
    }

    public static Price low(List<Price> prices){
        if(prices.size() > 0){
            return prices.get(0);
        }else{
            throw new NullPointerException("No price found");
        }
    }

    @Override
    public int compareTo (Object o) {
        if(o.getClass() != Price.class){
            return -1;
        }
        return this.price.intValue() - ((Price) o).price.intValue();
    }

    public String sourceStamp (){
        PrettyTime p = new PrettyTime();
        return name + ", " + p.format(new Date(time));
    }
}
