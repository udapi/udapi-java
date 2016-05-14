package cz.ufal.udapi.core.impl;

import cz.ufal.udapi.core.Bundle;
import cz.ufal.udapi.core.Document;
import cz.ufal.udapi.core.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of Bundle.
 *
 * Bundle has reference to document and serves the purpose of container for sentence trees.
 *
 * @author Martin Vojtek
 */
public class DefaultBundle implements Bundle {

    private List<Root> trees = new ArrayList<>();
    private Document document;
    private String id;
    private int index = -1;

    public DefaultBundle(Document document) {
        this.document = document;
    }

    public void addTree(Root root) {
        root.setBundle(this);
        trees.add(root);
    }

    @Override
    public Root createTree() {
        Root tree = new DefaultRoot(document, this);
        trees.add(tree);
        return tree;
    }

    @Override
    public List<Root> getTrees() {
        return trees;
    }

    @Override
    public void setDocument(Document document) {
        this.document = document;
    }

    @Override
    public Document getDocument() {
        return document;
    }

    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Optional<Root> getTree(String zone) {
        return trees.stream().filter(tree -> tree.getZone().equals(zone)).findFirst();
    }

    @Override
    public void remove() {
        document.getBundles().remove(this);
        document = null;
    }

    @Override
    public int getNumber() {
        return index;
    }
}
