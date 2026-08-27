package br.com.assistenteacademico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProvaDAO {

    public List<Object[]> buscarProvasPorUsuario(int idUsuario) {
        List<Object[]> provas = new ArrayList<>();
        String sql = "SELECT p.id_prova, p.id_disciplina, d.nome AS disciplina, p.data_prova, " +
                "p.conteudo, p.observacao FROM Prova p " +
                "INNER JOIN Disciplina d ON d.id_disciplina = p.id_disciplina " +
                "WHERE d.id_usuario = ? ORDER BY p.data_prova ASC";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idUsuario);
            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    provas.add(new Object[]{
                            resultado.getInt("id_prova"), resultado.getInt("id_disciplina"),
                            resultado.getString("disciplina"), resultado.getDate("data_prova"),
                            resultado.getString("conteudo"), resultado.getString("observacao")
                    });
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar provas: " + e.getMessage());
        }
        return provas;
    }

    public boolean cadastrarProva(int idDisciplina, String dataProva,
                                  String conteudo, String observacao) {
        String sql = "INSERT INTO Prova (id_disciplina, data_prova, conteudo, observacao) VALUES (?, ?, ?, ?)";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idDisciplina);
            comando.setDate(2, java.sql.Date.valueOf(LocalDate.parse(dataProva)));
            comando.setString(3, conteudo);
            comando.setString(4, observacao);
            return comando.executeUpdate() == 1;
        } catch (SQLException | java.time.format.DateTimeParseException e) {
            System.out.println("Erro ao cadastrar prova: " + e.getMessage());
            return false;
        }
    }

    public boolean cadastrarProva(int idUsuario, int idDisciplina, String dataProva,
                                  String conteudo, String observacao) {
        if (!disciplinaPertenceAoUsuario(idDisciplina, idUsuario)) {
            return false;
        }
        return cadastrarProva(idDisciplina, dataProva, conteudo, observacao);
    }

    public boolean editarProva(int idProva, int idUsuario, int idDisciplina,
                               String dataProva, String conteudo, String observacao) {
        String sql = "UPDATE p SET id_disciplina = ?, data_prova = ?, conteudo = ?, observacao = ? " +
                "FROM Prova p INNER JOIN Disciplina d ON d.id_disciplina = p.id_disciplina " +
                "WHERE p.id_prova = ? AND d.id_usuario = ? " +
                "AND EXISTS (SELECT 1 FROM Disciplina d2 WHERE d2.id_disciplina = ? AND d2.id_usuario = ?)";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idDisciplina);
            comando.setDate(2, java.sql.Date.valueOf(LocalDate.parse(dataProva)));
            comando.setString(3, conteudo);
            comando.setString(4, observacao);
            comando.setInt(5, idProva);
            comando.setInt(6, idUsuario);
            comando.setInt(7, idDisciplina);
            comando.setInt(8, idUsuario);
            return comando.executeUpdate() == 1;
        } catch (SQLException | java.time.format.DateTimeParseException e) {
            System.out.println("Erro ao editar prova: " + e.getMessage());
            return false;
        }
    }

    public boolean excluirProva(int idProva, int idUsuario) {
        String sql = "DELETE p FROM Prova p INNER JOIN Disciplina d " +
                "ON d.id_disciplina = p.id_disciplina " +
                "WHERE p.id_prova = ? AND d.id_usuario = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idProva);
            comando.setInt(2, idUsuario);
            return comando.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir prova: " + e.getMessage());
            return false;
        }
    }

    private boolean disciplinaPertenceAoUsuario(int idDisciplina, int idUsuario) {
        String sql = "SELECT 1 FROM Disciplina WHERE id_disciplina = ? AND id_usuario = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idDisciplina);
            comando.setInt(2, idUsuario);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao validar disciplina da prova: " + e.getMessage());
            return false;
        }
    }
}
