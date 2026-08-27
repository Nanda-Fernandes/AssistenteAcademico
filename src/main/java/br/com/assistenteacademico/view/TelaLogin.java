package br.com.assistenteacademico.view;

import br.com.assistenteacademico.Aluno;
import br.com.assistenteacademico.UsuarioDAO;

import javax.swing.*;
import java.awt.*;

public class TelaLogin extends JFrame {

    private final JTextField campoEmail = new JTextField();
    private final JPasswordField campoSenha = new JPasswordField();

    public TelaLogin() {
        setTitle("Assistente Acadêmico | Login");
        setSize(460, 360);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        EstiloVisual.configurarJanela(this);

        JPanel principal = new JPanel(new BorderLayout(20, 20));
        EstiloVisual.configurarPainel(principal);

        JLabel titulo = new JLabel("ASSISTENTE ACADÊMICO", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 25f));
        titulo.setForeground(EstiloVisual.AZUL_ESCURO);
        principal.add(titulo, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridLayout(4, 1, 8, 8));
        formulario.setOpaque(false);
        formulario.add(new JLabel("E-mail"));
        formulario.add(campoEmail);
        formulario.add(new JLabel("Senha"));
        formulario.add(campoSenha);
        principal.add(formulario, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new GridLayout(1, 2, 12, 0));
        botoes.setOpaque(false);
        JButton botaoLogin = new JButton("Entrar");
        JButton botaoCadastro = new JButton("Criar conta");
        EstiloVisual.configurarBotao(botaoLogin, true);
        EstiloVisual.configurarBotao(botaoCadastro, false);
        botoes.add(botaoLogin);
        botoes.add(botaoCadastro);
        principal.add(botoes, BorderLayout.SOUTH);

        botaoLogin.addActionListener(e -> fazerLogin());
        botaoCadastro.addActionListener(e -> abrirCadastro());
        campoSenha.addActionListener(e -> fazerLogin());

        add(principal);
        setVisible(true);
    }

    private void fazerLogin() {
        String email = campoEmail.getText().trim();
        String senha = new String(campoSenha.getPassword());
        if (email.isBlank() || senha.isBlank()) {
            JOptionPane.showMessageDialog(this, "Informe o e-mail e a senha.",
                    "Login", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Aluno aluno = new UsuarioDAO().autenticar(email, senha);
        if (aluno == null) {
            JOptionPane.showMessageDialog(this, "E-mail ou senha incorretos.",
                    "Login", JOptionPane.ERROR_MESSAGE);
            return;
        }

        dispose();
        new TelaDashboard(aluno.getId(), aluno.getNome());
    }

    private void abrirCadastro() {
        dispose();
        new TelaCadastro();
    }
}
