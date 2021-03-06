/**
 * 
 */
package com.github.jsonldjava.sesame;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.rio.ParserConfig;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterTest;
import org.openrdf.rio.WriterConfig;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.openrdf.rio.helpers.JSONLDMode;
import org.openrdf.rio.helpers.JSONLDSettings;
import org.openrdf.rio.helpers.StatementCollector;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SesameJSONLDWriterTest extends RDFWriterTest {

    public SesameJSONLDWriterTest() {
        super(new SesameJSONLDWriterFactory(), new SesameJSONLDParserFactory());
    }

    /*
     * TODO: Unignore tests when updating to Sesame-2.7.12
     */
    protected void setupWriterConfig(WriterConfig config) {
        config.set(JSONLDSettings.JSONLD_MODE, JSONLDMode.COMPACT);
    }

    /*
     * TODO: Unignore tests when updating to Sesame-2.7.12
     */
    protected void setupParserConfig(ParserConfig config) {
        config.set(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES, true);
        config.set(BasicParserSettings.FAIL_ON_UNKNOWN_LANGUAGES, true);
    }
    
    @Test
    @Override
    @Ignore("Default RDFWriter.getWriterConfig doesn't use JSONLDMode.COMPACT, so namespaces are not preserved")
    public void testPerformance() throws Exception {
    }

    @Test
    @Override
    @Ignore("Default RDFWriter.getWriterConfig doesn't use JSONLDMode.COMPACT, so namespaces are not preserved")
    public void testRoundTrip() throws Exception {
    }

    @Test
    @Override
    @Ignore("Default RDFWriter.getWriterConfig doesn't use JSONLDMode.COMPACT, so namespaces are not preserved")
    public void testRoundTripPreserveBNodeIds() throws Exception {
    }

    @Test
    public void testRoundTripNamespaces() throws Exception {
        String exNs = "http://example.org/";
        URI uri1 = vf.createURI(exNs, "uri1");
        URI uri2 = vf.createURI(exNs, "uri2");
        Literal plainLit = vf.createLiteral("plain", XMLSchema.STRING);

        Statement st1 = vf.createStatement(uri1, uri2, plainLit);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RDFWriter rdfWriter = rdfWriterFactory.getWriter(out);
        rdfWriter.getWriterConfig().set(JSONLDSettings.JSONLD_MODE, JSONLDMode.COMPACT);
        rdfWriter.handleNamespace("ex", exNs);
        rdfWriter.startRDF();
        rdfWriter.handleStatement(st1);
        rdfWriter.endRDF();

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        RDFParser rdfParser = rdfParserFactory.getParser();
        ParserConfig config = new ParserConfig();
        config.set(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES, true);
        config.set(BasicParserSettings.FAIL_ON_UNKNOWN_LANGUAGES, true);
        rdfParser.setParserConfig(config);
        rdfParser.setValueFactory(vf);
        Model model = new LinkedHashModel();
        rdfParser.setRDFHandler(new StatementCollector(model));

        rdfParser.parse(in, "foo:bar");

        assertEquals("Unexpected number of statements, found " + model.size(), 1, model.size());

        assertTrue("missing namespaced statement", model.contains(st1));

        if (rdfParser.getRDFFormat().supportsNamespaces()) {
            assertTrue("Expected at least one namespace, found " + model.getNamespaces().size(),
                    model.getNamespaces().size() >= 1);
            assertEquals(exNs, model.getNamespace("ex").getName());
        }
    }
}
