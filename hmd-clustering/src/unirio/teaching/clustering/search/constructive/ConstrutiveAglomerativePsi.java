package unirio.teaching.clustering.search.constructive;

import unirio.teaching.clustering.search.model.ClusterMetrics;
import unirio.teaching.clustering.search.model.Decomposition;
import unirio.teaching.clustering.search.model.Module;

/**
 * Constructive aglomerative method
 * 
 * @author User
 */
public class ConstrutiveAglomerativePsi extends ConstrutiveAbstract
{
	@Override
	public void createSolution(Decomposition mdg)
	{
		aglomerateClustering(mdg);
	}

	private void aglomerateClustering(Decomposition mdg)
	{
		int n = mdg.getSize();
		ClusterMetrics cm = new ClusterMetrics(mdg);
		
		System.out.println("Starting agglomerative clustering - Initial modules: " + mdg.getRoot().countModules());

		long start = System.currentTimeMillis();

		int k = 1;
		while (n - k > 1)
		{
			// Progress indicator for merge phase
			if (k == 1 || k % 10 == 0 || (n - k) <= 5) {
				System.out.println("  Merge phase - Step " + k + "/" + (n-1) + " (modules remaining: " + mdg.getRoot().countModules() + ")");
			}
			
			// selecionar elementos para a aglutinacao
			int aglutinatei = -1;
			int aglutinatej = -1;
			Double currentMaxDelta = null;

			for (int i = 0; i < mdg.getRoot().countModules(); i++) 
			{
				Module m = mdg.getModuleByIndex(i);

				for (int j = 0; j < mdg.getRoot().countModules(); j++) 
				{
					if (i == j) continue;

					Module mLine = mdg.getModuleByIndex(j);

					// verificar o delta da uniao desses dois clusteres
					double currentDelta = cm.calculateMergeClustersDelta(m, mLine);

					if (currentMaxDelta == null || currentDelta > currentMaxDelta)
					{
						aglutinatei = i;
						aglutinatej = j;
						currentMaxDelta = currentDelta;
					}
				}
			}

			if (aglutinatei >= 0 && aglutinatej >= 0) {
				// algutinar elementos
				cm.mergeClusters(aglutinatei, aglutinatej);
			}

			k += 1;
		}

		long finish = System.currentTimeMillis();
		System.out.println("Merge phase completed - Final modules: " + mdg.getRoot().countModules() + 
						   " | Time: " + (finish - start) + " ms");
		
		System.out.println("Starting hierarchy construction phase...");
		start = System.currentTimeMillis();

		k = 1;
		while (n - k > 1)
		{
			// Progress indicator for hierarchy phase
			if (k == 1 || k % 10 == 0 || (n - k) <= 5) {
				System.out.println("  Hierarchy phase - Step " + k + "/" + (n-1));
			}
			
			// selecionar elementos para a aglutinacao
			int innerjoini = -1;
			int innerjoinj = -1;
			Double currentMaxDelta = null;
			
			for (int i = 0; i < mdg.getRoot().countModules(); i++) 
			{
				Module m = mdg.getModuleByIndex(i);

				for (int j = 0; j < mdg.getRoot().countModules(); j++) 
				{
					if (i == j) continue;

					Module mLine = mdg.getModuleByIndex(j);

					double currentDelta = cm.calculateAddParentClusterDelta(m, mLine);
					if (currentMaxDelta == null || currentDelta > currentMaxDelta)
					{
						innerjoini = i;
						innerjoinj = j;
						currentMaxDelta = currentDelta;
					}
				}
			}

			if (innerjoini >= 0 && innerjoinj >= 0) {
				// algutinar elementos
				cm.addParentCluster(innerjoini, innerjoinj);
			}
			
			k += 1;
		}

		finish = System.currentTimeMillis();
		System.out.println("Hierarchy phase completed | Time: " + (finish - start) + " ms");
		
		System.out.println("Cleaning up modules...");
		start = System.currentTimeMillis();

		cm.removeModulesWithoutChildren();
		cm.moveOnlyChildToGrandparent();
		
		finish = System.currentTimeMillis();
		System.out.println("Cleanup completed | Time: " + (finish - start) + " ms");
		System.out.println("Agglomerative clustering finished - Final structure ready");
	}
}