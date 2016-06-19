package com.computerevaluator.ai;

import com.computerevaluator.Result;
import com.computerevaluator.models.Computer;
import com.computerevaluator.models.Settings;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.islands.IslandEvolution;
import org.uncommons.watchmaker.framework.islands.IslandEvolutionObserver;
import org.uncommons.watchmaker.framework.islands.RingMigration;
import org.uncommons.watchmaker.framework.operators.IdentityOperator;
import org.uncommons.watchmaker.framework.operators.Replacement;
import org.uncommons.watchmaker.framework.operators.SplitEvolution;
import org.uncommons.watchmaker.framework.selection.*;
import org.uncommons.watchmaker.framework.termination.GenerationCount;
import org.uncommons.watchmaker.framework.termination.Stagnation;
import org.uncommons.watchmaker.framework.termination.TargetFitness;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by erik on 6/14/16.
 */
public class Engine{


    public static void main(String[] args){
        final ArrayList<Computer> best = new ArrayList<>();
        Settings settings = new Settings(.7, 1, 800,850, 1, .7);
        for(int i = 0; i < 1; i++ ){
            //final Result allTimeBest = new Result();
            EvolutionEngine<Computer> engine = new GenerationalEvolutionEngine<Computer>(new ComputerFactory(),new SplitEvolution<Computer>(new ComputerCrossover(1), new Replacement<Computer>(new ComputerFactory(), new Probability(.9)), .94), (new ComputerEvaluator(settings)),new RouletteWheelSelection(), new MersenneTwisterRNG());

            engine.addEvolutionObserver((PopulationData<? extends Computer> populationData) -> {
                System.out.println("--------------");
                System.out.println("Generation: " + populationData.getGenerationNumber());
                System.out.println(populationData.getBestCandidate().toString());
                //System.out.println("Score: " + new ComputerEvaluator(settings).getFitness(populationData.getBestCandidate(), null));
                System.out.println("Fitness: " + populationData.getBestCandidateFitness() + "/" + populationData.getMeanFitness());
            });
            Computer result = engine.evolve(600, 20, new Stagnation(12, true));
          //  allTimeBest.updateIfBetter(result, new ComputerEvaluator(settings).getFitness(result, null));
           // result = allTimeBest.getComputer();

            if(result.getPrice() > settings.hardBudget){
                i--;
                System.out.println("ERROR");
            }else if(!result.compatible()){
                i--;
                System.out.println("Compatibility error");
            }else{
                System.out.println("RESULT:");
                System.out.println(result.toString());
                System.out.println(new ComputerEvaluator(settings).getFitness(result,null));
            }
             best.sort((o1, o2) -> {
                ComputerEvaluator fit = new ComputerEvaluator(settings);
                return(int) (fit.getFitness(o1, null) - fit.getFitness(o2, null));
            });

        }
    }
}
