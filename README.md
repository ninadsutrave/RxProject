# RxJava Reference – Reactive Types & Transformation Operators

## 1. Reactive Types in RxJava

| Reactive Type  | Emits                              | When to Use |
|----------------|------------------------------------|-------------|
| **Observable<T>** | 0…N items                         | Use when you have a sequence of data/events that may emit multiple items over time, such as UI events, sensor readings, or a list of database results. |
| **Single<T>**     | Exactly 1 item (or error)         | Use for a one-time asynchronous computation that will always emit exactly one item or fail, such as a network call, DB fetch, or reading a single file. |
| **Maybe<T>**      | 0 or 1 item                       | Use when the result may or may not exist — for example, finding a user by ID in a DB where they might not be present. |
| **Completable**   | No items (just complete/error)    | Use for operations that perform work but do not return a value, such as saving to a database, sending analytics events, or deleting a file. |
| **Flowable<T>**   | 0…N items (with backpressure)     | Use for large or unbounded data streams where the consumer may not keep up with the producer, e.g., reading logs, network packets, or continuous data feeds. |

---

## 2. `.map()` vs `.flatMap()` Comparison

| Feature                   | `.map()`                                                  | `.flatMap()`                                                                           |
|---------------------------|-----------------------------------------------------------|----------------------------------------------------------------------------------------|
| **Return type of lambda** | `T → R` (plain value)                                     | Transforms each emitted item into another reactive source (Observable, Single, etc.)   |
| **Use case**              | Transform value synchronously and quickly                 | Switch to another reactive source (often async or I/O)                                 |
| **Threading**             | Runs on current scheduler                                 | Respects scheduler of returned reactive source                                         |
| **Flattening**            | No flattening needed                                      | Flattens inner reactive sources into one stream                                        |
| **Blocking allowed?**     | Should be non-blocking                                    | Can wrap blocking calls with `fromCallable()` and switch scheduler                     |
| **Example**               | Convert `"foo"` → `"FOO"`                                 | Fetch user data from API after getting username                                        |
| **Analogy**               | “Change this thing into another thing”                    | “Use this thing to get something else”                                                 |

---

## 3. Examples

### Using `.map()` — Synchronous transformation
```java
Single.just("monday")
    .map(String::toUpperCase) // monday -> MONDAY
    .subscribe(System.out::println);
```

### Using .flatMap() — Switch to another reactive source
```java
Single.just("user_123")
    .flatMap(username -> fetchUserFromApi(username)) // returns Single<User>
    .subscribe(System.out::println);
```

.subscribe() starts the data flow in a reactive chain by connecting the producer (source) to a consumer (observer).
It tells RxJava: "Okay, I’m ready — start emitting and send me the results or errors."

### Flatmap "flattens inner reactive sources into one stream"

####  What does this even mean?

When .flatMap() returns another reactive source, it merges the inner emissions into the outer stream.
Without it, you’d end up with nested streams like Observable<Observable<T>>.

```java
// Without flattening (map)
Observable.just("apple", "banana")
.map(fruit -> Observable.just(fruit.toUpperCase()))
.subscribe(System.out::println);
// Prints Observable objects, not fruits
// io.reactivex.rxjava3.internal.operators.observable.ObservableJust@4b67cf4d
// io.reactivex.rxjava3.internal.operators.observable.ObservableJust@7ea987ac

// With flattening (flatMap)
Observable.just("apple", "banana")
.flatMap(fruit -> Observable.just(fruit.toUpperCase()))
.subscribe(System.out::println);
// Prints: APPLE, BANANA
```
