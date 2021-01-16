# Notification service

Proxy service for sending emails using the [GOV.UK Notify API](https://www.notifications.service.gov.uk).

## Running locally

* `git clone git@github.com:uktrade/lite-notification-service.git`
* `cd lite-notification-service`
* `cp src/main/resources/sample-config.yaml src/main/resources/config.yaml`
* `./gradlew run`

To test changes, you should use an appropriate Notify API key which is configured for test access, and set it as the `notifyApiKey`
config option. API keys are available on the [Notify admin console](https://www.notifications.service.gov.uk/services/f3d8fb42-a34b-4d85-8ac2-a62006a197dc/api/keys).

## Overview

This service receives messages via an `sqs` queue which are then used to send emails via `uk.gov.service.notify.NotificationClient`.
The queue message must be json of type `uk.gov.bis.lite.notification.api.EmailNotification`.
Queue messages with invalid `template`, `emailAddress` or `personalisation` are logged and then deleted immediately.
Emails that fail to be send because `uk.gov.service.notify.NotificationClient` throws an exception, are retried.

The amazon credentials and queue name must be configured in the `config.yaml`.
If the credentials are saved in a credential file, `aws.credentials.profileName` needs to be specified. Alternatively, if `aws.credentials.profileName` is omitted then both `aws.credentals.accessKey` and `aws.credentals.secretKey` must be specified.
Additionally, `sqsWaitTimeSeconds` determines the time to long poll for messages, and `sqsRetryDelaySeconds` states how long to wait until a message can be retried.

Email templates are defined on the [Notify Admin Console](https://www.notifications.service.gov.uk/services/f3d8fb42-a34b-4d85-8ac2-a62006a197dc/templates).
You will need a user account for the Notify service to make changes.

`template-config.yaml` contains configuration options which enumerate the defined email templates and their variables.
This is used to validate incoming messages before they are forwarded to the Notify API. If you add or modify
email templates, **you must update `template-config.yaml` accordingly**.

### GDS PaaS Deployment

This repo contains a pre-packed deployment file, lite-notification-service-xxxx.jar.  This can be used to deploy this service manually from the CF cli.  Using the following command:

* cf push [app_name] -p lite-notification-service-xxxx.jar

For this application to work the following dependencies need to be met:

* Bound PG DB (all services share the same backend db)
* Env VARs will need to be set

### Archive state

This repo is now archived. If you need to deploy this application, you can find a copy of the DB and VARs in the DIT AWS account.
