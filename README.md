# PAX

A Java object tree implementation based on the _Composite Pattern_ combined with a _tolerant XML/JSON reader_ for reading and writing any Extensible Markup Language (XML) or JavaScript Object Notation (JSON) structure - no need for an XML Schema Definition XSD.

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

Each created object can be stored as a node in a tree. There are no special types. Any object can start a fresh tree by being root. A root can store _children_. Those can be _added_ or _set_. Any children can be fetched _by tag_ or in case of _many_ with the same name, _by listing and filtering_. Each object can also store attributes. Those are organized in the same way as children but are used as a list. By any object, the tree can be recursed and generated to any data format. Both XML and JSON formats are supported.

## Features

- **XML Read/Write** - SAX-based parser for reading XML, generator for writing indented or compact XML
- **JSON Read/Write** - Parse and generate JSON using the PAX JSON format
- **XPath-like Search** - Navigate trees with absolute (`/root/child/grandchild`) and relative (`./child/sibling`) paths
- **Deep Copy** - Create independent copies of entire subtrees
- **No Schema Required** - Tolerant reader parses any well-formed XML/JSON without needing an XSD
- **Lazy Initialization** - Children and attributes collections are created on-demand for memory efficiency
- **Duplicate Handling** - Multiple siblings with the same tag are supported via synthetic keys

## Requirements

- Java 21 or higher
- Gradle 9.x (wrapper included)

## Installation

```bash
./gradlew build jar
```

This creates the JAR file in `build/libs/`.

## Quick Start

```java
// Create a simple node tree
IPax root = Instances.Factory().produce("library");
root.Attrib().add("name", "My Library");

// Add children
IPax book = Instances.Factory().produce("book");
book.Child().add("title", "Effective Java");
root.Child().add(book);

// Generate XML
String xml = root.XML();
System.out.println(xml);

// Generate JSON
String json = root.JSON();
System.out.println(json);
```

Output:
```xml
<library name="My Library">
  <book>
    <title>Effective Java</title>
  </book>
</library>
```

```json
{"__tag__": "library", "__attributes__": [{"name": "name", "value": "My Library"}], "__children__": {"book": {"__tag__": "book", "__children__": {"title": {"__tag__": "title", "__value__": "Effective Java"}}}}}
```

---

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

### JSON Generation

#### JSON() - Generate JSON string

```java
IPax root = Instances.Factory().produce("book");
root.Attrib().add("id", "1");
IPax title = Instances.Factory().produce("title", "Effective Java");
root.Child().add(title);

String json = root.JSON();
/*
{"__tag__": "book", "__attributes__": [{"name": "id", "value": "1"}], 
 "__children__": {"title": {"__tag__": "title", "__value__": "Effective Java"}}}
*/
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

### First() - Get first child

```java
IPax first = root.Child().First();
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

### searchByTag(String tag) - Recursively search by tag name

```java
// Find all <author> nodes anywhere in the tree
List<IPax> allAuthors = root.Child().searchByTag("author");
```

### searchByPath(String path) - Search by path, return all matches

```java
// Find all <chapter> nodes under <book> under <library>
List<IPax> chapters = root.Child().searchByPath("/library/book/chapter");
```

### typed() - Get children as typed list

```java
List<Book> books = root.Child().typed(); // assuming all children are Book instances
```

### typed(String tag) - Get children by tag as typed list

```java
List<Book> books = root.Child().typed("book");
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
boolean success2 = XmlWriter.Instance.XML(book, "output"); // adds .xml automatically
```

---

## JsonReader

Parse JSON from various sources. Uses the PAX JSON format.

### JsonReader.Instance - Get singleton instance

```java
JsonReader reader = JsonReader.Instance;
```

### parse(String filename) - Parse from file path

```java
IPax root = JsonReader.Instance.parse("./data.json");
```

### stream(InputStream stream) - Parse from InputStream

```java
String json = "{\"__tag__\": \"book\", \"__value__\": \"Content\"}";
InputStream is = new ByteArrayInputStream(json.getBytes());
IPax root = JsonReader.Instance.stream(is);
```

### parseJson(String json) - Parse from string

```java
String json = "{\"__tag__\": \"library\", \"__children__\": {\"book\": {\"__tag__\": \"book\"}}}";
IPax root = JsonReader.Instance.parseJson(json);
```

---

## JsonGenerator

The PAX JSON format uses four reserved keys:
- `__tag__` - the XML tag name
- `__value__` - the text content, if present
- `__attributes__` - an array of `{name, value}` objects
- `__children__` - a map of child tag to child object or array

When multiple sibling children share the same tag they are grouped into a JSON array.

### IPax.JSON() - Generate JSON

```java
String json = root.JSON();
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

### JSON Round-Trip

```java
import java.io.FileWriter;

// Create a tree
IPax root = Instances.Factory().produce("library");
root.Attrib().add("name", "My Library");

IPax book = Instances.Factory().produce("book");
book.Attrib().add("id", "1");
book.Child().add("title", "Effective Java");
root.Child().add(book);

// Generate JSON
String json = root.JSON();
System.out.println(json);

// Write JSON to file using standard Java I/O
try (FileWriter writer = new FileWriter("library.json")) {
    writer.write(json);
}

// Read JSON back from file
IPax loaded = JsonReader.Instance.parse("library.json");

// Verify round-trip
String xml = loaded.XML();
System.out.println(xml);
```

---

## Build

### Build the project

```bash
./gradlew build
```

### Build JAR only

```bash
./gradlew jar
```

### Run tests

```bash
./gradlew test
```

### Clean build

```bash
./gradlew clean build
```

The JAR file will be created in `build/libs/`.

**Note:** The Gradle wrapper is version 9. If you need a different version, see the [Gradle installation guide](https://gradle.org/install/#manually).

---

## License

MIT License - See LICENSE file
