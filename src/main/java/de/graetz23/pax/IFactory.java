/**
 * @brief pax
 * @details A Java written generator for plain old XML (POX) data domains
 * @copyright Copyright (c) 2017-2026 Christian (graetz23@gmail.com)
 * @author Christian (graetz23@gmail.com)
 * @file IFactory.java
 *
 * Factory interface for creating and copying {@link IPax} nodes. The
 * default implementation {@link Factory} produces plain {@link Pax}
 * instances. Generated domain packages provide a {@code PaxFactory} that
 * overrides this interface to return typed subclasses (e.g. {@code Book},
 * {@code Author}) based on the XML tag name.
 *
 * The active factory is registered globally via
 * {@link Instances#Factory(IFactory)} and is used by {@link Subset} when
 * nodes are created implicitly (e.g. via {@link ISubset#add(String)}) and
 * by {@link Pax#Pax(IPax)} during deep-copy construction.
 *
 * @see Factory
 * @see Instances
 * @see Pax
 */

package de.graetz23.pax;

public interface IFactory {

    /**
     * Creates a new {@link IPax} node with the given tag name and no
     * text value. Equivalent to calling {@link #produce(String, String)}
     * with {@code val = null}.
     *
     * @param tag the XML tag name for the new node; must not be
     *            {@code null} or empty
     * @return a new {@link IPax} instance for the given tag
     */
    IPax produce(String tag); // method

    /**
     * Creates a new {@link IPax} node with the given tag name and
     * optional text value. Generated {@code PaxFactory} implementations
     * match the tag against known domain class names and return the
     * appropriate typed subclass; unrecognised tags fall through to a
     * plain {@link Pax}.
     *
     * @param tag the XML tag name for the new node
     * @param val the initial text value, or {@code null} for no value
     * @return a new typed {@link IPax} instance for the given tag
     */
    IPax produce(String tag, String val); // method

    /**
     * Creates a deep copy of the given {@link IPax} node, including all
     * attributes and children, as the appropriate typed subclass.
     * Delegates to the copy constructor {@code new TypedClass(IPax)} of
     * the matching domain class.
     *
     * @param Pax the source node to copy; must not be {@code null}
     * @return a deep copy of the source node as the correct typed class
     */
    IPax copy(IPax Pax); // method

} // interface
