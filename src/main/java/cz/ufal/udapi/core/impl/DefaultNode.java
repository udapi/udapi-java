package cz.ufal.udapi.core.impl;

import cz.ufal.udapi.core.*;
import cz.ufal.udapi.exception.UdapiException;

import java.util.*;

/**
 * Default implementation of node.
 *
 * Every word in a sentence is represented by a node.
 *
 * @author Martin Vojtek
 */
public class DefaultNode implements Node {

    protected final Root tree;

    private final int id;
    private int ord = -1;
    private boolean isRemoved;

    private String form;
    private String lemma;
    private String upos;
    private String xpos;
    private String feats;
    private String head;
    private String deprel;
    private EnhancedDeps deps;
    private String misc;
    private Optional<Mwt> mwt = Optional.empty();

    private Optional<Node> firstChild = Optional.empty();
    private Optional<Node> nextSibling = Optional.empty();

    private Optional<Node> parent;

    public DefaultNode(Root tree, Node parent) {
        this.parent = Optional.ofNullable(parent);
        this.tree = tree;
        this.id = tree.getDocument().getUniqueNodeId();
    }

    public DefaultNode(Root tree) {
        this(tree, null);
    }

    @Override
    public void remove() {
        remove(EnumSet.noneOf(Node.RemoveArg.class));
    }

    /**
     * For non-root nodes, the general address format is:
     * node.bundle.bundle_id + '/' + node.root.zone + '#' + node.ord,
     * e.g. s123/en_udpipe#4. If zone is empty, the slash is excluded as well,
     * e.g. s123#4.
     * @return full (document-wide) id of the node.
     */
    @Override
    public String getAddress() {
        return (null != getRoot() ? getRoot().getAddress() : "?") + "#" + getOrd();
    }

    @Override
    public void remove(EnumSet<Node.RemoveArg> args) {
        //already removed
        if (isRemoved) return;

        Optional<Node> parent = getParent();
        if (args.contains(RemoveArg.REHANG)) {
            for (Node child : getChildren()) {
                child.setParent(parent.get());
            }
        }

        if (args.contains(RemoveArg.WARN)) {
            System.err.println(getAddress() + " is being removed by remove, but it has (unexpected) children");
        }

        List<Node> toRemove = getDescendantsF();
        toRemove.add(this);
        if (!toRemove.isEmpty()) {
            List<Node> allNodes = tree.getDescendants();
            allNodes.removeAll(toRemove);

            //update ord of the nodes in the tree
            getRoot().normalizeOrder();
        }

        //Disconnect the node from its parent (& siblings) and delete all attributes
        Optional<Node> node = toDefaultNode(parent.get()).getFirstChild();
        if (node.isPresent() && node.get() == this) {
            toDefaultNode(parent.get()).setFirstChild(this.getNextSibling());
        } else {
            while (node.isPresent() && (!node.get().getNextSibling().isPresent() || this != node.get().getNextSibling().get())) {
                node = node.get().getNextSibling();
            }
            if (node.isPresent()) {
                node.get().setNextSibling(getNextSibling());
            }
        }

        for (Node removedNode : toRemove) {
            toDefaultNode(removedNode).isRemoved = true;
        }
    }

    @Override
    public Root getRoot() {
        return tree;
    }

    @Override
    public Bundle getBundle() {
        return tree.getBundle();
    }

    @Override
    public Document getDocument() {
        return getBundle().getDocument();
    }

    public List<Node> getDescendantsF() {
        if (!getFirstChild().isPresent()) {
            return new ArrayList<>();
        }

        Deque<Node> stack = new ArrayDeque<>();
        stack.push(getFirstChild().get());

        List<Node> descs = new ArrayList<>();
        while (!stack.isEmpty()) {
            Node node = stack.pop();
            descs.add(node);
            node.getNextSibling().ifPresent(next -> stack.push(next));
            toDefaultNode(node).getFirstChild().ifPresent(first -> stack.push(first));
        }

        return descs;
    }

    @Override
    public Node createChild() {
        Node newChild = createNode();
        newChild.setParent(this);
        return newChild;
    }

    protected Node createNode() {
        DefaultNode newNode = new DefaultNode(tree);
        tree.getDescendants().add(newNode);
        newNode.ord = tree.getDescendants().size();
        return newNode;
    }

    @Override
    public List<Node> getChildren(EnumSet<ChildrenArg> args) {

        List<Node> result = new ArrayList<>();

        Optional<Node> child = getFirstChild();
        while (child.isPresent()) {
            result.add(child.get());
            child = child.get().getNextSibling();
        }

        if (!args.isEmpty()) {
            if (args.contains(ChildrenArg.ADD_SELF)) {
                result.add(this);
            }
            if (args.contains(ChildrenArg.FIRST_ONLY)) {
                return getFirstLastNode(result, true);
            }
            if (args.contains(ChildrenArg.LAST_ONLY)) {
                return getFirstLastNode(result, false);
            }
        }

        result.sort((o1, o2) -> o1.getOrd() - o2.getOrd());
        return result;
    }

    @Override
    public List<Node> getChildren() {
        return getChildren(EnumSet.noneOf(Node.ChildrenArg.class));
    }

    @Override
    public Optional<Node> getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        setParent(parent, false);
    }

    @Override
    public void setParent(Node parent, boolean skipCycles) {

        if (null == parent) {
            throw new UdapiException("Not allowed to set null parent.");
        }

        //check cycles
        if (this == parent) {
            if (skipCycles) return;
            throw new UdapiException("Bundle " + tree.getBundle().getId() + ": Attempt to set parent of " + ord
                    + " to itself (cycle).");
        }
        if (firstChild.isPresent()) {
            Optional<Node> grandpa = parent.getParent();
            while (grandpa.isPresent()) {
                if (grandpa.get() == this) {
                    if (skipCycles) return;
                    throw new UdapiException("Bundle " + tree.getBundle().getId() + ": Attempt to set parent of " + ord
                            + " to the node " + parent.getId() + ", which would lead to a cycle.");
                }
                grandpa = grandpa.get().getParent();
            }
        }

        //Disconnect the node from its original parent
        Optional<Node> origParent = getParent();
        if (origParent.isPresent()) {
            Optional<Node> node = toDefaultNode(origParent.get()).getFirstChild();
            if (node.isPresent() && this == node.get()) {
                toDefaultNode(origParent.get()).setFirstChild(nextSibling);
            } else {
                while (node.isPresent() && (!node.get().getNextSibling().isPresent() || this != node.get().getNextSibling().get())) {
                    node = node.get().getNextSibling();
                }
                if (node.isPresent()) {
                    node.get().setNextSibling(nextSibling);
                }
            }
        }

        //Attach the node to its parent and linked list of siblings.
        this.parent = Optional.of(parent);
        this.nextSibling = toDefaultNode(parent).getFirstChild();
        toDefaultNode(parent).setFirstChild(Optional.of(this));
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public List<Node> getDescendants(EnumSet<Node.DescendantsArg> args, Node except) {

        if (args.isEmpty()) {
            return getDescendantsInner(args, Optional.of(except));
        }

        return getDescendantsInner(args, Optional.of(except));
    }

    @Override
    public List<Node> getDescendants(EnumSet<Node.DescendantsArg> args) {
        return getDescendantsInner(args, Optional.empty());
    }

    @Override
    public List<Node> getDescendants() {
        return getDescendantsInner(EnumSet.noneOf(Node.DescendantsArg.class), Optional.empty());
    }

    protected List<Node> getDescendantsInner(EnumSet<Node.DescendantsArg> args, Optional<Node> except) {
        if (except.isPresent() && this == except.get()) {
            return new ArrayList<>();
        }

        List<Node> descs = new ArrayList<>();
        Deque<Node> stack = new ArrayDeque<>();
        getFirstChild().ifPresent(first -> stack.push(first));
        Node node;
        while (!stack.isEmpty()) {
            node = stack.pop();
            node.getNextSibling().ifPresent(next -> stack.push(next));
            if (except.isPresent() && except.get() == node) {
                continue;
            }
            descs.add(node);
            toDefaultNode(node).getFirstChild().ifPresent(first -> stack.push(first));
        }

        if (args.contains(DescendantsArg.ADD_SELF)) {
            descs.add(this);
        }

        if (args.contains(DescendantsArg.FIRST_ONLY)) {
            return getFirstLastNode(descs, true);
        }

        if (args.contains(DescendantsArg.LAST_ONLY)) {
            return getFirstLastNode(descs, false);
        }

        descs.sort((o1, o2) -> o1.getOrd() - o2.getOrd());
        return descs;
    }

    private DefaultNode toDefaultNode(Node node) {
        return (DefaultNode) node;
    }

    private List<Node> getFirstLastNode(List<Node> descs, boolean first) {
        if (!descs.isEmpty()) {
            Node firstLast = descs.get(0);
            for (int i = 1; i < descs.size(); i++) {
                Node next = descs.get(i);
                if (first) {
                    if (next.getOrd() < firstLast.getOrd()) {
                        firstLast = next;
                    }
                } else {
                    if (next.getOrd() > firstLast.getOrd()) {
                        firstLast = next;
                    }
                }
            }
            return Arrays.asList(firstLast);
        }
        return new ArrayList<>();
    }

    @Override
    public List<Node> getSiblings() {
        if (parent.isPresent()) {
            List<Node> siblings = parent.get().getChildren();
            siblings.remove(this);
            return siblings;
        } else return new ArrayList<>();
    }

    @Override
    public Optional<Node> getPrevSibling() {
        if (parent.isPresent()) {
            List<Node> parentChildren = parent.get().getChildren();

            int index = parentChildren.indexOf(this);
            if (index != -1 && index > 0) {
                return Optional.of(parentChildren.get(index - 1));
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Node> getNextSibling() {
        return nextSibling;
    }

    @Override
    public void setNextSibling(Optional<Node> newNextSibling) {
        this.nextSibling = newNextSibling;
    }

    @Override
    public Optional<Node> getNextNode() {
        int ord = getOrd();
        List<Node> rootDescendants = tree.getDescendants();
        if (ord == rootDescendants.size()) {
            return Optional.empty();
        }
        return Optional.of(rootDescendants.get(ord));
    }

    @Override
    public Optional<Node> getPrevNode() {

        int ord = getOrd() - 1;

        if (0 == ord) {
            return Optional.of(tree.getNode());
        }

        return Optional.of(tree.getDescendants().get(ord - 1));
    }

    @Override
    public boolean isDescendantOf(Node node) {

        if (!toDefaultNode(node).getFirstChild().isPresent()) {
            return false;
        }

        Optional<Node> pathParent = parent;
        while (pathParent.isPresent()) {
            if (pathParent.get() == node) {
                return true;
            } else {
                pathParent = pathParent.get().getParent();
            }
        }
        return false;
    }

    public void shiftAfterNode(Node node) {
        shiftAfterNode(node, EnumSet.noneOf(ShiftArg.class));
    }

    public void shiftAfterNode(Node node, EnumSet<ShiftArg> args) {
        shiftToNode(node, true, false, args);
    }

    public void shiftBeforeNode(Node node) {
        shiftBeforeNode(node, EnumSet.noneOf(ShiftArg.class));
    }

    public void shiftBeforeNode(Node node, EnumSet<ShiftArg> args) {
        shiftToNode(node, false, false, args);
    }

    public void shiftBeforeSubtree(Node node) {
        shiftBeforeSubtree(node, EnumSet.noneOf(ShiftArg.class));
    }

    public void shiftBeforeSubtree(Node node, EnumSet<ShiftArg> args) {
        shiftToNode(node, false, true, args);
    }

    public void shiftAfterSubtree(Node node) {
        shiftAfterSubtree(node, EnumSet.noneOf(ShiftArg.class));
    }

    public void shiftAfterSubtree(Node node, EnumSet<ShiftArg> args) {
        shiftToNode(node, true, true, args);
    }

    private void shiftToNode(Node referenceNode, boolean after, boolean subtree, EnumSet<ShiftArg> args) {

        //node.shiftAfterNode(node) should result in no action.
        if (!subtree && this == referenceNode) {
            return;
        }

        boolean withoutChildren = args.contains(ShiftArg.WITHOUT_CHILDREN);
        boolean skipIfDescendant = args.contains(ShiftArg.SKIP_IF_DESCENDANT);

        if (!firstChild.isPresent()) {
            withoutChildren = true;
        }

        if (!withoutChildren && referenceNode.isDescendantOf(this)) {
            if (skipIfDescendant) {
                return;
            }

            System.err.println("Node " + referenceNode + " is a descendant of " + this
                    + ". Maybe you have forgotten ShiftArg.WITHOUT_CHILDREN.");
        }

        //For shiftSubtree* methods, we need to find the real reference node first.
        if (subtree) {
            if (withoutChildren) {
                Node newRef = null;
                if (after) {

                    if (this != referenceNode) {
                        newRef = referenceNode;
                    }

                    for (Node node : toDefaultNode(referenceNode).getDescendantsF()) {
                        if (this == node) continue;
                        if (null == newRef || node.getOrd() > newRef.getOrd()) {
                            newRef = node;
                        }
                    }
                } else {
                    if (this != referenceNode) {
                        newRef = referenceNode;
                    }

                    for (Node node : toDefaultNode(referenceNode).getDescendantsF()) {
                        if (this == node) continue;
                        if (null == newRef || node.getOrd() < newRef.getOrd()) {
                            newRef = node;
                        }
                    }
                }
                if (null == newRef) {
                    return;
                }
                referenceNode = newRef;
            } else {
                //$reference_node, 1, !$after, $after, $self
                EnumSet<DescendantsArg> descendantsArgs = EnumSet.of(DescendantsArg.ADD_SELF);
                if (after) {
                    descendantsArgs.add(DescendantsArg.LAST_ONLY);
                } else {
                    descendantsArgs.add(DescendantsArg.FIRST_ONLY);
                }

                List<Node> descendants = referenceNode.getDescendants(descendantsArgs, this);
                referenceNode = descendants.get(0);
            }
        }

        //convert shiftAfter* to shiftBefore*
        List<Node> allNodes = tree.getDescendants();
        int referenceOrd = referenceNode.getOrd();
        if (after) {
            referenceOrd++;
        }

        //without children means moving just one node, which is easier
        if (withoutChildren) {
            int myOrd = getOrd();
            if (referenceOrd > myOrd + 1) {
                for (int newOrd = myOrd; newOrd < referenceOrd - 1; newOrd++) {
                    Node ordNode = allNodes.get(newOrd);
                    allNodes.set(newOrd - 1, ordNode);
                    ordNode.setOrd(newOrd);
                }
                allNodes.set(referenceOrd - 2, this);
                setOrd(referenceOrd - 1);
            } else if (referenceOrd < myOrd) {
                for (int newOrd = myOrd; newOrd > referenceOrd; newOrd--) {
                    Node ordNode = allNodes.get(newOrd - 2);
                    allNodes.set(newOrd - 1, ordNode);
                    ordNode.setOrd(newOrd);
                }
                allNodes.set(referenceOrd - 1, this);
                setOrd(referenceOrd);
            }
            return;
        }

        //which nodes are to be moved?
        //this and all its descendants
        List<Node> nodesToMove = getDescendants(EnumSet.of(DescendantsArg.ADD_SELF));
        int firstOrd = nodesToMove.get(0).getOrd();
        int lastOrd = nodesToMove.get(nodesToMove.size() - 1).getOrd();

        //TODO: optimization in case of no "gaps"

        //First, move a node from position sourceOrd to position targetOrd RIGH-ward.
        //sourceOrd iterates decreasingly over nodes which are not moving.
        int targetOrd = lastOrd;
        int sourceOrd = lastOrd - 1;
        int moveOrd = nodesToMove.size() - 2;

        RIGHTSWIPE:
        while (sourceOrd >= referenceOrd) {
            while (moveOrd >= 0 && allNodes.get(sourceOrd - 1) == nodesToMove.get(moveOrd)) {
                sourceOrd--;
                moveOrd--;
                if (sourceOrd < referenceOrd) {
                    break RIGHTSWIPE;
                }
            }
            Node ordNode = allNodes.get(sourceOrd - 1);
            allNodes.set(targetOrd - 1, ordNode);
            ordNode.setOrd(targetOrd);
            targetOrd--;
            sourceOrd--;
        }

        //Second, move a node from position sourceOrd to position targetOrd LEFT-ward.
        //sourceOrd iterates increasingly over nodes which are not moving.
        targetOrd = firstOrd;
        sourceOrd = firstOrd + 1;
        moveOrd = 1;

        LEFTSWIPE:
        while (sourceOrd < referenceOrd) {
            while (moveOrd < nodesToMove.size() && allNodes.get(sourceOrd - 1) == nodesToMove.get(moveOrd)) {
                sourceOrd++;
                moveOrd++;
                if (sourceOrd >= referenceOrd) {
                    break LEFTSWIPE;
                }
            }
            Node ordNode = allNodes.get(sourceOrd - 1);
            allNodes.set(targetOrd - 1, ordNode);
            ordNode.setOrd(targetOrd);
            targetOrd++;
            sourceOrd++;
        }

        //Third, move nodesToMove to targetOrd RIGHT-ward
        if (referenceOrd < firstOrd) {
            targetOrd = referenceOrd;
        }
        for (Node node : nodesToMove) {
            allNodes.set(targetOrd - 1, node);
            node.setOrd(targetOrd++);
        }

    }

    @Override
    public boolean precedes(Node anotherNode) {
        return ord < anotherNode.getOrd();
    }

    Optional<Node> getFirstChild() {
        return firstChild;
    }

    void setFirstChild(Optional<Node> newFirstChild) {
        this.firstChild = newFirstChild;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultNode that = (DefaultNode) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public int getOrd() {
        return ord;
    }

    public void setOrd(int ord) {
        this.ord = ord;
    }

    public int getId() {
        return id;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public String getUpos() {
        return upos;
    }

    public void setUpos(String upos) {
        this.upos = upos;
    }

    public String getXpos() {
        return xpos;
    }

    public void setXpos(String xpos) {
        this.xpos = xpos;
    }

    public String getFeats() {
        return feats;
    }

    public void setFeats(String feats) {
        this.feats = feats;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getDeprel() {
        return deprel;
    }

    public void setDeprel(String deprel) {
        this.deprel = deprel;
    }

    public EnhancedDeps getDeps() {
        return deps;
    }

    public void setDeps(EnhancedDeps deps) {
        this.deps = deps;
    }

    public String getMisc() {
        return misc;
    }

    public void setMisc(String misc) {
        this.misc = misc;
    }

    @Override
    public Optional<Mwt> getMwt() {
        return mwt;
    }

    @Override
    public void setMwt(Mwt mwt) {
        this.mwt = Optional.of(mwt);
    }

    @Override
    public String toString() {
        return "DefaultNode[ord='" + ord + "', form='" + form + "']";
    }
}
