package unirio.teaching.clustering.search;

import java.util.ArrayList;
import java.util.HashMap;

import unirio.teaching.clustering.model.Project;
import unirio.teaching.clustering.model.ProjectClass;
import unirio.teaching.clustering.search.constructive.ConstrutiveAbstract;
import unirio.teaching.clustering.search.model.ClusterMetrics;
import unirio.teaching.clustering.search.model.ModuleDependencyGraph;
import unirio.teaching.clustering.search.utils.PseudoRandom;

/**
 * Iterated Local Search for the next release problem
 */
public class IteratedLocalSearch
{
	private int PERTURBATION_SIZE = 5;
	
	/**
	 * Constructive method
	 */
	private ConstrutiveAbstract constructor;

	/**
	 * Number of classes in the project
	 */
	private int classCount;
	
	/**
	 * Dependency graph for the project
	 */
	private ModuleDependencyGraph mdg;
	
	/**
	 * Number of fitness evaluations available in the budget
	 */
	private int maxEvaluations;

	/**
	 * Number of fitness evaluations executed
	 */
	private int evaluationsConsumed;

	/**
	 * Number of iterations to best solution
	 */
	private int iterationBestFound;
	
	/**
	 * MQ calculator
	 */
	private ClusterMetrics metrics;

	/**
	 * Fitness of the best solution
	 */
	private double bestFitness; 

	/**
	 * Initializes the ILS search process
	 */
	public IteratedLocalSearch(ConstrutiveAbstract constructor, Project project, int maxEvaluations) throws Exception
	{
		this.constructor = constructor;
		this.classCount = project.getClassCount();
		this.mdg = buildGraph(project, this.classCount);
		this.maxEvaluations = maxEvaluations;
		this.evaluationsConsumed = 0;
		this.iterationBestFound = 0;
		this.metrics = null;
		this.bestFitness = -1_000_000_000_000.0;
	}
	
	/**
	 * Builds the project's dependency graph from its representation
	 */
	private ModuleDependencyGraph buildGraph(Project project, int classCount) throws Exception
	{
		ModuleDependencyGraph mdg = new ModuleDependencyGraph(classCount);
		
		for (int i = 0; i < classCount; i++)
		{
			ProjectClass _class = project.getClassIndex(i);

			for (int j = 0; j < _class.getDependencyCount(); j++)
			{
				String targetName = _class.getDependencyIndex(j).getElementName();
				int classIndex = project.getClassIndex(targetName);
				
				if (classIndex != -1)
				mdg.addModuleDependency(i, classIndex, 1);
			}
		}
		
		return mdg;
	}

	/**
	 * Returns the number of evaluations consumed during the search
	 */
	public int getEvaluationsConsumed()
	{
		return evaluationsConsumed;
	}

	/**
	 * Returns the maximum number of evaluations to be consumed
	 */
	public int getMaximumEvaluations()
	{
		return maxEvaluations;
	}
	
	/**
	 * Returns the iteration on which the best solution was found
	 */
	public int getIterationBestFound()
	{
		return iterationBestFound;
	}

	/**
	 * Returns the best fitness found
	 */
	public double getBestFitness()
	{
		return bestFitness;
	}
	
	/**
	 * Main loop of the algorithm
	 */
	public int[] execute() throws Exception
	{
		long startTimestamp = System.currentTimeMillis();

		int[] bestSolution = constructor.createSolution(mdg);
		this.metrics = new ClusterMetrics(mdg, bestSolution);

		this.bestFitness = metrics.calculateMQ();
		++evaluationsConsumed;

		long finishTimestamp = System.currentTimeMillis();
		long seconds = (finishTimestamp - startTimestamp);
		
		setAgglomerativeTimeMs(seconds);
		setAgglomerativeMQ(bestFitness);

		startTimestamp = System.currentTimeMillis();

		localSearch(this.metrics);
		double fitness = metrics.calculateMQ();
		++evaluationsConsumed;
		
		if (fitness > this.bestFitness)
		{
			bestSolution = this.metrics.cloneSolution();
			this.bestFitness = fitness;
		}
		
		while (getEvaluationsConsumed() < getMaximumEvaluations())
		{
			applyPerturbation(this.metrics, PERTURBATION_SIZE);
			
			localSearch(this.metrics);
			fitness = metrics.calculateMQ();
			++evaluationsConsumed;
			
			if (fitness > this.bestFitness)
			{
				bestSolution = this.metrics.cloneSolution();
				this.bestFitness = fitness;
			}
		}

		return bestSolution;
	}

	/**
	 * Applies the perturbation operator upon a solution
	 */
	private void applyPerturbation(ClusterMetrics calculator, int amount)
	{
		for (int i = 0; i < amount; i++)
		{
			int source = PseudoRandom.randInt(0, classCount-1);
			int target = PseudoRandom.randInt(0, classCount-1);
			calculator.makeMovement(source, target);
		}
	}

	/**
	 * Performs the local search starting from a given solution
	 */
	private void localSearch(ClusterMetrics calculator)
	{
		while (visitNeighbors(calculator))
			;
	}

	/**
	 * Runs a neighborhood visit starting from a given solution
	 */
	private boolean visitNeighbors(ClusterMetrics calculator)
	{
		if (evaluationsConsumed > maxEvaluations)
			return false;

		int source = -1;
		int target = -1;
		double bestGain = Double.NEGATIVE_INFINITY;
		
		for (int i = 0; i < classCount; i++)
		{
			int newPackage = PseudoRandom.randInt(0, classCount-1);
			double gain = calculator.calculateMovementDelta(i, newPackage);
			++evaluationsConsumed;

			if (gain > bestGain)
			{
				source = i;
				target = newPackage;
				bestGain = gain;
			}
		}

		if (bestGain > 0)
		{
			calculator.makeMovement(source, target);
			return true;
		}
		
		return false;
	}

	public void print(Project project, int[] solution)
	{
		HashMap<Integer, ArrayList<String>> clusters = getClusters(project, solution);

		for (Integer i : clusters.keySet()) 
		{
			System.out.println("-" + i);
			for (String s : clusters.get(i))
			{
				System.out.println("--" + s);
			}
		}
	}

	private HashMap<Integer, ArrayList<String>> getClusters(Project project, int[] solution){
		HashMap<Integer, ArrayList<String>> clusters = new HashMap<>();

		for (int i = 0; i < solution.length; i++)
		{
			int cluster = solution[i];
			String className = project.getClassIndex(i).getName();

			if (!clusters.containsKey(cluster))
			{
				ArrayList<String> nodes = new ArrayList<>();
				nodes.add(className);
				clusters.put(cluster, nodes);
			}
			else 
			{
				clusters.get(cluster).add(className);
			}
		}

		return clusters;
	}

	private String findParentCluster(HashMap<Integer, ArrayList<String>> clusters, String value)
	{
		for (Integer i : clusters.keySet()) 
		{
			if (clusters.get(i).contains(value))
				return i.toString();
		}

		return "";
	}

	public String buildJson(Project project, int[] solution, Boolean showPackageOnly)
	{
		HashMap<Integer, ArrayList<String>> clusters = getClusters(project, solution);

		StringBuilder str = new StringBuilder();
 
        str.append("[");

		for (Integer i : clusters.keySet()) 
		{
			str.append("{ \"group\": \"nodes\", \"data\": { \"id\": \"" + i + "\" } },");

			for (String s : clusters.get(i))
			{
				str.append("{ \"group\": \"nodes\", \"data\": { \"id\": \"" + s + "\"");
				str.append(", \"parent\": \"" + i + "\"");
				str.append(" } },");

				ProjectClass _class = project.getClassName(s);

				for (int j = 0; j < _class.getDependencyCount(); j++)
				{
					String targetName = _class.getDependencyIndex(j).getElementName();

					String source = showPackageOnly && !clusters.get(i).contains(targetName) ? i.toString() : s;
					String target = showPackageOnly && !clusters.get(i).contains(targetName) ? findParentCluster(clusters, targetName) : targetName;

					String edgeId = "\"" + source + "_" + target + "\"";
					String alternativeEdgeId = "\"" + target + "_" + source + "\"";

					if (str.indexOf(edgeId) == -1 && str.indexOf(alternativeEdgeId) == -1)
					{
						str.append("{ \"group\": \"edges\", \"data\": { \"id\": " 
						+ edgeId
						+ ", \"source\": \"" + source
						+ "\", \"target\": \"" + target + "\" } },");
					}
				}
			}
		}

		// remove last comma
		str.deleteCharAt(str.length() - 1);

		str.append("]");

		String t = str.toString();

		return t;
	}

	private long agglomerativeTimeMs;
	public long getAgglomerativeTimeMs() {
		return agglomerativeTimeMs;
	}

	public void setAgglomerativeTimeMs(long agglomerativeTimeMs) {
		this.agglomerativeTimeMs = agglomerativeTimeMs;
	}

	private double agglomerativeMQ;

	public double getAgglomerativeMQ() {
		return agglomerativeMQ;
	}

	public void setAgglomerativeMQ(double agglomerativeMQ) {
		this.agglomerativeMQ = agglomerativeMQ;
	}
}