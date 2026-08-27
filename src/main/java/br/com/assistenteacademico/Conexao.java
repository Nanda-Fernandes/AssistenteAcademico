package br.com.assistenteacademico;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private static final String URL =
            "jdbc:sqlserver://localhost:1433;databaseName=AssistenteAcademico;encrypt=true;trustServerCertificate=true";

    private static final String USUARIO = "assistente_academico";

    private static final String SENHA = "2305";

    public static Connection conectar() throws SQLException {
        Connection conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
        System.out.println("Conectado ao banco com sucesso!");
        return conexao;
    }
}