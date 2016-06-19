package com.computerevaluator.ai;

import com.computerevaluator.models.Computer;
import com.computerevaluator.models.*;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by erik on 6/14/16.
 */
public class ComputerFactory extends AbstractCandidateFactory<Computer>{

    //private Settings settings;

    public ComputerFactory(){//Settings s){
        //this.settings = s;
    }

    public Computer generateRandomCandidate(Random random){
        List<Cpu> cpuList = Cpu.list();
        Cpu cpu = cpuList.get(random.nextInt(cpuList.size()));
        Gpu gpu;
        if(random.nextDouble() < 0 && cpu.integratedGraphics){
            gpu = new IntegratedGraphics();
        }else{
            List<Gpu> gpuList = Gpu.list();
            gpu = gpuList.get(random.nextInt(gpuList.size()));
        }
        List<Drive> drives = Drive.list();
        Drive drive = drives.get(random.nextInt(drives.size()));
        Drive secondary = null;
        if(random.nextDouble() < .4)
            secondary = drives.get(random.nextInt(drives.size()));
        List<Motherboard> compatibleMotherboard = Motherboard.list();
        compatibleMotherboard = compatibleMotherboard.stream().collect(Collectors.toList());
        Motherboard motherboard = compatibleMotherboard.get(random.nextInt(compatibleMotherboard.size()));

        List<Psu> powersupplies = Psu.list();
        Psu psu = powersupplies.get(random.nextInt(powersupplies.size()));
        List<Ram> rams = Ram.list();
        Ram ram = rams.get(random.nextInt(rams.size()));

        return new Computer(cpu, gpu, drive, secondary, motherboard, psu, ram);
    }
}
