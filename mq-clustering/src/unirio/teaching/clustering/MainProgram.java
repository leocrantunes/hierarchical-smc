package unirio.teaching.clustering;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import unirio.teaching.clustering.model.Project;
import unirio.teaching.clustering.reader.CDAReader;
import unirio.teaching.clustering.reader.DependencyReader;
import unirio.teaching.clustering.search.IteratedLocalSearch;
import unirio.teaching.clustering.search.constructive.ConstrutiveAbstract;
import unirio.teaching.clustering.search.constructive.ConstrutiveAglomerativeMQ;

public class MainProgram {
	private static String BASE_DIRECTORY = "/home/leo/github/ils-clustering/data/clustering/deps-temp";

	public static final void main(String[] args) throws Exception {
    	BufferedWriter writer = new BufferedWriter(new FileWriter("/home/leo/github/ils-clustering/data/clustering/results_mq.csv"));
		// writer.write("Iteration;Project;Classes;Clusters;Fitness;Time(ms);Memory(MB);AgglomerativeTime(ms);AgglomerativeMQ");
		writer.write("Iteration;ProjectName;StdDev;ClassCount;ClustersCount;Fitness;ExecutionTime;Memory;ExecutionAborted");
		writer.write("\n");

		// Just ensure folder exists, don't cleanup
		String jsonFolderName = "/home/leo/github/ils-clustering/data/clustering/results_mq";
		File folder = new File(jsonFolderName);
		if (!folder.exists()) {
			folder.mkdirs();
			System.out.println("Created results folder: " + jsonFolderName);
		} else {
			System.out.println("Using existing results folder: " + jsonFolderName + " (skipping cleanup)");
		}
    
		for (int i = 0; i < 30; i++) {
			File file = new File(BASE_DIRECTORY);
			DecimalFormat df4 = new DecimalFormat("0.0000");

			ConstrutiveAbstract constructor = new ConstrutiveAglomerativeMQ();

			for (String projectName : file.list()) {
				long startTimestamp = System.currentTimeMillis();

				DependencyReader reader = new DependencyReader();
				Project project = reader.load(BASE_DIRECTORY + "/" + projectName);

				IteratedLocalSearch ils = new IteratedLocalSearch(constructor, project, 100_000);
				int[] solution = ils.execute();

				long finishTimestamp = System.currentTimeMillis();
				long seconds = (finishTimestamp - startTimestamp);

				long memory = Runtime.getRuntime().freeMemory() / (1024 * 1024);
				
				String text = padLeft("" + i, 2) + " " +		
					padLeft(projectName, 20) + " " +
					padRight("" + project.getClassCount(), 10) +
					padRight("" + countClusters(solution), 10) + " " +
					padRight(df4.format(ils.getBestFitness()), 10) + " " +
					padRight("" + seconds, 10) + " ms " +
					padRight("" + memory, 10) + " MB" +
					padRight("" + ils.getAgglomerativeTimeMs(), 10) + " ms" +
					padRight(df4.format(ils.getAgglomerativeMQ()), 10);

				String text2 = i + ";" +		
					projectName + ";" +
					"0;" + // Assuming StdDev is not calculated, set to 0
					project.getClassCount() + ";" +
					countClusters(solution) + ";" +
					df4.format(ils.getBestFitness()) + ";" +
					seconds + ";" +
					memory + ";" +
					"false"; // Assuming execution is not aborted, set to false

				System.out.println(text);
				writer.write(text2);
				writer.write("\n");
				writer.flush();

				var json = ils.buildJson(project, solution, true);

				saveJsonToFile(json, projectName, i, jsonFolderName);

				if (i == 0) {
					var client = HttpClient.newHttpClient();
					
					var json2 = getJson(projectName, false, ils.buildJson(project, solution, false),
										project.getClassCount(), countClusters(solution), seconds,
										df4.format(ils.getBestFitness()));

					HttpRequest request = HttpRequest.newBuilder()
													.uri(URI.create("https://hmd-gen-api.rj.r.appspot.com/mq/graphs?forceUpdate=true"))
													.header("content-type", "application/json")
													.POST(HttpRequest.BodyPublishers.ofString(json))
													.build();
					
					client.send(request, HttpResponse.BodyHandlers.ofString());
					
					request = HttpRequest.newBuilder()
										.uri(URI.create("https://hmd-gen-api.rj.r.appspot.com/mq/graphs?forceUpdate=true"))
										.header("content-type", "application/json")
										.POST(HttpRequest.BodyPublishers.ofString(json2))
										.build();
					
					client.send(request, HttpResponse.BodyHandlers.ofString());
				}
			}
		}

		writer.close();
	}

	private static void saveJsonToFile(String jsonContent, String projectName, int iteration, String folderName) {
		try {
			String filename = String.format("%s/%s_iteration_%s.json", folderName, projectName, iteration);
			
			// Write raw JSON to file (just the graphJson content)
			try (PrintWriter jsonWriter = new PrintWriter(new FileWriter(filename))) {
				jsonWriter.println(jsonContent);
				System.out.println("JSON saved to: " + filename);
			}
		} catch (IOException e) {
			System.err.println("Error saving JSON file: " + e.getMessage());
		}
	}

	private static int countClusters(int[] solution) {
		List<Integer> clusters = new ArrayList<Integer>();

		for (int i = 0; i < solution.length; i++) {
			int cluster = solution[i];

			if (!clusters.contains(cluster))
				clusters.add(cluster);
		}

		return clusters.size();
	}

	public static String padLeft(String s, int length) {
		StringBuilder sb = new StringBuilder();
		sb.append(s);

		while (sb.length() < length)
			sb.append(' ');

		return sb.toString();
	}

	public static String padRight(String s, int length) {
		StringBuilder sb = new StringBuilder();

		while (sb.length() < length - s.length())
			sb.append(' ');

		sb.append(s);
		return sb.toString();
	}

	private static String getJson(String projectName, Boolean packageOnly, String json, Integer classCount, int packagesCount, long ms, String fitness) {
		StringBuilder sb = new StringBuilder();

		sb.append("{ \"name\" : \"");
		sb.append(projectName);
		sb.append("\", \"packageOnly\" : \"");
		sb.append(packageOnly.toString());
		sb.append("\", \"numberOfClasses\" : \"");
		sb.append(classCount.toString());
		sb.append("\", \"numberOfPackages\" : \"");
		sb.append(packagesCount);
		sb.append("\", \"executionTimeMs\" : \"");
		sb.append(ms);
		sb.append("\", \"fitness\" : \"");
		sb.append(fitness);
		sb.append("\", \"elements\" : ");
		sb.append(json);
		sb.append(" }");

		return sb.toString();
	}
}