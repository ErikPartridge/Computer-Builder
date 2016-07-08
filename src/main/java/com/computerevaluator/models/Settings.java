package com.computerevaluator.models;

/**
 * Created by erik on 6/14/16.
 */
public class Settings{

    public final double cpuIntensity;

    public final double gpuIntensity;

    public int softBudget;

    public int hardBudget;

    public final double diskIntensity;

    public final double multicore;

    public final double diskSize;

    public final double newModel;

    public final Size size;

    public Settings(double cpuIntensity, double gpuIntensity, int softBudget, int hardBudget, double diskIntensity, double multicore, double newModel, double diskSize, Size size){
        this.cpuIntensity = cpuIntensity;
        this.gpuIntensity = gpuIntensity;
        this.softBudget = softBudget;
        this.hardBudget = hardBudget;
        this.diskIntensity = diskIntensity;
        this.multicore = multicore;
        this.size = size;
        this.diskSize = diskSize;
        this.newModel = newModel;
    }

    public Settings(double cpuIntensity, double gpuIntensity, int softBudget, int hardBudget, double diskIntensity, double multicore, double newModel, double diskSize){
        this.cpuIntensity = cpuIntensity;
        this.gpuIntensity = gpuIntensity;
        this.softBudget = softBudget;
        this.hardBudget = hardBudget;
        this.diskIntensity = diskIntensity;
        this.multicore = multicore;
        this.diskSize = diskSize;
        this.newModel = newModel;
        this.size = Size.ATX;
    }
}

