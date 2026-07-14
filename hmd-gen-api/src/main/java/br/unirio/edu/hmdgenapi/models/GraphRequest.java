package br.unirio.edu.hmdgenapi.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GraphRequest {

  private String name;

  private String type;

  private Integer numberOfClasses;

  private Boolean packageOnly;

  private String timestamp;

  private Integer numberOfPackages;

  private Double averageOfClassesPerPackage;

  private Integer maxDepth;

  private Long executionTimeMs;

  private Double fitness;

  private Double mojofm;

  private Double percentualCommits;

  private List<Element> elements;

}
