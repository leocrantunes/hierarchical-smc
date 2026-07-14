package unirio.teaching.clustering.search.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class that represents a module containing nodes
 * 
 * @author User
 */
public class Module
{
    private String name;
	private Module parent;

	protected List<Module> modules;
    protected List<Node> nodes;

	private List<Module> removedModules;
    private List<Node> removedNodes;

	private List<Module> addedModules;
    private List<Node> addedNodes;

	private Double psi;

	private int nodesSize;
	private int modulesSize;
	private Integer numberOfEdges;

	static HashMap<Double, Double> calculations = new HashMap<>();
	static HashMap<Double, Double> calculationsLog2 = new HashMap<>();
    
    /**
     * Initializes the module
     */
    public Module(String name) 
    {
    	this.name = name;
    	this.parent = null;
    	this.modules = new ArrayList<Module>();
    	this.nodes = new ArrayList<Node>();
		this.addedModules = new ArrayList<Module>();
    	this.addedNodes = new ArrayList<Node>();
		this.removedModules = new ArrayList<Module>();
    	this.removedNodes = new ArrayList<Node>();

		this.psi = null;
		this.nodesSize = 0;
		this.modulesSize = 0;
		this.numberOfEdges = null;
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

	public Module addModule(Module module)
	{
		return addModule(module, false);
	}

    /**
     * Adds a submodule to the module
     */
	public Module addModule(Module module, boolean addHistory)
	{
		modules.add(module);
		module.setParent(this);

		if (addHistory)
			addedModules.add(module);

		modulesSize++;

		return this;
	}

	public Module removeModule(Module module)
	{
		return removeModule(module, false);
	}

	/**
     * Removes a submodule to the module
     */
	public Module removeModule(Module module, boolean addHistory)
	{
		modules.remove(module);

		if (module.getParent() == this)
			module.setParent(null);

		if (addHistory)
			removedModules.add(module);

		modulesSize--;
		
		return this;
	}

	/**
	 * Returns the number of submodules
	 */
	public int countModules()
	{
		return modulesSize;
	}

	/**
	 * Returns a submodule, given its index
	 */
	public Module getModuleByIndex(int index)
	{
		return modules.get(index);
	}

	/**
	 * Returns a module, given its name
	 */
	public Module getModuleByName(String name)
	{
		if (this.name.compareToIgnoreCase(name) == 0)
			return this;
		
		for (Module module : modules)
		{
			Module submodule = module.getModuleByName(name);

			if (submodule != null)
				return submodule;
		}		
		
		return null;
	}

	/**
	 * Returns all submodules
	 */
	public Iterable<Module> getModules()
	{
		return modules;
	}

	/**
	 * Returns all nodes
	 */
	public Iterable<Node> getNodes()
	{
		return nodes;
	}

	public Module addNode(Node node)
	{
		return addNode(node, false);
	}
	/**
	 * Adds a node into the module
	 */
	public Module addNode(Node node, boolean addHistory)
	{
		nodes.add(node);
		node.setParent(this);

		if (addHistory)
			addedNodes.add(node);

		nodesSize++;

		return this;
	}

	public Module removeNode(Node node)
	{
		return removeNode(node, false);
	}

	/**
	 * Removes a node from the module
	 */
	public Module removeNode(Node node, boolean addHistory)
	{
		nodes.remove(node);

		if (node.getParent() == this)
			node.setParent(null);

		if (addHistory)
			removedNodes.add(node);

		nodesSize--;

		return this;
	}

	/**
	 * Returns the number of nodes in the module
	 */
	public int countNodes()
	{
		return nodesSize;
	}

	/**
	 * Returns a node, given its index
	 */
	public Node getNodeByIndex(int index)
	{
		return nodes.get(index);
	}

	/**
	 * Returns a node, given its name
	 */
	public Node getNodeByName(String name)
	{
		for (Node node : nodes)
		{
			if (node.getName().compareToIgnoreCase(name) == 0)
				return node;
		}		
		
		for (Module module : modules)
		{
			Node node = module.getNodeByName(name);

			if (node != null)
				return node;
		}		
		
		return null;
	}

	/**
	 * Returns a node, given its name
	 */
	public int getNumberOfModules()
	{
		int modulesCount = 1;
		
		for (Module module : modules)
		{
			modulesCount += module.getNumberOfModules();
		}		
		
		return modulesCount;
	}

	/**
	 * Builds the incoming links of the nodes in the module and its submodules
	 */
	public void buildIncomingLinks()
	{
		for (Node node : nodes)
		{
			for (Node outgoingNode : node.getOutgoingLinks())
			{
				outgoingNode.addIncomingLink(node);
			}
		}

		for (Module module : modules)
		{
			module.buildIncomingLinks();
		}
	}

	/**
	 * Builds the incoming links of the nodes in the module and its submodules
	 */
	public void buildOutgoingLinks(Module source)
	{
		for (Node node : source.getNodes())
		{
			Node targetNode = this.getParent().getNodeByName(node.getName());
			if (targetNode == null) 
			{
				targetNode = new Node(node.getId(), node.getName());
				Module targetNodeModule = this.getParent() != null ?
								          this.getParent().getModuleByName(targetNode.getName()) :
				                          this.getModuleByName(targetNode.getName());
				if (targetNodeModule == null)
				{
					targetNodeModule = new Module(targetNode.getName());
					this.getParent().addModule(targetNodeModule);
				}
				targetNodeModule.addNode(targetNode);
			}
			for (Node outgoingNode : node.getOutgoingLinks())
			{
				Node targetOutgoingNode = this.getParent().getNodeByName(outgoingNode.getName());
				if (targetOutgoingNode == null) 
				{
					targetOutgoingNode = new Node(outgoingNode.getId(), outgoingNode.getName());
					Module targetOutgoingNodeModule = this.getParent() != null ?
								          this.getParent().getModuleByName(targetNode.getName()) :
				                          this.getModuleByName(targetNode.getName());
					if (targetOutgoingNodeModule == null)
					{
						targetOutgoingNodeModule = new Module(targetOutgoingNode.getName());
						this.getParent().addModule(targetOutgoingNodeModule);
					}
					targetOutgoingNodeModule.addNode(targetOutgoingNode);
				}
				targetNode.addOutgoingLink(targetOutgoingNode);
			}
		}

		for (Module module : source.getModules())
		{
			Module targetModule = this.getParent() != null ? 
			                      this.getParent().getModuleByName(module.getName()) :
								  this.getModuleByName(module.getName());
			if (targetModule == null)
			{
				targetModule = new Module(module.getName());
				Module parent = this.getParent() != null ? this.getParent() : this;
				parent.addModule(targetModule);
			}
			targetModule.buildOutgoingLinks(module);
		}
	}

	public boolean noChanges() {
		boolean noChanges = this.addedModules.isEmpty() && this.removedModules.isEmpty()
		           && this.addedNodes.isEmpty() && this.removedNodes.isEmpty();

		if (!noChanges) 
			return noChanges;

		for (Module m : this.modules) {
			boolean rm = m.noChanges();
			if (!rm)
				return rm;
		}

		return noChanges;
	}

	/**
	 * 
	 */
	public double psi(Boolean singleModule)
    {
		if (this.psi != null && noChanges())
			return this.psi;

		int entities = nodesSize;
		int submodules = modulesSize;
		double result = codeLength(entities + submodules + 1);

		if (!singleModule)
			result += codeLength(submodules + 1);

		int numberOfEdges = calculateNumberOfEdges();

		result += codeLength(numberOfEdges + 1);

		result -= calculateFrequencyComponent();

		result += numberOfEdges;
		
		if (singleModule)
			result += 1 + codeLength(1);

		this.psi = result;

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
			double nodeFrequency = node.getInDegree() + node.getOutDegree();
			if (nodeFrequency > 0) 
			{
				double logRatio = nodeFrequency / totalFrequency;
				result += nodeFrequency * log2(logRatio);
			}
		}		

		for (Module module : modules)
		{
			double moduleFrequency = module.calculateInDegree() + module.calculateOutDegree() + 1;
			double logRatio = moduleFrequency / totalFrequency;
			result += moduleFrequency * log2(logRatio);
		}		

		return result;
	}

	private int calculateTotalFrequency()
	{
		int result = 0;
		
		for (Node node : nodes)
		{
			result += node.getInDegree() + node.getOutDegree();
		}		

		for (Module module : modules)
		{
			result += module.calculateInDegree() + module.calculateOutDegree() + 1;
		}

		return result;
	}

	private int calculateNumberOfEdges()
	{
		if (this.numberOfEdges != null && this.addedNodes.isEmpty() && this.removedNodes.isEmpty()) {
			return this.numberOfEdges;
		}

		int result = 0;
		
		for (Node node : nodes)
		{
			for (Node link : node.getOutgoingLinks())
			{
				if (link != null) 
				{
					result++;
				}
			}
		}
		
		this.numberOfEdges = result;
		
		return result;
	}

	private int calculateInDegree()
	{
		int result = 0;
		
		for (Node node : nodes)
		{
			for (Node link : node.getIncomingLinks())
			{
				if (!hasNode(link))
					result++;
			}
		}		
		
		for (Module module : modules)
			result += module.calculateInDegree() + 1;

		return result;
	}

	private int calculateOutDegree()
	{
		int result = 0;
		
		for (Node node : nodes)
		{
			for (Node link : node.getOutgoingLinks())
			{
				if (!hasNode(link))
					result++;
			}
		}		
		
		for (Module module : modules)
			result += module.calculateOutDegree();

		return result;
	}
	
	protected boolean hasNode(Node node)
	{
		if (nodes.contains(node))
			return true;
		
		for (Module module : modules)
		{
			if (module.hasNode(node))
				return true;
		}		
		
		return false;
	}

	public double codeLength(double n) 
    {
		Double prev = n;
		Double cache = calculations.get(prev);
		if (cache != null) 
			return cache;

		double result = log2(2.865064);
		while (n > 0) 
		{
			n = log2(n);
			if (n > 0)
			{
				result += n;
			}
		}

		calculations.put(prev, result);

		return result;
    }
    
    public double log2(double n)
    {
		Double cache = calculationsLog2.get(n);
		if (cache != null) 
			return cache;
		
    	double result = Math.log(n) / Math.log(2);

		calculationsLog2.put(n, result);

		return result;
    }

	public void getAllModules(List<Module> result)
	{
		for (Module module : modules)
		{
			result.add(module);
			module.getAllModules(result);
		}
	}

	public void print(String prefix) {
		System.out.println(prefix + "(M)" + getName());
		
		for (Node n : nodes) {
			System.out.println(prefix + "-(N)" + n.getName());
		}

		for (Module m : modules) {
			if (m.countNodes() == 0 && m.countModules() == 0)
				continue;
			
			m.print(prefix + "-");
		}
	}

	public void buildJson(StringBuilder str, Boolean showPackageOnly) {
		if (getParent() != null)
		{
			str.append("{ \"group\": \"nodes\", \"data\": { \"id\": \"" + getName() + "\", \"parent\": \"" + getParent().getName() + "\" } },");
		}
		
		for (Node n : nodes) {
			str.append("{ \"group\": \"nodes\", \"data\": { \"id\": \"" + n.getName() + "\"");
			if (n.getParent() != null && n.getParent().getParent() != null)
			{
				str.append(", \"parent\": \"" + getName() + "\"");
			}
			str.append(" } },");

			for (Node on : n.getOutgoingLinks())
			{
				String source = showPackageOnly && n.getParent() != null && n.getParent().getParent() != null && n.getParent() != on.getParent()
								? n.getParent().getName() : n.getName();

				String target = showPackageOnly && on.getParent() != null && on.getParent().getParent() != null && n.getParent() != on.getParent()
								? on.getParent().getName() : on.getName();
				
				String edgeId = "\"" + source + target + "\"";
				String alternativeEdgeId = "\"" + target + source + "\"";

				if (str.indexOf(edgeId) == -1 && str.indexOf(alternativeEdgeId) == -1)
				{
					str.append("{ \"group\": \"edges\", \"data\": { \"id\": " 
					+ edgeId
					+ ", \"source\": \"" + source
					+ "\", \"target\": \"" + target + "\" } },");
				}
			}
		}

		for (Module m : modules) {
			if (m.countNodes() == 0 && m.countModules() == 0)
				continue;
			
			m.buildJson(str, showPackageOnly);
		}
	}

	public void moveNode(Node targetNode, Module toModule, boolean addHistory)
	{
		Module fromModule = targetNode.getParent();

		fromModule.removeNode(targetNode, addHistory);
		toModule.addNode(targetNode, addHistory);
	}

	public void moveNode(String targetNodeName, String toModuleName, boolean addHistory) 
	{
		Node targetNode = getNodeByName(targetNodeName);
		Module toModule = getModuleByName(toModuleName);

		moveNode(targetNode, toModule, addHistory);
	}

	public void moveModule(Module targetModule, Module toModule, boolean addHistory) 
	{	
		Module fromModule = targetModule.getParent();

		fromModule.removeModule(targetModule, addHistory);
		toModule.addModule(targetModule, addHistory);
	}

	public void moveModule(String targetModuleName, String toModuleName, boolean addHistory) 
	{
		Module targetModule = getModuleByName(targetModuleName);
		Module toModule = getModuleByName(toModuleName);
		
		moveModule(targetModule, toModule, addHistory);
	}

	public void restoreModules()
	{
		for (int i = 0; i < removedModules.size(); i++)
		{
			addModule(removedModules.get(i));
		}

		for (int i = 0; i < addedModules.size(); i++)
		{
			removeModule(addedModules.get(i));
		}

		for (int i = 0; i < removedNodes.size(); i++)
		{
			addNode(removedNodes.get(i));			
		}

		for (int i = 0; i < addedNodes.size(); i++)
		{
			removeNode(addedNodes.get(i));
		}

		resetHistory();

		for (Module m : modules) 
		{
			m.restoreModules();
		}
	}

	public void resetHistory() 
	{
		addedModules.clear();
		removedModules.clear();
		addedNodes.clear();
		removedNodes.clear();

		this.psi = null;
		this.numberOfEdges = null;
	}
}