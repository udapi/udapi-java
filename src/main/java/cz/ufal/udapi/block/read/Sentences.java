package cz.ufal.udapi.block.read;

import cz.ufal.udapi.core.Block;
import cz.ufal.udapi.core.Bundle;
import cz.ufal.udapi.core.Document;
import cz.ufal.udapi.core.io.UdapiIOException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Reads sentences from standard input into default bundle.
 *
 * @author Martin Vojtek
 */
public class Sentences extends Block {

    /**
     * Loads sentences into given document.
     *
     * @param document document to load into
     */
    @Override
    public void processDocument(Document document) {

        boolean inAvailable;

        try {
            inAvailable = System.in.available() > 0;
        } catch (IOException e) {
            throw new UdapiIOException("Error when reading input stream.", e);
        }

        if (inAvailable) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
                //default bundle
                Bundle bundle = document.getDefaultBundle();
                String currLine;

                while ((currLine = bufferedReader.readLine()) != null) {
                    bundle.addTree().setSentence(currLine);
                }
            } catch (IOException e) {
                throw new UdapiIOException(e);
            }
        }

    }
}
