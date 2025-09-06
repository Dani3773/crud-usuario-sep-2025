package dev.daniel.crudusuariosep2025.domain;
//definir regras da entidade Usuario (contrato e validações planejadas)


public class Usuario {

    private Integer id;
    private String nome;
    private String email;
    private String tipo;

    public Usuario() {

    }

    public Usuario(Integer id, String nome, String email, String tipo) {

        this.id = id;
        this.nome = nome;
        this.email = email;
        this.tipo = tipo;
        
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + (nome != null ? nome : "") + '\'' +
                ", email='" + (email != null ? email : "") + '\'' +
                ", tipo='" + (tipo != null ? tipo : "") + '\'' +
                '}';
    }



}
