FROM  public.ecr.aws/lambda/provided

ADD target/UploadPost-1.0-SNAPSHOT-runner /var/runtime/bootstrap
RUN chmod ugo+x /var/runtime/bootstrap

CMD ["io.quarkus.amazon.lambda.runtime.QuarkusStreamHandler::handleRequest"]