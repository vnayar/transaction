#!/bin/bash

# Generate 10 transactions based on the current time.
for i in {1..10} ; do
    curl http://localhost:8080/transactions \
         -H "Content-Type: application/json" \
         -d "{\"amount\": $(( $RANDOM % 100 )), \"timestamp\": $(( $(date +%s) * 1000 )) }"
done


# Read the statistics out.
curl http://localhost:8080/statistics

echo ""
