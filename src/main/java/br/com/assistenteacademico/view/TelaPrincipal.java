package br.com.assistenteacademico.view;

import javax.swing.*;
import java.awt.*;

public class TelaPrincipal extends JFrame {

    private int idUsuario;
    private String nomeUsuario;

    public TelaPrincipal(int idUsuario, String nomeUsuario) {

        this.idUsuario = idUsuario;
        this.nomeUsuario = nomeUsuario;

        setTitle("Assistente Acadêmico");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel titulo = new JLabel(
                "ASSISTENTE ACADÊMICO - " + nomeUsuario,
                SwingConstants.CENTER
        );

        titulo.setFont(new Font("Arial", Font.BOLD, 24));

        JButton botaoDisciplinas = new JButton("Disciplinas");
        JButton botaoAtividades = new JButton("Atividades");
        JButton botaoProvas = new JButton("Provas");
        JButton botaoEstudos = new JButton("Estudos");
        JButton botaoLembretes = new JButton("Lembretes");
        JButton botaoSair = new JButton("Sair");

        JPanel painelBotoes = new JPanel(
                new GridLayout(4, 2, 10, 10)
        );
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JButton botaoDashboard = new JButton("Dashboard");
        painelBotoes.add(botaoDashboard);
        painelBotoes.add(botaoDisciplinas);
        painelBotoes.add(botaoAtividades);
        painelBotoes.add(botaoProvas);
        painelBotoes.add(botaoEstudos);
        painelBotoes.add(botaoLembretes);
        painelBotoes.add(botaoSair);

        setLayout(new BorderLayout(20, 20));

        add(titulo, BorderLayout.NORTH);
        add(painelBotoes, BorderLayout.CENTER);

        botaoDashboard.addActionListener(e -> {
            dispose();
            new TelaDashboard(idUsuario, nomeUsuario);
        });

        botaoDisciplinas.addActionListener(e -> {
            new TelaDisciplinas(idUsuario);
        });

        botaoAtividades.addActionListener(e -> {
            new TelaAtividades(idUsuario);
        });

        botaoEstudos.addActionListener(e -> {
            new TelaEstudos(idUsuario);
        });

        botaoProvas.addActionListener(e -> {
            new TelaProvas(idUsuario);
        });

        botaoLembretes.addActionListener(e -> {
            new TelaLembretes(idUsuario);
        });

        botaoSair.addActionListener(e -> {
            dispose();
            new TelaLogin();
        });

        setVisible(true);
    }
}