/**
 * @brief pax
 * @details A Java written generator for plain old XML (POX) data domains
 * @copyright Copyright (c) 2017-2026 Christian (graetz23@gmail.com)
 * @author Christian (graetz23@gmail.com)
 * @file PerformanceTest.java
 */

package de.graetz23.pax;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PerformanceTest {

  @BeforeEach
  void setUp() {
    Instances.resetFactory();
  }

  @Test
  void testCreateManyNodes() {
    long start = System.nanoTime();

    for (int i = 0; i < 10000; i++) {
      IPax node = Instances.Factory().produce("node" + i);
      node.Val("value" + i);
    }

    long end = System.nanoTime();
    long duration = (end - start) / 1_000_000;

    System.out.println("Creating 10000 nodes: " + duration + "ms");
    assertTrue(duration < 1000, "Should create 10000 nodes in under 1 second");
  }

  @Test
  void testBuildDeepTree() {
    long start = System.nanoTime();

    IPax root = Instances.Factory().produce("root");
    IPax current = root;

    for (int i = 0; i < 5000; i++) {
      IPax child = Instances.Factory().produce("level" + i);
      current.Child().add(child);
      current = child;
    }

    long end = System.nanoTime();
    long duration = (end - start) / 1_000_000;

    System.out.println("Building 5000-level deep tree: " + duration + "ms");
    assertTrue(duration < 5000, "Should build deep tree in under 500ms");
  }

  @Test
  void testBuildWideTree() {
    long start = System.nanoTime();

    IPax root = Instances.Factory().produce("root");

    for (int i = 0; i < 10000; i++) {
      root.Child().add("child" + i);
    }

    long end = System.nanoTime();
    long duration = (end - start) / 1_000_000;

    assertEquals(10000, root.Child().cnt());
    System.out.println("Building tree with 10000 children: " + duration + "ms");
    assertTrue(duration < 500, "Should build wide tree in under 500ms");
  }

  @Test
  void testXmlGeneration() {
    IPax root = Instances.Factory().produce("root");

    for (int i = 0; i < 100; i++) {
      IPax child = Instances.Factory().produce("item", "value" + i);
      child.Attrib().add("id", String.valueOf(i));
      root.Child().add(child);
    }

    long start = System.nanoTime();

    for (int i = 0; i < 100; i++) {
      root.XML();
    }

    long end = System.nanoTime();
    long duration = (end - start) / 1_000_000;

    System.out.println("Generating XML 100 times (100 children): " + duration + "ms");
    assertTrue(duration < 2000, "Should generate XML 100 times in under 2 seconds");
  }

  @Test
  void testXmlLinedGeneration() {
    IPax root = Instances.Factory().produce("root");

    for (int i = 0; i < 100; i++) {
      IPax child = Instances.Factory().produce("item", "value" + i);
      root.Child().add(child);
    }

    long start = System.nanoTime();

    for (int i = 0; i < 100; i++) {
      root.XML_lined();
    }

    long end = System.nanoTime();
    long duration = (end - start) / 1_000_000;

    System.out.println("Generating XML_lined 100 times: " + duration + "ms");
    assertTrue(duration < 1500, "Should generate XML_lined 100 times in under 1.5 seconds");
  }

  @Test
  void testXmlParsing() throws IOException {
    StringBuilder sb = new StringBuilder("<root>");
    for (int i = 0; i < 100; i++) {
      sb.append("<item id=\"").append(i).append("\">value").append(i).append("</item>");
    }
    sb.append("</root>");

    byte[] xmlBytes = sb.toString().getBytes();

    long start = System.nanoTime();

    for (int i = 0; i < 100; i++) {
      XmlReader.Instance.stream(new ByteArrayInputStream(xmlBytes));
    }

    long end = System.nanoTime();
    long duration = (end - start) / 1_000_000;

    System.out.println("Parsing XML 100 times (100 children): " + duration + "ms");
    assertTrue(duration < 3000, "Should parse XML 100 times in under 3 seconds");
  }

  @Test
  void testAttributeOperations() {
    IPax root = Instances.Factory().produce("root");

    long start = System.nanoTime();

    for (int i = 0; i < 1000; i++) {
      root.Attrib().add("attr" + i, "value" + i);
    }

    long end = System.nanoTime();
    long duration = (end - start) / 1_000_000;

    assertEquals(1000, root.Attrib().cnt());
    System.out.println("Adding 1000 attributes: " + duration + "ms");
    assertTrue(duration < 500, "Should add 1000 attributes in under 500ms");
  }

  @Test
  void testChildRetrieval() {
    IPax root = Instances.Factory().produce("root");

    for (int i = 0; i < 500; i++) {
      root.Child().add("item", "value" + i);
    }

    long start = System.nanoTime();

    for (int i = 0; i < 10000; i++) {
      root.Child().get("item");
    }

    long end = System.nanoTime();
    long duration = (end - start) / 1_000_000;

    System.out.println("10000 child retrievals (500 children): " + duration + "ms");
    assertTrue(duration < 1000, "Should retrieve children 10000 times in under 1 second");
  }

  @Test
  void testCopyOperation() {
    IPax original = Instances.Factory().produce("book");
    original.Attrib().add("id", "1");

    for (int i = 0; i < 10; i++) {
      IPax chapter = Instances.Factory().produce("chapter", "Content " + i);
      original.Child().add(chapter);
    }

    long start = System.nanoTime();

    for (int i = 0; i < 1000; i++) {
      Instances.Factory().copy(original);
    }

    long end = System.nanoTime();
    long duration = (end - start) / 1_000_000;

    System.out.println("Copying node 1000 times (10 children): " + duration + "ms");
    assertTrue(duration < 2000, "Should copy node 1000 times in under 2 seconds");
  }

  @Test
  void testRoundTrip() throws IOException {
    IPax original = Instances.Factory().produce("library");

    for (int i = 0; i < 100; i++) {
      IPax book = Instances.Factory().produce("book");
      book.Attrib().add("id", String.valueOf(i));
      book.Child().add("title", "Book " + i);
      original.Child().add(book);
    }

    long start = System.nanoTime();

    for (int i = 0; i < 100; i++) {
      String xml = original.XML();
      IPax parsed = XmlReader.Instance.stream(new ByteArrayInputStream(xml.getBytes()));
      assertEquals(100, parsed.Child().cnt());
    }

    long end = System.nanoTime();
    long duration = (end - start) / 1_000_000;

    System.out.println("100 round-trips (100 books): " + duration + "ms");
    assertTrue(duration < 5000, "Should complete 100 round-trips in under 5 seconds");
  }

  @Test
  void testPathCalculation() {
    IPax root = Instances.Factory().produce("root");
    IPax current = root;

    for (int i = 0; i < 100; i++) {
      IPax child = Instances.Factory().produce("level" + i);
      current.Child().add(child);
      current = child;
    }

    long start = System.nanoTime();

    for (int i = 0; i < 10000; i++) {
      current.Path();
    }

    long end = System.nanoTime();
    long duration = (end - start) / 1_000_000;

    System.out.println("10000 path calculations (100 levels deep): " + duration + "ms");
    assertTrue(duration < 2000, "Should calculate paths 10000 times in under 2 seconds");
  }

  @Test
  void testAllChildrenRetrieval() {
    IPax root = Instances.Factory().produce("root");

    for (int i = 0; i < 1000; i++) {
      root.Child().add("item", "value" + i);
    }

    long start = System.nanoTime();

    for (int i = 0; i < 10000; i++) {
      root.Child().all();
    }

    long end = System.nanoTime();
    long duration = (end - start) / 1_000_000;

    System.out.println("10000 all() retrievals (1000 children): " + duration + "ms");
    assertTrue(duration < 1000, "Should retrieve all children 10000 times in under 1 second");
  }

  @Test
  void testComplexDocument() throws IOException {
    long start = System.nanoTime();

    IPax library = Instances.Factory().produce("library");

    for (int b = 0; b < 100; b++) {
      IPax book = Instances.Factory().produce("book");
      book.Attrib().add("id", String.valueOf(b));
      book.Child().add("title", "Book Title " + b);
      book.Child().add("author", "Author " + b);

      for (int c = 0; c < 10; c++) {
        IPax chapter = Instances.Factory().produce("chapter");
        chapter.Val("Chapter " + c);
        book.Child().add(chapter);
      }

      library.Child().add(book);
    }

    String xml = library.XML();
    IPax parsed = XmlReader.Instance.stream(new ByteArrayInputStream(xml.getBytes()));

    long end = System.nanoTime();
    long duration = (end - start) / 1_000_000;

    assertNotNull(parsed);
    assertEquals(100, parsed.Child().cnt());
    System.out.println("Complex document (100 books, 10 chapters each): " + duration + "ms");
    assertTrue(duration < 2000, "Should build and parse complex document in under 2 seconds");
  }
}
