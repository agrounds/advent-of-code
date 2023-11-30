#!/usr/bin/env sh

usage() {
  echo "Usage: $0 [-v] year day"
  exit 1
}

verbose=
year=
day=

for arg in "$@"; do
  if [ "$arg" = "-v" ]; then
    verbose=true
  elif [ -z "$year" ]; then
    year="$arg"
    if [ "${#year}" -ne 4 ]; then
      usage
    fi
  elif [ -z "$day" ]; then
    day="$arg"
    # add leading zero if needed
    if [ "${#day}" -eq 1 ]; then
      day="0$day"
    fi
    if [ "${#day}" -ne 2 ]; then
      usage
    fi
  else
    usage
  fi
done

if [ -z "$verbose" ]; then
  mvn exec:java -q "-Dexec.mainClass=com.groundsfam.advent.y${year}.d${day}.Day${day}Kt"
else
  mvn exec:java "-Dexec.mainClass=com.groundsfam.advent.y${year}.d${day}.Day${day}Kt"
fi
