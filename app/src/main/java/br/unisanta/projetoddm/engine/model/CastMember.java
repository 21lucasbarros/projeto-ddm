package br.unisanta.projetoddm.engine.model;

public class CastMember {
    private String actorName;
    private RoleType role;

    public CastMember(String actorName, RoleType role) {
        this.actorName = actorName;
        this.role = role;
    }

    public String getActorName() {
        return actorName;
    }

    public RoleType getRole() {
        return role;
    }

    @Override
    public String toString() {
        return actorName + " (" + role + ")";
    }

    // dois CastMember sao "iguais" se o nome do ator for o mesmo
    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(!(obj instanceof CastMember)) {
            return false;
        }
        CastMember outro = (CastMember) obj;
        return actorName.equalsIgnoreCase(outro.actorName);
    }

    @Override
    public int hashCode() {
        return actorName.toLowerCase().hashCode();
    }
}
