/**
 * @brief pax
 * @details A Java written generator for plain old XML (POX) data domains
 * @copyright Copyright (c) 2017-2026 Christian (graetz23@gmail.com)
 * @author Christian (graetz23@gmail.com)
 * @file XmlReaderTest.java
 */

package de.graetz23.pax;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class XmlReaderTest {

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() {
    Instances.resetFactory();
  }

  @Test
  void testParseFromStream() throws IOException {
    String xml = "<root><child>value</child></root>";
    ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes());

    IPax result = XmlReader.Instance.stream(stream);

    assertNotNull(result);
    assertEquals("root", result.Tag());
    assertTrue(result.hasChild());
    assertEquals(1, result.Child().cnt());
  }

  @Test
  void testParseSimpleXml() throws IOException {
    String xml = "<root><child>test</child></root>";
    IPax result = XmlReader.Instance.stream(new ByteArrayInputStream(xml.getBytes()));

    assertEquals("root", result.Tag());
    IPax child = result.Child().get("child");
    assertNotNull(child);
    assertEquals("test", child.Val());
  }

  @Test
  void testParseWithAttributes() throws IOException {
    String xml = "<root id=\"123\" name=\"test\"><child>value</child></root>";
    IPax result = XmlReader.Instance.stream(new ByteArrayInputStream(xml.getBytes()));

    assertEquals("root", result.Tag());
    assertTrue(result.hasAttrib());
    assertEquals("123", result.Attrib().get("id").Val());
    assertEquals("test", result.Attrib().get("name").Val());
  }

  @Test
  void testParseNestedChildren() throws IOException {
    String xml = "<root><parent><child><grandchild>deep</grandchild></child></parent></root>";
    IPax result = XmlReader.Instance.stream(new ByteArrayInputStream(xml.getBytes()));

    assertEquals("root", result.Tag());
    IPax parent = result.Child().get("parent");
    assertNotNull(parent);
    IPax child = parent.Child().get("child");
    assertNotNull(child);
    IPax grandchild = child.Child().get("grandchild");
    assertNotNull(grandchild);
    assertEquals("deep", grandchild.Val());
  }

  @Test
  void testParseMultipleChildrenWithSameTag() throws IOException {
    String xml = "<root><item>1</item><item>2</item><item>3</item></root>";
    IPax result = XmlReader.Instance.stream(new ByteArrayInputStream(xml.getBytes()));

    assertEquals("root", result.Tag());
    assertEquals(3, result.Child().cnt());
  }

  @Test
  void testParseEmptyElement() throws IOException {
    String xml = "<root><empty /></root>";
    IPax result = XmlReader.Instance.stream(new ByteArrayInputStream(xml.getBytes()));

    IPax empty = result.Child().get("empty");
    assertNotNull(empty);
    assertFalse(empty.hasVal());
  }

  @Test
  void testParseSelfClosingTag() throws IOException {
    String xml = "<root><element attribute=\"value\" /></root>";
    IPax result = XmlReader.Instance.stream(new ByteArrayInputStream(xml.getBytes()));

    IPax element = result.Child().get("element");
    assertNotNull(element);
    assertEquals("value", element.Attrib().get("attribute").Val());
  }

  @Test
  void testParseFromFile() throws IOException {
    String xml = "<root><child>file content</child></root>";
    Path file = tempDir.resolve("test.xml");
    Files.writeString(file, xml);

    IPax result = XmlReader.Instance.parse(file.toString());

    assertNotNull(result);
    assertEquals("root", result.Tag());
    assertEquals("file content", result.Child().get("child").Val());
  }

  @Test
  void testParseFromLocalFile() throws IOException {
    String xml = "<root><child>local file</child></root>";
    Path file = tempDir.resolve("local.xml");
    Files.writeString(file, xml);

    IPax result = XmlReader.Instance.parseLocalFile(file.toString());

    assertNotNull(result);
    assertEquals("root", result.Tag());
  }

  @Test
  void testParseNullStream() {
    IPax result = XmlReader.Instance.stream(null);
    assertNull(result);
  }

  @Test
  void testParseWithComment() throws IOException {
    String xml = "<root><!-- this is a comment --><child>value</child></root>";
    IPax result = XmlReader.Instance.stream(new ByteArrayInputStream(xml.getBytes()));

    assertNotNull(result);
    assertTrue(result.hasChild());
  }

  @Test
  void testRoundTrip() throws IOException {
    IPax original = Instances.Factory().produce("book");
    original.Attrib().add("id", "100");
    IPax title = Instances.Factory().produce("title");
    title.Val("Effective Java");
    original.Child().add(title);

    String xml = original.XML();
    IPax parsed = XmlReader.Instance.stream(new ByteArrayInputStream(xml.getBytes()));

    assertNotNull(parsed);
    assertEquals("book", parsed.Tag());
    assertTrue(parsed.hasAttrib());
    assertEquals("100", parsed.Attrib().get("id").Val());
  }

  @Test
  void testParseXmlHeader() throws IOException {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>content</root>";
    IPax result = XmlReader.Instance.stream(new ByteArrayInputStream(xml.getBytes()));

    assertNotNull(result);
    assertEquals("root", result.Tag());
    assertEquals("content", result.Val());
  }

  @Test
  void testParseWhitespaceHandling() throws IOException {
    String xml = "<root>\n  <child>value</child>\n</root>";
    IPax result = XmlReader.Instance.stream(new ByteArrayInputStream(xml.getBytes()));

    IPax child = result.Child().get("child");
    assertNotNull(child);
    assertEquals("value", child.Val());
  }

  @Test
  void testParseComplexDocument() throws IOException {
    String xml = "<?xml version=\"1.0\"?>" + "<library>" + "  <book id=\"1\">" + "    <title>Book Title</title>" + "    <author>Author Name</author>" + "  </book>" + "  <book id=\"2\">" + "    <title>Another Book</title>" + "    <author>Another Author</author>" + "  </book>" + "</library>";

    IPax result = XmlReader.Instance.stream(new ByteArrayInputStream(xml.getBytes()));

    assertEquals("library", result.Tag());
    assertEquals(2, result.Child().cnt());

    IPax book1 = result.Child().all().get(0);
    assertEquals("1", book1.Attrib().get("id").Val());
    assertEquals("Book Title", book1.Child().get("title").Val());
  }
}
