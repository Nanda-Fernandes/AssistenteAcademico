package br.com.assistenteacademico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EstudoDAO {

    public List<Object[]> buscarEstudosPorUsuario(int idUsuario) {
        List<Object[]> estudos = new ArrayList<>();
        String sql = "SELECT e.id_estudo, e.id_disciplina, d.nome AS disciplina, e.o_que_estudar, " +
                "e.data_estudo, e.horario, e.observacao, e.status FROM Estudo e " +
                "INNER JOIN Disciplina d ON d.id_disciplina = e.id_disciplina " +
                "WHERE d.id_usuario = ? ORDER BY e.data_estudo ASC";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idUsuario);
            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    estudos.add(new Object[]{
                            resultado.getInt("id_estudo"), resultado.getInt("id_disciplina"),
                            resultado.getString("disciplina"), resultado.getString("o_que_estudar"),
                            resultado.getDate("data_estudo"), resultado.getString("horario"),
                            resultado.getString("observacao"), resultado.getString("status")
                    });
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar estudos: " + e.getMessage());
        }
        return estudos;
    }

    public boolean cadastrarEstudo(int idDisciplina, String oQueEstudar, String dataEstudo,
                                   String horario, String observacao, String status) {
        String sql = "INSERT INTO Estudo " +
                "(id_disciplina, o_que_estudar, data_estudo, horario, observacao, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idDisciplina);
            comando.setString(2, oQueEstudar);
            comando.setDate(3, java.sql.Date.valueOf(LocalDate.parse(dataEstudo)));
            comando.setString(4, horario);
            comando.setString(5, observacao);
            comando.setString(6, status);
            return comando.executeUpdate() == 1;
        } catch (SQLException | java.time.format.DateTimeParseException e) {
            System.out.println("Erro ao cadastrar estudo: " + e.getMessage());
            return false;
        }
    }

    public boolean cadastrarEstudo(int idUsuario, int idDisciplina, String oQueEstudar, String dataEstudo,
                                   String horario, String observacao, String status) {
        if (!disciplinaPertenceAoUsuario(idDisciplina, idUsuario)) {
            return false;
        }
        return cadastrarEstudo(idDisciplina, oQueEstudar, dataEstudo, horario, observacao, status);
    }

    public boolean editarEstudo(int idEstudo, int idUsuario, int idDisciplina,
                                String oQueEstudar, String dataEstudo, String horario,
                                String observacao, String status) {
        String sql = "UPDATE e SET id_disciplina = ?, o_que_estudar = ?, data_estudo = ?, " +
                "horario = ?, observacao = ?, status = ? FROM Estudo e " +
                "INNER JOIN Disciplina d ON d.id_disciplina = e.id_disciplina " +
                "WHERE e.id_estudo = ? AND d.id_usuario = ? " +
                "AND EXISTS (SELECT 1 FROM Disciplina d2 WHERE d2.id_disciplina = ? AND d2.id_usuario = ?)";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idDisciplina);
            comando.setString(2, oQueEstudar);
            comando.setDate(3, java.sql.Date.valueOf(LocalDate.parse(dataEstudo)));
            comando.setString(4, horario);
            comando.setString(5, observacao);
            comando.setString(6, status);
            comando.setInt(7, idEstudo);
            comando.setInt(8, idUsuario);
            comando.setInt(9, idDisciplina);
            comando.setInt(10, idUsuario);
            return comando.executeUpdate() == 1;
        } catch (SQLException | java.time.format.DateTimeParseException e) {
            System.out.println("Erro ao editar estudo: " + e.getMessage());
            return false;
        }
    }

    public boolean excluirEstudo(int idEstudo, int idUsuario) {
        String sql = "DELETE e FROM Estudo e INNER JOIN Disciplina d ON d.id_disciplina = e.id_disciplina " +
                "WHERE e.id_estudo = ? AND d.id_usuario = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idEstudo);
            comando.setInt(2, idUsuario);
            return comando.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir estudo: " + e.getMessage());
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
            System.out.println("Erro ao validar disciplina do estudo: " + e.getMessage());
            return false;
        }
    }
}
