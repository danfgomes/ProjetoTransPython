package br.com.CompilaQueVai.Enum;

public enum TipoProduto {
    CELULAR(0.7),
    GELADEIRA(50.0),
    AIR_FRYER(3.5),
    CADEIRA(5.0),
    LUMINARIA(0.8),
    LAVADORA_DE_ROUPA(15),
    PLAYSTATION5(3.9),
    NINTENDO_SWITCH(0.3);

    private final double pesoKg;

    TipoProduto(double pesoKg) {
        this.pesoKg = pesoKg;
    }

    public double getPesoKg() {
        return pesoKg;
    }
}