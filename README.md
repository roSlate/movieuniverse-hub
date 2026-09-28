# Movieuniverse-hub

A web app to search TMDB films, build personal playlists, rate films from 1 to 10, and see a combined rating that
weighs TMDB's votes together with the app's own users. Built for a training exercise, and not intended for production
use.

### Quick start

1. Follow [Install](#install) to get a TMDB token and set up `.env`;
2. Follow [Seed import](#seed-import) to load the sample data;
3. Follow [Run](#run) to start the backend and the frontend.

### Prerequisites

- Java 21
- Node.js 18 or newer (comes with npm)
- A free TMDB account and an API Read Access Token (see below)

### Install

1. Clone the repository at (https://github.com/roSlate/movieuniverse-hub);
2. Create a free account at [themoviedb.org](https://www.themoviedb.org) and generate an **API Read Access Token**
   from your account's API settings page;
3. In the project root, copy `.env.example` to `.env` and fill in your token:

   ```bash
   cp .env.example .env
   ```

   Then edit `.env` and set:

   ```
   TMDB_ACCESS_TOKEN=<your actual token>
   ```

   `.env` is git-ignored and must never be committed.

4. Install the frontend's dependencies:

   ```bash
   cd frontend && npm install
   ```

### Run

The quickest way to start everything is the following command at the project root:

```bash
./run.sh
```

Press Ctrl+C once to stop both servers. (If you get a "permission denied" error, run `chmod +x run.sh` once and
try again, some setups don't preserve the file's executable bit.)

If you'd rather run them separately (e.g. to see each server's own output), follow these two steps instead — both
do the same thing. Both processes must be running for the app to work.

1. In one terminal, start the backend from the `backend/` folder:

   ```bash
   cd backend && ./mvnw spring-boot:run
   ```

Wait for a line like `Started BackendApplication`. The backend serves the API at `http://localhost:8080`;

2. In a second terminal, start the frontend from the `frontend/` folder:

   ```bash
   cd frontend && npm run dev
   ```

Open the address it prints, usually `http://localhost:5173`. Calls the frontend makes to `/api/...` are
forwarded to the backend automatically.

### Seed import

The project ships with `data/seed_playlists.json`, sample data with 3 users, 10 playlists and some ratings. Import it
with:

```bash
cd backend && ./mvnw spring-boot:run -Dspring-boot.run.arguments=--seed
```

This starts the app as usual and imports the seed data on startup. Look for a log line starting with `Seed import
from ... finished:` reporting how many rows were added. The app keeps running afterwards; stop it with Ctrl+C once
the import line has appeared, or leave it running to use the app.

The import is safe to run more than once: anything already in the database is left untouched, so nothing gets
duplicated.

### Tests

Run the full backend test suite from `backend/`:

```bash
cd backend && ./mvnw test
```

It covers the domain model, repositories, the seed import (including running it twice), the combined rating rule
(including the brief's own test cases), and the TMDB client and endpoints, using a fake TMDB server so no real
network call or token is needed. See `RELATORIO.md` for a case-by-case summary.

### TMDB attribution

This product uses the TMDB API but is not endorsed or certified by TMDB. Data and images come from
[TMDB](https://www.themoviedb.org); the same notice and their logo are shown in the app's **Sobre** screen.