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

  @Test
  void testSearchByPathLargeLibrary() {
    IPax library = Instances.Factory().produce("library");

    String[][] bookData = {
      {"The Art of Computer Programming", "Donald Knuth", "1968", "978-0201896831", "Addison-Wesley", "1", "1"},
      {"Introduction to Algorithms", "Thomas Cormen", "2009", "978-0262033848", "MIT Press", "2", "2"},
      {"Design Patterns", "Erich Gamma", "1994", "978-0201633610", "Addison-Wesley", "3", "3"},
      {"Clean Code", "Robert Martin", "2008", "978-0132350884", "Prentice Hall", "4", "4"},
      {"The Pragmatic Programmer", "Andrew Hunt", "1999", "978-0201616224", "Addison-Wesley", "5", "5"},
      {"Structure and Interpretation of Computer Programs", "Harold Abelson", "1996", "978-0262510871", "MIT Press", "6", "6"},
      {"Algorithms", "Robert Sedgewick", "2011", "978-0321573513", "Addison-Wesley", "7", "7"},
      {"Computer Systems", "Randal Bryant", "2015", "978-0134092669", "Pearson", "8", "8"},
      {"Operating System Concepts", "Abraham Silberschatz", "2012", "978-1118063330", "Wiley", "9", "9"},
      {"Database System Concepts", "Abraham Silberschatz", "2010", "978-0078022159", "McGraw-Hill", "10", "10"},
      {"Computer Networks", "Andrew Tanenbaum", "2010", "978-0132126953", "Prentice Hall", "11", "11"},
      {"Artificial Intelligence", "Stuart Russell", "2020", "978-0136042594", "Pearson", "12", "12"},
      {"Deep Learning", "Ian Goodfellow", "2016", "978-0262035613", "MIT Press", "13", "13"},
      {"The Elements of Statistical Learning", "Trevor Hastie", "2009", "978-0387848570", "Springer", "14", "14"},
      {"Pattern Recognition", "Bishop", "2006", "978-0387310732", "Springer", "15", "15"},
      {"Computer Graphics", "James Foley", "1995", "978-0201847603", "Addison-Wesley", "16", "16"},
      {"Discrete Mathematics", "Kenneth Rosen", "2011", "978-0073383095", "McGraw-Hill", "17", "17"},
      {"Linear Algebra", "Gilbert Strang", "2005", "978-0961408877", "Wellesley-Cambridge Press", "18", "18"},
      {"Calculus", "James Stewart", "2015", "978-1285741550", "Cengage Learning", "19", "19"},
      {"Probability and Statistics", "Morris DeGroot", "2011", "978-0321500465", "Pearson", "20", "20"},
      {"Compilers", "Alfred Aho", "2006", "978-0201100886", "Addison-Wesley", "21", "21"},
      {"Operating Systems", "Andrew Tanenbaum", "2006", "978-0136006290", "Prentice Hall", "22", "22"},
      {"Computer Architecture", "John Hennessy", "2017", "978-0123838728", "Morgan Kaufmann", "23", "23"},
      {"Software Engineering", "Ian Sommerville", "2015", "978-0133943030", "Pearson", "24", "24"},
      {"Data Mining", "Jiawei Han", "2011", "978-0123814791", "Morgan Kaufmann", "25", "25"},
      {"Machine Learning", "Tom Mitchell", "1997", "978-0070428072", "McGraw-Hill", "26", "26"},
      {"Neural Networks", "Simon Haykin", "2008", "978-0262012119", "Prentice Hall", "27", "27"},
      {"Cryptography", "William Stallings", "2013", "978-0133354690", "Pearson", "28", "28"},
      {"Computer Security", "William Stallings", "2014", "978-0133773927", "Pearson", "29", "29"},
      {"Distributed Systems", "Andrew Tanenbaum", "2014", "978-1543057386", "Pearson", "30", "30"},
      {"Cloud Computing", "Rajiv Ranjan", "2020", "978-0367783242", "CRC Press", "31", "31"},
      {"Big Data", "Viktor Mayer-Schonberger", "2013", "978-0544002692", "Houghton Mifflin Harcourt", "32", "32"},
      {"Data Science", "John D Kelleher", "2020", "978-0262535431", "MIT Press", "33", "33"},
      {"Python Programming", "Mark Lutz", "2013", "978-1449355739", "O'Reilly Media", "34", "34"},
      {"JavaScript", "David Flanagan", "2020", "978-1491952023", "O'Reilly Media", "35", "35"},
      {"Effective Java", "Joshua Bloch", "2017", "978-0134685991", "Addison-Wesley", "36", "36"},
      {"Refactoring", "Martin Fowler", "2018", "978-0134757599", "Addison-Wesley", "37", "37"},
      {"Head First Design Patterns", "Eric Freeman", "2004", "978-0596007126", "O'Reilly Media", "38", "38"},
      {"Code Complete", "Steve McConnell", "2004", "978-0735619678", "Microsoft Press", "39", "39"},
      {"The Mythical Man-Month", "Frederick Brooks", "1995", "978-0201835953", "Addison-Wesley", "40", "40"},
      {"Peopleware", "Tom DeMarco", "1999", "978-0932633439", "Dorset House", "41", "41"},
      {"Rapid Development", "Steve McConnell", "1996", "978-1556159008", "Microsoft Press", "42", "42"},
      {"Applying UML and Patterns", "Craig Larman", "2004", "978-0131489066", "Prentice Hall", "43", "43"},
      {"UML Distilled", "Martin Fowler", "2003", "978-0321193681", "Addison-Wesley", "44", "44"},
      {"Extreme Programming Explained", "Kent Beck", "1999", "978-0201616415", "Addison-Wesley", "45", "45"},
      {"Agile Software Development", "Robert Martin", "2002", "978-0135974445", "Prentice Hall", "46", "46"},
      {"Scrum", "Ken Schwaber", "2017", "978-0137580374", "Prentice Hall", "47", "47"},
      {"The DevOps Handbook", "Gene Kim", "2016", "978-1942788003", "IT Revolution", "48", "48"},
      {"Site Reliability Engineering", "Betsy Beyer", "2016", "978-1491929124", "O'Reilly Media", "49", "49"},
      {"Continuous Delivery", "Jez Humble", "2010", "978-0321601919", "Addison-Wesley", "50", "50"},
      {"Microservices", "Sam Newman", "2015", "978-1492035640", "O'Reilly Media", "51", "51"},
      {"Docker", "Adrian Mouat", "2015", "978-1491917619", "O'Reilly Media", "52", "52"},
      {"Kubernetes", "Kelsey Hightower", "2017", "978-1492035718", "O'Reilly Media", "53", "53"},
      {"Algorithm Design Manual", "Steven Skiena", "2010", "978-1849967204", "Springer", "54", "54"},
      {"Data Structures", "Sartaj Sahni", "2003", "978-0070131519", "McGraw-Hill", "55", "55"},
      {"Fundamentals of Data Structures", "Ellis Horowitz", "1993", "978-0716782924", "W H Freeman", "56", "56"},
      {"Algorithm Analysis", "Anany Levitin", "2011", "978-0199747236", "Pearson", "57", "57"},
      {"Computability Complexity", "Christos Papadimitriou", "1993", "978-0070303429", "McGraw-Hill", "58", "58"},
      {"Formal Languages", "John Hopcroft", "2006", "978-0321323218", "Pearson", "59", "59"},
      {"Computational Theory", "Michael Sipser", "2012", "978-1133187790", "Cengage Learning", "60", "60"},
      {"Automata Theory", "Peter Linz", "2019", "978-1284077247", "Jones & Bartlett", "61", "61"},
      {"Logic for Computer Science", "Huth Ryan", "2004", "978-0521834556", "Cambridge University Press", "62", "62"},
      {"Numerical Analysis", "Richard Burden", "2015", "978-1305253667", "Cengage Learning", "63", "63"},
      {"Scientific Computing", "Michael Heath", "2018", "978-0073380223", "McGraw-Hill", "64", "64"},
      {"Parallel Programming", "Barry Wilkinson", "2009", "978-0132445056", "Prentice Hall", "65", "65"},
      {"High Performance Computing", "Charles Severance", "2015", "978-1498712183", "CRC Press", "66", "66"},
      {"Computer Performance", "Neil Gunther", "2007", "978-0387307670", "Springer", "67", "67"},
      {"Queuing Theory", "John Shortle", "2018", "978-1118851686", "Wiley", "68", "68"},
      {"Simulation", "Sheldon Ross", "2012", "978-0124158253", "Academic Press", "69", "69"},
      {"Information Retrieval", "Christopher Manning", "2008", "978-0521865715", "Cambridge University Press", "70", "70"},
      {"Search Engines", "Soumen Chakrabarti", "2003", "978-0321305115", "Addison-Wesley", "71", "71"},
      {"Natural Language Processing", "Daniel Jurafsky", "2000", "978-0131873216", "Prentice Hall", "72", "72"},
      {"Speech Recognition", "Lawrence Rabiner", "2009", "978-0136033042", "Pearson", "73", "73"},
      {"Computer Vision", "David Forsyth", "2011", "978-0131881891", "Prentice Hall", "74", "74"},
      {"Image Processing", "Rafael Gonzalez", "2018", "978-0201593754", "Pearson", "75", "75"},
      {"Digital Signal Processing", "Alan Oppenheim", "1996", "978-0132146357", "Prentice Hall", "76", "76"},
      {"Embedded Systems", "Frank Vahid", "2011", "978-1111436787", "Wiley", "77", "77"},
      {"Real-Time Systems", "Jane Liu", "2000", "978-0130996510", "Prentice Hall", "78", "78"},
      {"Digital Logic", "Morris Mano", "2016", "978-0132861983", "Pearson", "79", "79"},
      {"Microprocessors", "Barry Brey", "2008", "978-0132326490", "Prentice Hall", "80", "80"},
      {"Computer Organization", "Carl Hamacher", "2011", "978-0073529578", "McGraw-Hill", "81", "81"},
      {"VLSI Design", "Weste Harris", "2010", "978-0207016592", "Prentice Hall", "82", "82"},
      {"Computer Networking", "Kurose Ross", "2012", "978-0133594140", "Pearson", "83", "83"},
      {"Wireless Networks", "Kaveh Pahlavan", "2002", "978-0071380320", "McGraw-Hill", "84", "84"},
      {"Mobile Computing", "Frank Adelstein", "2005", "978-0123694434", "Morgan Kaufmann", "85", "85"},
      {"Network Security", "Behrouz Forouzan", "2007", "978-0073324220", "McGraw-Hill", "86", "86"},
      {"Cyber Security", "William Stallings", "2017", "978-0134794105", "Pearson", "87", "87"},
      {"Blockchain", "Donald Patterson", "2019", "978-1119473869", "Wiley", "88", "88"},
      {"Computer Ethics", "Deborah Johnson", "2009", "978-0205745072", "Pearson", "89", "89"},
      {"Software Testing", "Ron Patton", "2005", "978-0672327980", "Sams Publishing", "90", "90"},
      {"Software Quality", "Steve McConnell", "2004", "978-0735619678", "Microsoft Press", "91", "91"},
      {"Software Metrics", "Capers Jones", "1996", "978-0079131064", "McGraw-Hill", "92", "92"},
      {"Requirements Engineering", "Axel van Lamsweerde", "2009", "978-0470019496", "Wiley", "93", "93"},
      {"Project Management", "Harold Kerzner", "2017", "978-1119444562", "Wiley", "94", "94"},
      {"IT Project Management", "Kathy Schwalbe", "2015", "978-1305176409", "Cengage Learning", "95", "95"},
      {"Configuration Management", "Scott Hebbard", "2009", "978-0470292819", "Wiley", "96", "96"},
      {"Version Control", "Jon Loeliger", "2012", "978-1449373184", "O'Reilly Media", "97", "97"},
      {"Git", "Scott Chacon", "2009", "978-0596517984", "O'Reilly Media", "98", "98"},
      {"Unix", "Michael Linux", "2010", "978-0132465887", "Prentice Hall", "99", "99"},
      {"Linux", "William Shotts", "2012", "978-1593272203", "No Starch Press", "100", "100"},
      {"Shell Programming", "Richard Blum", "2010", "978-0071590730", "Sams Publishing", "101", "101"},
      {"System Programming", "John Lions", "1996", "1573980138", "Lions Commentary", "102", "102"},
      {"System Programming", "Randal Bryant", "2015", "978-0134093246", "Pearson", "103", "103"},
    };

    IPax authors = Instances.Factory().produce("authors");
    IPax publications = Instances.Factory().produce("publications");
    IPax booksNode = Instances.Factory().produce("books");

    for (int i = 0; i < bookData.length; i++) {
      String[] book = bookData[i];
      String bookId = book[5];
      String authorId = book[6];

      IPax author = Instances.Factory().produce("author");
      author.Attrib().add("id", authorId);
      author.Child().add("name", book[1]);
      author.Child().add("biography", "Author of " + book[0]);
      authors.Child().add(author);

      IPax publication = Instances.Factory().produce("publication");
      publication.Attrib().add("id", bookId);
      publication.Child().add("publisher", book[4]);
      publication.Child().add("year", book[2]);
      publication.Child().add("edition", "First Edition");
      publications.Child().add(publication);
    }

    String[] lenders = {null, "Alice Johnson", "Bob Smith", "Carol Davis", "David Wilson", null, "Eve Brown", null, "Frank Miller", "Grace Lee"};
    String[] shelves = {"A-1", "A-2", "A-3", "B-1", "B-2", "B-3", "C-1", "C-2", "C-3", "D-1"};

    for (int i = 0; i < bookData.length; i++) {
      String[] book = bookData[i];
      String bookId = book[5];
      String authorId = book[6];
      boolean available = (i % 3 != 0);
      String lender = lenders[i % lenders.length];

      IPax bookNode = Instances.Factory().produce("book");
      bookNode.Attrib().add("id", bookId);
      bookNode.Attrib().add("available", String.valueOf(available));
      if (!available && lender != null) {
        bookNode.Attrib().add("lent_to", lender);
      }

      bookNode.Child().add("title", book[0]);
      bookNode.Child().add("isbn", book[3]);

      IPax authorRef = Instances.Factory().produce("author_ref");
      authorRef.Attrib().add("ref", authorId);
      bookNode.Child().add(authorRef);

      IPax publicationRef = Instances.Factory().produce("publication_ref");
      publicationRef.Attrib().add("ref", bookId);
      bookNode.Child().add(publicationRef);

      bookNode.Child().add("location", "Shelf " + shelves[i % shelves.length]);

      booksNode.Child().add(bookNode);
    }

    library.Child().add(authors);
    library.Child().add(publications);
    library.Child().add(booksNode);

    XmlWriter.Instance.XML(library, "library.xml");

    int authorCount = authors.Child().cnt();
    int publicationCount = publications.Child().cnt();
    int bookCount = booksNode.Child().cnt();

    assertTrue(library.hasChild());
    assertEquals(103, authorCount);
    assertEquals(103, publicationCount);
    assertEquals(103, bookCount);

    List<IPax> foundTitles = library.Child().searchByPath("library/books/book/title");
    assertEquals(103, foundTitles.size());

    List<IPax> foundAuthors = library.Child().searchByPath("library/authors/author/name");
    assertEquals(103, foundAuthors.size());
    assertEquals("Donald Knuth", foundAuthors.get(0).Val());

    List<IPax> foundPublications = library.Child().searchByPath("library/publications/publication/publisher");
    assertEquals(103, foundPublications.size());

    List<IPax> foundLocations = library.Child().searchByPath("library/books/book/location");
    assertEquals(103, foundLocations.size());

    int availableCount = 0;
    int lentCount = 0;
    for (IPax b : booksNode.Child().all()) {
      if (b.Attrib().get("available") != null && b.Attrib().get("available").Val().equals("true")) {
        availableCount++;
      } else if (b.Attrib().get("lent_to") != null) {
        lentCount++;
      }
    }

    System.out.println("Large library test passed:");
    System.out.println("  - " + authorCount + " authors");
    System.out.println("  - " + publicationCount + " publications");
    System.out.println("  - " + bookCount + " books");
    System.out.println("  - " + availableCount + " available");
    System.out.println("  - " + lentCount + " lent out");
  }

  // ====== Location Tests ======

  @Test
  void testSearchByLocation_A1() {
    IPax library = createTestLibrary();
    IPax books = library.Child().get("books");
    
    List<IPax> found = books.Child().searchByPath("books/book/location");
    int countA1 = 0;
    for (IPax book : found) {
      if (book.Val() != null && book.Val().equals("Shelf A-1")) {
        countA1++;
      }
    }
    assertTrue(countA1 >= 1, "Should find books on Shelf A-1");
  }

  @Test
  void testSearchByLocation_A2() {
    IPax library = createTestLibrary();
    IPax books = library.Child().get("books");
    
    List<IPax> found = books.Child().searchByPath("books/book/location");
    int countA2 = 0;
    for (IPax book : found) {
      if (book.Val() != null && book.Val().equals("Shelf A-2")) {
        countA2++;
      }
    }
    assertTrue(countA2 >= 1, "Should find books on Shelf A-2");
  }

  @Test
  void testSearchByLocation_B1() {
    IPax library = createTestLibrary();
    IPax books = library.Child().get("books");
    
    List<IPax> found = books.Child().searchByPath("books/book/location");
    int countB1 = 0;
    for (IPax book : found) {
      if (book.Val() != null && book.Val().equals("Shelf B-1")) {
        countB1++;
      }
    }
    assertTrue(countB1 >= 1, "Should find books on Shelf B-1");
  }

  @Test
  void testSearchByLocation_All() {
    IPax library = createTestLibrary();
    IPax books = library.Child().get("books");
    
    List<IPax> found = books.Child().searchByPath("books/book/location");
    
    String[] shelves = {"A-1", "A-2", "A-3", "B-1", "B-2", "B-3", "C-1", "C-2", "C-3", "D-1"};
    for (String shelf : shelves) {
      int count = 0;
      for (IPax book : found) {
        if (book.Val() != null && book.Val().equals("Shelf " + shelf)) {
          count++;
        }
      }
      assertTrue(count >= 1, "Should find books on Shelf " + shelf);
    }
  }

  // ====== Author Tests ======

  @Test
  void testSearchByAuthor_Knuth() {
    IPax library = createTestLibrary();
    
    List<IPax> found = library.Child().searchByPath("library/authors/author/name");
    boolean foundKnuth = false;
    for (IPax author : found) {
      if ("Donald Knuth".equals(author.Val())) {
        foundKnuth = true;
        break;
      }
    }
    assertTrue(foundKnuth, "Should find Donald Knuth as author");
  }

  @Test
  void testSearchByAuthor_Cormen() {
    IPax library = createTestLibrary();
    
    List<IPax> found = library.Child().searchByPath("library/authors/author/name");
    boolean foundCormen = false;
    for (IPax author : found) {
      if ("Thomas Cormen".equals(author.Val())) {
        foundCormen = true;
        break;
      }
    }
    assertTrue(foundCormen, "Should find Thomas Cormen as author");
  }

  @Test
  void testSearchByAuthor_Tanenbaum() {
    IPax library = createTestLibrary();
    
    List<IPax> found = library.Child().searchByPath("library/authors/author/name");
    int countTanenbaum = 0;
    for (IPax author : found) {
      if ("Andrew Tanenbaum".equals(author.Val())) {
        countTanenbaum++;
      }
    }
    assertTrue(countTanenbaum >= 1, "Should find Andrew Tanenbaum as author");
  }

  @Test
  void testSearchByAuthor_NotFound() {
    IPax library = createTestLibrary();
    
    List<IPax> found = library.Child().searchByPath("library/authors/author/name");
    boolean foundFake = false;
    for (IPax author : found) {
      if ("NonExistent Author".equals(author.Val())) {
        foundFake = true;
        break;
      }
    }
    assertFalse(foundFake, "Should NOT find NonExistent Author");
  }

  // ====== Year Tests ======

  @Test
  void testSearchByYear_1968() {
    IPax library = createTestLibrary();
    
    List<IPax> found = library.Child().searchByPath("library/publications/publication/year");
    int count1968 = 0;
    for (IPax year : found) {
      if ("1968".equals(year.Val())) {
        count1968++;
      }
    }
    assertEquals(1, count1968, "Should find exactly one book from 1968");
  }

  @Test
  void testSearchByYear_2020() {
    IPax library = createTestLibrary();
    
    List<IPax> found = library.Child().searchByPath("library/publications/publication/year");
    int count2020 = 0;
    for (IPax year : found) {
      if ("2020".equals(year.Val())) {
        count2020++;
      }
    }
    assertTrue(count2020 >= 1, "Should find at least one book from 2020");
  }

  @Test
  void testSearchByYear_2000s() {
    IPax library = createTestLibrary();
    
    List<IPax> found = library.Child().searchByPath("library/publications/publication/year");
    int count2000s = 0;
    for (IPax year : found) {
      if (year.Val() != null && year.Val().startsWith("200")) {
        count2000s++;
      }
    }
    assertTrue(count2000s >= 1, "Should find at least 1 book from 2000s");
  }

  @Test
  void testSearchByYear_Distribution() {
    IPax library = createTestLibrary();
    
    List<IPax> found = library.Child().searchByPath("library/publications/publication/year");
    
    int count1968 = 0, count1990s = 0, count2000s = 0, count2010s = 0, count2020s = 0;
    
    for (IPax year : found) {
      String val = year.Val();
      if (val == null) continue;
      
      if (val.equals("1968")) count1968++;
      else if (val.startsWith("199")) count1990s++;
      else if (val.startsWith("200")) count2000s++;
      else if (val.startsWith("201")) count2010s++;
      else if (val.startsWith("202")) count2020s++;
    }
    
    assertEquals(1, count1968, "1968: 1 book");
    assertTrue(count1990s >= 2, "1990s: at least 2 books");
    assertTrue(count2000s >= 2, "2000s: at least 2 books");
    assertTrue(count2010s >= 4, "2010s: at least 4 books");
    assertTrue(count2020s >= 2, "2020s: at least 2 books");
  }

  // ====== Book Title Tests ======

  @Test
  void testSearchByTitle_ArtOfComputerProgramming() {
    IPax library = createTestLibrary();
    
    List<IPax> found = library.Child().searchByPath("library/books/book/title");
    boolean foundTitle = false;
    for (IPax title : found) {
      if ("The Art of Computer Programming".equals(title.Val())) {
        foundTitle = true;
        break;
      }
    }
    assertTrue(foundTitle, "Should find 'The Art of Computer Programming'");
  }

  @Test
  void testSearchByTitle_IntroductionToAlgorithms() {
    IPax library = createTestLibrary();
    
    List<IPax> found = library.Child().searchByPath("library/books/book/title");
    boolean foundTitle = false;
    for (IPax title : found) {
      if ("Introduction to Algorithms".equals(title.Val())) {
        foundTitle = true;
        break;
      }
    }
    assertTrue(foundTitle, "Should find 'Introduction to Algorithms'");
  }

  @Test
  void testSearchByTitle_FirstBook() {
    IPax library = createTestLibrary();
    
    IPax books = library.Child().get("books");
    IPax firstBook = books.Child().get("book");
    
    assertNotNull(firstBook, "Should find first book");
    assertEquals("The Art of Computer Programming", firstBook.Child().get("title").Val());
  }

  @Test
  void testSearchByTitle_NotFound() {
    IPax library = createTestLibrary();
    
    List<IPax> found = library.Child().searchByPath("library/books/book/title");
    boolean foundFake = false;
    for (IPax title : found) {
      if ("Non Existent Book Title".equals(title.Val())) {
        foundFake = true;
        break;
      }
    }
    assertFalse(foundFake, "Should NOT find non-existent title");
  }

  // ====== Publisher Tests ======

  @Test
  void testSearchByPublisher_AddisonWesley() {
    IPax library = createTestLibrary();
    
    List<IPax> found = library.Child().searchByPath("library/publications/publication/publisher");
    int count = 0;
    for (IPax pub : found) {
      if ("Addison-Wesley".equals(pub.Val())) {
        count++;
      }
    }
    assertTrue(count >= 5, "Should find at least 5 Addison-Wesley publications");
  }

  @Test
  void testSearchByPublisher_MITPress() {
    IPax library = createTestLibrary();
    
    List<IPax> found = library.Child().searchByPath("library/publications/publication/publisher");
    int count = 0;
    for (IPax pub : found) {
      if ("MIT Press".equals(pub.Val())) {
        count++;
      }
    }
    assertTrue(count >= 2, "Should find at least 2 MIT Press publications");
  }

  @Test
  void testSearchByPublisher_OReilly() {
    IPax library = createTestLibrary();
    
    List<IPax> found = library.Child().searchByPath("library/publications/publication/publisher");
    int count = 0;
    for (IPax pub : found) {
      if ("O'Reilly Media".equals(pub.Val())) {
        count++;
      }
    }
    assertTrue(count >= 2, "Should find at least 2 O'Reilly Media publications");
  }

  // ====== Attribute Tests ======

  @Test
  void testSearchByAttribute_Available() {
    IPax library = createTestLibrary();
    IPax books = library.Child().get("books");
    
    int availableCount = 0;
    for (IPax book : books.Child().all()) {
      IPax availAttr = book.Attrib().get("available");
      if (availAttr != null && "true".equals(availAttr.Val())) {
        availableCount++;
      }
    }
    assertTrue(availableCount >= 10, "Should have at least 10 available books");
  }

  @Test
  void testSearchByAttribute_Lent() {
    IPax library = createTestLibrary();
    IPax books = library.Child().get("books");
    
    int lentCount = 0;
    for (IPax book : books.Child().all()) {
      IPax lentToAttr = book.Attrib().get("lent_to");
      if (lentToAttr != null && lentToAttr.Val() != null) {
        lentCount++;
      }
    }
    assertTrue(lentCount >= 1, "Should have at least 1 lent book");
  }

  @Test
  void testSearchByAttribute_BookById() {
    IPax library = createTestLibrary();
    IPax books = library.Child().get("books");
    
    IPax book1 = null;
    for (IPax book : books.Child().all()) {
      IPax idAttr = book.Attrib().get("id");
      if (idAttr != null && "1".equals(idAttr.Val())) {
        book1 = book;
        break;
      }
    }
    
    assertNotNull(book1, "Should find book with id=1");
    assertEquals("The Art of Computer Programming", book1.Child().get("title").Val());
  }

  @Test
  void testSearchByAttribute_AuthorById() {
    IPax library = createTestLibrary();
    IPax authors = library.Child().get("authors");
    
    IPax author1 = null;
    for (IPax author : authors.Child().all()) {
      IPax idAttr = author.Attrib().get("id");
      if (idAttr != null && "1".equals(idAttr.Val())) {
        author1 = author;
        break;
      }
    }
    
    assertNotNull(author1, "Should find author with id=1");
    assertEquals("Donald Knuth", author1.Child().get("name").Val());
  }

  @Test
  void testSearchByAttribute_LentToNames() {
    IPax library = createTestLibrary();
    IPax books = library.Child().get("books");
    
    String[] expectedLenders = {"Carol Davis", "Eve Brown", "Grace Lee", "Bob Smith", "Frank Miller"};
    
    for (String lender : expectedLenders) {
      boolean found = false;
      for (IPax book : books.Child().all()) {
        IPax lentToAttr = book.Attrib().get("lent_to");
        if (lentToAttr != null && lender.equals(lentToAttr.Val())) {
          found = true;
          break;
        }
      }
      assertTrue(found, "Should find book lent to " + lender);
    }
  }

  // ====== Helper Method ======

  private IPax createTestLibrary() {
    IPax library = Instances.Factory().produce("library");

    String[][] bookData = {
      {"The Art of Computer Programming", "Donald Knuth", "1968", "978-0201896831", "Addison-Wesley", "1", "1"},
      {"Introduction to Algorithms", "Thomas Cormen", "2009", "978-0262033848", "MIT Press", "2", "2"},
      {"Design Patterns", "Erich Gamma", "1994", "978-0201633610", "Addison-Wesley", "3", "3"},
      {"Clean Code", "Robert Martin", "2008", "978-0132350884", "Prentice Hall", "4", "4"},
      {"The Pragmatic Programmer", "Andrew Hunt", "1999", "978-0201616224", "Addison-Wesley", "5", "5"},
      {"Structure and Interpretation of Computer Programs", "Harold Abelson", "1996", "978-0262510871", "MIT Press", "6", "6"},
      {"Algorithms", "Robert Sedgewick", "2011", "978-0321573513", "Addison-Wesley", "7", "7"},
      {"Computer Systems", "Randal Bryant", "2015", "978-0134092669", "Pearson", "8", "8"},
      {"Operating System Concepts", "Abraham Silberschatz", "2012", "978-1118063330", "Wiley", "9", "9"},
      {"Database System Concepts", "Abraham Silberschatz", "2010", "978-0078022159", "McGraw-Hill", "10", "10"},
      {"Computer Networks", "Andrew Tanenbaum", "2010", "978-0132126953", "Prentice Hall", "11", "11"},
      {"Artificial Intelligence", "Stuart Russell", "2020", "978-0136042594", "Pearson", "12", "12"},
      {"Deep Learning", "Ian Goodfellow", "2016", "978-0262035613", "MIT Press", "13", "13"},
      {"The Elements of Statistical Learning", "Trevor Hastie", "2009", "978-0387848570", "Springer", "14", "14"},
      {"Pattern Recognition", "Bishop", "2006", "978-0387310732", "Springer", "15", "15"},
      {"Computer Graphics", "James Foley", "1995", "978-0201847603", "Addison-Wesley", "16", "16"},
      {"Python Programming", "Mark Lutz", "2013", "978-1449355739", "O'Reilly Media", "17", "17"},
      {"JavaScript", "David Flanagan", "2020", "978-1491952023", "O'Reilly Media", "18", "18"},
      {"Effective Java", "Joshua Bloch", "2017", "978-0134685991", "Addison-Wesley", "19", "19"},
      {"Refactoring", "Martin Fowler", "2018", "978-0134757599", "Addison-Wesley", "20", "20"},
    };

    IPax authors = Instances.Factory().produce("authors");
    IPax publications = Instances.Factory().produce("publications");
    IPax booksNode = Instances.Factory().produce("books");

    for (int i = 0; i < bookData.length; i++) {
      String[] book = bookData[i];
      String bookId = book[5];
      String authorId = book[6];

      IPax author = Instances.Factory().produce("author");
      author.Attrib().add("id", authorId);
      author.Child().add("name", book[1]);
      author.Child().add("biography", "Author of " + book[0]);
      authors.Child().add(author);

      IPax publication = Instances.Factory().produce("publication");
      publication.Attrib().add("id", bookId);
      publication.Child().add("publisher", book[4]);
      publication.Child().add("year", book[2]);
      publication.Child().add("edition", "First Edition");
      publications.Child().add(publication);
    }

    String[] lenders = {null, "Alice Johnson", "Bob Smith", "Carol Davis", "David Wilson", null, "Eve Brown", null, "Frank Miller", "Grace Lee"};
    String[] shelves = {"A-1", "A-2", "A-3", "B-1", "B-2", "B-3", "C-1", "C-2", "C-3", "D-1"};

    for (int i = 0; i < bookData.length; i++) {
      String[] book = bookData[i];
      String bookId = book[5];
      String authorId = book[6];
      boolean available = (i % 3 != 0);
      String lender = lenders[i % lenders.length];

      IPax bookNode = Instances.Factory().produce("book");
      bookNode.Attrib().add("id", bookId);
      bookNode.Attrib().add("available", String.valueOf(available));
      if (!available && lender != null) {
        bookNode.Attrib().add("lent_to", lender);
      }

      bookNode.Child().add("title", book[0]);
      bookNode.Child().add("isbn", book[3]);

      IPax authorRef = Instances.Factory().produce("author_ref");
      authorRef.Attrib().add("ref", authorId);
      bookNode.Child().add(authorRef);

      IPax publicationRef = Instances.Factory().produce("publication_ref");
      publicationRef.Attrib().add("ref", bookId);
      bookNode.Child().add(publicationRef);

      bookNode.Child().add("location", "Shelf " + shelves[i % shelves.length]);

      booksNode.Child().add(bookNode);
    }

    library.Child().add(authors);
    library.Child().add(publications);
    library.Child().add(booksNode);

    return library;
  }
}
