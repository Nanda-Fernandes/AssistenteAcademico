package br.com.assistenteacademico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    private String ultimoErro;

    public String getUltimoErro() {
        return ultimoErro;
    }

    public boolean cadastrarUsuario(
            String nome,
            String email,
            String senha,
            String curso) {

        String sql = "INSERT INTO Usuario (nome, email, senha, curso) VALUES (?, ?, ?, ?)";

        if (emailJaCadastrado(email)) {
            return false;
        }

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, nome);
            comando.setString(2, email);
            comando.setString(3, senha);
            comando.setString(4, curso);

            comando.executeUpdate();

            System.out.println("Usuário cadastrado com sucesso!");
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar usuário: " + e.getMessage());
            return false;
        }
    }

    public boolean emailJaCadastrado(String email) {

        String sql = "SELECT 1 FROM Usuario WHERE email = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, email);

            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next();
            }

        } catch (SQLException e) {
            System.out.println(
                    "Erro ao verificar e-mail cadastrado: " + e.getMessage()
            );
            return false;
        }
    }

    public Aluno autenticar(String email, String senha) {
        ultimoErro = null;

        String sql = "SELECT id_usuario, nome " +
                "FROM Usuario " +
                "WHERE email = ? AND senha = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, email);
            comando.setString(2, senha);

            try (ResultSet resultado = comando.executeQuery()) {

                if (resultado.next()) {

                    int id = resultado.getInt("id_usuario");
                    String nome = resultado.getString("nome");
                    return new Aluno(id, nome);
                }
            }

        } catch (SQLException e) {
            ultimoErro = e.getMessage();
            System.out.println(
                    "Erro ao autenticar usuário: "
                            + e.getMessage()
            );
        }

        return null;
    }
}
