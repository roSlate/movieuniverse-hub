import { useEffect, useState } from "react";
import { getMovie, rateFilm } from "../api";

function formatVotes(average, count) {
  if (count === 0) {
    return "sem votos";
  }
  return `${average.toFixed(1).replace(".", ",")} · ${count.toLocaleString("pt-PT")} votos`;
}

export default function MovieDetailScreen({ tmdbId, userName, onBack }) {
  const [movie, setMovie] = useState(null);
  const [error, setError] = useState(null);
  const [myStars, setMyStars] = useState(5);

  function refresh() {
    getMovie(tmdbId)
      .then(setMovie)
      .catch((err) => setError(err.message));
  }

  useEffect(() => {
    setMovie(null);
    setError(null);
    refresh();
  }, [tmdbId]);

  async function handleRate(event) {
    event.preventDefault();
    if (!userName) return;
    await rateFilm(userName, tmdbId, Number(myStars));
    refresh();
  }

  if (error) {
    return (
      <div>
        <button onClick={onBack}>&larr; Voltar</button>
        <p role="alert">{error}</p>
      </div>
    );
  }

  if (!movie) {
    return <p>A carregar...</p>;
  }

  return (
    <div>
      <button onClick={onBack}>&larr; Voltar</button>
      <h1>{movie.title} {movie.year && `(${movie.year})`}</h1>
      {movie.posterUrl && <img src={movie.posterUrl} alt={movie.title} width="200" />}
      <p>{movie.overview}</p>
      <p>Géneros: {movie.genres.join(", ")}</p>
      <p>Duração: {movie.runtimeMinutes ? `${movie.runtimeMinutes} min` : "desconhecida"}</p>

      <h2>Notas</h2>
      <p>TMDB: {formatVotes(movie.tmdbAverage, movie.tmdbVotes)}</p>
      <p>Utilizadores: {formatVotes(movie.appAverage, movie.appVotes)}</p>
      <p>
        Combinada: {movie.combinedScore !== null
          ? `${movie.combinedScore.toFixed(1).replace(".", ",")} (${movie.combinedVotes} votos)`
          : "sem informação suficiente"}
      </p>
      <p><em>{movie.combinedExplanation}</em></p>

      <h2>A tua nota</h2>
      {userName ? (
        <form onSubmit={handleRate}>
          <select value={myStars} onChange={(event) => setMyStars(event.target.value)}>
            {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((n) => (
              <option key={n} value={n}>{n}</option>
            ))}
          </select>
          <button type="submit">Dar nota</button>
        </form>
      ) : (
        <p>Escreve o teu nome no topo da página para dares uma nota.</p>
      )}
    </div>
  );
}