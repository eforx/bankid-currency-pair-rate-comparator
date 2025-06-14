# Currency Exchange API

A Spring Boot application that provides API endpoints for comparing currency exchange rates between different providers.

## Features

- Get available currency pairs between providers
- Calculate currency exchange rate differences between providers
- Uses CNB (Czech National Bank) as a reference provider

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

## API Documentation

Swagger UI is available at: `http://localhost:8080/swagger-ui.html`

API documentation in OpenAPI format: `http://localhost:8080/v3/api-docs`

## API Endpoints

### Get Available Currency Pairs

Returns all available currency pairs between CNB and the specified provider.

```bash
curl -X GET "http://localhost:8080/api/currency/pairs?currencyExchangeProviderId=CURRENCY_API" -H "accept: application/json"
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
curl -X GET "http://localhost:8080/api/currency/exchange-rate-diff?currencyExchangeProviderId=CURRENCY_API&sourceCurrency=USD&destCurrency=EUR" -H "accept: application/json"
```

Example Response:
```json
{
  "exchangeRateDiff": 0.0123
}
```

## Available Currency Exchange Providers

The application supports the following currency exchange providers:

- CNB (Czech National Bank) - Used as a reference provider
- CURRENCY_API - External currency exchange rate provider

## Configuration

The application supports multiple Spring profiles:

- `default`: Default configuration
- `local`: Local development configuration with preconfigured external services

Configuration for external services is specified in the respective profile's configuration file (`application.yml`, `application-local.yml`).

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

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.
