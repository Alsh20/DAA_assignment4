# DAA Assignment 4 - Ospanov Alisher - SE - 2526

## Build and run

- Run mvn test to compile the code and execute the JUnit 5 suite
- Run java -cp target/classes Benchmark to regenerate results/results.csv

## Counter meanings

work_units counts recursive calls for plain recursion, DP-cell updates for the table methods, and character comparisons for string search. For KMP, it includes LPS construction comparisons. storage_cells counts allocated integer cells for edit-distance storage; the table counts every cell in its two-dimensional array and the two-row method counts both row arrays. Time is reported per run in milliseconds
## Files

- results/results.csv contains 184 raw timing rows
- results/plots/ contains the three required charts
- src/main/java contains all five algorithm variants, counters, input generators, and the benchmark
- src/test/java contains the JUnit 5 checks