package com.computerevaluator.ai;

import com.computerevaluator.models.*;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.operators.AbstractCrossover;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by erik on 6/14/16.
 */
public class ComputerCrossover extends AbstractCrossover<Computer>{

    private static final int TOTAL_COMPONENTS = 7;

    protected ComputerCrossover(int crossoverPoints){
        super(crossoverPoints);
    }

    protected ComputerCrossover(int crossoverPoints, Probability crossoverProbability){
        super(crossoverPoints, crossoverProbability);
    }

    protected ComputerCrossover(NumberGenerator<Integer> crossoverPointsVariable){
        super(crossoverPointsVariable);
    }

    protected ComputerCrossover(NumberGenerator<Integer> crossoverPointsVariable, NumberGenerator<Probability> crossoverProbabilityVariable){
        super(crossoverPointsVariable, crossoverProbabilityVariable);
    }

    protected List<Computer> mate(Computer computer, Computer t1, int points, Random random){
        Computer r1 = new Computer(computer.cpu, computer.gpu, computer.bootDrive, computer.secondaryDrive, computer.motherboard, computer.power, computer.ram);
        Computer r2 = new Computer(t1.cpu, t1.gpu, t1.bootDrive, t1.secondaryDrive, t1.motherboard, t1.power, t1.ram);
        List<Integer> crossoverPoints = new ArrayList<>();
        int count = 0;
        while(crossoverPoints.size() < points && count < 900){
            int number = random.nextInt(TOTAL_COMPONENTS -1);
            if(!crossoverPoints.contains(number))
                crossoverPoints.add(number);
            count++;
        }
        for(int index : crossoverPoints){
            switch (index){
                case 0 : Cpu temp = r2.cpu; r2.cpu = r1.cpu; r1.cpu = temp;break;
                case 1 : Gpu tempGpu = r2.gpu; r2.gpu = r1.gpu; r1.gpu = tempGpu; break;
                case 2 : Drive tempBoot = r2.bootDrive; r2.bootDrive =r1.bootDrive; r1.bootDrive = tempBoot; break;
                case 3 : Drive tempSec = r2.secondaryDrive; r2.secondaryDrive = r1.secondaryDrive; r1.secondaryDrive = tempSec; break;
                case 4 : Motherboard tempMobo = r2.motherboard; r2.motherboard = r1.motherboard; r1.motherboard = tempMobo;break;
                case 5 : Psu tempPsu = r2.power; r2.power = r1.power; r1.power = tempPsu; break;
                case 6 : Ram tempRam = r2.ram; r2.ram = r1.ram; r1.ram = tempRam; break;
            }

        }
        ArrayList<Computer> result = new ArrayList<>();
        result.add(r1);
        result.add(r2);
        return result;
    }


    public Computer switchOnePart(Computer computer, Random random){
        int index = random.nextInt(6);
        Computer generated = new ComputerFactory().generateRandomCandidate(random);
        switch(index){
            case 0: computer.cpu = generated.cpu;break;
            case 1: computer.gpu = generated.gpu;break;
            case 2: computer.bootDrive = generated.bootDrive;break;
            case 3: computer.secondaryDrive = generated.secondaryDrive;break;
            case 4: computer.motherboard = generated.motherboard;break;
            case 5: computer.power = generated.power;break;
            case 6: computer.ram = generated.ram;break;
        }
        return computer;
    }


}
