package cz.ufal.udapi.block.write;

import cz.ufal.udapi.core.Block;
import cz.ufal.udapi.core.Root;
import cz.ufal.udapi.exception.UdapiException;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serializes internal structure into sentances.
 *
 * @author Martin Vojtek
 */
public class Sentences extends Block {

    public static final String IF_MISSING = "if_missing";

    public static final String DETOKENIZE = "detokenize";

    public static final String EMPTY = "empty";

    public static final String FATAL = "fatal";

    private final PrintStream ps;

    public Sentences(Map<String, String> params) {
        super(params);
        if (!params.containsKey(IF_MISSING)) {
            params.put(IF_MISSING, DETOKENIZE);
        }

        try {
            ps = new PrintStream(System.out, true, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UdapiException(e);
        }
    }

    @Override
    public void processTree(Root tree) {

        String sentence = tree.getSentence();
        if (null == sentence) {
            if (getParams().containsKey(IF_MISSING)) {
                String ifMissing = getParams().get(IF_MISSING);
                if (DETOKENIZE.equals(ifMissing)) {
                    // TODO SpaceAfter=No
                    sentence = tree.getDescendants().stream().map(node -> node.getForm()).collect(Collectors.joining(" "));
                } else if (EMPTY.equals(ifMissing)) {
                    sentence = "";
                } else {
                    if (FATAL.equals(ifMissing)) {
                        throw new UdapiException("Sentence " + tree.getAddress() + " is undefined");
                    }
                }
            } else {
                System.err.println("Sentence " + tree.getAddress() + " is undefined");
            }
        }

        ps.println(sentence);
    }

}
