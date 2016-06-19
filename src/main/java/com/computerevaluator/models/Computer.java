package com.computerevaluator.models;

import com.jcabi.aspects.Cacheable;
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

    @Cacheable()
    public double getPrice(){
        double cPrice =  cpu.lowPrice().price.doubleValue();
        double gPrice = gpu.lowPrice().price.doubleValue();
        double bPrice = bootDrive.lowPrice().price.doubleValue();
        double sPrice = 0;
        if(secondaryDrive != null)
            sPrice = secondaryDrive.lowPrice().price.doubleValue();
        double mPrice = motherboard.lowPrice().price.doubleValue();
        double pPrice = power.lowPrice().price.doubleValue();
        double rPrice = ram.lowPrice().price.doubleValue();

        return cPrice + gPrice+bPrice+sPrice+mPrice+pPrice+rPrice;
    }

    public Computer(Cpu cpu, Gpu gpu, Drive bootDrive, @Nullable Drive secondaryDrive, Motherboard motherboard, Psu power, Ram ram){
        this.cpu = cpu;
        this.gpu = gpu;
        this.bootDrive = bootDrive;
        this.secondaryDrive = secondaryDrive;
        this.motherboard = motherboard;
        this.power = power;
        this.ram = ram;
    }

    public boolean motherboardCompatible(){
        return this.cpu.socket.equals(this.motherboard.socket);
    }

    public boolean ramCompatible(){
       return this.cpu.ram.contains(this.ram.type) && this.motherboard.ram.contains(this.ram.type);
    }

    public boolean compatible(){
        return this.power.watts > powerConsumption() && ramCompatible() && motherboardCompatible();
    }

    public int powerConsumption(){
        int basePower = 140;
        //Give some buffers
        basePower += this.gpu.power * 1.2;
        basePower += this.cpu.power * 1.2;
        basePower *= 1.1;
        return basePower;
    }

    public int ramAmount(){
        return ram.size * ram.number;
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
                "\nram:" + ram.number + "X" + ram.size + "-" + ram.type +
                "\nprice: $" + getPrice() + "\n" +
                '}';
        return str;
    }
}
