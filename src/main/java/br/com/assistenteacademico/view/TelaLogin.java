package br.com.assistenteacademico.view;

import br.com.assistenteacademico.Aluno;
import br.com.assistenteacademico.UsuarioDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class TelaLogin extends JFrame {

    private static final Color ROXO = new Color(111, 95, 211);
    private static final Color TEXTO = new Color(77, 84, 108);
    private static final Color PLACEHOLDER = new Color(154, 161, 183);
    private final HintField campoEmail = new HintField("seu@email.com");
    private final HintPasswordField campoSenha = new HintPasswordField("••••••••");
    private JButton botaoEntrar;

    public TelaLogin() {
        configurarJanela();

        JPanel raiz = new JPanel(new GridLayout(1, 2));
        raiz.setOpaque(false);
        raiz.add(new PainelArte());
        raiz.add(criarFormulario());
        setContentPane(raiz);
        if (!Navegacao.estaPreparando()) setVisible(true);
    }

    private void configurarJanela() {
        setTitle("Assistente Acadêmico | Login");
        setMinimumSize(new Dimension(700, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setBackground(Color.WHITE);
        setShape(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private JPanel criarFormulario() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(new EmptyBorder(18, 36, 18, 36));

        JPanel conteudo = new JPanel();
        conteudo.setOpaque(false);
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setPreferredSize(new Dimension(545, 690));
        conteudo.setMaximumSize(new Dimension(680, 690));
        conteudo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
        topo.setOpaque(false);
        topo.add(botaoJanela("—", false));
        topo.add(botaoJanela("×", true));
        painel.add(topo, BorderLayout.NORTH);

        JLabel marca = new JLabel("<html><b>Assistente</b><br><font color='#6F5FD3'>Acadêmico</font></html>");
        marca.setIcon(new PainelArte.CapeloIcon());
        marca.setIconTextGap(22);
        marca.setFont(new Font("SansSerif", Font.PLAIN, 42));
        marca.setForeground(new Color(49, 53, 69));
        marca.setAlignmentX(Component.CENTER_ALIGNMENT);
        conteudo.add(marca);
        conteudo.add(Box.createVerticalStrut(30));

        JLabel subtitulo = new JLabel("Faça login para continuar");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 25));
        subtitulo.setForeground(new Color(105, 112, 135));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        conteudo.add(subtitulo);
        conteudo.add(Box.createVerticalStrut(42));

        conteudo.add(rotulo("E-mail"));
        conteudo.add(Box.createVerticalStrut(12));
        conteudo.add(campo(campoEmail));
        conteudo.add(Box.createVerticalStrut(28));
        conteudo.add(rotulo("Senha"));
        conteudo.add(Box.createVerticalStrut(12));
        conteudo.add(campoSenha());
        conteudo.add(Box.createVerticalStrut(22));

        JLabel esqueci = link("Esqueceu sua senha?");
        esqueci.setFont(new Font("SansSerif", Font.PLAIN, 20));
        esqueci.setHorizontalAlignment(SwingConstants.RIGHT);
        esqueci.setAlignmentX(Component.CENTER_ALIGNMENT);
        esqueci.setMaximumSize(new Dimension(545, 28));
        conteudo.add(esqueci);
        conteudo.add(Box.createVerticalStrut(25));

        botaoEntrar = botao("Entrar", true);
        botaoEntrar.addActionListener(e -> fazerLogin());
        conteudo.add(botaoEntrar);
        conteudo.add(Box.createVerticalStrut(28));
        conteudo.add(divisor());
        conteudo.add(Box.createVerticalStrut(26));

        JLabel cadastro = new JLabel("Não tem uma conta? ");
        cadastro.setFont(new Font("SansSerif", Font.PLAIN, 21));
        cadastro.setForeground(PLACEHOLDER);
        JLabel linkCadastro = link("Cadastre-se");
        linkCadastro.setFont(new Font("SansSerif", Font.BOLD, 21));
        linkCadastro.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { abrirCadastro(); }
        });
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        rodape.setOpaque(false);
        rodape.add(cadastro);
        rodape.add(linkCadastro);
        conteudo.add(rodape);

        JPanel centralizador = new JPanel(new GridBagLayout());
        centralizador.setOpaque(false);
        centralizador.add(conteudo);
        painel.add(centralizador, BorderLayout.CENTER);
        campoSenha.addActionListener(e -> fazerLogin());
        return painel;
    }

    private JLabel rotulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("SansSerif", Font.PLAIN, 21));
        label.setForeground(TEXTO);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setPreferredSize(new Dimension(545, 28));
        label.setMaximumSize(new Dimension(680, 28));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JComponent campo(JTextField campo) {
        campo.setFont(new Font("SansSerif", Font.PLAIN, 18));
        campo.setForeground(TEXTO);
        campo.setBorder(new EmptyBorder(0, 56, 0, 16));
        campo.setOpaque(false);
        RoundedInput painel = new RoundedInput("person");
        painel.setLayout(new BorderLayout());
        painel.add(campo, BorderLayout.CENTER);
        painel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return painel;
    }

    private JComponent campoSenha() {
        campoSenha.setFont(new Font("SansSerif", Font.PLAIN, 18));
        campoSenha.setForeground(TEXTO);
        campoSenha.setBorder(new EmptyBorder(0, 56, 0, 12));
        campoSenha.setOpaque(false);
        RoundedInput painel = new RoundedInput("lock");
        painel.setLayout(new BorderLayout());
        painel.add(campoSenha, BorderLayout.CENTER);
        JButton olho = new JButton();
        olho.setBorder(null);
        olho.setContentAreaFilled(false);
        olho.setForeground(PLACEHOLDER);
        olho.setPreferredSize(new Dimension(48, 55));
        olho.setIcon(new EyeIcon());
        olho.addActionListener(e -> campoSenha.setEchoChar(campoSenha.getEchoChar() == 0 ? '•' : (char) 0));
        painel.add(olho, BorderLayout.EAST);
        painel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return painel;
    }

    private JButton botao(String texto, boolean destaque) {
        JButton botao = new JButton(texto);
        botao.setAlignmentX(Component.CENTER_ALIGNMENT);
        botao.setMaximumSize(new Dimension(680, 60));
        botao.setPreferredSize(new Dimension(545, 60));
        botao.setFont(new Font("SansSerif", Font.BOLD, 22));
        botao.setForeground(new Color(43, 39, 76));
        botao.setBackground(destaque ? ROXO : Color.WHITE);
        botao.setFocusPainted(false);
        botao.setBorder(new EmptyBorder(15, 20, 15, 20));
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return botao;
    }

    private JLabel link(String texto) {
        JLabel label = new JLabel("<html><u>" + texto + "</u></html>");
        label.setFont(new Font("SansSerif", Font.PLAIN, 20));
        label.setForeground(ROXO);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return label;
    }

    private JPanel divisor() {
        JPanel painel = new JPanel(new GridLayout(1, 3, 18, 0));
        painel.setOpaque(false);
        painel.setMaximumSize(new Dimension(680, 24));
        painel.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(new JSeparator());
        JLabel ou = new JLabel("ou", SwingConstants.CENTER);
        ou.setFont(new Font("SansSerif", Font.PLAIN, 18));
        ou.setForeground(TEXTO);
        painel.add(ou);
        painel.add(new JSeparator());
        return painel;
    }

    private JButton botaoJanela(String texto, boolean fechar) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("SansSerif", Font.PLAIN, fechar ? 34 : 25));
        botao.setForeground(new Color(133, 142, 164));
        botao.setBorder(null);
        botao.setContentAreaFilled(false);
        botao.setPreferredSize(new Dimension(26, 30));
        botao.addActionListener(e -> { if (fechar) dispose(); else setState(JFrame.ICONIFIED); });
        return botao;
    }

    private void fazerLogin() {
        String email = campoEmail.getText().trim();
        String senha = new String(campoSenha.getPassword());
        if (email.isBlank() || senha.isBlank()) {
            JOptionPane.showMessageDialog(this, "Informe o e-mail e a senha.", "Login", JOptionPane.WARNING_MESSAGE);
            return;
        }
        botaoEntrar.setEnabled(false);
        botaoEntrar.setText("Entrando...");

        SwingWorker<ResultadoLogin, Void> trabalhador = new SwingWorker<>() {
            @Override
            protected ResultadoLogin doInBackground() {
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                Aluno aluno = usuarioDAO.autenticar(email, senha);
                return new ResultadoLogin(aluno, usuarioDAO.getUltimoErro());
            }

            @Override
            protected void done() {
                botaoEntrar.setEnabled(true);
                botaoEntrar.setText("Entrar");
                try {
                    ResultadoLogin resultado = get();
                    if (resultado.aluno() == null) {
                        String mensagem = resultado.erro() == null
                                ? "E-mail ou senha incorretos."
                                : "Não foi possível conectar ao banco de dados.\n" + resultado.erro();
                        JOptionPane.showMessageDialog(TelaLogin.this, mensagem,
                                "Login", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    Navegacao.trocar(TelaLogin.this,
                            () -> new TelaDashboard(resultado.aluno().getId(), resultado.aluno().getNome()));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    JOptionPane.showMessageDialog(TelaLogin.this,
                            "O login foi interrompido. Tente novamente.", "Login",
                            JOptionPane.ERROR_MESSAGE);
                } catch (ExecutionException e) {
                    JOptionPane.showMessageDialog(TelaLogin.this,
                            "Não foi possível concluir o login: " + e.getCause().getMessage(),
                            "Login", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        trabalhador.execute();
    }

    private record ResultadoLogin(Aluno aluno, String erro) {
    }

    private void abrirCadastro() {
        Navegacao.trocar(this, TelaCadastro::new);
    }

    static class PainelArte extends JPanel {
        private final Image imagem;
        PainelArte() {
            URL url = TelaLogin.class.getResource("/br/com/assistenteacademico/view/login-art.png");
            imagem = url == null ? null : new ImageIcon(url).getImage();
        }

        static class CapeloIcon extends ImageIcon {
            CapeloIcon() {
                super(scaledImage());
            }

            private static Image scaledImage() {
                URL url = TelaLogin.class.getResource("/br/com/assistenteacademico/view/capelo-login.png");
                if (url == null) return new BufferedImage(128, 95, BufferedImage.TYPE_INT_ARGB);
                return new ImageIcon(url).getImage().getScaledInstance(128, 95, Image.SCALE_SMOOTH);
            }
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imagem == null) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.drawImage(imagem, 0, 0, getWidth(), getHeight(), this);
            g2.dispose();
        }
    }

    static class HintField extends JTextField {
        private final String hint;
        HintField(String hint) { this.hint = hint; }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && !hasFocus()) {
                g.setColor(PLACEHOLDER);
                g.setFont(getFont());
                g.drawString(hint, 56, getHeight() / 2 + 7);
            }
        }
    }

    static class HintPasswordField extends JPasswordField {
        private final String hint;
        HintPasswordField(String hint) { this.hint = hint; }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getPassword().length == 0 && !hasFocus()) {
                g.setColor(PLACEHOLDER);
                g.setFont(getFont());
                g.drawString(hint, 56, getHeight() / 2 + 7);
            }
        }
    }

    static class RoundedInput extends JPanel {
        private final String icone;
        RoundedInput(String icone) {
            this.icone = icone;
            setPreferredSize(new Dimension(545, 55));
            setMaximumSize(new Dimension(680, 55));
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            g2.setColor(new Color(222, 224, 232));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            g2.setColor(PLACEHOLDER);
            g2.setStroke(new BasicStroke(2.2f));
            if (icone.equals("lock")) {
                g2.drawRoundRect(20, 31, 17, 17, 3, 3);
                g2.drawArc(23, 20, 11, 18, 0, 180);
            } else {
                g2.drawOval(25, 20, 10, 10);
                g2.drawArc(20, 31, 20, 14, 0, 180);
            }
            g2.dispose();
            super.paintComponent(g);
        }

    }

    static class EyeIcon implements Icon {
        @Override public int getIconWidth() { return 25; }
        @Override public int getIconHeight() { return 18; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(PLACEHOLDER);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(x + 1, y + 3, 23, 13);
            g2.fillOval(x + 9, y + 7, 7, 7);
            g2.dispose();
        }
    }
}
