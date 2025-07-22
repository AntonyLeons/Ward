# Ward Tests

This directory contains the test suite for the Ward application. The tests are organized to mirror the structure of the main application code.

## Test Structure

```
src/test/
└── java/
    └── dev/
        └── leons/
            └── ward/
                ├── TestConfig.java                      # Test configuration for mocking dependencies
                ├── WardTest.java                       # Tests for the main application class
                ├── components/
                │   └── UtilitiesComponentTest.java     # Tests for utility components
                ├── controllers/
                │   └── InfoControllerTest.java         # Tests for REST controllers
                ├── dto/
                │   ├── ErrorDtoTest.java              # Tests for error DTOs
                │   ├── InfoDtoTest.java               # Tests for info DTOs
                │   └── ResponseDtoTest.java           # Tests for response DTOs
                ├── handlers/
                │   └── ControllerExceptionHandlerTest.java # Tests for exception handlers
                └── services/
                    ├── InfoServiceTest.java           # Tests for the info service
                    └── SetupServiceTest.java          # Tests for the setup service
```

## Running Tests

You can run the tests using Maven:

```bash
mvn test
```

Or run individual test classes using your IDE.

## Test Coverage

The test suite covers:

- Unit tests for services
- Unit tests for controllers
- Unit tests for components
- Unit tests for DTOs
- Unit tests for exception handlers

## Mocking Strategy

The tests use Mockito for mocking dependencies. The `TestConfig` class provides mock beans that can be used across multiple test classes.

## Adding New Tests

When adding new features to the application, please follow these guidelines for creating tests:

1. Create test classes that mirror the package structure of the main code
2. Use appropriate mocking for dependencies
3. Test both success and failure scenarios
4. Aim for high test coverage of business logic