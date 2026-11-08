API do RouteMath (guia para o front-end)

Base URL local: http://localhost:8080 Formato: JSON (Content-Type: application/json).

Valores fixos
Campo	Valores aceitos
algoritmo	HELD_KARP (ótimo), VIZINHO_MAIS_PROXIMO (heurística)
tipo (local)	DEPOSITO, CLIENTE
Fluxo recomendado para a tela
Cadastrar o depósito e os clientes (POST /api/locais).
Cadastrar as distâncias entre os locais (POST /api/distancias).
Escolher depósito, clientes e algoritmo e calcular (POST /api/rotas/calcular-por-locais).
Para comparar os dois algoritmos, chamar o passo 3 duas vezes (uma com cada algoritmo) e calcular a diferença no front: (distanciaGulosa - distanciaOtima) / distanciaOtima * 100.
Locais
POST /api/locais (201)
json
{ "nome": "Cliente A", "tipo": "CLIENTE", "latitude": -23.5, "longitude": -46.8 }

latitude e longitude são opcionais. Resposta:

json
{ "id": 2, "nome": "Cliente A", "tipo": "CLIENTE", "latitude": -23.5, "longitude": -46.8 }
GET /api/locais e GET /api/locais/{id}

Lista de locais (ou um local). Id inexistente: 404.

Distâncias
POST /api/distancias (201)
json
{ "origemId": 1, "destinoId": 2, "distanciaKm": 4.0, "bidirecional": true }
bidirecional é opcional (padrão true): grava também o sentido contrário.
Use false para rua de mão única.
Repetir o mesmo par atualiza a distância.
distanciaKm deve ser maior que zero; origem e destino devem ser diferentes.

Resposta: lista com 1 ou 2 itens:

json
[
{ "id": 1, "origemId": 1, "origemNome": "Depósito Central",
"destinoId": 2, "destinoNome": "Cliente A", "distanciaKm": 4.0 }
]
GET /api/distancias

Lista todas as distâncias cadastradas (um item por sentido).

Cálculo de rota
POST /api/rotas/calcular-por-locais (usa os locais e as distâncias cadastrados)
json
{ "depositoId": 1, "clientesIds": [2, 3, 4], "algoritmo": "HELD_KARP" }

Resposta:

json
{
"rotaIds": [1, 4, 3, 2, 1],
"rotaNomes": ["Depósito Central", "Cliente C", "Cliente B", "Cliente A", "Depósito Central"],
"distanciaTotal": 14.0,
"algoritmo": "HELD_KARP",
"otima": true,
"tempoCalculoMs": 0.12
}
A rota começa e termina no depósito.
otima só é true no Held-Karp.
O Held-Karp aceita no máximo 18 locais no total (depósito + clientes). Acima disso: 400.
Se faltar distância entre algum par, o 400 lista os pares que faltam.
POST /api/rotas/calcular (matriz informada na hora, sem banco)
json
{ "matriz": [[0,4,7,3],[4,0,2,6],[7,2,0,5],[3,6,5,0]], "deposito": 0, "algoritmo": "HELD_KARP" }

Resposta:

json
{ "rota": [0,3,2,1,0], "distanciaTotal": 14.0, "algoritmo": "HELD_KARP", "otima": true, "tempoCalculoMs": 0.1 }

rota traz posições da matriz (começando em 0), não ids de locais.

POST /api/rotas/comparar (matriz informada, roda os dois algoritmos)
json
{ "matriz": [[0,4,7,3],[4,0,2,6],[7,2,0,5],[3,6,5,0]], "deposito": 0 }

Resposta:

json
{
"otima":  { "rota": [0,3,2,1,0], "distanciaTotal": 14.0, "algoritmo": "HELD_KARP", "otima": true, "tempoCalculoMs": 0.1 },
"gulosa": { "rota": [0,3,2,1,0], "distanciaTotal": 14.0, "algoritmo": "VIZINHO_MAIS_PROXIMO", "otima": false, "tempoCalculoMs": 0.01 },
"diferencaKm": 0.0,
"diferencaPercentual": 0.0
}
GET /api/rotas/exemplo e GET /api/rotas/exemplo/{algoritmo}

Demonstração com uma matriz de 6 locais embutida (não grava no histórico). Servem para testar a conexão do front.

Histórico
GET /api/rotas

Cálculos salvos, do mais recente para o mais antigo:

json
[
{ "id": 9, "algoritmo": "HELD_KARP", "rota": [1,4,3,2,1], "distanciaTotal": 14.0,
"otima": true, "tempoCalculoMs": 0.12, "dataCalculo": "2026-11-01T15:33:11.093405" }
]
GET /api/rotas/{id}

Um item do histórico. Id inexistente: 404.

Observação: nos cálculos por locais, rota guarda os ids dos locais; nos cálculos por matriz, guarda as posições da matriz.

Erros
Status	Quando	Corpo
400	Dados inválidos, matriz inválida, algoritmo desconhecido, distância faltando, limite de 18 locais	{ "erro": "mensagem" } ou { "erros": ["campo: mensagem"] }
404	Local ou rota não encontrado	{ "erro": "Local 99 não encontrado." }

Exiba a mensagem do campo erro (ou junte os itens de erros) para o usuário.

CORS

Se o front rodar em outra porta (React, Vite, Live Server), a API precisa liberar a origem. Isso está em CorsConfig.java; avise o responsável pelo back-end se a porta do front for diferente das listadas.