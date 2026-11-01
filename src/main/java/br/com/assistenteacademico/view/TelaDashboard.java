package br.com.assistenteacademico.view;

import br.com.assistenteacademico.DashboardDAO;
import br.com.assistenteacademico.Prioridade;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Path2D;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.List;
import java.util.Comparator;

public class TelaDashboard extends JFrame {
    private static final Color ROXO = new Color(75, 55, 190);
    private static final Color TEXTO = new Color(25, 28, 43);
    private static final Color SECUNDARIO = new Color(75, 82, 112);
    private static final Color BORDA = new Color(232, 231, 243);
    private final int idUsuario;
    private final String nomeUsuario;
    private JLabel nomePerfil;
    private JLabel cursoPerfil;
    private JLabel saudacaoPerfil;
    private JLabel valorDisciplinas;
    private JLabel valorAtividades;
    private JLabel valorEstudos;
    private JLabel valorProvas;
    private JPanel listaCompromissos;
    private JPanel listaProvas;
    private int[] totaisPrioridades = new int[5];
    private final JLabel[] valoresPrioridades = new JLabel[5];

    public TelaDashboard(int idUsuario, String nomeUsuario) {
        this.idUsuario = idUsuario;
        this.nomeUsuario = nomeUsuario;
        setTitle("Assistente Acadêmico | Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setBackground(Color.WHITE);
        add(criarTela());
        if (!Navegacao.estaPreparando()) setVisible(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        carregarDadosAsync();
    }

    private JPanel criarTela() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Color.WHITE);
        raiz.add(criarMenu(), BorderLayout.WEST);

        JPanel conteudo = new JPanel(new BorderLayout());
        conteudo.setBackground(Color.WHITE);
        conteudo.add(criarCabecalho(), BorderLayout.NORTH);

        JPanel corpo = new JPanel(new BorderLayout(24, 24));
        corpo.setBackground(new Color(252, 252, 255));
        corpo.setBorder(new EmptyBorder(28, 32, 30, 32));
        corpo.add(criarCards(), BorderLayout.NORTH);
        corpo.add(criarColunas(), BorderLayout.CENTER);
        conteudo.add(corpo, BorderLayout.CENTER);
        raiz.add(conteudo, BorderLayout.CENTER);
        return raiz;
    }

    private JPanel criarMenu() {
        return MenuLateral.criar(this, idUsuario, "Dashboard");
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(Color.WHITE);
        cabecalho.setBorder(new EmptyBorder(32, 40, 28, 38));
        JPanel saudacao = new JPanel();
        saudacao.setOpaque(false);
        saudacao.setLayout(new BoxLayout(saudacao, BoxLayout.Y_AXIS));
        saudacaoPerfil = new JLabel("Olá, " + primeiroNome(nomeUsuario) + "! 👋");
        JLabel ola = saudacaoPerfil;
        ola.setFont(new Font("SansSerif", Font.BOLD, 30));
        ola.setForeground(TEXTO);
        JLabel mensagem = new JLabel("Tenha um ótimo dia e bons estudos!");
        mensagem.setFont(new Font("SansSerif", Font.PLAIN, 17));
        mensagem.setForeground(SECUNDARIO);
        saudacao.add(ola);
        saudacao.add(Box.createVerticalStrut(10));
        saudacao.add(mensagem);
        cabecalho.add(saudacao, BorderLayout.WEST);

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 24, 4));
        direita.setOpaque(false);
        Locale portugues = Locale.forLanguageTag("pt-BR");
        LocalDate hoje = LocalDate.now();
        LocalTime agora = LocalTime.now();
        JPanel dataPainel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        dataPainel.setOpaque(false);
        JLabel calendario = new JLabel(new IconeCalendario(new Color(75, 82, 112)));
        JPanel textosData = new JPanel();
        textosData.setOpaque(false);
        textosData.setLayout(new BoxLayout(textosData, BoxLayout.Y_AXIS));
        JLabel data = new JLabel(hoje.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", portugues)));
        data.setFont(new Font("SansSerif", Font.BOLD, 15));
        data.setForeground(TEXTO);
        JLabel horario = new JLabel(hoje.format(DateTimeFormatter.ofPattern("EEEE", portugues)) +
                ", " + agora.format(DateTimeFormatter.ofPattern("HH:mm", portugues)));
        horario.setFont(new Font("SansSerif", Font.PLAIN, 15));
        horario.setForeground(SECUNDARIO);
        textosData.add(data);
        textosData.add(Box.createVerticalStrut(6));
        textosData.add(horario);
        dataPainel.add(calendario);
        dataPainel.add(textosData);
        JLabel avatar = new JLabel("<html><b>" + iniciais(nomeUsuario) + "</b></html>", SwingConstants.CENTER);
        avatar.setOpaque(true);
        avatar.setBackground(new Color(239, 234, 255));
        avatar.setForeground(ROXO);
        avatar.setPreferredSize(new Dimension(58, 58));
        nomePerfil = new JLabel(nomeUsuario);
        nomePerfil.setFont(new Font("SansSerif", Font.BOLD, 15));
        nomePerfil.setForeground(TEXTO);
        cursoPerfil = new JLabel("Carregando curso...");
        cursoPerfil.setFont(new Font("SansSerif", Font.PLAIN, 15));
        cursoPerfil.setForeground(SECUNDARIO);
        JPanel perfil = new JPanel();
        perfil.setOpaque(false);
        perfil.setLayout(new BoxLayout(perfil, BoxLayout.Y_AXIS));
        perfil.add(nomePerfil);
        perfil.add(Box.createVerticalStrut(6));
        perfil.add(cursoPerfil);
        direita.add(dataPainel);
        direita.add(avatar);
        direita.add(perfil);
        cabecalho.add(direita, BorderLayout.EAST);
        return cabecalho;
    }

    private JPanel criarCards() {
        JPanel cards = new JPanel(new GridLayout(1, 4, 20, 0));
        cards.setOpaque(false);
        valorDisciplinas = new JLabel("0");
        valorAtividades = new JLabel("0");
        valorEstudos = new JLabel("0");
        valorProvas = new JLabel("0");
        cards.add(card(CardIcon.DISCIPLINAS, "Disciplinas", valorDisciplinas, "Cadastradas",
                new Color(245, 242, 255), ROXO));
        cards.add(card(CardIcon.ATIVIDADES, "Atividades", valorAtividades, "Pendentes",
                new Color(239, 251, 247), new Color(0, 150, 120)));
        cards.add(card(CardIcon.ESTUDOS, "Estudos", valorEstudos, "Pendentes",
                new Color(241, 247, 255), new Color(24, 105, 220)));
        cards.add(card(CardIcon.PROVAS, "Próximas Provas", valorProvas, "Agendadas",
                new Color(255, 243, 247), new Color(220, 48, 85)));
        return cards;
    }

    private JPanel card(int tipoIcone, String titulo, JLabel valor, String legenda, Color fundo, Color cor) {
        JPanel card = new RoundedPanel(18, fundo);
        card.setLayout(new BorderLayout(22, 0));
        card.setBorder(new EmptyBorder(20, 22, 20, 20));
        JLabel i = new JLabel(new CardIcon(tipoIcone, cor));
        i.setHorizontalAlignment(SwingConstants.CENTER);
        i.setOpaque(true);
        i.setBackground(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 18));
        i.setPreferredSize(new Dimension(78, 78));
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(titulo);
        t.setFont(new Font("SansSerif", Font.BOLD, 16));
        t.setForeground(cor);
        valor.setFont(new Font("SansSerif", Font.BOLD, 32));
        valor.setForeground(TEXTO);
        JLabel l = new JLabel(legenda);
        l.setFont(new Font("SansSerif", Font.PLAIN, 14));
        l.setForeground(SECUNDARIO);
        textos.add(t); textos.add(Box.createVerticalStrut(7)); textos.add(valor); textos.add(l);
        card.add(i, BorderLayout.WEST);
        card.add(textos, BorderLayout.CENTER);
        return card;
    }

    private static final class CardIcon implements Icon {
        private static final int DISCIPLINAS = 1;
        private static final int ATIVIDADES = 2;
        private static final int ESTUDOS = 3;
        private static final int PROVAS = 4;
        private final int tipo;
        private final Color cor;

        private CardIcon(int tipo, Color cor) {
            this.tipo = tipo;
            this.cor = cor;
        }

        @Override public int getIconWidth() { return 48; }
        @Override public int getIconHeight() { return 48; }

        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(cor);
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            switch (tipo) {
                case DISCIPLINAS -> {
                    g2.drawRoundRect(x + 11, y + 8, 25, 32, 3, 3);
                    g2.drawRoundRect(x + 6, y + 13, 25, 32, 3, 3);
                    g2.drawLine(x + 19, y + 21, x + 24, y + 21);
                    g2.drawLine(x + 19, y + 27, x + 27, y + 27);
                }
                case ATIVIDADES -> {
                    g2.drawRoundRect(x + 9, y + 10, 30, 34, 4, 4);
                    g2.drawRoundRect(x + 18, y + 5, 12, 8, 3, 3);
                    g2.drawLine(x + 16, y + 28, x + 22, y + 34);
                    g2.drawLine(x + 22, y + 34, x + 33, y + 21);
                }
                case ESTUDOS -> {
                    Path2D livro = new Path2D.Double();
                    livro.moveTo(x + 3, y + 8);
                    livro.quadTo(x + 13, y + 3, x + 24, y + 9);
                    livro.quadTo(x + 35, y + 3, x + 45, y + 8);
                    livro.lineTo(x + 45, y + 41);
                    livro.quadTo(x + 35, y + 36, x + 24, y + 43);
                    livro.quadTo(x + 13, y + 36, x + 3, y + 41);
                    livro.closePath();
                    g2.draw(livro);
                    g2.drawLine(x + 24, y + 9, x + 24, y + 43);
                }
                case PROVAS -> {
                    g2.drawOval(x + 10, y + 5, 29, 29);
                    g2.drawLine(x + 15, y + 34, x + 11, y + 45);
                    g2.drawLine(x + 35, y + 34, x + 40, y + 45);
                    g2.drawLine(x + 11, y + 45, x + 20, y + 40);
                    g2.drawLine(x + 40, y + 45, x + 31, y + 40);
                    g2.drawOval(x + 19, y + 14, 11, 11);
                }
                default -> {
                }
            }
            g2.dispose();
        }
    }

    private JPanel criarColunas() {
        JPanel colunas = new JPanel(new GridBagLayout());
        colunas.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 0, 20);
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.weightx = 0.54;
        colunas.add(criarCompromissos(), c);
        JPanel direita = new JPanel(new GridBagLayout());
        direita.setOpaque(false);
        GridBagConstraints d = new GridBagConstraints();
        d.fill = GridBagConstraints.BOTH; d.weightx = 1; d.weighty = 0.55; d.gridy = 0;
        direita.add(criarProvas(), d);
        d.gridy = 1; d.weighty = 0.45; d.insets = new Insets(20, 0, 0, 0);
        direita.add(criarPrioridades(), d);
        c.weightx = 0.46; c.insets = new Insets(0, 0, 0, 0);
        colunas.add(direita, c);
        return colunas;
    }

    private JPanel criarCompromissos() {
        JPanel p = painel("Próximos Compromissos", "Ver todos",
                () -> abrir(() -> new TelaAtividades(idUsuario)));
        JPanel lista = new JPanel(); lista.setOpaque(false); lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        listaCompromissos = lista;
        lista.add(new JLabel("Carregando..."));
        p.add(rolagem(lista), BorderLayout.CENTER); return p;
    }

    private JPanel criarProvas() {
        JPanel p = painel("Próximas Provas", "Ver todas",
                () -> abrir(() -> new TelaProvas(idUsuario)));
        JPanel lista = new JPanel(); lista.setOpaque(false); lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        listaProvas = lista;
        lista.add(new JLabel("Carregando..."));
        p.add(rolagem(lista), BorderLayout.CENTER); return p;
    }

    private JScrollPane rolagem(JPanel lista) {
        JScrollPane scroll = new JScrollPane(lista,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel linhaProva(Object[] prova) {
        String disciplina = String.valueOf(prova[0]);
        LocalDate data = converterData(prova[1]);
        long dias = Math.max(0, java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), data));

        JPanel linha = new JPanel(new BorderLayout(16, 0));
        linha.setOpaque(false);
        linha.setBorder(new EmptyBorder(12, 8, 12, 8));

        JPanel icone = new JPanel(new GridBagLayout());
        icone.setOpaque(true);
        icone.setBackground(new Color(255, 241, 246));
        icone.setPreferredSize(new Dimension(78, 78));
        JLabel calendario = new JLabel(new IconeCalendario(new Color(220, 48, 85)));
        icone.add(calendario);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel titulo = new JLabel("Prova de " + disciplina);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setForeground(TEXTO);
        JLabel nomeDisciplina = new JLabel(disciplina);
        nomeDisciplina.setFont(new Font("SansSerif", Font.PLAIN, 15));
        nomeDisciplina.setForeground(SECUNDARIO);
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(8));
        textos.add(nomeDisciplina);

        JLabel dataLabel = new JLabel(data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dataLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        dataLabel.setForeground(TEXTO);

        JLabel diasLabel = new JLabel(dias + (dias == 1 ? " dia" : " dias"), SwingConstants.CENTER);
        diasLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        diasLabel.setForeground(new Color(220, 48, 85));
        diasLabel.setOpaque(true);
        diasLabel.setBackground(new Color(255, 241, 246));
        diasLabel.setBorder(new EmptyBorder(10, 18, 10, 18));

        JPanel direita = new JPanel(new BorderLayout(18, 0));
        direita.setOpaque(false);
        direita.add(dataLabel, BorderLayout.CENTER);
        direita.add(diasLabel, BorderLayout.EAST);

        linha.add(icone, BorderLayout.WEST);
        linha.add(textos, BorderLayout.CENTER);
        linha.add(direita, BorderLayout.EAST);
        return linha;
    }

    private JPanel linhaCompromisso(Object[] compromisso) {
        String titulo = String.valueOf(compromisso[1]);
        String subtitulo = String.valueOf(compromisso[0]);
        String tipo = String.valueOf(compromisso[3]);
        LocalDate data = converterData(compromisso[2]);
        String prioridade = Prioridade.calcular(data);
        Color cor = corPrioridade(prioridade);

        JPanel linha = new JPanel(new GridBagLayout());
        linha.setOpaque(false);
        linha.setBorder(new EmptyBorder(12, 8, 12, 8));

        JLabel ponto = new JLabel("●");
        ponto.setFont(new Font("SansSerif", Font.PLAIN, 21));
        ponto.setForeground(cor);
        ponto.setPreferredSize(new Dimension(28, 72));

        JPanel icone = new JPanel(new GridBagLayout());
        icone.setOpaque(true);
        icone.setBackground(new Color(249, 247, 255));
        icone.setPreferredSize(new Dimension(82, 82));
        if ("Atividade".equalsIgnoreCase(tipo)) {
            icone.add(new JLabel(new IconeClipboard(ROXO)));
        } else {
            icone.add(new JLabel(new IconeCalendario(ROXO)));
        }

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel nome = new JLabel(titulo);
        nome.setFont(new Font("SansSerif", Font.BOLD, 17));
        nome.setForeground(TEXTO);
        JLabel disciplina = new JLabel(subtitulo);
        disciplina.setFont(new Font("SansSerif", Font.PLAIN, 15));
        disciplina.setForeground(SECUNDARIO);
        textos.setMinimumSize(new Dimension(150, 82));
        textos.setPreferredSize(new Dimension(190, 82));
        textos.add(nome);
        textos.add(Box.createVerticalStrut(8));
        textos.add(disciplina);

        JPanel informacoes = new JPanel();
        informacoes.setOpaque(false);
        informacoes.setLayout(new BoxLayout(informacoes, BoxLayout.Y_AXIS));
        JLabel etiqueta = new JLabel(prioridade, SwingConstants.CENTER);
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 14));
        etiqueta.setForeground(cor);
        etiqueta.setOpaque(true);
        etiqueta.setBackground(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 24));
        etiqueta.setBorder(new EmptyBorder(7, 16, 7, 16));
        etiqueta.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JLabel dataLabel = new JLabel(data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dataLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        dataLabel.setForeground(SECUNDARIO);
        dataLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        informacoes.add(etiqueta);
        informacoes.add(Box.createVerticalStrut(7));
        informacoes.add(dataLabel);

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 0, 8);
        linha.add(ponto, c);
        c.insets = new Insets(0, 0, 0, 14);
        linha.add(icone, c);
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        linha.add(textos, c);
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(0, 12, 0, 0);
        linha.add(informacoes, c);
        return linha;
    }

    private LocalDate converterData(Object valor) {
        if (valor instanceof java.sql.Date data) {
            return data.toLocalDate();
        }
        return LocalDate.parse(String.valueOf(valor));
    }

    private Color corPrioridade(String prioridade) {
        return switch (prioridade) {
            case "ATRASADO" -> new Color(220, 48, 85);
            case "HOJE" -> new Color(30, 110, 220);
            case "URGENTE" -> new Color(235, 120, 20);
            case "PRÓXIMO" -> ROXO;
            default -> new Color(0, 145, 110);
        };
    }

    private JPanel painel(String titulo, String acao, Runnable aoClicar) {
        JPanel p = new RoundedPanel(18, Color.WHITE); p.setLayout(new BorderLayout()); p.setBorder(new EmptyBorder(20, 20, 18, 20));
        JPanel topo = new JPanel(new BorderLayout()); topo.setOpaque(false);
        JLabel t = new JLabel(titulo); t.setFont(new Font("SansSerif", Font.BOLD, 18)); t.setForeground(TEXTO);
        JLabel a = new JLabel("<html><u>" + acao + "</u></html>");
        a.setForeground(ROXO);
        a.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        a.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                aoClicar.run();
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                a.setForeground(new Color(47, 30, 145));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                a.setForeground(ROXO);
            }
        });
        topo.add(t, BorderLayout.WEST); topo.add(a, BorderLayout.EAST); p.add(topo, BorderLayout.NORTH); return p;
    }

    private JPanel criarPrioridades() {
        JPanel p = painel("Prioridades", "", () -> {});
        JPanel dados = new JPanel(new GridLayout(1, 5, 6, 0)); dados.setOpaque(false);
        String[] nomes = {"Atrasados", "Hoje", "Urgentes", "Próximos", "Futuros"};
        Color[] cores = {new Color(220, 48, 85), new Color(30, 110, 220), new Color(235, 120, 20), ROXO, new Color(0, 145, 110)};
        for (int i = 0; i < nomes.length; i++) {
            JLabel l = new JLabel("<html><center><font size='5' color='#" + hex(cores[i]) + "'>" +
                    totaisPrioridades[i] + "</font><br>" + nomes[i] + "</center></html>", SwingConstants.CENTER);
            l.setOpaque(true);
            l.setBackground(corSuave(cores[i]));
            valoresPrioridades[i] = l;
            dados.add(l);
        }
        p.add(dados, BorderLayout.CENTER); return p;
    }

    private void abrir(java.util.function.Supplier<JFrame> criador) {
        Navegacao.trocar(this, criador);
    }
    private String iniciais(String nome) { String[] p = nome.trim().split("\\s+"); return p.length > 1 ? ("" + p[0].charAt(0) + p[p.length - 1].charAt(0)).toUpperCase() : nome.substring(0, Math.min(2, nome.length())).toUpperCase(); }
    private String primeiroNome(String nome) {
        String texto = nome == null ? "" : nome.trim();
        int separador = texto.indexOf(' ');
        return separador > 0 ? texto.substring(0, separador) : texto;
    }
    private String hex(Color c) { return String.format("%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue()); }

    private void carregarDadosAsync() {
        new SwingWorker<Resumo, Void>() {
            @Override
            protected Resumo doInBackground() {
                DashboardDAO dao = new DashboardDAO();
                return new Resumo(
                        dao.buscarPerfil(idUsuario),
                        dao.contarDisciplinas(idUsuario),
                        dao.contarAtividadesPendentes(idUsuario),
                        dao.contarEstudosPendentes(idUsuario),
                        dao.contarPrioridades(idUsuario),
                        dao.buscarProximasProvas(idUsuario),
                        dao.buscarProximosCompromissos(idUsuario));
            }

            @Override
            protected void done() {
                try {
                    Resumo resumo = get();
                    if (!resumo.perfil[0].isBlank()) {
                        saudacaoPerfil.setText("Olá, " + primeiroNome(resumo.perfil[0]) + "! 👋");
                        nomePerfil.setText(resumo.perfil[0]);
                    }
                    if (!resumo.perfil[1].isBlank()) {
                        cursoPerfil.setText(resumo.perfil[1]);
                    }
                    valorDisciplinas.setText(String.valueOf(resumo.disciplinas));
                    valorAtividades.setText(String.valueOf(resumo.atividades));
                    valorEstudos.setText(String.valueOf(resumo.estudos));
                    valorProvas.setText(String.valueOf(resumo.provas.size()));
                    totaisPrioridades = resumo.prioridades;
                    atualizarPrioridades();
                    preencherCompromissos(resumo.compromissos);
                    preencherProvas(resumo.provas);
                } catch (Exception e) {
                    preencherCompromissos(List.of());
                    preencherProvas(List.of());
                }
            }
        }.execute();
    }

    private void atualizarPrioridades() {
        String[] nomes = {"Atrasados", "Hoje", "Urgentes", "Próximos", "Futuros"};
        Color[] cores = {new Color(220, 48, 85), new Color(30, 110, 220), new Color(235, 120, 20), ROXO, new Color(0, 145, 110)};
        for (int i = 0; i < valoresPrioridades.length; i++) {
            valoresPrioridades[i].setText("<html><center><font size='5' color='#" + hex(cores[i]) + "'>" +
                    totaisPrioridades[i] + "</font><br>" + nomes[i] + "</center></html>");
            valoresPrioridades[i].setBackground(corSuave(cores[i]));
        }
    }

    private Color corSuave(Color cor) {
        return new Color(
                Math.min(255, cor.getRed() + (255 - cor.getRed()) * 88 / 100),
                Math.min(255, cor.getGreen() + (255 - cor.getGreen()) * 88 / 100),
                Math.min(255, cor.getBlue() + (255 - cor.getBlue()) * 88 / 100));
    }

    private void preencherCompromissos(List<Object[]> dados) {
        listaCompromissos.removeAll();
        dados.stream()
                .sorted(Comparator.comparing(x -> converterDataSeguro(x[2]),
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .forEach(x -> listaCompromissos.add(linhaCompromisso(x)));
        if (dados.isEmpty()) listaCompromissos.add(new JLabel("Nenhum compromisso pendente."));
        listaCompromissos.revalidate(); listaCompromissos.repaint();
    }

    private void preencherProvas(List<Object[]> dados) {
        listaProvas.removeAll();
        dados.stream()
                .sorted(Comparator.comparing(x -> converterDataSeguro(x[1]),
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .forEach(x -> listaProvas.add(linhaProva(x)));
        if (dados.isEmpty()) listaProvas.add(new JLabel("Nenhuma prova próxima."));
        listaProvas.revalidate(); listaProvas.repaint();
    }

    private LocalDate converterDataSeguro(Object valor) {
        try {
            return converterData(valor);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private record Resumo(String[] perfil, int disciplinas, int atividades, int estudos, int[] prioridades,
                          List<Object[]> provas, List<Object[]> compromissos) {}

    private static class IconeCalendario implements Icon {
        private final Color cor;

        private IconeCalendario(Color cor) {
            this.cor = cor;
        }

        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(cor);
            g2.setStroke(new BasicStroke(2.2f));
            g2.drawRoundRect(x + 2, y + 4, 30, 28, 4, 4);
            g2.drawLine(x + 2, y + 12, x + 32, y + 12);
            g2.drawLine(x + 10, y + 1, x + 10, y + 8);
            g2.drawLine(x + 24, y + 1, x + 24, y + 8);
            g2.fillOval(x + 9, y + 17, 3, 3);
            g2.fillOval(x + 17, y + 17, 3, 3);
            g2.fillOval(x + 25, y + 17, 3, 3);
            g2.dispose();
        }
        @Override public int getIconWidth() { return 34; }
        @Override public int getIconHeight() { return 34; }
    }

    private static class IconeClipboard implements Icon {
        private final Color cor;

        private IconeClipboard(Color cor) {
            this.cor = cor;
        }

        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(cor);
            g2.setStroke(new BasicStroke(2.4f));
            g2.drawRoundRect(x + 5, y + 5, 25, 29, 3, 3);
            g2.drawRoundRect(x + 11, y + 1, 13, 8, 3, 3);
            g2.drawLine(x + 11, y + 19, x + 16, y + 24);
            g2.drawLine(x + 16, y + 24, x + 25, y + 14);
            g2.dispose();
        }

        @Override public int getIconWidth() { return 36; }
        @Override public int getIconHeight() { return 36; }
    }

    private static class RoundedPanel extends JPanel {
        private final int raio; private final Color fundo;
        RoundedPanel(int raio, Color fundo) { this.raio = raio; this.fundo = fundo; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) { Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); g2.setColor(fundo); g2.fillRoundRect(0, 0, getWidth(), getHeight(), raio, raio); g2.setColor(BORDA); g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, raio, raio); g2.dispose(); super.paintComponent(g); }
    }
}
