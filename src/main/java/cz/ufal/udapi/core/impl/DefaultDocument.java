package cz.ufal.udapi.core.impl;

import cz.ufal.udapi.core.Bundle;
import cz.ufal.udapi.core.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of document.
 *
 * Generates unique IDs for nodes.
 *
 * @author Martin Vojtek
 */
public class DefaultDocument implements Document {
    private int nodeUniqueId;

    private List<Bundle> bundles = new ArrayList<>();

    /**
     *
     * @return unique ID for node
     */
    @Override
    public int getUniqueNodeId() {
        return ++nodeUniqueId;
    }

    /**
     * Default constructor.
     */
    public DefaultDocument() {
    }

    /**
     * Adds bundle to document.
     *
     * @param bundle bundle to add
     */
    @Override
    public void addBundle(Bundle bundle) {
        bundles.add(bundle);
    }

    /**
     * Creates new bundle and adds it to document.
     *
     * @return bundle added to document.
     */
    @Override
    public Bundle createBundle() {
        Bundle bundle = new DefaultBundle(this);
        bundles.add(bundle);
        return bundle;
    }

    /**
     * Returns bundles in document.
     *
     * @return bundles in document.
     */
    @Override
    public List<Bundle> getBundles() {
        return bundles;
    }

    /**
     * Helper method. Returns first bundle.
     *
     * @return first bundle
     */
    @Override
    public Bundle getDefaultBundle() {
        return bundles.get(0);
    }

}
