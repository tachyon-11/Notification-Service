package com.springboot.notificationsApp.elasticsearch.queryService;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.springboot.notificationsApp.elasticsearch.model.MessageModel;
import com.springboot.notificationsApp.exceptionHandling.NotificationException;
import org.springframework.beans.factory.annotation.Autowired;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import org.springframework.stereotype.Repository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
public class ElasticSearchQuery {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    private final String indexName = "products";


    public String createOrUpdateMessage(MessageModel sentMessage) throws IOException {

        IndexResponse response = elasticsearchClient.index(i -> i
                .index(indexName)
                .id(sentMessage.getId())
                .document(sentMessage)
        );
        if(response.result().name().equals("Created")){
            return "Message Status has been successfully added.";
        }else if(response.result().name().equals("Updated")){
            return "Message Status has been successfully updated.";
        }
        return "Error while performing the operation.";
    }

    public MessageModel getMessageById(String messageId) throws IOException{
        MessageModel sentMessage = null;
        GetResponse<MessageModel> response = elasticsearchClient.get(g -> g
                        .index(indexName)
                        .id(messageId),
                MessageModel.class
        );

        if (response.found()) {
            sentMessage = response.source();
        } else {
            throw new NotificationException("Message is not saved in elastic search with following ID");
        }

        return sentMessage;
    }

    public String deleteMessageById(String messageId) throws IOException {
        DeleteRequest request = DeleteRequest.of(d -> d.index(indexName).id(messageId));

        DeleteResponse deleteResponse = elasticsearchClient.delete(request);
        if (Objects.nonNull(deleteResponse.result()) && !deleteResponse.result().name().equals("NotFound")) {
            return "Message with id " + deleteResponse.id() + " has been deleted.";
        }
        return "Message with id " + deleteResponse.id() + " does not exist.";
    }

    public List<MessageModel> getMessagesByCreatedAtRange(Date startDate, Date endDate, int page, int size) throws IOException {
        RangeQuery rangeQuery = RangeQuery.of(r -> r
                .field("createdAt")
                .gte(JsonData.of(startDate))
                .lte(JsonData.of(endDate))
        );

        SearchResponse<MessageModel> response = elasticsearchClient.search(s -> s
                        .index(indexName)
                        .query(q -> q
                                .range(rangeQuery)
                        )
                        .sort(so -> so
                                .field(f -> f
                                        .field("createdAt")
                                        .order(SortOrder.Asc)
                                )
                        )
                        .from((page - 1) * size)
                        .size(size),
                MessageModel.class
        );

        List<MessageModel> messages = new ArrayList<>();
        for (Hit<MessageModel> hit : response.hits().hits()) {
            messages.add(hit.source());
        }

        return messages;
    }

    public List<MessageModel> searchMessagesByText(String searchText, int page, int size) throws IOException {
        Query query = Query.of(q -> q
                .match(m -> m
                        .field("message")
                        .query(searchText)
                )
        );

        SearchResponse<MessageModel> response = elasticsearchClient.search(s -> s
                        .index(indexName)
                        .query(query)
                        .from((page - 1) * size)
                        .size(size)
                        .sort(SortOptions.of(so -> so
                                .field(f -> f
                                        .field("createdAt")
                                        .order(SortOrder.Asc)
                                )
                        )),
                MessageModel.class
        );

        List<MessageModel> messages = new ArrayList<>();
        for (Hit<MessageModel> hit : response.hits().hits()) {
            messages.add(hit.source());
        }
        return messages;
    }
}
