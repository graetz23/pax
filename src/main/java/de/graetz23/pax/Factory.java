/**
 * @brief pax
 * @details An object-tree combined with a tolerant reader to parse any XML
 * @copyright Copyright (c) 2017-2026 Christian (graetz23@gmail.com)
 * @author Christian (graetz23@gmail.com)
 * @file Factory.java
 *
 * Default implementation of {@link IFactory} that produces plain
 * {@link Pax} instances for any tag name. This factory is registered
 * as the fallback in {@link Instances} and is used when no domain-
 * specific {@code PaxFactory} has been registered.
 *
 * <p>Generated domain packages provide a subclass of this factory
 * (typically named {@code PaxFactory}) that overrides {@link #produce}
 * and {@link #copy} to return typed domain objects (e.g. {@code Book},
 * {@code Author}) for known tag names, while unknown tags fall through
 * to the plain-{@code Pax} logic.</p>
 *
 * @see IFactory
 * @see Instances
 * @see Pax
 */

package de.graetz23.pax;

public class Factory implements IFactory {

    /**
     * {@inheritDoc}
     * Returns a new {@link Pax} node constructed with the given tag name
     * and no text value.
     *
     * @param tag the XML tag name for the new node
     * @return a new plain {@link Pax} instance
     */
    public IPax produce(String tag) {
        return new Pax(tag);
    } // method

    /**
     * {@inheritDoc}
     * Returns a new {@link Pax} node constructed with the given tag name
     * and text value.
     *
     * @param tag the XML tag name for the new node
     * @param val the initial text value; may be {@code null}
     * @return a new plain {@link Pax} instance with the value set
     */
    public IPax produce(String tag, String val) {
        return new Pax(tag, val);
    } // method

    /**
     * {@inheritDoc}
     * Creates a deep copy of the given node via the
     * {@link Pax#Pax(IPax)} copy constructor, preserving the full
     * subtree including attributes and children.
     *
     * @param Pax the source node to copy
     * @return a new {@link Pax} instance that is a deep copy of the source
     */
    public IPax copy(IPax Pax) {
        return new Pax(Pax);
    } // method

} // class
