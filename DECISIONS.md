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

Initial values: a neutral rating of 6.5 (roughly a typical film average) and a weight of 500 votes, meaning that at 500
real votes a film's own rating and the neutral value count equally. Both are placeholders for now and will be checked
against a sample of real TMDB data. They are constants in the code, so changing them means editing two numbers.