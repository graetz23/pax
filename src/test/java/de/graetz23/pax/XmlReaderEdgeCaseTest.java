/**
 * @brief pax
 * @details A Java written generator for plain old XML (POX) data domains
 * @copyright Copyright (c) 2017-2026 Christian (graetz23@gmail.com)
 * @author Christian (graetz23@gmail.com)
 * @file XmlReaderEdgeCaseTest.java
 */

package de.graetz23.pax;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class XmlReaderEdgeCaseTest {

  @Test
  void testParseEmptyRoot() throws IOException {
    String xml = "<root></root>";
    IPax result = XmlReader.Instance.stream(new ByteArrayInputStream(xml.getBytes()));

    assertNotNull(result);
    assertEquals("root", result.Tag());
    assertFalse(result.hasVal());
  }

  @Test
  void testParseRootWithOnlyWhitespaceValue() throws IOException {
    String xml = "<root>   \n\t  </root>";
    IPax result = XmlReader.Instance.stream(new ByteArrayInputStream(xml.getBytes()));

    assertNotNull(result);
    assertFalse(result.hasVal());
  }

  @Test
  void testParseSpecialCharactersInValue() throws IOException {
    String xml = "<root>Test &amp; More</root>";
    IPax result = XmlReader.Instance.stream(new ByteArrayInputStream(xml.getBytes()));

    assertNotNull(result);
    assertNotNull(result.Val());
  }

  @Test
  void testParseManySiblings() throws IOException {
    StringBuilder sb = new StringBuilder("<root>");
    for (int i = 0; i < 100; i++) {
      sb.append("<item>").append(i).append("</item>");
    }
    sb.append("</root>");

    IPax result = XmlReader.Instance.stream(new ByteArrayInputStream(sb.toString().getBytes()));

    assertNotNull(result);
    assertEquals(100, result.Child().cnt());
  }

  @Test
  void testParseElementWithEmptyAttribute() throws IOException {
    String xml = "<root attr=\"\"><child/></root>";
    IPax result = XmlReader.Instance.stream(new ByteArrayInputStream(xml.getBytes()));

    assertNotNull(result);
    assertTrue(result.hasAttrib());
    IPax attr = result.Attrib().get("attr");
    assertNotNull(attr);
  }

  @Test
  void testParseMixedContent() throws IOException {
    String xml = "<root>text<child>inner</child>more text</root>";
    IPax result = XmlReader.Instance.stream(new ByteArrayInputStream(xml.getBytes()));

    assertNotNull(result);
    assertTrue(result.hasChild());
  }

  @Test
  void testParseSiblingWithAttributes() throws IOException {
    String xml = "<root><item id=\"1\" /><item id=\"2\" /><item id=\"3\" /></root>";
    IPax result = XmlReader.Instance.stream(new ByteArrayInputStream(xml.getBytes()));

    assertNotNull(result);
    assertEquals(3, result.Child().cnt());

    for (int i = 0; i < 3; i++) {
      IPax item = result.Child().all().get(i);
      assertEquals(String.valueOf(i + 1), item.Attrib().get("id").Val());
    }
  }

  @Test
  void testParseEntityReferences() throws IOException {
    String xml = "<root>&lt;tag&gt;</root>";
    IPax result = XmlReader.Instance.stream(new ByteArrayInputStream(xml.getBytes()));

    assertNotNull(result);
  }
}
