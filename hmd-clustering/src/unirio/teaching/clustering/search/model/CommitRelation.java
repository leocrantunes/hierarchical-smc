package unirio.teaching.clustering.search.model;

public class CommitRelation extends Decomposition
{
    public CommitRelation(int size)
    {
      super(size, new WeightedModule("root"));
    }
}
