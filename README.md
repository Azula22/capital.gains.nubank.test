# Stock Tax Calculator CLI

## Overview
This project implements a command-line application (CLI) to calculate the taxes on stock market investments based on the profits or losses from stock transactions. The application calculates the tax owed on the profit or loss derived from a sequence of stock buy and sell operations.

This is a project for a coding challenge in Nubank

## How to Run It

### With Docker

1. **Build the Docker Image**:
   Navigate to the extracted directory and run the following command:
    ```bash
    docker build -t capital_gains .
    ```

2. **Run the Docker Container**:
   After building the Docker image, run the container interactively with:
    ```bash
    docker run -it capital_gains
    ```

3. **Use the Application**:
   Once the container is running, you should see the same or a similar text

    ```text
    [info] welcome to sbt 1.10.11 (Eclipse Adoptium Java 19.0.1)
    [info] loading project definition from /app/project
    [info] loading settings for project root from build.sbt...
    [info] set current project to capital.gains (in build file:/app/)
    [info] running Main 
    ```
   
   Now you can interact with the application by entering stock operations. For example:
   
    ```text
    [{"operation":"buy", "unit-cost":10.00, "quantity": 10000}, {"operation":"sell", "unit-cost":20.00, "quantity": 5000}]
   
    ```

   Example output:
   ```text
   [{"tax":0.00},{"tax":10000.00}]
    ```
## How to Run Tests

### With Docker

1. **Build the Docker Image**:
   Navigate to the extracted directory and run the following command:
    ```bash
    docker build -f Dockerfile.test -t capital-gains-tests .
    ```

2. **Run the Docker Container**:
    ```bash
    docker run --rm capital-gains-tests
    ```
   
   Once the container is running, you should see the same or a similar text

    ```text
   [info] MainTest:
   [info] - simulate console input and check console output
   [info] StocksTest:
   [info] - Case #1: Should not apply tax when the total sale amount is under $20,000
   [info] - Case #2: Should apply tax on profit when there are no prior losses, and skip tax on later loss
   [info] - Case #3: Should carry over a loss and deduct it from a later profit before applying tax
   [info] - Case #4: Should not apply tax when the average cost equals the selling price
    ```

### If you have SBT installed locally

   ```bash
    sbt test -v
   ```
---

## Technologies Used

- **Scala**: Version 3.3.6
- **Java**: Version 19.0.1
- **SBT**: Version 1.10.11
---

## Libraries Used
This project uses only two libraries:

- **zio-json**: Fast and lightweight JSON library. Chosen for its minimal footprint and strong Scala 3 support.
- **ScalaTest**: Used for writing expressive and maintainable unit tests.



## Solutions Avoided

As per the task instructions, the following assumptions and solutions were explicitly avoided:

1. State Persistence Between Simulations
2. Input Parsing Errors
3. No Over-Selling of Stocks

---

## Covered Corner Cases

While implementing the solution, I have covered a few corner cases the way I think it should work:

1. **Buying and Selling 0 Stocks**: it will be processed in a regular way
2. **Selling Stocks for 0 Dollars**: it will be processed in a regular way
3. **Buying Stocks for 0 Dollars**: I didn't know how to calculate weighted price in this situation, thus I've decided to ignore this command in total tax calculations, but it will print a 0 tax json for that exact command.

---