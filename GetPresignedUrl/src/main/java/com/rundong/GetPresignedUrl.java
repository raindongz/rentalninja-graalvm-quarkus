package com.rundong;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.logging.LogLevel;
import io.quarkus.amazon.lambda.http.model.AwsProxyRequestContext;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import software.amazon.awssdk.services.s3.S3BaseClientBuilder;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.internal.signing.S3SigningUtils;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Path("/get-presigned")
public class GetPresignedUrl {


    @POST
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response<Map<String, Object>> getPreSignedUel(@Context AwsProxyRequestContext requestCtx, @Context com.amazonaws.services.lambda.runtime.Context ctx ) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("hello", "hello");
        try (S3Presigner s3Presigner = S3Presigner.create()) {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket("rentalninja")
                    .key(System.currentTimeMillis() + ".png")
                    .build();
            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))  // The URL will expire in 10 minutes.
                    .putObjectRequest(objectRequest)
                    .build();
            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
            String externalForm = presignedRequest.url().toExternalForm();
            responseBody.put("url", externalForm);
            return new Response<Map<String, Object>>(200, responseBody, null);
        }catch (Exception e){
            LambdaLogger logger = ctx.getLogger();
            logger.log("function "+ ctx.getFunctionName()+ " is called\n");
            logger.log("get url error: "+e, LogLevel.ERROR);
        }
        return new Response<Map<String, Object>>(400, responseBody, null);
    }
}
