# RouteMath — Roadmap

Plano de desenvolvimento do projeto. Marque os itens conforme forem concluídos.

## Concluído

- [x] Modelagem matemática do problema (PCV, grafo ponderado, matriz de distâncias)
- [x] `MatrizDistancias` com validação (quadrada, diagonal zero, valores finitos e não negativos)
- [x] Heurística do **Vizinho Mais Próximo**
- [x] Algoritmo exato **Held-Karp** (programação dinâmica com bitmask)
- [x] Testes unitários dos algoritmos (comparação com força bruta)
- [x] API REST: `POST /api/rotas/calcular` (controller, service, DTOs)
- [x] Tratamento de erros da API (respostas 400 legíveis)
- [x] Entidades `LocalEntrega` e `Rota` e `LocalEntregaRepository`
- [x] README e repositório no GitHub

## Fase 1 — Comparação dos algoritmos

Objetivo: demonstrar, com números, a diferença entre a heurística e a solução ótima.

- [ ] Endpoint `POST /api/rotas/comparar` rodando Held-Karp e Vizinho Mais Próximo na mesma matriz
- [ ] Resposta com as duas rotas, a diferença em km e o percentual:
  `(D_gulosa − D_ótima) / D_ótima × 100`
- [ ] DTO de resposta da comparação
- [ ] Testes do endpoint de comparação
- [ ] Endpoint GET de exemplo (`/api/rotas/exemplo`) para demonstração no navegador (opcional)

## Fase 2 — Persistência e histórico

- [ ] Criar `RotaRepository`
- [ ] Salvar cada cálculo na tabela `rota` (algoritmo, sequência, distância, tempo, data)
- [ ] `GET /api/rotas` para listar o histórico
- [ ] `GET /api/rotas/{id}` para consultar um cálculo salvo
- [ ] Trocar o H2 pelo **MySQL** e configurar o `application.properties`
- [ ] Não versionar senhas (usar variáveis de ambiente ou arquivo no `.gitignore`)

## Fase 3 — Cadastro de locais e distâncias

- [ ] `LocalEntregaController`: `POST /api/locais` e `GET /api/locais`
- [ ] Entidade `Distancia` (origem, destino, km) com restrição de unicidade do par
- [ ] `POST /api/distancias` para cadastrar distâncias entre locais
- [ ] Montar a matriz automaticamente a partir dos locais selecionados
- [ ] Novo formato do cálculo: receber `depositoId` e `clientesIds` em vez da matriz
- [ ] Validar que existe distância cadastrada para todos os pares usados

## Fase 4 — Experimentos e análise (relatório)

- [ ] Teste de **correção**: Held-Karp contra a enumeração de todas as rotas
- [ ] Teste de **desempenho**: medir o tempo com 5, 10, 15 e 18 locais
- [ ] Teste de **qualidade**: diferença percentual entre a rota gulosa e a ótima em várias matrizes
- [ ] Repetir cada medição várias vezes, na mesma máquina, sem incluir o banco no tempo
- [ ] Gerar tabelas e gráficos para o relatório
- [ ] Documentar limitações (crescimento exponencial, distância x tempo, sem trânsito real)

## Fase 5 — Interface

- [ ] Definir a tecnologia do front-end (React, Thymeleaf ou HTML/CSS/JS puro)
- [ ] Tela de cadastro de locais e distâncias
- [ ] Tela para escolher os clientes e o algoritmo
- [ ] Exibição da sequência de visitas e da distância total
- [ ] Exibição da comparação entre algoritmos

## Fase 6 — Qualidade e documentação

- [ ] Documentar a API (Swagger / Springdoc OpenAPI)
- [ ] Testes de integração dos endpoints
- [ ] Atualizar o README com os endpoints finais e exemplos
- [ ] Revisar o código e remover classes não usadas (ex.: `Dijkstra`, se não for implementada)
- [ ] Preparar roteiro da demonstração ao vivo e da apresentação

## Extras (se sobrar tempo)

- [ ] Heurística **2-opt** para melhorar rotas e suportar mais locais
- [ ] **Dijkstra** para calcular menor caminho entre pontos
- [ ] Mapa visual com os pontos numerados
- [ ] Tempo estimado de viagem, além da distância
- [ ] Vários veículos e limite de entregas por veículo
- [ ] Distâncias reais por meio de uma API de mapas

## Premissas do escopo atual

- Um único depósito e um único veículo
- Todos os clientes visitados exatamente uma vez, com retorno ao depósito
- Objetivo: minimizar a distância total (km)
- Sem restrições de capacidade, janelas de horário ou trânsito em tempo real
