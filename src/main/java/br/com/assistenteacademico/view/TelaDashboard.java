package br.com.assistenteacademico.view;

import br.com.assistenteacademico.DashboardDAO;
import br.com.assistenteacademico.Prioridade;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TelaDashboard extends JFrame {

    private static final Color COR_ATRASADO = new Color(198, 40, 40);
    private static final Color COR_HOJE = new Color(230, 126, 34);
    private static final Color COR_URGENTE = new Color(211, 84, 0);
    private static final Color COR_PROXIMO = new Color(41, 128, 185);
    private static final Color COR_FUTURO = new Color(88, 89, 91);

    private int idUsuario;
    private String nomeUsuario;

    private JLabel labelDisciplinas;
    private JLabel labelAtividades;
    private JLabel labelEstudos;

    public TelaDashboard(int idUsuario, String nomeUsuario) {

        this.idUsuario = idUsuario;
        this.nomeUsuario = nomeUsuario;

        setTitle("Assistente Acadêmico");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painelPrincipal =
                new JPanel(new BorderLayout(20, 20));

        painelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(
                        30, 30, 30, 30
                )
        );

        JLabel saudacao =
                new JLabel(
                        "Olá, " + nomeUsuario + "! 👋"
                );

        saudacao.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        28
                )
        );

        painelPrincipal.add(
                saudacao,
                BorderLayout.NORTH
        );

        JPanel painelResumo =
                new JPanel(
                        new GridLayout(1, 3, 20, 20)
                );

        labelDisciplinas =
                new JLabel("0", SwingConstants.CENTER);

        labelAtividades =
                new JLabel("0", SwingConstants.CENTER);

        labelEstudos =
                new JLabel("0", SwingConstants.CENTER);

        painelResumo.add(
                criarCard(
                        "📚 Disciplinas",
                        labelDisciplinas
                )
        );

        painelResumo.add(
                criarCard(
                        "📝 Atividades pendentes",
                        labelAtividades
                )
        );

        painelResumo.add(
                criarCard(
                        "📖 Estudos pendentes",
                        labelEstudos
                )
        );

        JPanel painelCentro =
                new JPanel(new BorderLayout(20, 20));

        painelCentro.add(
                painelResumo,
                BorderLayout.NORTH
        );

        JPanel painelInformacoes =
                new JPanel(
                        new GridLayout(1, 3, 20, 20)
                );

        JPanel painelCompromissos =
                criarPainelCompromissos();

        JPanel painelProvas =
                criarPainelProvas();

        JPanel painelLembretes =
                criarPainelLembretes();

        painelInformacoes.add(
                painelCompromissos
        );

        painelInformacoes.add(
                painelProvas
        );

        painelInformacoes.add(
                painelLembretes
        );

        painelCentro.add(
                painelInformacoes,
                BorderLayout.CENTER
        );

        painelPrincipal.add(
                painelCentro,
                BorderLayout.CENTER
        );

        JButton botaoContinuar =
                new JButton("Continuar");

        botaoContinuar.addActionListener(e -> {

            dispose();

            new TelaPrincipal(
                    idUsuario,
                    nomeUsuario
            );
        });

        painelPrincipal.add(
                botaoContinuar,
                BorderLayout.SOUTH
        );

        add(painelPrincipal);

        carregarResumo();

        setVisible(true);
    }

    private JPanel criarCard(
            String titulo,
            JLabel valor) {

        JPanel painel =
                new JPanel(
                        new BorderLayout()
                );

        painel.setBorder(
                BorderFactory.createTitledBorder(
                        titulo
                )
        );

        valor.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        36
                )
        );

        painel.add(
                valor,
                BorderLayout.CENTER
        );

        return painel;
    }

    private void carregarResumo() {

        DashboardDAO dashboardDAO =
                new DashboardDAO();

        int disciplinas =
                dashboardDAO.contarDisciplinas(
                        idUsuario
                );

        int atividades =
                dashboardDAO.contarAtividadesPendentes(
                        idUsuario
                );

        int estudos =
                dashboardDAO.contarEstudosPendentes(
                        idUsuario
                );

        labelDisciplinas.setText(
                String.valueOf(disciplinas)
        );

        labelAtividades.setText(
                String.valueOf(atividades)
        );

        labelEstudos.setText(
                String.valueOf(estudos)
        );
    }

    private JPanel criarPainelCompromissos() {

        JPanel painel =
                new JPanel(new BorderLayout());

        painel.setBorder(
                BorderFactory.createTitledBorder(
                        "📌 Próximos compromissos"
                )
        );

        DefaultListModel<String> modelo =
                new DefaultListModel<>();

        JList<String> lista = criarListaPrioridades(modelo);

        DashboardDAO dashboardDAO =
                new DashboardDAO();

        List<Object[]> compromissos =
                dashboardDAO.buscarProximosCompromissos(
                        idUsuario
                );

        for (Object[] compromisso : compromissos) {

            String disciplina =
                    (String) compromisso[0];

            String titulo =
                    (String) compromisso[1];

            String tipo =
                    (String) compromisso[3];

            java.time.LocalDate dataCompromisso =
                    ((java.sql.Date) compromisso[2]).toLocalDate();

            String prioridade =
                    Prioridade.calcular(dataCompromisso);

            String texto =
                    prioridade + " | " +
                            dataCompromisso + " | " +
                            tipo + " | " +
                            disciplina + " | " +
                            titulo;

            modelo.addElement(texto);
        }

        if (modelo.isEmpty()) {

            modelo.addElement(
                    "Nenhum compromisso pendente."
            );
        }

        painel.add(
                new JScrollPane(lista),
                BorderLayout.CENTER
        );

        return painel;
    }

    private JPanel criarPainelProvas() {

        JPanel painel =
                new JPanel(new BorderLayout());

        painel.setBorder(
                BorderFactory.createTitledBorder(
                        "📅 Próximas provas"
                )
        );

        DefaultListModel<String> modelo =
                new DefaultListModel<>();

        JList<String> lista = criarListaPrioridades(modelo);

        DashboardDAO dashboardDAO =
                new DashboardDAO();

        List<Object[]> provas =
                dashboardDAO.buscarProximasProvas(
                        idUsuario
                );

        for (Object[] prova : provas) {

            String disciplina =
                    (String) prova[0];

            String conteudo =
                    (String) prova[2];

            java.time.LocalDate dataProva =
                    ((java.sql.Date) prova[1]).toLocalDate();

            String prioridade =
                    Prioridade.calcular(dataProva);

            String texto =
                    prioridade + " | " +
                            dataProva + " | " +
                            disciplina + " | " +
                            conteudo;

            modelo.addElement(texto);
        }

        if (modelo.isEmpty()) {

            modelo.addElement(
                    "Nenhuma prova próxima."
            );
        }

        painel.add(
                new JScrollPane(lista),
                BorderLayout.CENTER
        );

        return painel;
    }

    private JPanel criarPainelLembretes() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createTitledBorder("🔔 Lembretes pendentes"));

        DefaultListModel<String> modelo = new DefaultListModel<>();
        JList<String> lista = criarListaPrioridades(modelo);
        DashboardDAO dashboardDAO = new DashboardDAO();

        for (Object[] lembrete : dashboardDAO.buscarLembretesPendentes(idUsuario)) {
            java.sql.Timestamp data = (java.sql.Timestamp) lembrete[2];
            String prioridade = Prioridade.calcular(data.toLocalDateTime().toLocalDate());
            modelo.addElement(prioridade + " | " + data.toLocalDateTime().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                    + " | " + lembrete[0] + " #" + lembrete[1]);
        }

        if (modelo.isEmpty()) {
            modelo.addElement("Nenhum lembrete pendente.");
        }

        painel.add(new JScrollPane(lista), BorderLayout.CENTER);
        return painel;
    }

    private JList<String> criarListaPrioridades(
            DefaultListModel<String> modelo) {

        JList<String> lista = new JList<>(modelo);

        lista.setCellRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                JLabel rotulo = (JLabel) super.getListCellRendererComponent(
                        list,
                        value,
                        index,
                        isSelected,
                        cellHasFocus
                );

                String texto = String.valueOf(value);
                Color cor = identificarCorPrioridade(texto);

                rotulo.setFont(new Font("Arial", Font.BOLD, 13));
                rotulo.setBorder(
                        BorderFactory.createEmptyBorder(6, 8, 6, 8)
                );

                if (!isSelected && cor != null) {
                    rotulo.setForeground(cor);
                    rotulo.setBackground(new Color(250, 250, 250));
                }

                return rotulo;
            }
        });

        return lista;
    }

    private Color identificarCorPrioridade(String texto) {

        if (texto.startsWith("ATRASADO")) {
            return COR_ATRASADO;
        }

        if (texto.startsWith("HOJE")) {
            return COR_HOJE;
        }

        if (texto.startsWith("URGENTE")) {
            return COR_URGENTE;
        }

        if (texto.startsWith("PRÓXIMO")) {
            return COR_PROXIMO;
        }

        if (texto.startsWith("FUTURO")) {
            return COR_FUTURO;
        }

        return null;
    }
}
