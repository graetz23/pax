/**
 * @brief pax
 * @details A Java written generator for plain old XML (POX) data domains
 * @copyright Copyright (c) 2017-2026 Christian (graetz23@gmail.com)
 * @author Christian (graetz23@gmail.com)
 * @file JsonReaderTest.java
 */

package de.graetz23.pax;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonGenerationTest {

  @BeforeEach
  void setUp() {
    Instances.resetFactory();
  }

  @Test
  void testJsonSimpleObject() {
    IPax root = Instances.Factory().produce("object");
    root.Attrib().add("name", "value");

    String json = root.JSON();

    assertNotNull(json);
    assertTrue(json.contains("\"name\""));
  }

  @Test
  void testJsonWithMultipleAttributes() {
    IPax root = Instances.Factory().produce("person");
    root.Attrib().add("name", "John");
    root.Attrib().add("age", "30");

    String json = root.JSON();

    assertTrue(json.contains("\"name\""));
    assertTrue(json.contains("\"age\""));
  }

  @Test
  void testJsonNumericValues() {
    IPax root = Instances.Factory().produce("data");
    root.Attrib().add("count", "42");
    root.Attrib().add("price", "19.99");

    String json = root.JSON();

    assertTrue(json.contains("\"count\""));
    assertTrue(json.contains("\"price\""));
  }

  @Test
  void testJsonBooleanValues() {
    IPax root = Instances.Factory().produce("settings");
    root.Attrib().add("active", "true");
    root.Attrib().add("deleted", "false");

    String json = root.JSON();

    assertTrue(json.contains("\"active\""));
    assertTrue(json.contains("\"deleted\""));
  }

  @Test
  void testJsonWithChildren() {
    IPax root = Instances.Factory().produce("book");
    IPax title = Instances.Factory().produce("title", "Effective Java");
    root.Child().add(title);

    String json = root.JSON();

    assertTrue(json.contains("\"title\""));
    assertTrue(json.contains("Effective Java"));
  }

  @Test
  void testJsonNestedObjects() {
    IPax library = Instances.Factory().produce("library");
    IPax book = Instances.Factory().produce("book");
    book.Attrib().add("id", "1");
    library.Child().add(book);

    String json = library.JSON();

    assertNotNull(json);
    assertTrue(json.contains("\"book\""));
  }

  @Test
  void testJsonArray() {
    IPax array = Instances.Factory().produce("array");
    array.Attrib().add(Identity.LIST, "true");
    array.Child().add("item", "first");
    array.Child().add("item", "second");

    String json = array.JSON();

    assertTrue(json.contains("\"__children__\""));
  }

  @Test
  void testJsonWideStructure() {
    IPax root = Instances.Factory().produce("root");

    for (int i = 0; i < 10; i++) {
      root.Attrib().add("attr" + i, "value" + i);
    }

    String json = root.JSON();

    assertNotNull(json);
    assertTrue(json.contains("\"__attributes__\""));
  }

  @Test
  void testJsonRoundTrip() {
    IPax original = Instances.Factory().produce("book");
    original.Attrib().add("id", "1");
    original.Child().add("title", "Test Book");

    String json = original.JSON();

    IPax parsed = JsonReader.Instance.parseJson(json);

    assertNotNull(parsed);
  }

  @Test
  void testJsonRoundTripComplexStructure() {
    IPax library = Instances.Factory().produce("library");
    library.Attrib().add("name", "City Library");
    library.Attrib().add("location", "Downtown");

    IPax book1 = Instances.Factory().produce("book");
    book1.Attrib().add("id", "1");
    book1.Attrib().add("available", "true");
    IPax title1 = Instances.Factory().produce("title", "Effective Java");
    book1.Child().add(title1);
    IPax author1 = Instances.Factory().produce("author", "Joshua Bloch");
    book1.Child().add(author1);
    library.Child().add(book1);

    IPax book2 = Instances.Factory().produce("book");
    book2.Attrib().add("id", "2");
    book2.Attrib().add("available", "false");
    IPax title2 = Instances.Factory().produce("title", "Clean Code");
    book2.Child().add(title2);
    IPax author2 = Instances.Factory().produce("author", "Robert Martin");
    book2.Child().add(author2);
    library.Child().add(book2);

    String json = library.JSON();

    IPax parsed = JsonReader.Instance.parseJson(json);

    assertNotNull(parsed);
    assertEquals("library", parsed.Tag());

    assertTrue(parsed.hasChild(), "Should have children");
  }

  @Test
  void testJsonRoundTripWithArray() {
    IPax root = Instances.Factory().produce("items");
    root.Attrib().add("type", "inventory");

    IPax array = Instances.Factory().produce("array");
    array.Attrib().add(Identity.LIST, "true");

    for (int i = 0; i < 3; i++) {
      IPax item = Instances.Factory().produce("item", "Value " + i);
      item.Attrib().add("index", String.valueOf(i));
      array.Child().add(item);
    }
    root.Child().add(array);

    String json = root.JSON();

    IPax parsed = JsonReader.Instance.parseJson(json);

    assertNotNull(parsed);
    assertTrue(parsed.hasChild());
  }

  @Test
  void testJsonRoundTripWithNumericAndBoolean() {
    IPax data = Instances.Factory().produce("data");
    data.Attrib().add("count", "42");
    data.Attrib().add("price", "19.99");
    data.Attrib().add("active", "true");
    data.Attrib().add("deleted", "false");

    String json = data.JSON();

    IPax parsed = JsonReader.Instance.parseJson(json);

    assertNotNull(parsed);
  }

  @Test
  void testJsonRoundTripDeepNesting() {
    IPax root = Instances.Factory().produce("root");

    IPax level1 = Instances.Factory().produce("level1");
    level1.Attrib().add("depth", "1");
    IPax level2 = Instances.Factory().produce("level2");
    level2.Attrib().add("depth", "2");
    IPax level3 = Instances.Factory().produce("level3");
    level3.Attrib().add("depth", "3");
    level3.Val("deep value");
    level2.Child().add(level3);
    level1.Child().add(level2);
    root.Child().add(level1);

    String json = root.JSON();

    IPax parsed = JsonReader.Instance.parseJson(json);

    assertNotNull(parsed);
    assertTrue(parsed.hasChild());
  }

  @Test
  void testFullRoundtrip() {
    IPax root = Instances.Factory().produce("root");
    root.Attrib().add("attr1", "value1");

    IPax child = Instances.Factory().produce("child");
    child.Val("childValue");
    root.Child().add(child);

    String json = root.JSON();

    IPax parsed = JsonReader.Instance.parseJson(json);

    assertEquals("root", parsed.Tag(), "Tag should be preserved");
    assertTrue(parsed.hasAttrib(), "Should have attributes");
    assertTrue(parsed.hasChild(), "Should have children");

    String json2 = parsed.JSON();
    assertEquals(json, json2, "JSON should be identical after roundtrip");
  }

  @Test
  void testFullRoundtripLevel3() {
    // Level 1: library
    IPax library = Instances.Factory().produce("library");
    library.Attrib().add("name", "City Library");
    library.Attrib().add("location", "Downtown");

    // Level 2: book
    IPax book1 = Instances.Factory().produce("book");
    book1.Attrib().add("id", "1");
    book1.Attrib().add("available", "true");
    library.Child().add(book1);

    // Level 3: title, author
    IPax title1 = Instances.Factory().produce("title", "Effective Java");
    book1.Child().add(title1);
    IPax author1 = Instances.Factory().produce("author", "Joshua Bloch");
    book1.Child().add(author1);

    // Another book (level 2)
    IPax book2 = Instances.Factory().produce("book");
    book2.Attrib().add("id", "2");
    book2.Attrib().add("available", "false");
    library.Child().add(book2);

    IPax title2 = Instances.Factory().produce("title", "Clean Code");
    book2.Child().add(title2);
    IPax author2 = Instances.Factory().produce("author", "Robert Martin");
    book2.Child().add(author2);

    String json = library.JSON();
    System.out.println("Level 3 roundtrip: JSON -> IPax");

    IPax parsed = JsonReader.Instance.parseJson(json);

    assertEquals("library", parsed.Tag(), "Level 1 tag should be preserved");
    assertTrue(parsed.hasAttrib(), "Level 1 should have attributes");

    assertEquals("City Library", parsed.Attrib().get("name").Val());
    assertEquals("Downtown", parsed.Attrib().get("location").Val());

    assertTrue(parsed.hasChild(), "Should have book children");

    // Verify level 2
    IPax book = parsed.Child().get("book");
    assertNotNull(book, "Should have book child");

    // Verify level 3
    IPax title = book.Child().get("title");
    assertNotNull(title, "Should have title child");
    assertEquals("Effective Java", title.Val(), "Title value should match");

    String json2 = parsed.JSON();
    System.out.println("Level 3 roundtrip: IPax -> JSON");

    // Compare XML outputs
    assertEquals(json, json2, "JSON should match after full roundtrip");
    System.out.println("Level 3 roundtrip: passed");
  }

  @Test
  void testFullRoundtripLevel5() {
    // Level 1: library
    IPax library = Instances.Factory().produce("library");
    library.Attrib().add("name", "City Library");
    library.Attrib().add("location", "Downtown");

    // Level 2: book
    IPax book1 = Instances.Factory().produce("book");
    book1.Attrib().add("id", "1");
    book1.Attrib().add("isbn", "978-0321125217");
    library.Child().add(book1);

    // Level 3: chapter
    IPax chapter1 = Instances.Factory().produce("chapter");
    chapter1.Attrib().add("number", "1");
    book1.Child().add(chapter1);

    // Level 4: section
    IPax section1 = Instances.Factory().produce("section");
    section1.Attrib().add("title", "Introduction");
    chapter1.Child().add(section1);

    // Level 5: paragraph
    IPax para1 = Instances.Factory().produce("paragraph", "This is the introduction paragraph.");
    section1.Child().add(para1);

    // Another book
    IPax book2 = Instances.Factory().produce("book");
    book2.Attrib().add("id", "2");
    book2.Attrib().add("isbn", "978-0135957059");
    library.Child().add(book2);

    IPax chapter2 = Instances.Factory().produce("chapter");
    chapter2.Attrib().add("number", "1");
    book2.Child().add(chapter2);

    IPax section2 = Instances.Factory().produce("section");
    section2.Attrib().add("title", "Getting Started");
    chapter2.Child().add(section2);

    IPax para2 = Instances.Factory().produce("paragraph", "Let's begin with the basics.");
    section2.Child().add(para2);

    // First XML output
    System.out.println("Level 5 roundtrip: library XML");
    String xml1 = library.XML();

    System.out.println("Level 5 roundtrip: XML -> IPax");
    // Parse XML to IPax
    java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(xml1.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    IPax fromXml = XmlReader.Instance.stream(bis);

    // Generate JSON
    System.out.println("Level 5 roundtrip: IPax -> JSON");
    String json = fromXml.JSON();

    // Parse JSON back to IPax
    System.out.println("Level 5 roundtrip: JSON -> IPax");
    IPax fromJson = JsonReader.Instance.parseJson(json);

    // Generate XML again
    String xml2 = fromJson.XML();
    System.out.println("Level 5 roundtrip: IPax -> XML");

    // Compare XML outputs
    assertEquals(xml1, xml2, "XML should match after full roundtrip");
    System.out.println("Level 5 roundtrip: passed");

    // Verify structure is preserved
    assertEquals("library", fromJson.Tag());
    assertTrue(fromJson.hasChild());

    IPax book = fromJson.Child().get("book");
    assertNotNull(book);
    assertEquals("1", book.Attrib().get("id").Val());

    IPax chapter = book.Child().get("chapter");
    assertNotNull(chapter);
    assertEquals("1", chapter.Attrib().get("number").Val());

    IPax section = chapter.Child().get("section");
    assertNotNull(section);
    assertEquals("Introduction", section.Attrib().get("title").Val());

    IPax paragraph = section.Child().get("paragraph");
    assertNotNull(paragraph);
    assertEquals("This is the introduction paragraph.", paragraph.Val());
  }
}
