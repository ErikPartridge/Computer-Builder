package com.computerevaluator.models;

import org.json.JSONObject;

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

    public Computer(Cpu cpu, Gpu gpu, Drive bootDrive, Drive secondaryDrive, Motherboard motherboard, Psu power, Ram ram){
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

    public static Computer fromJson(JSONObject object){
        Cpu cpu = Cpu.find(object.getString("cpu"));
        Gpu gpu = Gpu.find(object.getString("gpu"));
        Drive boot = Drive.find(object.getString("boot"));
        Drive secondary = null;
        if(object.has("secondary")){
            secondary = Drive.find(object.getString("secondary"));
        }
        Motherboard motherboard = Motherboard.find(object.getString("motherboard"));
        Psu psu = Psu.find(object.getString("power"));
        Ram ram = Ram.find(object.getString("ram"));
        return new Computer(cpu, gpu, boot, secondary, motherboard, psu,ram);
    }

    public String render(){
        System.out.println("Rendering 1");
        String json = cpu.render() + "," + gpu.render() + "," + motherboard.render() + "," + power.render() + "," + ram.render() + ",";
        System.out.println("Rendering 2");
        json += bootDrive.render().replace("drive\":", "boot\":") + ",";
        System.out.println("Rendering 3");
        if(secondaryDrive != null){
            System.out.println("Rendering 4");
            json += secondaryDrive.render().replace("drive\":", "secondary\":") + ",";
            json += "\"secondaryDrive\" : true";
        }else{
            json += "\"secondaryDrive\" : false";
        }
        System.out.println("Rendering 5");
        json += ",";
        json += "\"price\":" + getPrice();
        System.out.println("Rendering 6");
        return json;
    }

}
