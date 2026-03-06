/**
 * @brief pax
 * @details A Java written generator for plain old XML (POX) data domains
 * @copyright Copyright (c) 2017-2026 Christian (graetz23@gmail.com)
 * @author Christian (graetz23@gmail.com)
 * @file Reader.java
 *
 * Singleton SAX-based XML parser that converts an XML document into a
 * {@link IPax} node tree. The active {@link Instances#Factory()} is
 * consulted for every element tag, so that registered domain factories
 * (e.g. a generated {@code PaxFactory}) automatically produce typed
 * subclasses during parsing.
 *
 * <p>Three entry points are provided to cover the most common input
 * sources:</p>
 * <ul>
 *   <li>{@link #parse(String)} – opens a file by path via
 *       {@link java.io.FileInputStream}</li>
 *   <li>{@link #parseLocalFile(String)} – passes the filename directly
 *       to the SAX parser (resolved relative to the working directory)</li>
 *   <li>{@link #stream(InputStream)} – parses from any
 *       {@link java.io.InputStream}</li>
 * </ul>
 *
 * <p>XML comments are captured via a {@link org.xml.sax.ext.LexicalHandler}
 * and stored as child nodes with the tag {@link Identity#COMMENT}.</p>
 *
 * <p>The parser validates that the document has exactly one root element
 * and that all elements are properly closed; violations raise a
 * {@link org.xml.sax.SAXException}.</p>
 *
 * @see IPax
 * @see Instances
 * @see IFactory
 */

package de.graetz23.pax;

import java.io.FileInputStream;
import java.io.InputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class Reader {

  /**
   * The singleton instance. Use this field to call parsing methods, e.g.
   * {@code Reader.Instance.parseLocalFile("library.xml")}.
   */
  public static Reader Instance = new Reader();

  /**
   * Private constructor enforcing the singleton pattern.
   */
  private Reader() {
  } // constructor

  /**
   * Parses the XML file at the given file-system path by opening it as a
   * {@link FileInputStream} and delegating to the SAX engine. Returns
   * {@code null} and prints a stack trace if the file cannot be found or
   * the XML is malformed.
   *
   * @param filename the path to the XML file; resolved by the OS
   * @return the root {@link IPax} of the parsed tree, or {@code null} on
   *         any error
   */
  public IPax parse(String filename) {

    IPax root = null;
    SAXParserFactory factory = SAXParserFactory.newInstance();

    try {
      SAXParser parser = factory.newSAXParser();
      IPaxHandler handler = new IPaxHandler();
      parser.getXMLReader().setProperty(handler.LexicalHandlerProperty(), handler.LexicalHandler());
      FileInputStream fileInputStream = new FileInputStream(filename); // open file
      if (fileInputStream != null) {
        parser.parse(fileInputStream, handler);
        root = handler.getRoot();
      } else {
        System.out.println("InputStream is null - no file found");
      } //
    } catch (Exception exception) {
      exception.printStackTrace();
    } // try

    return root;
  } // method

  /**
   * Parses the XML file with the given name by passing it directly to the
   * SAX parser (resolved relative to the JVM working directory). This is
   * the preferred method for tests and the code generator, where the file
   * lives in the project root.
   *
   * @param filename the name of the XML file relative to the working
   *                 directory, e.g. {@code "library.xml"}
   * @return the root {@link IPax} of the parsed tree, or {@code null} on
   *         any error
   */
  public IPax parseLocalFile(String filename) {

    IPax root = null;
    SAXParserFactory factory = SAXParserFactory.newInstance();
    try {
      SAXParser parser = factory.newSAXParser();
      IPaxHandler handler = new IPaxHandler();
      parser.getXMLReader().setProperty(handler.LexicalHandlerProperty(), handler.LexicalHandler());
      parser.parse(filename, handler);
      root = handler.getRoot();
    } catch (Exception exception) {
      exception.printStackTrace();
    } // try

    return root;
  } // method

  /**
   * Parses XML from the given {@link InputStream}. Useful when the XML
   * content originates from a network connection, classpath resource, or
   * in-memory buffer rather than a file on disk.
   *
   * @param stream the input stream carrying the XML content; must not be
   *               {@code null}
   * @return the root {@link IPax} of the parsed tree, or {@code null} on
   *         any error
   */
  public IPax stream(InputStream stream) {

    IPax root = null;
    SAXParserFactory factory = SAXParserFactory.newInstance();

    try {
      SAXParser parser = factory.newSAXParser();
      IPaxHandler handler = new IPaxHandler();
      parser.getXMLReader().setProperty(handler.LexicalHandlerProperty(), handler.LexicalHandler());
      if (stream != null) {
        parser.parse(stream, handler);
        root = handler.getRoot();
      } else {
        System.out.println("InputStream is null - no file found");
      } //
    } catch (Exception exception) {
      exception.printStackTrace();
    } // try

    return root;
  } // method


  /**
   * SAX content and lexical handler that converts SAX events into a
   * {@link IPax} node tree. Maintains a hierarchy-level counter and a
   * {@code _parent} pointer to track the current insertion point as
   * elements are opened and closed.
   *
   * <p>Element nodes are created via {@link Instances#Factory()#produce(String)}
   * so that domain-specific typed subclasses are returned when a
   * {@code PaxFactory} is registered.</p>
   *
   * <p>Validates that the document has exactly one root element
   * ({@link #startElement}) and that all elements are properly closed
   * ({@link #endDocument}).</p>
   */
  private class IPaxHandler extends DefaultHandler {

    /** Current nesting depth; -1 means before the root element. */
    private int _hierarchyLevel = -1; // member

    /** Guards against documents with multiple root elements. */
    private boolean _wasRootFound = false;

    /** Points to the element currently being parsed; acts as a stack top. */
    private IPax _parent = null; // member

    /** The SAX lexical handler that captures XML comments. */
    private LexicalHandler _lexicalHandler = null;

    /** The parsed root node; returned by {@link #getRoot()} after parsing. */
    private IPax _root = null; // member

    /**
     * Constructs the handler and wires the {@link IPaxLexicalHandler}
     * for comment capture.
     */
    public IPaxHandler() {
      _lexicalHandler = new IPaxLexicalHandler(this);
    } // method

    /**
     * Returns the lexical handler to be registered with the SAX parser
     * via {@code XMLReader.setProperty}.
     *
     * @return the {@link LexicalHandler} for comment capture
     */
    public LexicalHandler LexicalHandler() {
      return _lexicalHandler;
    } // method

    /**
     * Returns the SAX property name used to register a
     * {@link LexicalHandler} with an {@link org.xml.sax.XMLReader}.
     *
     * @return the standard lexical-handler property URI
     */
    public String LexicalHandlerProperty() {
      return "http://xml.org/sax/properties/lexical-handler";
    } // method

    /**
     * Returns the root node of the parsed tree. Call this after parsing
     * is complete.
     *
     * @return the root {@link IPax}, or {@code null} if parsing failed
     *         before any root element was encountered
     */
    public IPax getRoot() {
      return _root;
    } // method

    /**
     * Resets internal state at the start of a new document so that this
     * handler instance can be reused.
     *
     * @throws SAXException never; declared by the SAX API
     */
    @Override
    public void startDocument() throws SAXException {
      _hierarchyLevel = -1;
      _wasRootFound = false;
      _parent = null;
      _root = null;
    } // method

    /**
     * Validates that the document was properly closed by confirming the
     * hierarchy level has returned to {@code -1}.
     *
     * @throws SAXException if unclosed elements remain
     */
    @Override
    public void endDocument() throws SAXException {
      if (_hierarchyLevel != -1) { // check for staring value
        String message = this.getClass().getSimpleName();
        message += " - ";
        message += "XML document is not closed, hierarchy ended at level: ";
        message += _hierarchyLevel + 1; // add one up
        throw new SAXException(message);
      } // if
    } // method

    /**
     * Called by the SAX engine when an opening tag is encountered.
     * Creates the node via the active factory, sets up the parent-child
     * relationship, copies all XML attributes onto the node, and advances
     * the parent pointer.
     *
     * @param uri        the namespace URI (unused)
     * @param localName  the local name (unused)
     * @param qName      the qualified XML tag name used to produce the node
     * @param attributes the SAX attributes of the element
     * @throws SAXException if a second root element is encountered
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
        throws SAXException {
      _hierarchyLevel++; // one hierarchy up: 0, 1, 2, 3, ..

      IPax Pax = Instances.Factory().produce(qName); // typed new one, if tag is known

      if (_hierarchyLevel == 0) { // only for the root case
        if (_wasRootFound) {
          String message = this.getClass().getSimpleName();
          message += " - ";
          message += "XML document is keeping a 2nd XML root node";
          throw new SAXException(message);
        } // if
        _wasRootFound = true; // look for single root

        _root = Pax; // settle this as the root node

      } else { // all other cases
        _parent.Child().add(Pax); // new is child of parent
      } // if

      if (attributes.getLength() > 0) { // settle all attributes
        for (int i = 0; i < attributes.getLength(); i++) {
          String attr = attributes.getQName(i);
          String value = attributes.getValue(i);
          Pax.Attrib().add(attr, value);
        } // loop
      } // if

      _parent = Pax; // settle new as the next parent
    } // method

    /**
     * Called by the SAX engine when character data is encountered.
     * Updates the text value of the current parent node. Multiple
     * {@code characters} callbacks for the same element are allowed by
     * the SAX spec; each call may overwrite or extend the stored value
     * depending on {@link IPax#Val(String)}'s validation logic.
     *
     * @param ch     the character buffer
     * @param start  the start offset in the buffer
     * @param length the number of characters to read
     * @throws SAXException never; declared by the SAX API
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
      String val = new String(ch, start, length);
      _parent.Val(val); // update value of current
    } // method

    /**
     * Called by the SAX engine when a closing tag is encountered.
     * Decrements the hierarchy level and walks the parent pointer up
     * one level so the next sibling or parent element becomes current.
     *
     * @param uri       the namespace URI (unused)
     * @param localName the local name (unused)
     * @param qName     the qualified tag name (unused)
     * @throws SAXException never; declared by the SAX API
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
      _hierarchyLevel--; // one hierarchy down: .. 3, 2, 1, 0
      _parent = _parent.Parent(); // get one up for new ones on level higher
    } // method

    /**
     * SAX {@link LexicalHandler} implementation that captures XML comments
     * and stores them as child nodes with tag {@link Identity#COMMENT}.
     * All other lexical events (DTD, entities, CDATA boundaries) are
     * intentionally ignored.
     */
    public class IPaxLexicalHandler implements LexicalHandler {

      /** Reference to the enclosing SAX handler for state access. */
      private IPaxHandler _handler = null;

      /**
       * Constructs the lexical handler bound to the given SAX handler.
       *
       * @param handler the enclosing {@link IPaxHandler}
       */
      public IPaxLexicalHandler(IPaxHandler handler) {
        _handler = handler;
      } // constructor

      /**
       * Called at the start of a DTD declaration. Not used by PAX;
       * implementation is intentionally empty.
       *
       * @param name     the document type name
       * @param publicId the declared public identifier
       * @param systemId the declared system identifier
       * @throws SAXException never
       */
      @Override
      public void startDTD(String name, String publicId, String systemId) throws SAXException {
      } // method

      /**
       * Called at the end of a DTD declaration. Not used by PAX;
       * implementation is intentionally empty.
       *
       * @throws SAXException never
       */
      @Override
      public void endDTD() throws SAXException {
      } // method

      /**
       * Called at the start of an entity reference. Not used by PAX;
       * implementation is intentionally empty.
       *
       * @param name the entity name
       * @throws SAXException never
       */
      @Override
      public void startEntity(String name) throws SAXException {
      } // method

      /**
       * Called at the end of an entity reference. Not used by PAX;
       * implementation is intentionally empty.
       *
       * @param name the entity name
       * @throws SAXException never
       */
      @Override
      public void endEntity(String name) throws SAXException {
      } // method

      /**
       * Called at the start of a CDATA section. Not used by PAX;
       * implementation is intentionally empty.
       *
       * @throws SAXException never
       */
      @Override
      public void startCDATA() throws SAXException {
      } // method

      /**
       * Called at the end of a CDATA section. Not used by PAX;
       * implementation is intentionally empty.
       *
       * @throws SAXException never
       */
      @Override
      public void endCDATA() throws SAXException {
      } // method

      /**
       * Called by the SAX engine when an XML comment is encountered.
       * If parsing is currently inside the document (hierarchy level
       * {@code > -1}), adds the comment text as a child node of the
       * current parent using tag {@link Identity#COMMENT}.
       *
       * @param ch     the character buffer containing the comment text
       * @param start  the start offset in the buffer
       * @param length the number of characters to read
       * @throws SAXException never; declared by the SAX API
       */
      @Override
      public void comment(char[] ch, int start, int length) throws SAXException {
        if (_handler._hierarchyLevel > -1) {
          IPax parent = _handler._parent;
          parent.Child().add(Identity.COMMENT, new String(ch, start, length));
        } // if
      } // method

    } // class
  } // class
} // class
