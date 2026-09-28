#!/usr/bin/env bash
set -e
trap 'kill 0' EXIT

(cd backend && ./mvnw spring-boot:run) &
(cd frontend && npm run dev) &

wait