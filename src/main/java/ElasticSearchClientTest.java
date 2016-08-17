import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import org.apache.log4j.Priority;
import org.elasticsearch.node.NodeBuilder;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by zhengjinqiang on 16/7/29.
 */
public class ElasticSearchClientTest {


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        // on startup
        Settings settings = Settings.settingsBuilder().put("cluster.name", "logstats")
                .put("client.transport.sniff", true).build();
        Client client;
        client = TransportClient.builder().settings(settings).build()
//                .addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress("123.56.9.89", 9300)));
                .addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress("http://127.0.0.1", 9300)));
                //.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(""), 9200));
        /*Settings settings = ImmutableSettings.settingsBuilder()
                .put("client.transport.ignore_cluster_name", true)
                .put("client.transport.sniff", true)
                .build();

        client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("test13.kingsilk.xyz", 9300));*/
// on shutdown
        //IndexResponse response = client.prepareIndex("logstash-api-access-2016.07.25", null).get();

        SearchResponse response = client.prepareSearch().setIndices("logstash-api-access-2016.07.25")
                              .setQuery(QueryBuilders.matchQuery("url_path","/app/init")).execute().get();

        System.out.println("searchRange: " + response);
        client.close();
    }

}
