package br.unisanta.projetoddm.engine.model;

public enum RoleType {
    PROTAGONIST(1.0),
    SUPPORTING(0.6),
    CAMEO(0.25);

    private final double relevance;

    RoleType(double relavance) {
        this.relevance = relavance;
    }

    public double getRelevance() {
        return relevance;
    }
}
