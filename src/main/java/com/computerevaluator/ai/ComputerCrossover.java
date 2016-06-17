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
        List<Integer> crossoverPoints = new ArrayList<Integer>();
        int count = 0;
        while(crossoverPoints.size() < points && count < 900){
            int number = random.nextInt(TOTAL_COMPONENTS -1);
            if(!crossoverPoints.contains(number))
                crossoverPoints.add(number);
            count++;
        }
        for(int index : crossoverPoints){
            switch (index){
                case 0 : Cpu temp = t1.cpu; t1.cpu = computer.cpu; computer.cpu = temp;break;
                case 1 : Gpu tempGpu = t1.gpu; t1.gpu = computer.gpu; computer.gpu = tempGpu; break;
                case 2 : Drive tempBoot = t1.bootDrive; t1.bootDrive =computer.bootDrive; computer.bootDrive = tempBoot; break;
                case 3 : Drive tempSec = t1.secondaryDrive; t1.secondaryDrive = computer.secondaryDrive; computer.secondaryDrive = tempSec; break;
                case 4 : Motherboard tempMobo = t1.motherboard; t1.motherboard = computer.motherboard; computer.motherboard = tempMobo;break;
                case 5 : Psu tempPsu = t1.power; t1.power = computer.power; computer.power = tempPsu; break;
                case 6 : Ram tempRam = t1.ram; t1.ram = computer.ram; computer.ram = tempRam; break;
            }

        }
        ArrayList<Computer> result = new ArrayList<>();
        result.add(computer);
        result.add(t1);
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
