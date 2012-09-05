# Test guide

I choose an uncomfortable but visible and easy to learn test. With this guide you can learn how to use this module and you can confirm that what you need is really working well.

## Step 1. Run the `TestWebServer`

In the `samples/tests` directory, you can see the `TestWebServer.java` verticle. You can run this on your IDE or on command line.

### On IDE

Just run the main method in the `TestWebServer`

### On Terminal

Use, `vertx run /samples/tests/TestWebServer.java` on this project's root directory. In my case, I run the test server like this:

/workspace/mod-socket-io > vertx run samples/tests/TestWebServer.java

## Step 2. Go to `http://localhost:8080/`

Open your favorite browser, and type `http://localhost:8080/'. Then, you can see an available tests list.

## Step 3. Run node.js apps and vert.x verticles.

If you want to test the `sending and receiving events`, then you should run `samples/tests/SendingAndReceivingEvents.java` and `samples/tests/SendingAndReceivingEvents.js` optionally.

### Run the verticle

Like the `TestWebServer`, you can run the verticle on IDE or on command line lie this:

/workspace/mod-socket-io > vertx run samples/tests/SendingAndReceivingEvents.java

### Run the node.js app

node samples/tests/SendingAndReceivingEvents.js

## Step 3. Enjoy the tests.

Click the [link](http://localhost:8080/tests/sending-and-receiving-events.html) and enjoy the tests and guides.

ps: If you want to test another feature, you should run the another verticle or node.js application.

Thanks.

