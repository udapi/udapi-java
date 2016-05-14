package cz.ufal.udapi.core;

import java.util.List;

/**
 * Document representing given CoNLLU file or some collection of bundles.
 *
 * @author Martin Vojtek
 */
public interface Document {
    /**
     * Useful for node IDs generation.
     *
     * @return new unique id in the space of the document
     */
    int getUniqueNodeId();

    /**
     * Adds bundle to the document.
     *
     * @param bundle bundle to add
     */
    void addBundle(Bundle bundle);

    /**
     * Creates new bundle and adds it to the document.
     *
     * @return new bundle added to the document
     */
    Bundle createBundle();

    /**
     *
     * @return bundles of the document
     */
    List<Bundle> getBundles();

    /**
     * Convenient method to return first bundle.
     *
     * @return first bundle
     */
    Bundle getDefaultBundle();
}
