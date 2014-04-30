package org.tju.so.node;

import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

public class SearchNode {

    private static volatile boolean isRunning = true;

    public static void main(String[] args) throws InterruptedException {
        Node node = NodeBuilder.nodeBuilder().client(false).node();
        node.start();
        while (isRunning)
            Thread.sleep(1000);
        node.close();
    }
}
