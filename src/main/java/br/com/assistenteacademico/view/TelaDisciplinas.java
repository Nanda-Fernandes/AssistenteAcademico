package br.com.assistenteacademico.view;

import br.com.assistenteacademico.DisciplinaDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class TelaDisciplinas extends JFrame {
    private static final Color ROXO = new Color(76, 55, 190);
    private static final Color TEXTO = new Color(25, 30, 49);
    private static final Color SECUNDARIO = new Color(91, 101, 139);
    private static final Color BORDA = new Color(231, 231, 243);
    private static final int ITENS_POR_PAGINA = 6;

    private final int idUsuario;
    private final DisciplinaDAO disciplinaDAO = new DisciplinaDAO();
    private final DefaultTableModel modelo;
    private final JTable tabela;
    private final JTextField campoNome = new JTextField();
    private final JTextField campoProfessor = new JTextField();
    private final JTextField campoCurso = new JTextField();
    private final JTextField campoSemestre = new JTextField();
    private final JTextField campoHorario = new JTextField();
    private final JTextField campoSala = new JTextField();
    private final JTextField campoObservacao = new JTextField();
    private final JTextField busca = new JTextField();
    private final JComboBox<String> filtroDisciplina = new JComboBox<>();
    private final JPanel painelPaginas = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
    private List<Object[]> disciplinas = new ArrayList<>();
    private int paginaAtual;

    public TelaDisciplinas(int idUsuario) {
        this.idUsuario = idUsuario;
        setTitle("Assistente Acadêmico | Disciplinas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setBackground(new Color(245, 245, 251));
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        modelo = new DefaultTableModel(new String[]{
                "ID", "Disciplina", "Professor", "Curso", "Semestre", "Horário", "Sala", "Observação", "Ações"
        }, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(modelo);
        configurarTabela();
        add(criarTela());
        carregarDisciplinas();
        if (!Navegacao.estaPreparando()) setVisible(true);
    }

    private JPanel criarTela() {
        RoundedPanel raiz = new RoundedPanel(18, Color.WHITE);
        raiz.setLayout(new BorderLayout());
        raiz.add(criarMenu(), BorderLayout.WEST);

        JPanel principal = new JPanel(new BorderLayout());
        principal.setBackground(Color.WHITE);
        principal.setBorder(new EmptyBorder(48, 40, 22, 40));
        principal.add(criarCabecalho(), BorderLayout.NORTH);
        JPanel centro = new JPanel(new BorderLayout(0, 24));
        centro.setOpaque(false);
        centro.setBorder(new EmptyBorder(46, 0, 0, 0));
        centro.add(criarFiltros(), BorderLayout.NORTH);
        centro.add(criarTabela(), BorderLayout.CENTER);
        principal.add(centro, BorderLayout.CENTER);
        principal.add(criarRodape(), BorderLayout.SOUTH);
        raiz.add(principal, BorderLayout.CENTER);

        JPanel margem = new JPanel(new BorderLayout());
        margem.setOpaque(false);
        margem.setBorder(new EmptyBorder(15, 15, 15, 15));
        margem.add(raiz, BorderLayout.CENTER);
        return margem;
    }

    private JPanel criarFiltros() {
        JPanel filtros = new JPanel(new BorderLayout(18, 0));
        filtros.setOpaque(false);

        filtroDisciplina.addItem("Todas as disciplinas");
        filtroDisciplina.setPreferredSize(new Dimension(300, 60));
        filtroDisciplina.setFont(new Font("SansSerif", Font.PLAIN, 15));
        filtroDisciplina.addActionListener(e -> atualizarTabela());

        busca.setFont(new Font("SansSerif", Font.PLAIN, 15));
        busca.setForeground(TEXTO);
        busca.setCaretColor(ROXO);
        busca.setBorder(new EmptyBorder(0, 8, 0, 12));
        busca.putClientProperty("JTextField.placeholderText", "Buscar disciplina...");
        busca.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { atualizarTabela(); }
            public void removeUpdate(DocumentEvent e) { atualizarTabela(); }
            public void changedUpdate(DocumentEvent e) { atualizarTabela(); }
        });

        JPanel buscaPainel = new JPanel(new BorderLayout());
        buscaPainel.setBackground(Color.WHITE);
        buscaPainel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 190, 215)),
                new EmptyBorder(0, 10, 0, 4)));
        buscaPainel.add(new JLabel(new LupaIcon(20, new Color(75, 75, 110))), BorderLayout.WEST);
        buscaPainel.add(busca, BorderLayout.CENTER);
        buscaPainel.setPreferredSize(new Dimension(350, 60));

        filtros.add(filtroDisciplina, BorderLayout.WEST);
        filtros.add(buscaPainel, BorderLayout.EAST);
        return filtros;
    }

    private JPanel criarMenu() {
        return MenuLateral.criar(this, idUsuario, "Disciplinas");
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        JPanel titulo = new JPanel();
        titulo.setOpaque(false);
        titulo.setLayout(new BoxLayout(titulo, BoxLayout.Y_AXIS));
        JLabel principal = new JLabel("Disciplinas");
        principal.setFont(new Font("SansSerif", Font.BOLD, 32));
        principal.setForeground(TEXTO);
        JLabel subtitulo = new JLabel("Gerencie suas disciplinas");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subtitulo.setForeground(SECUNDARIO);
        titulo.add(principal);
        titulo.add(Box.createVerticalStrut(12));
        titulo.add(subtitulo);
        cabecalho.add(titulo, BorderLayout.WEST);

        JButton nova = new EstiloVisual.BotaoAcao("+  Nova Disciplina");
        nova.addActionListener(e -> abrirEditor(-1));
        cabecalho.add(nova, BorderLayout.EAST);
        return cabecalho;
    }

    private JScrollPane criarTabela() {
        tabela.setShowGrid(true);
        tabela.setGridColor(new Color(242, 242, 248));
        tabela.setRowHeight(70);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setFillsViewportHeight(true);
        tabela.setSelectionBackground(new Color(242, 237, 255));
        tabela.setSelectionForeground(TEXTO);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = tabela.rowAtPoint(e.getPoint());
                int col = tabela.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 8) {
                    int id = ((Number) modelo.getValueAt(row, 0)).intValue();
                    int x = e.getX() - tabela.getCellRect(row, col, true).x;
                    if (x < 55) abrirEditor(id); else excluirPorId(id);
                }
            }
        });
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(BORDA));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private JPanel criarRodape() {
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setOpaque(false);
        painelPaginas.setBorder(new EmptyBorder(0, 0, 0, 0));
        painelPaginas.setOpaque(false);
        rodape.add(painelPaginas, BorderLayout.EAST);
        return rodape;
    }

    private void configurarTabela() {
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        tabela.getTableHeader().setForeground(ROXO);
        tabela.getTableHeader().setBackground(new Color(246, 242, 255));
        tabela.getTableHeader().setPreferredSize(new Dimension(0, 54));
        tabela.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(new EmptyBorder(8, 6, 8, 6));
            }

            @Override public Component getTableCellRendererComponent(JTable table, Object value,
                                                                      boolean selected, boolean focused,
                                                                      int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, selected, focused, row, column);
                label.setForeground(ROXO);
                label.setBackground(new Color(246, 242, 255));
                label.setFont(new Font("SansSerif", Font.BOLD, 14));
                return label;
            }
        });
        tabela.getColumnModel().getColumn(0).setMinWidth(0);
        tabela.getColumnModel().getColumn(0).setMaxWidth(0);
        tabela.getColumnModel().getColumn(7).setMinWidth(0);
        tabela.getColumnModel().getColumn(7).setMaxWidth(0);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(230);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(170);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(150);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(130);
        tabela.getColumnModel().getColumn(6).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(8).setPreferredWidth(90);
        tabela.setDefaultRenderer(Object.class, new DisciplinaRenderer());
        tabela.getColumnModel().getColumn(1).setCellRenderer(new MultilineRenderer(Font.BOLD));
        tabela.getColumnModel().getColumn(2).setCellRenderer(new MultilineRenderer(Font.PLAIN));
        tabela.getColumnModel().getColumn(3).setCellRenderer(new MultilineRenderer(Font.PLAIN));
        tabela.getColumnModel().getColumn(5).setCellRenderer(new MultilineRenderer(Font.PLAIN));
        tabela.getColumnModel().getColumn(6).setCellRenderer(new MultilineRenderer(Font.PLAIN));
        tabela.getColumnModel().getColumn(8).setCellRenderer(new ActionRenderer());
        tabela.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                SwingUtilities.invokeLater(() -> ajustarAlturaLinhas(1, 2, 3, 5, 6));
            }
        });
    }

    private void ajustarAlturaLinhas(int... colunas) {
        for (int linha = 0; linha < tabela.getRowCount(); linha++) {
            int altura = 70;
            for (int coluna : colunas) {
                TableCellRenderer renderer = tabela.getCellRenderer(linha, coluna);
                Component componente = renderer.getTableCellRendererComponent(tabela,
                        tabela.getValueAt(linha, coluna), false, false, linha, coluna);
                componente.setSize(Math.max(1, tabela.getColumnModel().getColumn(coluna).getWidth() - 24),
                        Integer.MAX_VALUE);
                altura = Math.max(altura, componente.getPreferredSize().height + 8);
            }
            tabela.setRowHeight(linha, altura);
        }
    }

    private void carregarDisciplinas() {
        disciplinas = disciplinaDAO.buscarDisciplinasPorUsuario(idUsuario);
        filtroDisciplina.removeAllItems();
        filtroDisciplina.addItem("Todas as disciplinas");
        for (Object[] disciplina : disciplinas) {
            filtroDisciplina.addItem(String.valueOf(disciplina[1]));
        }
        paginaAtual = Math.min(paginaAtual, Math.max(0, totalPaginas() - 1));
        atualizarTabela();
    }

    private void atualizarTabela() {
        modelo.setRowCount(0);
        String termo = busca.getText().trim().toLowerCase();
        String disciplinaSelecionada = String.valueOf(filtroDisciplina.getSelectedItem());
        List<Object[]> filtradas = new ArrayList<>();
        for (Object[] disciplina : disciplinas) {
            String texto = String.valueOf(disciplina[1]) + " " + String.valueOf(disciplina[2]) + " "
                    + String.valueOf(disciplina[3]) + " " + String.valueOf(disciplina[4]);
            if (!termo.isEmpty() && !texto.toLowerCase().contains(termo)) continue;
            if (!"Todas as disciplinas".equals(disciplinaSelecionada)
                    && !disciplinaSelecionada.equals(String.valueOf(disciplina[1]))) continue;
            filtradas.add(disciplina);
        }
        int paginas = Math.max(1, (filtradas.size() + ITENS_POR_PAGINA - 1) / ITENS_POR_PAGINA);
        paginaAtual = Math.min(paginaAtual, paginas - 1);
        int inicio = paginaAtual * ITENS_POR_PAGINA;
        int fim = Math.min(inicio + ITENS_POR_PAGINA, filtradas.size());
        for (int i = inicio; i < fim; i++) {
            Object[] d = filtradas.get(i);
            modelo.addRow(new Object[]{d[0], d[1], d[2], d[3], d[4], d[5], d[6], d[7], "✎    🗑"});
        }
        atualizarPaginacao(paginas);
        ajustarAlturaLinhas(1, 2, 3, 5, 6);
    }

    private int totalPaginas() {
        return Math.max(1, (disciplinas.size() + ITENS_POR_PAGINA - 1) / ITENS_POR_PAGINA);
    }

    private void atualizarPaginacao(int paginas) {
        painelPaginas.removeAll();
        if (paginas > 1) {
            JButton anterior = botaoPagina("‹", paginaAtual > 0);
            anterior.addActionListener(e -> { paginaAtual--; atualizarTabela(); });
            painelPaginas.add(anterior);
            for (int i = 0; i < paginas; i++) {
                int pagina = i;
                JButton botao = botaoPagina(String.valueOf(i + 1), true);
                botao.setForeground(i == paginaAtual ? ROXO : TEXTO);
                botao.setBackground(i == paginaAtual ? new Color(242, 237, 255) : Color.WHITE);
                botao.addActionListener(e -> { paginaAtual = pagina; atualizarTabela(); });
                painelPaginas.add(botao);
            }
            JButton proxima = botaoPagina("›", paginaAtual < paginas - 1);
            proxima.addActionListener(e -> { paginaAtual++; atualizarTabela(); });
            painelPaginas.add(proxima);
        }
        painelPaginas.setVisible(paginas > 1);
        painelPaginas.revalidate();
        painelPaginas.repaint();
    }

    private JButton botaoPagina(String texto, boolean habilitado) {
        JButton botao = new JButton(texto);
        botao.setPreferredSize(new Dimension(50, 50));
        botao.setFont(new Font("SansSerif", Font.PLAIN, 20));
        botao.setForeground(habilitado ? TEXTO : new Color(190, 190, 205));
        botao.setBackground(Color.WHITE);
        botao.setBorder(BorderFactory.createLineBorder(BORDA));
        botao.setFocusPainted(false);
        botao.setEnabled(habilitado);
        return botao;
    }

    private void abrirEditor(int id) {
        if (id >= 0) preencherCampos(id); else limparCampos();
        JDialog dialog = new JDialog(this, id >= 0 ? "Editar disciplina" : "Nova disciplina", true);
        dialog.setLayout(new BorderLayout(12, 12));
        dialog.add(criarFormulario(), BorderLayout.CENTER);
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelar = new JButton("Cancelar");
        JButton salvar = new JButton(id >= 0 ? "Salvar alterações" : "Cadastrar");
        cancelar.addActionListener(e -> dialog.dispose());
        salvar.addActionListener(e -> {
            boolean concluido = id >= 0 ? editar(id) : cadastrar();
            if (concluido && dialog.isDisplayable()) dialog.dispose();
        });
        botoes.add(cancelar);
        botoes.add(salvar);
        dialog.add(botoes, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setSize(620, 390);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JPanel criarFormulario() {
        JPanel painel = new JPanel(new GridLayout(4, 4, 10, 10));
        painel.setBorder(new EmptyBorder(18, 18, 18, 18));
        adicionarCampo(painel, "Nome:*", campoNome);
        adicionarCampo(painel, "Professor:*", campoProfessor);
        adicionarCampo(painel, "Curso:*", campoCurso);
        adicionarCampo(painel, "Semestre:*", campoSemestre);
        adicionarCampo(painel, "Horário:*", campoHorario);
        adicionarCampo(painel, "Sala:*", campoSala);
        adicionarCampo(painel, "Observação:", campoObservacao);
        return painel;
    }

    private void adicionarCampo(JPanel painel, String rotulo, JTextField campo) {
        painel.add(new JLabel(rotulo));
        painel.add(campo);
    }

    private void preencherCampos(int id) {
        for (Object[] d : disciplinas) {
            if (((Number) d[0]).intValue() == id) {
                campoNome.setText(String.valueOf(d[1]));
                campoProfessor.setText(String.valueOf(d[2]));
                campoCurso.setText(String.valueOf(d[3]));
                campoSemestre.setText(String.valueOf(d[4]));
                campoHorario.setText(String.valueOf(d[5]));
                campoSala.setText(String.valueOf(d[6]));
                campoObservacao.setText(d[7] == null ? "" : String.valueOf(d[7]));
                return;
            }
        }
    }

    private boolean camposValidos() {
        if (campoNome.getText().trim().isBlank()) return aviso("Informe o nome da disciplina.");
        if (campoProfessor.getText().trim().isBlank()) return aviso("Informe o professor.");
        if (campoCurso.getText().trim().isBlank()) return aviso("Informe o curso.");
        if (campoSemestre.getText().trim().isBlank()) return aviso("Informe o semestre.");
        if (campoHorario.getText().trim().isBlank()) return aviso("Informe o horário.");
        if (campoSala.getText().trim().isBlank()) return aviso("Informe a sala.");
        return true;
    }

    private boolean aviso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Disciplinas", JOptionPane.WARNING_MESSAGE);
        return false;
    }

    private boolean cadastrar() {
        if (!camposValidos()) return false;
        if (disciplinaDAO.cadastrarDisciplina(idUsuario, campoNome.getText().trim(), campoProfessor.getText().trim(),
                campoCurso.getText().trim(), campoSemestre.getText().trim(), campoHorario.getText().trim(),
                campoSala.getText().trim(), campoObservacao.getText().trim())) {
            limparCampos();
            carregarDisciplinas();
            return true;
        } else erro("Não foi possível cadastrar a disciplina.");
        return false;
    }

    private boolean editar(int id) {
        if (!camposValidos()) return false;
        if (disciplinaDAO.editarDisciplina(id, idUsuario, campoNome.getText().trim(), campoProfessor.getText().trim(),
                campoCurso.getText().trim(), campoSemestre.getText().trim(), campoHorario.getText().trim(),
                campoSala.getText().trim(), campoObservacao.getText().trim())) {
            limparCampos();
            carregarDisciplinas();
            return true;
        } else erro("Não foi possível atualizar a disciplina.");
        return false;
    }

    private void excluirPorId(int id) {
        if (disciplinaDAO.possuiRegistrosRelacionados(id, idUsuario)) {
            aviso("Esta disciplina possui atividades, provas ou estudos vinculados e não pode ser excluída.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Deseja excluir esta disciplina?", "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION
                && disciplinaDAO.excluirDisciplina(id, idUsuario)) {
            carregarDisciplinas();
        }
    }

    private void limparCampos() {
        campoNome.setText("");
        campoProfessor.setText("");
        campoCurso.setText("");
        campoSemestre.setText("");
        campoHorario.setText("");
        campoSala.setText("");
        campoObservacao.setText("");
    }

    private void erro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }


    private static class LupaIcon implements Icon {
        private final int tamanho;
        private final Color cor;

        LupaIcon(int tamanho, Color cor) {
            this.tamanho = tamanho;
            this.cor = cor;
        }

        @Override public int getIconWidth() { return tamanho; }
        @Override public int getIconHeight() { return tamanho; }

        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(cor); g2.setStroke(new BasicStroke(2.3f));
            int d = tamanho - 8; g2.drawOval(x + 2, y + 2, d, d);
            g2.drawLine(x + d - 1, y + d - 1, x + tamanho - 2, y + tamanho - 2);
            g2.dispose();
        }
    }

    private static class DisciplinaRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean selecionada,
                                                                  boolean focada, int linha, int coluna) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    tabela, valor, selecionada, focada, linha, coluna);
            label.setBorder(new EmptyBorder(8, 14, 8, 14));
            label.setForeground(selecionada ? tabela.getSelectionForeground() : TEXTO);
            label.setBackground(selecionada ? tabela.getSelectionBackground()
                    : linha % 2 == 0 ? Color.WHITE : new Color(249, 249, 253));
            label.setOpaque(true);
            label.setFont(new Font("SansSerif", coluna == 1 ? Font.BOLD : Font.PLAIN, 14));
            label.setHorizontalAlignment(coluna >= 3 ? SwingConstants.CENTER : SwingConstants.LEFT);
            label.setVerticalAlignment(SwingConstants.CENTER);
            return label;
        }
    }

    private static class MultilineRenderer extends JTextArea implements TableCellRenderer {
        MultilineRenderer(int style) {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
            setBorder(new EmptyBorder(8, 12, 8, 12));
            setFont(new Font("SansSerif", style, 14));
        }

        @Override public Component getTableCellRendererComponent(JTable table, Object value,
                                                                  boolean selected, boolean focused,
                                                                  int row, int column) {
            setText(value == null ? "" : String.valueOf(value));
            setForeground(selected ? table.getSelectionForeground() : TEXTO);
            setBackground(selected ? table.getSelectionBackground()
                    : row % 2 == 0 ? Color.WHITE : new Color(249, 249, 253));
            return this;
        }
    }

    private static class ActionRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean selecionada,
                                                                  boolean focada, int linha, int coluna) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    tabela, valor, selecionada, focada, linha, coluna);
            label.setText("");
            label.setIcon(new EstiloVisual.AcoesIcon());
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBorder(new EmptyBorder(0, 0, 0, 0));
            return label;
        }
    }

    private static class RoundedPanel extends JPanel {
        private final int raio;
        private final Color fundo;

        RoundedPanel(int raio, Color fundo) {
            this.raio = raio;
            this.fundo = fundo;
            setOpaque(false);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fundo);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, raio, raio);
            g2.setColor(BORDA);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, raio, raio);
            g2.dispose();
            super.paintComponent(g);
        }
    }

}
