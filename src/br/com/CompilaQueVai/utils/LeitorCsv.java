package br.com.CompilaQueVai.utils;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class LeitorCsv {

    public List<String[]> lerArquivoCSV(){
        String caminhoArquivo = "DistanciasCidadesCSV.csv";
        List<String[]> todasLinhas = List.of();
        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(caminhoArquivo), StandardCharsets.UTF_8)) {
            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(';')
                    .build();
            try (com.opencsv.CSVReader csvReader = new CSVReaderBuilder(isr)
                    .withCSVParser(parser)
                    .build()) {
                todasLinhas = csvReader.readAll();
            }
        } catch (Exception e){
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }
        return todasLinhas;
    }

    public List<String> getListaCidades(List<String[]> todasLinhas) {
        // Lista pra guardar todas as cidades
        List<String> cidades = new ArrayList<>();
        // Aqui adiciona as cidades da primeira coluna (vertical)
        for (int i = 2; i < todasLinhas.size(); i++){
            String cidade = todasLinhas.get(i)[0];
            if (cidade != null && !cidade.isBlank() && !cidades.contains(cidade)) {
                cidades.add(cidade.trim());
            }
        }
        // Adiciona cidades do cabeçalho
        String[] cabecalho = todasLinhas.get(1);
        for (int j = 1; j < cabecalho.length; j++){ // começa em 1 pra pular a celula vazia
            String cidade = cabecalho[j];
            if (cidade != null && !cidade.isBlank() && !cidades.contains(cidade)){
                cidades.add(cidade.trim());
            }
        }
        return cidades;
    }

    public int consultarDistanciaPorIndice(int indiceOrigem, int indiceDestino, List<String[]> todasLinhas, List<String> listaCidades) {
        // Verifica se as cidades são iguais
        if (indiceOrigem == indiceDestino){
            System.out.println("As cidades de origem e destino devem ser diferentes, as selecionadas estão iguais");
            return -1;
        }
        // Ajustando o indice que o usuario informa
        String cidadeOrigem = listaCidades.get(indiceOrigem - 1);
        String cidadeDestino = listaCidades.get(indiceDestino - 1);
        // faz a busca da distância na direção reta
        Integer distancia = buscarDistancia(cidadeOrigem, cidadeDestino, todasLinhas);
        // Se não funcionar, faz a busca na direção invertida das cidades
        if (distancia == null) {
            distancia = buscarDistancia(cidadeDestino, cidadeOrigem, todasLinhas);
        }
        // Se não encontrar de qualquer forma, vai informar ao usuário e retornar -1
        if (distancia == null) {
            return -1;
        }
        // Caso de certo mostra e retorna
        System.out.println("Distância entre " + cidadeOrigem + " e " + cidadeDestino + ": " + distancia + " km");
        return distancia;
    }

    private Integer buscarDistancia(String origem, String destino, List<String[]> todasLinhas) {
        // Procura a linha de origem
        int linhaOrigem = -1;
        for (int i = 2; i < todasLinhas.size(); i++){
            if (origem.equalsIgnoreCase(todasLinhas.get(i)[0].trim())) {
                linhaOrigem = i;
                break;
            }
        }
        if (linhaOrigem == -1) {
            return null;
        }
        // Procura coluna de destino
        int colunaDestino = -1;
        String[] cabecalho = todasLinhas.get(1);
        for (int j = 1; j < cabecalho.length; j++) {
            if (destino.equalsIgnoreCase(cabecalho[j].trim())) {
                colunaDestino = j;
                break;
            }
        }
        if (colunaDestino == -1) {
            return null;
        }

        // pega e converte a distância
        String valor = todasLinhas.get(linhaOrigem)[colunaDestino];
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

}


