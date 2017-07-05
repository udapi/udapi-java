package cz.ufal.udapi.block.write;

import cz.ufal.udapi.core.Block;
import cz.ufal.udapi.core.Document;
import cz.ufal.udapi.core.Root;
import cz.ufal.udapi.core.io.UdapiIOException;
import cz.ufal.udapi.core.io.impl.CoNLLUWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * Serializes internal structure to CoNLLU file.
 *
 * @author Martin Vojtek
 */
public class CoNLLU extends Block {

    private BufferedWriter bufferedWriter;
    private CoNLLUWriter coNLLUWriter;

    @Override
    public void processStart() {
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
        coNLLUWriter = new CoNLLUWriter();
    }

    @Override
    public void processDocument(Document document) {
        coNLLUWriter.writeDocument(document, new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
    }

    @Override
    public void processTree(Root tree) {
        StringBuilder sb = new StringBuilder();
        coNLLUWriter.processTree(sb, tree);
        try {
            bufferedWriter.write(sb.toString());
        } catch (IOException e) {
            throw new UdapiIOException("Failed to write tree " + tree.getId(), e);
        }
    }

    @Override
    public void processEnd() {
        if (null != bufferedWriter) {
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                throw new UdapiIOException("Failed to close writer.", e);
            }
        }
    }
}
