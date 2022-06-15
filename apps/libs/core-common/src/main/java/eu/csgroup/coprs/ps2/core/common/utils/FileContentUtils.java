package eu.csgroup.coprs.ps2.core.common.utils;

import eu.csgroup.coprs.ps2.core.common.exception.AuxQueryException;
import eu.csgroup.coprs.ps2.core.common.exception.FileOperationException;
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
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public final class FileContentUtils {

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

    public static Optional<String> grep(Path path, String charSequence) {

        try (Stream<String> lines = Files.lines(path)) {

            return lines.filter(line -> line.contains(charSequence)).findAny();

        } catch (IOException e) {
            throw new FileOperationException("Unable to read file: " + path, e);
        }
    }

    public static String extractXmlTagValue(Path xmlPath, String tag) {

        final List<String> deleteRegexList = List.of(".*<" + tag + ">", "</" + tag + ">.*");

        return extractValue(xmlPath, tag, deleteRegexList);
    }

    public static String extractValue(Path filePath, String lineFilter, List<String> deleteRegexList) {

        String line = FileContentUtils.grep(filePath, lineFilter)
                .orElseThrow(() -> new AuxQueryException("Unable to find " + lineFilter + " in file " + filePath));

        for (String regex : deleteRegexList) {
            line = line.replaceAll(regex, "");
        }

        return line;
    }

    private FileContentUtils() {
    }

}
