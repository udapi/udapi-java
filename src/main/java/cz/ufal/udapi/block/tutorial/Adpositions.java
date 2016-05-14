package cz.ufal.udapi.block.tutorial;

import cz.ufal.udapi.core.Block;
import cz.ufal.udapi.core.Node;

/**
 * This class is useful for tutorial purposes.
 * After small changes it should count prepositions and postpositions.
 *
 * Example usage:
 * <code>
 *     for a in *&#47;*dev*.conllu; do
 *         printf '%50s ' $a;
 *         cat $a | udapi.groovy Read::CoNLLU Tutorial::Adpositions;
 *     done | tee ~/results.txt
 *
 *     # https://lindat.mff.cuni.cz/services/pmltq/#!/treebank/ud_cs/help
 *     a-node $A:= [
 *       child a-node [
 *         conll/cpos = 'ADP',
 *         ord &gt; $A.ord,
 *       ]
 *     ]
 * </code>
 *
 * @author Martin Vojtek
 */
public class Adpositions extends Block {

    private int prepositions;
    private int postpositions;

    private static final String ADP = "ADP";

    @Override
    public void processNode(Node node) {
        // TODO: Your task: distinguish prepositions and postpositions
        if (ADP.equals(node.getUpos())) {
            prepositions++;
        }
    }

    @Override
    public void processEnd() {
        int all = prepositions + postpositions;
        System.out.printf("prepositions %5.1f%%, postpositions %5.1f%%\n",
                prepositions * 100 / (float) all, postpositions * 100 / (float) all);
    }
}
