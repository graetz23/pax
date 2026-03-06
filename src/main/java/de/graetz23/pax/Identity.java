/**
 * @brief pax
 * @details A Java written generator for plain old XML (POX) data domains
 * @copyright Copyright (c) 2017-2026 Christian (graetz23@gmail.com)
 * @author Christian (graetz23@gmail.com)
 * @file Identity.java
 *
 * String constants for special XML node types that do not correspond to
 * regular element tags. These identifiers follow the W3C DOM specification
 * naming conventions (prefixed with {@code #}) and are used throughout
 * the PAX library to distinguish special nodes from regular element nodes.
 *
 * <p>During SAX parsing ({@link Reader}) and XML serialization
 * ({@link XmlGenerator}), these constants are checked via
 * {@link String#startsWith(String)} to route comments, CDATA sections,
 * and text nodes to their correct rendering logic.</p>
 *
 * @see Reader
 * @see XmlGenerator
 */

package de.graetz23.pax;

public class Identity {

    /**
     * W3C XML document header identifier. Marks the synthetic root node
     * that holds the XML declaration when present.
     */
    public final static String HEADER = "#xml"; // member

    /**
     * W3C XML text node identifier. Represents character data content
     * that appears directly inside an element (not as an attribute value).
     */
    public final static String TEXT = "#text"; // member

    /**
     * W3C XML comment identifier. Nodes with a tag starting with this
     * constant are rendered as {@code <!-- ... -->} by {@link XmlGenerator}
     * and are added as children during SAX parsing via
     * {@link Reader}'s {@code IPaxLexicalHandler}.
     */
    public final static String COMMENT = "#comment"; // member

    /**
     * W3C XML CDATA section identifier. Nodes with a tag starting with
     * this constant are rendered as {@code <![CDATA[ ... ]]>} by
     * {@link XmlGenerator}.
     */
    public final static String CDATA = "#cdata-section"; // member

    /**
     * PAX-internal list marker. Not part of any W3C standard. Used by
     * {@link JsonReader} to mark array-valued children in the JSON
     * representation so that they can be round-tripped correctly.
     */
    public final static String LIST = "#list"; // member  // not of any standard

} // class
