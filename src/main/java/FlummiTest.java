import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.ning.http.client.AsyncHttpClient;
import de.otto.flummi.Flummi;
import de.otto.flummi.aggregations.AggregationBuilder;
import de.otto.flummi.aggregations.NestedAggregationBuilder;
import de.otto.flummi.aggregations.TermsBuilder;
import de.otto.flummi.query.BoolQueryBuilder;
import de.otto.flummi.query.QueryBuilders;
import de.otto.flummi.request.SearchRequestBuilder;
import de.otto.flummi.response.*;
import org.elasticsearch.index.query.QueryBuilder;


import javax.management.Query;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhengjinqiang on 16/8/4.
 */
public class FlummiTest {


    public static JsonObject buildQuery() {

        JsonObject filter = new JsonObject();
        filter.addProperty("url_path.raw", "/html/benefit/platform/100");
//        filter.addProperty("url_path.raw", "/html/benefit/platform/62");

        BoolQueryBuilder mustQuery = QueryBuilders.bool();
//            mustQuery.must(filter);
            mustQuery.must(QueryBuilders.termQuery("url_path.raw", "/html/benefit/platform/100"));
//                     .must(QueryBuilders.termQuery("url_path.raw", "/html/benefit/platform/62"));


        return mustQuery.build();
    }

    public static AggregationBuilder builders() {



        return new TermsBuilder("url_all").field("url_path.raw").size(150)

                .subAggregation(new TermsBuilder("url_all").field("url_path.raw").size(150));

    }


    public static void main(String[] args) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        Flummi flummi = new Flummi(asyncHttpClient, "http://123.56.30.138:9200");

        JsonObject filter = new JsonObject();
        JsonObject element = new JsonObject();

        filter.addProperty("url_path.raw", "/html/benefit/platform/100");
        filter.addProperty("url_path.raw", "/html/benefit/platform/62");




        SearchRequestBuilder searchRequestBuilder = flummi
                .prepareSearch("logstash-api-access-2016.08.05"
//                        "logstash-api-access-2016.07.26", "logstash-api-access-2016.07.27", "logstash-api-access-2016.07.28"
//                        , "logstash-api-access-2016.07.29", "logstash-api-access-2016.07.30", "logstash-api-access-2016.07.31"
                )
                .setSize(10)

                .setQuery(
//                        buildQuery()
//                        QueryBuilders.termQuery("status", "500").build()
                        QueryBuilders.prefixFilter("url_path.raw", "/html/benefit/platform")
                                .build()
                )

                .setQuery(buildQuery()
                )

                .addAggregation(
                        new TermsBuilder("url_all").field("url_path.raw").size(150)


                )
                .addAggregation(new TermsBuilder("uuid_all").field("http_cookie.raw").size(2000))
                        .setTimeoutMillis(150);
//        final BoolQueryBuilder query = new BoolQueryBuilder();
//        JsonObject object = new JsonObject();
//        object.addProperty("url_path","/app/init");
//
//        QueryBuilders.

        SearchResponse searchResponse = searchRequestBuilder.execute();

        Map<String,AggregationResult> resultMap = searchResponse.getAggregations();
        Set<String> set = resultMap.keySet();
        System.out.println(set.size());
//        while(set.iterator().hasNext()) {
//            System.out.println(set.iterator().next());
//        }
        AggregationResult aggreResult = searchResponse.getAggregations().get("url_all");
        List<Bucket> list = aggreResult.getBuckets();
        for (Bucket bucket : list) {
            System.out.println(bucket.getKey() + "=" + bucket.getDocCount());
        }

        System.out.println("Found " + searchResponse.getHits().getTotalHits() + " products");
////        if (aggreResult.hasNestedAggregation()) {
//            System.out.println("----------------------------------------------------------------->");
//            Map<String,AggregationResult> subAggreResultMap  = aggreResult.getNestedAggregations();
            AggregationResult aggreResult2 = searchResponse.getAggregations().get("uuid_all");


            List<Bucket> list2 = aggreResult2.getBuckets();
            for (Bucket bucket : list2) {
                System.out.println(bucket.getKey() + "=" + bucket.getDocCount());
            }
            SearchHits hit = searchResponse.getHits();
            Iterator<SearchHit> interator = hit.iterator();
            while(interator.hasNext()) {
                SearchHit hit2 = interator.next();
                System.out.println(hit2);
            }

            System.out.println("Found " + searchResponse.getHits() + " products");
//        }


    }


}
