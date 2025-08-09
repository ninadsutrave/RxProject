package com.example;

import io.reactivex.rxjava3.core.Single;
import java.util.Random;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ApiService {
  private Random random = new Random();

  /**
   * Fetch user details by ID.
   *
   * Using Single.fromCallable():
   * - Wraps a blocking or synchronous piece of code (like DB/API calls) in a Single.
   * - Code inside the lambda runs ONLY when someone subscribes.
   * - Exceptions are caught and converted into onError() events automatically.
   */
  public Single<Models.User> getUserById(String userId) {
    return Single.fromCallable(() -> {
      if (random.nextInt(10) < 2) { // Simulated 20% failure
        throw new RuntimeException("API Error: User not found");
      }
      return new Models.User(userId, "User_" + userId);
    }).delay(300, MILLISECONDS); // Simulate network latency
  }

  /**
   * Fetch latest order for a user.
   *
   * Also uses Single.fromCallable for the same reasons as above.
   */
  public Single<Models.Order> getLatestOrderForUser(String userId) {
    return Single.fromCallable(() -> {
      if (random.nextInt(10) < 2) { // Simulated 20% failure
        throw new RuntimeException("API Error: No orders found");
      }
      return new Models.Order(
        "ORD-" + random.nextInt(1000),
        userId,
        random.nextDouble() * 100
      );
    }).delay(200, MILLISECONDS);
  }

  /**
   * Example usage of Single.defer():
   * - Unlike fromCallable, defer waits until subscription time to CREATE the Single itself.
   * - This is useful if you need to generate a fresh Single each time (e.g., random data).
   */
  public Single<String> getServerStatus() {
    return Single.defer(() ->
      Single.just("Server time: " + System.currentTimeMillis())
    );
  }
}
