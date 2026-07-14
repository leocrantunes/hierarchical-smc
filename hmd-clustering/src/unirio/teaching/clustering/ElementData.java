package unirio.teaching.clustering;

public class ElementData {
    private String id;
    private String parent;
    private String source;
    private String target;
    private String name;
    private String label;
    
    public ElementData() {}
    
    public ElementData(String id, String parent, String source, String target) {
        this.id = id;
        this.parent = parent;
        this.source = source;
        this.target = target;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getParent() {
        return parent;
    }
    
    public void setParent(String parent) {
        this.parent = parent;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getTarget() {
        return target;
    }
    
    public void setTarget(String target) {
        this.target = target;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    @Override
    public String toString() {
        return "ElementData{" +
                "id='" + id + '\'' +
                ", parent='" + parent + '\'' +
                ", source='" + source + '\'' +
                ", target='" + target + '\'' +
                ", name='" + name + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
