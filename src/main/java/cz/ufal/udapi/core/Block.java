package cz.ufal.udapi.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Block represents reader, writer or some transformation.
 *
 * Block is a base block of scenario. Scenario is a sequence of blocks.
 *
 * @author Martin Vojtek
 */
public class Block {

    /**
     * Parameters of the block.
     */
    private final Map<String, String> params;

    /**
     * Default constructor.
     */
    public Block() {
        this(new HashMap<>());
    }

    /**
     * Constructor with parameters.
     *
     * @param params parameters of the block
     */
    public Block(Map<String, String> params) {
        this.params = params;
    }

    /**
     *
     * @return parameters of the block.
     */
    protected Map<String, String> getParams() {
        return params;
    }

    /**
     * Called once at the beginning of block processing.
     */
    public void processStart() {
    }

    /**
     * Called once at the end of block processing.
     */
    public void processEnd() {
    }

    /**
     * Called before document processing.
     *
     * @param document document to process
     */
    public void beforeProcessDocument(Document document) {
    }

    /**
     * Processes document.
     *
     * The main method for document processing.
     *
     * @param document document to process
     */
    public void processDocument(Document document) {
        for (Bundle bundle : document.getBundles()) {
            if (shouldProcessBundle(bundle)) {
                beforeProcessBundle(bundle);
                processBundle(bundle);
                afterProcessBundle(bundle);
            }
        }
    }

    /**
     * Called after document processing.
     *
     * @param document document to process
     */
    public void afterProcessDocument(Document document) {
    }

    /**
     *
     * @param bundle bundle to process
     * @return true if the bundle with given bundle number should be processed
     */
    protected boolean shouldProcessBundle(Bundle bundle) {
        return true;
    }

    /**
     *
     * @param tree tree to process
     * @return true if the tree should be processed
     */
    protected boolean shouldProcessTree(Root tree) {
        return true;
    }

    /**
     * Called before bundle processing.
     *
     * @param bundle bundle to process
     */
    public void beforeProcessBundle(Bundle bundle) {
    }

    /**
     * The main method for bundle processing.
     *
     * @param bundle bundle to process
     */
    public void processBundle(Bundle bundle) {
        for (Root tree : bundle.getTrees()) {
            if (shouldProcessTree(tree)) {
                processTree(tree);
            }
        }
    }

    /**
     * Called after bundle processing.
     *
     * @param bundle bundle to process
     */
    public void afterProcessBundle(Bundle bundle) {
    }

    /**
     * The main method for tree processing.
     *
     * @param tree tree to process
     */
    public void processTree(Root tree) {
        //wrap with ArrayList to prevent ConcurrentModificationException
        new ArrayList<>(tree.getDescendants()).forEach(this::processNode);
    }

    /**
     * The main method for node processing.
     *
     * @param node node to process
     */
    public void processNode(Node node) {
        throw new RuntimeException("Block doesn't implement or override any of process* methods.");
    }
}
