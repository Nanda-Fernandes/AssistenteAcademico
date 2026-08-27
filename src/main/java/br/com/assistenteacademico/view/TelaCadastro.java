package br.com.assistenteacademico.view;

import br.com.assistenteacademico.UsuarioDAO;

import javax.swing.*;
import java.awt.*;

public class TelaCadastro extends JFrame {

    private JTextField campoNome;
    private JTextField campoEmail;
    private JPasswordField campoSenha;
    private JTextField campoCurso;

    public TelaCadastro() {

        setTitle("Criar conta - Assistente Acadêmico");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painel = new JPanel(new GridLayout(7, 2, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel(
                "CRIAR CONTA",
                SwingConstants.CENTER
        );

        campoNome = new JTextField();
        campoEmail = new JTextField();
        campoSenha = new JPasswordField();
        campoCurso = new JTextField();

        JButton botaoCadastrar = new JButton("Cadastrar");
        JButton botaoVoltar = new JButton("Voltar para login");

        painel.add(titulo);
        painel.add(new JLabel());
        painel.add(new JLabel("Nome:*"));
        painel.add(campoNome);
        painel.add(new JLabel("E-mail:*"));
        painel.add(campoEmail);
        painel.add(new JLabel("Senha:*"));
        painel.add(campoSenha);
        painel.add(new JLabel("Curso:*"));
        painel.add(campoCurso);
        painel.add(botaoCadastrar);
        painel.add(botaoVoltar);

        add(painel);

        botaoCadastrar.addActionListener(e -> cadastrar());
        botaoVoltar.addActionListener(e -> {
            dispose();
            new TelaLogin();
        });

        setVisible(true);
    }

    private void cadastrar() {

        String nome = campoNome.getText().trim();
        String email = campoEmail.getText().trim();
        String senha = new String(campoSenha.getPassword());
        String curso = campoCurso.getText().trim();

        if (nome.isBlank() ||
                email.isBlank() ||
                senha.isBlank() ||
                curso.isBlank()) {

            JOptionPane.showMessageDialog(
                    this,
                "Preencha todos os campos obrigatórios."
            );

            return;
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Informe um e-mail válido.",
                    "Cadastro",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (senha.length() < 6) {
            JOptionPane.showMessageDialog(
                    this,
                    "A senha deve ter pelo menos 6 caracteres.",
                    "Cadastro",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        UsuarioDAO usuarioDAO = new UsuarioDAO();

        if (usuarioDAO.emailJaCadastrado(email)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Já existe uma conta cadastrada com este e-mail.",
                    "Cadastro",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        boolean cadastrado = usuarioDAO.cadastrarUsuario(
                nome,
                email,
                senha,
                curso
        );

        if (!cadastrado) {

            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível realizar o cadastro. Tente novamente.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Cadastro realizado com sucesso!"
        );

        dispose();

        new TelaLogin();
    }
}
