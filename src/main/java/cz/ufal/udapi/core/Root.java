package cz.ufal.udapi.core;

import java.util.List;

/**
 * Root represent technical root of the sentence.
 *
 * @author Martin Vojtek
 */
public interface Root {
    /**
     * default zone is undefined
     */
    String DEFAULT_ZONE = "und";

    /**
     *
     * @return node representing technical root
     */
    Node getNode();

    /**
     *
     * @return document the root belongs to
     */
    Document getDocument();

    /**
     *
     * @param bundle new bundle the root will belong to
     */
    void setBundle(Bundle bundle);

    /**
     *
     * @return bundle the root belongs to
     */
    Bundle getBundle();

    /**
     * Add comment to the sentence.
     *
     * @param comment comment to add
     */
    void addComment(String comment);

    /**
     * Returns list of comments belonging to the sentence.
     *
     * @return list of comments
     */
    List<String> getComments();

    /**
     * Adds multiword to the sentence.
     *
     * @param multiword multiword to add
     */
    void addMultiword(String multiword);

    /**
     *
     * @return mutltiwords belonging to the sentence.
     */
    List<String> getMultiwords();

    /**
     * Sets sentence in plain text.
     *
     * @param sentenceText sentence plain text
     */
    void setSentence(String sentenceText);

    /**
     *
     * @return sentence in plain text
     */
    String getSentence();

    /**
     * Orders nodes in the tree to reflect correct word order.
     */
    void normalizeOrder();

    /**
     *
     * @return descendants of the node
     */
    List<Node> getDescendants();

    /**
     *
     * @param zone zone of the sentence
     */
    void setZone(String zone);

    /**
     *
     * @return zone of the sentence
     */
    String getZone();

    /**
     *
     * @return deep copy of the tree
     */
    Root copyTree();

    /**
     *
     * @return ID of the tree
     */
    String getId();

    /**
     *
     * @param id new ID of the tree
     */
    void setId(String id);

    /**
     * Validates if the zone is in correct format.
     */
    void validateZone();
}
