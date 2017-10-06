# Phantom Framework

The *Phantom Framework* provides an intuitive API that allows you to easily write succinct and performant asynchronous code within an event-driven architecture. There are multiple interfaces and classes available that assist in composing individual pieces of code into larger programs that can be run piece-by-piece. This promotes scalability by reducing thread overhead as well as by allowing the user to differentiate between long-running (ie. blocking) and short-running (ie. non-blocking) operations and have their execution confined to separate threads. This is a similar concept also seen in other frameworks and platforms like *Node.js*, but with the exception that the *Phantom Framework* allows for it to be applied to any user code (not just a predefined set of IO operations) and on a much finer scale.

All internals of the framework are non-blocking and lock-free aside from the default task scheduler, which will block when there is currently no work to do.

## Installation

*(JAR file forthcoming)*

## Usage

### Lambdas

`Lambda` interfaces are the computational building blocks of the framework and account for all user-defined code. They consist of the four combinatorial method signatures possible from having one or zero parameters along with one or zero (`void`) return values. They are accordingly given the names `ProcedureLambda`, `ConsumerLambda`, `ProducerLambda`, and `FunctionLambda`. These are congruent to the `Runnable`, `Consumer`, `Supplier`, and `Function` interfaces in *Java SE*, but additionally implement and provide default methods for the `Builder` classes of the framework that are ubiquitously used throughout. They also provide additional default methods for composing themselves with one another, which is a feature not wholely available among their *Java SE* counterparts.

The static `Task.lambda` utility methods allow you to supply lambda expressions and will return instances of their respective `Lambda` interfaces, which may be composed and reused with different `Builder`s.

Example:
```java
FunctionLambda<Integer, Integer> square = x -> x * x;

square.pipeRight(x -> {
	System.out.println(x);
}).pipeLeft(() -> 7).start();
```

### Builders

`Builder` objects are composite structures consisting of `Lambda`s and other `Builders`. Similar to the compositional possibilities of `Lambda`s, `Builder`s can be combined with each other in various ways to specify the runtime behavior of their contained `Lambda`s (more details in the sections ahead). All builders are immutable and reusable.

### Tasks

`Task` objects are produced by `Builder`s and are what is ultimately executed by the framework. Their existence in separation of `Builder`s is necessitated both by performance and by the nature of the API. To avoid `volatile` reads and ensure immutability at compile time, each `Task` must be supplied with its successor (the one triggered upon its completion) during construction. However, the chronological order of the fluent API would require that each `Task` already be constructed before its successor is specified. This is circumvented by the `Builder` classes, which recursively contain constructors of all the preceding `Task`s in a reverse linked list fashion. This also allows `Builder`s to be immutable/reusable in an efficient manner (without making a new list etc. for each one), so that preceding `Builder`s can branch off to compose other composite `Task`s without affecting the current path. This incurs more overhead for building `Task`s, but allows better performance under the premise that `Task`s will usually be used more than once.

`Task`s are also immutable and reusable. *Everything* is immutable and reusable.

### Jobs

Let's say you wanted to perform a specific `Task` many times (which may take different inputs or return different results each time). Using the method of `java.util.concurrent.CompletableFuture`, you would need to create a new one each time, as well as any others that it may be recursively composed of. This results in the immediate instantiation of a large number of ojects that must exist in memory until they are finished with, which is especially unwieldy if many copies of the same `CompletableFuture` are running at once.

The *Phantom Framework* avoids this by instead lazily instantiating a `Job` object each time a `Task` needs to be queued; containing the `Task` and possibly an input value (which is often a result obtained from the preceding `Job`/`Task`). This way, there will only exist a "copy" of one of the constituting `Task`s at a time, instead of all of them.

### Serial Task Execution

`Lambda`s can be executed in successive order by using the static `Task.serial` utility methods, which will return a `SerialBuilder` instance of the appropriate parameterized types. After starting the `Builder`'s produced `Task` (which is lazily instantiated when first requested), the completion of each `Lambda` will queue the one that follows it.

Example:
```java
Task.serial(() -> {
	System.out.println("Task 1");
}).then(() -> {
	String s = "Hello world!";
	System.out.println("Task 2 sends '" + s + "'");
	return s;
}).then(s -> {
	int i = 7;
	System.out.println("Task 3 received '" + s + "' and sends " + i);
	return i;
}).then(i -> {
	System.out.println("Task 4 received " + i);
}).start();
```

### Parallel Task Execution

Using the static `Task.parallel` utility methods, a group of `Lambda`s can also be executed at the same time (either truly in parallel or just in any order depending upon your hardware) and queue the next `Task` once all have been completed, possibly returning an array of the results of each. This mechanism is internally implemented by passing a context object through to each subtask during execution, after which they will signal their completion (also storing a result if applicable) and queue the next `Task` if they are the last to arrive.

Example:
```java
Task.serial(
	Task.parallel((List<Integer> data) -> {
		return compute(data.subList(0, data.size() / 3));
	}).and(data -> {
		return compute(data.subList(data.size() / 3, data.size() / 3 * 2));
	}).and(data -> {
		return compute(data.subList(data.size() / 3 * 2, data.size()));
})).then(results -> {
	System.out.println(combine(results));
}).start(exampleData);
```

### Metadata
`Lambda`s created through `Builder` interfaces can also be associated with certain performance characteristics upon creation by supplying a byte value argument before the lambda expression. Of particular interest in this usage is the `Meta` class, which provides built-in byte values for specifying execution priority and duration of the lambda (as estimated by the user). These associated characteristics are used for scheduling purposes once the lambda is queued for execution. Additional metadata can be defined by the user (such as specific stages in the case of a SEDA system), but should typically exist in categories of a power of two so that they may be placed in certain bit positions and combined with other categories using the OR bitwise operator (`|`).

Example:
``` java
Task.serial(Meta.HighPriority, (String userId) -> {
	System.out.println("Deleting account with ID " + userId + "...");
	return userId;
}).then(Meta.DeferredPriority | Meta.LongDuration, userId -> {
	removeFromDataBase(userId);
	return userId;
}).then(userId -> {
	System.out.println("User with ID " + userId + " successfully pruned from DB!");
}).start("227658");
```

### Job Scheduling

`Job`s are handled either by the default `JobHandler`, or a user implementation specified using the static `Phantom.setJobHandler` method. The default `JobHandler` implementation first sorts the `Job`s by their estimated durations (specified by the values defined in the `Meta` class) and passes them to separate queues accordingly. This prevents situations where all executing threads are occupied with long-running tasks and the system seems to not be responding. The queue for `Job`s with short durations is processed by just a single thread (with priorities being ignored). The two other queues for `Job`s with moderate and long durations are both implemented as a composed set of queues, with one for each of the four available priorities. Each of these queues is constructed with a specified expected response time for the `Job`s submitted to it (the time between being added to the queue and being chosen for execution). Each queue initially submits to itself a custom `Job` that updates an `AtomicLong` field with the current system time, and then resubmits itself again if there are still more `Job`s in the queue. When a thread from the threadpool is deciding which queue to process next, it will choose the one that is most overdue in respect to its system and expected response times. At any given moment, we know there exists a `Job` that has been waiting in the queue since before the last updated system time.

### Static Type Checking

The extensive use of generics in the API and`Builder` classes allows for type checking at compile time and the use of visual static analysis aids found in most IDEs.

## Documentation

*(in progress)*

## Testing

*(in progress)*

## Contributing
Check out the [issues](https://github.com/brandon-d-mckay/phantom-framework/issues) page or make a [pull request](https://github.com/brandon-d-mckay/phantom-framework/pulls) to contribute!
