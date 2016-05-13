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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    /**
     * Serializes document into given path.
     *
     * @param document document to serialize
     * @param path path where the document will be serialized
     */
    @Override
    public void writeDocument(Document document, Path path) {

        Set options = new HashSet();
        options.add(StandardOpenOption.CREATE);
        options.add(StandardOpenOption.WRITE);
        options.add(StandardOpenOption.TRUNCATE_EXISTING);

        FileChannel fileChannel;
        try {
            fileChannel = FileChannel.open(path, options);

            StringBuilder sb = new StringBuilder();


            for (Bundle bundle : document.getBundles()) {
                for (Root tree : bundle.getTrees()) {

                    List<Node> descendants = tree.getDescendants();

                    //do not write empty sentences
                    if (descendants.size() > 0) {

                        if (null != bundle.getId() && !"".equals(bundle.getId())) {
                            sb.append("# sent_id ");
                            sb.append(bundle.getId());
                            sb.append(tree.DEFAULT_ZONE.equals(tree.getZone()) ? "" : "/" + tree.getZone());
                            sb.append(NEW_LINE);
                        }

                        List<String> comments = tree.getComments();

                        for (String comment : comments) {
                            sb.append("#");
                            sb.append(comment);
                            sb.append(NEW_LINE);
                        }

                        //TODO: multiword

                        for (Node descendant : descendants) {
                            buildLine(sb, descendant);
                            sb.append(NEW_LINE);
                        }
                        sb.append(NEW_LINE);
                    }
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
            for (Bundle bundle : document.getBundles()) {
                for (Root tree : bundle.getTrees()) {
                    processTree(bufferedWriter, tree);
                }
            }
        } catch (IOException e) {
            throw new UdapiIOException(e);
        }
    }

    /**
     * Processes one sentence tree.
     *
     * @param bufferedWriter writer to write with
     * @param tree tree to serialize
     * @throws UdapiIOException If any IOException happens
     */
    public void processTree(BufferedWriter bufferedWriter, Root tree) throws UdapiIOException {
        List<Node> descendants = tree.getDescendants();
        Bundle bundle = tree.getBundle();
        //do not write empty sentences
        try {
            if (descendants.size() > 0) {

                if (null != bundle.getId() && !"".equals(bundle.getId())) {
                    String sentId = "# sent_id " + bundle.getId() + (tree.DEFAULT_ZONE.equals(tree.getZone()) ? "" : "/" + tree.getZone());
                    bufferedWriter.write(sentId, 0, sentId.length());
                    bufferedWriter.newLine();
                }

                List<String> comments = tree.getComments();

                for (String comment : comments) {
                    bufferedWriter.write("#", 0, 1);
                    bufferedWriter.write(comment, 0, comment.length());
                    bufferedWriter.newLine();
                }

                //TODO: multiword

                for (Node descendant : descendants) {
                    StringBuilder sb = new StringBuilder();
                    buildLine(sb, descendant);
                    String line = sb.toString();
                    bufferedWriter.write(line, 0, line.length());
                    bufferedWriter.newLine();
                }
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            throw new UdapiIOException("Failed to write tree " + tree.getId(), e);
        }
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
        sb.append(getString(node.getDeps()));
        sb.append(TAB);
        sb.append(getString(node.getMisc()));
    }

    private String getString(String field) {
        if (null == field) return UNDERSCORE;
        return field;
    }
}
