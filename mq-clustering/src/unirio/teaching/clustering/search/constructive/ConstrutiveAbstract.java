package unirio.teaching.clustering.search.constructive;

import unirio.teaching.clustering.search.model.ModuleDependencyGraph;

/**
 * Abstract constructive method
 * 
 * @author User
 */
public abstract class ConstrutiveAbstract
{
	public abstract int[] createSolution(ModuleDependencyGraph mdg);
}