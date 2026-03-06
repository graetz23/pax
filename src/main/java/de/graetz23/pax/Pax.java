/**
 * @brief pax
 * @details A Java written generator for plain old XML (POX) data domains
 * @copyright Copyright (c) 2017-2026 Christian (graetz23@gmail.com)
 * @author Christian (graetz23@gmail.com)
 * @file Pax.java
 *
 * Concrete base implementation of {@link IPax}. Every node in the PAX
 * XML tree – whether parsed from XML, built programmatically, or created
 * by a generated domain class – is ultimately an instance of {@code Pax}
 * or a subclass thereof.
 *
 * <p>A {@code Pax} node stores:</p>
 * <ul>
 *   <li>A <b>tag name</b> identifying the XML element, e.g. {@code "book"}</li>
 *   <li>An optional <b>text value</b>, e.g. {@code "1925"}</li>
 *   <li>An optional <b>parent</b> reference for upward tree traversal</li>
 *   <li>Lazily-created <b>children</b> ({@link Children}) and
 *       <b>attributes</b> ({@link Attributes}) collections</li>
 * </ul>
 *
 * <p>Both the {@link Children} and {@link Attributes} inner classes extend
 * {@link Subset} and are instantiated on demand via {@link #Child()} and
 * {@link #Attrib()} respectively, keeping the memory footprint of leaf
 * nodes minimal.</p>
 *
 * @see IPax
 * @see IChildren
 * @see IAttributes
 * @see Subset
 */

package de.graetz23.pax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Pax implements IPax {

  /** The XML tag name of this node, e.g. {@code "book"}. */
  private String _tag = null; // member

  /** The text content of this node, e.g. {@code "1925"}. */
  private String _val = null; // member

  /** The parent node; {@code null} for the root. */
  private IPax _parent = null; // member

  /** Lazily-created child-element collection. */
  private IChildren _children = null; // member

  /** Lazily-created XML-attribute collection. */
  private IAttributes _attributes = null; // member

  /**
   * Constructs a new {@code Pax} node with the given tag name and no
   * text value.
   *
   * @param tag the XML tag name; should be non-null and non-empty for
   *            the node to be considered valid by {@link #hasTag()}
   */
  public Pax(String tag) {
    Tag(tag);
  } // constructor

  /**
   * Constructs a new {@code Pax} node with the given tag name and an
   * initial text value. The value is validated by {@link #Val(String)};
   * blank or empty strings are silently discarded.
   *
   * @param tag the XML tag name
   * @param val the initial text value; ignored if blank or empty
   */
  public Pax(String tag, String val) {
    Tag(tag);
    if (val != null && !val.isEmpty()) {
      Val(val);
    } // if
  } // constructor

  /**
   * Deep-copy constructor. Creates a new {@code Pax} node that is a
   * structural copy of the given source node. Copies the tag name, all
   * attributes, the text value, and all children recursively. Each
   * child and attribute is copied via {@link Instances#Factory()}{@code .copy()},
   * so generated typed subclasses are preserved in the copy.
   *
   * @param Pax the source node to copy; if {@code null} the new node
   *            is created empty
   */
  public Pax(IPax Pax) {

    if (Pax != null) {

      if (Pax.hasTag()) {
        Tag(Pax.Tag());
      } // if

      if (Pax.hasAttrib()) {
        List<IPax> attribs = Pax.Attrib().all();
        for (IPax attrib : attribs) {
          IPax attrib_ = Instances.Factory().copy(attrib);
          Attrib().add(attrib_);
        } // loop
      } // if

      if (Pax.hasVal()) { // either value ..
        Val(Pax.Val());
      } // if

      if (Pax.hasChild()) {
        List<IPax> list = Pax.Child().all();
        for (IPax child : list) { // go recursive ..
          IPax child_ = Instances.Factory().copy(child);
          Child().add(child_);
        } // loop
      } // if

    } // if
  } // constructor

  /**
   * {@inheritDoc}
   */
  @Override
  public String Tag() {
    return _tag;
  } // method

  /**
   * {@inheritDoc}
   */
  @Override
  public void Tag(String tag) {
    _tag = tag;
  } // method

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasTag() {
    return _tag != null && !_tag.isEmpty();
  } // method

  /**
   * {@inheritDoc}
   */
  @Override
  public String Val() {
    return _val;
  } // method

  /**
   * {@inheritDoc}
   * Rejects {@code null}, empty strings, blank strings, and bare
   * line-separator strings; in those cases the stored value is set to
   * {@code null}.
   */
  @Override
  public void Val(String val) {
    if (val != null && !val.isEmpty() && !val.isBlank() && !val.toLowerCase().equals(Statics.LineSeparator) && !val.toLowerCase().equals("\n")) {
      _val = val;
    } else {
      _val = null;
    } // if
  } // method

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasVal() {
    return _val != null && !_val.isEmpty() && !_val.isBlank(); // is safe on null pointers ..
  } // method

  /**
   * {@inheritDoc}
   */
  @Override
  public IPax Parent() {
    return _parent;
  } // method

  /**
   * {@inheritDoc}
   */
  @Override
  public void Parent(IPax parent) {
    _parent = parent;
  } // method

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasParent() {
    return _parent != null;
  } // method

  /**
   * {@inheritDoc}
   * Walks the parent chain upward to the root, collects the tag names,
   * reverses the list, then joins them with {@code /} separators.
   */
  @Override
  public String Path() {
    String path = null;

    IPax current = this;
    List<IPax> list = new ArrayList<IPax>();
    list.add(current);
    while (current.hasParent()) {
      current = current.Parent();
      list.add(current);
    } // loop

    Collections.reverse(list);

    StringBuilder sb = new StringBuilder();
    for (IPax Pax : list) {
      String tag = Pax.Tag();
      sb.append("/");
      sb.append(tag);
    } // loop
    if (!sb.isEmpty()) {
      path = sb.toString();
    } // if
    return path;
  } // method

  /**
   * {@inheritDoc}
   * Creates a new {@link Children} instance on first call and caches it.
   */
  @Override
  public IChildren Child() {
    if (_children == null) {
      _children = new Children(this);
    } // if
    return _children;
  } // method

  /**
   * {@inheritDoc}
   * Returns {@code true} only when the {@link Children} object has
   * already been created and contains at least one entry, avoiding
   * unnecessary object allocation.
   */
  @Override
  public boolean hasChild() {
    boolean has = false;
    if (_children != null) {
      if (_children.cnt() > 0) {
        has = true;
      } // if
    } // if
    return has;
  } // method

  /**
   * {@inheritDoc}
   * Creates a new {@link Attributes} instance on first call and caches it.
   */
  @Override
  public IAttributes Attrib() {
    if (_attributes == null) {
      _attributes = new Attributes(this);
    } // if
    return _attributes;
  } // method

  /**
   * {@inheritDoc}
   * Returns {@code true} only when the {@link Attributes} object has
   * already been created and contains at least one entry.
   */
  @Override
  public boolean hasAttrib() {
    boolean has = false;
    if (_attributes != null) {
      if (_attributes.cnt() > 0) {
        has = true;
      } // if
    } // if
    return has;
  } // method

  /**
   * {@inheritDoc}
   * Delegates to {@link XmlGenerator#generate(IPax)}.
   */
  @Override
  public String XML() {
    return XmlGenerator.generate(this);
  } // method

  /**
   * {@inheritDoc}
   * Delegates to {@link XmlGenerator#generateLined(IPax)}.
   */
  @Override
  public String XML_lined() {
    return XmlGenerator.generateLined(this);
  } // method

  /**
   * {@inheritDoc}
   * Delegates to {@link JsonGenerator#generate(IPax)}.
   */
  @Override
  public String JSON() {
    return JsonGenerator.generate(this);
  } // method

  /**
   * Concrete implementation of {@link IChildren} used as the child-element
   * collection for every {@link Pax} node. Extends {@link Subset} to
   * inherit all ordered-map operations, and adds XPath-like
   * {@link #search(String)} navigation.
   *
   * <p>Instantiated lazily by {@link Pax#Child()} and bound to its
   * owning {@code Pax} as the ancestor.</p>
   *
   * @see IChildren
   * @see Subset
   */
  protected final class Children extends Subset implements IChildren {

    /**
     * Constructs a new {@code Children} collection bound to the given
     * ancestor node.
     *
     * @param ancestor the {@link IPax} node that owns this collection
     */
    public Children(IPax ancestor) {
      super(ancestor);
    } // constructor

    /**
     * {@inheritDoc}
     *
     * <p>Supports two path forms:</p>
     * <ul>
     *   <li><b>Absolute</b> (starts with {@code /}): walks up to the
     *       root first, then descends along the path segments.</li>
     *   <li><b>Relative</b> (starts with {@code ./}): removes the
     *       leading dots, prepends the current node's tag, then
     *       descends.</li>
     * </ul>
     * Redundant and leading/trailing slashes are stripped before
     * navigation. Returns {@code null} without throwing on any miss.
     */
    @Override
    public IPax search(String path) {
      IPax found = null;
      if (path != null && (path.startsWith("/") || path.startsWith("./"))) {

        IPax current = this.Ancestor();

        if (path.startsWith("/")) { // walk up root

          while (current.hasParent()) {
            current = current.Parent();
          } // loop

        } else if (path.startsWith(".")) { // start with child

          while (path.startsWith(".")) { // remove alls dots
            path = path.substring(1, path.length());
          } // if

          String tag = current.Tag();
          path = tag + path; // add myself for searching below

        } // if

        if (current != null) {

          while (path.contains("//")) {
            path = path.replaceAll("//", "/");
          } // loop

          while (path.startsWith("/")) { // remove all leading
            int endIndx = path.length();
            path = path.substring(1, endIndx);
          } // loop

          while (path.endsWith("/")) { // remove all trailing
            int endIndx = path.length() - 1;
            path = path.substring(0, endIndx);
          } // loop

          boolean wasFound = false;
          List<String> list = Arrays.stream(path.split("/")).toList();
          for (String tag : list) {

            if (current.Child().has(tag)) {

              IPax child = current.Child().get(tag);
              if (child != null) {
                current = child;
                wasFound = true;
              } else {
                wasFound = false;
              } // if

            } else {
              if (current.Tag().equals(tag)) {
                wasFound = true;
              } else {
                wasFound = false;
              } // if
            } // if

          } // loop

          if (wasFound) {
            found = current;
          } // if
        } // if
      } // if

      return found;
    } // method

  } // nested

  /**
   * Concrete implementation of {@link IAttributes} used as the XML
   * attribute collection for every {@link Pax} node. Extends {@link Subset}
   * to inherit all ordered-map operations, and adds {@link #XML()}
   * serialization for rendering attributes in an element's opening tag.
   *
   * <p>Instantiated lazily by {@link Pax#Attrib()} and bound to its
   * owning {@code Pax} as the ancestor.</p>
   *
   * @see IAttributes
   * @see Subset
   */
  protected final class Attributes extends Subset implements IAttributes {

    /**
     * Constructs a new {@code Attributes} collection bound to the given
     * ancestor node.
     *
     * @param ancestor the {@link IPax} node that owns this collection
     */
    public Attributes(IPax ancestor) {
      super(ancestor);
    } // constructor

    /**
     * {@inheritDoc}
     * Iterates over all attribute entries in insertion order and
     * renders each as {@code name="value"}, joining them with spaces.
     * The trailing space is trimmed before returning.
     */
    @Override
    public String XML() {
      StringBuilder xml = new StringBuilder();
      List<IPax> attribs = all();
      for (IPax attrib : attribs) {
        xml.append(attrib.Tag()).append("=\"").append(attrib.Val()).append("\" ");
      }
      return xml.toString().trim();
    } // method

  } // nested

} // class
