package com.computerevaluator.ai;

import com.computerevaluator.Result;
import com.computerevaluator.models.Computer;
import com.computerevaluator.models.Settings;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.islands.IslandEvolution;
import org.uncommons.watchmaker.framework.islands.IslandEvolutionObserver;
import org.uncommons.watchmaker.framework.islands.RingMigration;
import org.uncommons.watchmaker.framework.selection.*;
import org.uncommons.watchmaker.framework.termination.GenerationCount;
import org.uncommons.watchmaker.framework.termination.Stagnation;

/**
 * Created by erik on 6/14/16.
 */
public class Engine{


    public static void main(String[] args){
        final Result allTimeBest = new Result();
        Settings settings = new Settings(1,1,0,0, 1, .7);
        EvolutionEngine<Computer> engine = new GenerationalEvolutionEngine<Computer>(new ComputerFactory(), new ComputerCrossover(1), new ComputerEvaluator(settings), new SigmaScaling(), new MersenneTwisterRNG());

        engine.addEvolutionObserver((PopulationData<? extends Computer> populationData) -> {
            /*if(populationData.getGenerationNumber() % 10 != 0){
                return;
            }*/
            System.out.println("--------------");
            System.out.println("Generation: " + populationData.getGenerationNumber());
            System.out.println(populationData.getBestCandidate().toString());
            //System.out.println("Score: " + new ComputerEvaluator(settings).getFitness(populationData.getBestCandidate(), null));
            System.out.println("Fitness: " + populationData.getBestCandidateFitness() + "/" + populationData.getMeanFitness());
            allTimeBest.updateIfBetter(populationData.getBestCandidate(), populationData.getBestCandidateFitness());
        });
        Computer result = engine.evolve(300, 20,  new Stagnation(30,true ));
        System.out.println("RESULT:");
        System.out.println(allTimeBest.getComputer().toString());
        System.out.println(allTimeBest.getScore());

      /*  IslandEvolution<Computer> island = new IslandEvolution<Computer>(5, new RingMigration(), new ComputerFactory(), new ComputerCrossover(1), new ComputerEvaluator(settings), new TournamentSelection(new Probability(.8)), new MersenneTwisterRNG());
        island.addEvolutionObserver(new IslandEvolutionObserver<Computer>(){
            @Override
            public void islandPopulationUpdate(int i, PopulationData<? extends Computer> populationData){

            }

            @Override
            public void populationUpdate(PopulationData<? extends Computer> populationData){
                System.out.println("--------------");
                System.out.println("Generation: " + populationData.getGenerationNumber());
                System.out.println(populationData.getBestCandidate().toString());
                //System.out.println("Score: " + new ComputerEvaluator(settings).getFitness(populationData.getBestCandidate(), null));
                System.out.println("Fitness: " + populationData.getBestCandidateFitness() + "/" + populationData.getMeanFitness());

            }
        });
        Computer result = island.evolve(100, 5, 50, 3, new Stagnation(1000, true, false));
        System.out.println("RESULT:");
        System.out.println(result.toString());*/
    }
}
