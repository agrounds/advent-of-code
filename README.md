# Advent of Code

Solutions to [Advent of Code](https://adventofcode.com/) problems for various years, written in Kotlin.

## Requirements

To run the solutions in this project, you'll need to have these installed:

- a JDK (I've tested this with JDK 17)
- maven

You'll also need to have your puzzle input saved to your `~/data/advent-of-code/` directory, formatted by year and day.
For example, your input to 2022, day 1 should be saved to `~/data/advent-of-code/2022/day01.txt`. You can download
your puzzle input from [adventofcode.com](https://adventofcode.com) after logging in and navigating to a particular
puzzle.

## Running the code

Since this is all written in Kotlin, it's easy to run it from JetBrains's IntelliJ IDE. Alternatively, you can use the
provided `run.sh` script like so:

```shell
./run.sh 2022 1  # runs the code for year 2022, day 1's puzzle
```

By default, maven's output is not printed, which may obfuscate errors. To see full output including exceptions and
their stack traces, use the `-v` flag:

```shell
./run.sh -v 2022 1
```
