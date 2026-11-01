package br.com.assistenteacademico.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Path2D;
import java.net.URL;

final class MenuLateral {
    private static final Color ROXO = new Color(75, 55, 190);
    private static final Color BORDA = new Color(239, 238, 247);

    private MenuLateral() {
    }

    static JPanel criar(JFrame atual, int idUsuario, String ativo) {
        JPanel menu = new JPanel(new BorderLayout());
        menu.setPreferredSize(new Dimension(235, 0));
        menu.setBackground(Color.WHITE);
        menu.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDA));

        JPanel topo = new JPanel();
        topo.setBackground(new Color(250, 248, 255));
        topo.setBorder(new EmptyBorder(28, 18, 28, 12));
        topo.setLayout(new BoxLayout(topo, BoxLayout.X_AXIS));
        topo.add(new JLabel(new LogoIcon()));
        JLabel marca = new JLabel("<html><b><font color='#30245F'>Assistente</font></b><br><font color='#6F5FD3'>Acadêmico</font></html>");
        marca.setFont(new Font("SansSerif", Font.PLAIN, 24));
        marca.setBorder(new EmptyBorder(0, 10, 0, 0));
        topo.add(marca);
        menu.add(topo, BorderLayout.NORTH);

        JPanel itens = new JPanel();
        itens.setOpaque(false);
        itens.setBorder(new EmptyBorder(20, 20, 24, 16));
        itens.setLayout(new BoxLayout(itens, BoxLayout.Y_AXIS));
        adicionar(itens, atual, idUsuario, MenuIcon.DASHBOARD, "Dashboard", ativo);
        adicionar(itens, atual, idUsuario, MenuIcon.DISCIPLINAS, "Disciplinas", ativo);
        adicionar(itens, atual, idUsuario, MenuIcon.ATIVIDADES, "Atividades", ativo);
        adicionar(itens, atual, idUsuario, MenuIcon.PROVAS, "Provas", ativo);
        adicionar(itens, atual, idUsuario, MenuIcon.ESTUDOS, "Estudos", ativo);
        itens.add(Box.createVerticalGlue());
        adicionar(itens, atual, idUsuario, MenuIcon.SAIR, "Sair", ativo);
        menu.add(itens, BorderLayout.CENTER);
        return menu;
    }

    private static void adicionar(JPanel painel, JFrame atual, int idUsuario,
                                  int tipoIcone, String texto, String ativo) {
        boolean selecionado = texto.equals(ativo);
        JButton item = new NavButton(texto, selecionado);
        item.setHorizontalAlignment(SwingConstants.LEFT);
        item.setFont(new Font("SansSerif", selecionado ? Font.BOLD : Font.PLAIN, 15));
        item.setForeground("Sair".equals(texto) ? new Color(206, 48, 63) : selecionado ? ROXO : new Color(35, 42, 68));
        item.setBorder(new EmptyBorder(13, 14, 13, 12));
        item.setFocusPainted(false);
        item.setContentAreaFilled(false);
        item.setIcon(new MenuIcon(tipoIcone, item.getForeground()));
        item.setIconTextGap(14);
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, "Sair".equals(texto) ? 48 : 52));
        if (!selecionado) {
            item.addActionListener(e -> navegar(atual, idUsuario, texto));
        }
        painel.add(item);
        painel.add(Box.createVerticalStrut("Sair".equals(texto) ? 0 : 7));
    }

    private static void navegar(JFrame atual, int idUsuario, String destino) {
        switch (destino) {
            case "Dashboard" -> Navegacao.trocar(atual, () -> new TelaDashboard(idUsuario, ""));
            case "Disciplinas" -> Navegacao.trocar(atual, () -> new TelaDisciplinas(idUsuario));
            case "Atividades" -> Navegacao.trocar(atual, () -> new TelaAtividades(idUsuario));
            case "Provas" -> Navegacao.trocar(atual, () -> new TelaProvas(idUsuario));
            case "Estudos" -> Navegacao.trocar(atual, () -> new TelaEstudos(idUsuario));
            case "Sair" -> Navegacao.sair(atual);
            default -> {
            }
        }
    }

    private static final class NavButton extends JButton {
        private final boolean selecionado;

        private NavButton(String texto, boolean selecionado) {
            super(texto);
            this.selecionado = selecionado;
            setOpaque(false);
        }

        @Override protected void paintComponent(Graphics g) {
            if (selecionado) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(244, 241, 255));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    private static final class MenuIcon implements Icon {
        private static final int DASHBOARD = 1;
        private static final int DISCIPLINAS = 2;
        private static final int ATIVIDADES = 3;
        private static final int PROVAS = 4;
        private static final int ESTUDOS = 5;
        private static final int SAIR = 6;

        private final int tipo;
        private final Color cor;

        private MenuIcon(int tipo, Color cor) {
            this.tipo = tipo;
            this.cor = cor;
        }

        @Override public int getIconWidth() { return 26; }
        @Override public int getIconHeight() { return 26; }

        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(cor);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            switch (tipo) {
                case DASHBOARD -> {
                    Path2D casa = new Path2D.Double();
                    casa.moveTo(x + 4, y + 11); casa.lineTo(x + 13, y + 4);
                    casa.lineTo(x + 22, y + 11); casa.lineTo(x + 22, y + 22);
                    casa.lineTo(x + 4, y + 22); casa.closePath();
                    g2.draw(casa);
                    g2.drawLine(x + 11, y + 22, x + 11, y + 15);
                    g2.drawLine(x + 15, y + 22, x + 15, y + 15);
                }
                case DISCIPLINAS -> {
                    g2.drawRoundRect(x + 4, y + 5, 18, 17, 4, 4);
                    g2.drawLine(x + 13, y + 5, x + 13, y + 22);
                    g2.drawArc(x + 7, y + 8, 5, 5, 0, 180);
                    g2.drawArc(x + 14, y + 8, 5, 5, 0, 180);
                }
                case ATIVIDADES -> {
                    g2.drawRoundRect(x + 5, y + 5, 16, 18, 3, 3);
                    g2.drawRoundRect(x + 10, y + 3, 6, 5, 2, 2);
                    g2.drawLine(x + 9, y + 15, x + 12, y + 18);
                    g2.drawLine(x + 12, y + 18, x + 18, y + 11);
                }
                case PROVAS -> {
                    g2.drawRoundRect(x + 5, y + 4, 17, 19, 3, 3);
                    g2.drawLine(x + 9, y + 16, x + 18, y + 8);
                    g2.drawLine(x + 9, y + 19, x + 18, y + 11);
                }
                case ESTUDOS -> {
                    Path2D livro = new Path2D.Double();
                    livro.moveTo(x + 3, y + 6);
                    livro.quadTo(x + 8, y + 3, x + 13, y + 7);
                    livro.quadTo(x + 18, y + 3, x + 23, y + 6);
                    livro.lineTo(x + 23, y + 22);
                    livro.quadTo(x + 18, y + 19, x + 13, y + 23);
                    livro.quadTo(x + 8, y + 19, x + 3, y + 22);
                    livro.closePath();
                    g2.draw(livro);
                    g2.drawLine(x + 13, y + 7, x + 13, y + 23);
                }
                case SAIR -> {
                    g2.drawRoundRect(x + 4, y + 5, 12, 17, 2, 2);
                    g2.drawLine(x + 11, y + 13, x + 23, y + 13);
                    g2.drawLine(x + 18, y + 8, x + 23, y + 13);
                    g2.drawLine(x + 18, y + 18, x + 23, y + 13);
                }
                default -> {
                }
            }
            g2.dispose();
        }
    }

    private static final class LogoIcon implements Icon {
        private final ImageIcon imagem;

        private LogoIcon() {
            URL url = MenuLateral.class.getResource("/br/com/assistenteacademico/view/capelo-login.png");
            imagem = url == null ? null : new ImageIcon(url);
        }

        @Override public int getIconWidth() { return 62; }
        @Override public int getIconHeight() { return 48; }

        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            if (imagem != null) {
                g.drawImage(imagem.getImage(), x, y, getIconWidth(), getIconHeight(), c);
            }
        }
    }
}
