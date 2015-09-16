package com.alfredvc.constraint_satisfaction;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import search_algorithm.SearchAlgorithmResult;

/**
 * Created by Alfredvc on 9/6/2015.
 */
public class ConstraintSatisfactionResult<T> {
    private final Map<String, Variable<T>> variables;
    private final int violatedConstraints;
    private final int variablesWithDomainNotEqualToOne;
    private final int generatedNodes;
    private final int nodesPoppedFromTheAgenda;
    private final int solutionLength;

    private final Status status;
    public ConstraintSatisfactionResult(List<Variable<T>> vars, BitSet[] bitSets,
                                        SearchAlgorithmResult<ConstraintSatisfactionState<T>> searchAlgorithmResult, int violatedConstraints) {
        variables = new HashMap<>();
        for (int i = 0; i < vars.size(); i++) {
            Variable<T> toAdd = new Variable<>(vars.get(i));
            toAdd.packageGetDomain().setView(bitSets[i]);
            variables.put(vars.get(i).getName(), toAdd);
        }
        this.status = Status.valueOf(searchAlgorithmResult.getStatus().name());
        this.violatedConstraints = violatedConstraints;
        this.generatedNodes = searchAlgorithmResult.getGeneratedNodes();
        this.solutionLength = searchAlgorithmResult.getSolutionLength();
        this.nodesPoppedFromTheAgenda = searchAlgorithmResult.getPoppedNodes();
        int variablesWithDomainNotEqualToOne = 0;
        for (BitSet bitSet : bitSets) if (bitSet.cardinality() != 1) variablesWithDomainNotEqualToOne++;
        this.variablesWithDomainNotEqualToOne = variablesWithDomainNotEqualToOne;
    }



    public Status getStatus() {
        return status;
    }

    public Map<String, Variable<T>> getVariables() {
        return variables;
    }

    public int getViolatedConstraints() {
        return violatedConstraints;
    }

    public int getVariablesWithDomainNotEqualToOne() {
        return variablesWithDomainNotEqualToOne;
    }

    public int generatedNodes() {
        return generatedNodes;
    }

    public int getNodesPoppedFromTheAgenda() {
        return nodesPoppedFromTheAgenda;
    }

    public int getSolutionLength() {
        return solutionLength;
    }

    @Override
    public String toString() {
        return "ConstraintSatisfactionResult{" +
                "variables=" + Arrays.toString(variables.entrySet().toArray()) +
                '}';
    }
    public enum Status {
        FAILED,
        SUCCEEDED
    }
}
