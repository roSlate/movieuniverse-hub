import { useEffect, useState } from "react";
import { createPlaylist, listPlaylists } from "../api";

export default function PlaylistsScreen({ userName, onSelectPlaylist }) {
  const [playlists, setPlaylists] = useState([]);
  const [newName, setNewName] = useState("");
  const [error, setError] = useState(null);

  function refresh() {
    listPlaylists(userName).then(setPlaylists).catch((err) => setError(err.message));
  }

  useEffect(() => {
    if (userName) refresh();
  }, [userName]);

  async function handleCreate(event) {
    event.preventDefault();
    if (!newName.trim()) return;
    try {
      await createPlaylist(userName, newName);
      setNewName("");
      refresh();
    } catch (err) {
      setError(err.message);
    }
  }

  if (!userName) {
    return <p>Escreve o teu nome acima para veres as tuas playlists.</p>;
  }

  return (
    <div>
      <h1>As minhas playlists</h1>
      {error && <p role="alert">{error}</p>}
      <form onSubmit={handleCreate}>
        <input
          type="text"
          value={newName}
          onChange={(event) => setNewName(event.target.value)}
          placeholder="Nome da nova playlist"
        />
        <button type="submit">Criar</button>
      </form>
      <ul>
        {playlists.map((playlist) => (
          <li key={playlist.id} onClick={() => onSelectPlaylist(playlist.id)}>
            {playlist.name}
          </li>
        ))}
      </ul>
    </div>
  );
}