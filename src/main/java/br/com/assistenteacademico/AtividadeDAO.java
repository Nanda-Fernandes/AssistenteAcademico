package br.com.assistenteacademico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AtividadeDAO {

    public List<Object[]> buscarAtividadesPorUsuario(int idUsuario) {
        List<Object[]> atividades = new ArrayList<>();
        String sql = "SELECT a.id_atividade, a.id_disciplina, d.nome AS disciplina, a.titulo, " +
                "a.tipo, a.data_entrega, a.descricao, a.observacao, a.status " +
                "FROM Atividade a INNER JOIN Disciplina d ON d.id_disciplina = a.id_disciplina " +
                "WHERE d.id_usuario = ? ORDER BY a.data_entrega ASC";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idUsuario);
            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    atividades.add(new Object[]{
                            resultado.getInt("id_atividade"), resultado.getInt("id_disciplina"),
                            resultado.getString("disciplina"), resultado.getString("titulo"),
                            resultado.getString("tipo"), resultado.getDate("data_entrega"),
                            resultado.getString("descricao"), resultado.getString("observacao"),
                            resultado.getString("status")
                    });
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar atividades: " + e.getMessage());
        }
        return atividades;
    }

    public boolean cadastrarAtividade(int idDisciplina, String titulo, String tipo,
                                      String dataEntrega, String descricao,
                                      String observacao, String status) {
        String sql = "INSERT INTO Atividade " +
                "(id_disciplina, titulo, tipo, data_entrega, descricao, observacao, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idDisciplina);
            comando.setString(2, titulo);
            comando.setString(3, tipo);
            comando.setDate(4, java.sql.Date.valueOf(LocalDate.parse(dataEntrega)));
            comando.setString(5, descricao);
            comando.setString(6, observacao);
            comando.setString(7, status);
            return comando.executeUpdate() == 1;
        } catch (SQLException | java.time.format.DateTimeParseException e) {
            System.out.println("Erro ao cadastrar atividade: " + e.getMessage());
            return false;
        }
    }

    public boolean cadastrarAtividade(int idUsuario, int idDisciplina, String titulo, String tipo,
                                         String dataEntrega, String descricao,
                                      String observacao, String status) {
        if (!disciplinaPertenceAoUsuario(idDisciplina, idUsuario)) {
            return false;
        }
        return cadastrarAtividade(idDisciplina, titulo, tipo, dataEntrega, descricao, observacao, status);
    }

    public boolean editarAtividade(int idAtividade, int idUsuario, int idDisciplina,
                                   String titulo, String tipo, String dataEntrega,
                                   String descricao, String observacao, String status) {
        String sql = "UPDATE a SET id_disciplina = ?, titulo = ?, tipo = ?, data_entrega = ?, " +
                "descricao = ?, observacao = ?, status = ? FROM Atividade a " +
                "INNER JOIN Disciplina d ON d.id_disciplina = a.id_disciplina " +
                "WHERE a.id_atividade = ? AND d.id_usuario = ? " +
                "AND EXISTS (SELECT 1 FROM Disciplina d2 WHERE d2.id_disciplina = ? AND d2.id_usuario = ?)";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idDisciplina);
            comando.setString(2, titulo);
            comando.setString(3, tipo);
            comando.setDate(4, java.sql.Date.valueOf(LocalDate.parse(dataEntrega)));
            comando.setString(5, descricao);
            comando.setString(6, observacao);
            comando.setString(7, status);
            comando.setInt(8, idAtividade);
            comando.setInt(9, idUsuario);
            comando.setInt(10, idDisciplina);
            comando.setInt(11, idUsuario);
            return comando.executeUpdate() == 1;
        } catch (SQLException | java.time.format.DateTimeParseException e) {
            System.out.println("Erro ao editar atividade: " + e.getMessage());
            return false;
        }
    }

    public boolean alterarStatus(int idAtividade, int idUsuario, String status) {
        String sql = "UPDATE a SET status = ? FROM Atividade a " +
                "INNER JOIN Disciplina d ON d.id_disciplina = a.id_disciplina " +
                "WHERE a.id_atividade = ? AND d.id_usuario = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, status);
            comando.setInt(2, idAtividade);
            comando.setInt(3, idUsuario);
            return comando.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Erro ao alterar status da atividade: " + e.getMessage());
            return false;
        }
    }

    public boolean excluirAtividade(int idAtividade, int idUsuario) {
        String sqlRemoverLembretes = "DELETE l FROM Lembrete l " +
                "INNER JOIN Atividade a ON a.id_atividade = l.id_item " +
                "INNER JOIN Disciplina d ON d.id_disciplina = a.id_disciplina " +
                "WHERE l.tipo_item = 'ATIVIDADE' AND a.id_atividade = ? AND d.id_usuario = ?";
        String sqlRemoverAtividade = "DELETE a FROM Atividade a " +
                "INNER JOIN Disciplina d ON d.id_disciplina = a.id_disciplina " +
                "WHERE a.id_atividade = ? AND d.id_usuario = ?";

        try (Connection conexao = Conexao.conectar()) {
            conexao.setAutoCommit(false);
            try (PreparedStatement removerLembretes = conexao.prepareStatement(sqlRemoverLembretes);
                 PreparedStatement removerAtividade = conexao.prepareStatement(sqlRemoverAtividade)) {
                removerLembretes.setInt(1, idAtividade);
                removerLembretes.setInt(2, idUsuario);
                removerLembretes.executeUpdate();
                removerAtividade.setInt(1, idAtividade);
                removerAtividade.setInt(2, idUsuario);
                boolean excluida = removerAtividade.executeUpdate() == 1;
                conexao.commit();
                return excluida;
            } catch (SQLException e) {
                conexao.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao excluir atividade: " + e.getMessage());
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
            System.out.println("Erro ao validar disciplina da atividade: " + e.getMessage());
            return false;
        }
    }
}
