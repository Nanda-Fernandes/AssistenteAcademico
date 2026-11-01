package br.com.assistenteacademico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {

    public String[] buscarPerfil(int idUsuario) {
        String sql = "SELECT nome, curso FROM Usuario WHERE id_usuario = ?";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idUsuario);
            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return new String[]{resultado.getString("nome"), resultado.getString("curso")};
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar perfil do usuário: " + e.getMessage());
        }
        return new String[]{"", ""};
    }

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

    public int[] contarPrioridades(int idUsuario) {

        int[] totais = new int[5];
        String sql =
                "WITH compromissos AS (" +
                        "SELECT a.data_entrega AS data_item " +
                        "FROM Atividade a " +
                        "INNER JOIN Disciplina d ON a.id_disciplina = d.id_disciplina " +
                        "WHERE d.id_usuario = ? AND a.status <> 'Concluído' " +
                        "UNION ALL " +
                        "SELECT e.data_estudo AS data_item " +
                        "FROM Estudo e " +
                        "INNER JOIN Disciplina d ON e.id_disciplina = d.id_disciplina " +
                        "WHERE d.id_usuario = ? AND e.status <> 'Concluído' " +
                        "UNION ALL " +
                        "SELECT p.data_prova AS data_item " +
                        "FROM Prova p " +
                        "INNER JOIN Disciplina d ON p.id_disciplina = d.id_disciplina " +
                        "WHERE d.id_usuario = ?" +
                        ") " +
                        "SELECT " +
                        "COALESCE(SUM(CASE WHEN data_item < CAST(GETDATE() AS DATE) THEN 1 ELSE 0 END), 0), " +
                        "COALESCE(SUM(CASE WHEN data_item = CAST(GETDATE() AS DATE) THEN 1 ELSE 0 END), 0), " +
                        "COALESCE(SUM(CASE WHEN data_item > CAST(GETDATE() AS DATE) " +
                        "AND data_item <= DATEADD(DAY, 3, CAST(GETDATE() AS DATE)) THEN 1 ELSE 0 END), 0), " +
                        "COALESCE(SUM(CASE WHEN data_item > DATEADD(DAY, 3, CAST(GETDATE() AS DATE)) " +
                        "AND data_item <= DATEADD(DAY, 7, CAST(GETDATE() AS DATE)) THEN 1 ELSE 0 END), 0), " +
                        "COALESCE(SUM(CASE WHEN data_item > DATEADD(DAY, 7, CAST(GETDATE() AS DATE)) THEN 1 ELSE 0 END), 0) " +
                        "FROM compromissos";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, idUsuario);
            comando.setInt(2, idUsuario);
            comando.setInt(3, idUsuario);

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    for (int i = 0; i < totais.length; i++) {
                        totais[i] = resultado.getInt(i + 1);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao contar prioridades: " + e.getMessage());
        }

        return totais;
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

}