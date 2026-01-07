package br.com.CompilaQueVai.Enum;

public enum TipoCaminhao {
    PEQUENO( 5.83,1000),
    MEDIO(13.42, 2000),
    GRANDE( 29.21, 3000);

    private final double precoPorKM;
    private final int capacidadeMaxima;

    TipoCaminhao(double precoPorKM, int capacidadeMaxima) {
        this.precoPorKM = precoPorKM;
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public double getPrecoPorKM() {
        return precoPorKM;
    }

    public int getCapacidadeMaxima() { return capacidadeMaxima;}
}



