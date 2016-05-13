package cz.ufal.udapi.core.io;

import cz.ufal.udapi.core.Document;
import cz.ufal.udapi.core.Root;

import java.io.BufferedWriter;
import java.io.Writer;
import java.nio.file.Path;

/**
 * Used to write documents into different formats.
 *
 * @author Martin Vojtek
 */
public interface DocumentWriter {
    /**
     * Writes document to given writer.
     *
     * Writer is closed after processing.
     *
     * @param document document to write
     * @param writer writer to use
     * @throws UdapiIOException If any error occurs
     */
    void writeDocument(Document document, Writer writer) throws UdapiIOException;

    /**
     * Writes document to given output path.
     *
     * @param document document to write
     * @param outPath path where the document will be serialized
     * @throws UdapiIOException If any error occurs
     */
    void writeDocument(Document document, Path outPath) throws UdapiIOException;

    /**
     * Writes tree to given writer.
     *
     * Writer is not closed. It is responsibility of caller to close the writer.
     *
     * @param bufferedWriter writer to use
     * @param tree tree to write
     * @throws UdapiIOException If any error occurs
     */
    void processTree(BufferedWriter bufferedWriter, Root tree) throws UdapiIOException;
}
