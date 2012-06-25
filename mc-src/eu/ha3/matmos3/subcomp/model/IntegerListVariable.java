package eu.ha3.matmos3.subcomp.model;

import java.util.List;

import eu.ha3.matmos3.units.model.VariableUnit;

public interface IntegerListVariable extends VariableUnit
{
	public List<Integer> getIntegers();
	public boolean hasInteger(int integer);
}
