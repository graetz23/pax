/**
 * @brief pax
 * @details A Java written generator for plain old XML (POX) data domains
 * @copyright Copyright (c) 2017-2026 Christian (graetz23@gmail.com)
 * @author Christian (graetz23@gmail.com)
 * @file JsonGenerator.java
 *
 * Static utility class that serializes a {@link IPax} node tree to a
 * JSON string using the PAX JSON format. The format uses four reserved
 * keys to represent an XML node's structure:
 * <ul>
 *   <li>{@code __tag__} – the XML tag name</li>
 *   <li>{@code __value__} – the text content, if present</li>
 *   <li>{@code __attributes__} – an array of {@code {name, value}} objects</li>
 *   <li>{@code __children__} – a map of child tag to child object or array</li>
 * </ul>
 *
 * <p>When multiple sibling children share the same tag they are grouped
 * into a JSON array; a single child is represented as a plain object.
 * Numeric and boolean values are emitted without quotes; all other
 * string values are JSON-escaped and quoted.</p>
 *
 * <p>This format is designed to round-trip through {@code JsonReader},
 * preserving the full PAX tree structure including tag names, attributes,
 * and repeated children.</p>
 *
 * @see IPax#JSON()
 * @see Pax
 */

package de.graetz23.pax;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonGenerator {

  /**
   * Serializes the given {@link IPax} node and its entire subtree to a
   * compact JSON string using the PAX JSON format.
   *
   * @param pax the root node to serialize; must not be {@code null}
   * @return a JSON string representing the subtree rooted at {@code pax};
   *         returns {@code "null"} if the node is {@code null} or tag-less
   */
  public static String generate(IPax pax) {
    StringBuilder json = new StringBuilder();
    toJson(pax, json, 0, true);
    return json.toString();
  }

  /**
   * Recursively appends the JSON representation of a single {@link IPax}
   * node to the given {@link StringBuilder}. Builds the
   * {@code __tag__}, {@code __value__}, {@code __attributes__}, and
   * {@code __children__} keys as applicable, grouping repeated children
   * into arrays.
   *
   * @param pax    the node to render
   * @param json   the {@link StringBuilder} accumulating the JSON output
   * @param indent current indent level (reserved for future use)
   * @param isRoot {@code true} when this is the outermost call
   */
  private static void toJson(IPax pax, StringBuilder json, int indent, boolean isRoot) {
    if (pax == null || !pax.hasTag()) {
      json.append("null");
      return;
    }

    String tag = pax.Tag();

    json.append("{");

    boolean hasContent = false;

    json.append("\"__tag__\": \"");
    json.append(escapeJson(tag));
    json.append("\"");
    hasContent = true;

    if (pax.hasVal()) {
      if (hasContent) json.append(", ");
      json.append("\"__value__\": ");
      appendJsonValue(pax.Val(), json);
      hasContent = true;
    }

    List<IPax> attrs = pax.Attrib().all();
    if (!attrs.isEmpty()) {
      if (hasContent) json.append(", ");
      json.append("\"__attributes__\": [");
      for (int i = 0; i < attrs.size(); i++) {
        if (i > 0) json.append(", ");
        IPax attr = attrs.get(i);
        json.append("{");
        json.append("\"name\": \"").append(escapeJson(attr.Tag())).append("\", ");
        json.append("\"value\": ");
        appendJsonValue(attr.Val(), json);
        json.append("}");
      }
      json.append("]");
      hasContent = true;
    }

    List<IPax> children = pax.Child().all();
    if (!children.isEmpty()) {
      if (hasContent) json.append(", ");
      json.append("\"__children__\": {");

      Map<String, List<IPax>> childGroups = new LinkedHashMap<>();
      for (IPax child : children) {
        String childTag = child.Tag();
        if (!childGroups.containsKey(childTag)) {
          childGroups.put(childTag, new ArrayList<>());
        }
        childGroups.get(childTag).add(child);
      }

      boolean firstChild = true;
      for (Map.Entry<String, List<IPax>> entry : childGroups.entrySet()) {
        if (!firstChild) json.append(", ");

        String childTag = entry.getKey();
        List<IPax> childList = entry.getValue();

        if (childList.size() == 1) {
          json.append("\"").append(escapeJson(childTag)).append("\": ");
          toJsonValue(childList.get(0), json, false, true);
        } else {
          json.append("\"").append(escapeJson(childTag)).append("\": [");
          for (int i = 0; i < childList.size(); i++) {
            if (i > 0) json.append(", ");
            toJsonValue(childList.get(i), json, false, true);
          }
          json.append("]");
        }
        firstChild = false;
      }

      json.append("}");
    }

    json.append("}");
  }

  /**
   * Decides how to render a single {@link IPax} value within a JSON
   * context. If {@code forceObject} is {@code true} or the node has
   * children or attributes, the full object form is used via
   * {@link #toJson}. Otherwise the node's plain text value is emitted
   * directly if available.
   *
   * @param pax         the node to render
   * @param json        the {@link StringBuilder} accumulating output
   * @param isRoot      {@code true} when at the top level
   * @param forceObject {@code true} to always emit a full JSON object
   */
  private static void toJsonValue(IPax pax, StringBuilder json, boolean isRoot, boolean forceObject) {
    if (forceObject && pax.hasTag()) {
      toJson(pax, json, 0, false);
    } else if (pax.hasVal()) {
      appendJsonValue(pax.Val(), json);
    } else if (pax.hasChild() || pax.hasAttrib()) {
      toJson(pax, json, 0, false);
    } else if (isRoot && pax.hasTag()) {
      toJson(pax, json, 0, true);
    } else {
      json.append("{}");
    }
  }

  /**
   * Appends a JSON-typed value for the given string to the builder.
   * Boolean literals ({@code "true"}, {@code "false"}) and numeric
   * strings are emitted without quotes; all other values are
   * JSON-escaped and surrounded by double quotes. {@code null} values
   * are emitted as the JSON literal {@code null}.
   *
   * @param val  the string value to emit; may be {@code null}
   * @param json the {@link StringBuilder} accumulating output
   */
  private static void appendJsonValue(String val, StringBuilder json) {
    if (val == null) {
      json.append("null");
    } else if (val.equals("true") || val.equals("false")) {
      json.append(val);
    } else if (isNumeric(val)) {
      json.append(val);
    } else {
      json.append("\"").append(escapeJson(val)).append("\"");
    }
  }

  /**
   * Escapes a string for safe embedding in a JSON string literal.
   * Handles backslashes, double quotes, and the common control characters
   * {@code \n}, {@code \r}, {@code \t}, {@code \b}, and {@code \f}.
   *
   * @param str the raw string to escape; returns {@code ""} if {@code null}
   * @return the escaped string suitable for use inside JSON double quotes
   */
  private static String escapeJson(String str) {
    if (str == null) return "";
    return str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t").replace("\b", "\\b").replace("\f", "\\f");
  }

  /**
   * Returns {@code true} when the given string can be parsed as a
   * {@link Double}, indicating it should be emitted as an unquoted JSON
   * number.
   *
   * @param str the string to test; returns {@code false} for {@code null}
   *            or empty strings
   * @return {@code true} if {@code str} is a valid numeric value
   */
  private static boolean isNumeric(String str) {
    if (str == null || str.isEmpty()) return false;
    try {
      Double.parseDouble(str);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
