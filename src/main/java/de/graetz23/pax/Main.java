/**
 * @brief pax
 * @details A Java written generator for plain old XML (POX) data domains
 * @copyright Copyright (c) 2017-2026 Christian (graetz23@gmail.com)
 * @author Christian (graetz23@gmail.com)
 * @file Main.java
 *
 * Interactive demonstration class for the PAX library. Shows how to
 * construct an {@link IPax} node tree programmatically, serialize it to
 * XML, write it to disk, read it back, and perform both absolute and
 * relative XPath-like path searches.
 *
 * <p>This class is intended as a quick-start reference and integration
 * smoke-test, not as a production entry point. The actual code generator
 * is located in the {@code de.graetz23.pox} package.</p>
 *
 * @see IPax
 * @see Reader
 * @see Writer
 * @see IChildren#search(String)
 */

package de.graetz23.pax;

public class Main {

    /**
     * Demonstrates the core PAX API:
     * <ol>
     *   <li>Builds a small node tree with children, nested children, and
     *       attributes programmatically via {@link IPax#Child()} and
     *       {@link IPax#Attrib()}.</li>
     *   <li>Serializes the tree to an indented XML string and prints it.</li>
     *   <li>Writes the XML to {@code root.xml} on disk via
     *       {@link Writer#XML(IPax, String)}.</li>
     *   <li>Reads the file back via {@link Reader#parse(String)} and
     *       prints the reloaded XML to verify round-trip fidelity.</li>
     *   <li>Searches the tree using absolute paths
     *       (e.g. {@code /root/child1/child4}), relative paths
     *       (e.g. {@code ./child1/child4}), and paths with redundant
     *       slashes to show the tolerance of
     *       {@link IChildren#search(String)}.</li>
     * </ol>
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {

        System.out.println("Hello and welcome!");

        IPax root = Instances.Factory().produce("root"); // produce a pax node as named root
        root.Child().add("child1"); // add a child node
        root.Child().add("child2");
        IPax child3 = Instances.Factory().produce("child3");
        root.Child().add(child3);
        root.Child().get("child2").Attrib().add("is", "active"); // get a child by tag and add and attribute
        IPax child3_ = root.Child().get("child3");
        child3_.Attrib().add("is", "inactive");
        root.Child().get("child1").Child().add("child4"); // add child node to another by tag
        root.Child().get("child1").Child().get("child4").Attrib().add("is", "active");

        String xml = root.XML(); // generate XML from the node tree
        System.out.println(xml);

        XmlWriter.Instance.XML(root, "root.xml"); // write XML to drive

        IPax loaded = XmlReader.Instance.parse("./root.xml"); // parse XML to node tree
        String xml_ = loaded.XML(); // generate XMl from loaded
        System.out.println(xml_);

        // search for some nodes by absolute or relative path; no wildcards yet

        String xpath_absolute1 = "/root/child1/child4/"; // hit
        IPax found1 = root.Child().search(xpath_absolute1);

        String xpath_absolute2 = "/root/child1/child5/"; // miss
        IPax found2 = root.Child().search(xpath_absolute2);

        String xpath_relative1 = "./child1/child4/"; // hit
        IPax found3 = root.Child().search(xpath_relative1);

        String xpath_relative2 = "./child1/child5/"; // miss
        IPax found4 = root.Child().search(xpath_relative2);

        IPax found5 = found3.Child().search("/root"); // somewhere

        IPax found6 = found3.Child().search("///root///////child1////"); // somewhere

        boolean stopHere = true;

    } // main

} // class
