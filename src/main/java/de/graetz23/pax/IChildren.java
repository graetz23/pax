/**
 * @brief pax
 * @details An object-tree combined with a tolerant reader to parse any XML
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

import java.util.List;

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

  /**
   * Recursively searches for all nodes in the subtree whose tag equals
   * the given tag name. The search descends through all descendants at
   * any depth.
   *
   * @param tag the tag name to search for
   * @return a list of all matching {@link IPax} nodes; empty if none found
   */
  List<IPax> searchByTag(String tag);

  /**
   * Recursively searches for nodes by an XPath-like path expression.
   * The path is split by "/" and each segment represents a tag name to
   * match at each level of the tree. The search finds ALL nodes that
   * match the complete path, not just the first one.
   *
   * <p>Examples:</p>
   * <ul>
   *   <li>{@code searchByPath("/root/child/grandchild")} - finds all
   *       "grandchild" nodes under "child" under "root"</li>
   *   <li>{@code searchByPath("child/grandchild")} - relative path from
   *       current node</li>
   * </ul>
   *
   * <p>Redundant slashes are ignored. Returns an empty list if no matches
   * are found.</p>
   *
   * @param path the XPath-like path with "/" as separator
   * @return a list of all matching {@link IPax} nodes; empty if none found
   */
  List<IPax> searchByPath(String path);

} // interface
