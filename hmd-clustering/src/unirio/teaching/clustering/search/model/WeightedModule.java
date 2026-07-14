package unirio.teaching.clustering.search.model;

import java.util.Map.Entry;

/**
 * Class that represents a module containing nodes with weighted edges
 * 
 * @author User
 */
public class WeightedModule extends Module
{
    /**
     * Initializes the module
     */
    public WeightedModule(String name) 
    {
		super(name);
    }

	/**
	 * Builds the incoming links of the nodes in the module and its submodules
	 */
	@Override
	public void buildIncomingLinks()
	{
		for (Node node : this.nodes)
		{
			WeightedNode weightedNode = (WeightedNode) node;

			for (Entry<WeightedNode, Double> outgoingNode : weightedNode.getWeightedOutgoingLinks())
			{
				outgoingNode.getKey().addWeightedIncomingLink(weightedNode, outgoingNode.getValue());
			}
		}

		for (Module module : modules)
		{
			module.buildIncomingLinks();
		}
	}

	/**
	 * 
	 */
	@Override
	public double psi(Boolean singleModule)
    {
		int entities = nodes.size();
		int submodules = modules.size();
		double result = codeLength(entities + submodules + 1);

		if (!singleModule)
			result += codeLength(submodules + 1);

		double numberOfEdges = calculateNumberOfEdges();

		result += codeLength(numberOfEdges + 1);

		result -= calculateFrequencyComponent();

		result += numberOfEdges;
		
		if (singleModule)
			result += 1 + codeLength(1);

		return result;
    }

	private double calculateFrequencyComponent()
	{
		double result = 0;
		double totalFrequency = calculateTotalFrequency();

		if (totalFrequency == 0)
			return 0;

		for (Node node : nodes)
		{
			WeightedNode weightedNode = (WeightedNode) node;

			double nodeFrequency = weightedNode.getWeightedInDegree() + weightedNode.getWeightedOutDegree();
			if (nodeFrequency > 0) 
			{
				double logRatio = nodeFrequency / totalFrequency;
				if (logRatio == 1.0) logRatio -= 0.000001; // avoid log(1)
				result += log2(logRatio);
			}
		}		

		for (Module module : modules)
		{
			WeightedModule weightedModule = (WeightedModule) module;

			double moduleFrequency = weightedModule.calculateInDegree() + weightedModule.calculateOutDegree() + 1;
			double logRatio = moduleFrequency / totalFrequency;
			if (logRatio == 1.0) logRatio -= 0.000001; // avoid log(1)
			result += log2(logRatio);
		}		

		return result;
	}


	private double calculateTotalFrequency()
	{
		double result = 0;
		
		for (Node node : nodes)
		{
			WeightedNode weightedNode = (WeightedNode) node;
			result += weightedNode.getWeightedInDegree() + weightedNode.getWeightedOutDegree();
		}		

		for (Module module : modules)
		{
			WeightedModule weightedModule = (WeightedModule) module;
			result += weightedModule.calculateInDegree() + weightedModule.calculateOutDegree() + 1;
		}

		return result;
	}

	private double calculateNumberOfEdges()
	{
		double result = 0;
		
		for (Node node : nodes)
		{
			WeightedNode weightedNode = (WeightedNode) node;

			for (Entry<WeightedNode, Double> link : weightedNode.getWeightedOutgoingLinks())
			{
				if (link != null && link.getValue() != null)
				{
					result += link.getValue();
				}
			}
		}		
		
		return result;
	}

	private double calculateInDegree()
	{
		double result = 0;
		
		for (Node node : nodes)
		{
			WeightedNode weightedNode = (WeightedNode) node;

			for (Entry<WeightedNode, Double> link : weightedNode.getWeightedIncomingLinks())
			{
				if (!hasNode(link.getKey()))
					result++;
			}
		}		
		
		for (Module module : modules) {
			WeightedModule weightedModule = (WeightedModule) module;
			result += weightedModule.calculateInDegree() + 1;
		}

		return result;
	}

	private double calculateOutDegree()
	{
		double result = 0;
		
		for (Node node : nodes)
		{
			WeightedNode weightedNode = (WeightedNode) node;
			for (Entry<WeightedNode, Double> link : weightedNode.getWeightedOutgoingLinks())
			{
				if (!hasNode(link.getKey()))
					result++;
			}
		}		
		
		for (Module module : modules)
		{
			WeightedModule weightedModule = (WeightedModule) module;
			result += weightedModule.calculateOutDegree();
		}

		return result;
	}

	@Override
	public void buildJson(StringBuilder str, Boolean showPackageOnly) {
		if (getParent() != null)
		{
			str.append("{ \"group\": \"nodes\", \"data\": { \"id\": \"" + getName() + "\", \"parent\": \"" + getParent().getName() + "\" } },");
		}
		
		for (Node n : nodes) {
			WeightedNode node = (WeightedNode) n;

			str.append("{ \"group\": \"nodes\", \"data\": { \"id\": \"" + node.getName() + "\"");
			if (node.getParent() != null && node.getParent().getParent() != null)
			{
				str.append(", \"parent\": \"" + getName() + "\"");
			}
			str.append(" } },");

			/*for (Entry<WeightedNode, Double> on : node.getWeightedOutgoingLinks())
			{
				String source = showPackageOnly && node.getParent() != null && node.getParent().getParent() != null && node.getParent() != on.getKey().getParent()
								? node.getParent().getName() : node.getName();

				String target = showPackageOnly && on.getKey().getParent() != null && on.getKey().getParent().getParent() != null && node.getParent() != on.getKey().getParent()
								? on.getKey().getParent().getName() : on.getKey().getName();
				
				String edgeId = "\"" + source + target + "\"";
				String alternativeEdgeId = "\"" + target + source + "\"";

				if (str.indexOf(edgeId) == -1 && str.indexOf(alternativeEdgeId) == -1)
				{
					str.append("{ \"group\": \"edges\", \"data\": { \"id\": " 
					+ edgeId
					+ ", \"source\": \"" + source
					+ "\", \"target\": \"" + target + "\" } },");
				}
			}*/
		}

		for (Module m : modules) {
			if (m.countNodes() == 0 && m.countModules() == 0)
				continue;
			
			m.buildJson(str, showPackageOnly);
		}
	}
}
