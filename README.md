# Transaction: Demo Java Spring Server for Real-time Data Processing

## Abstract

This is a demo Java Spring server for realtime data processing. The primary goal is to
efficiently solve a realtime problem. In this case, incoming "transactions" are to be
accumulated into statistics which can arrive out of chronological order. The server
should return, in O(1) time and space, statistics for only transactions that have occurred
in the past 60 seconds.

## Setup

The package can be build using Maven by running the following command:

```sh
$ mvn clean package
```

## Running the Server

To run the server, run the following command:

```sh
$ java -jar target/transaction-0.0.1-SNAPSHOT.jar
```

If you wish for more verbose logging, use the "dev" profile like so:

```sh
$ java -jar target/transaction-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

By default, the server will run on port 8080.

## Sending Transactions to the Server

The server uses a basic JSON format for transactions of the following form.

```json
{
  "amount": 100.00,           // A transaction amount.
  "timestamp": 1521320895682  // Timestamp in milliseconds.
}
```

The `curl` command can be used to post a simple request.

```sh
$ curl http://localhost:8080/transactions \
    -H "Content-Type: application/json" \
    -d "{\"amount\": 100.00, \"timestamp\": 1521320895682 }"
```

## Requesting Statistics over the Past Minute

Again, the `curl` command can be used to make a basic request to the `/statistics` endpoint.

```sh
$ curl http://localhost:8080/statistics
```

A simple script to send 10 reqeusts and see the current statistics is also available.

```sh
$ ./test.sh
```
