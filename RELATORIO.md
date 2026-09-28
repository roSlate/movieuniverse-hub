## Relatório de testes

Todos os casos abaixo passam na suite atual (`cd backend && ./mvnw test`). Um caso que falhasse seria assinalado
aqui explicitamente.

### Modelo e repositórios

- Um `AppUser` com nome duplicado é rejeitado pela base de dados.
- Uma `Playlist` sem `owner` é rejeitada.
- O mesmo filme duas vezes na mesma playlist é rejeitado por `PlaylistMovie`.
- Uma segunda nota do mesmo utilizador para o mesmo filme é rejeitada por `UserRating`.
- Notas fora do intervalo 1–10 são rejeitadas, tanto na validação Java como na base de dados.

### Importação do seed

- A importação do `seed_playlists.json` fornecido cria 3 utilizadores, 10 playlists, 50 entradas de filme e 20 notas.
- Repetir a importação não duplica nada (todos os contadores ficam a zero).
- Playlists marcadas como apagadas são importadas e assinaladas como tal.
- Filmes que só existem em playlists apagadas são importados na mesma.
- O filme duplicado numa playlist é guardado uma única vez, mantendo a posição mais baixa.
- Reimportar não sobrepõe alterações feitas na aplicação (nome, apagada, nota).
- Uma playlist com um utilizador desconhecido é rejeitada com um erro claro.

### Nota combinada

- Um filme com 8,9 (12 votos) fica abaixo de um filme com 8,4 (30 000 votos).
- Três utilizadores a dar 10 ao filme com poucos votos não muda essa ordem.
- Um filme sem votos em lado nenhum não tem nota, e a explicação di-lo.
- Uma média sem votos associados é ignorada (não distorce o resultado).
- A nota combinada mantém-se sempre entre 0 e 10, mesmo em casos extremos.
- Número de votos negativo ou média fora de 0–10 (com votos) é rejeitado.

### Cliente TMDB e endpoints

- A pesquisa por título devolve os filmes e os campos esperados.
- Um filme sem poster, sem data ou sem votos é lido sem erros, com os campos correspondentes a `null`/0.
- Cada pedido à TMDB inclui o token no cabeçalho `Authorization`.
- Uma credencial rejeitada pela TMDB é convertida numa resposta 502, sem expor detalhes.
- `GET /api/movies/search` com título vazio é rejeitado com 400, sem chamar a TMDB.
- `GET /api/movies/{id}` combina a média da TMDB com as notas dos utilizadores da app através da mesma função de
  nota combinada.

### Playlists

- Criar uma playlist cria o utilizador se ainda não existir.
- Criar uma playlist para um utilizador já existente não duplica o utilizador.
- Listar playlists devolve apenas as que não estão apagadas.
- Adicionar um filme já presente na playlist não duplica a entrada.
- Adicionar e remover um filme funcionam em conjunto, refletidos de imediato na playlist.
- Remover um filme que não está na playlist não faz nada.
- Um pedido para uma playlist inexistente é rejeitado com 404.

### Notas dos utilizadores

- Dar uma nota a um filme cria o utilizador se ainda não existir.
- Dar uma segunda nota ao mesmo filme atualiza a nota existente em vez de duplicar.
- Notas fora do intervalo 1–10 são rejeitadas.