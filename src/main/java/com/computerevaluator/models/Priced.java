package com.computerevaluator.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by erik on 5/16/16.
 */
public class Priced{

    final List<Price> prices;

    Priced (List<Price> prices){
        this.prices = prices;
    }

    static Priced parsePrice (String json){
        JSONObject obj = new JSONObject(json);
        final List<Price> prices = new ArrayList<>();
        JSONArray priceArray = obj.getJSONArray("prices");
        for (int j = 0; j < priceArray.length(); j++){
            final Price price = Price.priceFromJson(priceArray.getJSONObject(j));
            if(price != null){
                prices.add(price);
            }
        }
        prices.sort((o1, o2) -> (int) (o1.price.doubleValue() * 100 - o2.price.doubleValue() * 100));
        return new Priced(prices);
    }

    public Price lowPrice (){
        return Price.low(prices);
    }


}
