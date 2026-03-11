/**
 * @brief pax
 * @details An object-tree combined with a tolerant reader to parse any XML
 * @copyright Copyright (c) 2017-2026 Christian (graetz23@gmail.com)
 * @author Christian (graetz23@gmail.com)
 * @file XmlFile.java
 * <p>
 * Wrapping class to generate the necessary XML header and having a single root element.
 */

package de.graetz23.pax;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class XmlFile {

  private Charset _charset = null;
  private IPax _root = null; // the XML root node

  /// uses UTF-8 in general
  public XmlFile(IPax root) {
    _root = root;
    _charset = StandardCharsets.UTF_8;
  } // constructor

  /// use a different encoding
  public XmlFile(IPax root, Charset charset) {
    _root = root;
    _charset = charset;
  } // constructor

  private String getHeader(Charset charset) {
    String charset_ = charset.toString();
    return "<?xml version=\"1.0\" encoding=\"" + charset_ + "\"?>";
  } // method

  public Charset getCharset() {
    return _charset;
  } // method

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

} // class
