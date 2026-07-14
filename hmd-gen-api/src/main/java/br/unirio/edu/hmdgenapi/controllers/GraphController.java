package br.unirio.edu.hmdgenapi.controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.spring.data.firestore.FirestoreReactiveOperations;
import com.google.cloud.spring.data.firestore.FirestoreTemplate;

import br.unirio.edu.hmdgenapi.models.Element;
import br.unirio.edu.hmdgenapi.models.Graph;
import br.unirio.edu.hmdgenapi.models.GraphMetrics;
import br.unirio.edu.hmdgenapi.models.GraphRequest;
import br.unirio.edu.hmdgenapi.repositories.GraphRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
public class GraphController {
    private final String TYPE_ORIGINAL = "original";
    private final String TYPE_MQ = "mq";
    private final String TYPE_HMD = "hmd";
    private final String TYPE_CRG75 = "crg75";
    private final String TYPE_CRG25 = "crg25";
    private final String TYPE_CRG50 = "crg50";

    @Autowired
    GraphRepository graphRepository;

    @Autowired
    FirestoreTemplate firestoreTemplate;

    @GetMapping("/test")
    Mono<Double> test() {
        Double d = 0.0;
        try {
            for (String t : Arrays.asList(TYPE_CRG75, TYPE_CRG25, TYPE_CRG50)) {
                for (String g : Arrays.asList(
                    "_test2-jgit-ssh-6.8.0 100C.odem", "_test2-jmetal-auto-6.2.2 69C.odem", "_test2-junit-platform-launcher-1.10.1 116C.odem", "_test2-jgit-http-6.8.0 61C.odem"
                )) {
                    Graph graph = graphRepository.findByTypeAndNameAndPackageOnly(t, g, false).block();
                    List<Element> elements = this.firestoreTemplate
                        .withParent(graph)
                        .findAll(Element.class)
                        .filter(e -> e.getGroup().equals("edges"))
                        .collectList()
                        .block();
                    
                    System.out.println(g + " " + t + " " + elements.size());
                }
            }

            /*Graph graph = graphRepository.findByTypeAndNameAndPackageOnly(
                TYPE_HMD, "_jgit-ssh-6.8.0 100C.odem", false).block();
            List<Element> elements = this.firestoreTemplate
                .withParent(graph)
                .findAll(Element.class)
                .filter(e -> e.getGroup().equals("edges"))
                .collectList()
                .block();*/
            
            String filename = "/home/leo/github/hmd-gen-api/src/main/resources/junit_LogListFile.data";
            // d = getPercentualCommits(filename, elements);
            // d = getCommitRelation(filename, elements);
        }
        catch(Throwable ex) {
            return Mono.just(d);
        }

        return Mono.just(d);
    }

    @GetMapping("/generateMatrixFile")
    Mono<Double> generateMatrixFile() {
        Double d = 0.0;
        try {
            Map<String, String> odemLogListMap = new HashMap<>();
            //odemLogListMap.put("_aep-core-0.10.0 163C.odem", "aep-core_LogListFile.data");
            //odemLogListMap.put("_dubbo-cluster-3.2.8 195C.odem", "dubbo_LogListFile.data");
            //odemLogListMap.put("_elasticsearch-core-9.1.0 64C.odem", "elasticsearch_LogListFile.data");
            //odemLogListMap.put("_elasticsearch-geo-9.1.0 59C.odem", "elasticsearch_LogListFile.data");
            //odemLogListMap.put("_elasticsearch-native-9.1.0 67C.odem", "elasticsearch_LogListFile.data");
            //odemLogListMap.put("_javaGeom-0.11.3 159C.odem", "javaGeom_LogListFile.data");
            //odemLogListMap.put("_gson-2.10.1 217C.odem", "gson_LogListFile.data");
            //odemLogListMap.put("_javacc-7.0.14 190C.odem", "javacc_LogListFile.data");
            //odemLogListMap.put("_jmetal-component-6.2.2 132C.odem", "jMetal_LogListFile.data");
            //odemLogListMap.put("_jmetal-lab-6.2.2 54C.odem", "jMetal_LogListFile.data");
            //odemLogListMap.put("_jmetal-auto-6.2.2 69C.odem", "jMetal_LogListFile.data");
            //odemLogListMap.put("_joda-money-2.0.3 29C.odem", "jodamoney_LogListFile.data");
            //odemLogListMap.put("_joda-time-2.14.0 248C.odem", "jodatime_LogListFile.data");
            //odemLogListMap.put("_lucene-luke-11.0.0 278C.odem", "lucene_LogListFile.data");
            //odemLogListMap.put("_lucene-codecs-11.0.0 208C.odem", "lucene_LogListFile.data");
            //odemLogListMap.put("_lucene-analysis-nori-11.0.0 48C.odem", "lucene_LogListFile.data");
            //odemLogListMap.put("_lucene-highlighter-11.0.0 167C.odem", "lucene_LogListFile.data");
            //odemLogListMap.put("_lucene-analysis-kuromoji-11.0.0 69C.odem", "lucene_LogListFile.data");
            //odemLogListMap.put("_junit-platform-engine-1.10.1 136C.odem", "junit_LogListFile.data");
            //odemLogListMap.put("_junit-jupiter-engine-5.10.1 130C.odem", "junit_LogListFile.data");
            //odemLogListMap.put("_junit-platform-launcher-1.10.1 116C.odem", "junit_LogListFile.data");
            //odemLogListMap.put("_jgit-pgm-6.8.0 121C.odem", "jgit_LogListFile.data");
            //odemLogListMap.put("_jgit-ssh-6.8.0 100C.odem", "jgit_LogListFile.data");
            //odemLogListMap.put("_jgit-http-6.8.0 61C.odem", "jgit_LogListFile.data");
            //odemLogListMap.put("_jgit-lfs-6.8.0 41C.odem", "jgit_LogListFile.data");
            //odemLogListMap.put("_log4j-api-2.21.1 189C.odem", "log4j_LogListFile.data");
            //odemLogListMap.put("_log4j-layout-template-json-2.21.1 154C.odem", "log4j_LogListFile.data");
            //odemLogListMap.put("_log4j-jpa-2.21.1 26C.odem", "log4j_LogListFile.data");
            //odemLogListMap.put("_log4j-iostreams-2.21.1 29C.odem", "log4j_LogListFile.data");
            //odemLogListMap.put("_moquette-broker-0.19 204C.odem", "moquette_LogListFile.data");
            //odemLogListMap.put("_scribejava-apis-8.3.4 175C.odem", "scribejava_LogListFile.data");
            odemLogListMap.put("jodamoney_marlon", "jodamoney_marlon_LogListFile.data");

            // generate log list file for each project
            for (Map.Entry<String, String> entry : odemLogListMap.entrySet()) {
                String odemName = entry.getKey();
                String logListFileName = entry.getValue();
                
                try {
                    Graph graph = graphRepository.findByTypeAndNameAndPackageOnly(
                    TYPE_ORIGINAL, odemName, false).block();
                    List<Element> elements = this.firestoreTemplate
                        .withParent(graph)
                        .findAll(Element.class)
                        .filter(e -> e.getGroup().equals("nodes"))
                        .collectList()
                        .block();
                    String filename = "/home/leo/github/hmd-gen-api/src/main/resources/" + logListFileName;

                    getCommitRelation(odemName, filename, elements);
                    System.out.println("Generated matrix file for " + odemName);
                }
                catch (Exception ex) {
                    System.out.println("Error generating matrix file for " + odemName);
                    ex.printStackTrace();
                }
            }
        }
        catch(Throwable ex) {
            return Mono.just(d);
        }

        return Mono.just(d);
    }

    @GetMapping("/generateLogListFile")
    Mono<Double> generateLogListFile() {
        Double d = 0.0;
        try {
            // generate array list of projects
            List<String> projects = Arrays.asList(
                "aep-core", "dubbo", "elasticsearch", 
                "gson", "javacc", "jMetal", "jgit", "javaGeom", 
                "jodatime", "jodamoney", "junit", "log4j", 
                "lucene", "moquette", "scribejava"
            );
            
            // generate log list file for each project
            for (String project : projects) {
                generateLogListFile(project);
            }
        }
        catch(Throwable ex) {
            return Mono.just(d);
        }

        return Mono.just(d);
    }

    @GetMapping("/graphs")
    Flux<Graph> getAll() {
        return graphRepository.findByOrderByNameAscTypeAsc();
    }

    @GetMapping("/graphs/{id}")
    Mono<Graph> getById(@PathVariable String id) {
        return graphRepository.findById(id);
    }

    @GetMapping("/graphs/{id}/elements")
    Flux<Element> getElementsById(@PathVariable String id) {
        return this.firestoreTemplate.withParent(new Graph(id, null, null, null, null, null, null))
                .findAll(Element.class);
    }

    @PostMapping("/graphs/mojofm")
    Mono<Boolean> calculateMojofm(@RequestParam("forceUpdate") Boolean forceUpdate) {
        String prefix = "_a02-";

        List<Graph> graphs = graphRepository.findByPackageOnly(false)
            .filter(g -> g.getName().startsWith(prefix)).collectList().block();

        String basePath = "/home/leo/github/hmd-gen-api/lib/mojofm/";

        for (Graph g : graphs) {
            generateRsfFile(g, basePath, forceUpdate);
        }

        List<Graph> graphsHmd = graphRepository.findByTypeAndPackageOnly(TYPE_HMD, false)
            .filter(g -> g.getName().startsWith(prefix)).collectList().block();

        for (Graph h1 : graphsHmd) {
            Graph o = graphRepository.findByTypeAndNameAndPackageOnly(TYPE_ORIGINAL, h1.getName(), false).block();
            Graph m1 = graphRepository.findByTypeAndNameAndPackageOnly(TYPE_MQ, h1.getName(), false).block();
            Graph m2 = graphRepository.findByTypeAndNameAndPackageOnly(TYPE_MQ, h1.getName(), true).block();
            Graph h2 = graphRepository.findByTypeAndNameAndPackageOnly(TYPE_HMD, h1.getName(), true).block();

            if (o == null || m1 == null || m2 == null || h2 == null) {
                System.out.println("Skipping " + h1.getName() + " due to missing graphs.");
                continue;
            }

            Double mojoFM_HMD = calculateMojoFm(basePath, h1, o);
            Double mojoFM_MQ = calculateMojoFm(basePath, m1, o);

            Double pMojoFM_HMD = mojoFM_HMD != null ? mojoFM_HMD : null;
            Double pMojoFM_MQ = mojoFM_MQ != null ? mojoFM_MQ : null;

            GraphMetrics mh1 = h1.getMetrics();
            mh1.setMojofm(pMojoFM_HMD);
            h1.setMetrics(mh1);

            GraphMetrics mh2 = h2.getMetrics();
            mh2.setMojofm(pMojoFM_HMD);
            h2.setMetrics(mh2);

            GraphMetrics mm1 = m1.getMetrics();
            mm1.setMojofm(pMojoFM_MQ);
            m1.setMetrics(mm1);

            GraphMetrics mm2 = m2.getMetrics();
            mm2.setMojofm(pMojoFM_MQ);
            m2.setMetrics(mm2);

            graphRepository.save(h1).block();
            graphRepository.save(h2).block();
            graphRepository.save(m1).block();
            graphRepository.save(m2).block();

            System.out.println(h1.getName() + ": " + pMojoFM_HMD + ", " + pMojoFM_MQ);
        }

        return Mono.empty();
    }

    @PostMapping("/graphs/crg/mojofm")
    Mono<Boolean> calculateCrgMojofm(@RequestParam("forceUpdate") Boolean forceUpdate) {
        String basePath = "/home/leo/github/hmd-gen-api/lib/mojofm/";
        String prefix = "_a03-";

        List<Graph> graphs0 = graphRepository.findByTypeAndPackageOnly(TYPE_ORIGINAL, false).filter(g -> g.getName().startsWith(prefix)).collectList().block();

        for (Graph g : graphs0) {
            generateRsfFile(g, basePath, forceUpdate);
        }

        List<Graph> graphs = graphRepository.findByTypeAndPackageOnly(TYPE_CRG75, false).filter(g -> g.getName().startsWith(prefix)).collectList().block();

        for (Graph g : graphs) {
            generateRsfFile(g, basePath, forceUpdate);
        }

        List<Graph> graphs2 = graphRepository.findByTypeAndPackageOnly(TYPE_CRG25, false).filter(g -> g.getName().startsWith(prefix)).collectList().block();

        for (Graph g : graphs2) {
            generateRsfFile(g, basePath, forceUpdate);
        }

        List<Graph> graphs3 = graphRepository.findByTypeAndPackageOnly(TYPE_CRG50, false).filter(g -> g.getName().startsWith(prefix)).collectList().block();

        for (Graph g : graphs3) {
            generateRsfFile(g, basePath, forceUpdate);
        }

        List<Graph> graphsCrg = graphRepository.findByTypeAndPackageOnly(TYPE_CRG75, false).filter(g -> g.getName().startsWith(prefix)).collectList().block();

        for (Graph crg75_1 : graphsCrg) {
            Graph crg75_2 = graphRepository.findByTypeAndNameAndPackageOnly(TYPE_CRG75, crg75_1.getName(), true).block();
            Graph o = graphRepository.findByTypeAndNameAndPackageOnly(TYPE_ORIGINAL, crg75_1.getName(), false).block();
            Graph crg25_1 = graphRepository.findByTypeAndNameAndPackageOnly(TYPE_CRG25, crg75_1.getName(), false).block();
            Graph crg25_2 = graphRepository.findByTypeAndNameAndPackageOnly(TYPE_CRG25, crg75_1.getName(), true).block();
            Graph crg50_1 = graphRepository.findByTypeAndNameAndPackageOnly(TYPE_CRG50, crg75_1.getName(), false).block();
            Graph crg50_2 = graphRepository.findByTypeAndNameAndPackageOnly(TYPE_CRG50, crg75_1.getName(), true).block();

            if (o == null || crg75_2 == null || crg25_1 == null || crg25_2 == null || crg50_1 == null || crg50_2 == null) {
                System.out.println("Skipping " + crg75_1.getName() + " due to missing graphs.");
                continue;
            }

            Double mojoFM_CRG75 = calculateMojoFm(basePath, crg75_1, o);
            Double mojoFM_CRG25 = calculateMojoFm(basePath, crg25_1, o);
            Double mojoFM_CRG50 = calculateMojoFm(basePath, crg50_1, o);

            GraphMetrics mc00_1 = crg75_1.getMetrics();
            mc00_1.setMojofm(mojoFM_CRG75);
            crg75_1.setMetrics(mc00_1);

            GraphMetrics mc00_2 = crg75_2.getMetrics();
            mc00_2.setMojofm(mojoFM_CRG75);
            crg75_2.setMetrics(mc00_2);

            GraphMetrics mc25_1 = crg25_1.getMetrics();
            mc25_1.setMojofm(mojoFM_CRG25);
            crg25_1.setMetrics(mc25_1);

            GraphMetrics mc25_2 = crg25_2.getMetrics();
            mc25_2.setMojofm(mojoFM_CRG25);
            crg25_2.setMetrics(mc25_2);

            GraphMetrics mc50_1 = crg50_1.getMetrics();
            mc50_1.setMojofm(mojoFM_CRG50);
            crg50_1.setMetrics(mc50_1);

            GraphMetrics mc50_2 = crg50_2.getMetrics();
            mc50_2.setMojofm(mojoFM_CRG50);
            crg50_2.setMetrics(mc50_2);

            graphRepository.save(crg75_1).block();
            graphRepository.save(crg75_2).block();
            graphRepository.save(crg25_1).block();
            graphRepository.save(crg25_2).block();
            graphRepository.save(crg50_1).block();
            graphRepository.save(crg50_2).block();

            System.out.println(mojoFM_CRG75 + ", " + mojoFM_CRG25 + ", " + mojoFM_CRG50);
        }

        return Mono.empty();
    }

    @PostMapping("/graphs/maxdepth")
    Mono<Graph> calculateMaxDepth(@RequestParam("forceUpdate") Boolean forceUpdate) {
        String prefix = "_";
        List<Graph> graphs = graphRepository.findByPackageOnly(false).filter(g -> g.getName().startsWith(prefix)).collectList().block();

        for (Graph g : graphs) {
            if (g.getMetrics().getMaxDepth() != null && !forceUpdate)
                continue;

            List<Element> elements = this.firestoreTemplate.withParent(g)
                    .findAll(Element.class).filter(e -> e.getGroup().equals("nodes")).collectList().block();

            Integer maxDepth = 0;
            for (Element e : elements) {
                maxDepth = Integer.max(maxDepth, calculateDepth(e, elements));
            }

            if (maxDepth > 0) maxDepth -= 1;

            System.out.println(g.getName() + " " + g.getType() + " " + g.getPackageOnly());

            g.getMetrics().setMaxDepth(maxDepth);

            graphRepository.save(g).block();
        }

        return Mono.empty();
    }

    @PostMapping("/graphs/avgclasses")
    Mono<Graph> averageClasses(@RequestParam("forceUpdate") Boolean forceUpdate) {
        String prefix = "_";
        List<Graph> graphs = graphRepository.findByPackageOnly(false).filter(g -> g.getName().startsWith(prefix)).collectList().block();

        for (Graph g : graphs) {
            if (g.getMetrics().getAverageOfClassesPerPackage() != null && !forceUpdate)
                continue;
            
            List<Element> elements = this.firestoreTemplate.withParent(g)
                .findAll(Element.class).filter(e -> e.getGroup().equals("nodes")).collectList().block();

            Map<String, Long> t = elements.stream().filter(e -> e.getData().getParent() != null)
                    .collect(Collectors.groupingBy(e -> e.getData().getParent(), Collectors.counting()));

            Double avgClasses = t.values().stream().collect(Collectors.averagingLong(v -> v));

            g.getMetrics().setAverageOfClassesPerPackage(avgClasses);

            graphRepository.save(g).block();

            System.out.println(g.getName() + " " + g.getType() + " " + g.getPackageOnly());
        }

        return Mono.empty();
    }

    @PostMapping("/graphs/percentualcommits")
    Mono<Graph> singlePackageCommitCalculation(@RequestParam("forceUpdate") Boolean forceUpdate) {
        String prefix = "_a03-";
        List<Graph> graphs = graphRepository.findByPackageOnly(false).filter(g -> g.getName().startsWith(prefix)).collectList().block();

        for (Graph g : graphs) {
            if (g.getMetrics().getPercentualCommits() != null && !forceUpdate)
                continue;
            
            List<Element> elements = this.firestoreTemplate.withParent(g)
                    .findAll(Element.class).filter(e -> e.getGroup().equals("nodes")).collectList().block();

            double percentualCommits = 0.0;
            
            try {
                String keyword = getKeyword(g.getName());
                String filename = "/home/leo/github/hmd-gen-api/src/main/resources/" + keyword + "_LogListFile.data";
                percentualCommits = getPercentualCommits(filename, elements);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            
            g.getMetrics().setPercentualCommits(percentualCommits);

            graphRepository.save(g).block();

            System.out.println(g.getName() + " " + g.getType() + " " + g.getPackageOnly());
        }

        return Mono.empty();
    }

    private String getKeyword(String name) {
        if (name.toLowerCase().contains("aep")) return "aep-core";
        if (name.toLowerCase().contains("jmetal")) return "jMetal";
        if (name.toLowerCase().contains("jgit")) return "jgit";
        if (name.toLowerCase().contains("javageom")) return "javaGeom";
        if (name.toLowerCase().contains("junit")) return "junit";
        if (name.toLowerCase().contains("scribejava")) return "scribejava";
        if (name.toLowerCase().contains("jodamoney")) return "jodamoney";
        if (name.toLowerCase().contains("moquette")) return "moquette";
        if (name.toLowerCase().contains("jodatime")) return "jodatime";
        if (name.toLowerCase().contains("lucene")) return "lucene";
        if (name.toLowerCase().contains("javacc")) return "javacc";
        if (name.toLowerCase().contains("log4j")) return "log4j";
        if (name.toLowerCase().contains("dubbo")) return "dubbo";
        if (name.toLowerCase().contains("gson")) return "gson";

        return "";
    }

    private double getPercentualCommits(String filename, List<Element> elements) throws IOException {        
		System.out.println(new File(".").getCanonicalPath());
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;

        HashMap<String, Integer> packagesPerCommit = new HashMap<>();

		String lastRevision = "";
		List<String> lastPackages = new ArrayList<String>();
		br.readLine(); // jumping the title of the file.
		while ((line = br.readLine()) != null) {
			if (line.length() > 0) {
				String tokens[] = line.split(" ");

				String revision = tokens[1];
				String clazz = tokens[3];

                // remove extension
				clazz = clazz.substring(0, clazz.lastIndexOf('.'));
                
                // get class name
                final String clazz2 = clazz.substring(nthLastIndexOf(1, "/", clazz) + 1);
                
                // get parent package
                final String lastParent = clazz.substring(nthLastIndexOf(2, "/", clazz) + 1, nthLastIndexOf(1, "/", clazz));    
                
                List<Element> el = elements.stream().filter(
                    e -> e.getData().getId().endsWith("." + clazz2) && 
                         !e.getData().getId().startsWith("M")
                ).toList();

                if (el.size() > 1) {
                    if (lastParent == null || lastParent.isEmpty() 
                        || el.stream().filter(e -> e.getData().getId().contains(lastParent)).count() > 1)
                    {
                        continue;
                        // throw new IOException("more than one element with the same name");
                    }
                    else 
                    {
                        el = el.stream().filter(e -> e.getData().getId().contains(lastParent)).toList();
                    }
                }

                if (el.size() == 0) 
                    continue;

                String packageName = el.get(0).getData().getParent();
                
				if (revision.compareToIgnoreCase(lastRevision) != 0) {
                    if (lastPackages.size() > 0)
                        packagesPerCommit.put(lastRevision, lastPackages.size());

					lastRevision = revision;
					lastPackages.clear();
				}

				if (!hasPackage(lastPackages, packageName))
					lastPackages.add(packageName);
			}
		}

		br.close();

        if (packagesPerCommit.size() == 0) 
            return 0.0;

        // return packagesPerCommit.values().stream().collect(Collectors.averagingInt(v -> v));

        double total = packagesPerCommit.size();
        double onlyOnePackage = packagesPerCommit.values().stream().filter(v -> v == 1).count();

        return onlyOnePackage / total;
	}

    // Helper class to store commit data
    private static class CommitData {
        String date;
        String revision;
        String clazz;
        String oldName;
        
        CommitData(String date, String revision, String clazz, String oldName) {
            this.date = date;
            this.revision = revision;
            this.clazz = clazz;
            this.oldName = oldName;
        }
    }

    private void commitRelationInputValidation(String odem, String filename, List<Element> elements) throws IOException {
        if (odem == null || odem.trim().isEmpty()) {
            throw new IllegalArgumentException("Odem parameter cannot be null or empty");
        }
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename parameter cannot be null or empty");
        }
        if (elements == null) {
            throw new IllegalArgumentException("Elements list cannot be null");
        }
        
        File inputFile = new File(filename);
        if (!inputFile.exists()) {
            throw new IOException("LogList file does not exist: " + filename);
        }
        if (!inputFile.canRead()) {
            throw new IOException("Cannot read LogList file: " + filename);
        }
    }

    private List<CommitData> collectCommitData(String filename) throws IOException {
        System.out.println("Collecting commit data from " + filename);
        
        // Read the file and collect commit data
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;

        // First pass: collect all commit data
        List<CommitData> allCommits = new ArrayList<>();
        
        br.readLine(); // skip header

        while ((line = br.readLine()) != null) {
            if (line.length() > 0) {
                String tokens[] = line.split(" ");
                if (tokens.length >= 4) {
                    allCommits.add(new CommitData(
                        tokens[0], // date
                        tokens[1], // revision
                        tokens[3], // class
                        tokens.length > 4 ? tokens[4] : null // old name if rename
                    ));
                }
            }
        }

        br.close();
        
        // Sort commits by date (ascending order)
        allCommits.sort((a, b) -> a.date.compareTo(b.date));

        return allCommits;
    }

    private int processRenamesInsideCommits(HashMap<String, Set<String>> synonyms, List<CommitData> allCommits, List<String> possiblePackageNames) {
        int renamedClasses = 0;

        for (CommitData commitData : allCommits) {
            String clazz = commitData.clazz;
            String clazzOldName = commitData.oldName;

            if (clazzOldName != null && !clazzOldName.isEmpty()) {
                // Process class names - check if extension exists
                int lastDotIndex = clazz.lastIndexOf('.');
                if (lastDotIndex <= 0) {
                    System.out.println("Skipping rename - no extension found in class: " + clazz);
                    continue;
                }
                clazz = clazz.substring(0, lastDotIndex);
                
                int lastIndexJava = 0;
                for (String p : possiblePackageNames) {
                    lastIndexJava = clazz.lastIndexOf("/" + p + "/");
                    if (lastIndexJava >= 1) {
                        lastIndexJava += p.length() + 2;
                        break;
                    }
                }
                
                // If no package root found, skip this rename
                if ((lastIndexJava = checkRootFolder(clazz, lastIndexJava)) == -1) {
                    continue;
                }

                final String clazzFullName = clazz.substring(lastIndexJava).replace('/', '.');
                
                // Process old name
                int lastIndexDot = clazzOldName.lastIndexOf('.');
                if (lastIndexDot >= 0) {
                    clazzOldName = clazzOldName.substring(0, clazzOldName.lastIndexOf('.'));
                }
                
                // Find the appropriate package root in the old name
                int lastIndexJavaOld = 0;
                for (String p : possiblePackageNames) {
                    lastIndexJavaOld = clazzOldName.lastIndexOf("/" + p + "/");
                    if (lastIndexJavaOld >= 0) {
                        lastIndexJavaOld += p.length() + 2; // +2 for the slashes
                        break;
                    }
                }
                
                // If no package root found, skip this rename
                if ((lastIndexJavaOld = checkRootFolder(clazzOldName, lastIndexJavaOld)) == -1) {
                    continue;
                }

                // Extract the old class name
                clazzOldName = clazzOldName.substring(lastIndexJavaOld).replace('/', '.');
                
                if (!clazzOldName.equals(clazzFullName)) {
                    addSynonym(synonyms, clazzFullName, clazzOldName);
                    addSynonym(synonyms, clazzOldName, clazzFullName);
                    renamedClasses++;
                    System.out.println("Rename detected: " + clazzOldName + " -> " + clazzFullName);
                }
            }
        }

        return renamedClasses;
    }

    private int checkRootFolder(String clazzName, int lastIndexJava) {
        List<String> possiblePackageNames = Arrays.asList(
            "jmetal/", "sandbox/", "solr/", "gradle/", "dev-tools/", "x-pack/", "build-tools-internal/", "examples/");

        if (lastIndexJava <= 0 || lastIndexJava >= clazzName.length()) {
            if (possiblePackageNames.stream().anyMatch(clazzName::startsWith)) {
                // Special cases - allow renames without package root
                return 0;
            } else if (clazzName.startsWith("src/")){
                lastIndexJava = 4; // src/ is a common root;
            } else {
                // For other cases, skip the rename
                System.out.println("Skipping rename - no package root found in name: " + clazzName);
                // return -1; // Indicate no valid package root found
                return 0;
            }
        }

        return lastIndexJava;
    }

    double getCommitRelation(String odem, String filename, List<Element> elements) throws IOException {
        // Validate and read commit data
        commitRelationInputValidation(odem, filename, elements);
        List<CommitData> allCommits = collectCommitData(filename);
        System.out.println("Processing " + allCommits.size() + " commits for " + odem);
        
        // Define possible package names for consistent use across phases
        List<String> possiblePackageNames = Arrays.asList(
            "java", "java-templates", "src", "tst", "test", "exttst", "test-deprecated", 
            "common", "example", "demo", "javaFiles", "solrj", "gcj", "log4j-api-java9", "lucene_extras");

        // Prepare data structures for processing
        HashMap<String, Set<String>> commitsPerModule = new HashMap<>();
        HashMap<String, Set<String>> synonyms = new HashMap<>();
        Set<String> commits = new HashSet<>();
        
        // PHASE 1: Build complete synonym map by processing all renames first
        System.out.println("Phase 1: Building complete synonym relationships...");
        int renamedClasses = processRenamesInsideCommits(synonyms, allCommits, possiblePackageNames);
        System.out.println("Phase 1 complete. Found " + renamedClasses + " renamed classes, " + synonyms.size() + " synonym groups");

        // PHASE 2: Process all commits with complete synonym information
        System.out.println("Phase 2: Processing commits with complete synonym information...");
        int processedCommits = 0;
        for (CommitData commitData : allCommits) {
            processedCommits++;
            String revision = commitData.revision;
            String clazz = commitData.clazz;

            // remove extension - check if extension exists
            int lastDotIndex = clazz.lastIndexOf('.');
            if (lastDotIndex <= 0) {
                System.out.println("Skipping commit - no extension found in class: " + clazz);
                continue;
            }
            clazz = clazz.substring(0, lastDotIndex);

            int lastIndexJava = 0;
            for (String p : possiblePackageNames) {
                lastIndexJava = clazz.lastIndexOf("/" + p + "/");
                if (lastIndexJava >= 1) {
                    lastIndexJava += p.length() + 2; // +2 for the slashes
                    break;
                }
            }

            // If no package root found, skip this commit
            if ((lastIndexJava = checkRootFolder(clazz, lastIndexJava)) == -1) {
                continue;
            }

            final String clazzFullName = clazz.substring(lastIndexJava).replace('/', '.');

            // Get all possible names for this class (using complete synonym information)
            Set<String> allPossibleNames = getAllSynonyms(synonyms, clazzFullName);

            // Find matching elements in the current codebase
            List<Element> matchingElements = elements.stream().filter(
                e -> allPossibleNames.stream().anyMatch(name -> 
                    e.getData().getId().equals(name) || 
                    e.getData().getId().endsWith("." + getSimpleClassName(name))
                ) && !e.getData().getId().startsWith("M")
            ).collect(Collectors.toList());

            // Map commits to the current class names
            for (Element element : matchingElements) {
                String currentClassName = element.getData().getId();
                commitsPerModule.putIfAbsent(currentClassName, new HashSet<>());
                commitsPerModule.get(currentClassName).add(revision);
                commits.add(revision);
            }
        }

        // Print processing statistics
        System.out.println("Statistics for " + odem + ":");
        System.out.println("  - Total commits processed: " + processedCommits);
        System.out.println("  - Classes with renames: " + renamedClasses);
        System.out.println("  - Total synonym groups: " + synonyms.size());
        System.out.println("  - Classes mapped to commits: " + commitsPerModule.size());
        System.out.println("  - Total unique commits: " + commits.size());
        System.out.println("  - Total elements in current codebase: " + elements.size());

        // Check for unmapped classes in current codebase
        List<String> unmappedClasses = elements.stream()
            .filter(e -> !e.getData().getId().startsWith("M") && !e.getData().getId().contains("$")) // Exclude modules and inner classes
            .map(e -> e.getData().getId())
            .filter(className -> !commitsPerModule.containsKey(className))
            .collect(Collectors.toList());
            
        if (!unmappedClasses.isEmpty()) {
            System.out.println("Warning: " + unmappedClasses.size() + " classes in current codebase have no commit history:");
            unmappedClasses.stream().limit(10).forEach(className -> 
                System.out.println("  - " + className));
            if (unmappedClasses.size() > 10) {
                System.out.println("  ... and " + (unmappedClasses.size() - 10) + " more");
            }
        }

        // Check for classes mentioned in commits but not found in current codebase
        Set<String> historicalClasses = new HashSet<>();
        for (CommitData commitData : allCommits) {
            // Check if extension exists before removing it
            int lastDotIndex = commitData.clazz.lastIndexOf('.');
            if (lastDotIndex <= 0) {
                continue; // Skip if no extension
            }
            String clazz = commitData.clazz.substring(0, lastDotIndex);
            int lastIndexJava = 0;
            for (String p : possiblePackageNames) {
                lastIndexJava = clazz.lastIndexOf("/" + p + "/");
                if (lastIndexJava >= 1) {
                    lastIndexJava += p.length() + 2;
                    break;
                }
            }
            
            // Only add to historical classes if we found a valid package root
            if (lastIndexJava > 0 && lastIndexJava < clazz.length()) {
                historicalClasses.add(clazz.substring(lastIndexJava).replace('/', '.'));
            }
        }
        
        Set<String> currentClasses = elements.stream()
            .filter(e -> !e.getData().getId().startsWith("M"))
            .map(e -> e.getData().getId())
            .collect(Collectors.toSet());
            
        Set<String> deletedClasses = new HashSet<>(historicalClasses);
        deletedClasses.removeAll(currentClasses);
        
        if (!deletedClasses.isEmpty()) {
            System.out.println("Info: " + deletedClasses.size() + " classes mentioned in commits but not in current codebase (likely deleted):");
            deletedClasses.stream().limit(10).forEach(className -> 
                System.out.println("  - " + className));
            if (deletedClasses.size() > 10) {
                System.out.println("  ... and " + (deletedClasses.size() - 10) + " more");
            }
        }

        List<String> innerClasses = elements.stream()
                                            .filter(e -> e.getData().getId().contains("$") && !e.getData().getId().startsWith("M"))
                                            .map(e -> e.getData().getId()).collect(Collectors.toList());

        for (String c : innerClasses) {
            if (commitsPerModule.containsKey(c)) continue;

            String baseClassName = findBaseClass(commitsPerModule, c);

            if (baseClassName == null || baseClassName.isEmpty() || !commitsPerModule.containsKey(baseClassName)) {
                // Still add the inner class to the matrix even if base class not found
                commitsPerModule.putIfAbsent(c, new HashSet<>());
                System.out.println("Added inner class with no base class commits: " + c);
                continue;
            }

            commitsPerModule.putIfAbsent(c, new HashSet<String>());
            for(String commit : commitsPerModule.get(baseClassName)) {
                commitsPerModule.get(c).add(commit);
            }
            System.out.println("Inherited commits for inner class " + c + " from base class " + baseClassName);
        }

        // Ensure all current classes are included in the matrix (even if they have no commits)
        List<String> allCurrentClasses = elements.stream()
            .filter(e -> !e.getData().getId().startsWith("M"))
            .map(e -> e.getData().getId())
            .collect(Collectors.toList());
            
        int addedClassesWithoutCommits = 0;
        for (String className : allCurrentClasses) {
            if (!commitsPerModule.containsKey(className)) {
                commitsPerModule.put(className, new HashSet<>());
                addedClassesWithoutCommits++;
                System.out.println("Added class with no commits to matrix: " + className);
            }
        }
        
        System.out.println("Final validation before matrix generation:");
        System.out.println("  - Total elements in codebase: " + allCurrentClasses.size());
        System.out.println("  - Classes in commitsPerModule: " + commitsPerModule.size());
        System.out.println("  - Added classes without commits: " + addedClassesWithoutCommits);
        
        if (allCurrentClasses.size() != commitsPerModule.size()) {
            System.err.println("ERROR: Matrix size mismatch! Expected " + allCurrentClasses.size() + " but got " + commitsPerModule.size());
            
            // Find missing classes
            Set<String> missingClasses = new HashSet<>(allCurrentClasses);
            missingClasses.removeAll(commitsPerModule.keySet());
            if (!missingClasses.isEmpty()) {
                System.err.println("Missing classes from matrix:");
                missingClasses.forEach(cls -> System.err.println("  - " + cls));
            }
        }

        Double[][] matrix = new Double[commitsPerModule.size()][commitsPerModule.size()];

        List<String> keys = new ArrayList<>(commitsPerModule.keySet());
        keys.sort(String::compareTo);

        for (int i = 0; i < commitsPerModule.size(); i++) {
            for (int j = 0; j < commitsPerModule.size(); j++) {
                String module1 = keys.get(i);
                String module2 = keys.get(j);

                Set<String> commits1 = commitsPerModule.get(module1);
                Set<String> commits2 = commitsPerModule.get(module2);


                Set<String> commitsUnion = new HashSet<>();
                commitsUnion.addAll(commits1);
                commitsUnion.addAll(commits2);

                int count = 0;
                for (String commit : commits1) {
                    if (commits2.contains(commit))
                        count++;
                }

                Double percentual = (double) count / commitsUnion.size();
                
                // Validate matrix values
                if (percentual < 0.0 || percentual > 1.0) {
                    System.err.println("Warning: Invalid matrix value " + percentual + " at [" + i + "," + j + "]");
                }

                matrix[i][j] = percentual;
            }
        }
        
        // Validate matrix properties
        int classesWithCommits = 0;
        int classesWithoutCommits = 0;
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i][i] > 0.0) {
                classesWithCommits++;
            } else {
                classesWithoutCommits++;
                System.out.println("Class with no commits: " + keys.get(i));
            }
        }
        
        System.out.println("Matrix validation:");
        System.out.println("  - Matrix size: " + matrix.length + "x" + matrix.length);
        System.out.println("  - Classes with commits: " + classesWithCommits);
        System.out.println("  - Classes without commits: " + classesWithoutCommits);

        // Use the translation method to get the correct output file name
        String outputFileName = translateOdemToOutputFileName(odem);
        File file = new File("/home/leo/github/hmd-gen-api/src/main/resources/matrix/" + outputFileName);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < commitsPerModule.size(); i++) {
                String module1 = keys.get(i);
                bw.write(module1 + " ");
                
                for (int j = 0; j < commitsPerModule.size(); j++) {
                    bw.write(matrix[i][j] + " ");
                }

                bw.newLine();
            }
            
            System.out.println("Matrix file written successfully: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error writing matrix file: " + e.getMessage());
            throw e;
        }

        return 0;
	}

    // Helper method to add synonym relationships
    private void addSynonym(HashMap<String, Set<String>> synonyms, String name1, String name2) {
        synonyms.putIfAbsent(name1, new HashSet<>());
        synonyms.get(name1).add(name1); // Include self
        synonyms.get(name1).add(name2);
        synonyms.putIfAbsent(name2, new HashSet<>());
        synonyms.get(name2).add(name2); // Include self
        synonyms.get(name2).add(name1);
    }

    // Helper method to get all synonyms for a class name
    private Set<String> getAllSynonyms(HashMap<String, Set<String>> synonyms, String className) {
        Set<String> result = new HashSet<>();
        result.add(className);
        
        Set<String> directSynonyms = synonyms.get(className);
        if (directSynonyms != null) {
            result.addAll(directSynonyms);
            
            // Also get synonyms of synonyms (transitive closure)
            for (String synonym : directSynonyms) {
                Set<String> indirectSynonyms = synonyms.get(synonym);
                if (indirectSynonyms != null) {
                    result.addAll(indirectSynonyms);
                }
            }
        }
        
        return result;
    }

    // Helper method to extract simple class name
    private String getSimpleClassName(String fullClassName) {
        if (fullClassName.contains(".")) {
            return fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        }
        return fullClassName;
    }

    // Helper method to translate ODEM file names to output file names
    private String translateOdemToOutputFileName(String odemName) {
        // Remove the leading underscore and the " 123C.odem" suffix
        String baseName = odemName;
        
        if (baseName.startsWith("_")) {
            baseName = baseName.substring(1);
        }
        
        // Remove the pattern like " 175C.odem", " 163C.odem", etc.
        if (baseName.contains(" ")) {
            baseName = baseName.substring(0, baseName.indexOf(" "));
        }
        
        // Handle special cases and naming conventions
        switch (baseName) {
            case "aep-core-0.10.0":
                return "aep-core-0.10.0.txt";
            case "dubbo-cluster-3.2.8":
                return "dubbo-cluster-3.2.8.txt";
            case "elasticsearch-core-9.1.0":
                return "elasticsearch-core-9.1.0.txt";
            case "elasticsearch-geo-9.1.0":
                return "elasticsearch-geo-9.1.0.txt";
            case "elasticsearch-native-9.1.0":
                return "elasticsearch-native-9.1.0.txt";
            case "gson-2.10.1":
                return "gson-2.10.1.txt";
            case "javacc-7.0.14":
                return "javacc-7.0.14.txt";
            case "javaGeom-0.11.3":
                return "javaGeom-0.11.3.txt";
            case "jgit-http-6.8.0":
                return "jgit-http-6.8.0.txt";
            case "jgit-lfs-6.8.0":
                return "jgit-lfs-6.8.0.txt";
            case "jgit-pgm-6.8.0":
                return "jgit-pgm-6.8.0.txt";
            case "jgit-ssh-6.8.0":
                return "jgit-ssh-6.8.0.txt";
            case "jmetal-component-6.2.2":
                return "jMetal-component-6.2.2.txt";
            case "jmetal-lab-6.2.2":
                return "jMetal-lab-6.2.2.txt";
            case "jmetal-auto-6.2.2":
                return "jMetal-auto-6.2.2.txt";
            case "joda-money-2.0.3":
                return "joda-money-2.0.3.txt";
            case "joda-time-2.14.0":
                return "joda-time-2.14.0.txt";
            case "junit-jupiter-engine-5.10.1":
                return "junit-jupiter-engine-5.10.1.txt";
            case "junit-platform-engine-1.10.1":
                return "junit-platform-engine.txt";
            case "junit-platform-launcher-1.10.1":
                return "junit-platform-launcher-1.10.1.txt";
            case "log4j-api-2.21.1":
                return "log4j-api-2.21.1.txt";
            case "log4j-iostreams-2.21.1":
                return "log4j-iostreams-2.21.1.txt";
            case "log4j-jpa-2.21.1":
                return "log4j-jpa-2.21.1.txt";
            case "log4j-layout-template-json-2.21.1":
                return "log4j-layout-template-json-2.21.1.txt";
            case "lucene-codecs-11.0.0":
                return "lucene-codecs-11.0.0.txt";
            case "lucene-highlighter-11.0.0":
                return "lucene-highlighter-11.0.0.txt";
            case "lucene-analysis-kuromoji-11.0.0":
                return "lucene-kuromoji-11.0.0.txt";
            case "lucene-luke-11.0.0":
                return "lucene-luke-11.0.0.txt";
            case "lucene-analysis-nori-11.0.0":
                return "lucene-nori-11.0.0.txt";
            case "moquette-broker-0.19":
                return "moquette-0.19.txt";
            case "scribejava-apis-8.3.4":
                return "scribejava-8.3.4.txt";
            default:
                return baseName + ".txt";
        }
    }

    private static String findBaseClass(HashMap<String, Set<String>> commitsPerModule, String childClass) {
        if (childClass == null || childClass.isEmpty()) return "";

        if (commitsPerModule.containsKey(childClass)) return childClass;

        String[] parts = childClass.split("\\$");
        
        // Handle nested inner classes recursively
        if (parts.length > 2) {
            return findBaseClass(commitsPerModule, childClass.substring(0, childClass.lastIndexOf('$')));
        }
        else if (parts.length == 2) {
            // Check if base class exists
            String baseClass = parts[0];
            if (commitsPerModule.containsKey(baseClass)) {
                return baseClass;
            }
            
            // Handle special cases like anonymous classes ($1, $2, etc.) or inner classes ($InstanceHolder)
            // These should inherit from their outer class
            return baseClass;
        }
        else {
            return childClass; // No $ in name, it's already a base class
        }
    }

    private static boolean hasPackage(List<String> packages, String packageName) {
		if (packageName == null || packageName.isEmpty() || packages == null || packages.isEmpty())
            return false;
        
        for (String name : packages) {
            if (name == null || name.isEmpty())
                continue;

			if (name.compareToIgnoreCase(packageName) == 0)
				return true;
        }

		return false;
	}

    private int nthLastIndexOf(int nth, String ch, String string) {
        if (nth <= 0) return string.length();
        return nthLastIndexOf(--nth, ch, string.substring(0, string.lastIndexOf(ch)));
    }

    @PatchMapping("/graphs/{id}")
    Mono<Graph> updateGraphById(@PathVariable String id, @RequestBody GraphRequest request) {
        Graph graph = graphRepository.findById(id).block();

        if (request.getMaxDepth() != null)
            graph.getMetrics().setMaxDepth(request.getMaxDepth());

        return graphRepository.save(graph);
    }

    @PatchMapping("/graphs")
    Mono<Graph> updateGraph(@RequestBody GraphRequest request) {
        Graph graph = graphRepository
                .findByTypeAndNameAndPackageOnly(request.getType(), request.getName(), request.getPackageOnly())
                .block();

        if (request.getMaxDepth() != null)
            graph.getMetrics().setMaxDepth(request.getMaxDepth());

        return graphRepository.save(graph);
    }

    // ORIGINAL

    @PostMapping("/original/graphs")
    Mono<Graph> createOriginal(@RequestBody GraphRequest request, @RequestParam("forceUpdate") Boolean forceUpdate) {
        return create(request, forceUpdate, TYPE_ORIGINAL);
    }

    @GetMapping("/original/graphs")
    Flux<Graph> getAllOriginal() {
        return graphRepository.findByType(TYPE_ORIGINAL);
    }

    // MQ

    @PostMapping("/mq/graphs")
    Mono<Graph> createMq(@RequestBody GraphRequest request, @RequestParam("forceUpdate") Boolean forceUpdate) {
        return create(request, forceUpdate, TYPE_MQ);
    }

    @GetMapping("/mq/graphs")
    Flux<Graph> getAllMq() {
        return graphRepository.findByType(TYPE_MQ);
    }

    // HMD

    @PostMapping("/hmd/graphs")
    Mono<Graph> createHmd(@RequestBody GraphRequest request, @RequestParam("forceUpdate") Boolean forceUpdate) {
        return create(request, forceUpdate, TYPE_HMD);
    }

    @GetMapping("/hmd/graphs")
    Flux<Graph> getAllHmd() {
        return graphRepository.findByType(TYPE_HMD);
    }

    // CRG

    @PostMapping("/crg75/graphs")
    Mono<Graph> createCrg75(@RequestBody GraphRequest request, @RequestParam("forceUpdate") Boolean forceUpdate) {
        return create(request, forceUpdate, TYPE_CRG75);
    }

    @GetMapping("/crg75/graphs")
    Flux<Graph> getAllCrg75() {
        return graphRepository.findByType(TYPE_CRG75);
    }

    @PostMapping("/crg25/graphs")
    Mono<Graph> createCrg25(@RequestBody GraphRequest request, @RequestParam("forceUpdate") Boolean forceUpdate) {
        return create(request, forceUpdate, TYPE_CRG25);
    }

    @GetMapping("/crg25/graphs")
    Flux<Graph> getAllCrg25() {
        return graphRepository.findByType(TYPE_CRG25);
    }

    @PostMapping("/crg50/graphs")
    Mono<Graph> createCrg50(@RequestBody GraphRequest request, @RequestParam("forceUpdate") Boolean forceUpdate) {
        return create(request, forceUpdate, TYPE_CRG50);
    }

    @GetMapping("/crg50/graphs")
    Flux<Graph> getAllCrg50() {
        return graphRepository.findByType(TYPE_CRG50);
    }

    // Auxiliary methods

    /**
     * Creates 
     * @param request
     * @param forceUpdate
     * @param type
     * @return
     */
    private Mono<Graph> create(GraphRequest request, Boolean forceUpdate, String type) {
        try {
            request.setType(type);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            request.setTimestamp(dtf.format(now));

            Mono<Graph> existingMonoGraph = graphRepository.findByTypeAndNameAndPackageOnly(type, request.getName(),
                    request.getPackageOnly());
            Graph existingGraph = existingMonoGraph.block();

            if (existingGraph != null) {
                if (forceUpdate) {
                    firestoreTemplate.withParent(existingGraph)
                            .deleteAll(Element.class).block();
                    graphRepository.delete(existingGraph).block();
                } else {
                    return existingMonoGraph;
                }
            }

            Graph graph = new Graph(request);

            FirestoreReactiveOperations graphTemplate = firestoreTemplate.withParent(graph);

            return graphTemplate.saveAll(Flux.fromIterable(request.getElements())).then(graphRepository.save(graph));

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return null;
    }

    /**
     * Generates RSF file from graph.
     * 
     * @param g graph
     * @param basePath base path
     * @param forceUpdate whether it should force the update
     */
    private void generateRsfFile(Graph g, String basePath, Boolean forceUpdate) {
        File file = new File(getGraphFileName(basePath, g));

        if (file.exists()) {
            if (!forceUpdate)
                return;

            file.delete();
        }

        // get all elements that are not clusters
        Flux<Element> elements = this.firestoreTemplate.withParent(g)
                .findAll(Element.class).filter(e -> e.getGroup().equals("nodes")).sort(new Comparator<Element>() {
                    public int compare(Element arg0, Element arg1) {
                        String a0 = arg0.getData().getParent() != null ? arg0.getData().getParent() : "";
                        String a1 = arg1.getData().getParent() != null ? arg1.getData().getParent() : "";
                        return a0.compareTo(a1);
                    };
                });

        writeToRsfFile(g, file, elements, forceUpdate);
    }

    /**
     * Writes graph object to RSF file.
     * 
     * @param g graph
     * @param file file
     * @param elements list of elements (clusters and nodes)
     * @param forceUpdate whether it should force the update
     */
    private void writeToRsfFile(Graph g, File file, Flux<Element> elements, Boolean forceUpdate) {
        BufferedWriter bw = null;
        try {
            file.createNewFile();

            FileWriter fw = new FileWriter(file);
            bw = new BufferedWriter(fw);

            List<Element> it = elements.collectList().block();

            for (Element i : it) {
                if (it.stream().filter(
                        o -> o.getData().getParent() != null && o.getData().getParent().equals(i.getData().getId()))
                        .findFirst().isPresent())
                    continue;

                String parent = i.getData().getParent();
                String parentLine = parent == null || parent.equals("") ? "root" : parent;

                bw.write("contain ");
                bw.write(parentLine);
                bw.write(" ");
                bw.write(i.getData().getId());
                bw.newLine();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
            } catch (Exception ex) {
                System.out.println("Error in closing the BufferedWriter" + ex);
            }
        }
    }

    /**
     * Gets RSF graph file full path, including file name.
     * 
     * @param basePath base path
     * @param g graph
     * @return graph file full path
     */
    private String getGraphFileName(String basePath, Graph g) {
        String s = basePath + "rsf/" + g.getName() + "-" + g.getType() + ".rsf";
        s = s.replace(" ", "-");
        s = s.replace(".odem", "");
        return s;
    }

    /**
     * Calculates MojoFM between two different graphs.
     * 
     * @param basePath base path fo fing the RSF representation of the graph
     * @param source source graph
     * @param target target graph
     * @return
     */
    private Double calculateMojoFm(String basePath, Graph source, Graph target) {
        try {
            String s = getGraphFileName(basePath, source);
            String t = getGraphFileName(basePath, target);

            String value = runProcess(
                    "java -cp " + basePath + " MoJo " + s + " " + t + " -fm");

            if (value == null || value.equals(""))
                return null;

            return Double.parseDouble(value);
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }

        return null;
    }

    /**
     * Runs process and gets its output message.
     * 
     * ref: https://stackoverflow.com/questions/10723346/why-should-avoid-using-runtime-exec-in-java
     * 
     * @param command command
     * @return output message
     * @throws Exception
     */
    private String runProcess(String command) throws Exception {
        Process pro = Runtime.getRuntime().exec(command);
        String value = getProcessOutputMessage(pro.getInputStream());
        pro.waitFor();

        return value;
    }

    /**
     * Gets process output message from process input stream.
     * 
     * @param ins input stream
     * @return outout message
     * @throws Exception
     */
    private String getProcessOutputMessage(InputStream ins) throws Exception {
        String line = null;
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            return line;
        }

        return "";
    }

    /**
     * Calculates the depth of an specific node.
     * 
     * @param e current element
     * @param elements list of graph elements
     * @return depth of node
     */
    private int calculateDepth(Element e, List<Element> elements) {
        if (e == null)
            return 0;

        try {
            String parent = e.getData().getParent();

            if (parent != null) {
                if (parent.equals(e.getData().getId()))
                    return 0;

                Element p = elements.stream().filter(el -> el.getData().getId().equals(parent)).findFirst().orElse(null);
                int dep = 1 + calculateDepth(p, elements);

                // removing "root" package
                // dep -= 1;

                return dep;
            }

            return 1;
        }
        catch(Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    /**
	 * Interpret and generate a list of log file containing the commits.
	 */
	private void generateLogListFile(String projectName) throws IOException, Throwable {
		PrintStream ps = null;
		try{
            String path = "/home/leo/github/hmd-gen-api/src/main/resources/" + projectName + "_LogListFile.data";
			String loadPath = "/home/leo/github/hmd-gen-api/src/main/resources/" + projectName + "_log_raw.txt";
			FileOutputStream out = new FileOutputStream(path);
			ps = new PrintStream(out);
			loadLogFile(loadPath, ps);
		}
		catch (Exception e) {
			System.out.println(e);
		}
		
		finally {
			if(!Objects.isNull(ps)) {
				ps.close();	
			}
		}
        
        System.out.println("generateLogListFile Finished!");		
	}

    /**
	 * Generate the data file based on the commit logs
	 * 
	 * @param filename Name of the file that will be loaded
	 * @param ps       The prinstream
	 * @throws IOException
	 * @throws ParseException
	 */
	private void loadLogFile(String filename, PrintStream ps) throws IOException, ParseException {
		BufferedReader br = new BufferedReader(new FileReader(filename));

		String line;
		String revision = "";
		String author = "";
		String date = "";
		String type = "";

		new ArrayList<String>();
		ps.println("date\tversion\tauthor\tcurrent_file\trenamed_file");

		while ((line = br.readLine()) != null) {
			try {
				if (line.length() > 0) {
					String tokens[] = line.split(":");
					String tokensTab[] = line.split("\t");
					String tokensSpace[] = line.split(" ");

					/*
					 * If log file is generated from git log we'll use the commit hash to replace
					 * the revision
					 */
					if (tokensSpace.length > 1) {
						if (tokensSpace[0].equals("commit")) {
                            revision = tokensSpace[1];
						}
					}

					if (tokens[0].equals("Author")) {
						if (tokens.length > 1) {
							author = tokens[1].trim().length() == 0 ? "N/A" : tokens[1];
						}

					}

					else if (tokens[0].equals("Date")) {
						String str = tokensTab[0].split(": ")[1].trim();
						if (tokens.length > 1) {
							// Fri Dec 19 03:00:43 2003 +0000
							@SuppressWarnings("deprecation")
							Date dateFromString = new Date(str);
							DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
							date = dateFormat.format(dateFromString);
						}

					}

					/* Git uses D to represent deletions */
					else if (tokensTab[0].equals("M") || tokensTab[0].equals("A") || tokensTab[0].equals("D")) {
						if (tokensTab.length > 1) {
							if (tokensTab[1].endsWith(".java")) {
								type = tokensTab[1];
								ps.println(date + " " + revision.trim() + " " + author.trim().replace(' ', '-') + " "
										+ type.trim());
							}
						}
					}

                    else if (tokensTab[0].equals("R") || tokensTab[0].matches("R...")) {
						if (tokensTab.length > 2) {
							if (tokensTab[2].endsWith(".java")) {
								type = tokensTab[2];
								ps.println(date + " " + revision.trim() + " " + author.trim().replace(' ', '-') + " "
										+ type.trim() + " " + tokensTab[1].trim());
							}  
						}
					}

                    // else if (tokensTab[0].matches("R...")){
                    //     String l = tokensTab[0];
                    // }

                    // else if (tokensTab[0] != null && !tokensTab[0].isEmpty() && !tokensTab[0].contains("commit") && !tokensTab[0].startsWith(" ") && !tokensTab[0].startsWith("Merge:")) {
                    //     String l = tokensTab[0];
                    //     System.out.println(l);
                    // }
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}

		br.close();
	}
}
