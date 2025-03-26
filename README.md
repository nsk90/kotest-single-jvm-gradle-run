Sample project to develop a Gragle Task that runs kotest tests in android multi-module project in single JVM instance.

This is neccessary when Mocks (mockk in my case) are widely used in a project. 
Gradle can only run each module's tests in new JVM instance which causes serious performance problems, as mock must be reinitialized for each class in each JVM instance. 

Initialising a mock for class (especially containing many methods) is time consuming operation.
Android Activity mock is initialised for more than a second!) as bytecode generation takes place.
Only first mock operation of a class is so long, all futher mocks are created very fast, (mockking library caches class mocking information internally).

This performance impact might be so big that it is much faster to run all test in 1 thread but with mocks cache then use default gradles parrallel strategy. 
Gradle parallels executon of tests per module (it treats tham as a separate tasks and just uses its parallel task execution feature).
