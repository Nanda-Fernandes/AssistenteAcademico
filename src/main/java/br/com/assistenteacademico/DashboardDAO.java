package br.com.assistenteacademico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {

    public int contarDisciplinas(int idUsuario) {

        String sql = "SELECT COUNT(*) " +
                "FROM Disciplina " +
                "WHERE id_usuario = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, idUsuario);

            try (ResultSet resultado = comando.executeQuery()) {

                if (resultado.next()) {
                    return resultado.getInt(1);
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Erro ao contar disciplinas: "
                            + e.getMessage()
            );
        }

        return 0;
    }


    public int contarAtividadesPendentes(int idUsuario) {

        String sql = "SELECT COUNT(*) " +
                "FROM Atividade " +
                "INNER JOIN Disciplina " +
                "ON Atividade.id_disciplina = Disciplina.id_disciplina " +
                "WHERE Disciplina.id_usuario = ? " +
                "AND Atividade.status <> 'Concluído'";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, idUsuario);

            try (ResultSet resultado = comando.executeQuery()) {

                if (resultado.next()) {
                    return resultado.getInt(1);
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Erro ao contar atividades: "
                            + e.getMessage()
            );
        }

        return 0;
    }


    public int contarEstudosPendentes(int idUsuario) {

        String sql = "SELECT COUNT(*) " +
                "FROM Estudo " +
                "INNER JOIN Disciplina " +
                "ON Estudo.id_disciplina = Disciplina.id_disciplina " +
                "WHERE Disciplina.id_usuario = ? " +
                "AND Estudo.status <> 'Concluído'";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, idUsuario);

            try (ResultSet resultado = comando.executeQuery()) {

                if (resultado.next()) {
                    return resultado.getInt(1);
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Erro ao contar estudos: "
                            + e.getMessage()
            );
        }

        return 0;
    }


    public List<Object[]> buscarProximosCompromissos(int idUsuario) {

        List<Object[]> compromissos = new ArrayList<>();

        String sql =
                "SELECT TOP 5 " +
                        "Disciplina.nome AS disciplina, " +
                        "Atividade.titulo AS titulo, " +
                        "Atividade.data_entrega AS data, " +
                        "'Atividade' AS tipo " +
                        "FROM Disciplina " +
                        "INNER JOIN Atividade " +
                        "ON Disciplina.id_disciplina = Atividade.id_disciplina " +
                        "WHERE Disciplina.id_usuario = ? " +
                        "AND Atividade.status <> 'Concluído' " +

                        "UNION ALL " +

                        "SELECT TOP 5 " +
                        "Disciplina.nome AS disciplina, " +
                        "Estudo.o_que_estudar AS titulo, " +
                        "Estudo.data_estudo AS data, " +
                        "'Estudo' AS tipo " +
                        "FROM Disciplina " +
                        "INNER JOIN Estudo " +
                        "ON Disciplina.id_disciplina = Estudo.id_disciplina " +
                        "WHERE Disciplina.id_usuario = ? " +
                        "AND Estudo.status <> 'Concluído' " +

                        "ORDER BY data ASC";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, idUsuario);
            comando.setInt(2, idUsuario);

            try (ResultSet resultado = comando.executeQuery()) {

                while (resultado.next()) {

                    compromissos.add(new Object[]{
                            resultado.getString("disciplina"),
                            resultado.getString("titulo"),
                            resultado.getDate("data"),
                            resultado.getString("tipo")
                    });
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Erro ao buscar próximos compromissos: "
                            + e.getMessage()
            );
        }

        return compromissos;
    }


    public List<Object[]> buscarProximasProvas(int idUsuario) {

        List<Object[]> provas = new ArrayList<>();

        String sql =
                "SELECT TOP 5 " +
                        "Disciplina.nome AS disciplina, " +
                        "Prova.data_prova, " +
                        "Prova.conteudo, " +
                        "Prova.observacao " +
                        "FROM Disciplina " +
                        "INNER JOIN Prova " +
                        "ON Disciplina.id_disciplina = Prova.id_disciplina " +
                        "WHERE Disciplina.id_usuario = ? " +
                        "AND Prova.data_prova >= CAST(GETDATE() AS DATE) " +
                        "ORDER BY Prova.data_prova ASC";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, idUsuario);

            try (ResultSet resultado = comando.executeQuery()) {

                while (resultado.next()) {

                    provas.add(new Object[]{
                            resultado.getString("disciplina"),
                            resultado.getDate("data_prova"),
                            resultado.getString("conteudo"),
                            resultado.getString("observacao")
                    });
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Erro ao buscar próximas provas: "
                            + e.getMessage()
            );
        }

        return provas;
    }

    public List<Object[]> buscarLembretesPendentes(int idUsuario) {
        List<Object[]> lembretes = new ArrayList<>();
        String sql = "SELECT TOP 5 l.tipo_item, l.id_item, l.data_lembrete, l.status " +
                "FROM Lembrete l WHERE l.id_usuario = ? AND l.status <> 'Concluído' " +
                "ORDER BY l.data_lembrete ASC";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idUsuario);
            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    lembretes.add(new Object[]{
                            resultado.getString("tipo_item"),
                            resultado.getInt("id_item"),
                            resultado.getTimestamp("data_lembrete"),
                            resultado.getString("status")
                    });
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar lembretes pendentes: " + e.getMessage());
        }
        return lembretes;
    }
}