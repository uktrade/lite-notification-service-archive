package uk.gov.bis.lite.notification.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.apache.commons.lang3.StringUtils;
import uk.gov.bis.lite.notification.message.MessageQueueConsumer;
import uk.gov.bis.lite.notification.message.MessageQueueConsumerImpl;
import uk.gov.bis.lite.notification.message.SqsClient;
import uk.gov.bis.lite.notification.message.SqsClientImpl;
import uk.gov.bis.lite.notification.message.SqsPollingJob;
import uk.gov.bis.lite.notification.message.SqsPollingJobImpl;
import uk.gov.bis.lite.notification.service.TemplateService;
import uk.gov.bis.lite.notification.service.TemplateServiceImpl;
import uk.gov.service.notify.NotificationClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GuiceModule extends AbstractModule {

  @Override
  protected void configure() {
    bindConstant().annotatedWith(Names.named("templatePath")).to("/template-config.yaml");
    bind(TemplateService.class).to(TemplateServiceImpl.class);
    bind(MessageQueueConsumer.class).to(MessageQueueConsumerImpl.class);
    bind(SqsClient.class).to(SqsClientImpl.class);
    bind(SqsPollingJob.class).to(SqsPollingJobImpl.class);
  }

  @Singleton
  @Provides
  public TemplateConfig provideTemplateConfig(@Named("templatePath") String templatePath) throws IOException {
    InputStream in = getClass().getResourceAsStream(templatePath);
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    return mapper.readValue(reader, TemplateConfig.class);
  }

  @Provides
  public NotificationClient provideNotificationClient(NotificationConfiguration configuration) {
    return new NotificationClient(configuration.getNotifyApiKey(), configuration.getNotifyUrl());
  }

  @Provides
  public AmazonSQS provideAmazonSQSClient(NotificationConfiguration configuration) {
    return AmazonSQSClientBuilder.standard()
        .withCredentials(getAwsCredentials(configuration))
        .withRegion(configuration.getAwsConfig().getRegion())
        .build();
  }

  @Provides
  @Named("sqsQueueUrl")
  public String provideSqsQueueUrl(NotificationConfiguration configuration) {
    return configuration.getAwsConfig().getSqsQueueUrl();
  }

  @Provides
  @Named("sqsWaitTimeSeconds")
  public Integer provideSqsWaitTimeSeconds(NotificationConfiguration configuration) {
    return configuration.getAwsConfig().getSqsWaitTimeSeconds();
  }

  @Provides
  @Named("sqsRetryDelaySeconds")
  public Integer provideSqsRetryDelaySeconds(NotificationConfiguration configuration) {
    return configuration.getAwsConfig().getSqsRetryDelaySeconds();
  }

  private AWSCredentialsProvider getAwsCredentials(NotificationConfiguration notificationAppConfig) {
    AwsConfig awsConfig = notificationAppConfig.getAwsConfig();
    AwsCredentialsConfig awsCredentialsConfig = awsConfig.getAwsCredentialsConfig();

    if (StringUtils.isNoneBlank(awsCredentialsConfig.getProfileName())) {
      return new ProfileCredentialsProvider(awsCredentialsConfig.getProfileName());
    } else {
      String accessKey = awsCredentialsConfig.getAccessKey();
      String secretKey = awsCredentialsConfig.getSecretKey();

      if (StringUtils.isBlank(accessKey) || StringUtils.isBlank(secretKey)) {
        throw new RuntimeException("accessKey and secretKey must both be specified if no profile name is specified");
      }
      return new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));
    }
  }

}
