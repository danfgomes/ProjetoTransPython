// Representa uma cidade
package br.com.CompilaQueVai.models;

public class Cidade {
    //nome da cidade
    private final String nome;
    //posicao da cidade na matriz
    private final int indice;

    // Construtor recebe nome e índice
    public Cidade(String nome, int indice) {
        this.nome = nome;
        this.indice = indice;
    }

    // Retorna o nome da cidade
    public String getNome() {
        return nome;
    }

    // Retorna o índice da cidade na matriz
    public int getIndice() {
        return indice;
    }

    // Para exibir apenas o nome em logs e listas
    @Override
    public String toString() {
        return nome;
    }

}
