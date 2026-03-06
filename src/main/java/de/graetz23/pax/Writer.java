/**
 * @brief pax
 * @details A Java written generator for plain old XML (POX) data domains
 * @copyright Copyright (c) 2017-2026 Christian (graetz23@gmail.com)
 * @author Christian (graetz23@gmail.com)
 * @file Writer.java
 *
 * Singleton utility for persisting a {@link IPax} node tree to an XML
 * file on disk. The output is always UTF-8 encoded and prefixed with the
 * XML 1.1 declaration {@code <?xml version="1.1" encoding="UTF-8"?>}.
 *
 * <p>Typical usage:</p>
 * <pre>{@code
 *   Writer.Instance.XML(root, "output.xml");
 * }</pre>
 *
 * <p>The XML content is produced by calling {@link IPax#XML()} on the
 * root node, which delegates to {@link XmlGenerator#generate(IPax)} for
 * indented, line-separated output.</p>
 *
 * @see IPax#XML()
 * @see XmlGenerator
 */

package de.graetz23.pax;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

public class Writer {

    /**
     * The singleton instance. Use this field to call write methods, e.g.
     * {@code Writer.Instance.XML(root, "output.xml")}.
     */
    public static Writer Instance = new Writer();

    /**
     * Private constructor enforcing the singleton pattern.
     */
    private Writer() {
    } // constructor

    /**
     * Writes the given {@link IPax} tree to an XML file whose name is
     * derived from the root node's tag (with {@code .xml} appended if not
     * already present). Delegates to {@link #XML(IPax, String)}.
     *
     * @param root the root node of the tree to serialize; ignored if
     *             {@code null}
     * @return {@code true} if the file was written successfully;
     *         {@code false} if root was {@code null} or an I/O error
     *         occurred
     */
    public boolean XML(IPax root) {
        boolean wasWritten = false;
        if (root != null) {
            String tag = "__file_noname";
            if (root.hasTag()) {
                tag = root.Tag();
            } // if
            wasWritten = XML(root, tag);
        } // if
        return wasWritten;
    } // method

    /**
     * Writes the given {@link IPax} tree to an XML file with the specified
     * name. If the name does not end with {@code .xml} (case-insensitive),
     * the extension is appended automatically. The file is written in
     * UTF-8 encoding and prefixed with the XML 1.1 header.
     *
     * @param root     the root node of the tree to serialize; ignored if
     *                 {@code null}
     * @param fileName the target file name or path; {@code .xml} is
     *                 appended if not already present
     * @return {@code true} if the file was written successfully;
     *         {@code false} if root was {@code null} or an I/O error
     *         occurred (stack trace is printed)
     */
    public boolean XML(IPax root, String fileName) {
        boolean wasWritten = false;
        if (root != null) {
            if (!fileName.toLowerCase().endsWith(".xml")) {
                fileName += ".xml";
            } // if

            try {
                OutputStream stream = new FileOutputStream(fileName); // streaming the content
                CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder(); // get sure to write UTF-8
                OutputStreamWriter file = new OutputStreamWriter(stream, encoder);
                String xml = root.XML();
                String header = "<?xml version=\"1.1\" encoding=\"UTF-8\"?>" + Statics.LineSeparator;
                file.write(header);
                file.write(xml);
                file.close();
                wasWritten = true;
            } catch (IOException e) {
                e.printStackTrace();
            } // try
        } // if
        return wasWritten;
    } // method

} // class
