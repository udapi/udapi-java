import cz.ufal.udapi.core.Block;
import cz.ufal.udapi.core.Node;

import java.util.Optional;

/**
 * Created by mvojtek on 5/13/16.
 */
public class RehangPrepositions extends Block {

    @Override
    public void processNode(Node node, int bundleNo) {

        if ("ADP".equals(node.getUpos())) {
            Optional<Node> origParent = node.getParent();
            origParent.ifPresent(orig -> {
                orig.getParent().ifPresent(origParentParent -> node.setParent(origParentParent));
                orig.setParent(node);
            });

        }

    }
}
