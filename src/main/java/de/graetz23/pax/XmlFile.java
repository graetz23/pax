/**
 * @brief pax
 * @details An object-tree combined with a tolerant reader to parse any XML
 * @copyright Copyright (c) 2017-2026 Christian (graetz23@gmail.com)
 * @author Christian (graetz23@gmail.com)
 * @file XmlFile.java
 *
 * Wrapper class that generates the XML declaration header and encapsulates
 * a single root {@link IPax} node for file I/O operations. Used by
 * {@link XmlWriter} to produce properly formatted XML files with UTF-8
 * encoding and the standard XML 1.0 declaration.
 *
 * @see XmlWriter
 * @see XmlGenerator
 */

package de.graetz23.pax;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class XmlFile {

  private Charset _charset = null;
  private IPax _root = null; // the XML root node

  /**
   * Constructs a new XmlFile wrapper with the given root node using
   * UTF-8 encoding.
   *
   * @param root the root {@link IPax} node of the XML document
   */
  public XmlFile(IPax root) {
    _root = root;
    _charset = StandardCharsets.UTF_8;
  } // constructor

  /**
   * Constructs a new XmlFile wrapper with the given root node and
   * specified charset encoding.
   *
   * @param root    the root {@link IPax} node of the XML document
   * @param charset the charset to use for encoding the output
   */
  public XmlFile(IPax root, Charset charset) {
    _root = root;
    _charset = charset;
  } // constructor

  /**
   * Generates the XML declaration header with the specified charset.
   *
   * @param charset the charset to include in the header
   * @return the XML declaration string, e.g. {@code <?xml version="1.0" encoding="UTF-8"?>}
   */
  private String getHeader(Charset charset) {
    String charset_ = charset.toString();
    return "<?xml version=\"1.0\" encoding=\"" + charset_ + "\"?>";
  } // method

  /**
   * Returns the charset used for encoding the XML file.
   *
   * @return the charset, or {@code null} if not set
   */
  public Charset getCharset() {
    return _charset;
  } // method

  /**
   * Returns the complete XML document as a byte array, including
   * the XML declaration header.
   *
   * @return the XML bytes, or {@code null} if root is {@code null}
   */
  public byte[] getBytes() {
    byte[] bytes = null;
    if (_root != null) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(getHeader(_charset) + Statics.LineSeparator);
      stringBuilder.append(_root.XML());
      bytes = stringBuilder.toString().getBytes(_charset);
    } // if
    return bytes;
  } // method

  /**
   * Returns the complete XML document as a string, including
   * the XML header declaration.
   *
   * @return the XML string, or {@code null} if root is {@code null}
   */
  public String getXml() {
    String xml = null;
    if (_root != null) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(getHeader(_charset) + Statics.LineSeparator);
      stringBuilder.append(_root.XML());
      xml = stringBuilder.toString();
    } // if
    return xml;
  } // method

  /**
   * Returns the complete XML document as a string, without breaklines,
   * including the XML header declaration .
   *
   * @return the XML string, or {@code null} if root is {@code null}
   */
  public String getXml_lined() {
    String xml = null;
    if (_root != null) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(getHeader(_charset));
      stringBuilder.append(_root.XML_lined());
      xml = stringBuilder.toString();
    } // if
    return xml;
  } // method

} // class
