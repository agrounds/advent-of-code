#!/usr/bin/env sh

usage() {
  echo "Usage: $0 [-v] year day"
  exit 1
}

verbose=
year=
day=

# parse cli args
for arg in "$@"; do
  if [ "$arg" = "-v" ]; then
    verbose=true
  elif [ -z "$year" ]; then
    year="$arg"
  elif [ -z "$day" ]; then
    day="$arg"
  else
    usage
  fi
done

# add leading zero if needed
if [ "${#day}" -eq 1 ]; then
  day="0$day"
fi

# validate inputs
if (echo "$year" | grep -Evq '^\d{4}$'); then
  usage
fi
if (echo "$day" | grep -Evq '^\d{2}$'); then
  usage
fi

# invoke appropriate main class
if [ -z "$verbose" ]; then
  mvn exec:java -q "-Dexec.mainClass=com.groundsfam.advent.y${year}.d${day}.Day${day}Kt"
else
  mvn exec:java "-Dexec.mainClass=com.groundsfam.advent.y${year}.d${day}.Day${day}Kt"
fi
