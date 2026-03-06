/**
 * @brief pax
 * @details A Java written generator for plain old XML (POX) data domains
 * @copyright Copyright (c) 2017-2026 Christian (graetz23@gmail.com)
 * @author Christian (graetz23@gmail.com)
 * @file IPax.java
 *
 * Central interface for every node in the PAX XML tree. Each element,
 * attribute, text node, comment, and CDATA section is represented by an
 * object that implements IPax. The interface covers the full lifecycle of
 * a node: identity (tag name), text content (value), parent linkage,
 * XPath-style navigation via children and attributes, and serialization
 * to both XML and JSON.
 *
 * @see Pax
 * @see IChildren
 * @see IAttributes
 */

package de.graetz23.pax;

public interface IPax {

  /**
   * Returns the XML tag name of this node, e.g. {@code "book"} for
   * {@code <book>}.
   *
   * @return the tag name string, or {@code null} if none has been set
   */
  String Tag();

  /**
   * Sets the XML tag name of this node.
   *
   * @param tag the tag name to assign; must not be {@code null} or empty
   *            for the node to be considered valid
   */
  void Tag(String tag);

  /**
   * Returns {@code true} when this node has a non-null, non-empty tag name.
   *
   * @return {@code true} if a tag name is present
   */
  boolean hasTag();

  /**
   * Returns the text content (character data) of this node, e.g.
   * {@code "1925"} for {@code <year>1925</year>}.
   *
   * @return the value string, or {@code null} if no value has been set
   */
  String Val();

  /**
   * Sets the text content of this node. Blank values, empty strings, and
   * bare line-separator strings are treated as {@code null} and will clear
   * any previously stored value.
   *
   * @param val the value to assign; ignored if blank or whitespace-only
   */
  void Val(String val);

  /**
   * Returns {@code true} when this node has a non-null, non-empty,
   * non-blank text value.
   *
   * @return {@code true} if a meaningful value is present
   */
  boolean hasVal();

  /**
   * Returns the parent node of this node in the tree, or {@code null}
   * if this node is the root.
   *
   * @return the parent {@link IPax}, or {@code null}
   */
  IPax Parent();

  /**
   * Sets the parent link of this node. Called internally by
   * {@link ISubset#add(IPax)} and {@link ISubset#set(IPax)} to maintain
   * the parent-child relationship.
   *
   * @param parent the parent node to link to; may be {@code null} to
   *               detach this node from its parent
   */
  void Parent(IPax parent);

  /**
   * Returns {@code true} when this node has a non-null parent, i.e. it is
   * not the root of the tree.
   *
   * @return {@code true} if a parent link exists
   */
  boolean hasParent();

  /**
   * Computes and returns the absolute path of this node from the root,
   * using {@code /} as the separator, e.g. {@code /library/books/book}.
   * Traverses the parent chain upward to the root, then concatenates the
   * tag names.
   *
   * @return the full path string, or {@code null} if the node has no tag
   */
  String Path();

  /**
   * Returns the {@link IChildren} manager for this node, creating it
   * lazily on first access. Use this to add, get, search, or iterate
   * over child nodes.
   *
   * @return the children container; never {@code null}
   */
  IChildren Child();

  /**
   * Returns {@code true} when this node has at least one child element.
   * Does not create the children container if it does not yet exist.
   *
   * @return {@code true} if one or more children are present
   */
  boolean hasChild();

  /**
   * Returns the {@link IAttributes} manager for this node, creating it
   * lazily on first access. Use this to add, get, or iterate over XML
   * attributes.
   *
   * @return the attributes container; never {@code null}
   */
  IAttributes Attrib();

  /**
   * Returns {@code true} when this node has at least one attribute.
   * Does not create the attributes container if it does not yet exist.
   *
   * @return {@code true} if one or more attributes are present
   */
  boolean hasAttrib();

  /**
   * Serializes this node and its entire subtree to a formatted XML string
   * with indentation and line separators. Delegates to
   * {@link XmlGenerator#generate(IPax)}.
   *
   * @return the indented XML representation of this subtree
   */
  String XML();

  /**
   * Serializes this node and its entire subtree to a compact, single-line
   * XML string without indentation or line separators. Delegates to
   * {@link XmlGenerator#generateLined(IPax)}.
   *
   * @return the inline XML representation of this subtree
   */
  String XML_lined();

  /**
   * Serializes this node and its entire subtree to a JSON string using the
   * PAX JSON format with special keys {@code __tag__}, {@code __value__},
   * {@code __attributes__}, and {@code __children__}. Delegates to
   * {@link JsonGenerator#generate(IPax)}.
   *
   * @return the JSON representation of this subtree
   */
  String JSON();

} // interface
