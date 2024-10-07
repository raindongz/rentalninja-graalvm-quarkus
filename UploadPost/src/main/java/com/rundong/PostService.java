package com.rundong;

import io.quarkus.amazon.dynamodb.enhanced.runtime.NamedDynamoDbTable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.util.List;

@ApplicationScoped
public class PostService {
    public static final String TABLE_NAME = "posts";
    public static final String POST_USER_ID = "user_id";
    public static final String POST_TITLE = "title";
    public static final String POST_ID_COL = "post_id";

    @Inject
    @NamedDynamoDbTable(TABLE_NAME)
    DynamoDbTable<Post> dbTable;

    public List<Post> scanRequest(){
        return dbTable.scan().items().stream().toList();
    }

    public void add(Post post){
        dbTable.putItem(post);
    }


}
