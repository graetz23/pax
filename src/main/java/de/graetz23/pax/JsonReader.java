/**
 * @brief pax
 * @details An object-tree combined with a tolerant reader to parse any XML
 * @copyright Copyright (c) 2017-2026 Christian (graetz23@gmail.com)
 * @author Christian (graetz23@gmail.com)
 * @file JsonReader.java
 *
 * Singleton utility for parsing JSON strings into a {@link IPax} node tree.
 * Supports the PAX JSON format produced by {@link JsonGenerator}, which uses
 * four reserved keys ({@code __tag__}, {@code __value__}, {@code __attributes__},
 * and {@code __children__}) to represent XML node structure.
 *
 * <p>Three entry points are provided:</p>
 * <ul>
 *   <li>{@link #parse(String)} – reads JSON from a file by path</li>
 *   <li>{@link #stream(InputStream)} – parses JSON from any input stream</li>
 *   <li>{@link #parseJson(String)} – parses JSON from a string directly</li>
 * </ul>
 *
 * @see IPax#JSON()
 * @see JsonGenerator
 */

package de.graetz23.pax;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonReader {

  private static final Logger LOGGER = Logger.getLogger(JsonReader.class.getName());

  /**
   * The singleton instance. Use this field to call parsing methods, e.g.
   * {@code JsonReader.Instance.parse("data.json")}.
   */
  public static JsonReader Instance = new JsonReader();

  /**
   * Private constructor enforcing the singleton pattern.
   */
  private JsonReader() {
  }

  /**
   * Parses the JSON file at the given file-system path by opening it as a
   * {@link FileInputStream} and delegating to the stream parser. Returns
   * {@code null} and prints a stack trace if the file cannot be found or
   * the JSON is malformed.
   *
   * @param filename the path to the JSON file; resolved by the OS
   * @return the root {@link IPax} of the parsed tree, or {@code null} on
   *         any error
   */
  public IPax parse(String filename) {
    IPax root = null;
    try {
      FileInputStream fis = new FileInputStream(filename);
      root = stream(fis);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Failed to read JSON file: " + filename, e);
    }
    return root;
  }

  /**
   * Parses JSON from the given {@link InputStream}. Useful when the JSON
   * content originates from a network connection, classpath resource, or
   * in-memory buffer rather than a file on disk.
   *
   * @param stream the input stream carrying the JSON content; must not be
   *               {@code null}
   * @return the root {@link IPax} of the parsed tree, or {@code null} on
   *         any error
   */
  public IPax stream(InputStream stream) {
    IPax root = null;
    try {
      if (stream != null) {
        String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        root = parseJson(json);
      } else {
        System.out.println("InputStream is null - no data found");
      }
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Failed to read JSON from InputStream", e);
    }
    return root;
  }

  /**
   * Parses a JSON string directly into an {@link IPax} node tree.
   * The string must be valid JSON in the PAX JSON format.
   *
   * @param json the JSON string to parse; must not be {@code null}
   * @return the root {@link IPax} of the parsed tree, or {@code null} if
   *         the input is null or empty
   */
  public IPax parseJson(String json) {
    IPax root = null;
    if (json != null && !json.trim().isEmpty()) {
      JsonParser parser = new JsonParser(json);
      root = parser.parse();
    }
    return root;
  }

  /**
   * Internal recursive-descent JSON parser that converts a JSON string
   * into an {@link IPax} node tree following the PAX JSON format.
   * Handles objects, arrays, strings, numbers, booleans, and null values.
   */
  private static class JsonParser {
    private final String json;
    private int pos = 0;
    private int length;

    /**
     * Constructs a new parser for the given JSON string.
     *
     * @param json the JSON string to parse; must not be {@code null}
     */
    public JsonParser(String json) {
      this.json = json;
      this.length = json.length();
    }

    /**
     * Parses the entire JSON string and returns the root {@link IPax} node.
     *
     * @return the root IPax node representing the JSON value
     */
    public IPax parse() {
      skipWhitespace();
      return parseValue();
    }

    private void skipWhitespace() {
      while (pos < length && Character.isWhitespace(json.charAt(pos))) {
        pos++;
      }
    }

    private char peek() {
      skipWhitespace();
      return pos < length ? json.charAt(pos) : '\0';
    }

    private char consume() {
      skipWhitespace();
      return pos < length ? json.charAt(pos++) : '\0';
    }

    private void expect(char c) {
      char actual = consume();
      if (actual != c) {
        throw new RuntimeException("Expected '" + c + "' but got '" + actual + "' at position " + pos);
      }
    }

    private IPax parseValue() {
      skipWhitespace();
      if (pos >= length) {
        return null;
      }

      char c = json.charAt(pos);
      switch (c) {
        case '{':
          return parseObjectWithAttributes();
        case '[':
          return parseArray();
        case '"':
          return parseStringValue();
        case 't':
        case 'f':
          return parseBoolean();
        case 'n':
          return parseNull();
        default:
          if (c == '-' || Character.isDigit(c)) {
            return parseNumber();
          }
          throw new RuntimeException("Unexpected character: " + c + " at position " + pos);
      }
    }

    private IPax parseObjectWithAttributes() {
      expect('{');
      IPax obj = Instances.Factory().produce("object");

      skipWhitespace();
      if (peek() == '}') {
        consume();
        return obj;
      }

      String tag = "object";
      String value = null;
      java.util.List<String> attrKeys = new java.util.ArrayList<>();
      java.util.List<String> attrValues = new java.util.ArrayList<>();
      java.util.Map<String, java.util.List<IPax>> childrenMap = new java.util.LinkedHashMap<>();

      while (true) {
        skipWhitespace();
        String key = parseString();
        skipWhitespace();
        expect(':');

        IPax parsedValue = parseValue();

        if (key.equals("__tag__")) {
          if (parsedValue.hasVal()) {
            tag = parsedValue.Val();
            obj.Tag(tag);
          }
        } else if (key.equals("__value__")) {
          if (parsedValue.hasVal()) {
            value = parsedValue.Val();
            obj.Val(value);
          }
        } else if (key.equals("__attributes__")) {
          if (parsedValue.hasChild()) {
            for (IPax attrItem : parsedValue.Child().all()) {
              IPax nameNode = null;
              IPax valueNode = null;

              if (attrItem.hasChild()) {
                for (IPax childOfItem : attrItem.Child().all()) {
                  String childTag = childOfItem.Tag();
                  if ("name".equals(childTag)) {
                    nameNode = childOfItem;
                  } else if ("value".equals(childTag)) {
                    valueNode = childOfItem;
                  }
                }
              }

              if (nameNode == null && attrItem.hasAttrib()) {
                nameNode = attrItem.Attrib().get("name");
              }
              if (valueNode == null && attrItem.hasAttrib()) {
                valueNode = attrItem.Attrib().get("value");
              }

              if (nameNode != null && nameNode.hasVal()) {
                String attrName = nameNode.Val();
                String attrValue = (valueNode != null && valueNode.hasVal()) ? valueNode.Val() : "";
                attrKeys.add(attrName);
                attrValues.add(attrValue);
              }
            }
          }
        } else if (key.equals("__children__")) {
          if (parsedValue.hasChild()) {
            for (IPax childItem : parsedValue.Child().all()) {
              String childTag = childItem.Tag();

              boolean isArray = false;
              IPax listAttr = childItem.Attrib().get(Identity.LIST);
              if (listAttr != null && listAttr.hasVal() && "true".equals(listAttr.Val())) {
                isArray = true;
              }

              if (isArray && childItem.hasChild()) {
                for (IPax arrayElement : childItem.Child().all()) {
                  String elementTag = "item";
                  IPax originalTagAttr = arrayElement.Attrib().get("#arrayElementTag");
                  if (originalTagAttr != null && originalTagAttr.hasVal()) {
                    elementTag = originalTagAttr.Val();
                    arrayElement.Attrib().del("#arrayElementTag");
                  }
                  arrayElement.Attrib().del("index");

                  if (!childrenMap.containsKey(elementTag)) {
                    childrenMap.put(elementTag, new java.util.ArrayList<>());
                  }
                  childrenMap.get(elementTag).add(arrayElement);
                }
              } else {
                if (childItem.hasChild()) {
                  IPax tagNode = childItem.Child().get("__tag__");
                  if (tagNode != null && tagNode.hasVal()) {
                    childTag = tagNode.Val();
                  }
                }

                if (!childrenMap.containsKey(childTag)) {
                  childrenMap.put(childTag, new java.util.ArrayList<>());
                }
                childrenMap.get(childTag).add(childItem);
              }
            }
          }
        } else {
          // Treat as a child
          if (!childrenMap.containsKey(key)) {
            childrenMap.put(key, new java.util.ArrayList<>());
          }
          childrenMap.get(key).add(parsedValue);
        }

        skipWhitespace();
        if (peek() == ',') {
          consume();
        } else {
          break;
        }
      }

      expect('}');

      for (int i = 0; i < attrKeys.size(); i++) {
        obj.Attrib().add(attrKeys.get(i), attrValues.get(i));
      }

      for (java.util.Map.Entry<String, java.util.List<IPax>> entry : childrenMap.entrySet()) {
        String childTag = entry.getKey();
        java.util.List<IPax> childList = entry.getValue();

        for (IPax child : childList) {
          child.Tag(childTag);
          obj.Child().add(child);
        }
      }

      return obj;
    }

    private IPax parseObject() {
      return parseObject("object");
    }

    private IPax parseObject(String tag) {
      expect('{');
      IPax obj = Instances.Factory().produce(tag);

      skipWhitespace();
      if (peek() == '}') {
        consume();
        return obj;
      }

      while (true) {
        skipWhitespace();
        String key = parseString();
        skipWhitespace();
        expect(':');

        if (key.equals("__attributes__")) {
          parseAttributesArray(obj);
        } else {
          IPax value = parseValue();

          boolean isNullValue = value.Tag().equals("null");

          if (value.hasVal()) {
            obj.Attrib().add(key, value.Val());
          } else if (isNullValue) {
            obj.Attrib().add(key, null);
          } else if (value.hasChild() || value.hasAttrib()) {
            value.Tag(key);
            obj.Child().add(value);
          } else {
            obj.Child().add(key);
          }
        }

        skipWhitespace();
        if (peek() == ',') {
          consume();
        } else {
          break;
        }
      }

      expect('}');
      return obj;
    }

    private void parseAttributesArray(IPax parent) {
      expect('[');
      skipWhitespace();

      if (peek() == ']') {
        consume();
        return;
      }

      while (true) {
        skipWhitespace();

        char c = peek();
        IPax attrValue;
        String attrName = null;

        if (c == '{') {
          attrValue = parseValue();

          if (attrValue.hasChild()) {
            IPax nameNode = attrValue.Child().get("name");
            IPax valueNode = attrValue.Child().get("value");

            if (nameNode != null && nameNode.hasVal()) {
              attrName = nameNode.Val();
            }
            if (valueNode != null && valueNode.hasVal()) {
              parent.Attrib().add(attrName, valueNode.Val());
            } else if (valueNode != null) {
              parent.Attrib().add(attrName, "");
            }
          }
        } else {
          attrValue = parseValue();
        }

        skipWhitespace();
        if (peek() == ',') {
          consume();
        } else {
          break;
        }
      }

      expect(']');
    }

    private IPax parseArray() {
      expect('[');
      IPax array = Instances.Factory().produce("array");
      array.Attrib().add(Identity.LIST, "true");

      skipWhitespace();
      if (peek() == ']') {
        consume();
        return array;
      }

      int index = 0;
      while (true) {
        IPax item = parseValue();
        String originalTag = item.Tag();
        item.Tag("item");
        item.Attrib().add("index", String.valueOf(index));
        item.Attrib().add("#arrayElementTag", originalTag);
        array.Child().add(item);
        index++;

        skipWhitespace();
        if (peek() == ',') {
          consume();
        } else {
          break;
        }
      }

      expect(']');
      return array;
    }

    private String parseString() {
      expect('"');
      StringBuilder sb = new StringBuilder();

      while (pos < length) {
        char c = json.charAt(pos++);
        if (c == '"') {
          break;
        } else if (c == '\\') {
          if (pos < length) {
            char escaped = json.charAt(pos++);
            switch (escaped) {
              case '"':
                sb.append('"');
                break;
              case '\\':
                sb.append('\\');
                break;
              case '/':
                sb.append('/');
                break;
              case 'b':
                sb.append('\b');
                break;
              case 'f':
                sb.append('\f');
                break;
              case 'n':
                sb.append('\n');
                break;
              case 'r':
                sb.append('\r');
                break;
              case 't':
                sb.append('\t');
                break;
              case 'u':
                String hex = json.substring(pos, pos + 4);
                sb.append((char) Integer.parseInt(hex, 16));
                pos += 4;
                break;
              default:
                sb.append(escaped);
                break;
            }
          }
        } else {
          sb.append(c);
        }
      }

      return sb.toString();
    }

    private IPax parseStringValue() {
      String value = parseString();
      IPax pax = Instances.Factory().produce("string", value);
      return pax;
    }

    private IPax parseNumber() {
      StringBuilder sb = new StringBuilder();
      if (peek() == '-') {
        sb.append(consume());
      }
      while (pos < length && Character.isDigit(peek())) {
        sb.append(consume());
      }
      if (peek() == '.') {
        sb.append(consume());
        while (pos < length && Character.isDigit(peek())) {
          sb.append(consume());
        }
      }
      if (peek() == 'e' || peek() == 'E') {
        sb.append(consume());
        if (peek() == '+' || peek() == '-') {
          sb.append(consume());
        }
        while (pos < length && Character.isDigit(peek())) {
          sb.append(consume());
        }
      }

      String numStr = sb.toString();
      IPax pax = Instances.Factory().produce("number", numStr);
      return pax;
    }

    private IPax parseBoolean() {
      if (json.substring(pos).startsWith("true")) {
        pos += 4;
        return Instances.Factory().produce("boolean", "true");
      } else if (json.substring(pos).startsWith("false")) {
        pos += 5;
        return Instances.Factory().produce("boolean", "false");
      }
      throw new RuntimeException("Invalid boolean at position " + pos);
    }

    private IPax parseNull() {
      if (json.substring(pos).startsWith("null")) {
        pos += 4;
        IPax pax = Instances.Factory().produce("null");
        return pax;
      }
      throw new RuntimeException("Invalid null at position " + pos);
    }
  }
}
