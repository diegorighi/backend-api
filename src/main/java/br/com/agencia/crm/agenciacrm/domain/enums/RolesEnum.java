package br.com.agencia.crm.agenciacrm.domain.enums;

public enum RolesEnum {
    
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_USER("ROLE_USER"),
    ROLE_CLIENT("ROLE_CLIENT"),
    ROLE_GUEST("ROLE_GUEST");

    private String role;

    RolesEnum(String role) {
        this.role = role;
    }

    public String fromString(String roleStr) {
        for (RolesEnum role : RolesEnum.values()) {
            if (role.name().equalsIgnoreCase(roleStr)) {
                return roleStr;
            }
        }
        throw new IllegalArgumentException("No enum constant " + roleStr);
    }

    public String getDescricao() {
        return role;
    }

}
