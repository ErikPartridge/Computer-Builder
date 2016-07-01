package com.computerevaluator.ai;

import com.computerevaluator.models.Computer;
import com.computerevaluator.models.Cpu;
import com.computerevaluator.models.Gpu;
import com.computerevaluator.models.Settings;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by erik on 6/14/16.
 */
public class ComputerEvaluator implements FitnessEvaluator<Computer>{

    private final Settings settings;

    public ComputerEvaluator(Settings settings){
        this.settings = settings;
    }


    public double getFitness(Computer computer, List<? extends Computer> list){
        double score = 0;
        score = getUnadjustedFitness(computer);
        double price = computer.getPrice();
        if(price > settings.hardBudget){
            score = 1;
        }
        if(price < settings.softBudget){
            score -= (settings.softBudget - price) / 120;
        }
        if(price > settings.softBudget && price < settings.hardBudget){
            score -= (price - settings.softBudget) / 20;
        }
        if(!computer.compatible()){
            score = 1;
        }
        if(score < 0)
            return 0;

        return score;
    }

    private double getUnadjustedFitness(Computer computer){
        /**
         * Get rid of the two things that if they don't match, render it not functional
         */
        //a little boost, since otherwise the max score was 95.5
        double score = 0;
        if(computer.compatible())
            score += 9.5;
        int ramAmount =computer.ramAmount();
        if(ramAmount < 8)
            score -= (8 - ramAmount) * 2;
        else if(ramAmount > 8 && ramAmount < 17)
            score += .0625 * (ramAmount - 8);
        else if(ramAmount > 17)
            score += .03 * (ramAmount - 8);

        if(computer.cpu.name.contains("Intel") && !(computer.cpu.shortname.contains("-5") || computer.cpu.shortname.contains("-6"))){
            score -= 2;
        }
        if(computer.cpu.name.contains("GeForce") && !(computer.gpu.name.contains("GTX 9") || computer.gpu.name.contains("GTX 1"))){
            score -= 2;
        }
        if(computer.bootDrive.size < 240)
            score -= 1;
        if(computer.bootDrive.size > 700)
            score += 4;
        else
            score += computer.bootDrive.size / 175;
        if(computer.secondaryDrive != null)
            score += computer.secondaryDrive.size / 2000;

        //Now ensure there's enough power, and give a bonus for there being ~300 watts more than needed;
        //there are 4.5 points to gain in powersupply, 10 points to lose
        int basePower = computer.powerConsumption();
        int ideal = basePower + 200;
        if(computer.power.watts < basePower)
            score -= 10;
        else if(computer.power.watts < ideal)
            score += (basePower - computer.power.watts) / 100;
        else
            score += 2.5 - (computer.power.watts - basePower) / 250;
        score += 3 - computer.power.tier;

        // Now motherboard, one point to gain, one to lose
        score += 2 - computer.motherboard.getQuality();

        double driveScore = ((computer.bootDrive.reads + computer.bootDrive.writes) / 2) / 88;
        driveScore -= 2;
        if(driveScore > 7.5){
            driveScore = 7.5;
        }
        score += driveScore;
        double rawCpu = (1 - settings.multicore) * computer.cpu.singlecore +  (settings.multicore) * (computer.cpu.multicore / 2);
        double adjustedCpu = 20 + 40* (settings.cpuIntensity / (settings.cpuIntensity + settings.gpuIntensity))* rawCpu / maxCpuScore();
        double rawGpu = computer.gpu.fps + (computer.gpu.threedmark / 100);
        double adjustedGpu = 20 + 40 * (settings.gpuIntensity / (settings.cpuIntensity + settings.gpuIntensity)) * (rawGpu / maxGpuScore());
        //Inflate the GPU score slightly
       // adjustedGpu  * ((35 - adjustedGpu) / 35);
        score += adjustedGpu;
        score += adjustedCpu;

        return score;
    }


    private double maxCpuScore(){
        List<Cpu> cpus = new ArrayList<>(Cpu.list());
        cpus.sort((o1, o2) -> (int)((( settings.multicore * o1.multicore / 2 )+ ( 1 - settings.multicore) * o1.singlecore)  - (settings.multicore * o2.multicore /2 + (1-settings.multicore) * o2.singlecore)));
        return (double) ((settings.multicore * cpus.get(cpus.size() - 1).multicore / 2) + (1-settings.multicore) * cpus.get(cpus.size() - 1).singlecore);
    }

    private double lowCpuScore(){
        List<Cpu> cpus = Cpu.list();
        cpus.sort((o1, o2) -> (int)((o1.multicore / 2 )* o1.singlecore  - (o2.multicore /2 * o2.singlecore)));
        return (double) ((cpus.get(0).multicore / 2) * cpus.get(0).singlecore);
    }

    private double maxGpuScore(){
        List<Gpu> gpus = new ArrayList<>(Gpu.list());
        gpus.sort((o1, o2) -> (o1.fps * o1.threedmark / 100  - o2.fps * o2.threedmark / 100 ));
        return (double) ((gpus.get(gpus.size() - 1).fps + gpus.get(gpus.size() - 1).threedmark / 100));

    }

    private double getMatchPercent(){
        return .7;
    }

    public boolean isNatural(){
        return true;
    }
}
