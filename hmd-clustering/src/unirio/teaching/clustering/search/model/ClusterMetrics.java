package unirio.teaching.clustering.search.model;

import java.util.ArrayList;

/**
 * Metricas utilizadas para calulos e operacoes na clusterizacao
 * 
 * @author kiko
 */
public class ClusterMetrics
{
	private final Decomposition mdg;
	
	public ClusterMetrics(Decomposition mdg)
	{
		this.mdg = mdg;
	}

	/**
	 * Calcula a alteracao que a funcao objetivo sofrera com o movimento
	 */
	public double calculateMovementDelta(int fromCluster, int toCluster)
	{
		if (fromCluster == toCluster)
			return 0d; // mover para o próprio cluster

		Module m = mdg.getModuleByIndex(fromCluster);
		Module mLine = mdg.getModuleByIndex(toCluster);

		// verificar o delta da uniao desses dois clusteres
		double currentDelta = calculateMergeClustersDelta(m, mLine);

		return currentDelta;
	}

	/**
	 * Retorna o número total de clusteres existentes na solução
	 * 
	 * @return
	 */
	public int getTotalClusteres()
	{
		return this.mdg.getNumberOfModules();
	}

	/**
	 * Retorna o MDG da instância
	 * 
	 * @return
	 */
	public Decomposition getMdg()
	{
		return mdg;
	}

    public double psiRoot() 
	{
        return mdg.psi();
    }

    public double calculateMergeClustersDelta(Module m, Module mLine)
	{
		double psiBefore = mdg.psi();
	
		mdg.mergeModules(m, mLine, true);

		double psiAfter = mdg.psi();

		mdg.restoreModules();

        return psiBefore - psiAfter;
    }

	public void mergeClusters(int i, int j)
	{
		Module m = mdg.getModuleByIndex(i);
		Module mLine = mdg.getModuleByIndex(j);

		mdg.mergeModules(m, mLine, false);
    }

	public double calculateAddParentClusterDelta(Module m, Module mLine)
	{
		double psiBefore = mdg.psi();
	
		mdg.moveChildModule(m, mLine, true);

		double psiAfter = mdg.psi();

		mdg.restoreModules();

        return psiBefore - psiAfter;
    }

	public void addParentCluster(int i, int j)
	{
		Module m = mdg.getModuleByIndex(i);
		Module mLine = mdg.getModuleByIndex(j);

		mdg.moveChildModule(m, mLine, false);
    }

	public void removeModulesWithoutChildren()
	{
		double psiBefore = mdg.psi();
	
		removeModulesWithoutChildren(mdg.getRoot(), true);

		double psiAfter = mdg.psi();

		mdg.restoreModules();

		if (psiBefore > psiAfter)
		{
			removeModulesWithoutChildren(mdg.getRoot(), false);
		}
    }

	private boolean removeModulesWithoutChildren(Module module, boolean addHistory)
	{
		ArrayList<Module> modulesToRemove = new ArrayList<>();

		for (Module m : module.getModules())
		{
			boolean orphan = removeModulesWithoutChildren(m, addHistory);
			if (orphan) modulesToRemove.add(m); 
		}

		for (Module m : modulesToRemove)
		{
			module.removeModule(m, addHistory);
		}

		return module.countModules() + module.countNodes() == 0;
	}

	public void moveOnlyChildToGrandparent()
	{
		double psiBefore = mdg.psi();
	
		moveOnlyChildToGrandparent(mdg.getRoot(), true);

		double psiAfter = mdg.psi();

		mdg.restoreModules();

		if (psiBefore > psiAfter)
		{
			moveOnlyChildToGrandparent(mdg.getRoot(), false);
		}
    }

	private boolean moveOnlyChildToGrandparent(Module module, boolean addHistory)
	{
		ArrayList<Module> modulesToMove = new ArrayList<>();

		for (Module m : module.getModules())
		{
			boolean solo = moveOnlyChildToGrandparent(m, addHistory);
			if (solo) modulesToMove.add(m); 
		}

		for (Module m : modulesToMove)
		{
			for (int i = 0; i < m.countNodes(); i++)
			{
				module.moveNode(m.getNodeByIndex(i), m.getParent(), addHistory);
			}

			for (int i = 0; i < m.countModules(); i++)
			{
				module.moveModule(m.getModuleByIndex(i), m.getParent(), addHistory);
			}

			m.getParent().removeModule(m, addHistory);
		}

		boolean check = module.countModules() + module.countNodes() == 1;

		return check;
	}
}