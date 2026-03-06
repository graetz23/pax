/**
 * @brief pax
 * @details A Java written generator for plain old XML (POX) data domains
 * @copyright Copyright (c) 2017-2026 Christian (graetz23@gmail.com)
 * @author Christian (graetz23@gmail.com)
 * @file XmlWriterTest.java
 */

package de.graetz23.pax;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XmlWriterTest {

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() {
    Instances.resetFactory();
  }

  @Test
  void testWriteToFile() throws Exception {
    IPax root = Instances.Factory().produce("root");
    root.Val("content");

    Path file = tempDir.resolve("output.xml");
    boolean result = XmlWriter.Instance.XML(root, file.toString());

    assertTrue(result);
    assertTrue(Files.exists(file));
    String content = Files.readString(file);
    assertTrue(content.contains("<?xml"));
    assertTrue(content.contains("<root>content</root>"));
  }

  @Test
  void testWriteAddsXmlExtension() throws Exception {
    IPax root = Instances.Factory().produce("root");

    Path file = tempDir.resolve("noextension");
    XmlWriter.Instance.XML(root, file.toString());

    assertTrue(Files.exists(tempDir.resolve("noextension.xml")));
  }

  @Test
  void testWritePreservesXmlExtension() throws Exception {
    IPax root = Instances.Factory().produce("root");

    Path file = tempDir.resolve("already.xml");
    XmlWriter.Instance.XML(root, file.toString());

    assertTrue(Files.exists(file));
  }

  @Test
  void testWriteWithAttributes() throws Exception {
    IPax root = Instances.Factory().produce("element");
    root.Attrib().add("id", "123");
    root.Attrib().add("name", "test");

    Path file = tempDir.resolve("attr.xml");
    XmlWriter.Instance.XML(root, file.toString());

    String content = Files.readString(file);
    assertTrue(content.contains("id=\"123\""));
    assertTrue(content.contains("name=\"test\""));
  }

  @Test
  void testWriteWithChildren() throws Exception {
    IPax root = Instances.Factory().produce("parent");
    root.Child().add("child1");
    root.Child().add("child2");

    Path file = tempDir.resolve("children.xml");
    XmlWriter.Instance.XML(root, file.toString());

    String content = Files.readString(file);
    assertTrue(content.contains("<parent>"));
    assertTrue(content.contains("<child1"));
    assertTrue(content.contains("<child2"));
  }

  @Test
  void testWriteNullRoot() {
    boolean result = XmlWriter.Instance.XML(null);
    assertFalse(result);
  }

  @Test
  void testWriteNoTag() throws Exception {
    IPax root = Instances.Factory().produce(null);

    Path file = tempDir.resolve("notag.xml");
    XmlWriter.Instance.XML(root, file.toString());

    assertTrue(Files.exists(file));
  }

  @Test
  void testWriteNestedStructure() throws Exception {
    IPax library = Instances.Factory().produce("library");
    IPax book = Instances.Factory().produce("book");
    book.Attrib().add("id", "1");
    library.Child().add(book);
    IPax title = Instances.Factory().produce("title");
    title.Val("Test Book");
    book.Child().add(title);

    Path file = tempDir.resolve("nested.xml");
    XmlWriter.Instance.XML(library, file.toString());

    String content = Files.readString(file);
    assertTrue(content.contains("<library>"));
    assertTrue(content.contains("<book"));
    assertTrue(content.contains("id=\"1\""));
  }

  @Test
  void testXMLMethodNoFilename() {
    IPax root = Instances.Factory().produce("root");
    boolean result = XmlWriter.Instance.XML(root);
    assertTrue(result);
  }

  @Test
  void testWriteUtf8Encoding() throws Exception {
    IPax root = Instances.Factory().produce("root");
    root.Val("UTF-8 test: äöü");

    Path file = tempDir.resolve("encoding.xml");
    XmlWriter.Instance.XML(root, file.toString());

    String content = Files.readString(file);
    assertTrue(content.contains("UTF-8"));
  }
}
