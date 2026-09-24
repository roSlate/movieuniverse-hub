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