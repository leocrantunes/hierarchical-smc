package unirio.teaching.clustering;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import unirio.teaching.clustering.model.Project;
import unirio.teaching.clustering.reader.CDAReader;
import unirio.teaching.clustering.reader.DependencyReader;
import unirio.teaching.clustering.reader.ILoad;
import unirio.teaching.clustering.search.IteratedLocalSearch;
import unirio.teaching.clustering.search.constructive.ConstrutiveAbstract;
import unirio.teaching.clustering.search.constructive.ConstrutiveAglomerativePsi;

public class MainProgram
{	
	static Boolean ORIGINAL = false;
	static Boolean HMD = false;
	static Boolean COMMIT_RELATION = false;

	public static final void main(String[] args) throws Exception
	{
		// Parse command line arguments
		CommandLineArgs parsedArgs = parseCommandLineArgs(args);
		
		if (parsedArgs.showHelp) {
			printHelp();
			return;
		}
		
		// Set the type flags based on parsed arguments
		switch (parsedArgs.type.toLowerCase()) {
			case "original":
				ORIGINAL = true;
				break;
			case "hmd":
				HMD = true;
				break;
			case "crg":
				COMMIT_RELATION = true;
				break;
			default:
				System.err.println("Invalid type: " + parsedArgs.type);
				printHelp();
				return;
		}

		String currentDirectory = new File("").getAbsolutePath();
		Path baseDirectory = Path.of(currentDirectory, "data", "clustering", parsedArgs.dataFolder);

		File file = new File(baseDirectory.toFile().getAbsolutePath());
		DecimalFormat df4 = new DecimalFormat("0.0000");
		
		// Create single results file per type (overwrite existing if not skipping)
		String resultsFileName = "results_" + parsedArgs.type + ".csv";
		File resultsFile = new File(resultsFileName);
		
		// Handle folder creation based on skip-existing mode
		String jsonFolderName = "results_" + parsedArgs.type;
		if (parsedArgs.skipExisting) {
			// Just ensure folder exists, don't cleanup
			File folder = new File(jsonFolderName);
			if (!folder.exists()) {
				folder.mkdirs();
				System.out.println("Created results folder: " + jsonFolderName);
			} else {
				System.out.println("Using existing results folder: " + jsonFolderName + " (skipping cleanup)");
			}
		} else {
			// Clean up and create fresh folder (original behavior)
			cleanupAndCreateFolder(jsonFolderName);
		}
		
		System.out.println("Results will be saved to: " + resultsFileName + 
			(parsedArgs.skipExisting ? " (appending if exists)" : " (overwriting if exists)"));
		
		PrintWriter writer = null;
		try {
			// In skip mode, append to existing file; otherwise overwrite
			boolean appendMode = parsedArgs.skipExisting && resultsFile.exists();
			writer = new PrintWriter(new FileWriter(resultsFile, appendMode));
			
			// Write CSV header only if creating new file or not in append mode
			if (!appendMode) {
				writer.println("ProjectName;StdDev;ClassCount;ClustersCount;Fitness;ExecutionTime;Memory;ExecutionAborted");
			}
		} catch (IOException e) {
			System.err.println("Error creating results file: " + e.getMessage());
			return;
		}
		
		ConstrutiveAbstract constructor = new ConstrutiveAglomerativePsi();

		String[] stdDeviations = COMMIT_RELATION ? new String[] { "25", "50", "75" } : new String[] { "0" };
		
		for (String projectName : file.list())
		{		
			for (String stdDev : stdDeviations) 
			{
				// Check if we should skip this project when in skip-existing mode
				if (parsedArgs.skipExisting && projectAlreadyProcessed(projectName, jsonFolderName, stdDev, parsedArgs.type)) {
					System.out.println("Skipping already processed project: " + projectName + 
						(COMMIT_RELATION ? " (stdDev: " + stdDev + ")" : ""));
					continue;
				}
				
				long startTimestamp = System.currentTimeMillis();
				boolean isOdem = projectName.endsWith(".odem");
				
				String algorithmType = COMMIT_RELATION ? "CRG" : HMD ? "HMD" : "ORIGINAL";
				String fileType = isOdem ? "ODEM" : "DEPENDENCY";
				
				System.out.println(String.format(
					"Starting project: %s | Algorithm: %s | StdDev: %s | FileType: %s | Time: %s",
					projectName,
					algorithmType,
					stdDev,
					fileType,
					new SimpleDateFormat("HH:mm:ss").format(new Date())
				));

				ILoad reader = isOdem ? new CDAReader() : new DependencyReader();
				Project project = reader.load(baseDirectory.resolve(projectName).toString());

				IteratedLocalSearch ils = new IteratedLocalSearch(
					constructor, project, 100_000, Integer.parseInt(stdDev));

				if (COMMIT_RELATION) ils.executeCrg();
				else if (HMD) ils.execute();
				
				long finishTimestamp = System.currentTimeMillis();
				long seconds = (finishTimestamp - startTimestamp);

				int clustersCount = COMMIT_RELATION ? ils.getCommitRelationNumberOfModules() :
									HMD ? ils.getNumberOfModules() : project.getPackageCount();
				String fitness = COMMIT_RELATION ? df4.format(ils.getBestCommitRelationFitness()) :
								HMD ? df4.format(ils.getBestFitness()) : "0";
				
				long memory = Runtime.getRuntime().freeMemory() / (1024 * 1024);
				String resultLine = projectName + ";" + 
					stdDev + ";" + 
					project.getClassCount() + ";" +
					clustersCount + ";" + 
					fitness + ";" + 
					seconds + " ms;" + 
					memory + "MB;" + 
					ils.wasExecutionAborted();
				
				// Print to console and write to file
				System.out.println(resultLine);
				writer.println(resultLine);
				writer.flush(); // Ensure data is written immediately

				// Generate JSON data (always, regardless of API usage)
				String graphJson = COMMIT_RELATION ? ils.getCommitRelationJson(true) : 
								HMD ? ils.getJson(true) : ils.getOriginalJson(project, true);

				// Save raw JSON to file
				saveJsonToFile(graphJson, projectName, stdDev, parsedArgs.type, jsonFolderName);

				// Only send to visualization API if URL is provided
				if (parsedArgs.apiUrl != null && !parsedArgs.apiUrl.trim().isEmpty()) {
					var client = HttpClient.newHttpClient();

					String graphJson2 = COMMIT_RELATION ? ils.getCommitRelationJson(false) : 
										HMD ? ils.getJson(false) : ils.getOriginalJson(project, false);

					var json = getJson(projectName, true, graphJson, project.getClassCount(), clustersCount, seconds, fitness);
					var json2 = getJson(projectName, false, graphJson2, project.getClassCount(), clustersCount, seconds, fitness);

					String graphType = COMMIT_RELATION ? "crg" + stdDev : HMD ? "hmd" : "original";

					HttpRequest request = HttpRequest.newBuilder()
						.uri(URI.create(parsedArgs.apiUrl + "/" + graphType + "/graphs?forceUpdate=true"))
						.header("content-type", "application/json")
						.POST(HttpRequest.BodyPublishers.ofString(json))
						.build();
		
					client.send(request, HttpResponse.BodyHandlers.ofString());

					request = HttpRequest.newBuilder()
						.uri(URI.create(parsedArgs.apiUrl + "/" + graphType + "/graphs?forceUpdate=true"))
						.header("content-type", "application/json")
						.POST(HttpRequest.BodyPublishers.ofString(json2))
						.build();
		
					client.send(request, HttpResponse.BodyHandlers.ofString());
					
					System.out.println("Data sent to visualization API: " + parsedArgs.apiUrl);
				}
			}
		}
		
		// Close the file writer
		if (writer != null) {
			writer.close();
			System.out.println("Results saved to: " + resultsFile.getAbsolutePath());
		}
	}

	private static String getJson(String projectName, Boolean packageOnly, String json, Integer classCount, int packagesCount, long ms, String fitness)
	{
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

	public static String padLeft(String s, int length) 
	{
	    StringBuilder sb = new StringBuilder();
	    sb.append(s);
	    
	    while (sb.length() < length)
	        sb.append(' ');
	    
	    return sb.toString();
	}

	public static String padRight(String s, int length) 
	{
	    StringBuilder sb = new StringBuilder();
	    
	    while (sb.length() < length - s.length())
	        sb.append(' ');
	    
	    sb.append(s);
	    return sb.toString();
	}

	private static boolean projectAlreadyProcessed(String projectName, String jsonFolderName, String stdDev, String algorithmType) {
		// For CRG algorithm, check if JSON result file already exists for this project and standard deviation
		File jsonFile;
		if ("crg".equalsIgnoreCase(algorithmType)) {
			jsonFile = new File(jsonFolderName, projectName + "_stddev_" + stdDev + ".json");
		} else {
			jsonFile = new File(jsonFolderName, projectName + ".json");
		}
		return jsonFile.exists();
	}

	private static void saveJsonToFile(String jsonContent, String projectName, String stdDev, String algorithmType, String folderName) {
		try {
			// For CRG algorithm, include standard deviation in filename since we process multiple stdDevs
			String filename;
			if ("crg".equalsIgnoreCase(algorithmType)) {
				filename = String.format("%s/%s_stddev_%s.json", folderName, projectName, stdDev);
			} else {
				filename = String.format("%s/%s.json", folderName, projectName);
			}
			
			// Write raw JSON to file (just the graphJson content)
			try (PrintWriter jsonWriter = new PrintWriter(new FileWriter(filename))) {
				jsonWriter.println(jsonContent);
				System.out.println("JSON saved to: " + filename);
			}
		} catch (IOException e) {
			System.err.println("Error saving JSON file: " + e.getMessage());
		}
	}

	private static void cleanupAndCreateFolder(String folderName) {
		try {
			File folder = new File(folderName);
			
			// Remove existing folder and all its contents
			if (folder.exists()) {
				deleteDirectoryRecursively(folder);
				System.out.println("Cleaned up existing folder: " + folderName);
			}
			
			// Create fresh folder
			if (folder.mkdirs()) {
				System.out.println("Created clean folder: " + folderName);
			}
		} catch (Exception e) {
			System.err.println("Error cleaning up folder: " + e.getMessage());
		}
	}

	private static void deleteDirectoryRecursively(File directory) {
		if (directory.isDirectory()) {
			File[] files = directory.listFiles();
			if (files != null) {
				for (File file : files) {
					deleteDirectoryRecursively(file);
				}
			}
		}
		directory.delete();
	}

	// Command line argument parsing
	static class CommandLineArgs {
		String type = "hmd";
		String dataFolder = "odem-2nd-phase";
		String apiUrl = null; // Default: no API calls
		boolean showHelp = false;
		boolean skipExisting = false; // Default: cleanup and reprocess all
	}

	private static CommandLineArgs parseCommandLineArgs(String[] args) {
		CommandLineArgs parsedArgs = new CommandLineArgs();
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--help") || args[i].equals("-h")) {
				parsedArgs.showHelp = true;
				return parsedArgs;
			} else if (args[i].equals("--type")) {
				if (i + 1 < args.length) {
					parsedArgs.type = args[++i];
				} else {
					System.err.println("Error: --type requires a value");
					parsedArgs.showHelp = true;
					return parsedArgs;
				}
			} else if (args[i].equals("--data")) {
				if (i + 1 < args.length) {
					parsedArgs.dataFolder = args[++i];
				} else {
					System.err.println("Error: --data requires a value");
					parsedArgs.showHelp = true;
					return parsedArgs;
				}
			} else if (args[i].equals("--api")) {
				if (i + 1 < args.length) {
					parsedArgs.apiUrl = args[++i];
				} else {
					System.err.println("Error: --api requires a value");
					parsedArgs.showHelp = true;
					return parsedArgs;
				}
			} else if (args[i].equals("--skip-existing")) {
				parsedArgs.skipExisting = true;
			} else {
				System.err.println("Unknown argument: " + args[i]);
				parsedArgs.showHelp = true;
				return parsedArgs;
			}
		}
		
		return parsedArgs;
	}

	private static void printHelp() {
		System.out.println("ILS Clustering HMD - Command Line Tool");
		System.out.println();
		System.out.println("Usage: java MainProgram [OPTIONS]");
		System.out.println();
		System.out.println("Options:");
		System.out.println("  --type <TYPE>     Algorithm type: original, hmd, or crg (default: hmd)");
		System.out.println("  --data <FOLDER>   Data folder name under 'data/clustering' (default: odem-2nd-phase)");
		System.out.println("  --api <URL>       Optional: Visualization API base URL (default: no API calls)");
		System.out.println("  --skip-existing   Skip projects that already have JSON results and don't cleanup folders");
		System.out.println("  --help, -h        Show this help message");
		System.out.println();
		System.out.println("Examples:");
		System.out.println("  java MainProgram --type original --data odem-2nd-phase");
		System.out.println("  java MainProgram --type hmd --data my-odem");
		System.out.println("  java MainProgram --type crg --data new-odem --api https://hmd-gen-api.rj.r.appspot.com");
		System.out.println("  java MainProgram --type hmd --data my-odem --skip-existing");
		System.out.println("  java MainProgram --type hmd --data my-odem --api https://localhost:8080");
		System.out.println();
		System.out.println("Algorithm Types:");
		System.out.println("  original - Original odem file json generator");
		System.out.println("  hmd      - Hierarchical Module Decomposition (HMD) algorithm");
		System.out.println("  crg      - Commit Relation Graph (CRG) algorithm");
		System.out.println();
		System.out.println("Output Files:");
		System.out.println("  - CSV results file: results_<type>.csv");
		System.out.println("  - JSON files: results_<type>/<project>.json");
		System.out.println("    * For CRG algorithm: results_<type>/<project>_stddev_<25|50|75>.json");
		System.out.println();
		System.out.println("Note: By default, folders are cleaned and all projects are reprocessed.");
		System.out.println("      Use --skip-existing to preserve existing results and only process new projects.");
		System.out.println("      For CRG algorithm, each standard deviation (25, 50, 75) creates separate JSON files.");
		System.out.println("      Use --api parameter to enable visualization data upload.");
	}
}