package org.techpleiad.plato.core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@ToString
@Builder
@Getter
public class PropertyTreeNode {

    @Builder.Default
    private final HashMap<String, PropertyTreeNode> child = new HashMap<>();
    @Builder.Default
    private boolean isLeaf = false;
    private final String property;

    public PropertyTreeNode getChild(final String... nodes) {

        for (final String node : nodes) {
            if (child.containsKey(node)) {
                return child.get(node);
            }
        }
        return null;
    }

    public void addChild(final String node, final PropertyTreeNode treeNode) {
        child.put(node, treeNode);
    }

    public void setLeaf(final boolean leaf) {
        isLeaf = leaf;
    }

    public static void constructPropertyTree(final PropertyTreeNode root, final String[] property, final int index) {

        final String prop = property[index];

        PropertyTreeNode childNode = root.getChild(prop);
        if (Objects.isNull(childNode)) {
            childNode = PropertyTreeNode.builder().property(prop).build();
            root.addChild(prop, childNode);
        }

        if (property.length == (index + 1)) {
            childNode.setLeaf(true);
            return;
        }
        constructPropertyTree(childNode, property, index + 1);
    }

    public static PropertyTreeNode convertPropertiesToPropertyTree(final List<String> properties) {

        final PropertyTreeNode suppressErrorTree = PropertyTreeNode.builder().build();

        properties.forEach(suppressError ->
                constructPropertyTree(suppressErrorTree, suppressError.split("\\."), 0)
        );

        return suppressErrorTree;
    }

    public boolean contains(final String key) {
        return child.containsKey(key);
    }
}
