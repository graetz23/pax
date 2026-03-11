/**
 * @brief pax
 * @details An object-tree combined with a tolerant reader to parse any XML
 * @copyright Copyright (c) 2017-2026 Christian (graetz23@gmail.com)
 * @author Christian (graetz23@gmail.com)
 * @file XmlGenerator.java
 *
 * Static utility class that serializes a {@link IPax} node tree to an
 * XML string. Two output modes are provided:
 * <ul>
 *   <li>{@link #generate(IPax)} – produces indented, multi-line XML
 *       using {@link Statics#Indent()} and {@link Statics#LineSeparator}
 *       for human-readable output.</li>
 *   <li>{@link #generateLined(IPax)} – produces compact inline XML
 *       without any line breaks or indentation, suitable for embedding
 *       in single-line contexts.</li>
 * </ul>
 *
 * <p>Both modes handle the full range of PAX node types:</p>
 * <ul>
 *   <li>Regular elements with and without attributes</li>
 *   <li>Self-closing tags for empty elements ({@code <tag />})</li>
 *   <li>Text-content elements ({@code <tag>value</tag>})</li>
 *   <li>Elements with child elements (recursive)</li>
 *   <li>XML comments ({@link Identity#COMMENT}): {@code <!-- ... -->}</li>
 *   <li>CDATA sections ({@link Identity#CDATA}): {@code <![CDATA[ ... ]]>}</li>
 * </ul>
 *
 * <p><b>Note:</b> {@link Statics} indentation state is global and not
 * thread-safe. Do not use {@link #generate(IPax)} concurrently.</p>
 *
 * @see IPax#XML()
 * @see IPax#XML_lined()
 * @see Statics
 * @see Identity
 */

package de.graetz23.pax;

import java.util.StringJoiner;

public class XmlGenerator {

  /**
   * Serializes the given {@link IPax} node and its entire subtree to an
   * indented, multi-line XML string. Uses the global indentation state in
   * {@link Statics} to produce properly nested output.
   *
   * <p>Calls {@link #generateElement(IPax, StringBuilder)} recursively
   * for each child node.</p>
   *
   * @param pax the root node to serialize; must not be {@code null} and
   *            must have a tag
   * @return a formatted XML string representing the subtree rooted at
   *         {@code pax}; may be an empty string for a null or tag-less node
   */
  public static String generate(IPax pax) {
    StringBuilder xml = new StringBuilder();
    return generateElement(pax, xml).toString();
  } // method

  /**
   * Serializes the given {@link IPax} node and its entire subtree to a
   * compact inline XML string with no line breaks or indentation.
   *
   * <p>Calls {@link #generateElementLined(IPax, StringJoiner)} recursively
   * for each child node.</p>
   *
   * @param pax the root node to serialize; must not be {@code null} and
   *            must have a tag
   * @return an inline XML string representing the subtree rooted at
   *         {@code pax}; may be empty for a null or tag-less node
   */
  public static String generateLined(IPax pax) {
    StringJoiner xml = new StringJoiner("");
    return generateElementLined(pax, xml).toString();
  } // method

  /**
   * Recursively appends the indented XML representation of a single
   * {@link IPax} node to the given {@link StringBuilder}. Handles all
   * node types: comments, CDATA, leaf elements (with or without
   * attributes), self-closing empty elements, and container elements
   * with child nodes. Increments and decrements the global indentation
   * level in {@link Statics} as it descends and ascends the tree.
   *
   * @param pax the node to render; returns the builder unchanged if
   *            {@code null} or tag-less
   * @param xml the {@link StringBuilder} accumulating the output
   * @return the same {@link StringBuilder} for chaining
   */
  private static StringBuilder generateElement(IPax pax, StringBuilder xml) {
    if (pax == null || !pax.hasTag()) {
      return xml;
    }
    xml.append(Statics.Indent());
    if (!pax.hasChild()) {
      if (pax.hasVal()) {
        if (pax.Tag().startsWith(Identity.COMMENT)) {
          xml.append("<!--").append(pax.Val()).append("-->").append(Statics.LineSeparator);
        } else if (pax.Tag().startsWith(Identity.CDATA)) {
          xml.append("<![CDATA[").append(pax.Val()).append("]]>").append(Statics.LineSeparator);
        } else {
          if (pax.hasAttrib()) {
            xml.append("<").append(pax.Tag()).append(" ").append(pax.Attrib().XML()).append(">").append(pax.Val()).append("</").append(pax.Tag()).append(">").append(Statics.LineSeparator);
          } else {
            xml.append("<").append(pax.Tag()).append(">").append(pax.Val()).append("</").append(pax.Tag()).append(">").append(Statics.LineSeparator);
          }
        }
      } else {
        if (pax.hasAttrib()) {
          xml.append("<").append(pax.Tag()).append(" ").append(pax.Attrib().XML()).append("/>").append(Statics.LineSeparator);
        } else {
          xml.append("<").append(pax.Tag()).append(" />").append(Statics.LineSeparator);
        }
      }
    } else {
      if (pax.hasAttrib()) {
        xml.append("<").append(pax.Tag()).append(" ").append(pax.Attrib().XML()).append(">").append(Statics.LineSeparator);
      } else {
        xml.append("<").append(pax.Tag()).append(">").append(Statics.LineSeparator);
      }
      Statics.incIndent();
      for (IPax child : pax.Child().all()) {
        generateElement(child, xml);
      }
      Statics.decindent();
      xml.append(Statics.Indent()).append("</").append(pax.Tag()).append(">").append(Statics.LineSeparator);
    }
    return xml;
  } // method

  /**
   * Recursively appends the inline (no-whitespace) XML representation of
   * a single {@link IPax} node to the given {@link StringJoiner}. Handles
   * all the same node types as {@link #generateElement(IPax, StringBuilder)}
   * but without any indentation or line separators.
   *
   * @param pax the node to render; returns the joiner unchanged if
   *            {@code null} or tag-less
   * @param xml the {@link StringJoiner} accumulating the output
   * @return the same {@link StringJoiner} for chaining
   */
  private static StringJoiner generateElementLined(IPax pax, StringJoiner xml) {
    if (pax == null || !pax.hasTag()) {
      return xml;
    }
    if (!pax.hasChild()) {
      if (pax.hasVal()) {
        if (pax.Tag().startsWith(Identity.COMMENT)) {
          xml.add("<!--").add(pax.Val()).add("-->");
        } else if (pax.Tag().startsWith(Identity.CDATA)) {
          xml.add("<![CDATA[").add(pax.Val()).add("]]>");
        } else {
          if (pax.hasAttrib()) {
            xml.add("<").add(pax.Tag()).add(" ").add(pax.Attrib().XML()).add(">").add(pax.Val()).add("</").add(pax.Tag()).add(">");
          } else {
            xml.add("<").add(pax.Tag()).add(">").add(pax.Val()).add("</").add(pax.Tag()).add(">");
          }
        }
      } else {
        if (pax.hasAttrib()) {
          xml.add("<").add(pax.Tag()).add(" ").add(pax.Attrib().XML()).add("/>");
        } else {
          xml.add("<").add(pax.Tag()).add(" />");
        }
      }
    } else {
      if (pax.hasAttrib()) {
        xml.add("<").add(pax.Tag()).add(" ").add(pax.Attrib().XML()).add(">");
      } else {
        xml.add("<").add(pax.Tag()).add(">");
      }
      Statics.incIndent();
      for (IPax child : pax.Child().all()) {
        generateElementLined(child, xml);
      }
      Statics.decindent();
      xml.add("</").add(pax.Tag()).add(">");
    }
    return xml;
  } // method

} // class
