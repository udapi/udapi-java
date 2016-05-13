package cz.ufal.udapi.block.tutorial;

import cz.ufal.udapi.core.Block;
import cz.ufal.udapi.core.Node;

/**
 * This classes changes prepositions into postpositions.
 *
 * Example usage:
 * <code>
 *     for a in *&#47;*dev*.conllu; do
 *         printf '%50s ' $a;
 *         cat $a | udapi.pl Read::CoNLLU Tutorial::ToPositions;
 *     done | tee ~/results.txt
 *
 *     # https://lindat.mff.cuni.cz/services/pmltq/#!/treebank/ud_cs/help
 *     a-node $A:= [
 *       child a-node [
 *         conll/cpos = 'ADP',
 *         ord > $A.ord,
 *       ]
 *     ]
 * </code>
 *
 * @author Martin Vojtek
 */
public class ToPositions extends Block {

    private static final String ADP = "ADP";

    @Override
    public void processNode(Node node, int bundleNo) {
        if (node.getParent().isPresent()) {
            if (ADP.equals(node.getUpos()) && node.precedes(node.getParent().get())) {
                node.shiftAfterSubtree(node.getParent().get());
            }
        }
    }

}
