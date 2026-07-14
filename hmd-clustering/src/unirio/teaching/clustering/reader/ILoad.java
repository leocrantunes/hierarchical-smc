package unirio.teaching.clustering.reader;

import java.io.FileNotFoundException;

import javax.management.modelmbean.XMLParseException;

import unirio.teaching.clustering.model.Project;

public interface ILoad {
    Project load(String filename) throws FileNotFoundException, XMLParseException;
}
