package unirio.teaching.clustering.search.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Class that represents a node
 * 
 * @author User
 */
public class WeightedNode extends Node
{
	private Map<WeightedNode, Double> weightedOutgoingLinks;
    private Map<WeightedNode, Double> weightedIncomingLinks;
    
    /**
     * Initializes the node
     */
    public WeightedNode(int id, String name)
    {
		super(id, name);

    	this.weightedOutgoingLinks = new HashMap<WeightedNode, Double>();
    	this.weightedIncomingLinks = new HashMap<WeightedNode, Double>();
    }
    
    /**
     * Adds a link starting in the node
     */
	public WeightedNode addWeightedOutgoingLink(WeightedNode target, Double weight)
	{
		weightedOutgoingLinks.put(target, weight);
		return this;
	}

	/**
	 * Returns all links starting in the node
	 */
	public Iterable<Entry<WeightedNode, Double>> getWeightedOutgoingLinks()
	{
		return weightedOutgoingLinks.entrySet();
	}

	/**
	 * Returns the number of links starting in the node
	 */
	public Double getWeightedOutDegree()
	{
		return weightedOutgoingLinks.values().stream().filter(w -> w != null).collect(Collectors.summingDouble(Double::doubleValue));
	}
	
	/**
	 * Adds a link terminating in the node 
	 */
	public void addWeightedIncomingLink(WeightedNode node, Double weight)
	{
		this.weightedIncomingLinks.put(node, weight);
	}

	/**
	 * Returns all links terminating in the node
	 */
	public Iterable<Entry<WeightedNode, Double>> getWeightedIncomingLinks()
	{
		return weightedIncomingLinks.entrySet();
	}

	/**
	 * Returns the number of links terminating in the node
	 */
	public double getWeightedInDegree()
	{
		return weightedIncomingLinks.values().stream().filter(w -> w != null).collect(Collectors.summingDouble(Double::doubleValue));
	}

	/**
	 * Determines whether the node is connected
	 */
	@Override
	public boolean isConnected()
	{
		return !weightedIncomingLinks.isEmpty() || !weightedOutgoingLinks.isEmpty();
	}
}
