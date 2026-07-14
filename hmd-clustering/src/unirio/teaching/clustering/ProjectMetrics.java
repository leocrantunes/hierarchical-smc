package unirio.teaching.clustering;

import java.util.List;

public class ProjectMetrics {
    public String projectName;
    public String algorithm;
    public String fileName;
    
    // Basic structural metrics
    public int numberOfNodes;
    public int numberOfEdges;
    public int numberOfPackages;
    
    // Calculated metrics
    public double avgClassesPerPackage;
    public double mojoFM;
    public double singlePackageCommitRatio;
    public double avgPackageCommit;
    
    // Store nodes for commit processing
    public List<Element> nodes;
    
    public ProjectMetrics() {}
    
    @Override
    public String toString() {
        return "ProjectMetrics{" +
                "projectName='" + projectName + '\'' +
                ", algorithm='" + algorithm + '\'' +
                ", fileName='" + fileName + '\'' +
                ", numberOfNodes=" + numberOfNodes +
                ", numberOfEdges=" + numberOfEdges +
                ", numberOfPackages=" + numberOfPackages +
                ", avgClassesPerPackage=" + avgClassesPerPackage +
                ", mojoFM=" + mojoFM +
                ", singlePackageCommitRatio=" + singlePackageCommitRatio +
                ", avgPackageCommit=" + avgPackageCommit +
                '}';
    }
}
