package cz.ufal.udapi;

import cz.ufal.udapi.core.*;
import cz.ufal.udapi.core.io.impl.CoNLLUReader;
import cz.ufal.udapi.core.io.impl.CoNLLUWriter;
import org.junit.Test;

import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by mvojtek on 03/06/2017.
 */
public class NodeTest {

    @Test
    public void testEnhancedDependencies() throws Exception {
        String documentPath = "enh_deps.conllu";

        //test read
        CoNLLUReader reader = new CoNLLUReader(Paths.get(getClass().getResource(documentPath).toURI()));

        Document document = reader.readDocument();

        assertEquals(1, document.getDefaultBundle().getTrees().size());

        Root tree = document.getDefaultBundle().getTrees().get(0);

        assertEquals("no deprels at tree level", 0, tree.getDeps().getDeps().size());

        Node firstNode = tree.getDescendants().get(0);

        List<EnhancedDeps.Dep> deps = firstNode.getDeps().getDeps();
        assertEquals("first node has 2 enhanced dependencies", 2, deps.size());
        EnhancedDeps.Dep firstDep = deps.get(0);
        EnhancedDeps.Dep secondDep = deps.get(1);

        assertEquals("root", firstDep.getRel());
        assertEquals(true, firstDep.getHead().isRoot());
        assertEquals(tree, firstDep.getHead().getRoot());

        assertEquals("amod", secondDep.getRel());
        assertEquals(false, secondDep.getHead().isRoot());
        assertEquals(tree.getDescendants().get(1), secondDep.getHead().getNode());

        //test write
        CoNLLUWriter writer = new CoNLLUWriter();

        StringWriter sw = new StringWriter();
        writer.writeDocument(document, sw);

        String resultConllu = sw.toString();

        String originalContent = new String(Files.readAllBytes(Paths.get(getClass().getResource(documentPath).toURI())));
        assertEquals(originalContent, resultConllu);
    }

    @Test
    public void testMwt() throws Exception {
        String documentPath = "mwt_test.conllu";

        //test read
        CoNLLUReader reader = new CoNLLUReader(Paths.get(getClass().getResource(documentPath).toURI()));

        Document document = reader.readDocument();

        assertEquals(1, document.getDefaultBundle().getTrees().size());

        Root tree = document.getDefaultBundle().getTrees().get(0);

        assertEquals("no deprels at tree level", 0, tree.getDeps().getDeps().size());

        assertEquals(1, tree.getMultiwords().size());

        Mwt mwt = tree.getMultiwords().get(0);
        assertEquals("aby", mwt.getForm());
        assertEquals(2, mwt.getWords().size());
        assertEquals("aby", mwt.getWords().get(0).getForm());
        assertEquals("by", mwt.getWords().get(1).getForm());

        //test write
        CoNLLUWriter writer = new CoNLLUWriter();

        StringWriter sw = new StringWriter();
        writer.writeDocument(document, sw);

        String resultConllu = sw.toString();

        String originalContent = new String(Files.readAllBytes(Paths.get(getClass().getResource(documentPath).toURI())));
        assertEquals(originalContent, resultConllu);
    }
}
