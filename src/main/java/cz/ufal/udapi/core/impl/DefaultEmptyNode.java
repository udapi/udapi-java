package cz.ufal.udapi.core.impl;

import cz.ufal.udapi.core.EmptyNode;
import cz.ufal.udapi.core.Root;

/**
 * Created by mvojtek on 05/07/2017.
 */
public class DefaultEmptyNode extends DefaultNode implements EmptyNode {

    private String id;

    public DefaultEmptyNode(Root tree) {
        super(tree);
    }

    @Override
    public String getEmptyNodeId() {
        return id;
    }

    @Override
    public void setEmptyNodeId(String id) {
        this.id = id;
    }

    @Override
    public int getEmptyNodePrefixId() {
        return Integer.parseInt(id.substring(0, id.indexOf(".")));
    }
}
