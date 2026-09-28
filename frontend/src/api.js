const BASE_URL = "/api";

async function request(path) {
  const response = await fetch(`${BASE_URL}${path}`);
  const body = await response.json();
  if (!response.ok) {
    throw new Error(body.detail || "Something went wrong");
  }
  return body;
}

export function searchMovies(title) {
  return request(`/movies/search?title=${encodeURIComponent(title)}`);
}

export function getMovie(tmdbId) {
  return request(`/movies/${tmdbId}`);
}