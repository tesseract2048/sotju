package org.tju.so.node;

import javax.annotation.Resource;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.stereotype.Service;

/**
 * Manager for elasticsearch transport clients
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Service
public class ClientManager {

    @Resource
    private String clusterName;

    @Resource
    private String clusterMasterHost;

    @Resource
    private int clusterMasterPort;

    private Client client = null;

    /**
     * Get a elasticsearch client instance (singleton)
     * 
     * @return
     */
    public Client getClient() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    Settings settings = ImmutableSettings.settingsBuilder()
                            .put("cluster.name", clusterName).build();
                    client = new TransportClient(settings)
                            .addTransportAddress(new InetSocketTransportAddress(
                                    clusterMasterHost, clusterMasterPort));
                }
            }
        }
        return client;
    }
}
