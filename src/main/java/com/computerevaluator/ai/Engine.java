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
        System.out.println("Running");
        int j = 0;
        for(int i = 0; i < 1; i++ ){
            System.out.println("In the loop");
            //final Result allTimeBest = new Result();
            ComputerFactory cf = new ComputerFactory();
            System.out.println("Factory done");
            SplitEvolution<Computer> ev = new SplitEvolution<Computer>(new ComputerCrossover(1), new Replacement<Computer>(cf, new Probability(.9)), .94);
            System.out.println("Evolution");
            ComputerEvaluator evaluator = new ComputerEvaluator(settings);
            System.out.println("Evaluator done");
            RouletteWheelSelection rws = new RouletteWheelSelection();
            System.out.println("Roulette wheel done");
            MersenneTwisterRNG rng = new MersenneTwisterRNG();
            System.out.println("Random done");

            EvolutionEngine<Computer> engine = new ComputerEvolutionEngine(cf, ev, evaluator, rws, rng);

            System.out.println("engine created");
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
