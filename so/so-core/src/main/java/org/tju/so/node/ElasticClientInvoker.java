package org.tju.so.node;

import javax.annotation.PostConstruct;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public abstract class ElasticClientInvoker {

    @Autowired
    private ClientManager clientManager;

    protected Client client;

    @PostConstruct
    public void initClient() {
        client = clientManager.getClient();
    }

}
