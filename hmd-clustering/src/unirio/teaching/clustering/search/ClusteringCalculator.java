package unirio.teaching.clustering.search;

import unirio.teaching.clustering.model.Project;
import unirio.teaching.clustering.model.ProjectClass;

public class ClusteringCalculator
{
	private int classCount;
	private int packageCount;
	private int[][] dependencies;

	/**
	 * Inicializa o calculador de acoplamento
	 */
	public ClusteringCalculator(Project project, int packageCount) throws Exception
	{
		this.classCount = project.getClassCount();
		this.packageCount = packageCount;
		prepareDependenciesAmongClasses(project);
	}

	/**
	 * Retorna o numero de classes
	 */
	public int getClassCount()
	{
		return classCount;
	}

	/**
	 * Retorna o numero de pacotes
	 */
	public int getPackageCount()
	{
		return packageCount;
	}
	
	/**
	 * Prepara as classes para serem processadas pelo programa
	 */
	private void prepareDependenciesAmongClasses(Project project) throws Exception
	{
		this.dependencies = new int[classCount][classCount];
		
		for (int i = 0; i < classCount; i++)
		{
			ProjectClass _class = project.getClassIndex(i);

			for (int j = 0; j < _class.getDependencyCount(); j++)
			{
				String targetName = _class.getDependencyIndex(j).getElementName();
				int classIndex = project.getClassIndex(targetName);
				
				if (classIndex == -1)
					throw new Exception ("Class not registered in project: " + targetName);
				
				dependencies[i][classIndex]++;
			}
		}
	}
}