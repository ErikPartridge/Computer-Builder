package com.computerevaluator.ai;

import com.computerevaluator.models.Computer;
import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.interactive.InteractiveSelection;

import java.util.Random;

/**
 * Created by erik on 7/3/16.
 */
public class ComputerEvolutionEngine extends GenerationalEvolutionEngine<Computer>{

    public ComputerEvolutionEngine(CandidateFactory<Computer> candidateFactory, EvolutionaryOperator<Computer> evolutionScheme, FitnessEvaluator<? super Computer> fitnessEvaluator, SelectionStrategy<? super Computer> selectionStrategy, Random rng){
        super(candidateFactory, evolutionScheme, fitnessEvaluator, selectionStrategy, rng);
        System.out.println("Completed construction");
    }

    public ComputerEvolutionEngine(CandidateFactory<Computer> candidateFactory, EvolutionaryOperator<Computer> evolutionScheme, InteractiveSelection<Computer> selectionStrategy, Random rng){
        super(candidateFactory, evolutionScheme, selectionStrategy, rng);
        throw new NullPointerException();
    }
}
