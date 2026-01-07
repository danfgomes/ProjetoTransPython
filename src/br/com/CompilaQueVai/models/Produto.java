// Representa um produto transportado (nome, peso unitário, quantidade)
package br.com.CompilaQueVai.models;

import br.com.CompilaQueVai.Enum.TipoProduto;

public class Produto {
    //enum que indica o tipo de produto celular, geladeira..
    private final TipoProduto tipo;
    //quantidade de unidades desse tipo produto
    private final int quantidade;

    //Construtor: recebe o tipo de produtyo e a quantidae a transportar
    public Produto(TipoProduto tipo, int quantidade) {
        this.tipo = tipo;
        this.quantidade = quantidade;

    }

    // Retorna o tipo de produto (enum)
    public TipoProduto getTipo() {
        return tipo;
    }

    // Retorna o nome do produto conforme definido no enum
    public String getNome() {
        return tipo.name();
    }

    // Retorna o peso de uma unidade do produto em quilogramas
    public double getPesoUnitario() {
        return tipo.getPesoKg();
    }

    // Calcula o peso total de todas as unidades deste produto
    public double getPesoTotal() {
        return getPesoUnitario() * quantidade;
    }

    // Retorna a quantidade de unidades deste produto
    public int getQuantidade() {
        return quantidade;
    }

    // Representação em texto para debug e relatórios
    @Override
    public String toString() {
        return quantidade
                + " x "
                + getNome()
                + " (peso total: "
                + String.format("%.2f", getPesoTotal())
                + " kg)";
    }

}
