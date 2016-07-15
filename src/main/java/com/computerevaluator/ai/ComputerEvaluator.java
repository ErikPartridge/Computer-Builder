package com.computerevaluator.ai;

import com.computerevaluator.models.*;
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
        score += 6 * getMatchPercent(computer);
        if(computer.compatible() && sizeCompatability(computer))
            score += 9.5;
        int ramAmount =computer.ramAmount();
        if(ramAmount < 8)
            score -= (8 - ramAmount) * 2;
        else if(ramAmount > 8 && ramAmount < 17)
            score += .06 * (ramAmount - 8);
        else if(ramAmount > 17)
            score += .03 * (ramAmount - 8);

        if(computer.cpu.name.contains("Intel") && computer.cpu.name.endsWith("K")){
            if (!computer.motherboard.overclock()){
                score -= 6;
            }
        }

        if(computer.cpu.name.contains("Intel") && !(computer.cpu.shortname.contains("-5") || computer.cpu.shortname.contains("-6"))){
            score -= 2;
        }
        if(computer.cpu.name.contains("GeForce") && !(computer.gpu.name.contains("GTX 9") || computer.gpu.name.contains("GTX 1"))){
            score -= 2;
        }
        if(computer.bootDrive.size < 240)
            score -= settings.diskSize * 0.33;
        if(computer.bootDrive.size > 700)
            score += settings.diskSize * 5;
        else
            score += settings.diskSize * computer.bootDrive.size / 140;
        if(computer.secondaryDrive != null)
            score += settings.diskSize * computer.secondaryDrive.size / 1000;

        //Now ensure there's enough power, and give a bonus for there being ~300 watts more than needed;
        //there are 4.5 points to gain in powersupply, 10 points to lose
        int basePower = computer.powerConsumption();
        int ideal = basePower + 65;
        if(computer.power.watts < basePower)
            score -= 10;
        else if(computer.power.watts < ideal)
            score += (basePower - computer.power.watts) / 100;
        else
            score += 2.5 - (computer.power.watts - basePower) / 250;
        score += (3 - computer.power.tier) * .6;

        // Now motherboard, one point to gain, one to lose
        score += 2 - computer.motherboard.getQuality();

        double driveScore = ((computer.bootDrive.reads + computer.bootDrive.writes) / 2) / 88;
        driveScore -= 2;
        if(driveScore > 7.5){
            driveScore = 7.5;
        }
        score += driveScore;
        double rawCpu = ((1 - settings.multicore) / 3) * computer.cpu.singlecore +  ((settings.multicore / 3) * (computer.cpu.multicore));
        double adjustedCpu = 26 + 40* ((settings.cpuIntensity / (settings.cpuIntensity + settings.gpuIntensity))* rawCpu - lowCpuScore()) / (maxCpuScore() - lowCpuScore());
        double rawGpu = computer.gpu.fps + (computer.gpu.threedmark / 100);
        double adjustedGpu = 17 + 40 * ((settings.gpuIntensity / (settings.cpuIntensity + settings.gpuIntensity)) * rawGpu - lowGpuScore()) / (maxGpuScore() - lowGpuScore());
        //Inflate the GPU score slightly
       // adjustedGpu  * ((35 - adjustedGpu) / 35);
        score += adjustedGpu;
        score += adjustedCpu;

        return score;
    }

    private boolean sizeCompatability(Computer comp){
        return comp.motherboard.formFactor.equals("MicroATX") && settings.size == Size.mATX || comp.motherboard.formFactor.equals("ATX") && (settings.size == Size.ATX);
    }


    private double maxCpuScore(){
        List<Cpu> cpus = new ArrayList<>(Cpu.list());
        cpus.sort((o1, o2) -> (int)((( settings.multicore * o1.multicore / 3 )+ (( (1 - settings.multicore) / 3) * o1.singlecore))  - (settings.multicore * o2.multicore / 3 + ((1-settings.multicore) / 3) * o2.singlecore)));
        return (double) ((settings.multicore * cpus.get(cpus.size() - 1).multicore / 3) + ((1-settings.multicore) / 3) * cpus.get(cpus.size() - 1).singlecore);
    }

    private double lowCpuScore(){
        List<Cpu> cpus = new ArrayList<>(Cpu.list());
        cpus.sort((o1, o2) -> (int)((( settings.multicore * o1.multicore / 3 )+ (( (1 - settings.multicore) / 3) * o1.singlecore))  - (settings.multicore * o2.multicore / 3 + ((1-settings.multicore) / 3) * o2.singlecore)));
        return (double) ((settings.multicore * cpus.get(0).multicore / 3) + (1-settings.multicore) * cpus.get(0).singlecore) / 3;    }

    private double maxGpuScore(){
        List<Gpu> gpus = new ArrayList<>(Gpu.list());
        gpus.sort((o1, o2) -> ((o1.fps + o1.threedmark / 100)  - (o2.fps + o2.threedmark / 100) ));
        return (double) ((gpus.get(gpus.size() - 1).fps + gpus.get(gpus.size() - 1).threedmark / 100));
    }

    private double lowGpuScore(){
        List<Gpu> gpus = new ArrayList<>(Gpu.list());
        gpus.sort((o1, o2) -> ((o1.fps + o1.threedmark / 100)  - (o2.fps + o2.threedmark / 100) ));
        return (double) ((gpus.get(0).fps + gpus.get(0).threedmark / 100));
    }

    private double getMatchPercent(Computer computer){
        double matchQuality = 1.0;
        if(settings.softBudget < 650 && computer.ram.size * computer.ram.number > 8){
            matchQuality -= .2;
        }else if(settings.softBudget > 1050 && computer.ram.size * computer.ram.number < 14){
            matchQuality -= .2;
        }
        double rawCpu = ((1 - .5) / 3) * computer.cpu.singlecore +  ((settings.multicore / 3) * (computer.cpu.multicore));
        double adjustedCpu = (rawCpu - lowCpuScore()) / (maxCpuScore() - lowCpuScore());
        double rawGpu = computer.gpu.fps + (computer.gpu.threedmark / 100);
        double adjustedGpu = (rawGpu - lowGpuScore()) / (maxGpuScore() - lowGpuScore());
        if(Math.abs(adjustedCpu - adjustedGpu) > 0.4)
            matchQuality -= .3;
        return matchQuality;
    }

    public boolean isNatural(){
        return true;
    }
}
