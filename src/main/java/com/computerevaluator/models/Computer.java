package com.computerevaluator.models;

import com.sun.istack.internal.Nullable;

/**
 * Created by erik on 6/14/16.
 */
public class Computer{

    public Cpu cpu;

    public Gpu gpu;

    public Drive bootDrive;

    public Drive secondaryDrive;

    public Motherboard motherboard;

    public Psu power;

    public Ram ram;

    public Computer(Cpu cpu, Gpu gpu, Drive bootDrive, @Nullable Drive secondaryDrive, Motherboard motherboard, Psu power, Ram ram){
        this.cpu = cpu;
        this.gpu = gpu;
        this.bootDrive = bootDrive;
        this.secondaryDrive = secondaryDrive;
        this.motherboard = motherboard;
        this.power = power;
        this.ram = ram;
    }

    @Override
    public String toString(){
        String str =  "Computer{" +
                "cpu:" + cpu.name +
                "\ngpu:" + gpu.name +
                "\nbootDrive:" + bootDrive.name;
                if(secondaryDrive != null)
                    str += "\nsecondaryDrive:" + secondaryDrive.name;
                str += "\nmotherboard:" + motherboard.name +
                "\npower:" + power.name +
                "\nram:" + ram.number + "X" + ram.size + "-" + ram.type + "\n" +
                '}';
        return str;
    }
}
