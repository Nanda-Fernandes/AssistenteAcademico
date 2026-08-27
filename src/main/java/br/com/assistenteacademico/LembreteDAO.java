package br.com.assistenteacademico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class LembreteDAO {

    public List<Object[]> buscarLembretesPorUsuario(int idUsuario) {
        List<Object[]> lembretes = new ArrayList<>();
        String sql = "SELECT id_lembrete, tipo_item, id_item, data_lembrete, status " +
                "FROM Lembrete WHERE id_usuario = ? ORDER BY data_lembrete ASC";
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idUsuario);
            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    lembretes.add(new Object[]{resultado.getInt("id_lembrete"),
                            resultado.getString("tipo_item"), resultado.getInt("id_item"),
                            resultado.getTimestamp("data_lembrete"), resultado.getString("status")});
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar lembretes: " + e.getMessage());
        }
        return lembretes;
    }

    public List<Object[]> listarItensPorUsuario(int idUsuario) {
        List<Object[]> itens = new ArrayList<>();
        String sql = "SELECT 'ATIVIDADE' AS tipo, a.id_atividade AS id, d.nome + ' - ' + a.titulo AS descricao " +
                "FROM Atividade a INNER JOIN Disciplina d ON d.id_disciplina=a.id_disciplina WHERE d.id_usuario=? " +
                "UNION ALL SELECT 'PROVA', p.id_prova, d.nome + ' - ' + p.conteudo FROM Prova p INNER JOIN Disciplina d ON d.id_disciplina=p.id_disciplina WHERE d.id_usuario=? " +
                "UNION ALL SELECT 'ESTUDO', e.id_estudo, d.nome + ' - ' + e.o_que_estudar FROM Estudo e INNER JOIN Disciplina d ON d.id_disciplina=e.id_disciplina WHERE d.id_usuario=? ORDER BY descricao";
        try (Connection conexao = Conexao.conectar(); PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idUsuario); comando.setInt(2, idUsuario); comando.setInt(3, idUsuario);
            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) itens.add(new Object[]{resultado.getString("tipo"), resultado.getInt("id"), resultado.getString("descricao")});
            }
        } catch (SQLException e) { System.out.println("Erro ao consultar itens para lembrete: " + e.getMessage()); }
        return itens;
    }

    public boolean cadastrarLembrete(String tipoItem, int idItem, String dataLembrete, String status, int idUsuario) {
        if (!itemPertenceAoUsuario(tipoItem, idItem, idUsuario)) return false;
        String sql = "INSERT INTO Lembrete (tipo_item, id_item, data_lembrete, status, id_usuario) VALUES (?, ?, ?, ?, ?)";
        try (Connection conexao = Conexao.conectar(); PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, tipoItem); comando.setInt(2, idItem);
            comando.setTimestamp(3, Timestamp.valueOf(dataLembrete)); comando.setString(4, status); comando.setInt(5, idUsuario);
            return comando.executeUpdate() == 1;
        } catch (SQLException | IllegalArgumentException e) { System.out.println("Erro ao cadastrar lembrete: " + e.getMessage()); return false; }
    }

    private boolean itemPertenceAoUsuario(String tipo, int idItem, int idUsuario) {
        String tabela; String idColuna;
        switch (tipo) { case "ATIVIDADE" -> { tabela="Atividade"; idColuna="id_atividade"; } case "PROVA" -> { tabela="Prova"; idColuna="id_prova"; } case "ESTUDO" -> { tabela="Estudo"; idColuna="id_estudo"; } default -> { return false; } }
        String sql = "SELECT 1 FROM " + tabela + " i INNER JOIN Disciplina d ON d.id_disciplina=i.id_disciplina WHERE i." + idColuna + "=? AND d.id_usuario=?";
        try (Connection conexao=Conexao.conectar(); PreparedStatement comando=conexao.prepareStatement(sql)) { comando.setInt(1,idItem); comando.setInt(2,idUsuario); try(ResultSet r=comando.executeQuery()){return r.next();} } catch(SQLException e){return false;}
    }

    public boolean alterarStatus(int idLembrete, int idUsuario, String status) { return executar("UPDATE Lembrete SET status=? WHERE id_lembrete=? AND id_usuario=?", status, idLembrete, idUsuario); }
    public boolean excluirLembrete(int idLembrete, int idUsuario) { return executar("DELETE FROM Lembrete WHERE id_lembrete=? AND id_usuario=?", null, idLembrete, idUsuario); }
    private boolean executar(String sql, String status, int id, int usuario) { try(Connection c=Conexao.conectar(); PreparedStatement p=c.prepareStatement(sql)){int i=1;if(status!=null)p.setString(i++,status);p.setInt(i++,id);p.setInt(i,usuario);return p.executeUpdate()==1;}catch(SQLException e){System.out.println("Erro ao alterar lembrete: "+e.getMessage());return false;} }
}
