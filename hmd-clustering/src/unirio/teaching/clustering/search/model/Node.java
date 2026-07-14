package unirio.teaching.clustering.search.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a node
 * 
 * @author User
 */
public class Node
{
	private int id;
    private String name;
	private Module parent;

	private List<Node> outgoingLinks;
    private List<Node> incomingLinks;
    
    /**
     * Initializes the node
     */
    public Node(int id, String name)
    {
		this.id = id;
    	this.name = name;
    	this.parent = null;
    	this.outgoingLinks = new ArrayList<Node>();
    	this.incomingLinks = new ArrayList<Node>();
    }

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Module getParent() {
		return parent;
	}

	public void setParent(Module parent) {
		this.parent = parent;
	}
    
    /**
     * Adds a link starting in the node
     */
	public Node addOutgoingLink(Node target)
	{
		outgoingLinks.add(target);
		return this;
	}

	/**
	 * Returns all links starting in the node
	 */
	public Iterable<Node> getOutgoingLinks()
	{
		return outgoingLinks;
	}

	/**
	 * Returns the number of links starting in the node
	 */
	public int getOutDegree()
	{
		return outgoingLinks.size();
	}
	
	/**
	 * Adds a link terminating in the node 
	 */
	public void addIncomingLink(Node node)
	{
		this.incomingLinks.add(node);
	}

	/**
	 * Returns all links terminating in the node
	 */
	public Iterable<Node> getIncomingLinks()
	{
		return incomingLinks;
	}

	/**
	 * Returns the number of links terminating in the node
	 */
	public int getInDegree()
	{
		return incomingLinks.size();
	}

	/**
	 * Determines whether the node is connected
	 */
	public boolean isConnected()
	{
		return !incomingLinks.isEmpty() || !outgoingLinks.isEmpty();
	}
}