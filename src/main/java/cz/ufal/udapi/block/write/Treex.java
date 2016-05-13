package cz.ufal.udapi.block.write;

import cz.ufal.udapi.core.*;
import cz.ufal.udapi.exception.UdapiException;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * Serializes internal structure into format used by TrEd tool.
 * <a href="https://ufal.mff.cuni.cz/tred/">
 *
 * @author Martin Vojtek
 */
public class Treex extends Block {

    private final PrintStream ps;

    private static final String SPACES_8 = "        ";
    private static final String SPACES_12 = "            ";

    public Treex(Map<String, String> params) {
        super(params);

        try {
            ps = new PrintStream(System.out, true, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UdapiException(e);
        }
    }

    @Override
    public void beforeProcessDocument(Document document) {
        ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<treex_document xmlns=\"http://ufal.mff.cuni.cz/pdt/pml/\">\n" +
                "  <head>\n" +
                "    <schema href=\"treex_schema.xml\" />\n" +
                "  </head>\n" +
                "  <meta/>\n" +
                "  <bundles>");
    }

    @Override
    public void afterProcessDocument(Document document) {
        ps.println("  </bundles>\n" +
                "</treex_document>");
    }

    @Override
    public void beforeProcessBundle(Bundle bundle, int bundleNo) {
        ps.println("    <LM id=\"s" + bundleNo + "\">\n" +
                "      <zones>");
    }

    @Override
    public void afterProcessBundle(Bundle bundle, int bundleNo) {
        ps.println("      </zones>\n    </LM>");
    }

    @Override
    public void processTree(Root tree, int bundleNo) {
        StringBuilder rootId = new StringBuilder("a-");
        rootId.append(bundleNo);
        String sentence = tree.getSentence();
        String language = "und"; //TODO: selector
        StringBuilder treeId = new StringBuilder("s");
        treeId.append(bundleNo).append("-").append(language);
        String in = SPACES_8;
        StringBuilder say = new StringBuilder(in);
        say.append("<zone language='").append(language).append("'>\n");

        if (null != sentence) {
            say.append(in).append("  <sentence>").append(sentence).append("</sentence>\n");
        }
        say.append(in).append("  <trees>\n").append(in).append("    <a_tree id='").append(treeId).append("'>");

        ps.println(say.toString());
        printSubTree(true, tree.getNode(), treeId, SPACES_12);

        StringBuilder postTree = new StringBuilder(in);
        postTree.append("    </a_tree>\n").append(in).append("  </trees>\n").append(in).append("</zone>");
        ps.println(postTree.toString());
    }

    public void printSubTree(boolean isRoot, Node node, StringBuilder treeId, String indent) {
        if (!isRoot) {
            StringBuilder toPrint = new StringBuilder(indent);
            toPrint.append("<LM id='").append(treeId).append("-n").append(node.getOrd()).append("'>");
            ps.println(toPrint.toString());
        }

        StringBuilder in = new StringBuilder(indent);
        in.append("  ");
        StringBuilder toPrint = new StringBuilder(in);
        toPrint.append("<ord>").append(node.getOrd()).append("</ord>\n");

        if (null != node.getForm()) {
            toPrint.append(in).append("<form>").append(node.getForm()).append("</form>\n");
        }
        if (null != node.getLemma()) {
            toPrint.append(in).append("<lemma>").append(node.getLemma()).append("</lemma>\n");
        }
        if (null != node.getUpos()) {
            toPrint.append(in).append("<tag>").append(node.getUpos()).append("</tag>\n");
        }
        if (null != node.getDeprel()) {
            toPrint.append(in).append("<deprel>").append(node.getDeprel()).append("</deprel>\n");
        }

        if (!isRoot) {
            toPrint.append(in).append("<conll><pos>");
            toPrint.append(null != node.getXpos() ? node.getXpos() : "");
            toPrint.append("</pos><feat>");
            toPrint.append(null != node.getFeats() ? node.getFeats() : "");
            toPrint.append("</feat></conll>\n");
        }

        ps.print(toPrint.toString());

        List<Node> children = node.getChildren();
        if (!children.isEmpty()) {
            ps.println(in + "<children>");
            for (Node child : children) {
                printSubTree(false, child, treeId, in.toString() + "  ");
            }
            ps.println(in + "</children>");
        }
        if (!isRoot) {
            ps.println(indent + "</LM>");
        }
    }
}
