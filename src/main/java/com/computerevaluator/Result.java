package com.computerevaluator;

import com.computerevaluator.models.Computer;

/**
 * Created by erik on 6/15/16.
 */
public class Result{
    private Computer computer;

    private double score;

    public Result(){
        this.setComputer(null);
        this.setScore(0.0);
    }

    public void updateIfBetter(Computer c, double s){
        if(s > getScore()){
            this.setComputer(c);
            this.score = s;
        }
    }

    public Computer getComputer(){
        return computer;
    }

    public void setComputer(Computer computer){
        this.computer = computer;
    }

    public double getScore(){
        return score;
    }

    public void setScore(double score){
        this.score = score;
    }
}
