import { useEffect, useState } from "react";
import { getMovie } from "../api";

function formatVotes(average, count) {
  if (count === 0) {
    return "sem votos";
  }
  return `${average.toFixed(1).replace(".", ",")} · ${count.toLocaleString("pt-PT")} votos`;
}

export default function MovieDetailScreen({ tmdbId, onBack }) {
  const [movie, setMovie] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    setMovie(null);
    setError(null);
    getMovie(tmdbId)
      .then(setMovie)
      .catch((err) => setError(err.message));
  }, [tmdbId]);

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
      <p>
        Combinada: {movie.combinedScore !== null
          ? `${movie.combinedScore.toFixed(1).replace(".", ",")} (${movie.combinedVotes} votos)`
          : "sem informação suficiente"}
      </p>
      <p><em>{movie.combinedExplanation}</em></p>
    </div>
  );
}