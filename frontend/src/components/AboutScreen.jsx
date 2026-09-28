import tmdbLogo from "../assets/tmdb-logo.svg";

export default function AboutScreen() {
  return (
    <div>
      <h1>Sobre</h1>
      <p>
        MovieUniverse Hub é uma aplicação de treino para pesquisar filmes, criar playlists e dar notas,
        combinando a média da TMDB com as notas dos utilizadores da aplicação.
      </p>
      <p>
        <a href="https://www.themoviedb.org" target="_blank" rel="noreferrer">
          <img src={tmdbLogo} alt="The Movie Database (TMDB)" width="220" />
        </a>
      </p>
      <p>This product uses the TMDB API but is not endorsed or certified by TMDB.</p>
    </div>
  );
}