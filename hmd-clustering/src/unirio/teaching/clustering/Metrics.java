package unirio.teaching.clustering;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Metrics {
    private static final String INPUT_DIR = "data/clustering/metrics/input";
    private static final String COMMITS_DIR = "data/clustering/log_list_files";
    private static final String OUTPUT_FILE = "metrics_results.csv";
    
    private final Gson gson = new Gson();
    
    public static void main(String[] args) {
        Metrics metrics = new Metrics();
        try {
            metrics.generateMetricsFile();
            System.out.println("Metrics file generated successfully: " + OUTPUT_FILE);
        } catch (Exception e) {
            System.err.println("Error generating metrics: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void generateMetricsFile() throws IOException {
        List<ProjectMetrics> allMetrics = new ArrayList<>();
        
        // Process each algorithm directory
        String[] algorithms = {"results_original", "results_hmd", "results_crg", "results_mq"};
        
        for (String algorithm : algorithms) {
            Path algorithmPath = Paths.get(INPUT_DIR, algorithm);
            if (!Files.exists(algorithmPath)) {
                System.out.println("Warning: Algorithm directory not found: " + algorithmPath);
                continue;
            }
            
            try (Stream<Path> files = Files.list(algorithmPath)) {
                files.filter(file -> file.toString().endsWith(".json"))
                     .forEach(file -> {
                         try {
                             System.out.println("Processing file: " + file);
                             ProjectMetrics metrics = processJsonFile(file, algorithm);
                             if (metrics != null) {
                                 allMetrics.add(metrics);
                             }
                         } catch (Exception e) {
                             System.err.println("Error processing file: " + file + " - " + e.getMessage());
                         }
                     });
            }
        }
        
        // Write metrics to CSV
        writeMetricsToCSV(allMetrics);
    }
    
    private ProjectMetrics processJsonFile(Path jsonFile, String algorithm) throws IOException {
        String fileName = jsonFile.getFileName().toString();
        String projectName = extractProjectName(fileName);
        
        if (projectName == null) {
            System.out.println("Warning: Could not extract project name from: " + fileName);
            return null;
        }
        
        // Read and parse JSON
        String jsonContent = Files.readString(jsonFile);
        Type listType = new TypeToken<List<Element>>(){}.getType();
        List<Element> elements = gson.fromJson(jsonContent, listType);
        
        // Calculate metrics
        ProjectMetrics metrics = new ProjectMetrics();
        metrics.projectName = projectName;
        metrics.algorithm = extractAlgorithmVariant(algorithm, fileName);
        metrics.fileName = fileName;
        
        calculateMetrics(elements, metrics);
        
        // Load commit data if available
        loadCommitData(projectName, metrics);
        
        return metrics;
    }
    
    private String extractProjectName(String fileName) {
        // Extract project name from patterns:
        // _b01-{project-name} {number}C.odem.json (original, hmd)
        // _b01-{project-name} {number}C.odem_stddev_{value}.json (crg)
        // _b01-{project-name} {number}C.odem_iteration_{value}.json (mq)
        if (fileName.startsWith("_b01-") && fileName.contains("C.odem")) {
            String withoutPrefix = fileName.substring(5); // Remove "_b01-"
            int spaceIndex = withoutPrefix.indexOf(' ');
            if (spaceIndex > 0) {
                return withoutPrefix.substring(0, spaceIndex);
            }
        }
        return null;
    }
    
    private String extractAlgorithmVariant(String algorithm, String fileName) {
        switch (algorithm) {
            case "results_crg":
                // Extract stddev from CRG files
                if (fileName.contains("_stddev_")) {
                    String stddev = fileName.substring(fileName.indexOf("_stddev_") + 8);
                    stddev = stddev.substring(0, stddev.indexOf('.'));
                    return "crg_stddev_" + stddev;
                }
                return "crg";
            case "results_mq":
                // Extract iteration from MQ files
                if (fileName.contains("_iteration_")) {
                    String iteration = fileName.substring(fileName.indexOf("_iteration_") + 11);
                    iteration = iteration.substring(0, iteration.indexOf('.'));
                    return "mq_iteration_" + iteration;
                }
                return "mq";
            case "results_original":
                return "original";
            case "results_hmd":
                return "hmd";
            default:
                return algorithm;
        }
    }
    
    private void calculateMetrics(List<Element> elements, ProjectMetrics metrics) {
        List<Element> nodes = new ArrayList<>();
        List<Element> edges = new ArrayList<>();
        
        // Separate nodes and edges
        for (Element element : elements) {
            if ("nodes".equals(element.getGroup()) && !element.getData().getId().startsWith("M")) {
                nodes.add(element);
            } else if ("edges".equals(element.getGroup())) {
                edges.add(element);
            }
        }
        
        // Calculate basic metrics
        //metrics.numberOfNodes = (int) nodes.stream().filter(element -> ).count();
        metrics.numberOfNodes = nodes.size();
        metrics.numberOfEdges = edges.size();
        
        // Calculate package-related metrics
        calculatePackageMetrics(nodes, metrics);
        
        // Calculate MojoFM (placeholder implementation)
        metrics.mojoFM = calculateMojoFM(nodes, edges);
        
        // Store nodes for commit data processing
        metrics.nodes = nodes;
    }
    
    private void calculatePackageMetrics(List<Element> nodes, ProjectMetrics metrics) {
        // Group nodes by package (parent)
        Map<String, List<Element>> packageToClasses = new HashMap<>();
        Set<String> packages = new HashSet<>();
        
        for (Element node : nodes) {
            String parent = node.getData().getParent();
            if (parent != null && !parent.equals("root")) {
                packages.add(parent);
                packageToClasses.computeIfAbsent(parent, k -> new ArrayList<>()).add(node);
            }
        }
        
        metrics.numberOfPackages = packages.size();
        
        if (metrics.numberOfPackages > 0) {
            // Calculate average classes per package
            int totalClassesInPackages = packageToClasses.values().stream()
                .mapToInt(List::size)
                .sum();
            metrics.avgClassesPerPackage = (double) totalClassesInPackages / metrics.numberOfPackages;
        } else {
            metrics.avgClassesPerPackage = 0.0;
        }
    }
    
    private double calculateMojoFM(List<Element> nodes, List<Element> edges) {
        // Simplified MojoFM calculation
        // This is a placeholder - real MojoFM requires comparison between two clusterings
        if (nodes.isEmpty()) return 0.0;
        
        // Calculate clustering quality based on package distribution
        Map<String, Integer> packageSizes = new HashMap<>();
        for (Element node : nodes) {
            String parent = node.getData().getParent();
            if (parent != null && !parent.equals("root")) {
                packageSizes.merge(parent, 1, Integer::sum);
            }
        }
        
        if (packageSizes.isEmpty()) return 0.0;
        
        // Simple metric: inverse of package size variance (normalized)
        double avg = packageSizes.values().stream().mapToInt(Integer::intValue).average().orElse(0.0);
        double variance = packageSizes.values().stream()
            .mapToDouble(size -> Math.pow(size - avg, 2))
            .average().orElse(0.0);
        
        return variance > 0 ? 1.0 / (1.0 + variance / avg) : 1.0;
    }
    
    private void loadCommitData(String projectName, ProjectMetrics metrics) {
        // Try to find matching LogListFile
        Path commitsPath = Paths.get(COMMITS_DIR);
        if (!Files.exists(commitsPath)) {
            return;
        }
        
        String keyword = getKeyword(projectName);
        if (keyword.isEmpty()) {
            System.out.println("Warning: No keyword mapping found for project: " + projectName);
            return;
        }
        
        String logFileName = keyword + "_LogListFile.data";
        Path logFile = commitsPath.resolve(logFileName);
        
        if (Files.exists(logFile)) {
            try {
                parseCommitData(logFile, metrics);
            } catch (IOException e) {
                System.err.println("Error loading commit data for " + projectName + ": " + e.getMessage());
            }
        } else {
            System.out.println("Warning: LogListFile not found: " + logFile);
        }
    }
    
    private String getKeyword(String name) {
        if (name.toLowerCase().contains("aep")) return "aep-core";
        if (name.toLowerCase().contains("jmetal")) return "jMetal";
        if (name.toLowerCase().contains("jgit")) return "jgit";
        if (name.toLowerCase().contains("javageom")) return "javaGeom";
        if (name.toLowerCase().contains("elastic")) return "elasticsearch";
        if (name.toLowerCase().contains("junit")) return "junit";
        if (name.toLowerCase().contains("scribejava")) return "scribejava";
        if (name.toLowerCase().contains("jodamoney") || name.toLowerCase().contains("joda-money")) return "jodamoney";
        if (name.toLowerCase().contains("moquette")) return "moquette";
        if (name.toLowerCase().contains("jodatime") || name.toLowerCase().contains("joda-time")) return "jodatime";
        if (name.toLowerCase().contains("lucene")) return "lucene";
        if (name.toLowerCase().contains("javacc")) return "javacc";
        if (name.toLowerCase().contains("log4j")) return "log4j";
        if (name.toLowerCase().contains("dubbo")) return "dubbo";
        if (name.toLowerCase().contains("gson")) return "gson";

        return "";
    }
    
    private void parseCommitData(Path commitFile, ProjectMetrics metrics) throws IOException {
        List<String> lines = Files.readAllLines(commitFile);
        
        if (lines.isEmpty()) {
            return;
        }
        
        // Get the clustering elements to map classes to packages
        Map<String, String> classToPackageMap = buildClassToPackageMap(metrics);
        
        // Parse commit data following the LogListFile format
        Map<String, Set<String>> packagesPerCommit = new HashMap<>();
        String lastRevision = "";
        Set<String> lastPackages = new HashSet<>();
        
        // Skip the first line (header)
        boolean skipFirst = true;
        
        for (String line : lines) {
            if (skipFirst) {
                skipFirst = false;
                continue;
            }
            
            if (line.trim().isEmpty()) continue;
            
            String[] tokens = line.trim().split("\\s+");
            if (tokens.length < 4) continue;
            
            try {
                String revision = tokens[1];
                String clazz = tokens[3];
                
                // Remove extension
                if (clazz.contains(".")) {
                    clazz = clazz.substring(0, clazz.lastIndexOf('.'));
                }
                
                // Get class name (last part after /)
                String className = clazz;
                if (clazz.contains("/")) {
                    className = clazz.substring(clazz.lastIndexOf('/') + 1);
                }
                
                // Get parent package for disambiguation (from file path)
                String lastParent = "";
                if (clazz.contains("/") && nthLastIndexOf(2, "/", clazz) >= 0) {
                    lastParent = clazz.substring(nthLastIndexOf(2, "/", clazz) + 1, nthLastIndexOf(1, "/", clazz));
                }
                
                // Find the package for this class using the enhanced matching logic
                String packageName = findPackageForClass(className, lastParent, classToPackageMap, metrics.nodes);
                if (packageName == null) {
                    continue; // Skip if we can't find the package
                }
                
                // Handle revision changes
                if (!revision.equals(lastRevision)) {
                    if (!lastPackages.isEmpty()) {
                        packagesPerCommit.put(lastRevision, new HashSet<>(lastPackages));
                    }
                    lastRevision = revision;
                    lastPackages.clear();
                }
                
                if (!hasPackage(lastPackages, packageName)) {
                    lastPackages.add(packageName);
                }
                
            } catch (Exception e) {
                // Skip malformed lines
                continue;
            }
        }
        
        // Don't forget the last commit
        if (!lastPackages.isEmpty()) {
            packagesPerCommit.put(lastRevision, new HashSet<>(lastPackages));
        }
        
        // Calculate metrics
        if (!packagesPerCommit.isEmpty()) {
            double totalCommits = packagesPerCommit.size();
            double singlePackageCommits = packagesPerCommit.values().stream()
                .mapToLong(packages -> packages.size() == 1 ? 1 : 0)
                .sum();
            
            double totalPackageCount = packagesPerCommit.values().stream()
                .mapToInt(Set::size)
                .sum();
            
            metrics.singlePackageCommitRatio = singlePackageCommits / totalCommits;
            metrics.avgPackageCommit = totalPackageCount / totalCommits;
        }
    }
    
    private int nthLastIndexOf(int n, String substring, String string) {
        int index = string.length();
        for (int i = 0; i < n; i++) {
            index = string.lastIndexOf(substring, index - 1);
            if (index == -1) {
                return -1;
            }
        }
        return index;
    }
    
    private boolean hasPackage(Set<String> packages, String packageName) {
        return packages.contains(packageName);
    }
    
    private Map<String, String> buildClassToPackageMap(ProjectMetrics metrics) {
        Map<String, String> classToPackageMap = new HashMap<>();
        
        if (metrics.nodes != null) {
            for (Element node : metrics.nodes) {
                String classId = node.getData().getId();
                String packageName = node.getData().getParent();
                
                if (classId != null && packageName != null && !packageName.equals("root")) {
                    classToPackageMap.put(classId, packageName);
                    
                    // Also map just the class name (without package prefix)
                    if (classId.contains(".")) {
                        String simpleClassName = classId.substring(classId.lastIndexOf('.') + 1);
                        classToPackageMap.put(simpleClassName, packageName);
                    }
                }
            }
        }
        
        return classToPackageMap;
    }
    
    private String findPackageForClass(String className, String lastParent, Map<String, String> classToPackageMap, List<Element> elements) {
        // Find elements that match the class name (following the original algorithm logic)
        List<Element> matchingElements = new ArrayList<>();
        
        for (Element element : elements) {
            String elementId = element.getData().getId();
            if (elementId != null && elementId.endsWith("." + className) && !elementId.startsWith("M")) {
                matchingElements.add(element);
            }
        }
        
        if (matchingElements.isEmpty()) {
            return null;
        }
        
        if (matchingElements.size() > 1) {
            // If we have multiple matches and lastParent is available, use it for disambiguation
            if (lastParent == null || lastParent.isEmpty()) {
                return null; // Can't disambiguate
            }
            
            List<Element> filteredElements = new ArrayList<>();
            for (Element element : matchingElements) {
                if (element.getData().getId().contains(lastParent)) {
                    filteredElements.add(element);
                }
            }
            
            if (filteredElements.size() != 1) {
                return null; // Still ambiguous or no matches
            }
            
            matchingElements = filteredElements;
        }
        
        // Return the package name of the matched element
        return matchingElements.get(0).getData().getParent();
    }
    
    private void writeMetricsToCSV(List<ProjectMetrics> allMetrics) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(OUTPUT_FILE))) {
            // Write header
            writer.println("projectName,algorithm,fileName,numberOfNodes,numberOfEdges,numberOfPackages," +
                          "avgClassesPerPackage,mojoFM,singlePackageCommitRatio,avgPackageCommit");
            
            // Write data
            for (ProjectMetrics metrics : allMetrics) {
                writer.printf("%s,%s,%s,%d,%d,%d,%.4f,%.6f,%.6f,%.6f%n",
                    metrics.projectName,
                    metrics.algorithm,
                    metrics.fileName,
                    metrics.numberOfNodes,
                    metrics.numberOfEdges,
                    metrics.numberOfPackages,
                    metrics.avgClassesPerPackage,
                    metrics.mojoFM,
                    metrics.singlePackageCommitRatio,
                    metrics.avgPackageCommit);
            }
        }
        
        System.out.println("Generated metrics for " + allMetrics.size() + " files");
    }
}
