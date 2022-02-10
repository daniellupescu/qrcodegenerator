## QR code generator

QR code generator demo app exposing a REST API that consumes a CSV file.

This is a SpringBoot app that contains a controller and a service.
Authentication is base on an api-key (custom http header). Edit the application.yml to put your API key.
CSV parsing is done with the opencsv library.
QR generate is done with the zxing library.

Change Maven property `<java.version>` to match the version of your JDK.

## Why this app?

This app does not pretend to be deployed as is, as a SpringBoot app on a server.
It is only a piece of code that anyone can copy into its SpringBoot app.

## Prerequisites
- Maven
- JDK 8+

## Build and run the app
In the root folder:

```sh
mvn clean package
```

```sh
mvn spring-boot:run
```

## License
[MIT](https://choosealicense.com/licenses/mit/)
