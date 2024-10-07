package com.rundong;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.logging.LogLevel;
import io.quarkus.amazon.lambda.http.model.AwsProxyRequestContext;
import io.quarkus.amazon.lambda.http.model.CognitoAuthorizerClaims;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/get-post-list")
public class GetPostList {

    @Inject
    PostService postService;

    @POST
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response<Map<String, Object>> getPostList(@Context AwsProxyRequestContext requestCtx, @Context com.amazonaws.services.lambda.runtime.Context ctx ) {
        LambdaLogger logger = ctx.getLogger();
        logger.log("function "+ ctx.getFunctionName()+ " is called\n");
        Map<String, Object> responseBody = new HashMap<>();

        // Get user ID from request context
        CognitoAuthorizerClaims claims = requestCtx.getAuthorizer().getClaims();
        String username = null;
        if (claims != null) {
            username = claims.getUsername();
        }else {
            logger.log("claims is null\n" , LogLevel.ERROR);
            return new Response<>(403, null, new Error("403", "auth header not valid."));
        }

        List<Post> posts = postService.scanRequest();
        logger.log("username: "+username+"\n", LogLevel.ERROR);
        responseBody.put("username", username);
        responseBody.put("table", posts);
        return new Response<Map<String, Object>>(200, responseBody, null);
    }
}
