package unirio.teaching.clustering.search;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

import unirio.teaching.clustering.model.Project;
import unirio.teaching.clustering.model.ProjectClass;
import unirio.teaching.clustering.search.constructive.ConstrutiveAbstract;
import unirio.teaching.clustering.search.model.ClusterMetrics;
import unirio.teaching.clustering.search.model.Decomposition;
import unirio.teaching.clustering.search.model.Node;
import unirio.teaching.clustering.search.model.Module;

/**
 * Iterated Local Search for the next release problem
 */
public class IteratedLocalSearch {
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
	private Decomposition mdg;

	/**
	 * Dependency graph for the project
	 */
	private Decomposition crg;

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
	 * PSI calculator
	 */
	private ClusterMetrics metrics;

	/**
	 * Fitness of the best solution
	 */
	private double bestFitness;

	/**
	 * PSI calculator (Commit Relation)
	 */
	private ClusterMetrics commitRelationMetrics;

	/**
	 * Fitness of the best solution
	 */
	private double bestCommitRelationFitness;

	/**
	 * Aborts the execution of the search
	 */
	private boolean abortExecution;

	/**
	 * Indicates if the execution was aborted due to standard deviation
	 */
	public boolean wasExecutionAborted() {
		return abortExecution;
	}

	/**
	 * Initializes the ILS search process
	 */
	public IteratedLocalSearch(ConstrutiveAbstract constructor, Project project, int maxEvaluations, int stdDeviation) throws Exception {
		this.abortExecution = false;
		this.constructor = constructor;
		this.classCount = project.getClassCount(); // project.getClassCount(); // 8;
		this.mdg = buildGraph(project, this.classCount); // buildGraph(project, this.classCount); // buildFakeGraph2(this.classCount);
		this.crg = stdDeviation > 0 ? buildCommitRelationGraph(project, this.mdg, stdDeviation) : null; // buildFakeCommitRelationGraph(project, this.classCount);
		this.maxEvaluations = maxEvaluations;
		this.evaluationsConsumed = 0;
		this.iterationBestFound = 0;
		this.metrics = null;
		this.bestFitness = -1_000_000_000_000.0;
		this.bestCommitRelationFitness = -1_000_000_000_000.0;
	}

	/**
	 * Builds the project's dependency graph from its representation
	 */
	private Decomposition buildGraph(Project project, int classCount) throws Exception {
		Decomposition decomposition = new Decomposition(classCount, null);

		for (int i = 0; i < classCount; i++) {
			ProjectClass _class = project.getClassIndex(i);
			Node sourceNode = decomposition.getNodeByName(_class.getName());
			Module module = decomposition.getModuleByName("M" + _class.getName());

			if (module == null) {
				module = new Module("M" + _class.getName());
				decomposition.getRoot().addModule(module);
			}

			if (sourceNode == null) {
				sourceNode = new Node(i, _class.getName());
				module.addNode(sourceNode);
			}

			for (int j = 0; j < _class.getDependencyCount(); j++) {
				String targetName = _class.getDependencyIndex(j).getElementName();
				Node targetNode = decomposition.getNodeByName(targetName);
				Module targetModule = decomposition.getModuleByName("M" + targetName);

				if (targetModule == null) {
					targetModule = new Module("M" + targetName);
					decomposition.getRoot().addModule(targetModule);
				}

				if (targetNode == null) {
					targetNode = new Node(j, targetName);
					targetModule.addNode(targetNode);
				}

				sourceNode.addOutgoingLink(targetNode);
			}
		}

		decomposition.prepare();

		return decomposition;
	}

	/**
	 * Builds the project's dependency graph from its representation
	 */
	private Decomposition buildCommitRelationGraph(Project project, Decomposition mdg, int stdDeviationPerc) throws Exception {
		String currentDirectory = new File("").getAbsolutePath();
		Path baseDirectory = Path.of(currentDirectory, "data", "clustering", "commits");
		Decomposition decomposition = new Decomposition(mdg.getSize(), null);
		FileInputStream fis = new FileInputStream(baseDirectory.resolve(project.getName() + ".txt").toFile());
		Scanner sc = new Scanner(fis);
		try {
			Double[][] matrix = new Double[decomposition.getSize()][decomposition.getSize()];
			HashMap<Integer, String> indexToClassName = new HashMap<>();

			int numberOfLines = 0;

			while (sc.hasNextLine()) {
				String line = sc.nextLine();

				if (line.length() > 0 && numberOfLines < decomposition.getSize()) {
					// split the line by space
					String[] parts = line.split(" ");
					String classFullName = parts[0];
					int index = project.getIndexForClass(project.getClassName(classFullName));

					Node node = new Node(index, classFullName);
					Module module = new Module("M" + classFullName);

					module.addNode(node);
					decomposition.getRoot().addModule(module);

					indexToClassName.put(numberOfLines, classFullName);

					// iterate over the parts and fill the matrix
					for (int j = 1; j < parts.length; j++) {
						matrix[numberOfLines][j - 1] = Double.parseDouble(parts[j]);
					}

					numberOfLines++;
				}
			}

			ArrayList<Double> newArray = new ArrayList<>();

			for (int i = 0; i < numberOfLines; i++) {
				for (int j = 0; j < numberOfLines; j++) {
					Double number = matrix[i][j];

					if (i >= j)
						continue;
					
					newArray.add(number);
				}
			}

			Collections.sort(newArray);
			
			Double perc = (((double) stdDeviationPerc) / 100);
			int index = (int) (newArray.size() * perc);
			Double point = newArray.get(index);

			System.out.println("Standard Deviation: " + stdDeviationPerc + "%, Point: " + point);

			if (point == 0.0) {
				this.abortExecution = true;
				return decomposition;
			}

			for (int i = 0; i < numberOfLines; i++) {
				String className = indexToClassName.get(i);
				Node node = (Node) decomposition.getRoot().getNodeByName(className);

				for (int j = 0; j < numberOfLines; j++) {
					if (i >= j) continue;

					String classLineName = indexToClassName.get(j);
					Node nodeLine = (Node) decomposition.getRoot().getNodeByName(classLineName);

					if (matrix[i][j] != null && matrix[i][j] >= point) {
						node.addOutgoingLink(nodeLine);
					}
				}
			}

			decomposition.add(mdg);
			decomposition.prepare();			

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			sc.close();
		}

		return decomposition;
	}

	private Module getParent(Decomposition decomposition, String packageName) {
		Module module = null;
		int dot = 0;

		dot = packageName.lastIndexOf('.');

		if (dot == -1)
			return decomposition.getRoot();

		packageName = packageName.substring(0, dot);

		module = decomposition.getModuleByName("M" + packageName);

		if (module == null) {
			module = new Module("M" + packageName);
			Module parent = getParent(decomposition, packageName);
			parent.addModule(module);
		}

		return module;
	}

	public Decomposition buildOriginalGraph(Project project, int classCount) throws Exception {
		Decomposition decomposition = new Decomposition(classCount, null);

		for (int i = 0; i < classCount; i++) {
			ProjectClass _class = project.getClassIndex(i);
			Node sourceNode = decomposition.getNodeByName(_class.getName());
			Module module = decomposition.getModuleByName("M" + _class.getPackage().getName());

			if (module == null) {
				module = new Module("M" + _class.getPackage().getName());
				Module parent = getParent(decomposition, _class.getPackage().getName());
				parent.addModule(module);
			}

			if (sourceNode == null) {
				sourceNode = new Node(i, _class.getName());
				module.addNode(sourceNode);
			}

			for (int j = 0; j < _class.getDependencyCount(); j++) {
				String targetName = _class.getDependencyIndex(j).getElementName();
				Node targetNode = decomposition.getNodeByName(targetName);
				ProjectClass _depClass = project.getClassName(targetName);
				Module targetModule = decomposition.getModuleByName("M" + _depClass.getPackage().getName());

				if (targetModule == null) {
					targetModule = new Module("M" + _depClass.getPackage().getName());
					Module parent = getParent(decomposition, _depClass.getPackage().getName());
					parent.addModule(targetModule);
				}

				if (targetNode == null) {
					targetNode = new Node(j, targetName);
					targetModule.addNode(targetNode);
				}

				sourceNode.addOutgoingLink(targetNode);
			}
		}

		decomposition.prepare();

		return decomposition;
	}

	/**
	 * Returns the number of evaluations consumed during the search
	 */
	public int getEvaluationsConsumed() {
		return evaluationsConsumed;
	}

	/**
	 * Returns the maximum number of evaluations to be consumed
	 */
	public int getMaximumEvaluations() {
		return maxEvaluations;
	}

	/**
	 * Returns the iteration on which the best solution was found
	 */
	public int getIterationBestFound() {
		return iterationBestFound;
	}

	/**
	 * Returns the best fitness found
	 */
	public double getBestFitness() {
		return this.bestFitness;
	}

	/**
	 * Returns the best fitness found
	 */
	public double getBestCommitRelationFitness() {
		return this.bestCommitRelationFitness;
	}

	/**
	 * Main loop of the algorithm
	 */
	public void execute() throws Exception {
		constructor.createSolution(mdg);
		this.metrics = new ClusterMetrics(mdg);
		this.bestFitness = metrics.psiRoot();
	}

	/**
	 * Main loop of the algorithm
	 */
	public void executeCrg() throws Exception {
		if (this.abortExecution) {
			System.out.println("Execution aborted due to standard deviation.");
			return;
		}
		
		constructor.createSolution(crg);
		this.commitRelationMetrics = new ClusterMetrics(crg);
		this.bestCommitRelationFitness = commitRelationMetrics.psiRoot();
	}

	public String getJson(Boolean showPackageOnly) {
		return this.mdg.getJson(showPackageOnly);
	}

	public String getCommitRelationJson(Boolean showPackageOnly) {
		return this.crg.getJson(showPackageOnly);
	}

	public Integer getNumberOfModules() {
		return this.mdg.getNumberOfModules();
	}

	public Integer getCommitRelationNumberOfModules() {
		return this.crg.getNumberOfModules();
	}

	public String getOriginalJson(Project project, Boolean showPackageOnly) {
		try {
			return this.buildOriginalGraph(project, project.getClassCount()).getJson(showPackageOnly);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	public void print() {
		this.mdg.print();
		this.crg.print();
	}
}