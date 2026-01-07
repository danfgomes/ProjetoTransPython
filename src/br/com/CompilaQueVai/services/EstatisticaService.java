package br.com.CompilaQueVai.services;

import br.com.CompilaQueVai.models.Cidade;
import br.com.CompilaQueVai.models.Transporte;
import br.com.CompilaQueVai.models.Produto;
import br.com.CompilaQueVai.models.Caminhao;

import java.util.*;

public class EstatisticaService {
    private final Scanner sc = new Scanner(System.in);

    public void gerarRelatorios(List<Transporte> transportes) {
        if (transportes.isEmpty()) {
            System.out.println("Nenhum transporte cadastrado ainda.");
            return;
        }

        if (transportes.size() == 1) {
            gerarRelatorio(transportes.get(0)); // só 1 transporte
        } else {
            escolherTransporte(transportes);    // escolhe qual ver
        }
    }

    // Menu para escolher transporte
    private void escolherTransporte(List<Transporte> transportes) {
        System.out.println("\n==== Transportes Cadastrados ====");
        for (int i = 0; i < transportes.size(); i++) {
            Transporte transporte = transportes.get(i);

            // Monta a string da rota a partir da lista de cidade armazenada
            List<Cidade> rota = transporte.getRota();
            System.out.printf("%d - ", i + 1);
            for (int j = 0; j < rota.size(); j++) {
                System.out.print(rota.get(j).getNome());
                if (j < rota.size() - 1) System.out.print(" -> ");
            }

            System.out.printf(" | Distância total: %.1f km | Custo: R$%.2f%n",
                    transporte.getDistanciaKm(), transporte.getCustoTotal());
        }
        int escolha = -1;
        while (escolha < 1 || escolha > transportes.size()) {
            System.out.print("Escolha o transporte (1-" + transportes.size() + "): ");
            try {
                escolha = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException exception) {
                escolha = -1; // força uma repetição caso tenha entrada invalida
            }
        }
        gerarRelatorio(transportes.get(escolha - 1));
    }

    // Relatório detalhado de UM transporte
    // Relatório detalhado de UM transporte
    // Relatório detalhado de UM transporte
    private void gerarRelatorio(Transporte transporte) {
        double custoTotal = transporte.getCustoTotal();
        double distancia = transporte.getDistanciaKm();
        int totalItens = 0;

        Map<String, Integer> qtdPorProduto = new HashMap<>();
        Map<String, Double> custoPorModalidade = new HashMap<>();

        // calcula total de itens e quantidade por produto
        for (Produto produto : transporte.getProdutos()) {
            totalItens += produto.getQuantidade();
            qtdPorProduto.merge(produto.getNome(), produto.getQuantidade(), Integer::sum);
        }

        // custo por modalidade de caminhão
        for (Caminhao c : transporte.getVeiculosAlocados()) {
            String tipo = c.getTipo().name();
            custoPorModalidade.merge(tipo, c.getCustoPorKm() * distancia, Double::sum);
        }

        double custoMedioKm = distancia > 0 ? custoTotal / distancia : 0.0;

        // impressão
        System.out.println("\n===== RELATÓRIO DETALHADO DO TRANSPORTE =====");
        System.out.println("Origem: " + transporte.getOrigem().getNome());
        System.out.println("Destino: " + transporte.getDestino().getNome());

        // Mostra paradas, se existirem
        List<Cidade> rota = transporte.getRota();
        if (rota.size() > 2) {
            System.out.print("Paradas: ");
            for (int i = 1; i < rota.size() - 1; i++) {
                System.out.print(rota.get(i).getNome());
                if (i < rota.size() - 2) System.out.print(" -> ");
            }
            System.out.println();
        }

        System.out.printf("Distância Total: %.1f km%n", distancia);
        System.out.printf("Custo total: R$ %.2f%n", custoTotal);
        System.out.printf("Custo médio por km: R$ %.2f%n", custoMedioKm);

        // Total de veiculos deslocados com tipo
        System.out.println("Total de veículos deslocados: " + transporte.getVeiculosAlocados().size());
        Map<String, Integer> contagem = new HashMap<>();
        for (Caminhao c : transporte.getVeiculosAlocados()) {
            String tipo = c.getTipo().name();
            contagem.put(tipo, contagem.getOrDefault(tipo, 0) + 1);
        }
        for (String tipo : contagem.keySet()) {
            System.out.println("  " + tipo + ": " + contagem.get(tipo) + " veículo(s)");
        }

        System.out.println("Total de itens transportados: " + totalItens);

        System.out.println("\n--- Lista de Itens Transportados ---");
        qtdPorProduto.forEach((produto, qtd) -> System.out.printf("%s: %d unidades%n", produto, qtd));

        System.out.println("\n--- Custo médio por Produto ---");
        for (Produto produto : transporte.getProdutos()) {
            int quantidade = produto.getQuantidade();
            double custoMedio = quantidade > 0 ? custoTotal * ((double) quantidade / totalItens) / quantidade : 0;
            System.out.printf("%s: R$ %.2f por unidade%n", produto.getNome(), custoMedio);
        }

        System.out.println("\n--- Custo por Modalidade ---");
        String[] modalidade = {"PEQUENO", "MÉDIO", "GRANDE"};
        for (String tipo : modalidade) {
            double custo = custoPorModalidade.getOrDefault(tipo, 0.0);
            System.out.printf("Caminhão %s: R$%.2f%n", tipo.toLowerCase(), custo);
        }

        System.out.println("=====================================");
    }

}
