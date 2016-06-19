package com.computerevaluator.models;

import com.computerevaluator.models.Computer;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by erik on 6/15/16.
 */
public class Result{


    private volatile static long safeId = System.currentTimeMillis();

    public final long id;

    public final boolean finished;

    public final Computer result;

    public Result(){
        safeId ++;
        this.id = safeId;
        this.finished = false;
        this.result = null;
    }

    public Result(long id, boolean done, Computer res){
        this.id = id;
        this.finished = done;
        this.result = res;
    }

    public Result(long id){
        this.id = id;
        this.finished = false;
        this.result = null;
    }
}
