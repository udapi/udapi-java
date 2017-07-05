package cz.ufal.udapi.core;

/**
 * Created by mvojtek on 05/07/2017.
 */
public interface EmptyNode extends Node {
    public String getEmptyNodeId();

    public void setEmptyNodeId(String id);

    public int getEmptyNodePrefixId();
}
