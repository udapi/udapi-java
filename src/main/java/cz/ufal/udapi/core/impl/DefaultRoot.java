package cz.ufal.udapi.core.impl;

import cz.ufal.udapi.core.Bundle;
import cz.ufal.udapi.core.Document;
import cz.ufal.udapi.core.Root;
import cz.ufal.udapi.core.Node;
import cz.ufal.udapi.core.io.UdapiIOException;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of Root.
 *
 * Serves as a container for technical node.
 * Maintains descendants in word order.
 *
 * @author Martin Vojtek
 */
public class DefaultRoot implements Root {

    private final Node node;
    private final Document document;
    private Bundle bundle;
    private String zone = Root.DEFAULT_ZONE;

    private List<String> comments = new ArrayList<>();
    private List<String> multiwords = new ArrayList<>();
    private List<Node> descendants = new ArrayList<>();
    private String text;
    private String id;

    public DefaultRoot(Document document) {
        this.document = document;
        this.node = createNode();
        this.node.setOrd(0);
    }

    public DefaultRoot(Document document, Bundle bundle) {
        this.document = document;
        this.node = createNode();
        this.node.setOrd(0);
        this.bundle = bundle;
    }

    protected Node createNode() {
        return new DefaultRootNode(this);
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public Document getDocument() {
        return document;
    }

    @Override
    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public Bundle getBundle() {
        return bundle;
    }

    @Override
    public void normalizeOrder() {
        int newOrder = 1;
        for (Node descendant : node.getDescendants()) {
            descendant.setOrd(newOrder++);
        }
    }

    @Override
    public List<Node> getDescendants() {
        return descendants;
    }

    @Override
    public void setZone(String zone) {
        this.zone = zone;
    }

    @Override
    public String getZone() {
        return zone;
    }

    @Override
    public Root copyTree() {
        DefaultRoot newRoot = new DefaultRoot(document, bundle);
        copySubtree(getNode(), newRoot.getNode());
        return newRoot;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void validateZone() {
        if (!zone.matches("^[a-z-]+(_[A-Za-z0-9-])?$")) {
            throw new UdapiIOException("'" + zone + "' is not a valid zone name (from treeId='" + id + "')");
        }
    }

    @Override
    public String getAddress() {
        return bundle.getId() + ("".equals(zone) ? "" : "/" + zone);
    }

    @Override
    public Node createChild() {
        return node.createChild();
    }

    @Override
    public String getForm() {
        return node.getForm();
    }

    @Override
    public String getLemma() {
        return node.getLemma();
    }

    @Override
    public String getUpos() {
        return node.getUpos();
    }

    @Override
    public String getXpos() {
        return node.getXpos();
    }

    @Override
    public String getFeats() {
        return node.getFeats();
    }

    @Override
    public String getDeprel() {
        return node.getDeprel();
    }

    @Override
    public String getDeps() {
        return node.getDeps();
    }

    @Override
    public String getMisc() {
        return node.getMisc();
    }

    private void copySubtree(Node oldNode, Node newNode) {
        for (Node child : oldNode.getChildren()) {
            Node newChild = newNode.createChild();
            newChild.setDeps(child.getDeps());
            newChild.setDeprel(child.getDeprel());
            newChild.setFeats(child.getFeats());
            newChild.setForm(child.getForm());
            newChild.setHead(child.getHead());
            newChild.setLemma(child.getLemma());
            newChild.setMisc(child.getMisc());
            newChild.setOrd(child.getOrd());
            newChild.setUpos(child.getUpos());
            newChild.setXpos(child.getXpos());
            copySubtree(child, newChild);
        }
    }

    public void addComment(String comment) {
        this.comments.add(comment);
    }

    public List<String> getComments() {
        return comments;
    }

    public void addMultiword(String multiword) {
        this.multiwords.add(multiword);
    }

    public List<String> getMultiwords() {
        return multiwords;
    }

    @Override
    public void setSentence(String sentenceText) {
        this.text = sentenceText;
    }

    @Override
    public String getSentence() {
        return text;
    }


}
