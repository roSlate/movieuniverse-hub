import { useEffect, useState } from "react";
import { addFilmToPlaylist, getMovie, getPlaylist, removeFilmFromPlaylist, searchMovies } from "../api";

export default function PlaylistDetailScreen({ playlistId, onBack }) {
  const [playlist, setPlaylist] = useState(null);
  const [films, setFilms] = useState([]);
  const [error, setError] = useState(null);
  const [query, setQuery] = useState("");
  const [searchResults, setSearchResults] = useState([]);

  function refresh() {
    getPlaylist(playlistId)
      .then(async (data) => {
        setPlaylist(data);
        const details = await Promise.all(data.tmdbIds.map((id) => getMovie(id)));
        setFilms(details);
      })
      .catch((err) => setError(err.message));
  }

  useEffect(() => {
    refresh();
  }, [playlistId]);

  async function handleRemove(tmdbId) {
    await removeFilmFromPlaylist(playlistId, tmdbId);
    refresh();
  }

  async function handleSearch(event) {
    event.preventDefault();
    if (!query.trim()) return;
    const results = await searchMovies(query);
    setSearchResults(results);
  }

  async function handleAdd(tmdbId) {
    await addFilmToPlaylist(playlistId, tmdbId);
    setSearchResults([]);
    setQuery("");
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

  if (!playlist) {
    return <p>A carregar...</p>;
  }

  return (
    <div>
      <button onClick={onBack}>&larr; Voltar</button>
      <h1>{playlist.name}</h1>

      <ul>
        {films.map((film) => (
          <li key={film.tmdbId}>
            {film.title} {film.year && `(${film.year})`}
            <button onClick={() => handleRemove(film.tmdbId)}>Remover</button>
          </li>
        ))}
      </ul>

      <h2>Adicionar filme</h2>
      <form onSubmit={handleSearch}>
        <input
          type="text"
          value={query}
          onChange={(event) => setQuery(event.target.value)}
          placeholder="Pesquisar filmes..."
        />
        <button type="submit">Pesquisar</button>
      </form>
      <ul>
        {searchResults.map((movie) => (
          <li key={movie.tmdbId}>
            {movie.title} {movie.year && `(${movie.year})`}
            <button onClick={() => handleAdd(movie.tmdbId)}>Adicionar</button>
          </li>
        ))}
      </ul>
    </div>
  );
}