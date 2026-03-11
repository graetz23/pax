/**
 * @brief pax
 * @details An object-tree combined with a tolerant reader to parse any XML
 * @copyright Copyright (c) 2017-2026 Christian (graetz23@gmail.com)
 * @author Christian (graetz23@gmail.com)
 * @file Instances.java
 *
 * Global singleton registry for the active {@link IFactory}. All node-
 * creation operations inside {@link Subset} (and therefore in every PAX
 * tree) go through {@code Instances.Factory().produce()} so that the
 * correct typed subclass is returned when a domain-specific factory is
 * registered.
 *
 * <p>Typical usage in a generated test's {@code @BeforeEach}:</p>
 * <pre>{@code
 *   Instances.Factory(new PaxFactory());
 * }</pre>
 *
 * <p>The default factory is a plain {@link Factory} that returns
 * {@link Pax} instances for every tag. Calling {@link #resetFactory()}
 * restores this default, which is useful between tests.</p>
 *
 * @see IFactory
 * @see Factory
 */

package de.graetz23.pax;

public class Instances {

    /** The default base factory; never replaced; used by {@link #resetFactory()}. */
    private final static IFactory _base = new Factory(); // member

    /** The currently active factory; may be replaced by a domain-specific one. */
    private static IFactory _factory = _base; // member

    /**
     * Resets the active factory back to the default {@link Factory},
     * discarding any previously registered domain-specific factory.
     * Useful for test tear-down to ensure test isolation.
     */
    public static void resetFactory() { _factory = _base; } // method

    /**
     * Returns the currently active {@link IFactory}. All implicit node
     * creation in {@link Subset} (via {@link ISubset#add(String)} etc.)
     * and in the {@link Pax} copy constructor delegates to this factory.
     *
     * @return the active factory; never {@code null}
     */
    public static IFactory Factory() {
        return _factory;
    } // method

    /**
     * Registers a new active factory, replacing the previously registered
     * one. Call this with a generated {@code PaxFactory} instance to
     * ensure that the SAX {@link Reader} and all tree operations produce
     * typed domain objects instead of plain {@link Pax} nodes.
     *
     * @param factory the factory to activate; must not be {@code null}
     */
    public static void Factory(IFactory factory) {
        _factory = factory;
    } // method

} // class
