package br.com.assistenteacademico.view;

import br.com.assistenteacademico.UsuarioDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TelaCadastro extends JFrame {
    private static final Color ROXO = new Color(111, 95, 211);
    private static final Color TEXTO = new Color(77, 84, 108);
    private static final Color PLACEHOLDER = new Color(154, 161, 183);

    private final TelaLogin.HintField campoNome = new TelaLogin.HintField("Seu nome completo");
    private final TelaLogin.HintField campoEmail = new TelaLogin.HintField("seu@email.com");
    private final TelaLogin.HintPasswordField campoSenha = new TelaLogin.HintPasswordField("••••••••");
    private final TelaLogin.HintField campoCurso = new TelaLogin.HintField("Ex.: Ciência da Computação");

    public TelaCadastro() {
        configurarJanela();

        JPanel raiz = new JPanel(new GridLayout(1, 2));
        raiz.setOpaque(false);
        raiz.add(new TelaLogin.PainelArte());
        raiz.add(criarFormulario());
        setContentPane(raiz);
        if (!Navegacao.estaPreparando()) setVisible(true);
    }

    private void configurarJanela() {
        setTitle("Assistente Acadêmico | Cadastro");
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

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
        topo.setOpaque(false);
        topo.add(botaoJanela("—", false));
        topo.add(botaoJanela("×", true));
        painel.add(topo, BorderLayout.NORTH);

        JPanel conteudo = new JPanel();
        conteudo.setOpaque(false);
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setPreferredSize(new Dimension(545, 930));
        conteudo.setMinimumSize(new Dimension(545, 930));
        conteudo.setMaximumSize(new Dimension(680, 1100));
        conteudo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel marca = new JLabel("<html><b>Assistente</b><br><font color='#6F5FD3'>Acadêmico</font></html>");
        marca.setIcon(new TelaLogin.PainelArte.CapeloIcon());
        marca.setIconTextGap(22);
        marca.setFont(new Font("SansSerif", Font.PLAIN, 42));
        marca.setForeground(new Color(49, 53, 69));
        marca.setAlignmentX(Component.CENTER_ALIGNMENT);
        conteudo.add(marca);
        conteudo.add(Box.createVerticalStrut(30));

        JLabel subtitulo = new JLabel("Crie sua conta para começar");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 25));
        subtitulo.setForeground(new Color(105, 112, 135));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        conteudo.add(subtitulo);
        conteudo.add(Box.createVerticalStrut(32));

        conteudo.add(rotulo("Nome completo"));
        conteudo.add(Box.createVerticalStrut(12));
        conteudo.add(campo(campoNome, "person"));
        conteudo.add(Box.createVerticalStrut(20));
        conteudo.add(rotulo("E-mail"));
        conteudo.add(Box.createVerticalStrut(12));
        conteudo.add(campo(campoEmail, "person"));
        conteudo.add(Box.createVerticalStrut(20));
        conteudo.add(rotulo("Senha"));
        conteudo.add(Box.createVerticalStrut(12));
        conteudo.add(campoSenha());
        conteudo.add(Box.createVerticalStrut(20));
        conteudo.add(rotulo("Curso"));
        conteudo.add(Box.createVerticalStrut(12));
        conteudo.add(campo(campoCurso, "person"));
        conteudo.add(Box.createVerticalStrut(24));

        JButton cadastrar = botao("Cadastrar", true);
        cadastrar.addActionListener(e -> cadastrar());
        conteudo.add(cadastrar);
        conteudo.add(Box.createVerticalStrut(28));
        conteudo.add(divisor());
        conteudo.add(Box.createVerticalStrut(26));

        JLabel possuiConta = new JLabel("Já tem uma conta? ");
        possuiConta.setFont(new Font("SansSerif", Font.PLAIN, 21));
        possuiConta.setForeground(PLACEHOLDER);
        JLabel voltar = link("Voltar para login");
        voltar.setFont(new Font("SansSerif", Font.BOLD, 21));
        voltar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Navegacao.trocar(TelaCadastro.this, TelaLogin::new);
            }
        });
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        rodape.setOpaque(false);
        rodape.add(possuiConta);
        rodape.add(voltar);
        conteudo.add(rodape);

        JPanel areaConteudo = new JPanel(new BorderLayout());
        areaConteudo.setOpaque(false);
        areaConteudo.setBorder(new EmptyBorder(0, 28, 28, 28));
        areaConteudo.add(conteudo, BorderLayout.NORTH);

        JScrollPane rolagem = new JScrollPane(areaConteudo,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        rolagem.setBorder(null);
        rolagem.setOpaque(false);
        rolagem.getViewport().setOpaque(false);
        rolagem.getViewport().setBorder(null);
        rolagem.getVerticalScrollBar().setUnitIncrement(16);
        rolagem.getVerticalScrollBar().setPreferredSize(new Dimension(14, 0));
        rolagem.getVerticalScrollBar().setOpaque(false);
        painel.add(rolagem, BorderLayout.CENTER);
        campoSenha.addActionListener(e -> cadastrar());
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

    private JComponent campo(JTextField campo, String icone) {
        campo.setFont(new Font("SansSerif", Font.PLAIN, 18));
        campo.setForeground(TEXTO);
        campo.setCaretColor(ROXO);
        campo.setBorder(new EmptyBorder(0, 56, 0, 16));
        campo.setOpaque(false);
        TelaLogin.RoundedInput painel = new TelaLogin.RoundedInput(icone);
        painel.setLayout(new BorderLayout());
        painel.add(campo, BorderLayout.CENTER);
        painel.setPreferredSize(new Dimension(545, 70));
        painel.setMinimumSize(new Dimension(545, 70));
        painel.setMaximumSize(new Dimension(680, 70));
        painel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return painel;
    }

    private JComponent campoSenha() {
        campoSenha.setFont(new Font("SansSerif", Font.PLAIN, 18));
        campoSenha.setForeground(TEXTO);
        campoSenha.setCaretColor(ROXO);
        campoSenha.setBorder(new EmptyBorder(0, 56, 0, 12));
        campoSenha.setOpaque(false);
        TelaLogin.RoundedInput painel = new TelaLogin.RoundedInput("lock");
        painel.setLayout(new BorderLayout());
        painel.add(campoSenha, BorderLayout.CENTER);
        JButton olho = new JButton();
        olho.setBorder(null);
        olho.setContentAreaFilled(false);
        olho.setForeground(PLACEHOLDER);
        olho.setPreferredSize(new Dimension(48, 55));
        olho.setIcon(new TelaLogin.EyeIcon());
        olho.addActionListener(e -> campoSenha.setEchoChar(campoSenha.getEchoChar() == 0 ? '•' : (char) 0));
        painel.add(olho, BorderLayout.EAST);
        painel.setPreferredSize(new Dimension(545, 70));
        painel.setMinimumSize(new Dimension(545, 70));
        painel.setMaximumSize(new Dimension(680, 70));
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
        botao.addActionListener(e -> {
            if (fechar) dispose();
            else setState(JFrame.ICONIFIED);
        });
        return botao;
    }

    private void cadastrar() {
        String nome = campoNome.getText().trim();
        String email = campoEmail.getText().trim();
        String senha = new String(campoSenha.getPassword());
        String curso = campoCurso.getText().trim();
        if (nome.isBlank() || email.isBlank() || senha.isBlank() || curso.isBlank()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos obrigatórios.", "Cadastro", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            JOptionPane.showMessageDialog(this, "Informe um e-mail válido.", "Cadastro", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (senha.length() < 6) {
            JOptionPane.showMessageDialog(this, "A senha deve ter pelo menos 6 caracteres.", "Cadastro", JOptionPane.WARNING_MESSAGE);
            return;
        }
        UsuarioDAO dao = new UsuarioDAO();
        if (dao.emailJaCadastrado(email)) {
            JOptionPane.showMessageDialog(this, "Já existe uma conta cadastrada com este e-mail.", "Cadastro", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!dao.cadastrarUsuario(nome, email, senha, curso)) {
            JOptionPane.showMessageDialog(this, "Não foi possível realizar o cadastro. Tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Cadastro realizado com sucesso!", "Cadastro", JOptionPane.INFORMATION_MESSAGE);
        Navegacao.trocar(this, TelaLogin::new);
    }
}
