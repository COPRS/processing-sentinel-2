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

    /**
     * Converts a xml file to a string
     *
     * @param fileName Name of the file
     * @return The converted file as a string
     */
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
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

            Transformer transformer = transformerFactory.newTransformer();

            StringWriter stringWriter = new StringWriter();
            StreamResult streamResult = new StreamResult(stringWriter);

            transformer.transform(domSource, streamResult);

            return stringWriter.getBuffer().toString();

        } catch (Exception e) {
            throw new XmlException("Unable to convert XML file '" + fileName + "' to String.", e);
        }
    }

    /**
     * Replaces a series of placeholders with their matching values in a string.
     *
     * @param input     String to work on
     * @param valuesMap Map of <placeholder, value> to replace
     */
    public static String replaceAll(String input, Map<String, String> valuesMap) {
        String output = input;
        for (Map.Entry<String, String> entry : valuesMap.entrySet()) {
            output = output.replaceAll(entry.getKey(), entry.getValue());
        }
        return output;
    }

    /**
     * Returns any line in the file that contains a specific sequence of characters.
     * It may be the first line, and the only one, but that's not guaranteed. At all.
     *
     * @param path         Path to the file
     * @param charSequence Sequence of characters to look fo
     * @return An optional of the line found, if any
     */
    public static Optional<String> grep(Path path, String charSequence) {

        try (Stream<String> lines = Files.lines(path)) {

            return lines.filter(line -> line.contains(charSequence)).findAny();

        } catch (IOException e) {
            throw new FileOperationException("Unable to read file: " + path, e);
        }
    }

    /**
     * Extracts the value enclosed in a given xml tag from a file.
     * Only works when tag is full opened and close, and on a single line.
     *
     * @param xmlPath Path to the xml file
     * @param tag     Tag to look for
     * @return The value enclosed in the tag.
     */
    public static String extractXmlTagValue(Path xmlPath, String tag) {

        final List<String> deleteRegexList = List.of(".*<" + tag + ">", "</" + tag + ">.*");

        return extractValue(xmlPath, tag, deleteRegexList);
    }

    /**
     * Extract a value from a file, given a filter to match a specific line, and a list of regular
     * expressions to remove parts of that line.
     * Only works for a single line.
     *
     * @param filePath        Path to the file
     * @param lineFilter      A string to filter the line to look for
     * @param deleteRegexList List of regex to identify parts of the line to be removed
     * @return The extracted value
     */
    public static String extractValue(Path filePath, String lineFilter, List<String> deleteRegexList) {

        String line = FileContentUtils.grep(filePath, lineFilter)
                .orElseThrow(() -> new AuxQueryException("Unable to find " + lineFilter + " in file " + filePath));

        for (String regex : deleteRegexList) {
            line = line.replaceAll(regex, "");
        }

        return line;
    }

    /**
     * Replaces a series of placeholders with their matching values in a file.
     *
     * @param filePath Path to the file
     * @param values   Map of <placeholder, value> to replace
     */
    public static void replaceInFile(Path filePath, Map<String, String> values) {
        try {
            final String input = Files.readString(filePath);
            final String output = FileContentUtils.replaceAll(input, values);
            Files.writeString(filePath, output);
        } catch (IOException e) {
            throw new FileOperationException("Unable to read file " + filePath, e);
        }
    }

    private FileContentUtils() {
    }

}
