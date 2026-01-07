<h1 align="center">🚚 CompilaQueVai</h1>

<p align="center">
  <img src="LogoCompilaQueVai.png" alt="Logo Compila Que Vai" width="220"/>
</p>

<p align="center">
  Sistema de transporte em linha de comando para planejamento de rotas, controle de carga e análises de custo.
</p>

---

## 📌 Funcionalidades

* 📍 Consulta de distâncias entre cidades e estimativa de custo por tipo de caminhão.
* 🛣 Cadastro de transporte com definição de rota (origem, destino e paradas).
* 📦 Registro de produtos e quantidades via enumeração de tipos.
* 🚛 Alocação automática de caminhões com base em custo e capacidade.
* ⛽ Descarregamento parcial em paradas intermediárias.
* 📊 Relatórios detalhados com histórico de operações.

---

## ⚙️ Pré-requisitos

* Java 21
* OpenCSV no classpath (via arquivo `.jar` ou dependência Maven/Gradle)
* Arquivo `DistanciasCidadesCSV.csv` na raiz do projeto

---

## 🛠️ Instalação e Execução

1. Clone o repositório:

   ```bash
   git clone https://github.com/SEU_USUARIO/CompilaQueVai.git
   ```
2. Adicione o JAR do OpenCSV em `libs/` ou configure a dependência no seu build tool.
3. Compile o código:

   ```bash
   javac -cp "libs/opencsv.jar" -d bin src/br/com/CompilaQueVai/**/*.java
   ```
4. Execute o menu principal:

   ```bash
   java -cp "bin;libs/opencsv.jar" br.com.CompilaQueVai.Menu
   ```

---

## ▶️ Exemplo de Uso

Ao iniciar, o terminal exibe:

```
== Sistema de Transporte ==
1 - Consultar trechos e modalidades
2 - Cadastrar transporte
3 - Dados estatísticos
4 - Finalizar programa
```

* **Opção 1** → Consulta origem, destino e tipo de caminhão, retornando o custo estimado.
* **Opção 2** → Auxilia na montagem da rota, cadastro da carga, alocação de veículos e gera resumo final antes de salvar.
* **Opção 3** → Lista transportes salvos e gera relatórios detalhados (custo total, custo médio por km, por produto e por modalidade).

---

## 🗂 Formato do CSV de Distâncias

O arquivo deve usar ponto e vírgula `;` como separador:

```
;CidadeA;CidadeB;CidadeC
CidadeA;0;120;340
CidadeB;120;0;210
CidadeC;340;210;0
```

* A primeira linha traz os nomes das cidades como cabeçalho.
* Cada linha seguinte inicia com o nome de uma cidade, seguida das distâncias simétricas para as demais.

---

## 🛠️ Detalhes Técnicos

* **TipoCaminhao** → enum com valores `PEQUENO`, `MEDIO`, `GRANDE`, cada um com preço por km e capacidade máxima.
* **TipoProduto** → enum de produtos com peso em kg para cálculo automático da carga.
* **LeitorCsv** → carrega o CSV, monta a lista de cidades e pesquisa distâncias simétricas.
* **TransporteService** → fluxo completo de cadastro de rotas, carga/descarga, alocação de caminhões e resumo de custo.
* **EstatisticaService** → geração de relatórios a partir do histórico de transportes salvos.

---

## 📚 Tecnologias

* Java 21
* OpenCSV
* Maven ou Gradle (opcional para dependências)
* CSV para dados de cidades e distâncias
