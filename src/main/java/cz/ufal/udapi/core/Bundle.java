package cz.ufal.udapi.core;

import java.util.List;
import java.util.Optional;

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
    Root createTree();

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

    /**
     * Returns tree with given zone.
     *
     * @param zone zone to search for
     * @return tree with given zone
     */
    Optional<Root> getTree(String zone);

    /**
     * Removes bundle from the document.
     */
    void remove();

    /**
     * Returns index of the bundle.
     *
     * @return index of the bundle
     */
    int getNumber();

    /**
     *
     * @return address of the bundle
     */
    String getAddress();
}
