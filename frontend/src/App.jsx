import { useState } from "react";
import SearchScreen from "./components/SearchScreen";
import MovieDetailScreen from "./components/MovieDetailScreen";
import AboutScreen from "./components/AboutScreen";
import PlaylistsScreen from "./components/PlaylistsScreen";
import PlaylistDetailScreen from "./components/PlaylistDetailScreen";

export default function App() {
  const [view, setView] = useState("search");
  const [selectedMovieId, setSelectedMovieId] = useState(null);
  const [selectedPlaylistId, setSelectedPlaylistId] = useState(null);
  const [userName, setUserName] = useState("");

  function handleSelectMovie(tmdbId) {
    setSelectedMovieId(tmdbId);
    setView("detail");
  }

  function handleSelectPlaylist(playlistId) {
    setSelectedPlaylistId(playlistId);
    setView("playlistDetail");
  }

  return (
    <div>
      <nav>
        <button onClick={() => setView("search")}>Pesquisar</button>
        <button onClick={() => setView("playlists")}>Playlists</button>
        <button onClick={() => setView("about")}>Sobre</button>
        <input
          type="text"
          value={userName}
          onChange={(event) => setUserName(event.target.value)}
          placeholder="O teu nome"
        />
      </nav>

      {view === "search" && <SearchScreen onSelectMovie={handleSelectMovie} />}
      {view === "detail" && (
        <MovieDetailScreen tmdbId={selectedMovieId} userName={userName} onBack={() => setView("search")} />
      )}
      {view === "playlists" && (
        <PlaylistsScreen userName={userName} onSelectPlaylist={handleSelectPlaylist} />
      )}
      {view === "playlistDetail" && (
        <PlaylistDetailScreen playlistId={selectedPlaylistId} onBack={() => setView("playlists")} />
      )}
      {view === "about" && <AboutScreen />}
    </div>
  );
}