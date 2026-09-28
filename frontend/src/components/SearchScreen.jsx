import { useState } from "react";
import { searchMovies } from "../api";

function formatRating(voteAverage, voteCount) {
  if (voteCount === 0) {
    return "sem votos";
  }
  const rating = voteAverage.toFixed(1).replace(".", ",");
  const votes = voteCount.toLocaleString("pt-PT");
  return `${rating} · ${votes} votos`;
}

export default function SearchScreen({ onSelectMovie }) {
  const [title, setTitle] = useState("");
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  async function handleSearch(event) {
    event.preventDefault();
    if (!title.trim()) {
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const movies = await searchMovies(title);
      setResults(movies);
    } catch (err) {
      setError(err.message);
      setResults([]);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div>
      <h1>MovieUniverse Hub</h1>
      <form onSubmit={handleSearch}>
        <input
          type="text"
          value={title}
          onChange={(event) => setTitle(event.target.value)}
          placeholder="Pesquisar filmes..."
        />
        <button type="submit" disabled={loading}>
          {loading ? "A pesquisar..." : "Pesquisar"}
        </button>
      </form>

      {error && <p role="alert">{error}</p>}

      <ul>
        {results.map((movie) => (
          <li key={movie.tmdbId} onClick={() => onSelectMovie(movie.tmdbId)}>
            {movie.posterUrl && <img src={movie.posterUrl} alt={movie.title} width="80" />}
            <div>
              <strong>{movie.title}</strong> {movie.year && `(${movie.year})`}
              <div>{formatRating(movie.voteAverage, movie.voteCount)}</div>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}