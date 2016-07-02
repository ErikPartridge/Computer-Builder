package com.computerevaluator.ai;

import com.computerevaluator.models.Computer;
import com.computerevaluator.models.Result;
import com.computerevaluator.models.Settings;
import com.computerevaluator.models.Size;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.operators.Replacement;
import org.uncommons.watchmaker.framework.operators.SplitEvolution;
import org.uncommons.watchmaker.framework.selection.*;
import org.uncommons.watchmaker.framework.termination.GenerationCount;
import org.uncommons.watchmaker.framework.termination.Stagnation;

import java.util.ArrayList;

/**
 * Created by erik on 6/14/16.
 */
public class Engine implements Runnable{

    private final Settings settings;

    private final long id;

    public Engine(Settings s, long id){
        this.settings = s;
        this.id = id;
    }

    //@Override
    public void run(){
        int j = 0;
        for(int i = 0; i < 1; i++ ){
            //final Result allTimeBest = new Result();
            EvolutionEngine<Computer> engine = new GenerationalEvolutionEngine<Computer>(new ComputerFactory(),new SplitEvolution<Computer>(new ComputerCrossover(1), new Replacement<Computer>(new ComputerFactory(), new Probability(.9)), .94), (new ComputerEvaluator(settings)),new RouletteWheelSelection(), new MersenneTwisterRNG());

            engine.addEvolutionObserver((PopulationData<? extends Computer> populationData) -> {
                if(populationData.getGenerationNumber() % 100 == 0){
                    System.out.println("Generation : " + populationData.getGenerationNumber());
                }
            });
            Computer result = engine.evolve(250, 20, new Stagnation(80, true));
            //  allTimeBest.updateIfBetter(result, new ComputerEvaluator(settings).getFitness(result, null));
            // result = allTimeBest.getComputer();

            if(result.getPrice() > settings.hardBudget){
                i--;
                j++;
                System.out.println("ERROR");
            }else if(!result.compatible()){
                i--;
                j++;
                System.out.println("Compatibility error");
            }else{
                System.out.println("Complete");
                Result.addComputer(id, result);
            }
            if(j > 2){
                break;
            }
        }
    }
}
