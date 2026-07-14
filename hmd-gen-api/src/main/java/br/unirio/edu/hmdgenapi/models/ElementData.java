package br.unirio.edu.hmdgenapi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElementData {

    private String id;

    private String parent;

    private String source;

    private String target;

}
