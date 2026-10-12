# RouteMath

**Sistema de Otimização Matemática de Rotas de Entrega**

Projeto da disciplina de **Matemática Computacional**. A aplicação recebe um depósito e vários clientes e calcula a melhor sequência de visitas, com retorno ao depósito, **minimizando a distância total percorrida (km)**.

## Sobre o problema

Uma empresa possui um depósito central e precisa entregar produtos a vários clientes. A ordem das visitas altera diretamente a distância total. Esse é o **Problema do Caixeiro-Viajante (PCV / TSP)**.

### Modelagem matemática

- Os locais (depósito e clientes) são os **vértices** de um grafo ponderado `G = (V, E)`.
- Os deslocamentos são as **arestas**, e o peso de cada uma é a distância `d(i, j)` em km.
- As distâncias ficam armazenadas em uma **matriz de distâncias** (linhas = origem, colunas = destino).
- Função objetivo: minimizar a soma das distâncias de um ciclo que parte do depósito, visita cada cliente exatamente uma vez e retorna ao depósito.

Exemplo de matriz (ordem: D, A, B, C), valores ilustrativos em km:

```
        D   A   B   C
   D [  0   4   7   3 ]
   A [  4   0   2   6 ]
   B [  7   2   0   5 ]
   C [  3   6   5   0 ]
```

A rota `D → A → B → C → D` custa 4 + 2 + 5 + 3 = **14 km**.

> A matriz apenas armazena os custos. Quem decide a rota é o algoritmo.

## Algoritmos implementados

| Algoritmo | Tipo | Garante o ótimo? | Complexidade |
|---|---|---|---|
| **Vizinho Mais Próximo** | Heurística gulosa | Não | O(n²) |
| **Held-Karp** | Programação dinâmica com bitmask | Sim | O(n² · 2ⁿ) tempo, O(n · 2ⁿ) memória |

- O Held-Karp está limitado a **18 locais** (`CaixeiroViajante.MAX_LOCAIS`) por causa do crescimento exponencial.
- O vizinho mais próximo serve como referência para comparar a qualidade da solução:

```
Diferença (%) = (D_gulosa − D_ótima) / D_ótima × 100
```

## Tecnologias

- Java 21
- Spring Boot 4.1.1 (Spring Web MVC, Spring Data JPA, Validation, DevTools)
- MySQL
- Lombok
- Maven
- JUnit 5

## Estrutura do projeto

```
src/main/java/com/routecompany/routemath/routemath
├── algoritmo
│   ├── MatrizDistancias.java      # matriz validada (quadrada, diagonal zero, valores >= 0)
│   ├── RotaSolver.java            # interface comum aos algoritmos
│   ├── RotaCalculada.java         # record com rota, distância, algoritmo e flag "otima"
│   ├── VizinhoMaisProximo.java    # heurística gulosa
│   ├── CaixeiroViajante.java      # Held-Karp (solução exata)
│   └── Dijkstra.java              # reservado para evolução futura
├── controller   (RotaController)
├── service      (RotaService)
├── model        (LocalEntrega, Rota)
└── repository   (LocalEntregaRepository)
```

A camada matemática é independente do Spring e do banco de dados, o que permite testá-la isoladamente.

## Como executar

**Pré-requisitos:** JDK 21 e Maven (ou o wrapper `mvnw` incluído).

```bash
# clonar
git clone https://github.com/SEU-USUARIO/routemath.git
cd routemath/routemath

# rodar os testes
./mvnw test          # no Windows (PowerShell): .\mvnw test
```

### Banco de dados

A parte de persistência (MySQL) ainda está em desenvolvimento. Quando estiver pronta, configure o `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/routemath
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA
spring.jpa.hibernate.ddl-auto=update
```


## Testes

Os testes automatizados validam:

- **Correção:** o Held-Karp é comparado com a força bruta (todas as permutações) em 100 matrizes aleatórias, simétricas e assimétricas.
- **Consistência:** a rota começa e termina no depósito, e a soma dos trechos confere com a distância total.
- **Qualidade:** o resultado do Held-Karp nunca é pior que o do vizinho mais próximo.
- **Validação da matriz:** matriz não quadrada, diagonal diferente de zero, valores negativos e matriz vazia.

## Status do projeto

- [x] Modelagem matemática
- [x] Matriz de distâncias com validação
- [x] Vizinho mais próximo
- [x] Held-Karp
- [x] Testes unitários dos algoritmos
- [ ] API REST (`POST /api/rotas/calcular`)
- [ ] Persistência dos locais e resultados (MySQL)
- [ ] Interface para cadastro e visualização
- [ ] Experimentos de desempenho e qualidade

## API planejada

```http
POST /api/rotas/calcular
Content-Type: application/json

{
  "matriz": [[0,4,7,3],[4,0,2,6],[7,2,0,5],[3,6,5,0]],
  "deposito": 0,
  "algoritmo": "HELD_KARP"
}
```

Resposta esperada (ilustrativa):

```json
{
  "rota": [0, 1, 2, 3, 0],
  "distanciaTotal": 14.0,
  "algoritmo": "HELD_KARP",
  "otima": true
}
```

## Premissas e limitações

- Um único depósito e um único veículo.
- Todos os clientes são visitados exatamente uma vez.
- O objetivo é distância, não tempo; não há trânsito em tempo real.
- Sem restrições de capacidade ou janelas de horário.
- As distâncias são informadas manualmente ou vêm de conjuntos de teste.
- O campo `otima` só é verdadeiro quando o algoritmo garante a solução ótima.

## Equipe

- Arthur Lopes de Freitas
- Isaac Torres
- José Henrique do Nascimento Lopes
- Lucas Ryan Cardoso Ribeiro

## Licença

Projeto acadêmico, sem licença definida.
