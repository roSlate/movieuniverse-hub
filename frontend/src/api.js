const BASE_URL = "/api";

async function request(path, options = {}) {
  const response = await fetch(`${BASE_URL}${path}`, options);
  const text = await response.text();
  const body = text ? JSON.parse(text) : null;
  if (!response.ok) {
    throw new Error((body && body.detail) || "Something went wrong");
  }
  return body;
}

export function searchMovies(title) {
  return request(`/movies/search?title=${encodeURIComponent(title)}`);
}

export function getMovie(tmdbId) {
  return request(`/movies/${tmdbId}`);
}

export function listPlaylists(userName) {
  return request(`/users/${encodeURIComponent(userName)}/playlists`);
}

export function createPlaylist(userName, name) {
  return request(`/users/${encodeURIComponent(userName)}/playlists`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name }),
  });
}

export function getPlaylist(playlistId) {
  return request(`/playlists/${playlistId}`);
}

export function addFilmToPlaylist(playlistId, tmdbId) {
  return request(`/playlists/${playlistId}/movies/${tmdbId}`, { method: "PUT" });
}

export function removeFilmFromPlaylist(playlistId, tmdbId) {
  return request(`/playlists/${playlistId}/movies/${tmdbId}`, { method: "DELETE" });
}