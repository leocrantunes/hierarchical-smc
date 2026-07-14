package unirio.teaching.clustering;

public class Element {
    private String group;
    private ElementData data;
    
    public Element() {}
    
    public Element(String group, ElementData data) {
        this.group = group;
        this.data = data;
    }
    
    public String getGroup() {
        return group;
    }
    
    public void setGroup(String group) {
        this.group = group;
    }
    
    public ElementData getData() {
        return data;
    }
    
    public void setData(ElementData data) {
        this.data = data;
    }
    
    @Override
    public String toString() {
        return "Element{" +
                "group='" + group + '\'' +
                ", data=" + data +
                '}';
    }
}
