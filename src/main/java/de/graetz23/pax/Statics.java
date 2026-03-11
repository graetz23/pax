/**
 * @brief pax
 * @details An object-tree combined with a tolerant reader to parse any XML
 * @copyright Copyright (c) 2017-2026 Christian (graetz23@gmail.com)
 * @author Christian (graetz23@gmail.com)
 * @file Statics.java
 *
 * Shared static utilities used across the PAX library. Provides:
 * <ul>
 *   <li>The platform line separator for XML output formatting</li>
 *   <li>A space string used as a separator when building synthetic
 *       duplicate-key names in {@link Subset}</li>
 *   <li>A global indentation state machine used by {@link XmlGenerator}
 *       to produce pretty-printed XML</li>
 *   <li>A global auto-increment counter used by {@link Subset} to
 *       generate unique map keys for duplicate sibling elements</li>
 * </ul>
 *
 * <p><b>Note:</b> The indentation state is global and not thread-safe.
 * It is incremented and decremented by {@link XmlGenerator} during
 * recursive serialization; concurrent use from multiple threads would
 * produce incorrect indentation.</p>
 *
 * @see Subset
 * @see XmlGenerator
 */

package de.graetz23.pax;

public class Statics {

    /**
     * The platform-specific line separator (e.g. {@code \n} on Linux,
     * {@code \r\n} on Windows). Used by {@link XmlGenerator} to terminate
     * each line of pretty-printed XML output.
     */
    public static final String LineSeparator = System.lineSeparator(); // member

    /**
     * A single space character {@code " "}. Used by {@link Subset} as
     * the separator between a tag name and the auto-increment counter when
     * forming a synthetic map key for duplicate sibling elements, e.g.
     * {@code "genre 1"}, {@code "genre 2"}.
     */
    public static final String Separation = " "; // member

    /** Number of spaces per indentation level for XML pretty-printing. */
    private static int _sizeIndent = 2; // member

    /** Current accumulated indentation depth in spaces. */
    private static int _currentIndent = 0; // member

    /**
     * Returns a string of spaces representing the current indentation
     * level. Called by {@link XmlGenerator} before writing each XML line.
     *
     * @return a string of {@code _currentIndent} space characters
     */
    public static String Indent() {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < _currentIndent; i++) {
            indent.append(" ");
        } // loop
        return indent.toString();
    } // method

    /**
     * Increases the current indentation depth by {@link #_sizeIndent}
     * spaces. Called by {@link XmlGenerator} when descending into a
     * child element.
     */
    public static void incIndent() {
        _currentIndent += _sizeIndent;
    } // method

    /**
     * Decreases the current indentation depth by {@link #_sizeIndent}
     * spaces. If the result would go below zero, the depth is clamped
     * to zero. Called by {@link XmlGenerator} when ascending back from
     * a child element.
     */
    public static void decindent () {
        _currentIndent -= _sizeIndent;
        if(_currentIndent < 0) {
            _currentIndent = 0;
        } // if
    } // method

    /** Global auto-increment counter for generating unique synthetic map keys. */
    private static long _cnt = 0; // member

    /**
     * Returns the next value of the global auto-increment counter and
     * advances it by one. Used by {@link Subset#add(String)} and
     * {@link Subset#add(IPax)} to build unique synthetic map keys for
     * duplicate sibling elements, e.g. storing {@code "genre 1"},
     * {@code "genre 2"} for two {@code <genre>} children.
     *
     * @return the next counter value (starts at 1 on first call)
     */
    public static long Next() {
        _cnt = _cnt + 1;
        return _cnt;
    } // method

} // class
