/**
 * @brief pax
 * @details A Java written generator for plain old XML (POX) data domains
 * @copyright Copyright (c) 2017-2026 Christian (graetz23@gmail.com)
 * @author Christian (graetz23@gmail.com)
 * @file IAttributes.java
 *
 * Extends {@link ISubset} with XML serialization support for the
 * attribute collection of an {@link IPax} node. Every {@link IPax}
 * instance lazily creates a concrete implementation of this interface
 * (an inner {@code Attributes} class inside {@link Pax}) on first access
 * to {@link IPax#Attrib()}.
 *
 * XML attributes are stored exactly like child elements in the backing
 * {@link ISubset} map; the only difference is that they are rendered
 * inline on the opening tag rather than as nested elements.
 *
 * @see IPax#Attrib()
 * @see ISubset
 * @see Pax
 */

package de.graetz23.pax;

public interface IAttributes extends ISubset {

    /**
     * Serializes all attributes in this collection to an XML attribute
     * string of the form {@code name1="value1" name2="value2"} (space-
     * separated, no trailing space). Used internally by
     * {@link XmlGenerator} when rendering element opening tags.
     *
     * @return the attribute string, or an empty string if the collection
     *         is empty
     */
    String XML(); // method

} // method
