// Guarda os dados de um transporte realizado (origem, destino, produtos, caminhões usados, custo total)
package br.com.CompilaQueVai.models;

import java.util.List;

public class Transporte {

        private final Cidade origem;
        private final Cidade destino;
        private final List<Produto> produtos;
        private final List<Caminhao> veiculosAlocados;
        private final double distanciaKm;
        private final double custoTotal;
        private final List<Cidade> rota;

        // Construtor recebe todas as informações calculadas
    public Transporte(
            Cidade origem,
            Cidade destino,
            List<Produto> produtos,
            List<Caminhao> veiculosAlocados,
            double distanciaKm,
            double custoTotal,
            List<Cidade> rota
    ) {
        this.origem = origem;
        this.destino = destino;
        this.produtos = produtos;
        this.veiculosAlocados = veiculosAlocados;
        this.distanciaKm = distanciaKm;
        this.custoTotal = custoTotal;
        this.rota = rota;
    }

        // Getters para acessar cada dado do transporte
        public Cidade getOrigem() {
        return origem;
    }

        public Cidade getDestino() {
        return destino;
    }

        public List<Produto> getProdutos() {
        return produtos;
    }

        public List<Caminhao> getVeiculosAlocados() {
        return veiculosAlocados;
    }

        public double getDistanciaKm() {
        return distanciaKm;
    }

        public double getCustoTotal() {
        return custoTotal;
    }

        public List<Cidade> getRota() {
            return rota;
        }

        // Para exibir resumo do transporte em relatórios
        @Override
        public String toString() {
            String rotaStr = "";
            for (int i = 0; i < rota.size(); i++) {
                rotaStr += rota.get(i).getNome();
                if (i < rota.size() - 1) rotaStr += " -> ";
            }
            return "Transporte: " + rotaStr + " | distância total: " + distanciaKm + " km"
                    + " | custo total: R$" + custoTotal;
        }

}
