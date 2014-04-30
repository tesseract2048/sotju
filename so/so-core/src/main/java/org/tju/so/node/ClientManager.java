package org.tju.so.node;

import javax.annotation.Resource;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.stereotype.Service;

@Service
public class ClientManager {

    @Resource
    private String clusterName;

    private Client client = null;

    public Client getClient() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    Node node = NodeBuilder.nodeBuilder()
                            .clusterName(clusterName).client(true).node();
                    client = node.client();
                }
            }
        }
        return client;
    }
}
