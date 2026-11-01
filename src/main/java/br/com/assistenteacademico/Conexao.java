package br.com.assistenteacademico;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private static final String URL = configuracao(
            "ASSISTENTE_DB_URL",
            "jdbc:sqlserver://127.0.0.1:1433;databaseName=AssistenteAcademico;encrypt=false;loginTimeout=5");

    private static final String USUARIO = configuracao("ASSISTENTE_DB_USER", "assistente_academico");

    private static final String VARIAVEL_SENHA = "ASSISTENTE_DB_PASSWORD";

    public static Connection conectar() throws SQLException {
        String senha = System.getenv(VARIAVEL_SENHA);
        if (senha == null || senha.isBlank()) {
            throw new SQLException("Configure a variável de ambiente " + VARIAVEL_SENHA + " antes de conectar.");
        }
        return DriverManager.getConnection(URL, USUARIO, senha);
    }

    private static String configuracao(String variavel, String valorPadrao) {
        String valor = System.getenv(variavel);
        return valor == null || valor.isBlank() ? valorPadrao : valor;
    }
}