package cz.ufal.udapi.core.impl;

import cz.ufal.udapi.core.EnhancedDeps;
import cz.ufal.udapi.core.Node;
import cz.ufal.udapi.core.Root;

import java.util.*;

/**
 * Represents technical root of the sentence.
 *
 * @author Martin Vojtek
 */
public class DefaultRootNode extends DefaultNode {

    public DefaultRootNode(Root tree, Node parent) {
        super(tree, parent);
        setRootFields();
    }

    public DefaultRootNode(Root tree) {
        super(tree, null);
        setRootFields();
    }

    private void setRootFields() {
        setForm("<ROOT>");
        setLemma("<ROOT>");
        setUpos("<ROOT>");
        setXpos("<ROOT>");
        setFeats("<ROOT>");
        setDeprel("<ROOT>");
        setDeps(new EnhancedDeps("_", tree));
    }

    @Override
    public List<Node> getDescendants() {
        return Collections.unmodifiableList(tree.getDescendants());
    }

    @Override
    public List<Node> getDescendants(EnumSet<DescendantsArg> args) {
        if (args.isEmpty()) {
            return Collections.unmodifiableList(tree.getDescendants());
        }

        return getDescendantsInner(args, Optional.empty());
    }

    @Override
    public List<Node> getDescendants(EnumSet<DescendantsArg> args, Node except) {
        if (args.isEmpty()) {
            return Collections.unmodifiableList(tree.getDescendants());
        }

        return getDescendantsInner(args, Optional.of(except));
    }

    @Override
    public List<Node> getDescendantsF() {
        return Collections.unmodifiableList(tree.getDescendants());
    }

    @Override
    protected List<Node> getDescendantsInner(EnumSet<DescendantsArg> args, Optional<Node> except) {
        if (args.contains(DescendantsArg.FIRST_ONLY)) {
            if (args.contains(DescendantsArg.ADD_SELF)) {
                return Arrays.asList(this);
            }
            return Arrays.asList(tree.getDescendants().get(0));
        }

        return super.getDescendantsInner(args, except);
    }

    @Override
    public boolean isDescendantOf(Node node) {
        return false;
    }

    @Override
    public Optional<Node> getPrevNode() {
        return Optional.empty();
    }

    @Override
    public boolean isRoot() {
        return true;
    }

    @Override
    public boolean precedes(Node anotherNode) {
        return true;
    }

    @Override
    public Optional<Node> getNextNode() {
        List<Node> descendants = tree.getDescendants();
        if (descendants.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(descendants.get(0));
    }
}
