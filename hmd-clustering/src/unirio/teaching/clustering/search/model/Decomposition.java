package unirio.teaching.clustering.search.model;

import java.util.ArrayList;
import java.util.List;

public class Decomposition
{
	private int size;
    private Module root;
    
    public Decomposition(int size, Module root)
    {
		if (root != null)
			this.root = root;
		else
			this.root = new Module("root");
		
		this.size = size;
    }

	public Decomposition(Decomposition base)
    {
		root = new Module(base.getRoot().getName());
		size = base.getSize();

		for (Module m : base.getAllModules())
		{
			Module newModule = new Module(m.getName());

			for (Node n : m.getNodes())
			{
				Node node = new Node(n.getId(), n.getName());
				
				for (Node nn : n.getOutgoingLinks())
				{
					Node nodeNn = new Node(nn.getId(), nn.getName());
					node.addOutgoingLink(nodeNn);
				}
				
				newModule.addNode(node);
			}

			for (Module im : m.getModules())
			{
				newModule.addModule(new Module(im.getName()));
			}
			
			root.addModule(newModule);
		}

		prepare();
    }

	public Module getRoot() {
		return root;
	}

	public int getSize() {
		return size;
	}

    public void prepare()
    {
    	root.buildIncomingLinks();
		root.resetHistory();
    }

	public void add(Decomposition source)
    {
		root.buildOutgoingLinks(source.getRoot());
    }

	public List<Module> getAllModules() 
	{
		List<Module> modules = new ArrayList<Module>();

    	root.getAllModules(modules);  

		return modules;
	}

	public List<Module> getAllModulesWithRoot() 
	{
		List<Module> modules = new ArrayList<Module>();

    	modules.add(root);
    	root.getAllModules(modules);  

		return modules;
	}
    
    public double psi()
    {
		List<Module> modules = getAllModulesWithRoot();
		
    	double result = 0.0;

		boolean singleModule = modules.size() == 1;

    	for (Module module : modules)
			result += module.psi(singleModule);

		if (!singleModule) result += 1;
    	
    	return result;
    }

	public Module getModuleByName(String name)
	{
		return root.getModuleByName(name);
	}

	public Module getModuleByIndex(int index)
	{
		return root.getModuleByIndex(index);
	}

	public Node getNodeByName(String name)
	{
		return root.getNodeByName(name);
	}

	public int getNumberOfModules()
	{
		return root.getNumberOfModules();
	}

	public void moveNode(String source, String target, boolean addHistory)
	{
		root.moveNode(source, target, addHistory);
	}

	public void removeModule(String moduleName)
	{
		root.removeModule(root.getModuleByName(moduleName));
	}

	public void mergeModules(Module fromModule, Module toModule, boolean addHistory)
	{
		if (fromModule == toModule || fromModule == null || toModule == null)
			return;

		for (int i = 0; i < fromModule.countModules(); i++) 
		{
			root.moveModule(fromModule.getModuleByIndex(i), toModule, addHistory);
		}

		for (int i = 0; i < fromModule.countNodes(); i++) 
		{
			root.moveNode(fromModule.getNodeByIndex(i), toModule, addHistory);
		}
	}

	public void restoreModules()
	{
		root.restoreModules();
	}

	public void moveChildModule(Module parentModule, Module childModule, boolean addHistory)
	{
		if (parentModule == childModule)
			return;
		
		Module oldParent = childModule.getParent();
		oldParent.moveModule(childModule, parentModule, addHistory);
	}

	public void print() 
	{
		System.out.println(this.psi());
		root.print("");
	}

	public String getJson(Boolean showPackageOnly) {
		StringBuilder str = new StringBuilder();
 
        str.append("[");

		root.buildJson(str, showPackageOnly);

		// remove last comma
		str.deleteCharAt(str.length() - 1);

		str.append("]");

		String t = str.toString();

		return t;
	}
}