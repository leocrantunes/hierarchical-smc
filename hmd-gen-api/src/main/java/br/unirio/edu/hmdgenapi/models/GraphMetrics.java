package br.unirio.edu.hmdgenapi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GraphMetrics {

    public GraphMetrics(GraphRequest request) {
        setNumberOfPackages(request.getNumberOfPackages());
        setAverageOfClassesPerPackage(request.getAverageOfClassesPerPackage());
        setMaxDepth(request.getMaxDepth());
        setExecutionTimeMs(request.getExecutionTimeMs());
        setFitness(request.getFitness());
        setMojofm(request.getMojofm());
        setPercentualCommits(request.getPercentualCommits());
    }

    private Integer numberOfPackages;

    private Double averageOfClassesPerPackage;

    private Integer maxDepth;

    private Long executionTimeMs;

    private Double fitness;

    private Double mojofm;

    private Double percentualCommits;

}
