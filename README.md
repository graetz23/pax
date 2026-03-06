# PAX
A Java object tree implementation based on the _Composite Pattern_ combined with a _tolerant XML reader_ for reading
and writing any Extensible Markup Language (XML) structure - no need for an XML Schema Definition XSD.

```
  ███████████    █████████   █████ █████
 ▒▒███▒▒▒▒▒███  ███▒▒▒▒▒███ ▒▒███ ▒▒███ 
  ▒███    ▒███ ▒███    ▒███  ▒▒███ ███  
  ▒██████████  ▒███████████   ▒▒█████   
  ▒███▒▒▒▒▒▒   ▒███▒▒▒▒▒███    ███▒███  
  ▒███         ▒███    ▒███   ███ ▒▒███ 
 █████        █████   █████ █████ █████
▒▒▒▒▒        ▒▒▒▒▒   ▒▒▒▒▒ ▒▒▒▒▒ ▒▒▒▒▒ 
```

## Introduction
Each created object can be stored as a node in a tree. There are no special types. Any object can start a fresh tree by
being root. A root can store _children_. Those can be _added_ or _set_. Any children can be fetched _by tag_ or in case
of _many_ with the same name, _by listing and filtering_. Each object can also store attributes. Those are organized in
the same way as child but are used as a list. By any object, the tree can be recursed and generated to any data format.
Currently, the generation of XML is implemented only.

## Installation
```bash
./gradlew build jar
```

## Factory (Instances)
Get the factory to create new nodes:
```java
IFactory factory = Instances.Factory();
```

### produce(tag)
Create a new node with a tag name:
```java
IPax root = Instances.Factory().produce("root");
```

### produce(tag, val)
Create a new node with a tag and value:
```java
IPax element = Instances.Factory().produce("title", "Effective Java");
```

### copy(ipax)
Create a deep copy of an existing node:
```java
IPax original = Instances.Factory().produce("book");
IPax copy = Instances.Factory().copy(original);
```

---

## IPax Interface

All node operations are available through the `IPax` interface.

### Tag Operations

#### Tag() - Get the tag name
```java
IPax root = Instances.Factory().produce("book");
String tag = root.Tag(); // returns "book"
```

#### Tag(String tag) - Set the tag name
```java
IPax root = Instances.Factory().produce("root");
root.Tag("book"); // changes tag to "book"
```

#### hasTag() - Check if tag exists
```java
IPax node = Instances.Factory().produce("element");
boolean hasTag = node.hasTag(); // true

IPax noTag = Instances.Factory().produce(null);
boolean empty = noTag.hasTag(); // false
```

### Value Operations

#### Val() - Get the value
```java
IPax title = Instances.Factory().produce("title", "My Book");
String value = title.Val(); // returns "My Book"
```

#### Val(String val) - Set the value
```java
IPax element = Instances.Factory().produce("description");
element.Val("A great book");
```

#### hasVal() - Check if value exists
```java
IPax element = Instances.Factory().produce("empty");
boolean hasValue = element.hasVal(); // false

element.Val("content");
hasValue = element.hasVal(); // true
```

Note: Empty, blank, or newline-only values are rejected and stored as null.

### Parent Operations

#### Parent() - Get the parent node
```java
IPax parent = Instances.Factory().produce("parent");
IPax child = Instances.Factory().produce("child");
parent.Child().add(child);

IPax foundParent = child.Parent(); // returns parent
```

#### Parent(IPax parent) - Set the parent
```java
IPax parent = Instances.Factory().produce("parent");
IPax child = Instances.Factory().produce("child");
child.Parent(parent);
```

#### hasParent() - Check if has parent
```java
IPax root = Instances.Factory().produce("root");
boolean hasParent = root.hasParent(); // false

IPax child = Instances.Factory().produce("child");
root.Child().add(child);
hasParent = child.hasParent(); // true
```

### Path Operations

#### Path() - Get the full path from root
```java
IPax root = Instances.Factory().produce("library");
IPax book = Instances.Factory().produce("book");
root.Child().add(book);
IPax chapter = Instances.Factory().produce("chapter");
book.Child().add(chapter);

String path = chapter.Path(); // returns "/library/book/chapter"
```

### Child Operations

#### Child() - Get children collection
```java
IChildren children = root.Child();
```

#### hasChild() - Check if has children
```java
boolean has = root.hasChild();
```

### Attribute Operations

#### Attrib() - Get attributes collection
```java
IAttributes attributes = root.Attrib();
```

#### hasAttrib() - Check if has attributes
```java
boolean has = root.hasAttrib();
```

### XML Generation

#### XML() - Generate XML with indentation
```java
IPax root = Instances.Factory().produce("book");
root.Attrib().add("id", "1");
IPax title = Instances.Factory().produce("title", "Effective Java");
root.Child().add(title);

String xml = root.XML();
/*
<book id="1">
  <title>Effective Java</title>
</book>
*/
```

#### XML_lined() - Generate XML on single line
```java
String xml = root.XML_lined(); // <book id="1"><title>Effective Java</title></book>
```

---

## IChildren Interface

Manage child nodes.

### add(String tag) - Add child by tag name
```java
root.Child().add("chapter");
```

### add(String tag, String val) - Add child with value
```java
root.Child().add("author", "Joshua Bloch");
```

### add(IPax child) - Add existing node as child
```java
IPax chapter = Instances.Factory().produce("chapter", "Introduction");
root.Child().add(chapter);
```

### get(String tag) - Get first child by tag
```java
IPax chapter = root.Child().get("chapter");
```

### get(int index) - Get child by index
```java
IPax first = root.Child().get(0);
```

### has(String tag) - Check if child exists
```java
boolean exists = root.Child().has("chapter");
```

### all() - Get all children
```java
List<IPax> allChildren = root.Child().all();
```

### all(String tag) - Get all children with specific tag
```java
List<IPax> chapters = root.Child().all("chapter");
```

### cnt() - Get children count
```java
int count = root.Child().cnt();
```

### del(String tag) - Delete first child by tag
```java
root.Child().del("chapter");
```

### del(IPax child) - Delete specific child
```java
root.Child().del(chapter);
```

### del() - Delete all children
```java
root.Child().del();
```

### set(String tag, String val) - Set or update child value
```java
root.Child().set("title", "New Title");
```

### set(IPax child) - Set or update child
```java
root.Child().set(newChapter);
```

### search(String path) - Search by XPath-like path
```java
IPax found = root.Child().search("/library/book/chapter");
IPax foundRelative = root.Child().search("./book/chapter");
```

---

## IAttributes Interface

Manage attributes on a node.

### add(String tag, String val) - Add attribute
```java
root.Attrib().add("id", "123");
root.Attrib().add("type", "novel");
```

### add(IPax attribute) - Add attribute node
```java
IPax attr = Instances.Factory().produce("class", "primary");
root.Attrib().add(attr);
```

### get(String tag) - Get attribute by name
```java
IPax id = root.Attrib().get("id");
String value = id.Val(); // "123"
```

### has(String tag) - Check if attribute exists
```java
boolean hasId = root.Attrib().has("id");
```

### all() - Get all attributes
```java
List<IPax> attrs = root.Attrib().all();
```

### cnt() - Get attribute count
```java
int count = root.Attrib().cnt();
```

### del(String tag) - Delete attribute
```java
root.Attrib().del("id");
```

### del() - Delete all attributes
```java
root.Attrib().del();
```

### XML() - Generate attributes as XML string
```java
String attrXml = root.Attrib().XML(); // returns "id=\"123\" type=\"novel\" "
```

---

## XmlReader

Parse XML from various sources.

### XmlReader.Instance - Get singleton instance
```java
XmlReader reader = XmlReader.Instance;
```

### parse(String filename) - Parse from file path
```java
IPax root = XmlReader.Instance.parse("./config.xml");
```

### parseLocalFile(String filename) - Parse local file
```java
IPax root = XmlReader.Instance.parseLocalFile("data.xml");
```

### stream(InputStream stream) - Parse from InputStream
```java
String xml = "<book><title>Java</title></book>";
InputStream is = new ByteArrayInputStream(xml.getBytes());
IPax root = XmlReader.Instance.stream(is);
```

---

## XmlWriter

Write IPax trees to files.

### XmlWriter.Instance - Get singleton instance
```java
XmlWriter writer = XmlWriter.Instance;
```

### XML(IPax root) - Write to file using tag as filename
```java
IPax book = Instances.Factory().produce("book");
book.Val("Content");
boolean success = XmlWriter.Instance.XML(book); // writes to "book.xml"
```

### XML(IPax root, String filename) - Write to specified file
```java
boolean success = XmlWriter.Instance.XML(book, "mybook.xml");
boolean success2 = Writer.Instance.XML(book, "output"); // adds .xml automatically
```

---

## Complete Examples

### Creating a Document
```java
// Create root
IPax library = Instances.Factory().produce("library");

// Add attributes
library.Attrib().add("name", "City Library");
library.Attrib().add("location", "Downtown");

// Add children
IPax book1 = Instances.Factory().produce("book");
book1.Attrib().add("id", "1");
book1.Child().add("title", "Effective Java");
book1.Child().add("author", "Joshua Bloch");
library.Child().add(book1);

IPax book2 = Instances.Factory().produce("book");
book2.Attrib().add("id", "2");
book2.Child().add("title", "Clean Code");
book2.Child().add("author", "Robert Martin");
library.Child().add(book2);

// Generate XML
String xml = library.XML();
System.out.println(xml);
```

Output:
```xml
<library name="City Library" location="Downtown">
  <book id="1">
    <title>Effective Java</title>
    <author>Joshua Bloch</author>
  </book>
  <book id="2">
    <title>Clean Code</title>
    <author>Robert Martin</author>
  </book>
</library>
```

### Reading and Modifying XML
```java
// Read from file
IPax library = XmlReader.Instance.parse("library.xml");

// Find specific book using search
IPax book = library.Child().search("/library/book");

// Add new child
IPax review = Instances.Factory().produce("review", "Excellent!");
book.Child().add(review);

// Write back to file
XmlWriter.Instance.XML(library, "library_updated.xml");
```

### Copying Nodes
```java
IPax original = Instances.Factory().produce("book");
original.Attrib().add("id", "1");
original.Child().add("title", "Original");

IPax copy = Instances.Factory().copy(original);
copy.Attrib().add("id", "2");
copy.Child().get("title").Val("Copy");

// Both nodes exist independently
```

## Build

Gradle Wrapper is generated in version 9; try:
```bash
./gradlew build jar
```

If you need to settle another version of the wrapper, get the latest gradle version and [install it](https://gradle.org/install/#manually).

The change to your cloned directory of jPAX and
```bash
gradle wrapper
./gradlew build
```

## License
MIT License - See LICENSE file
