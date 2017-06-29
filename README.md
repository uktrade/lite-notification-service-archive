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

The service exposes a single endpoint, `/notification/send-email`, which uses GOV.UK Notify to send an email.

The endpoint attempts to send the email as soon as the request is received. If the Notify service is unavailable, emails
are queued in a local SQLite database and are periodically retried. See `NotificationDao` and `NotificationRetryJob`.

Email templates are defined on the [Notify Admin Console](https://www.notifications.service.gov.uk/services/f3d8fb42-a34b-4d85-8ac2-a62006a197dc/templates).
You will need a user account for the Notify service to make changes.

`template-config.yaml` contains configuration options which enumerate the defined email templates and their variables. 
This is used to validate incoming `send-email` requests before they are forwarded to the Notify API. If you add or modify
email templates, **you must update `template-config.yaml` accordingly**. 