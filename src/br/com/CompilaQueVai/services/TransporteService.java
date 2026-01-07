package br.com.CompilaQueVai.services;

import br.com.CompilaQueVai.Enum.TipoCaminhao;
import br.com.CompilaQueVai.Enum.TipoProduto;
import br.com.CompilaQueVai.models.Caminhao;
import br.com.CompilaQueVai.models.Cidade;
import br.com.CompilaQueVai.models.Produto;
import br.com.CompilaQueVai.models.Transporte;
import br.com.CompilaQueVai.utils.LeitorCsv;

import java.util.*;

public class TransporteService {

    private final Scanner sc = new Scanner(System.in);
    private final LeitorCsv leitorCsv = new LeitorCsv();
    private List<Transporte> transportes = new ArrayList<>();

    // Cadastra um novo transporte certificando de que tenha carga/distância válida
    public void cadastrarTransporte() {
        // Le dados das cidades e distancias
        List<String[]> todasLinhas = leitorCsv.lerArquivoCSV();
        List<String> listaCidades = leitorCsv.getListaCidades(todasLinhas);
        // Monta a rota escolhida pelo usuario
        List<Integer> rotaCidades = montarRota(listaCidades);
        // Verifica se ha distancia registrada entre as cidades da rota
        for (int i = 0; i < rotaCidades.size() - 1; i++) {
            int idxOrigem = rotaCidades.get(i);
            int idxDestino = rotaCidades.get(i + 1);
            int distancia = leitorCsv.consultarDistanciaPorIndice(idxOrigem, idxDestino, todasLinhas, listaCidades);
            if (distancia < 0) {
                System.out.printf("Não há distância registrada entre %s e %s. Cadastro de transporte cancelado.\n",
                        listaCidades.get(idxOrigem - 1), listaCidades.get(idxDestino - 1));
                return; // sai do metodo sem continuar
            }
        }
        // Exibe rota completa
        exibirRota(rotaCidades, listaCidades);
        // Cadastra carga inicial
        List<TipoProduto> produtos = new ArrayList<>();
        List<Integer> quantidades = new ArrayList<>();
        boolean temCarga = cadastrarCargaInicial(produtos, quantidades);
        if (!temCarga) {
            System.out.println("\nNenhuma carga informada. Transporte anulado e retornando ao menu.");
            return;
        }
        // Cria a lista para armazenar informações de cada trecho
        List<Map<String, Object>> resumoTrechos = new ArrayList<>();
        // calcula transporte - custo, caminhoes, peso e distancia
        double custoTotal = calcularTransporte(rotaCidades, listaCidades, todasLinhas, produtos, quantidades, resumoTrechos);
        // Exibir resumo final do transporte
        exibirResumoFinal(rotaCidades, listaCidades, custoTotal, produtos, quantidades, resumoTrechos);
        // Preparar dados para estatisticas
        Cidade origem = new Cidade(listaCidades.get(rotaCidades.get(0) - 1), rotaCidades.get(0));
        Cidade destino = new Cidade(listaCidades.get(rotaCidades.get(rotaCidades.size() - 1) - 1), rotaCidades.get(rotaCidades.size() - 1));
        // Converter produtos em objetos Produto
        List<Produto> listaProdutos = new ArrayList<>();
        for (int i = 0; i < produtos.size(); i++) {
            TipoProduto tipoProduto = produtos.get(i);
            int quantidadeProduto = quantidades.get(i);
            listaProdutos.add(new Produto(tipoProduto, quantidadeProduto));
        }
        // Converter caminhoes usados em objetos Caminhao
        List<Caminhao> veiculosAlocados = new ArrayList<>();
        for (Map<String, Object> trecho : resumoTrechos) {
            Map<TipoCaminhao, Integer> caminhoesUsados = (Map<TipoCaminhao, Integer>) trecho.get("caminhoes");
            for (TipoCaminhao tipo : caminhoesUsados.keySet()) {
                int quantidade = caminhoesUsados.get(tipo);
                for (int j = 0; j < quantidade; j++) {
                    veiculosAlocados.add(new Caminhao(tipo, tipo.getPrecoPorKM()));
                }
            }
        }
        // Calcular distancia total percorrida
        int distanciaTotal = 0;
        for (Map<String, Object> trecho : resumoTrechos) distanciaTotal += (Integer) trecho.get("distancia");

        List<Cidade> rotaComoCidades = new ArrayList<>();
        for (int idx : rotaCidades) rotaComoCidades.add(new Cidade(listaCidades.get(idx - 1), idx));
        // Cria objeto Transporte e o salva
        Transporte novoTransporte = new Transporte(
                origem, destino, listaProdutos, veiculosAlocados, distanciaTotal, custoTotal, rotaComoCidades
        );
        this.transportes.add(novoTransporte);
        System.out.println("Transporte salvo para estatísticas!");
    }

    private List<Integer> montarRota(List<String> cidades) {
        List<Integer> rota = new ArrayList<>();
        // Mostra todas as cidades disponiveis
        System.out.println("==== Lista de Cidades disponíveis ====");
        for (int i = 0; i < cidades.size(); i++) System.out.println((i + 1) + " - " + cidades.get(i));
        // Le a cidade de origem
        int cidadeOrigem = lerIntNoIntervalo("\nDigite o número da cidade de origem: ", 1, cidades.size());
        rota.add(cidadeOrigem);
        // Le a cidade de destino
        int cidadeDestino = lerIntNoIntervalo("Digite o número da cidade de destino: ", 1, cidades.size());
        // Pergunta se deseja adicionar cidades de parada
        String desejaParadas = lerSimNao("Deseja adicionar cidades de parada? (s/n): ");
        if (desejaParadas.equalsIgnoreCase("s")) {
            while (true) {
                int cidadeParada = lerIntNoIntervalo("Digite o número da cidade de parada: ", 1, cidades.size());
                rota.add(cidadeParada);
                System.out.println(cidades.get(cidadeParada - 1) + " adicionada à rota.");
                //
                String continuar = lerSimNao("Deseja adicionar outra parada? (s/n): ");
                if (!continuar.equalsIgnoreCase("s")) break; // encerra a adição de paradas
            }
        }
        // Adiciona a cidade de destino no final da rota
        rota.add(cidadeDestino);
        return rota;
    }

    private void exibirRota(List<Integer> rota, List<String> cidades) {
        System.out.print("\nRota definida: ");
        for (int i = 0; i < rota.size(); i++) {
            System.out.print(cidades.get(rota.get(i) - 1));
            if (i < rota.size() - 1) System.out.print(" → ");
        }
        System.out.println();
    }

    private boolean cadastrarCargaInicial(List<TipoProduto> produtos, List<Integer> quantidades) {
        System.out.println("\n==== Cadastro da carga inicial ====");
        TipoProduto[] listaProdutos = TipoProduto.values();
        boolean temCarga = false; // utilizado pra informar se algum produto foi adicionado a lista de produtos

        while (true) {
            // Mostra a lista de produtos disponiveis
            System.out.println("\nEscolha um produto para adicionar à carga: ");
            for (int i = 0; i < listaProdutos.length; i++) {
                TipoProduto produto = listaProdutos[i];
                System.out.printf("%d - %s | Peso: %.2f kg%n", i + 1, produto.name(), produto.getPesoKg());
            }
            // Le a escolha do usuario
            int escolha = lerIntNoIntervalo("Opção: ", 1, listaProdutos.length);
            TipoProduto produtoSelecionado = listaProdutos[escolha - 1];
            // Le a quantidade desejada
            int quantidade = lerIntNaoNegativo("Quantidade de " + produtoSelecionado.name() + ": ");
            if (quantidade > 0) {
                // Adiciona ou soma a quantidade na lista
                adicionarOuSomarProduto(produtos, quantidades, produtoSelecionado, quantidade);
                temCarga = true;
                System.out.println("Adicionado: " + quantidade + "x " + produtoSelecionado.name());
            } else {
                System.out.println("Quantidade zero - nada adicionado.");
            }
            // Pergunta se deseja continuar adicionando produtos
            String continuar = lerSimNao("Deseja adicionar outro produto? (s/n): ");
            if (!continuar.equalsIgnoreCase("s")) break;
        }
        if (!temCarga) System.out.println("\nNenhum item foi adicionado! Transporte será anulado!");
        return temCarga;
    }

    private void adicionarOuSomarProduto(List<TipoProduto> produtos, List<Integer> quantidades, TipoProduto produto, int qtd) {
        for (int i = 0; i < produtos.size(); i++) {
            if (produtos.get(i) == produto) {
                quantidades.set(i, quantidades.get(i) + qtd);
                return;
            }
        }
        produtos.add(produto);
        quantidades.add(qtd);
    }

    private double calcularTransporte (
            List<Integer> rota,
            List<String> cidades,
            List<String[]> linhas,
            List<TipoProduto> produtos,
            List<Integer> quantidades,
            List<Map<String, Object>> resumoTrechos
    ) {
        double custoTotal = 0.0;
        // Percorre cada trecho da rota (origem - destino)
        for (int i = 0; i < rota.size() - 1; i++) {
            // Nomes das cidades de origem e destino
            String cidadeOrigem = cidades.get(rota.get(i) - 1);
            String cidadeDestino = cidades.get(rota.get(i + 1) - 1);
            // Consulta a distancia entre as cidades
            int distanciaTrecho = leitorCsv.consultarDistanciaPorIndice(rota.get(i), rota.get(i + 1), linhas, cidades);
            // Se não encontrar distancia, tenta o contrário (destino - origem)
            if (distanciaTrecho <= 0) {
                int simetrico = leitorCsv.consultarDistanciaPorIndice(rota.get(i + 1), rota.get(i), linhas, cidades);
                if (simetrico > 0) distanciaTrecho = simetrico;
            }
            // Se ainda não encontrar a distancia, ignora esse trecho do código
            if (distanciaTrecho <= 0) {
                System.out.printf("\nTrecho %s → %s sem distância cadastrada. Ignorando este trecho.\n",
                        cidadeOrigem, cidadeDestino);

                Map<String, Object> trecho = new HashMap<>();
                trecho.put("origem", cidadeOrigem);
                trecho.put("destino", cidadeDestino);
                trecho.put("distancia", -1);
                trecho.put("peso", calcularPeso(produtos, quantidades));
                trecho.put("custo", 0.0);
                trecho.put("caminhoes", Collections.emptyMap());
                trecho.put("status", "sem-distancia");
                resumoTrechos.add(trecho);
                continue; // passa para o próximo trecho
            }
            // Calcula o peso atual da carga
            double pesoAtual = calcularPeso(produtos, quantidades);
            // Se n houver mais carga, encerra o transporte
            if (pesoAtual <= 0) {
                System.out.println("\nNão há mais carga para transportar. Encerrando transporte.");
                break;
            }
            // Escolhe os caminhoes necessarios para o peso atual
            List<TipoCaminhao> caminhoesSelecionados = escolherCaminhoes(pesoAtual);
            // Conta quantos caminhoes de cada tipo foram usados
            Map<TipoCaminhao, Integer> tiposCaminhoesUsados = new HashMap<>();
            for (TipoCaminhao caminhao : caminhoesSelecionados) {
                tiposCaminhoesUsados.put(caminhao, tiposCaminhoesUsados.getOrDefault(caminhao, 0) + 1);
            }
            // Calcula o custo do trecho somando preço por km de cada caminhao
            double custoTrecho = 0.0;
            for (TipoCaminhao caminhao : caminhoesSelecionados) custoTrecho += caminhao.getPrecoPorKM() * distanciaTrecho;
            // Mostra o resumo do trecho no console
            System.out.printf("\nTrecho %s → %s | Distância: %d km | Peso: %.2f kg | Caminhões: %d | Custo: R$ %.2f%n",
                    cidadeOrigem, cidadeDestino, distanciaTrecho, pesoAtual, caminhoesSelecionados.size(), custoTrecho);
            // Atualiza o custo total
            custoTotal += custoTrecho;
            // Pergunta se deseja descarregar na cidade de destino (se n for o ultimo trecho)
            if (i < rota.size() - 2) {
                String resp = lerSimNao("Deseja descarregar algo em " + cidadeDestino + "? (s/n): ");
                if (resp.equals("s")) {
                    descarregarNaCidade(produtos, quantidades);
                    double pesoRestante = calcularPeso(produtos, quantidades);
                    System.out.printf("Peso restante após descarregar em %s: %.2f kg%n", cidadeDestino, pesoRestante);
                }
            }
            // Salva informacoes do trecho no resumo
            Map<String, Object> trecho = new HashMap<>();
            trecho.put("origem", cidadeOrigem);
            trecho.put("destino", cidadeDestino);
            trecho.put("distancia", distanciaTrecho);
            trecho.put("peso", pesoAtual);
            trecho.put("custo", custoTrecho);
            trecho.put("caminhoes", tiposCaminhoesUsados);
            trecho.put("status", "ok");
            resumoTrechos.add(trecho);
        }
        return custoTotal;
    }

    private void descarregarNaCidade(List<TipoProduto> produtos, List<Integer> quantidades) {
        // Verifica se a carga está vazia
        if (produtos.isEmpty()) {
            System.out.println("Não há produtos na carga.");
            return;
        }

        while (true) {
            System.out.println("\nItens na carga (atual):");
            // Exibe a lista de produtos e suas quantidades
            for (int i = 0; i < produtos.size(); i++) {
                TipoProduto produto = produtos.get(i);
                int quantidadeAtual = quantidades.get(i);
                System.out.println((i + 1) + " - " + produto.name() + " | Quantidade: " + quantidadeAtual);
            }
            // Pergunta qual item o usuário deseja descarregar
            int opcao = lerIntNoIntervalo("Selecione um item para decarregar (ou 0 para finalizar): ", 0, produtos.size());
            if (opcao == 0) break;

            int indiceSelecionado = opcao -1;
            int quantidadeDisponivel = quantidades.get(indiceSelecionado);
            // Verifica se o item já está zerado
            if (quantidadeDisponivel == 0) {
                System.out.println("Este item já está zerado.");
                continue;
            }
            // Pergunta quanto o usuário quer descarregar
            int quantidadeDescarregar = lerIntNoIntervalo("Quantidade a descarregar (máx " + quantidadeDisponivel + "): ", 0, quantidadeDisponivel);
            // Atualiza a quantidade do item apos o descarregamento
            quantidades.set(indiceSelecionado, quantidadeDisponivel - quantidadeDescarregar);
            System.out.println("Descarregado: " + quantidadeDescarregar + "x " + produtos.get(indiceSelecionado).name());
        }
    }

    private double calcularPeso(List<TipoProduto> produtos, List<Integer> quantidades) {
        double pesoTotal = 0.0;
        // Para cada produto, multiplica o peso unitario pela quantidade
        for (int i = 0; i < produtos.size(); i++) pesoTotal += produtos.get(i).getPesoKg() * quantidades.get(i);
        return pesoTotal;
    }

    private List<TipoCaminhao> escolherCaminhoes(double peso) {
        List<TipoCaminhao> caminhoesSelecionados = new ArrayList<>();
        // Pega todos os tipos de caminhoes disponiveis
        TipoCaminhao[] tiposCaminhoes = TipoCaminhao.values();
        // Ordena os tipos de caminhao pelo custo por kg de capacidade (mais barato primeiro sempre)
        Arrays.sort(tiposCaminhoes, Comparator.comparingDouble(tipo -> tipo.getPrecoPorKM() / tipo.getCapacidadeMaxima()));

        double pesoRestante = peso;
        // Adiciona caminhoes até que todo peso seja transportado
        for (TipoCaminhao tipo : tiposCaminhoes) {
            while (pesoRestante > 0) {
                caminhoesSelecionados.add(tipo);
                pesoRestante -= tipo.getCapacidadeMaxima();
            }
            if (pesoRestante <= 0) break; // Já carregou todo o peso
        }
        return caminhoesSelecionados;
    }

    private void exibirResumoFinal(
            List<Integer> rota,
            List<String> cidades,
            double custoTotal,
            List<TipoProduto> produtos,
            List<Integer> quantidades,
            List<Map<String, Object>> resumoTrechos
    ) {
        System.out.println("\n==== Resumo Final do Transporte");
        // Mostra a rota completa
        System.out.print("Rota: ");
        for (int i = 0; i <rota.size(); i++) {
            String cidade = cidades.get(rota.get(i) - 1);
            System.out.print(cidade);
            if (i < rota.size() - 1) System.out.print(" -> ");
        }
        System.out.println("\n\nTrechos percorridos:");
        int distanciaTotal = 0;
        // Percorre cada trecho para exibir detalhes
        for (int i = 0; i < resumoTrechos.size(); i++) {
            Map<String, Object> trecho = resumoTrechos.get(i);
            String origem = (String) trecho.get("origem");
            String destino = (String) trecho.get("destino");
            String status = (String) trecho.getOrDefault("status", "ok");
            Integer distancia = (Integer) trecho.get("distancia");
            double peso = (double) trecho.get("peso");
            double custo = (double) trecho.get("custo");

            System.out.printf("%d. %s -> %s\n", i + 1, origem, destino);
            // Trechos sem distancia valida
            if ("sem-distancia".equals(status) || distancia == null || distancia <= 0) {
                System.out.println("   - Distância: não cadastrada");
                System.out.printf("   - Peso transportado (estimado): %.2f kg\n", peso);
                System.out.println("   - Caminhões utilizados: -");
                System.out.println("   - Custo: R$ 0,00\n");
                continue;
            }
            // Trecho valido
            System.out.printf("   - Distância: %d km\n", distancia);
            System.out.printf("   - Peso transportado: %.2f kg\n", peso);
            System.out.println("   - Caminhões utilizados:");
            Map<TipoCaminhao, Integer> caminhoesUsados = (Map<TipoCaminhao, Integer>) trecho.get("caminhoes");
            for (Map.Entry<TipoCaminhao, Integer> entry : caminhoesUsados.entrySet()) {
                TipoCaminhao tipo = entry.getKey();
                int quantidade = entry.getValue();
                System.out.printf("     • %dx Caminhão %s\n", quantidade, tipo.name());
            }
            System.out.printf("   - Custo: R$ %.2f\n\n", custo);
            // Atualiza a distancia total
            distanciaTotal += distancia;
        }
        // Exibe resumo final total
        System.out.printf("Distância total percorrida: %d km\n", distanciaTotal);
        System.out.printf("Custo total do transporte: R$%.2f\n", custoTotal);
    }

    private int lerIntNoIntervalo(String mensagem, int min, int max) {

        while (true) {
            System.out.print(mensagem);
            try {
                int valor = Integer.parseInt(sc.nextLine().trim());
                if (valor < min || valor > max) {
                    System.out.println("Valor inválido. Informe um número entre " + min + " e " + max + ".");
                    continue;
                }
                return valor;
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número inteiro.");
            }
        }
    }

    private int lerIntNaoNegativo(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String entrada = sc.nextLine().trim();
            try {
                int valor = Integer.parseInt(entrada);
                if (valor < 0) {
                    System.out.println("Valor inválido. Não pode ser negativo");
                    continue;
                }
                return valor;
            } catch (NumberFormatException exception) {
                System.out.println("Entrada inválida. Digite um número inteiro.");
            }
        }
    }

    private String lerSimNao(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String resposta = sc.nextLine().trim().toLowerCase();
            if (resposta.equals("s") || resposta.equals("n")) return resposta;
            System.out.println("Resposta inválida. Digite 's' para sim ou 'n' para não.");
        }
    }

    public List<Transporte> getTransportesCadastrados() {
        return this.transportes;
    }

}