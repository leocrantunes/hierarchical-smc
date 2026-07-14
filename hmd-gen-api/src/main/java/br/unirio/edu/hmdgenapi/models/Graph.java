package br.unirio.edu.hmdgenapi.models;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collectionName = "graphs")
public class Graph {

  public Graph(GraphRequest request) {
    setName(request.getName());
    setType(request.getType());
    setNumberOfClasses(request.getNumberOfClasses());
    setPackageOnly(request.getPackageOnly());
    setTimestamp(request.getTimestamp());
    setMetrics(new GraphMetrics(request));
  }

  @DocumentId
  private String id;

  private String name;

  private String type;

  private Integer numberOfClasses;

  private Boolean packageOnly;

  private String timestamp;

  private GraphMetrics metrics;

}
