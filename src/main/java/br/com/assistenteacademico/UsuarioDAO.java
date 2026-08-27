package br.com.assistenteacademico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public void listarUsuarios() {

        String sql = "SELECT id_usuario, nome, email, curso FROM Usuario";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {

                int id = resultado.getInt("id_usuario");
                String nome = resultado.getString("nome");
                String email = resultado.getString("email");
                String curso = resultado.getString("curso");

                System.out.println("ID: " + id);
                System.out.println("Nome: " + nome);
                System.out.println("E-mail: " + email);
                System.out.println("Curso: " + curso);
                System.out.println("-------------------------");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao consultar usuários: " + e.getMessage());
        }
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

    public int fazerLogin(String email, String senha) {

        String sql = "SELECT id_usuario, nome, curso " +
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
                    String curso = resultado.getString("curso");

                    System.out.println();
                    System.out.println("Login realizado com sucesso!");
                    System.out.println("Nome: " + nome);
                    System.out.println("Curso: " + curso);

                    return id;
                }

                System.out.println("E-mail ou senha incorretos.");
                return -1;
            }

        } catch (SQLException e) {
            System.out.println("Erro ao realizar login: " + e.getMessage());
            return -1;
        }
    }

    public Aluno autenticar(String email, String senha) {

        String sql = "SELECT id_usuario, nome, email, senha, curso " +
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
                    String emailBanco = resultado.getString("email");
                    String senhaBanco = resultado.getString("senha");
                    String curso = resultado.getString("curso");

                    return new Aluno(
                            id,
                            nome,
                            emailBanco,
                            senhaBanco,
                            curso
                    );
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Erro ao autenticar usuário: "
                            + e.getMessage()
            );
        }

        return null;
    }
}
