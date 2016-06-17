package com.computerevaluator.models;

/**
 * Created by erik on 6/14/16.
 */
public class Settings{

    public final double cpuIntensity;

    public final double gpuIntensity;

    public final int softBudget;

    public final int hardBudget;

    public final double diskIntensity;

    public final double multicore;

    public final Size size;

    public Settings(double cpuIntensity, double gpuIntensity, int softBudget, int hardBudget, double diskIntensity, double multicore, Size size){
        this.cpuIntensity = cpuIntensity;
        this.gpuIntensity = gpuIntensity;
        this.softBudget = softBudget;
        this.hardBudget = hardBudget;
        this.diskIntensity = diskIntensity;
        this.multicore = multicore;
        this.size = size;
    }

    public Settings(double cpuIntensity, double gpuIntensity, int softBudget, int hardBudget, double diskIntensity, double multicore){
        this.cpuIntensity = cpuIntensity;
        this.gpuIntensity = gpuIntensity;
        this.softBudget = softBudget;
        this.hardBudget = hardBudget;
        this.diskIntensity = diskIntensity;
        this.multicore = multicore;
        this.size = Size.ATX;
    }
}

enum Size{
    mATX("MicroATX"), ATX("ATX"), mITX("Mini ITX");
    Size(String name){

    }
}