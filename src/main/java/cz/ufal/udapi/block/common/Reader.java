package cz.ufal.udapi.block.common;

import cz.ufal.udapi.core.Block;
import cz.ufal.udapi.core.Bundle;
import cz.ufal.udapi.core.Document;
import cz.ufal.udapi.core.Root;
import cz.ufal.udapi.exception.UdapiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Reader is base class for more specific readers, e.g. CoNLLU.
 *
 * This class is zone and bundle aware.
 *
 * @author Martin Vojtek
 */
public abstract class Reader extends Block {

    public static final String KEEP_ZONE = "keep";

    public static final String PARAM_ZONE = "zone";

    public static final String PARAM_BUNDLES_PER_DOC = "bundlesPerDoc";

    /**
     * What should be the zone of the new trees.
     . Default="keep" means keep the zone saved in the input file (or use "und" if no zone is specified).
     */
    private String zone = KEEP_ZONE;

    /**
     * Create a new document after each N bundles read. Default=0 means unlimited.
     */
    private int bundlesPerDoc = 0;

    /**
     * Helper class during loading.
     */
    private Optional<Root> buffer = Optional.empty();

    /**
     * Reads tree and loads it into the document.
     *
     * @param document document to read into
     * @return Root of read tree
     */
    protected abstract Optional<Root> readTree(Document document);

    /**
     * @return true if the reader supports multiple zones
     */
    boolean isMultizoneReader() {
        return true;
    }

    public Reader(Map<String, String> params) {
        super(params);

        if (params.containsKey(PARAM_ZONE)) {
            zone = params.get(PARAM_ZONE);
        }
        if (params.containsKey(PARAM_BUNDLES_PER_DOC)) {
            String bundlesPerDoc = params.get(PARAM_BUNDLES_PER_DOC);
            try {
                this.bundlesPerDoc = Integer.parseInt(params.get(bundlesPerDoc));
            } catch (Exception e) {
                throw new UdapiException("Invalid format of " + PARAM_BUNDLES_PER_DOC + " parameter: " + bundlesPerDoc);
            }
        }
    }


    /**
     * Processes document. This method is called by Run class.
     */
    @Override
    public void processDocument(Document document) {

        List<Bundle> originalBundles = new ArrayList<>(document.getBundles());

        int bundleNo = 0;
        Bundle bundle = null;
        int bpd = bundlesPerDoc;
        String lastBundleId = "";

        // There may be a tree left in the buffer when reading the last doc.
        if (buffer.isPresent()) {

            if (!originalBundles.isEmpty()) {
                bundle = originalBundles.remove(0);
            } else {
                bundle = document.createBundle();
                bundleNo++;
            }
            bundle.addTree(buffer.get());
            buffer = Optional.empty();
        }
        int sentenceId = 1;
        Optional<Root> root = readTree(document);
        while (root.isPresent()) {
            Root tree = root.get();


            boolean addToTheLastBundle = false;

            String treeId = tree.getId();
            if (null != treeId) {
                String bundleId;
                int slashIndex = treeId.indexOf("/");

                if (-1 != slashIndex) {
                    bundleId = treeId.substring(0, slashIndex);
                    if (slashIndex < treeId.length() - 1) {
                        tree.setZone(treeId.substring(slashIndex + 1));
                        tree.validateZone();
                    }
                } else {
                    bundleId = treeId;
                }

                if (null != lastBundleId && lastBundleId.equals(bundleId)) {
                    addToTheLastBundle = true;
                }
                lastBundleId = bundleId;
                tree.setId(null);
            }

            if (!KEEP_ZONE.equals(zone)) {
                tree.setZone(zone);
            }

            if (null != bundle || !addToTheLastBundle) {
                if (bpd != 0 && bpd == bundleNo) {
                    buffer = Optional.of(tree);
                    if (!originalBundles.isEmpty()) {
                        System.err.println("bundlesPerDoc=" + bpd + " but the doc already contained "
                                + originalBundles.size() + " bundles");
                    }
                    return;
                }

                if (!originalBundles.isEmpty()) {
                    bundle = originalBundles.remove(0);
                    if (null != lastBundleId && !lastBundleId.equals(bundle.getId())) {
                        System.err.println("Mismach in bundle IDs: " + bundle.getId() + " vs " + lastBundleId + ". Keeping the former one.");
                    }
                } else {
                    bundle = document.createBundle();
                    if (null == lastBundleId || "".equals(lastBundleId)) {
                        bundle.setId(String.valueOf(sentenceId));
                    } else {
                        bundle.setId(lastBundleId);
                    }
                }
                bundleNo++;
            }

            bundle.addTree(tree);

            // If bundlesPerDoc is set and we have read the specified number of bundles,
            // we should end the current document and return.
            // However, if the reader supports reading multiple zone, we can never know
            // if the current bundle has ended or there will be another tree for this bundle.
            // So in case of multizone readers we need to read one extra tree
            // and store it in the buffer (and include it into the next document).
            if (bpd != 0 && bpd == bundleNo && !isMultizoneReader()) {
                return;
            }

            root = readTree(document);
            sentenceId++;
        }

    }

}
