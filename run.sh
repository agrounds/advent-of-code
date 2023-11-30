#!/usr/bin/env sh

verbose=
year=
day=

for arg in "$@"; do
  if [ "$arg" = "-v" ]; then
    verbose=true
  elif [ -z "$year" ]; then
    year="$arg"
  elif [ -z "$month" ]; then
    day="$arg"
  else
  echo "Usage: $0 [-v] year day"
  exit 1
  fi
done

if [ -z "$verbose" ]; then
  mvn exec:java -q "-Dexec.mainClass=com.groundsfam.advent.y${year}.d${day}.Day${day}Kt"
else
  mvn exec:java "-Dexec.mainClass=com.groundsfam.advent.y${year}.d${day}.Day${day}Kt"
fi
