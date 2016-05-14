package cz.ufal.udapi.block.write;

import cz.ufal.udapi.core.Block;
import cz.ufal.udapi.core.Root;
import cz.ufal.udapi.exception.UdapiException;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Serializes internal structure into user friendly text format.
 *
 * @author Martin Vojtek
 */
public class TextModeTrees extends Block {

    private static final String H = "\u2500"; // ─
    private static final String V = "\u2502"; // │
    private static final String LT = "\u2518"; // ┘
    private static final String LB = "\u2510"; // ┐
    private static final String RB = "\u250C"; // ┌
    private static final String RT = "\u2514"; // └
    private static final String RV = "\u251C"; // ├
    private static final String LV = "\u2524"; // ┤
    private static final String HB = "\u252C"; // ┬
    private static final String HT = "\u2534"; // ┴
    private static final String HV = "\u253C"; // ┼

    private final PrintStream ps;

    private static final Pattern replacePattern = Pattern.compile(".*[" + H + "|" + RT + "|" + RB + "|" + RV + "]$");

    private static final int Hi = 0;
    private static final int Vi = 1;
    private static final int LTi = 2;
    private static final int LBi = 3;
    private static final int RBi = 4;
    private static final int RTi = 5;
    private static final int RVi = 6;
    private static final int LVi = 7;
    private static final int HBi = 8;
    private static final int HTi = 9;
    private static final int HVi = 10;

    private String[] signs = {H, V, LT, LB, RB, RT, RV, LV, HB, HT, HV};

    private static final int indent = 1;

    public TextModeTrees(Map<String, String> params) {
        super(params);

        try {
            ps = new PrintStream(System.out, true, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UdapiException(e);
        }

        if (indent > 0) {
            int[] lineSigns = {Hi, LTi, LBi, LVi, HBi, HTi, HVi};
            StringBuilder before = new StringBuilder();

            for (int lineSign : lineSigns) {

                for (int i = 0; i < indent; i++) {
                    before.append(H);
                }
                signs[lineSign] = before.toString() + signs[lineSign];
                before.setLength(0);
            }

            int[] spaceSigns = {Vi, RBi, RTi, RVi};
            for (int spaceSign : spaceSigns) {
                for (int i = 0; i < indent; i++) {
                    before.append(" ");
                }
                signs[spaceSign] = before.toString() + signs[spaceSign];
                before.setLength(0);
            }
        }
        return;
    }

    @Override
    public void processTree(Root xtree) {

        Map<Integer, Node> tree = new HashMap<>();

        Node rootNode = new Node();
        rootNode.index = 0;
        rootNode.leftmost = 0;
        rootNode.rightmost = 0;
        rootNode.xNode = null;
        rootNode.parent = 0;
        rootNode.sons = new ArrayList<>();
        rootNode.printed = false;
        rootNode.depth = 0;
        tree.put(0, rootNode);

        Deque<Node> stack = new ArrayDeque<>();
        stack.push(rootNode);

        for (cz.ufal.udapi.core.Node descendant : xtree.getDescendants()) {
            int index = descendant.getOrd();
            Node newNode = new Node();
            newNode.index = index;
            newNode.leftmost = index;
            newNode.rightmost = index;
            newNode.xNode = descendant;
            newNode.parent = descendant.getParent().get().getOrd();
            newNode.sons = new ArrayList<>();
            newNode.printed = false;
            newNode.depth = 0;
            tree.put(index, newNode);
        }

        //fill sons
        for (int i = 1; i < tree.size(); i++) {
            Node son = tree.get(i);
            Node parent = tree.get(son.parent);
            parent.sons.add(son);
        }

        fillLeftRightMost(rootNode);

        //precompute lines for printing
        while (!stack.isEmpty()) {
            Node node = stack.pop();
            boolean top = false;
            boolean bottom = false;
            String append;

            int minSonOrSelf = node.index;
            int maxSonOrSelf = node.index;
            if (!node.sons.isEmpty()) {
                minSonOrSelf = Math.min(node.index, node.sons.get(0).index);
                maxSonOrSelf = Math.max(node.index, node.sons.get(node.sons.size() - 1).index);
            }
            int fillLen = tree.get(minSonOrSelf).depth;
            for (int i = minSonOrSelf; i < maxSonOrSelf + 1; i++) {
                int depth = tree.get(i).depth;
                if (depth > fillLen) {
                    fillLen = depth;
                }
            }

            for (int i = minSonOrSelf; i < maxSonOrSelf + 1; i++) {
                append = " ";

                if (replacePattern.matcher(tree.get(i).print.toString()).matches()) {
                    append = H;
                }

                int times = (fillLen - tree.get(i).print.length());
                for (int j = 0; j < times; j++) {
                    tree.get(i).print.append(append);
                }
            }
            node.printed = true;

            // printing from leftmost son       SYMETRIC
            append = " ";

            for (int i = minSonOrSelf; i < node.index; i++) {
                if (top) {
                    append = signs[Vi];
                }

                if (tree.get(i).parent == node.index) {
                    append = signs[RBi];
                    if (top) {
                        append = signs[RVi];
                    }
                    top = true;

                    if (tree.get(i).leftmost == tree.get(i).rightmost) {
                        append += signs[Hi] + nodeToString(tree.get(i).xNode);
                        tree.get(i).printed = true;
                    }

                    if (!tree.get(i).printed) {
                        stack.push(tree.get(i));
                    }
                }
                tree.get(i).print.append(append);
                tree.get(i).depth = tree.get(i).print.length();
            }


            // printing from rightmost son       SYMETRIC
            append = " ";

            for (int i = maxSonOrSelf; i > node.index; i--) {
                if (bottom) {
                    append = signs[Vi];
                }
                if (tree.get(i).parent == node.index) {
                    append = signs[RTi];
                    if (bottom) {
                        append = signs[RVi];
                    }
                    bottom = true;
                    if (tree.get(i).leftmost == tree.get(i).rightmost) {
                        append += signs[Hi] + nodeToString(tree.get(i).xNode);
                        tree.get(i).printed = true;
                    }
                    if (!tree.get(i).printed) {
                        stack.push(tree.get(i));
                    }
                }
                tree.get(i).print.append(append);
                tree.get(i).depth = tree.get(i).print.length();
            }

            // printing node
            node.print.append((bottom ? (top ? signs[LVi] : signs[LBi]) : (top ? signs[LTi] : signs[Hi])) + nodeToString(node.xNode));
            node.depth = node.print.length();

            // sorting stack to minimize crossing of edges
            Node[] stackNodes = stack.toArray(new Node[0]);

            Arrays.sort(stackNodes, (Node a, Node b) -> {
                if (a.index == b.index) {
                    return 0;
                }

                if ((a.index < b.index && a.rightmost < b.index) ||
                        a.index > b.index && a.leftmost < b.index) {
                    return -1;
                }

                return 0;
            });

            stack.clear();
            stack.addAll(Arrays.asList(stackNodes)); //TODO: convert to heap !!!
        }

        for (int i = 0; i < tree.size(); i++) {
            ps.println(tree.get(i).print.toString());
        }
    }

    private void fillLeftRightMost(Node rootNode) {
        if (!rootNode.sons.isEmpty()) {
            for (Node son : rootNode.sons) {
                fillLeftRightMost(son);
                rootNode.leftmost = Math.min(rootNode.leftmost, son.index);
                rootNode.rightmost = Math.max(rootNode.rightmost, son.index);
            }
        }
    }

    private String nodeToString(cz.ufal.udapi.core.Node node) {

        if (null == node) {
            return ""; //for roots
        }

        String str = node.getForm();
        if (null == str) {
            str = "";
        }
        if (node.getUpos() != null || node.getDeprel() != null) {
            str += "(";
            if (null != node.getUpos()) {
                str += node.getUpos();
            }
            if (null != node.getDeprel()) {
                str += "/" + node.getDeprel();
            }
            str += ")";
        }

        return str;
    }

    private static class Node {
        StringBuilder print = new StringBuilder();
        int index;
        int parent;
        List<Node> sons;
        boolean printed;
        int depth;
        int leftmost;
        int rightmost;
        cz.ufal.udapi.core.Node xNode;
    }
}
