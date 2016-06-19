package com.computerevaluator.models;

import java.math.BigDecimal;
import java.util.ArrayList;

public class IntegratedGraphics extends Gpu{

    public IntegratedGraphics(){
        super("Integrated Graphics", "Integrated", new ArrayList<>(), 1, 1, 0);
    }

    @Override
    public Price lowPrice(){
        return new Price("N/A", 0, "", BigDecimal.ZERO, "");
    }
}
