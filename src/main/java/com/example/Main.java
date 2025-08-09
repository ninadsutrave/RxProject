package com.example;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.Observable;

public class Main {
  public static void main(String[] args) throws InterruptedException {
    ApiService api = new ApiService();

    // 1️⃣ map — synchronous transformation
    api.getUserById("101") // returns a Single<User> (created in ApiService using Single.fromCallable)
      .map(user -> user.name.toUpperCase()) // User -> String
      .subscribe(
        name -> System.out.println("[map] Uppercased name: " + name), // ✅ onSuccess: value received
        err -> System.err.println("[map] Error: " + err) // ❌ onError: error received
      );

    // 2️⃣ flatMap — async dependent calls
    api.getUserById("102")
      .flatMap(user -> api.getLatestOrderForUser(user.id)) // returns Single<Order>
      .subscribe(
        order -> System.out.println("[flatMap] Got order: " + order),
        err -> System.err.println("[flatMap] Error: " + err)
      );

    // 3️⃣ zip — combine results from multiple calls
    Single.zip(
      api.getUserById("103"),
      api.getLatestOrderForUser("103"),
      (user, order) -> user.name + " placed " + order.orderId
    ).subscribe(
      result -> System.out.println("[zip] " + result),
      err -> System.err.println("[zip] Error: " + err)
    );

    // 4️⃣ onErrorReturn — return fallback value
    api.getUserById("104")
      .onErrorReturn(err -> new Models.User("fallback", "Default User"))
      .subscribe(
        user -> System.out.println("[onErrorReturn] " + user),
        err -> System.err.println("[onErrorReturn] Error: " + err)
      );

    // 5️⃣ onErrorResumeNext — switch to another source
    api.getUserById("105")
      .onErrorResumeNext(err -> api.getUserById("fallback")) // call again with fallback ID
      .subscribe(
        user -> System.out.println("[onErrorResumeNext] " + user),
        err -> System.err.println("[onErrorResumeNext] Error: " + err)
      );

    // 6️⃣ doOnNext — peek at each emission (Observable example)
    Observable.range(1, 3)
      .doOnNext(n -> System.out.println("[doOnNext] Emitting: " + n))
      .map(n -> n * 10)
      .subscribe(n -> System.out.println("[doOnNext] Final: " + n));

    // 7️⃣ doOnSuccess — peek before success (Single example)
    api.getUserById("106")
      .doOnSuccess(user -> System.out.println("[doOnSuccess] Retrieved: " + user))
      .subscribe(
        user -> System.out.println("[doOnSuccess] Consumed: " + user),
        err -> System.err.println("[doOnSuccess] Error: " + err)
      );

    // 8️⃣ doOnError — peek at error without handling it
    api.getUserById("107")
      .doOnError(err -> System.err.println("[doOnError] Logging: " + err))
      .subscribe(
        user -> System.out.println("[doOnError] Got: " + user),
        err -> System.err.println("[doOnError] Consumed error: " + err)
      );

    /*
      📌 Quick Reference for RxJava methods used in ApiService:

      🔹 Single.fromCallable(...)
         - Creates a Single that runs the given Callable when subscribed to.
         - Good for wrapping synchronous code that may throw exceptions.
         - Example: Single.fromCallable(() -> fetchDataFromDb());
           → Runs fetchDataFromDb() only when someone subscribes.

      🔹 Single.defer(...)
         - Creates a Single lazily by calling a Supplier for each subscription.
         - Useful if you want a *fresh* Single each time (e.g., reading current time).
         - Example: Single.defer(() -> Single.just(System.currentTimeMillis()));
           → New time value for each subscription.

      🔹 subscribe(onSuccess, onError)
         - Starts the Single/Observable pipeline.
         - onSuccess: what to do when a value is emitted successfully.
         - onError: what to do if an error occurs.
         - Without subscribe(), nothing runs (RxJava is lazy).
    */

    // Sleep main thread to allow async calls to finish
    Thread.sleep(2000);
  }
}
