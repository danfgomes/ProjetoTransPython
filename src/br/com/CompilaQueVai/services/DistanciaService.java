// Responsável por carregar o CSV das distâncias entre cidades e responder consultas de distância
package br.com.CompilaQueVai.services;

import br.com.CompilaQueVai.Enum.TipoCaminhao;
import br.com.CompilaQueVai.utils.LeitorCsv;
import java.util.List;
import java.util.Scanner;

public class DistanciaService {
    public void distanciaEntreCidades() {
        Scanner sc = new Scanner(System.in);

        // Inicializa leitorCSV e dados
        LeitorCsv leitorCsv = new LeitorCsv();
        List<String[]> todasLinhas = leitorCsv.lerArquivoCSV();
        List<String> listaCidades = leitorCsv.getListaCidades(todasLinhas);

        // Mostra a lista de cidades e recebe origem/destino do usuario
        mostrarListaCidades(listaCidades);
        int origem = solicitarIndice(sc, "Digite o número da cidade de origem: ", listaCidades.size());
        int destino = solicitarIndice(sc, "Digite o número da cidade de destino: ", listaCidades.size());

        // Consulta distancia (metodo que esta sendo implementado na classe leitorCsv)
        int distancia = leitorCsv.consultarDistanciaPorIndice(origem, destino, todasLinhas, listaCidades);
        if (distancia == -1) {
            System.out.println("Não foi possível calcular a distância entre as cidades selecionadas.");
            return;
        }

        // Usuario define o tipo de caminhao
        mostrarTiposCaminhoes();
        TipoCaminhao caminhaoEscolhido = solicitarTipoCaminhao(sc);

        // Calcula o trajeto e mostra os dados
        double custo = calcularCusto(distancia, caminhaoEscolhido);
        mostrarResultado(listaCidades, origem, destino, distancia, caminhaoEscolhido, custo);

    }


    // --- Funções auxiliares para metodo principal ---
    private void mostrarListaCidades(List<String> listaCidades) {
        System.out.println("==== Lista de Cidades Disponíveis ====");
        for (int i = 0; i < listaCidades.size(); i++) {
            System.out.println((i + 1) + " - " + listaCidades.get(i));
        }
    }

    // Solicita que o usuario informe o indice da cidade escolhida, fazendo verificaçoes simples
    private int solicitarIndice(Scanner sc, String mensagem, int max) {
        int valor = -1;
        while(true) {
            System.out.print("\n" + mensagem);
            if(!sc.hasNextInt()) {
                System.out.println("Entrada inválida. Digite um número inteiro.");
                // sc.next() funciona aqui para descartar oq o usuario possa ter digitado, ex: 'abcdef'
                sc.next();
                continue; // Volta pro inicio do while
            }
            // Aqui deixa a certeza que de o valor é um numero
            valor = sc.nextInt();
            if (valor < 1 || valor > max) {
                System.out.println("Opção fora do intervalo. Escolha um número entre 1 e " + max + ".");
                continue; // Volta pro inicio do while
            }
            break; // Aqui o valor é valido e faz sair do while
        }
        return valor;
    }

    private void mostrarTiposCaminhoes() {
        System.out.println("\n==== Tipos de Caminhões ====");
        TipoCaminhao[] caminhoes = TipoCaminhao.values();
        for (int i = 0; i < caminhoes.length; i++) {
            TipoCaminhao tipo = caminhoes[i];
            System.out.println((i + 1) + " - " + tipo.name() + " | Preço por KM: R$" + tipo.getPrecoPorKM());
        }
    }

    // Solicita pro usuario o tipo de caminhao e fica repetindo até receber uma opção válida
    private TipoCaminhao solicitarTipoCaminhao(Scanner sc) {
        TipoCaminhao[] caminhoes = TipoCaminhao.values();
        int escolha = -1;
        while (true) {
            System.out.print("\nEscolha o tipo de caminhão: ");
            if (!sc.hasNextInt()) {
                System.out.println("Entrada inválida. Digite o número correspondente ao caminhão.");
                sc.next();
                continue;
            }
            escolha = sc.nextInt();
            if (escolha < 1 || escolha > caminhoes.length) {
                System.out.println("Opção inválida! Escolha um número entre 1 e " + caminhoes.length);
                continue;
            }
            break;
        }
        return caminhoes[escolha - 1];
    }

    private double calcularCusto(int distancia, TipoCaminhao caminhao) {
        return distancia * caminhao.getPrecoPorKM();
    }

    private void mostrarResultado(List<String> listaCidades, int origem, int destino, int distanciaTotal, TipoCaminhao caminhao, double custo) {
        System.out.println("\n==== Resultado ====");
        System.out.println("Origem: " + listaCidades.get(origem - 1));
        System.out.println("Destino: " + listaCidades.get(destino -1));
        System.out.println("Distância: " + distanciaTotal + " km");
        System.out.println("Tipo de caminhão escolhido: " + caminhao.name());
        System.out.printf("Custo estimado: R$%.2f%n", custo);
    }
}
