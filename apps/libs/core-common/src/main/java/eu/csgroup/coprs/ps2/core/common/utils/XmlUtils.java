package eu.csgroup.coprs.ps2.core.common.utils;

import eu.csgroup.coprs.ps2.core.common.exception.XmlException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.util.Map;

public final class XmlUtils {

    public static String xmlToString(String fileName) {

        try {

            InputSource inputSource = new InputSource(new FileInputStream(fileName));
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            documentBuilderFactory.setExpandEntityReferences(false);

            Document document = documentBuilderFactory.newDocumentBuilder().parse(inputSource);

            DOMSource domSource = new DOMSource(document);

            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

            Transformer transformer = transformerFactory.newTransformer();

            StringWriter stringWriter = new StringWriter();
            StreamResult streamResult = new StreamResult(stringWriter);

            transformer.transform(domSource, streamResult);

            return stringWriter.getBuffer().toString();

        } catch (Exception e) {
            throw new XmlException("Unable to convert XML file '" + fileName + "' to String.");
        }
    }

    public static String replaceAll(String input, Map<String, String> valuesMap) {
        String output = input;
        for (Map.Entry<String, String> entry : valuesMap.entrySet()) {
            output = output.replaceAll(entry.getKey(), entry.getValue());
        }
        return output;
    }

    private XmlUtils() {
    }

}
