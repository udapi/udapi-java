package cz.ufal.udapi.core;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

/**
 * Node represents token (word) of the sentence.
 *
 * @author Martin Vojtek
 */
public interface Node {

    /**
     * Args used for child removal.
     */
    enum RemoveArg {
        REHANG /* rehangs children to parent of the removed parent */,
        WARN
    }

    /**
     * Args used for children retrieval.
     */
    enum ChildrenArg {
        ADD_SELF /* result will include also self node */,
        FIRST_ONLY /* result will include only the first child */,
        LAST_ONLY /* result will include only the last child */
    }

    /**
     * Args used for descendants retrieval.
     */
    enum DescendantsArg {
        ADD_SELF /* result will include also self node */,
        FIRST_ONLY /* result will include only the first child */,
        LAST_ONLY /* result will include only the last child */
    }

    enum ShiftArg {
        WITHOUT_CHILDREN /* shift node without its children */,
        SKIP_IF_DESCENDANT /* skip shifting if given node is descendant of the node to shift after/before */
    }

    /**
     * Remove node from the tree. Only non-root nodes can be removed.
     */
    void remove();

    /**
     * Creates new child of the given node and returns it.
     *
     * @return new child
     */
    Node createChild();

    /**
     * Returns children of the node in word order.
     *
     * @return children of the node
     */
    List<Node> getChildren();

    /**
     * Returns children of the node in word order.
     *
     * @param args args to augment resulting collection
     * @return children of the node
     */
    List<Node> getChildren(EnumSet<ChildrenArg> args);

    /**
     * Returns parent node.
     *
     * @return parent node
     */
    Optional<Node> getParent();

    /**
     * Sets parent.
     *
     * @param node new parent node
     */
    void setParent(Node node);

    /**
     * Sets parent.
     *
     * @param node new parent node
     * @param skipCycles skip operation in case of cycles
     */
    void setParent(Node node, boolean skipCycles);

    /**
     *
     * @return true if the node is technical root
     */
    boolean isRoot();

    /**
     *
     * @return descendants of the node in word order
     */
    List<Node> getDescendants();

    /**
     *
     * @param args args to augment resulting collection
     * @return descendants of the node in word order
     */
    List<Node> getDescendants(EnumSet<DescendantsArg> args);

    /**
     *
     * @param args args to augment resulting collection
     * @param except the resulting collection without this node
     * @return descendants of the node in word order
     */
    List<Node> getDescendants(EnumSet<DescendantsArg> args, Node except);

    /**
     *
     * @return siblings of the node
     */
    List<Node> getSiblings();

    /**
     *
     * @return previous sibling
     */
    Optional<Node> getPrevSibling();

    /**
     *
     * @return next sibling
     */
    Optional<Node> getNextSibling();

    /**
     *
     * @param newNextSibling set new next sibling
     */
    void setNextSibling(Optional<Node> newNextSibling);

    /**
     *
     * @return previous node in word order
     */
    Optional<Node> getPrevNode();

    /**
     *
     * @return next node in word order
     */
    Optional<Node> getNextNode();

    /**
     *
     * @param node node we want to find out if there is descendat relation
     * @return true if this is descendant of given node
     */
    boolean isDescendantOf(Node node);

    /**
     *
     * @return ID of the node
     */
    int getId();

    /**
     *
     * @return form of the node
     */
    String getForm();

    /**
     * Sets form of the node.
     *
     * @param form new form of the node
     */
    void setForm(String form);

    /**
     *
     * @return lemma of the node
     */
    String getLemma();

    /**
     *
     * @param lemma new lemma of the node
     */
    void setLemma(String lemma);

    /**
     *
     * @return upos of the node
     */
    String getUpos();

    /**
     *
     * @param upos new upos of the node
     */
    void setUpos(String upos);

    /**
     *
     * @return xpos of the node
     */
    String getXpos();

    /**
     *
     * @param xpos new xpos of the node
     */
    void setXpos(String xpos);

    /**
     *
     * @return feats of the node
     */
    String getFeats();

    /**
     *
     * @param feats new feats of the node
     */
    void setFeats(String feats);

    /**
     *
     * @return head of the node
     */
    String getHead();

    /**
     *
     * @param head new head of the node
     */
    void setHead(String head);

    /**
     *
     * @return deprel of the node
     */
    String getDeprel();

    /**
     *
     * @param deprel new deprel of the node
     */
    void setDeprel(String deprel);

    /**
     *
     * @return deps of the node
     */
    String getDeps();


    /**
     *
     * @param deps new deps of the node
     */
    void setDeps(String deps);

    /**
     *
     * @return misc of the node
     */
    String getMisc();

    /**
     *
     * @param misc new misc of the node
     */
    void setMisc(String misc);

    /**
     *
     * @return ord of the node
     */
    int getOrd();

    /**
     *
     * @param ord new ord of the node
     */
    void setOrd(int ord);

    /**
     * Shifts node after given node.
     *
     * @param node node to shift after
     */
    void shiftAfterNode(Node node);

    /**
     * Shifts node after given node.
     *
     * @param node node to shift after
     * @param args args to augment resulting collection
     */
    void shiftAfterNode(Node node, EnumSet<ShiftArg> args);

    /**
     * Shifts node before given node.
     *
     * @param node node to shift before
     */
    void shiftBeforeNode(Node node);

    /**
     * Shifts node before given node.
     *
     * @param node node to shift before
     * @param args args to augment resulting collection
     */
    void shiftBeforeNode(Node node, EnumSet<ShiftArg> args);

    /**
     * Shifts node with its subtree after given node.
     *
     * @param node node to shift after
     */
    void shiftAfterSubtree(Node node);

    /**
     * Shifts node with its subtree after given node.
     *
     * @param node node to shift after
     * @param args args to augment resulting collection
     */
    void shiftAfterSubtree(Node node, EnumSet<ShiftArg> args);

    /**
     * Shifts node with its subtree before given node.
     *
     * @param node node to shift before
     */
    void shiftBeforeSubtree(Node node);

    /**
     * Shifts node with its subtree before given node.
     *
     * @param node node to shift before
     * @param args args to augment resulting collection
     */
    void shiftBeforeSubtree(Node node, EnumSet<ShiftArg> args);

    /**
     *
     * @param anotherNode anotherNode we want to find out precedes relation
     * @return true if the node precedes anotherNode
     */
    boolean precedes(Node anotherNode);

    /**
     * Removes node from the tree.
     *
     * @param args args to augment removal
     */
    void remove(EnumSet<RemoveArg> args);

    /**
     *
     * @return tree of the sentence the node is part of
     */
    Root getTree();

    /**
     *
     * @return bundle the node belongs to
     */
    Bundle getBundle();

    /**
     *
     * @return document the node belongs to
     */
    Document getDocument();
}
