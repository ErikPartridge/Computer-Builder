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
        List<Price> prices = new ArrayList<>();
        JSONArray priceArray = obj.getJSONArray("prices");
        for (int j = 0; j < priceArray.length(); j++){
            Price price = Price.priceFromJson(priceArray.getJSONObject(j));
            if(price != null){
                prices.add(price);
            }
        }
        return new Priced(prices);
    }

    public Price lowPrice (){
        Price low = null;
        for (Price price : prices){
            if (low == null || price.price.doubleValue() < low.price.doubleValue()){
                low = price;
            }
        }
        return low;
    }


}
