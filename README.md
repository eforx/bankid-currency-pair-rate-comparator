# Currency Exchange API

A Spring Boot application that provides API endpoints for comparing currency exchange rates between different providers.

## Features

- Get available currency pairs between providers
- Calculate currency exchange rate differences between providers
- Uses CNB (Czech National Bank) as a reference provider

## Technical features
- Logs HTTP requests and responses for better observability using Logbook
- Implements caching using Caffeine to optimize performance
- Provides health monitoring and metrics via Spring Boot Actuator endpoints (/actuator/health)
- Secures API endpoints with HTTP Basic Authentication

## Tech Stack

- Kotlin 1.9
- Spring Boot (with Spring MVC)
- Java 21

## Getting Started

### Prerequisites

- JDK 21
- Gradle

### Running the Application

```bash
./gradlew bootRun
```

By default, the application will start with the default profile. To use a specific profile:

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

The `local` Spring profile comes with predefined settings.

#### Default credentials for basic authentication
```
username: user
password: 1234
```

## API Documentation

Swagger UI is available at: `http://localhost:8080/swagger-ui.html`

API documentation in OpenAPI format: `http://localhost:8080/v3/api-docs`

Note: The Swagger UI and OpenAPI documentation are publicly accessible without authentication.

## API Endpoints

### Get Available Currency Pairs

Returns all available currency pairs between CNB and the specified provider.

```bash
curl -X GET "http://localhost:8080/api/currency/pairs?currencyExchangeProviderId=CURRENCY_API" -H "accept: application/json" -u user:1234
```

Example Response:
```json
{
  "pairs": [
    {
      "source": "USD",
      "dest": "EUR"
    },
    {
      "source": "EUR",
      "dest": "USD"
    }
  ]
}
```

### Get Currency Exchange Rate Difference

Calculates the difference in exchange rates between CNB and the specified provider for a given currency pair.

```bash
curl -X GET "http://localhost:8080/api/currency/exchange-rate-diff?currencyExchangeProviderId=CURRENCY_API&sourceCurrency=CZK&destCurrency=EUR" -H "accept: application/json" -u user:1234
```

Example Response:
```json
{
  "exchangeRateDiff": "-0.000013"
}
```

## Available Currency Exchange Providers

The application supports the following currency exchange providers:

- CNB (Czech National Bank) - Used as a reference provider
- CURRENCY_API - External currency exchange rate provider
  - https://github.com/fawazahmed0/exchange-api

## Configuration

The application supports multiple Spring profiles:

- `default`: Default configuration
- `local`: Local development configuration with preconfigured external services

Configuration for external services is specified in the respective profile's configuration file (`application.yml`, `application-local.yml`).

### HTTP Request Logging

The application uses Logbook to log HTTP requests and responses, providing enhanced visibility for API interactions. This is especially useful for debugging external service calls and API usage.

#### Logbook Configuration

Logbook is configured in the application's logback.xml file. Here's an example configuration:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    <include resource="org/springframework/boot/logging/logback/console-appender.xml" />
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
    </root>

    <logger name="org.zalando.logbook" level="TRACE" additivity="false">
        <appender-ref ref="CONSOLE" />
    </logger>
</configuration>
```

In your application.yml, you can further customize Logbook behavior:

```yaml
logbook:
  format:
    style: http
  exclude:
    - /actuator/**
  filter:
    enabled: true
  obfuscate:
    headers:
      - Authorization
      - X-Secret
  write:
    level: INFO
  predicate:
    exclude:
      - path: "/v3/api-docs"
      - path: "/v3/api-docs/**"
      - path: "/swagger-ui/**"
```

### External Service Configuration

The application uses the following properties to configure external currency exchange providers:

#### CNB (Czech National Bank)

```yaml
external:
  cnb:
    url: https://www.cnb.cz
```

#### Currency API

```yaml
external:
  currency-api:
    url: https://cdn.jsdelivr.net
```

You can override these configurations in your `application-local.yml` for local development or testing with mock services.

### Caching Configuration

The application uses Caffeine for high-performance caching to improve response times and reduce load on external services. Caching is primarily used in the currency exchange service for frequently accessed data.

#### Cache Usage

Caching is implemented in the following areas:
- Currency pairs retrieval
- Exchange rate calculations

#### Example Cache Configuration

```yaml
spring:
  cache:
    caffeine:
      spec: maximumSize=500,expireAfterWrite=600s
    cache-names:
      - getCurrencyPairs
      - exchangeRates
```

The above configuration creates two caches with a maximum size of 500 entries each, and entries expire after 10 minutes.

### Security Configuration

The application secures API endpoints using HTTP Basic Authentication. Public endpoints like Swagger UI, OpenAPI documentation, and health checks are accessible without authentication.

#### Default Credentials

Default user credentials are configured in the application-local.yml file:

```yaml
spring:
  security:
    user:
      name: user
      password: 1234
```

For production environments, you should change these default credentials.

### Example Configuration YAML

Below is an example of a complete configuration file:

```yaml
spring:
  application:
    name: currency-pair-exchange-rate-comparator

server:
  port: 8080

external:
  cnb:
    url: https://www.cnb.cz
  currency-api:
    url: https://cdn.jsdelivr.net
```

## Building for Production

```bash
./gradlew build
```

The resulting JAR will be located in `build/libs/`.

## Monitoring and Health Checks

The application includes Spring Boot Actuator for monitoring and health checks.

### Health Endpoint

The health endpoint provides information about the application's health status, including the status of external dependencies.

```bash
curl -X GET "http://localhost:8080/actuator/health" -H "accept: application/json"
```

Note: The health endpoint is publicly accessible without authentication.

Example Response:
```json
{
  "status": "UP"
}
```

By default, the following actuator endpoints are enabled:
- /actuator/health

You can enable additional endpoints in your application.yml:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.
