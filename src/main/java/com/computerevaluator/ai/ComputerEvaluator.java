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
        /**
         * Get rid of the two things that if they don't match, render it not functional
         */
        //a little boost, since otherwise the max score was 95.5
        double score = 4.5;
        if(!computer.cpu.socket.equals(computer.motherboard.socket))
            score -= 10;
        int ramAmount = computer.ram.number * computer.ram.size;
        if(ramAmount < 8)
            score -= (8 - ramAmount) * 3;
        else if(ramAmount > 8 && ramAmount < 17)
            score += .33 * (ramAmount - 8);
        else if(ramAmount > 17)
            score += .2083333333333 * (ramAmount - 8);
        if(!computer.cpu.ram.contains(computer.ram.type) || !computer.motherboard.ram.contains(computer.ram.type))
            score -= 10;

        if(computer.bootDrive.size > 700)
            score += 3.5;
        else
            score += computer.bootDrive.size / 200;
        if(computer.secondaryDrive != null)
            score += computer.secondaryDrive.size / 1000;

        //Now ensure there's enough power, and give a bonus for there being ~300 watts more than needed;
        //there are 4.5 points to gain in powersupply, 10 points to lose
        int basePower = 140;
        //Give some buffers
        basePower += computer.gpu.power * 1.2;
        basePower += computer.cpu.power * 1.2;
        basePower *= 1.1;
        int ideal = basePower + 250;
        if(computer.power.watts < basePower)
            score -= 10;
        else if(computer.power.watts < ideal)
            score += (basePower - computer.power.watts) / 100;
        else
            score += 2.5 - (computer.power.watts - basePower) / 250;
        score += 3 - computer.power.tier;

        // Now motherboard, one point to gain, one to lose
        score += 2 - computer.motherboard.getQuality();

        double driveScore = ((computer.bootDrive.reads + computer.bootDrive.writes) / 2) / 70;
        driveScore -= 2;
        if(driveScore > 7.5){
            driveScore = 7.5;
        }
        score += driveScore;
        double rawCpu = computer.cpu.singlecore * 35*  (computer.cpu.multicore / 2);
        double adjustedCpu = rawCpu / maxCpuScore();
        double rawGpu = computer.gpu.fps * computer.gpu.threedmark / 100;
        double adjustedGpu =  35 * rawGpu / maxGpuScore();
        adjustedGpu += 7 * ((35 - adjustedGpu) / 35);
        score += adjustedGpu;
        score += adjustedCpu;
        if(score < 0){
            score = 0.001;
        }
        return score;
    }


    private double maxCpuScore(){
        List<Cpu> cpus = Cpu.list();
        cpus.sort((o1, o2) -> (int)((o1.multicore / 2 )* o1.singlecore  - (o2.multicore /2 * o2.singlecore)));
        return (double) ((cpus.get(cpus.size() - 1).multicore / 2) * cpus.get(cpus.size() - 1).singlecore);
    }

    private double lowCpuScore(){
        List<Cpu> cpus = Cpu.list();
        cpus.sort((o1, o2) -> (int)((o1.multicore / 2 )* o1.singlecore  - (o2.multicore /2 * o2.singlecore)));
        return (double) ((cpus.get(0).multicore / 2) * cpus.get(0).singlecore);
    }

    private double maxGpuScore(){
        List<Gpu> gpus = Gpu.list();
        gpus.sort((o1, o2) -> (o1.fps * o1.threedmark / 100  - o2.fps * o2.threedmark / 100 ));
        return (double) ((gpus.get(gpus.size() - 1).fps * gpus.get(gpus.size() - 1).threedmark / 100));

    }

    public boolean isNatural(){
        return true;
    }
}
