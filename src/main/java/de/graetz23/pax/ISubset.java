/**
 * @brief pax
 * @details A Java written generator for plain old XML (POX) data domains
 * @copyright Copyright (c) 2017-2026 Christian (graetz23@gmail.com)
 * @author Christian (graetz23@gmail.com)
 * @file ISubset.java
 *
 * Shared base interface for {@link IChildren} and {@link IAttributes}.
 * It defines all ordered-map operations that are common to both child
 * elements and XML attributes stored on an {@link IPax} node.
 *
 * Internally the backing store is a {@link java.util.LinkedHashMap} that
 * preserves insertion order. When a duplicate tag is added, a synthetic
 * key (tag + space + counter) is created so that the map key remains
 * unique while the node itself retains its original {@link IPax#Tag()}.
 * This allows multiple sibling nodes with the same tag to coexist.
 *
 * @see IChildren
 * @see IAttributes
 * @see Subset
 */
package de.graetz23.pax;

import java.util.List;

interface ISubset {

    /**
     * Returns the {@link IPax} node that owns this subset, i.e. the
     * element whose children or attributes this collection represents.
     *
     * @return the owning ancestor node; may be {@code null} if not yet set
     */
    IPax Ancestor();

    /**
     * Sets the owning ancestor node. Called once during construction to
     * bind this subset to its parent element.
     *
     * @param ancestor the owning {@link IPax} node
     */
    void Ancestor(IPax ancestor);

    /**
     * Returns {@code true} when an ancestor has been assigned to this
     * subset.
     *
     * @return {@code true} if the ancestor reference is non-null
     */
    boolean hasAncestor();

    /**
     * Returns the first element in insertion order, or {@code null} if the
     * collection is empty.
     *
     * @return the first {@link IPax}, or {@code null}
     */
    IPax First();

    /**
     * Returns {@code true} when the collection contains an entry whose map
     * key equals the given tag. Note that duplicate entries are stored
     * under synthetic keys, so only the original (first) entry is found
     * by exact key match.
     *
     * @param tag the tag name to look up
     * @return {@code true} if found
     */
    boolean has(String tag);

    /**
     * Returns {@code true} when this exact {@link IPax} instance is
     * present in the collection (identity comparison by reference).
     *
     * @param Pax the node to search for
     * @return {@code true} if the instance is contained
     */
    boolean has(IPax Pax);

    /**
     * Returns the element at the given zero-based positional index in
     * insertion order, or {@code null} if the index is out of range.
     *
     * @param i zero-based index
     * @return the {@link IPax} at position {@code i}, or {@code null}
     */
    IPax get(int i);

    /**
     * Returns the element stored under the given tag key, or {@code null}
     * if no entry with that key exists. For duplicates, only the entry
     * stored under the original (non-synthetic) key is returned.
     *
     * @param tag the tag key to look up
     * @return the matching {@link IPax}, or {@code null}
     */
    IPax get(String tag);

    /**
     * Creates a new {@link IPax} node via {@link Instances#Factory()} and
     * adds it to the collection. If a node with the same tag already
     * exists, the new node is stored under a synthetic key
     * ({@code tag + " " + counter}) so that the duplicate is preserved.
     * The parent of the newly created node is set to the ancestor.
     *
     * @param tag the XML tag name for the new node
     * @return {@code true} if the tag was non-empty and the node was added
     */
    boolean add(String tag);

    /**
     * Creates a new {@link IPax} node with the given tag and text value
     * via {@link Instances#Factory()} and adds it to the collection.
     * Duplicate-key handling is identical to {@link #add(String)}.
     *
     * @param tag the XML tag name for the new node
     * @param val the text value to assign to the new node
     * @return {@code true} if the tag was non-empty and the node was added
     */
    boolean add(String tag, String val);

    /**
     * Adds an existing {@link IPax} instance to the collection and sets
     * its parent to the ancestor. If a node with the same tag already
     * exists, the instance is stored under a synthetic key so that both
     * are retained.
     *
     * @param Pax the node to add; ignored if {@code null}
     * @return {@code true} if the node was non-null and was added
     */
    boolean add(IPax Pax);

    /**
     * Updates the text value of an existing entry, or adds a new entry if
     * no entry with the given tag is found. If the entry exists its
     * {@link IPax#Val(String)} is called; otherwise {@link #add(String, String)}
     * is invoked.
     *
     * @param tag the tag key to look up or create
     * @param val the new text value
     * @return {@code true} if the operation succeeded
     */
    boolean set(String tag, String val);

    /**
     * Replaces the existing entry that has the same tag as the given node,
     * or inserts it as a new entry if no such key exists. Unlike
     * {@link #add(IPax)}, this method never creates duplicate entries for
     * the same tag key.
     *
     * @param Pax the node to replace or insert; ignored if {@code null}
     * @return {@code true} if the operation succeeded
     */
    boolean set(IPax Pax);

    /**
     * Removes the entry with the given key from the collection and clears
     * its parent reference.
     *
     * @param key the map key of the entry to remove
     * @return {@code true} if an entry was found and removed
     */
    boolean del(String key);

    /**
     * Removes the given {@link IPax} instance from the collection by
     * searching for it by reference (not by tag key) and clears its parent
     * reference.
     *
     * @param Pax the exact instance to remove
     * @return {@code true} if the instance was found and removed
     */
    boolean del(IPax Pax);

    /**
     * Removes all entries from the collection.
     *
     * @return {@code true} if the collection was non-empty before clearing
     */
    boolean del();

    /**
     * Returns the number of entries currently in the collection. Note that
     * duplicate siblings increase this count because each is stored under
     * its own synthetic key.
     *
     * @return the entry count; zero if the collection is empty
     */
    int cnt();

    /**
     * Returns all entries in insertion order as an unordered
     * {@link List}. Returns an empty list if the collection is empty.
     *
     * @return a list of all {@link IPax} values
     */
    List<IPax> all();

    /**
     * Returns all entries whose {@link IPax#Tag()} starts with the given
     * prefix (case-insensitive). Useful for retrieving all duplicates of a
     * repeated element even when stored under synthetic keys.
     *
     * @param tag the tag-name prefix to filter by
     * @return a filtered list; empty if no matches
     */
    List<IPax> all(String tag);

    /**
     * Returns all entries cast to the type parameter {@code T}. Intended
     * for use in generated typed getter methods where all entries are
     * known to be of the same concrete class.
     *
     * @param <T> the target type, must extend {@link IPax}
     * @return a typed list of all entries
     */
    <T extends IPax> List<T> typed();

    /**
     * Returns all entries whose {@link IPax#Tag()} starts with the given
     * prefix (case-insensitive), cast to the type parameter {@code T}.
     * Used by generated list-getter methods such as {@code List<Book> Books()}.
     *
     * @param <T> the target type, must extend {@link IPax}
     * @param tag the tag-name prefix to filter by
     * @return a typed filtered list; empty if no matches
     */
    <T extends IPax> List<T> typed(String tag);

} // interface
