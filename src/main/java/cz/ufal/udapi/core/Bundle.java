package cz.ufal.udapi.core;

import java.util.List;

/**
 * Represents bundle. Bundle is a collection of sentence trees.
 *
 * @author Martin Vojtek
 */
public interface Bundle {
    /**
     * Adds tree to the bundle.
     *
     * @param root root of sentence tree
     */
    void addTree(Root root);

    /**
     * Creates tree and adds it to the bundle.
     *
     * @return created tree
     */
    Root addTree();

    /**
     * Returns list of sentence trees.
     *
     * @return list of sentence trees
     */
    List<Root> getTrees();

    /**
     * Ties bundle with given document.
     * @param document document the bundle will belong to
     */
    void setDocument(Document document);

    /**
     * Returns document of the bundle.
     *
     * @return document of the bundle
     */
    Document getDocument();

    /**
     *
     * @return ID of the bundle
     */
    String getId();

    /**
     * Sets ID of the bundle.
     *
     * @param id new ID of the bundle
     */
    void setId(String id);
}
