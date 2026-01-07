// Representa cada tipo de caminhão (nome, capacidade de carga, custo por km)
package br.com.CompilaQueVai.models;

import br.com.CompilaQueVai.Enum.TipoCaminhao;

public class Caminhao {

    // Tipo definido no enum (PEQUENO, MÉDIO, GRANDE)
    private final TipoCaminhao tipo;

    // Capacidade de carga em quilogramas
    private final double capacidadeEmKg;

    // Construtor recebe tipo e capacidade
    public Caminhao(TipoCaminhao tipo, double capacidadeEmKg) {
        this.tipo = tipo;
        this.capacidadeEmKg = capacidadeEmKg;
    }

    // Retorna o tipo de caminhão
    public TipoCaminhao getTipo() {
        return tipo;
    }

    // Retorna o custo por quilômetro conforme enum
    public double getCustoPorKm() {
        return tipo.getPrecoPorKM();
    }

    // Retorna a capacidade de carga do caminhão
    public double getCapacidadeEmKg() {
        return capacidadeEmKg;
    }

    //Retorna a capacidade máxima
    public int getCapacidadeMaxima(){return tipo.getCapacidadeMaxima();}

    // Para exibir tipo, capacidade e custo em relatórios
    @Override
    public String toString() {
        return String.format("%s (capacidade: %.2f kg, custo: R$ %.2f/km)",
                tipo.name(),
                capacidadeEmKg,
                tipo.getPrecoPorKM());
    }

}
