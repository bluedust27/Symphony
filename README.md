
# A Simple Bank Application
This project contains RESTful APIs to perform basic bank operations.

## List of endpoints
|      API     | Method |                Description               |                                        JSON Body                                       |
|:------------:|:------:|:----------------------------------------:|:--------------------------------------------------------------------------------------:|
| /create      | POST   | Create a new user account                | { "accountUserName" : "john", "pin":"1234", "accountNumber":12345678 }                |
| /init        | POST   | Add an initial balance to the account    | { "accountUserName" : "john", "pin":"1234", "amount":10000, "accountNumber":12345678 } |
| /deposit     | POST   | Deposit money to the account             | { "accountUserName" : "john", "pin":"1234", "amount":10000, "accountNumber":12345678 } |
| /withdraw    | POST   | Withdraw money from the account          | { "accountUserName" : "john", "pin":"1234", "amount":10000, "accountNumber":12345678 } |
| /balance     | POST   | Check current balance of the account     | { "accountUserName" : "john", "pin":"1234", "accountNumber":12345678 }                 |
| /users       | GET    | Get list of all users                    | NA                                                                                     |
| /accountscsv | GET    | Get list of all accounts in a CSV format | NA                                                                                     |

## Technology Stack
* Spring boot
* Apache Tomcat
* MySQL
* Jmeter
* InfluxDB
* Grafana

## Pre-requisites
For building and running the application you need:
* [JDK](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
* [Maven ](https://maven.apache.org/download.cgi?Preferred=ftp://ftp.osuosl.org/pub/apache/)

## Running the application locally
Below are ways to run the application on your local machine. 

 - Execute the main method in the `com.trans.BankApplication` class from your IDE.
- Alternatively, you can use the Spring Boot Maven plugin like so:
	a. Build the project using mvn clean install
	b. Run using mvn spring-boot:run

The application will start running with default settings at: **http://localhost:8080**
## Deploying on a AWS cloud instance
TBD

## Testing
Running load tests against this application using Jmeter.
### Set up
1. Download
 - [Apache JMeter](https://jmeter.apache.org/download_jmeter.cgi)
 - [Grafana](https://grafana.com/grafana/download) (for monitoring only)
 - [InfluxDB](https://portal.influxdata.com/downloads/) (for monitoring only)

2. Set Path for Jmeter in environment variables
JMETER_HOME: `\apache-jmeter-<version>`
JAVA_HOME:		 `\Java\<jdkversion>`
Add to PATH:  `%JMETER_HOME%\bin` and `%JAVA_HOME%\bin`
### Edit the test script
To edit the script in GUI mode run in powershell - `EditTestInJmeter.ps1`
### Configure test parameters
Test parameters can be configured using file - `test.properties`
### Run load tests
To start a load test run in powershell - `StartLoadtest.ps1`
### Reports
Jmeter reports for each test can be found under : `\Reports\<Date>\graphs\index.html`

## Monitoring
### Set up
[InfluxDB and Grafana setup](https://www.linkedin.com/pulse/jmeter-integration-grafanainfluxdb-real-time-monitoring-ashish-khole/?articleId=6667441580867235840)

### Realtime monitoring of Jmeter Tests
#### Grafana Dashboard
With default settings, you can view live test metrics using the dashboard available at: **http://localhost:3000/**
