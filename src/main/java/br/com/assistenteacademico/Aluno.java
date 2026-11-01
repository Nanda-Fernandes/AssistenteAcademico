package br.com.assistenteacademico;

public class Aluno {

    private final int id;
    private final String nome;
    public Aluno(int id, String nome) {

        this.id = id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

}