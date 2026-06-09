package io.github.valerioisufi.model.domain;

public class Credentials {

    private String username;
    private String password;

    private Role role;

    private int idInsegnante;

    public Credentials(String username, String password, Role role, int idInsegnante) {
        this.username = username;
        this.password = password;
        this.role = role;

        this.idInsegnante = (idInsegnante < 0) ? -1 : idInsegnante;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public int getIdInsegnante() {
        return idInsegnante;
    }
}
