package cz.ufal.udapi.block.read;

import cz.ufal.udapi.core.Document;
import cz.ufal.udapi.core.Root;
import cz.ufal.udapi.core.io.DocumentReader;
import cz.ufal.udapi.core.io.UdapiIOException;
import cz.ufal.udapi.core.io.impl.CoNLLUReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

/**
 * CoNLLU reader. Loads CoNLLU from the standard input into internal structure.
 *
 * @author Martin Vojtek
 */
public class CoNLLU extends cz.ufal.udapi.block.common.Reader {

    public CoNLLU(Map<String, String> params) {
        super(params);
    }

    private DocumentReader coNLLUReader;
    private BufferedReader reader;

    /**
     * Intialize readers.
     */
    @Override
    public void processStart() {
        reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        coNLLUReader = new CoNLLUReader(reader);
    }

    /**
     * Read one tree at a time.
     * @param document document to read into
     * @return Loaded tree.
     */
    @Override
    protected Optional<Root> readTree(Document document) {
        return coNLLUReader.readTree(reader, document);
    }

    /**
     * Close readers.
     */
    @Override
    public void processEnd() {
        try {
            if (null != reader) {
                reader.close();
            }
        } catch (IOException e) {
            throw new UdapiIOException("Failed to close reader.", e);
        }
    }
}
