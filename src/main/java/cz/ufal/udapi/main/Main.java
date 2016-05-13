package cz.ufal.udapi.main;

import cz.ufal.udapi.core.*;
import cz.ufal.udapi.core.impl.DefaultNode;
import cz.ufal.udapi.core.io.DocumentReader;
import cz.ufal.udapi.core.io.DocumentWriter;
import cz.ufal.udapi.core.io.UdapiIOException;
import cz.ufal.udapi.core.io.impl.CoNLLUReader;
import cz.ufal.udapi.core.io.impl.CoNLLUWriter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.*;

/**
 * The purpose of this class is to test correct behavior of Node operations
 * and to provide standard benchmark scenario.
 *
 * @author Martin Vojtek
 */
public class Main {
    private static long seed = 42;
    private static long maxseed = (long)Math.pow(2, 32);
    private static int myrand(long modulo) {
        seed = (1103515245L * seed + 12345L) % maxseed;
        return (int)(seed % modulo);
    }

    public static void main(String[] args) {
        boolean debug = false;

        String inCoNLL;
        String outCoNLL;
        int iterations = 1;
        int startIndex = 0;
        if ("-d".equals(args[startIndex])) {
            debug = true;
            startIndex++;
        }
        if ("-n".equals(args[startIndex])) {
            iterations = Integer.parseInt(args[startIndex+1]);
            startIndex += 2;
        }
        inCoNLL = args[startIndex];
        outCoNLL = args[startIndex+1];

        System.out.println("init");

        for (int i=1; i <= iterations; i++) {
            test(inCoNLL, outCoNLL, debug);
        }
        System.out.println("end");
    }

    public static void test(String inCoNLL, String outCoNLL, boolean debug) {

        FileReader fileReader;
        try {
            fileReader = new FileReader(Paths.get(inCoNLL).toFile());
        } catch (FileNotFoundException e) {
            throw new UdapiIOException("Provided CoNLL file '"+inCoNLL+"' not found.");
        }

        DocumentReader coNLLUReader = new CoNLLUReader(fileReader);
        Document document = coNLLUReader.readDocument();
        System.out.println("load");

        if (debug) {
            writeDoc("java-load.conllu", document);
        }

        for (Bundle bundle : document.getBundles()) {
            for (Root tree : bundle.getTrees()) {
                for (Node node : tree.getDescendants()) {
                    //noop
                }
            }
        }
        System.out.println("iter");

        for (Bundle bundle : document.getBundles()) {
            for (Root tree : bundle.getTrees()) {
                for (Node node : ((DefaultNode)tree.getNode()).getDescendantsF()) {
                    //noop
                }
            }
        }
        System.out.println("iterF");

        for (Bundle bundle : document.getBundles()) {
            for (Root tree : bundle.getTrees()) {
                for (Node child : tree.getNode().getChildren()) {
                    for (Node node : child.getDescendants()) {
                        //noop
                    }
                }
            }
        }
        System.out.println("iterS");

        for (Bundle bundle : document.getBundles()) {
            for (Root tree : bundle.getTrees()) {
                Optional<Node> node = Optional.of(tree.getNode());
                while (node.isPresent()) {
                    node = node.get().getNextNode();
                    //noop
                }
            }
        }
        System.out.println("iterN");

        for (Bundle bundle : document.getBundles()) {
            for (Root tree : bundle.getTrees()) {
                for (Node node : tree.getDescendants()) {
                    String form_lemma_tag = node.getForm() + node.getLemma();
                }
            }
        }
        System.out.println("read");

        for (Bundle bundle : document.getBundles()) {
            for (Root tree : bundle.getTrees()) {
                for (Node node : tree.getDescendants()) {
                    node.setDeprel("dep");
                }
            }
        }
        System.out.println("write");
        if (debug) {
            writeDoc("java-write.conllu", document);
        }

        for (Bundle bundle : document.getBundles()) {
            for (Root tree : bundle.getTrees()) {
                List<Node> descendants = tree.getDescendants();
                for (Node node : descendants) {
                    int rand_index = myrand(descendants.size());
                    node.setParent(descendants.get(rand_index), true);
                }
            }
        }
        System.out.println("rehang");
        if (debug) {
            writeDoc("java-rehang.conllu", document);
        }

        for (Bundle bundle : document.getBundles()) {
            for (Root tree : bundle.getTrees()) {
                for (Node node : new ArrayList<Node>(tree.getDescendants())) {
                    if (myrand(10) == 0) {
                        node.remove();
                    }
                }
            }
        }
        System.out.println("remove");
        if (debug) {
            writeDoc("java-remove.conllu", document);
        }

        for (Bundle bundle : document.getBundles()) {
            for (Root tree : bundle.getTrees()) {
                for (Node node : new ArrayList<Node>(tree.getDescendants())) {
                    if (myrand(10) == 0) {
                        Node nodeChild = node.createChild();
                        nodeChild.setLemma("x");
                        nodeChild.setForm("x");
                        nodeChild.shiftAfterSubtree(node);
                    }
                }
            }
        }
        System.out.println("add");
        if (debug) {
            writeDoc("java-add.conllu", document);
        }

        for (Bundle bundle : document.getBundles()) {
            for (Root tree : bundle.getTrees()) {
                List<Node> nodes = new ArrayList(tree.getDescendants());
                for (Node node : nodes) {
                    int rand_index = myrand(nodes.size());
                    if (myrand(10) == 0) {
                        node.shiftAfterNode(nodes.get(rand_index), EnumSet.of(Node.ShiftArg.SKIP_IF_DESCENDANT));
                    } else if (myrand(10) == 0) {
                        node.shiftBeforeSubtree(nodes.get(rand_index), EnumSet.of(Node.ShiftArg.WITHOUT_CHILDREN));
                    }
                }
            }
        }
        System.out.println("reorder");
        if (debug) {
            writeDoc("java-reorder.conllu", document);
        }

        DocumentWriter coNLLUWriter = new CoNLLUWriter();
        coNLLUWriter.writeDocument(document, Paths.get(outCoNLL));
        System.out.println("save");
    }

    private static void writeDoc(String fileName, Document document) {
        DocumentWriter coNLLUWriter = new CoNLLUWriter();
        coNLLUWriter.writeDocument(document, Paths.get(fileName));
    }
}
