package unirio.teaching.clustering.search.model;

/**
 * Instancia representada como um Module Dependency Graph (MDG)
 * 
 * @author kiko
 */
public class ModuleDependencyGraph
{
	private String name;

	private int size;
	
	private int[][] dependencyWeight;		// peso das dependencias
	
	private int[][] dependencyCount;		// Contagem das dependencias entre m贸dulos

	private int[][] moduleDependencies; 	// primeiro indice 茅 o modulo e o segundo s茫o as dependencias.
	
	private int[] moduleDependenciesCount; 	// total de dependencias de cada m贸dulo

	private int totalDependencyCount;		// conta todas as dependencias.
	
	private int totalDependencyEdgeCount;	// conta as arestas existentes
	
	private boolean weighted = false;

	/**
	 * Cria um novo ModuleDependencyGraph - MDG
	 */
	public ModuleDependencyGraph(int moduleCount)
	{
		int totalModules = moduleCount;
		this.size = totalModules;
		dependencyWeight = new int[totalModules][totalModules];
		dependencyCount = new int[totalModules][totalModules];
		moduleDependencies = new int[totalModules][totalModules];
		moduleDependenciesCount = new int[totalModules];

		for (int i = 0; i < totalModules; i++)
		{
			moduleDependenciesCount[i] = 0;
			
			for (int j = 0; j < totalModules; j++)
			{
				dependencyWeight[i][j] = 0;
				dependencyCount[i][j] = 0;
				moduleDependencies[i][j] = -1;
			}
		}

		totalDependencyCount = 0;
		totalDependencyEdgeCount = 0;
	}

	/**
	 * Adiciona uma dependencia entre dois modulos pelo nome dos modulos
	 */
//	public int addModuleDependency(String module, String dependsOn, int weight)
//	{
//		int modulePosition = findModulePosition(module);
//		int dependsOnPosition = findModulePosition(dependsOn);
//		return addModuleDependency(modulePosition, dependsOnPosition, weight);
//	}

	/**
	 * Adiciona uma dependencia entre dois modulos pela posicao dos modulos com uma dependencia
	 */
	public int addModuleDependency(int module, int dependsOn, int weight)
	{
		return addModuleDependency(module, dependsOn, weight, 1);
	}

	/**
	 * Adiciona uma dependencia entre dois modulos pela posicao dos modulos com a quantidade de arestas existentes
	 */
	public int addModuleDependency(int module, int dependsOn, int weight, int qty)
	{
		if (module > dependsOn)
			return addModuleDependency(dependsOn, module, weight, qty);
		
		/*
		 * if(dependencyCount[module][dependsOn] == 0 && module != dependsOn){ //nao existe dependencia e nao e o
		 * proprio modulo moduleDependency[module][dependencyCount[module]++]=dependsOn; }
		 */
		if (dependencyCount[module][dependsOn] == 0)
		{
			totalDependencyEdgeCount++;// adicona mais uma dependencia nova
			moduleDependencies[module][moduleDependenciesCount[module]++] = dependsOn;
			if (module != dependsOn)
			{
				moduleDependencies[dependsOn][moduleDependenciesCount[dependsOn]++] = module;
			}
		}

		dependencyCount[module][dependsOn]++;// adiciona uma dependencia entre os m贸dulos
		dependencyWeight[module][dependsOn] += weight;// adiciona o peso atual
		totalDependencyCount += qty;// adiciona a dependencia no MDG
		return dependencyWeight[module][dependsOn];
	}

	/**
	 * Remove uma dependencia entre modulos
	 */
	public void removeModuleDependency(int module, int wasDependentOn)
	{
		if (module > wasDependentOn)
		{
			removeModuleDependency(wasDependentOn, module);
			return;
		}

		if (dependencyCount[module][wasDependentOn] > 0)
		{
			dependencyWeight[module][wasDependentOn] = 0;
			boolean existsDependency = removeDependencieInfo(module, wasDependentOn);
			if (existsDependency && module != wasDependentOn)
			{
				removeDependencieInfo(wasDependentOn, module);
			}

			int count = dependencyCount[module][wasDependentOn];
			dependencyCount[module][wasDependentOn] = 0;
			totalDependencyCount -= count;// remove o total de dependencias
			totalDependencyEdgeCount--;// remove a aresta como um todo
		}
	}

	/**
	 * Remove da lista de dependencias a informa莽茫o de dependencia entra dois m贸dulos
	 */
	private boolean removeDependencieInfo(int module1, int module2)
	{
		boolean found = false;

		if (module1 != module2)
		{
			for (int i = 0; i < moduleDependencies[module1].length; i++)
			{
				if (found)
				{
					moduleDependencies[module1][i - 1] = moduleDependencies[module1][i];
					moduleDependencies[module1][i] = -1;
				}
		
				if (!found && moduleDependencies[module1][i] == module2)
				{
					found = true;
				}
			}
		
			if (found)
			{
				moduleDependenciesCount[module1]--;
			}
		}
		
		return found;
	}

	/**
	 * Retorna a forca da dependencia entre dois modulos
	 */
	public int dependencyWeight(int module, int otherModule)
	{
		if (module > otherModule)
			return dependencyWeight(otherModule, module);

		return dependencyWeight[module][otherModule];
	}

	/**
	 * Retorna a quantidade de dependencias entre os dois modulos
	 */
	public int dependencyCount(int module, int otherModule)
	{
		if (module > otherModule)
			return dependencyCount(otherModule, module);
		
		return dependencyCount[module][otherModule];
	}

	/**
	 * Encontra a posicao de um modulo pelo seu nome
	 */
	/*private int findModulePosition(String moduleName)
	{
		int i = 0;
		
		for (String currentModuleName : moduleNames)
		{
			if (currentModuleName.equals(moduleName))
				return i;

			i++;
		}

		throw new RuntimeException("MODULE NOT FOUND");
	}*/

	/**
	 * Retorna o unico modulo conectado ao modulo module, ou -1 caso nao seja verdade
	 */
	public int getUniqueModuleDependency(int module)
	{
		if (dependencyCount[module][module] > 0)
			return -1;// modulo possui auto relacionamento. nao pode ser transformado em outro
		
		int connectedModule = -1;
		
		for (int i = 0; i < dependencyCount.length; i++)
		{
			if ((i < module && dependencyCount[i][module] > 0) || (i > module && dependencyCount[module][i] > 0))
			{
				if (connectedModule != -1)
				{
					return -1;
				}
				
				connectedModule = i;
			}
		}
		return connectedModule;
	}

	/**
	 * Junta um modulo que pode ser simplificado dentro do outro
	 */
	public void insertModuleInsideAnother(int moduleBase, int moduleToBeInserted)
	{
		int weight = dependencyWeight(moduleBase, moduleToBeInserted);
		int count = dependencyCount(moduleBase, moduleToBeInserted);
		removeModuleDependency(moduleBase, moduleToBeInserted);
		size--;
		addModuleDependency(moduleBase, moduleBase, weight, count);
	}

	public boolean checkHasDependency(int moduleA, int moduleB)
	{
		if (moduleA > moduleB)
			return dependencyCount[moduleB][moduleA] > 0;

		return dependencyCount[moduleA][moduleB] > 0;
	}

	/**
	 * Retorna o n鷐ero total de modulos
	 */
	public int getSize()
	{
		return size;
	}

	/**
	 * Lista com os modulos que possuem relacionamento com o modulo informado
	 */
	public int[] moduleDependencies(int module)
	{
		return moduleDependencies[module];
	}

	/**
	 * total de dependencias que o modulo informado possui
	 */
	public int moduleDependenciesCount(int module)
	{
		return moduleDependenciesCount[module];
	}

	/**
	 * Retorna o nome da instancia
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Altera o nome da inst鈔cia
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Retorna a quantidade de dependencias na instancia
	 */
	public int getTotalDependencyCount()
	{
		return totalDependencyCount;
	}

	public int getTotalDependencyEdgeCount()
	{
		return totalDependencyEdgeCount;
	}

	public boolean isWeighted()
	{
		return weighted;
	}

	public void setWeighted(boolean weighted)
	{
		this.weighted = weighted;
	}
}