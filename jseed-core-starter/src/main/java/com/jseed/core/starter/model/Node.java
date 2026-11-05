package com.jseed.core.starter.model;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Node
 *
 * @author cyrus
 * @date: 2024-07-08
 */
@Data
public class Node<k, v> {
    /**
     * key
     */
    private k k;
    /**
     * Value
     */
    private v v;
    /**
     * Additional fields
     */
    private Object ext;
    /**
     * Children
     */
    private List<Node<k, v>> children;

    public static <k, v> Node<k, v> build(k k) {
        return build(k, null);
    }

    public static <k, v> Node<k, v> build(k k, v v) {
        Node<k, v> node = new Node<>();
        node.setK(k);
        node.setV(v);
        return node;
    }

    public static <k, v> Node<k, v> build(k k, v v, Serializable ext) {
        Node<k, v> node = new Node<>();
        node.setK(k);
        node.setV(v);
        node.setExt(ext);
        return node;
    }
}
