package cz.ufal.udapi.core.io.impl;

import cz.ufal.udapi.core.*;
import cz.ufal.udapi.core.io.DocumentWriter;
import cz.ufal.udapi.core.io.UdapiIOException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Writer for CoNLLU format.
 *
 * Serializes Document into CoNLLU format.
 *
 * @author Martin Vojtek
 */
public class CoNLLUWriter implements DocumentWriter {

    private static final String TAB = "\t";
    private static final String UNDERSCORE = "_";
    private static final String NEW_LINE = "\n";
    private static final Charset utf8Charset = StandardCharsets.UTF_8;

    private static final int BUFFER = 256 * 1024;
    private boolean printSentId = true;

    public boolean isPrintSentId() {
        return printSentId;
    }

    public void setPrintSentId(boolean printSentId) {
        this.printSentId = printSentId;
    }

    /**
     * Serializes document into given path.
     *
     * @param document document to serialize
     * @param path path where the document will be serialized
     */
    @Override
    public void writeDocument(Document document, Path path) {

        Set<StandardOpenOption> options = new HashSet<>();
        options.add(StandardOpenOption.CREATE);
        options.add(StandardOpenOption.WRITE);
        options.add(StandardOpenOption.TRUNCATE_EXISTING);

        FileChannel fileChannel;
        try {
            fileChannel = FileChannel.open(path, options);

            StringBuilder sb = new StringBuilder();


            for (Bundle bundle : document.getBundles()) {

                for (Root tree : bundle.getTrees()) {
                    processTree(sb, tree);
                }
                if (sb.length() > BUFFER) {
                    fileChannel.write(ByteBuffer.wrap(sb.toString().getBytes(utf8Charset)));
                    sb.setLength(0);
                }
            }

            fileChannel.write(ByteBuffer.wrap(sb.toString().getBytes(utf8Charset)));
            fileChannel.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Serializes document with given writer.
     *
     * @param document document to serialize
     * @param writer writer to write with
     */
    public void writeDocument(Document document, Writer writer) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            StringBuilder sb = new StringBuilder();
            for (Bundle bundle : document.getBundles()) {
                for (Root tree : bundle.getTrees()) {
                    processTree(sb, tree);
                }
                if (sb.length() > BUFFER) {
                    String content = sb.toString();
                    bufferedWriter.write(content, 0, content.length());
                    sb.setLength(0);
                }
            }
            String content = sb.toString();
            bufferedWriter.write(content, 0, content.length());
            sb.setLength(0);
        } catch (IOException e) {
            throw new UdapiIOException(e);
        }
    }

    public void processTree(StringBuilder sb, Root tree) throws UdapiIOException {
        List<Node> descendants = tree.getDescendants();
        Bundle bundle = tree.getBundle();

        //do not write empty sentences
        if (descendants.size() > 0) {

            if (isPrintSentId()) {

                if (null != tree.getNewDocId()) {
                    sb.append("# newdoc id = "+tree.getNewDocId());
                    sb.append(NEW_LINE);
                }
                if (null != tree.getNewParId()) {
                    sb.append("# newpar id = "+tree.getNewParId());
                    sb.append(NEW_LINE);
                }

                if (null != tree.getSentId()) {
                    String sentIdString = "# sent_id = " + tree.getSentId();
                    sb.append(sentIdString);
                    sb.append(NEW_LINE);
                } else if (null != bundle.getId() && !"".equals(bundle.getId())) {
                    sb.append("# sent_id = ");
                    sb.append(tree.getAddress());
                    sb.append(NEW_LINE);
                }
            }

            List<String> comments = tree.getComments();

            for (String comment : comments) {
                sb.append("#");
                sb.append(comment);
                sb.append(NEW_LINE);
            }

            int lastMwtId = 0;
            int nextEmptyNode = -1;
            int emptyNodesIndex = 0;
            List<EmptyNode> emptyNodes = tree.getEmptyNodes();
            if (!emptyNodes.isEmpty()) {
                nextEmptyNode = emptyNodes.get(0).getEmptyNodePrefixId();
            }

            for (Node descendant : descendants) {

                //multiword
                Optional<MultiwordToken> mwt = descendant.getMwt();
                if (mwt.isPresent() && descendant.getOrd()  >lastMwtId) {
                    List<Node> words = mwt.get().getWords();
                    lastMwtId = words.get(words.size()-1).getOrd();
                    sb.append(mwt.get().toStringFormat());
                    sb.append(NEW_LINE);
                }

                buildLine(sb, descendant);
                sb.append(NEW_LINE);

                //empty nodes
                while (descendant.getOrd() == nextEmptyNode && emptyNodesIndex < emptyNodes.size()) {

                    EmptyNode emptyNode = emptyNodes.get(emptyNodesIndex);
                    emptyNodesIndex++;
                    if (emptyNodesIndex < emptyNodes.size()) {
                        nextEmptyNode = emptyNodes.get(emptyNodesIndex).getEmptyNodePrefixId();
                    }

                    buildEmptyNodeLine(sb, emptyNode);
                    sb.append(NEW_LINE);
                }
            }
            sb.append(NEW_LINE);
        }
    }

    private void buildEmptyNodeLine(StringBuilder sb, EmptyNode node) {
        sb.append(node.getEmptyNodeId());
        sb.append(TAB);
        sb.append(getString(node.getForm()));
        sb.append(TAB);
        sb.append(getString(node.getLemma()));
        sb.append(TAB);
        sb.append(getString(node.getUpos()));
        sb.append(TAB);
        sb.append(getString(node.getXpos()));
        sb.append(TAB);
        sb.append(getString(node.getFeats()));
        sb.append(TAB);
        sb.append(UNDERSCORE);
        sb.append(TAB);
        sb.append(UNDERSCORE);
        sb.append(TAB);
        sb.append(getString(node.getDeps().toStringFormat()));
        sb.append(TAB);
        sb.append(getString(node.getMisc()));
    }

    private void buildLine(StringBuilder sb, Node node) {
        sb.append(node.getOrd());
        sb.append(TAB);
        sb.append(getString(node.getForm()));
        sb.append(TAB);
        sb.append(getString(node.getLemma()));
        sb.append(TAB);
        sb.append(getString(node.getUpos()));
        sb.append(TAB);
        sb.append(getString(node.getXpos()));
        sb.append(TAB);
        sb.append(getString(node.getFeats()));
        sb.append(TAB);
        sb.append(node.getParent().get().getOrd());
        sb.append(TAB);
        sb.append(getString(node.getDeprel()));
        sb.append(TAB);
        sb.append(getString(node.getDeps().toStringFormat()));
        sb.append(TAB);
        sb.append(getString(node.getMisc()));
    }

    private String getString(String field) {
        if (null == field) return UNDERSCORE;
        return field;
    }
}
