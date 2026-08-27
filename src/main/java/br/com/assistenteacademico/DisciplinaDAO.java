package br.com.assistenteacademico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DisciplinaDAO {

    public boolean cadastrarDisciplina(int idUsuario, String nome, String professor,
                                      String curso, String semestre, String horario,
                                      String sala, String observacao) {
        String sql = "INSERT INTO Disciplina " +
                "(id_usuario, nome, professor, curso, semestre, horario, sala, observacao) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idUsuario);
            comando.setString(2, nome);
            comando.setString(3, professor);
            comando.setString(4, curso);
            comando.setString(5, semestre);
            comando.setString(6, horario);
            comando.setString(7, sala);
            comando.setString(8, observacao);
            return comando.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar disciplina: " + e.getMessage());
            return false;
        }
    }

    public List<Object[]> buscarDisciplinasPorUsuario(int idUsuario) {
        List<Object[]> disciplinas = new ArrayList<>();
        String sql = "SELECT id_disciplina, nome, professor, curso, semestre, horario, sala, observacao " +
                "FROM Disciplina WHERE id_usuario = ? ORDER BY nome";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idUsuario);
            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    disciplinas.add(new Object[]{
                            resultado.getInt("id_disciplina"), resultado.getString("nome"),
                            resultado.getString("professor"), resultado.getString("curso"),
                            resultado.getString("semestre"), resultado.getString("horario"),
                            resultado.getString("sala"), resultado.getString("observacao")
                    });
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar disciplinas: " + e.getMessage());
        }
        return disciplinas;
    }

    public List<Object[]> listarDisciplinasPorUsuario(int idUsuario) {
        return buscarDisciplinasPorUsuario(idUsuario);
    }

    public boolean editarDisciplina(int idDisciplina, int idUsuario, String nome,
                                    String professor, String curso, String semestre,
                                    String horario, String sala, String observacao) {
        String sql = "UPDATE Disciplina SET nome = ?, professor = ?, curso = ?, " +
                "semestre = ?, horario = ?, sala = ?, observacao = ? " +
                "WHERE id_disciplina = ? AND id_usuario = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, nome);
            comando.setString(2, professor);
            comando.setString(3, curso);
            comando.setString(4, semestre);
            comando.setString(5, horario);
            comando.setString(6, sala);
            comando.setString(7, observacao);
            comando.setInt(8, idDisciplina);
            comando.setInt(9, idUsuario);
            return comando.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Erro ao editar disciplina: " + e.getMessage());
            return false;
        }
    }

    public boolean possuiRegistrosRelacionados(int idDisciplina, int idUsuario) {
        String sql = "SELECT CASE WHEN EXISTS (" +
                "SELECT 1 FROM Disciplina d INNER JOIN Atividade a ON a.id_disciplina = d.id_disciplina " +
                "WHERE d.id_disciplina = ? AND d.id_usuario = ?) OR EXISTS (" +
                "SELECT 1 FROM Disciplina d INNER JOIN Prova p ON p.id_disciplina = d.id_disciplina " +
                "WHERE d.id_disciplina = ? AND d.id_usuario = ?) OR EXISTS (" +
                "SELECT 1 FROM Disciplina d INNER JOIN Estudo e ON e.id_disciplina = d.id_disciplina " +
                "WHERE d.id_disciplina = ? AND d.id_usuario = ?) THEN 1 ELSE 0 END AS possui";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idDisciplina);
            comando.setInt(2, idUsuario);
            comando.setInt(3, idDisciplina);
            comando.setInt(4, idUsuario);
            comando.setInt(5, idDisciplina);
            comando.setInt(6, idUsuario);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() && resultado.getBoolean("possui");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao verificar dependências da disciplina: " + e.getMessage());
            return true;
        }
    }

    public boolean excluirDisciplina(int idDisciplina, int idUsuario) {
        String sql = "DELETE FROM Disciplina WHERE id_disciplina = ? AND id_usuario = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idDisciplina);
            comando.setInt(2, idUsuario);
            return comando.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir disciplina: " + e.getMessage());
            return false;
        }
    }
}
