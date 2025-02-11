# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

## Diagram

[Here](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5ks9K+KDvvorxLAC5wFrKaooOUCAHjysL7oeqLorE2IJoYLphm6ZIUgatLlqOJpEuGFocjAADiFJ2gK2hCiKRHiq60pJpeMHlAa8ZylhBI4Sy5Set6QYBkGIYseakYUdGMCxsG2ihqRbqdph5RydxMGdv+abITy2bYIkBiYNpKlXmmgFMcBMDTrO6DfmZnbZD2MD9oOvRAbMNmNnZZicKu3h+IEXgoOge4Hr4zDHukmSYE5F5FNQ17SAAoruyX1MlzQtA+qhPt0Xlzu2v5nECJYFegxmQR20FOvKMDcCEKCUaOol1t5aAYTxGr8aSMDkmA6naK1M7tSRTLKVJ5TUcwg3yDAyQZKkMCQIVEkRjV4KyWJCmYd1SkCfVXo0M1oyESOoxjWaEaFJaRTcOAVGjoKC1+jAQEwDy83RSg3TQEKCCgA21m8gYzCQDAirKpVpXFap9Xhb6w22R1jrOnx+29RwKDcJks3AEjo2KeNrE3RR0jYxShh4-Ni3Le10OpqZiXmfZzOOeeYB9gOpZLn5ngBRukK2ru0KPaMrJRaesUc2yP7lBUlHpVl9ijvlQbI0VbLaeU5VoAzUHsbVsH1dCJ1qATc6dZp2EY+6fUUnjFvoJdZGTVRNHUy9S0rRVa1M0banbfIqOJujxO9f1CjKmbqiwi7E2k1NNHAMqYsoIKn3GETV3+5tZuMhUjQh1hyZVTppujqo+mGWIJlsQlJRXH0Kvi+MlQ9C3KAAJIcLMJ6ZAadyzDoAMgA2g+jtM3SdwAcqOeyNKzxz13FnMuQOQ7N5XbcVB3o493330T2+-2A8fwHT6Oc+jAvvMrvz66BNgPhQNg3DwLqmRpyk33SzkzA1WvLUBoytVbBHVu1Ics956a1LjDK40CT66wgjDWWPFBKfyai1RBVldZWyNntcOdtI5B3xng7OrtE7uxmqQmmr0fZ6z9vXOGeNi6EKuhgr0mQzawnztoeOJN2TlDgJgiGKBkhpw0gQ+u2sP5cKwaMauaAjJ10AeZTuXdpBL2qqcVeXMnClg0Vo0wy5-KPwCJYbG8EJEACkICfTNoEEegM-7mDQczeWVRKR3haJ3NWbU5xDjfsASxUA4AQHglAWYRjtHFVkT0YJoTwmROifvaQnkIFzhQYzDa8oABW9i0A8MSZQZJ0BUmjE0RkgJzs2E2yIXhAapCnYoyYVQ6aW04z0S9nTVaPV1qG02qw3a9SOH2zADwoxAjJLtJonwuaAAzbwwwYCaIoRNXJxt5nADqWHMZfgtDcJats6Z10hF8mwIcww2yhT6BQLaRIqzpDdFhH9LGOMrJ+lCsAG0YiJGHOAPg50cDUzlDsTyM2SiVFl39gBEpYSInlL6mk2J7N-76NLPCspUTkWVOMaYh+gUAheBCV2L0sBgDYDfoQeIjzJYxVXu4xulQUppQyllLOP4tZlyuNkg2DdNogAanCIFocZD9MEsK6QOg47rMEbdcmHzrKpwgDoPJKBwCskziYVRhsAKopXhzDFPQ76YCAA) is a link to a sequence diagram made for Phase 2 of development.
