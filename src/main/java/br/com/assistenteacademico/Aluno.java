package br.com.assistenteacademico;

public class Aluno {

    private int id;
    private String nome;
    private String email;
    private String senha;
    private String curso;

    public Aluno(
            int id,
            String nome,
            String email,
            String senha,
            String curso) {

        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.curso = curso;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public String getCurso() {
        return curso;
    }
}