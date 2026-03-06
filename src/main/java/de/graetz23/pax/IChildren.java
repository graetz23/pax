/**
 * @brief pax
 * @details A Java written generator for plain old XML (POX) data domains
 * @copyright Copyright (c) 2017-2026 Christian (graetz23@gmail.com)
 * @author Christian (graetz23@gmail.com)
 * @file IChildren.java
 *
 * Extends {@link ISubset} with XPath-style path search capability for
 * navigating the child element tree of an {@link IPax} node. Every
 * {@link IPax} instance lazily creates a concrete implementation of this
 * interface (an inner {@code Children} class inside {@link Pax}) on first
 * access to {@link IPax#Child()}.
 *
 * The {@link #search(String)} method supports both absolute paths
 * (starting with {@code /}, resolved from the tree root) and relative
 * paths (starting with {@code ./}, resolved from the current node).
 *
 * @see IPax#Child()
 * @see ISubset
 * @see Pax
 */

package de.graetz23.pax;

public interface IChildren extends ISubset {

  // overload or add methods, if necessary

  /**
   * Searches for a node in the tree by an XPath-like path expression.
   *
   * <p>Two path forms are supported:</p>
   * <ul>
   *   <li><b>Absolute</b> – starts with {@code /}, e.g.
   *       {@code /root/child1/child4}. Navigation begins at the tree
   *       root (found by walking up the parent chain).</li>
   *   <li><b>Relative</b> – starts with {@code ./}, e.g.
   *       {@code ./child1/child4}. Navigation begins at the node that
   *       owns this children collection.</li>
   * </ul>
   *
   * <p>Redundant slashes and leading/trailing slashes are cleaned up
   * automatically before the path is resolved. Returns {@code null} on
   * any miss; no exception is thrown.</p>
   *
   * @param path the path expression with {@code /} as the separator
   * @return the found {@link IPax} node, or {@code null} if not found
   */
  IPax search(String path);

} // interface
