package br.unirio.edu.hmdgenapi.models;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collectionName = "elements")
public class Element {

    @DocumentId
    private String id;

    private String group;

    private ElementData data;
    
}
