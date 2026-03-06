/**
 * @brief pax
 * @details A Java written generator for plain old XML (POX) data domains
 * @copyright Copyright (c) 2017-2026 Christian (graetz23@gmail.com)
 * @author Christian (graetz23@gmail.com)
 * @file PaxTest.java
 */

package de.graetz23.pax;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaxTest {

  private IPax root;

  @BeforeEach
  void setUp() {
    root = Instances.Factory().produce("root");
  }

  @Test
  void testTagGetterSetter() {
    assertEquals("root", root.Tag());

    root.Tag("newTag");
    assertEquals("newTag", root.Tag());
  }

  @Test
  void testHasTag() {
    assertTrue(root.hasTag());

    IPax noTag = Instances.Factory().produce(null);
    assertFalse(noTag.hasTag());

    IPax emptyTag = Instances.Factory().produce("");
    assertFalse(emptyTag.hasTag());
  }

  @Test
  void testValGetterSetter() {
    assertFalse(root.hasVal());
    assertNull(root.Val());

    root.Val("testValue");
    assertTrue(root.hasVal());
    assertEquals("testValue", root.Val());
  }

  @Test
  void testValRejectsEmptyOrBlank() {
    root.Val("valid");
    assertTrue(root.hasVal());

    root.Val("");
    assertFalse(root.hasVal());
    assertNull(root.Val());

    root.Val("   ");
    assertFalse(root.hasVal());
    assertNull(root.Val());

    root.Val("\n");
    assertFalse(root.hasVal());
  }

  @Test
  void testParentGetterSetter() {
    assertFalse(root.hasParent());
    assertNull(root.Parent());

    IPax child = Instances.Factory().produce("child");
    child.Parent(root);

    assertTrue(child.hasParent());
    assertEquals(root, child.Parent());
  }

  @Test
  void testPath() {
    assertEquals("/root", root.Path());

    IPax child = Instances.Factory().produce("child");
    root.Child().add(child);

    assertEquals("/root/child", child.Path());

    IPax grandchild = Instances.Factory().produce("grandchild");
    child.Child().add(grandchild);

    assertEquals("/root/child/grandchild", grandchild.Path());
  }

  @Test
  void testPathWithNoTag() {
    IPax noTag = Instances.Factory().produce(null);
    assertEquals("/null", noTag.Path());
  }

  @Test
  void testChildCreation() {
    assertFalse(root.hasChild());

    root.Child().add("child1");

    assertTrue(root.hasChild());
    assertEquals(1, root.Child().cnt());
  }

  @Test
  void testChildGet() {
    root.Child().add("child1");
    root.Child().add("child2");

    IPax child1 = root.Child().get("child1");
    assertNotNull(child1);
    assertEquals("child1", child1.Tag());

    IPax child2 = root.Child().get("child2");
    assertNotNull(child2);
    assertEquals("child2", child2.Tag());
  }

  @Test
  void testChildGetNotFound() {
    IPax notFound = root.Child().get("nonexistent");
    assertNull(notFound);
  }

  @Test
  void testChildAll() {
    root.Child().add("child1");
    root.Child().add("child2");
    root.Child().add("child3");

    assertEquals(3, root.Child().all().size());
  }

  @Test
  void testChildDelete() {
    root.Child().add("child1");
    root.Child().add("child2");

    IPax child1 = root.Child().get("child1");
    root.Child().del(child1);

    assertEquals(1, root.Child().cnt());
    assertNull(root.Child().get("child1"));
  }

  @Test
  void testAttributeCreation() {
    assertFalse(root.hasAttrib());

    root.Attrib().add("id", "123");

    assertTrue(root.hasAttrib());
    assertEquals(1, root.Attrib().cnt());
  }

  @Test
  void testAttributeGet() {
    root.Attrib().add("id", "123");
    root.Attrib().add("name", "test");

    IPax attrId = root.Attrib().get("id");
    assertNotNull(attrId);
    assertEquals("id", attrId.Tag());
    assertEquals("123", attrId.Val());

    IPax attrName = root.Attrib().get("name");
    assertNotNull(attrName);
    assertEquals("test", attrName.Val());
  }

  @Test
  void testAttributeGetNotFound() {
    root.Attrib().add("id", "123");

    IPax notFound = root.Attrib().get("nonexistent");
    assertNull(notFound);
  }

  @Test
  void testAttributeAll() {
    root.Attrib().add("a", "1");
    root.Attrib().add("b", "2");
    root.Attrib().add("c", "3");

    assertEquals(3, root.Attrib().all().size());
  }

  @Test
  void testXMLWithNoTag() {
    IPax noTag = Instances.Factory().produce(null);
    assertEquals("", noTag.XML());
  }

  @Test
  void testXMLSelfClosing() {
    root.Tag("element");
    String xml = root.XML();
    assertTrue(xml.contains("<element />"));
  }

  @Test
  void testXMLWithValue() {
    root.Tag("element");
    root.Val("content");

    String xml = root.XML();
    assertTrue(xml.contains("<element>content</element>"));
  }

  @Test
  void testXMLWithAttributes() {
    root.Tag("element");
    root.Attrib().add("id", "123");
    root.Attrib().add("name", "test");

    String xml = root.XML();
    assertTrue(xml.contains("id=\"123\""));
    assertTrue(xml.contains("name=\"test\""));
  }

  @Test
  void testXMLWithChildren() {
    root.Tag("parent");
    root.Child().add("child");

    String xml = root.XML();
    assertTrue(xml.contains("<parent>"));
    assertTrue(xml.contains("<child"));
    assertTrue(xml.contains("</parent>"));
  }

  @Test
  void testXMLLined() {
    root.Tag("parent");
    root.Child().add("child");

    String xml = root.XML_lined();
    assertTrue(xml.contains("<parent>"));
    assertTrue(xml.contains("<child"));
    assertTrue(xml.contains("</parent>"));
    assertFalse(xml.contains("\n"));
  }

  @Test
  void testXMLWithComment() {
    root.Tag(Identity.COMMENT);
    root.Val("This is a comment");

    String xml = root.XML();
    assertTrue(xml.contains("<!--This is a comment-->"));
  }

  @Test
  void testXMLWithCData() {
    root.Tag(Identity.CDATA);
    root.Val("<html></html>");

    String xml = root.XML();
    assertTrue(xml.contains("<![CDATA[<html></html>]]>"));
  }

  @Test
  void testCopyConstructor() {
    root.Tag("original");
    root.Val("value");
    root.Attrib().add("attr", "attrValue");
    root.Child().add("child");

    IPax copy = Instances.Factory().copy(root);

    assertEquals(root.Tag(), copy.Tag());
    assertEquals(root.Val(), copy.Val());
    assertTrue(copy.hasAttrib());
    assertTrue(copy.hasChild());
    assertEquals(1, copy.Attrib().cnt());
    assertEquals(1, copy.Child().cnt());
  }

  @Test
  void testCopyConstructorWithNull() {
    IPax copy = Instances.Factory().copy(null);
    assertNotNull(copy);
    assertFalse(copy.hasTag());
  }

  @Test
  void testFullWorkflow() {
    IPax book = Instances.Factory().produce("book");
    book.Attrib().add("id", "100");

    IPax title = Instances.Factory().produce("title");
    title.Val("Effective Java");
    book.Child().add(title);

    IPax author = Instances.Factory().produce("author");
    author.Val("Joshua Bloch");
    book.Child().add(author);

    IPax chapter = Instances.Factory().produce("chapter");
    chapter.Val("Chapter 1");
    book.Child().add(chapter);

    assertTrue(book.hasTag());
    assertFalse(book.hasVal());
    assertTrue(book.hasAttrib());
    assertTrue(book.hasChild());
    assertEquals(3, book.Child().cnt());

    String xml = book.XML();
    assertNotNull(xml);
    assertTrue(xml.contains("<book"));
    assertTrue(xml.contains("id=\"100\""));
    assertTrue(xml.contains("<title>"));
    assertTrue(xml.contains("Effective Java"));
    assertTrue(xml.contains("<author>"));
    assertTrue(xml.contains("Joshua Bloch"));
  }

  @Test
  void testSearchByTag() {
    root.Tag("root");
    root.Child().add("child");
    root.Child().add("item");
    root.Child().get("child").Child().add("item");
    root.Child().get("child").Child().get("item").Child().add("item");

    List<IPax> items = root.Child().searchByTag("item");
    assertEquals(3, items.size());
  }

  @Test
  void testSearchByTagNotFound() {
    root.Tag("root");
    root.Child().add("child");

    List<IPax> found = root.Child().searchByTag("nonexistent");
    assertTrue(found.isEmpty());
  }

  @Test
  void testSearchByTagEmptyTree() {
    root.Tag("root");

    List<IPax> found = root.Child().searchByTag("root");
    assertTrue(found.isEmpty());
  }

  @Test
  void testSearchByTagNullTag() {
    root.Tag("root");
    root.Child().add("child");

    List<IPax> found = root.Child().searchByTag(null);
    assertTrue(found.isEmpty());
  }

  @Test
  void testSearchByPath() {
    root.Tag("root");
    IPax child = Instances.Factory().produce("child");
    root.Child().add(child);
    child.Child().add("grandchild");
    child.Child().add("grandchild");

    List<IPax> found = root.Child().searchByPath("/root/child/grandchild");
    assertEquals(2, found.size());
  }

  @Test
  void testSearchByPathSingleMatch() {
    root.Tag("root");
    IPax child = Instances.Factory().produce("child");
    root.Child().add(child);
    IPax grandchild = Instances.Factory().produce("grandchild");
    child.Child().add(grandchild);

    List<IPax> found = root.Child().searchByPath("child/grandchild");
    assertEquals(1, found.size());
    assertEquals("grandchild", found.get(0).Tag());
  }

  @Test
  void testSearchByPathNotFound() {
    root.Tag("root");
    root.Child().add("child");

    List<IPax> found = root.Child().searchByPath("/root/child/nonexistent");
    assertTrue(found.isEmpty());
  }

  @Test
  void testSearchByPathWithRedundantSlashes() {
    root.Tag("root");
    IPax child = Instances.Factory().produce("child");
    root.Child().add(child);
    child.Child().add("grandchild");

    List<IPax> found = root.Child().searchByPath("///child//grandchild///");
    assertEquals(1, found.size());
  }

  @Test
  void testSearchByPathEmptyPath() {
    root.Tag("root");
    root.Child().add("child");

    List<IPax> found = root.Child().searchByPath("");
    assertTrue(found.isEmpty());
  }
}
