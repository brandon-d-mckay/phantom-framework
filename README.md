# Phantom Framework

The *Phantom Framework* provides an intuitive API that allows you to easily write succinct and performant asynchronous code within an event-driven architecture. There are multiple interfaces and classes available that assist in composing individual pieces of code into larger programs that can be run piece-by-piece in parallel without the need for manually managing threads. This promotes scalability by reducing thread overhead as well as by allowing the user to differentiate between long-running (ie. blocking) and short-running (ie. non-blocking) operations and have their execution confined to separate threads, greatly improving throughput and response time.

All internals of the framework are non-blocking and lock-free aside from the default task scheduler, which will block when there is currently no work to do.

## Installation

*(upload to Maven Central Repository TBA)*

## Usage

### Lambdas

`Lambda` interfaces are the computational building blocks of the framework and account for all user-defined code. They consist of the four combinatorial method signatures possible from having one or zero parameters along with one or zero (`void`) return values. They are accordingly given the names `ProcedureLambda`, `ConsumerLambda`, `ProducerLambda`, and `FunctionLambda`. These are congruent to the `Runnable`, `Consumer`, `Supplier`, and `Function` interfaces in *Java SE*, but additionally implement and provide default methods for the `Builder` classes of the framework that are ubiquitously used throughout. They also provide additional methods for composing themselves with one another, which is a feature only partially available among their *Java SE* counterparts.

As expected, `Lambda`s can be instantiated by using lambda expressions with assignments and method calls. The static `Task.lambda` utility methods also provide this functionality within ambiguous contexts. Since `Lambda`s are stateless, they may be reused with different `Builder`s.

Example:
```java
FunctionLambda<Long, Long> elapsedTime = t -> System.nanoTime() - t;

// Do some stuff

elapsedTime.pipeRight(x -> {
	System.out.println(x);
}).pipeLeft(() -> 7).start();
```

### Builders

`Builder` objects are composite structures consisting of `Lambda`s and other `Builders`. Similar to the compositional possibilities of `Lambda`s, `Builder`s can be combined with each other in various ways to specify the runtime behavior of their contained `Lambda`s (more details in the sections ahead). All builders are immutable and reusable.

### Tasks

`Task` objects are produced by `Builder`s and are what is ultimately executed by the framework. Their existence in separation of `Builder`s is necessitated both by performance and by the nature of the API. To avoid `volatile` reads and ensure immutability at compile time, each `Task` must be supplied with its successor (the one triggered upon its completion) during construction. However, the chronological order of the fluent API would require that each `Task` already be constructed before its successor is specified. This is circumvented by the `Builder` classes, which recursively contain constructors of all the preceding `Task`s in a reverse linked list fashion (essentially forming a trie). This allows `Builder`s to be immutable/reusable in an efficient manner by avoiding the creation of a nearly-identical list for each one and allows preceding `Builder`s to branch off to compose other composite `Task`s without affecting the current path. This incurs more overhead for building `Task`s, but allows for better performance under the assumption that `Builder`s will be discarded and that their `Task`s will be used more than once.

`Task`s are also immutable and reusable. *Everything* is immutable and reusable.

### Jobs

Let's say you wanted to perform a particular asynchronous operation many times (possibly taking different inputs and returning different outputs with each use). Using a [CompletableFuture](http://docs.oracle.com/javase/10/docs/api/java/util/concurrent/CompletableFuture.html), you would need to create a new instance each time, as well as new instances of any other `CompletableFuture`s that it may be recursively composed of. This results in the immediate instantiation of a large number of objects that must exist in memory until the task has completed and is no longer referenced, which is especially unwieldy if many copies of the same `CompletableFuture` are running at once.

The *Phantom Framework* avoids this by instead lazily instantiating a `Job` object each time a `Task` needs to be queued; containing the `Task` and possibly an input value (which is often a result obtained from the preceding `Job`/`Task`). This way, there will only exist a "copy" of one of the constituting `Task`s at a time, instead of all of them.

### Serial Task Execution

`Lambda`s can be executed in successive order by using the static `Task.serial` utility methods, which will return a `SerialBuilder` instance of the appropriate parameterized types. After starting the `Builder`'s produced `Task` (which is lazily instantiated and cached when first requested), the completion of each `Lambda` will queue the one that follows after it.

Example:
```java
Task.serial((String s) -> {
    System.out.println("Task #1 sends '" + s + "'");
    return s;
}).then(s -> {
    int i = 7;
    System.out.println("Task #2 received '" + s + "' and sends " + i);
    return i;
}).then(i -> {
    System.out.print("Task #3 received " + i + " and is going to sleep... ");
    try { Thread.sleep(3000); } catch(InterruptedException e) {}
    List<Integer> list = new ArrayList<>();
    System.out.println("Task #3 is waking up and sends an " + list.getClass().getSimpleName());
    return list;
}).then((Collection<Integer> collection) -> {
    System.out.println("Task #4 received an " + collection.getClass().getSimpleName());
}).start("Hello world!");
```

### Parallel Task Execution

Using the static `Task.parallel` utility methods, a group of `Lambda`s can also be executed at the same time (either truly in parallel or just in any order depending upon the system's hardware) and queue the next `Task` once all have been completed, possibly returning an array of the results of each. This mechanism is internally implemented by passing a context object through to each subtask during execution, after which they will signal their completion (also storing a result if applicable) and queue the next `Task` if they are the last to arrive.

Example:
```java
Task.serial(
	Task.parallel((List<MolecularDescriptor> data) -> {
		return compute(data.subList(0, data.size() / 3));
	}).and(data -> {
		return compute(data.subList(data.size() / 3, data.size() / 3 * 2));
	}).and(data -> {
		return compute(data.subList(data.size() / 3 * 2, data.size()));
})).then(results -> {
	aggregateAndStore(results);
	System.out.println("The computation has finished!");
}).start(molecularDescriptors);
```

### Metadata
`Lambda`s created through `Builder` interfaces can also be associated with certain runtime characteristics upon creation by supplying a byte value argument before the lambda expression. Of particular interest in this usage is the `Meta` class, which provides built-in byte values for specifying execution priority and duration of the lambda (as estimated by the user). These associated characteristics are used for scheduling purposes once the lambda is queued for execution. Additional metadata can be defined by the user (such as specific stages in the case of a SEDA system), but should typically exist in categories of a power of two so that they may be placed in certain bit positions and combined with other categories using the OR bitwise operator (`|`).

Example:
``` java
Task.serial(Meta.HighPriority, (String username) -> {
	showAlert(username + " has been invited to the chat.");
	return userId;
}).then(Meta.DeferredPriority | Meta.LongDuration, userId -> {
	removeFromDataBase(userId);
	return userId;
}).then(Meta.HighPriority, username -> {
	showAlert(username + " has joined the chat.");
}).start("user38");
```

### Job Scheduling

`Job`s are handled either by the default `JobDispatcher`, or a user implementation specified using the static `Phantom.setJobDispatcher` method.

The default `JobDispatcher` implementation first separates the `Job`s by their estimated durations (indicated by the values defined in the `Meta` class) and passes them to separate queues accordingly. This prevents situations where all executing threads are occupied with long-running tasks and the system seems to not be responding. The queue for `Job`s with short durations is processed by just a single thread (with priorities being ignored to reduce latency). The two other queues for `Job`s with moderate and long durations are both implemented as a composed set of queues, with one for each of the four available priorities. Each of these underlying queues is set with an expected response time for the `Job`s submitted to it (the time between being added to the queue and being chosen for execution). Each queue initially submits to itself a custom `Job` (referred to as a *tracker*) that will update a timestamp with the current system time, and then resubmit itself again if there are other `Job`s still remaining in the queue. When a thread from the worker pool is determining which queue to take from next, it will choose the one that is most overdue in terms of its tracker timestamp and its expected response time. At any given moment where the queue is not empty, we know there exists a `Job` (the tracker itself) that has been waiting in the queue since before the most recent tracker timestamp. When new `Job`s are added to the queue, the supplying thread will resubmit the tracker if it is not already in the queue.

### Static Type Checking

The extensive use of generics in the API and `Builder` classes allows for type checking at compile time and the use of visual static analysis aids found in most IDEs.

## Documentation

*(in progress)*

## Testing

*(in progress)*

## Contributing
Check out the [issues](https://github.com/brandon-d-mckay/phantom-framework/issues) page or make a [pull request](https://github.com/brandon-d-mckay/phantom-framework/pulls) to contribute!
