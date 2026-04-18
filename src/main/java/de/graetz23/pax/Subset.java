/**
 * @brief pax
 * @details An object-tree combined with a tolerant reader to parse any XML
 * @copyright Copyright (c) 2017-2026 Christian (graetz23@gmail.com)
 * @author Christian (graetz23@gmail.com)
 * @file SubSet.java
 *
 * Concrete implementation of {@link ISubset}. Backs both the
 * {@link Pax.Children} and {@link Pax.Attributes} inner classes with a
 * single {@link java.util.LinkedHashMap} that preserves insertion order.
 *
 * <p><b>Duplicate-key handling:</b> XML allows sibling elements with the
 * same tag (e.g. multiple {@code <genre>} nodes). Because a map requires
 * unique keys, the second and subsequent siblings are stored under a
 * synthetic key formed as {@code tag + " " + Statics.Next()} (a global
 * auto-increment counter). The stored {@link IPax} node retains its
 * original {@link IPax#Tag()} so that {@link #all(String)} and
 * {@link #typed(String)} can still retrieve all duplicates by matching
 * against the node's tag with a case-insensitive {@code startsWith}
 * check.</p>
 *
 * @see ISubset
 * @see Pax
 * @see Statics#Next()
 */

package de.graetz23.pax;

import java.util.*;

public class Subset implements ISubset {

    /** Ordered backing store; preserves insertion order for all operations. */
    private final LinkedHashMap<String, IPax> _hashMap = new LinkedHashMap<String, IPax>();

    /** The {@link IPax} node that owns this collection. */
    private IPax _ancestor = null;

    /**
     * Constructs a new {@code Subset} bound to the given ancestor node.
     *
     * @param ancestor the owning {@link IPax} node; used as the parent
     *                 reference for every node added to this collection
     */
    public Subset(IPax ancestor) {
        _ancestor = ancestor;
    } // constructor

    /**
     * {@inheritDoc}
     */
    @Override
    public IPax Ancestor() {
        return _ancestor;
    } // method

    /**
     * {@inheritDoc}
     */
    @Override
    public void Ancestor(IPax ancestor) {
        _ancestor = ancestor;
    } // method

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasAncestor() {
        return _ancestor != null;
    } // method

    /**
     * {@inheritDoc}
     * Delegates to {@link #get(int)} with index {@code 0}.
     */
    @Override
    public IPax First() {
        IPax first = null;
        if (!_hashMap.isEmpty()) {
            first = get(0);
        } // if
        return first;
    } // method

    /**
     * Returns the last element in insertion order, or {@code null} if the
     * collection is empty.
     *
     * @return the first {@link IPax}, or {@code null}
     */
    @Override
    public IPax Last() {
        IPax last = null;
        if (!_hashMap.isEmpty()) {
            int cnt = cnt();
            if(cnt > 0) {
                last = get(cnt - 1);
            } // if
        } // if
        return last;
    } // method

    /**
     * {@inheritDoc}
     * Uses {@link LinkedHashMap#containsKey(Object)} for an O(1) lookup
     * against the exact map key. Note that duplicate entries are stored
     * under synthetic keys, so only the first occurrence matches here.
     */
    @Override
    public boolean has(String tag) {
        return _hashMap.containsKey(tag);
    } // method

    /**
     * {@inheritDoc}
     * Uses {@link LinkedHashMap#containsValue(Object)} for a reference
     * equality check across all values.
     */
    @Override
    public boolean has(IPax Pax) {
        return _hashMap.containsValue(Pax);
    } // method

    /**
     * {@inheritDoc}
     * Iterates over all map entries in insertion order until the given
     * positional index is reached. Returns {@code null} if the index
     * exceeds the collection size.
     */
    @Override
    public IPax get(int i) {
        IPax Pax = null;
        int currentIndx = 0;
        for (Map.Entry<String, IPax> entry : _hashMap.entrySet()) {
            if (currentIndx == i) {
                Pax = entry.getValue();
                break;
            } // if
            currentIndx++;
        } // loop
        return Pax;
    } // method

    /**
     * {@inheritDoc}
     * Returns the value stored under the exact tag key, or {@code null}
     * if the key is not found.
     */
    @Override
    public IPax get(String tag) {
        IPax Pax = null;
        if (has(tag)) {
            Pax = _hashMap.get(tag);
        } // if
        return Pax;
    } // method

    /**
     * {@inheritDoc}
     * If the tag is new, the node is stored under the tag itself.
     * If the tag already exists, the node is stored under a synthetic key
     * ({@code tag + " " + Statics.Next()}) to preserve both entries.
     * The new node's parent is set to the ancestor before insertion.
     */
    @Override
    public boolean add(String tag) {
        boolean wasAdded = false;
        if (!tag.isEmpty()) {
            if (!has(tag)) {
                IPax Pax = Instances.Factory().produce(tag);
                Pax.Parent(Ancestor());
                _hashMap.put(Pax.Tag(), Pax);
            } else { // a helping hand to put correctly ..
                String tag_ = tag + Statics.Separation + Statics.Next();
                IPax Pax = Instances.Factory().produce(tag);
                Pax.Parent(Ancestor());
                _hashMap.put(tag_, Pax);
            } // if
            wasAdded = true;
        } // if
        return wasAdded;
    } // method

    /**
     * {@inheritDoc}
     * Behaves identically to {@link #add(String)} but also assigns the
     * given text value to the newly created node.
     */
    @Override
    public boolean add(String tag, String val) {
        boolean wasAdded = false;
        if (!tag.isEmpty()) {
            if (!has(tag)) {
                IPax Pax = Instances.Factory().produce(tag, val);
                Pax.Parent(Ancestor());
                _hashMap.put(Pax.Tag(), Pax);
            } else { // a helping hand to put correctly ..
                String tag_ = tag + Statics.Separation + Statics.Next();
                IPax Pax = Instances.Factory().produce(tag, val);
                Pax.Parent(Ancestor());
                _hashMap.put(tag_, Pax);
            } // if
            wasAdded = true;
        } // if
        return wasAdded;
    } // method

    /**
     * {@inheritDoc}
     * Sets the node's parent to the ancestor before inserting. If the
     * node's tag is already present as a map key, a synthetic key is
     * used so that the duplicate is preserved alongside the original.
     */
    @Override
    public boolean add(IPax Pax) {
        boolean wasAdded = false;
        if (Pax != null) {
            Pax.Parent(Ancestor());
            if (!has(Pax.Tag())) {
                _hashMap.put(Pax.Tag(), Pax);
            } else { // a helping hand to put correctly ..
                String tag = Pax.Tag();
                String tag_ = tag + Statics.Separation + Statics.Next();
                _hashMap.put(tag_, Pax);
            } // if
            wasAdded = true;
        } // if
        return wasAdded;
    } // method

    /**
     * {@inheritDoc}
     * If the entry exists, updates its value in-place via
     * {@link IPax#Val(String)}. If not, delegates to
     * {@link #add(String, String)} to create a new entry.
     */
    @Override
    public boolean set(String tag, String val) {
        boolean wasSet = false;
        if (has(tag)) {
            IPax Pax = get(tag);
            if (Pax != null) {
                Pax.Val(val);
                wasSet = true;
            } // if
        } else {
            add(tag, val);
            wasSet = true;
        } // if
        return wasSet;
    } // method

    /**
     * {@inheritDoc}
     * Uses {@link LinkedHashMap#replace(Object, Object)} when the key
     * already exists, or {@link LinkedHashMap#put(Object, Object)} for
     * a new key. Unlike {@link #add(IPax)}, this method never creates
     * a duplicate entry for the same tag.
     */
    @Override
    public boolean set(IPax pax) {
        boolean wasSet = false;
        if (pax != null) {
            pax.Parent(Ancestor()); // Set the parent
            if (_hashMap.containsKey(pax.Tag())) {
                _hashMap.replace(pax.Tag(), pax); // Replace or add
            } else {
                _hashMap.put(pax.Tag(), pax);
            } // if
            wasSet = true;
        } // if
        return wasSet;
    } // method

    /**
     * {@inheritDoc}
     * Clears the parent reference of the removed node before removing
     * it from the map.
     */
    @Override
    public boolean del(String key) {
        boolean wasDeleted = false;
        if (has(key)) {
            _hashMap.get(key).Parent(null);
            _hashMap.remove(key);
            wasDeleted = true;
        } // if
        return wasDeleted;
    } // method

    /**
     * {@inheritDoc}
     * Searches all entries by reference equality to find the synthetic
     * key under which the node may be stored (important for duplicates).
     * Clears the parent reference before removal.
     */
    @Override
    public boolean del(IPax Pax) {
        boolean wasDeleted = false;
        if (has(Pax)) {
            String key_memento = null;
            IPax Pax_memento = null;
            Set<Map.Entry<String, IPax>> entries = _hashMap.entrySet();
            for (Map.Entry<String, IPax> entry : entries) {
                IPax Pax_ = entry.getValue();
                if (Pax == Pax_) {
                    key_memento = entry.getKey(); // important to take this key ..
                    Pax_memento = Pax_;
                    break;
                } // if
            } // loop
            if (key_memento != null && Pax_memento != null) { // do family business ..
                Pax_memento.Parent(null);
                _hashMap.remove(key_memento);
                wasDeleted = true;
            } // if
        } // if
        return wasDeleted;
    } // method

    /**
     * {@inheritDoc}
     * Calls {@link LinkedHashMap#clear()} to remove all entries at once.
     * Note: parent references of the removed nodes are NOT individually
     * cleared in this bulk operation.
     */
    @Override
    public boolean del() {
        boolean wasDeleted = false;
        if (!_hashMap.isEmpty()) { // delete em all ..
            _hashMap.clear();
            wasDeleted = true;
        } // if
        return wasDeleted;
    } // method

    /**
     * {@inheritDoc}
     * Delegates directly to {@link LinkedHashMap#size()}.
     */
    @Override
    public int cnt() {
        return _hashMap.size();
    } // method

    /**
     * {@inheritDoc}
     * Converts the map's value collection to an unmodifiable list via
     * the stream API. Returns an empty list when the map is empty.
     */
    @Override
    public List<IPax> all() {
        List<IPax> all = new ArrayList<>();
        if (cnt() > 0) {
            all = new ArrayList<>(_hashMap.values());
        } // if
        return all;
    } // method

    /**
     * {@inheritDoc}
     * Filters by case-insensitive {@code startsWith} match against each
     * node's {@link IPax#Tag()} (not the map key), so that duplicates
     * stored under synthetic keys are included in the result.
     */
    @Override
    public List<IPax> all(String tag) {
        List<IPax> filtered = new ArrayList<>();
        if (cnt() > 0) {
            List<IPax> all = new ArrayList<>(_hashMap.values());
            for (IPax child : all) {
                if (child.Tag().toLowerCase().startsWith(tag.toLowerCase())) {
                    filtered.add(child);
                } // if
            } // loop
        } // if
        return filtered;
    } // method

    /**
     * {@inheritDoc}
     * Casts every entry to {@code T} without filtering. Intended for
     * homogeneous collections where all entries are of the same type.
     *
     * @param <T> the target type, must extend {@link IPax}
     */
    @Override
    public <T extends IPax> List<T> typed() {
        List<T> filtered = new ArrayList<>();
        if (cnt() > 0) {
            List<IPax> all = new ArrayList<>(_hashMap.values());
            for (IPax child : all) {
                T typedPax = (T) child;
                filtered.add(typedPax);
            } // loop
        } // if
        return filtered;
    } // method

    /**
     * {@inheritDoc}
     * Combines the tag-prefix filter of {@link #all(String)} with the
     * unchecked cast of {@link #typed()}. Used by generated list-getter
     * methods such as {@code List<Book> Books()}.
     *
     * @param <T> the target type, must extend {@link IPax}
     */
    @Override
    public <T extends IPax> List<T> typed(String tag) {
        List<T> filtered = new ArrayList<>();
        if (cnt() > 0) {
            List<IPax> all = new ArrayList<>(_hashMap.values());
            for (IPax child : all) {
                if (child.Tag().toLowerCase().startsWith(tag.toLowerCase())) {
                    T typedPax = (T) child;
                    filtered.add(typedPax);
                } // if
            } // loop
        } // if
        return filtered;
    } // method

} // class
