## Decisions


### Stack: Spring Boot + React

Recommended stack from the brief. I already know both from the bootcamp, and I am fairly familiar with how they work.

### Database: H2 file mode

H2 runs inside the application, so no external service is needed and the app starts with one command on a clean machine. 
File mode keeps the data across restarts. It's not meant for production, which is acceptable here.

- Location: `backend/storage/`, separate from the seed file in `data/`.
Starting from scratch means deleting that folder; the seed import will rebuild the data.

- `ddl-auto=update`: creates missing tables and keeps existing data. It only adds, so changing an entity leaves the 
old column behind. Deleting `backend/storage/` fixes that during development.

- `open-in-view=false`: database access stays inside the service layer, not while the response is being written.

- H2 console enabled: for inspecting data during local development only.

### Usernames are case-sensitive

`ana` and `Ana` count as two different users, given the request is that a name is chosen by the user, and the seed uses
lowercase names. The risk is lookalike names, but the benefit is that users can choose their preferred capitalization.

### Seed import

Deleted playlists (`pl-03`, `pl-07`) are imported and marked as deleted, and so are the films that only exist in them, 
they will be excluded from anything user-facing (home page, comparison, game).

Film 27205 appears twice in `pl-01`, the first position is kept, the second is skipped.
 
Films are identified by `tmdb_id`, never by title, so filmes with the same title with different years stay separate.

Re-running the import never changes existing rows, so edits made in the app remain.

### Combined rating

The combined rating is a Bayesian (weighted) average, the same approach IMDb uses for its Top 250. A film's own
average is mixed with a neutral value, and the number of votes decides how much the film's own average counts.

score = (sum of all rating points + anchor votes × anchor rating) / (total votes + anchor votes)

Sources: [IMDb Help](https://help.imdb.com/article/imdb/track-movies-tv/ratings-faq/G67Y87TFYYP6TWAV),
[Wikipedia](https://en.wikipedia.org/wiki/Bayesian_average)

Each vote counts the same regardless of its source: a TMDB vote and an app user's vote are pooled together and weighted 
only by count, not by where they came from.

Initial values: a neutral rating of 6.5 (roughly a typical film average) and a weight of 500 votes, meaning that at 500
real votes a film's own rating and the neutral value count equally. Both are placeholders for now and will be checked
against a sample of real TMDB data. They are constants in the code, so changing them means editing two numbers.

### TMDB client

**Credential:** the app authenticates with TMDB's API Read Access Token, sent in the `Authorization` header, not the
  short API key, which would travel in the URL of every request and runs the risk of being seen in logs or error messages;

**Configuration:** the token lives in `.env` (to never be committed) and Spring loads it with one line in
  `application.properties`, so no extra library is needed. The app must be started from the `backend/` directory;

**Language:** requests use `pt-PT`, so titles and synopses come in Portuguese. For some films TMDB has no
  Portuguese synopsis, so it may be empty.

### Ambiguities in the brief

- Anexo A lists "exportação" as a README topic. It isn't described as a feature anywhere in the functional
  requirements, so we read it as referring to the OpenAPI/Swagger spec (section 4), not a playlist-export feature.
- The architecture diagram (section 2) mentions a `popular-films.json` with aggregated data. It isn't referenced
  anywhere in the functional requirements and doesn't match the provided `seed_playlists.json`, so we treat it as
  leftover template text and ignore it.

### Starting with only one command

The brief asks the app to start with one command. Running the backend and frontend as two separate processes(each its 
own command) risked not meeting that literally, so we added `run.sh`, which starts both and stops both together on a 
single Ctrl+C. The two-terminal instructions remain in the README as an alternative for seeing each server's own output.

### Playlist management ("estrela")

The brief describes a ★ on the film card (search/listing or detail page) to add or remove a film from a playlist.
That needs an "active playlist" concept the UI doesn't have. Instead, adding a film happens from the playlist's own
page, via an inline search. Same outcome (add/remove a film from a playlist), different entry point.