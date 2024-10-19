# Crypto Wallet Management System
This repository contains the code base for the Crypto wallet management system.
The details including the approach methodologies, available feature, API documentations
are included in this ReadMe.

# Approach & Methodology
The approach taken in this application is a single wallet for each user with multiple wallet
items for each currency.
The flow for the user takes the below approach:
- User creates an account in the application using the `signup` API.
- User then authenticates with the `username` and `password` to generate an access token.
- User can then authorize via this token to create a wallet.
    - Wallet creation requires at-least one currency to be associated with the wallet. 
    - Wallet creation requires user to enter a pin which in future is required to do any further modifications to wallet.
    - Wallet creation happens only once, if user wishes to add support for another currency they need to add a wallet item via `add wallet item` API.
- User can send currency units to another wallet via an encrypted algorithm
    - Transfer request are authorized meaning users need to provide the token.
    - Transfer request is first validated for sufficient balance.
    - User's public key and pin are then verified.
    - After all validation are successful the transaction is created and signed with the user's private key
    - Then it's added to a blockchain


# APIs
- Users
   - `signup` : Public
   - `auhenticate` : Public
- Wallet
   - `Get wallet By ID` : Authorized
   - `Create Wallet` : Authorized
   - `Update Wallet Pin` : Authorized
   - `Add Wallet Item` : Authorized
   - `Delete Wallet` : Authorized
- Transaction
   - `Transfer Units` : Authorized

# Integrations
- The application integrate with [Coin Paprika](https://api.coinpaprika.com) for crypto market data.
- Application supports for Bitcoin and Ethereum prices.
- The approach for real time price is via Rest API using Retrofit because the requirement is to fetch price to calculate balance only.
- Websockets integrations can be taken for real time value pulling.

# Tech Stack
- Kotlin
- Java
- Spring Boot
- Spring Security
- Maven
- JWT
- Spring Data JPA
- Retrofit Client API
- Spring Doc Open API for API Documentation
- Docker

# Testing
Swagger URL: http://localhost:8080/swagger-ui/index.html