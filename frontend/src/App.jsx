import { useState } from "react";
import SearchScreen from "./components/SearchScreen";
import MovieDetailScreen from "./components/MovieDetailScreen";
import AboutScreen from "./components/AboutScreen";

export default function App() {
  const [view, setView] = useState("search");
  const [selectedMovieId, setSelectedMovieId] = useState(null);

  function handleSelectMovie(tmdbId) {
    setSelectedMovieId(tmdbId);
    setView("detail");
  }

  return (
    <div>
      <nav>
        <button onClick={() => setView("search")}>Pesquisar</button>
        <button onClick={() => setView("about")}>Sobre</button>
      </nav>

      {view === "search" && <SearchScreen onSelectMovie={handleSelectMovie} />}
      {view === "detail" && (
        <MovieDetailScreen tmdbId={selectedMovieId} onBack={() => setView("search")} />
      )}
      {view === "about" && <AboutScreen />}
    </div>
  );
}