package br.com.assistenteacademico.view;

import br.com.assistenteacademico.DisciplinaDAO;
import br.com.assistenteacademico.EstudoDAO;

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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TelaEstudos extends JFrame {
    private static final Color ROXO = new Color(76, 55, 190);
    private static final Color TEXTO = new Color(25, 30, 49);
    private static final Color SECUNDARIO = new Color(91, 101, 139);
    private static final Color BORDA = new Color(231, 231, 243);
    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final int idUsuario;
    private final EstudoDAO estudoDAO = new EstudoDAO();
    private final DefaultTableModel modelo;
    private final JTable tabela;
    private final JComboBox<DisciplinaItem> campoDisciplina = new JComboBox<>();
    private final JTextField campoAssunto = new JTextField();
    private final JTextField campoData = new JTextField();
    private final JTextField campoHorario = new JTextField();
    private final JTextField campoObservacao = new JTextField();
    private final JComboBox<String> campoStatus = new JComboBox<>(
            new String[]{"Não iniciado", "Em andamento", "Concluído"});
    private final JTextField busca = new JTextField();
    private final JComboBox<String> filtroDisciplina = new JComboBox<>(new String[]{"Todos os estudos"});
    private final JComboBox<String> filtroStatus = new JComboBox<>(
            new String[]{"Todos os status", "Não iniciado", "Em andamento", "Concluído"});
    private List<Object[]> estudos = new ArrayList<>();

    public TelaEstudos(int idUsuario) {
        this.idUsuario = idUsuario;
        setTitle("Assistente Acadêmico | Estudos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setBackground(new Color(245, 245, 251));
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        modelo = new DefaultTableModel(new String[]{
                "ID", "ID disciplina", "Estudo", "Disciplina", "Data", "Horário",
                "Status", "Prioridade", "Ações"
        }, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(modelo);
        configurarTabela();
        carregarDisciplinas();
        add(criarTela());
        carregarEstudos();
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
        raiz.add(principal, BorderLayout.CENTER);

        JPanel margem = new JPanel(new BorderLayout());
        margem.setOpaque(false);
        margem.setBorder(new EmptyBorder(15, 15, 15, 15));
        margem.add(raiz, BorderLayout.CENTER);
        return margem;
    }

    private JPanel criarMenu() {
        return MenuLateral.criar(this, idUsuario, "Estudos");
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        JPanel titulo = new JPanel();
        titulo.setOpaque(false);
        titulo.setLayout(new BoxLayout(titulo, BoxLayout.Y_AXIS));
        JLabel principal = new JLabel("Estudos");
        principal.setFont(new Font("SansSerif", Font.BOLD, 32));
        principal.setForeground(TEXTO);
        JLabel subtitulo = new JLabel("Organize sua rotina de estudos");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subtitulo.setForeground(SECUNDARIO);
        titulo.add(principal);
        titulo.add(Box.createVerticalStrut(12));
        titulo.add(subtitulo);
        cabecalho.add(titulo, BorderLayout.WEST);

        JButton novo = new EstiloVisual.BotaoAcao("+  Novo Estudo");
        novo.addActionListener(e -> abrirEditor(-1));
        cabecalho.add(novo, BorderLayout.EAST);
        return cabecalho;
    }

    private JPanel criarFiltros() {
        JPanel filtros = new JPanel(new BorderLayout(18, 0));
        filtros.setOpaque(false);
        filtroDisciplina.setPreferredSize(new Dimension(230, 48));
        filtroDisciplina.setFont(new Font("SansSerif", Font.PLAIN, 15));
        filtroDisciplina.addActionListener(e -> aplicarFiltros());
        filtroStatus.setPreferredSize(new Dimension(180, 48));
        filtroStatus.setFont(new Font("SansSerif", Font.PLAIN, 15));
        filtroStatus.addActionListener(e -> aplicarFiltros());
        busca.setFont(new Font("SansSerif", Font.PLAIN, 15));
        busca.setForeground(TEXTO);
        busca.setCaretColor(ROXO);
        busca.setBorder(new EmptyBorder(0, 8, 0, 12));
        busca.putClientProperty("JTextField.placeholderText", "Buscar estudo...");
        busca.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { aplicarFiltros(); }
            public void removeUpdate(DocumentEvent e) { aplicarFiltros(); }
            public void changedUpdate(DocumentEvent e) { aplicarFiltros(); }
        });
        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        esquerda.setOpaque(false);
        esquerda.add(filtroDisciplina);
        esquerda.add(filtroStatus);
        JPanel buscaPainel = new JPanel(new BorderLayout());
        buscaPainel.setBackground(Color.WHITE);
        buscaPainel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 190, 215)),
                new EmptyBorder(0, 10, 0, 4)));
        buscaPainel.add(new JLabel(new LupaIcon(20, new Color(75, 75, 110))), BorderLayout.WEST);
        buscaPainel.add(busca, BorderLayout.CENTER);
        buscaPainel.setPreferredSize(new Dimension(280, 48));
        filtros.add(esquerda, BorderLayout.WEST);
        filtros.add(buscaPainel, BorderLayout.EAST);
        return filtros;
    }

    private JScrollPane criarTabela() {
        tabela.setShowGrid(true);
        tabela.setGridColor(new Color(242, 242, 248));
        tabela.setRowHeight(70);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        tabela.setFillsViewportHeight(true);
        tabela.setSelectionBackground(new Color(242, 237, 255));
        tabela.setSelectionForeground(TEXTO);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = tabela.rowAtPoint(e.getPoint());
                int col = tabela.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 8) {
                    tabela.setRowSelectionInterval(row, row);
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
        ocultarColuna(0);
        ocultarColuna(1);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(240);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(170);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(130);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(110);
        tabela.getColumnModel().getColumn(6).setPreferredWidth(140);
        tabela.getColumnModel().getColumn(7).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(8).setPreferredWidth(90);
        tabela.setDefaultRenderer(Object.class, new EstudoRenderer());
        tabela.getColumnModel().getColumn(2).setCellRenderer(new MultilineRenderer());
        tabela.getColumnModel().getColumn(3).setCellRenderer(new MultilineRenderer());
        tabela.getColumnModel().getColumn(6).setCellRenderer(new PillRenderer());
        tabela.getColumnModel().getColumn(7).setCellRenderer(new PillRenderer());
        tabela.getColumnModel().getColumn(8).setCellRenderer(new ActionRenderer());
        tabela.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                SwingUtilities.invokeLater(() -> ajustarAlturaLinhas(2, 3));
            }
        });
    }

    private void ocultarColuna(int indice) {
        tabela.getColumnModel().getColumn(indice).setMinWidth(0);
        tabela.getColumnModel().getColumn(indice).setMaxWidth(0);
        tabela.getColumnModel().getColumn(indice).setPreferredWidth(0);
    }

    private void carregarDisciplinas() {
        for (Object[] d : new DisciplinaDAO().buscarDisciplinasPorUsuario(idUsuario)) {
            DisciplinaItem item = new DisciplinaItem(((Number) d[0]).intValue(), String.valueOf(d[1]));
            campoDisciplina.addItem(item);
            filtroDisciplina.addItem(item.toString());
        }
    }

    private void carregarEstudos() {
        estudos = estudoDAO.buscarEstudosPorUsuario(idUsuario);
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        modelo.setRowCount(0);
        String termo = busca.getText().trim().toLowerCase();
        String disciplina = String.valueOf(filtroDisciplina.getSelectedItem());
        String status = String.valueOf(filtroStatus.getSelectedItem());
        List<Object[]> filtrados = new ArrayList<>();
        for (Object[] estudo : estudos) {
            String texto = (estudo[2] + " " + estudo[3] + " " + estudo[6] + " " + estudo[7]).toLowerCase();
            if (!termo.isEmpty() && !texto.contains(termo)) continue;
            if (!"Todos os estudos".equals(disciplina) && !disciplina.equals(String.valueOf(estudo[2]))) continue;
            if (!"Todos os status".equals(status) && !status.equals(String.valueOf(estudo[7]))) continue;
            filtrados.add(estudo);
        }
        for (Object[] e : filtrados) {
            LocalDate data = data(e[4]);
            String statusValor = String.valueOf(e[7]);
            modelo.addRow(new Object[]{e[0], e[1], e[3] + "\n" + e[6], e[2],
                    data == null ? String.valueOf(e[4]) : data.format(DATA_BR), e[5],
                    statusValor, prioridade(statusValor, data), "Ações"});
        }
        ajustarAlturaLinhas(2, 3);
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

    private void abrirEditor(int id) {
        if (id >= 0) preencherCampos(id); else limparCampos();
        JDialog dialog = new JDialog(this, id >= 0 ? "Editar estudo" : "Novo estudo", true);
        dialog.setLayout(new BorderLayout(12, 12));
        dialog.add(criarFormulario(), BorderLayout.CENTER);
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelar = new JButton("Cancelar");
        JButton salvar = new JButton(id >= 0 ? "Salvar alterações" : "Cadastrar");
        cancelar.addActionListener(e -> dialog.dispose());
        salvar.addActionListener(e -> {
            boolean concluido = id >= 0 ? editar(id) : cadastrar();
            if (concluido) dialog.dispose();
        });
        botoes.add(cancelar);
        botoes.add(salvar);
        dialog.add(botoes, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setSize(650, 430);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JPanel criarFormulario() {
        JPanel painel = new JPanel(new GridLayout(3, 4, 10, 10));
        painel.setBorder(new EmptyBorder(18, 18, 18, 18));
        adicionarCampo(painel, "Disciplina:*", campoDisciplina);
        adicionarCampo(painel, "O que estudar:*", campoAssunto);
        adicionarCampo(painel, "Data (AAAA-MM-DD):*", campoData);
        adicionarCampo(painel, "Horário (HH:MM):*", campoHorario);
        adicionarCampo(painel, "Observação:", campoObservacao);
        adicionarCampo(painel, "Status:*", campoStatus);
        return painel;
    }

    private void adicionarCampo(JPanel painel, String rotulo, JComponent campo) {
        painel.add(new JLabel(rotulo));
        painel.add(campo);
    }

    private boolean cadastrar() {
        if (!camposValidos()) return false;
        DisciplinaItem disciplina = (DisciplinaItem) campoDisciplina.getSelectedItem();
        boolean ok = estudoDAO.cadastrarEstudo(idUsuario, disciplina.getId(), campoAssunto.getText().trim(),
                campoData.getText().trim(), campoHorario.getText().trim(), campoObservacao.getText().trim(),
                String.valueOf(campoStatus.getSelectedItem()));
        if (ok) { limparCampos(); carregarEstudos(); } else erro("Não foi possível cadastrar o estudo.");
        return ok;
    }

    private boolean editar(int id) {
        if (!camposValidos()) return false;
        DisciplinaItem disciplina = (DisciplinaItem) campoDisciplina.getSelectedItem();
        boolean ok = estudoDAO.editarEstudo(id, idUsuario, disciplina.getId(), campoAssunto.getText().trim(),
                campoData.getText().trim(), campoHorario.getText().trim(), campoObservacao.getText().trim(),
                String.valueOf(campoStatus.getSelectedItem()));
        if (ok) { limparCampos(); carregarEstudos(); } else erro("Não foi possível atualizar o estudo.");
        return ok;
    }

    private boolean camposValidos() {
        if (campoDisciplina.getSelectedItem() == null || campoAssunto.getText().trim().isBlank()
                || campoData.getText().trim().isBlank() || campoHorario.getText().trim().isBlank()) {
            aviso("Preencha todos os campos obrigatórios.");
            return false;
        }
        try {
            LocalDate.parse(campoData.getText().trim());
            java.time.LocalTime.parse(campoHorario.getText().trim());
            return true;
        } catch (DateTimeParseException e) {
            aviso("Informe data AAAA-MM-DD e horário HH:MM.");
            return false;
        }
    }

    private void preencherCampos(int id) {
        for (Object[] e : estudos) {
            if (((Number) e[0]).intValue() == id) {
                selecionarDisciplina(((Number) e[1]).intValue());
                campoAssunto.setText(String.valueOf(e[3]));
                campoData.setText(String.valueOf(e[4]));
                campoHorario.setText(String.valueOf(e[5]));
                campoObservacao.setText(e[6] == null ? "" : String.valueOf(e[6]));
                campoStatus.setSelectedItem(e[7]);
                return;
            }
        }
    }

    private void selecionarDisciplina(int id) {
        for (int i = 0; i < campoDisciplina.getItemCount(); i++) {
            if (campoDisciplina.getItemAt(i).getId() == id) {
                campoDisciplina.setSelectedIndex(i);
                return;
            }
        }
    }

    private void excluirPorId(int id) {
        if (JOptionPane.showConfirmDialog(this, "Deseja excluir este estudo?", "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION
                && estudoDAO.excluirEstudo(id, idUsuario)) {
            limparCampos();
            carregarEstudos();
        }
    }

    private void limparCampos() {
        campoAssunto.setText("");
        campoData.setText("");
        campoHorario.setText("");
        campoObservacao.setText("");
        campoStatus.setSelectedIndex(0);
    }

    private LocalDate data(Object valor) {
        if (valor instanceof java.sql.Date) return ((java.sql.Date) valor).toLocalDate();
        try { return LocalDate.parse(String.valueOf(valor)); } catch (DateTimeParseException e) { return null; }
    }

    private String prioridade(String status, LocalDate data) {
        if ("Concluído".equals(status) || data == null) return "BAIXA";
        long dias = ChronoUnit.DAYS.between(LocalDate.now(), data);
        return dias < 4 ? "ALTA" : dias < 8 ? "MÉDIA" : "BAIXA";
    }

    private void aviso(String mensagem) { JOptionPane.showMessageDialog(this, mensagem, "Estudos", JOptionPane.WARNING_MESSAGE); }
    private void erro(String mensagem) { JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE); }

    private static class LupaIcon implements Icon {
        private final int tamanho; private final Color cor;
        LupaIcon(int tamanho, Color cor) { this.tamanho = tamanho; this.cor = cor; }
        public int getIconWidth() { return tamanho; }
        public int getIconHeight() { return tamanho; }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(cor); g2.setStroke(new BasicStroke(2.3f));
            int d = tamanho - 8; g2.drawOval(x + 2, y + 2, d, d);
            g2.drawLine(x + d - 1, y + d - 1, x + tamanho - 2, y + tamanho - 2); g2.dispose();
        }
    }

    private static class EstudoRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
            l.setBorder(new EmptyBorder(8, 12, 8, 12));
            l.setForeground(s ? t.getSelectionForeground() : TEXTO);
            l.setBackground(s ? t.getSelectionBackground()
                    : r % 2 == 0 ? Color.WHITE : new Color(249, 249, 253));
            l.setOpaque(true);
            l.setFont(new Font("SansSerif", Font.PLAIN, 14));
            l.setVerticalAlignment(SwingConstants.CENTER);
            if (c == 4 || c == 5) l.setHorizontalAlignment(SwingConstants.CENTER);
            return l;
        }
    }

    private static class MultilineRenderer extends JTextArea implements TableCellRenderer {
        MultilineRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
            setBorder(new EmptyBorder(8, 12, 8, 12));
            setFont(new Font("SansSerif", Font.PLAIN, 14));
        }

        @Override public Component getTableCellRendererComponent(JTable table, Object value,
                                                                  boolean selected, boolean focused,
                                                                  int row, int column) {
            setText(value == null ? "" : String.valueOf(value));
            setForeground(selected ? table.getSelectionForeground() : TEXTO);
            setBackground(selected ? table.getSelectionBackground()
                    : row % 2 == 0 ? Color.WHITE : new Color(249, 249, 253));
            setAlignmentY(CENTER_ALIGNMENT);
            return this;
        }
    }

    private static class PillRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            PillLabel l = new PillLabel(); String text = String.valueOf(v);
            Color color = c == 6 ? new Color(220, 50, 80) : ROXO;
            if ("Em andamento".equals(text)) color = new Color(224, 116, 15);
            if ("Concluído".equals(text)) color = new Color(34, 139, 84);
            if ("BAIXA".equals(text)) color = new Color(35, 110, 205);
            if ("MÉDIA".equals(text)) color = new Color(224, 116, 15);
            l.setText(text); l.setForeground(color);
            l.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 28));
            l.setFont(new Font("SansSerif", Font.BOLD, 14)); l.setHorizontalAlignment(SwingConstants.CENTER);
            return l;
        }
    }
    private static class PillLabel extends JLabel {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = g2.getFontMetrics(getFont()).stringWidth(getText()) + 24;
            int h = g2.getFontMetrics(getFont()).getHeight() + 12;
            int x = Math.max(4, (getWidth() - w) / 2), y = Math.max(4, (getHeight() - h) / 2);
            g2.setColor(getBackground()); g2.fillRoundRect(x, y, w, h, 12, 12); super.paintComponent(g2); g2.dispose();
        }
    }
    private static class ActionRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
            l.setText(""); l.setIcon(new EstiloVisual.AcoesIcon()); l.setHorizontalAlignment(SwingConstants.CENTER);
            l.setBorder(new EmptyBorder(0, 0, 0, 0)); return l;
        }
    }
    private static class RoundedPanel extends JPanel {
        private final int raio; private final Color fundo;
        RoundedPanel(int raio, Color fundo) { this.raio = raio; this.fundo = fundo; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fundo); g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, raio, raio);
            g2.setColor(BORDA); g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, raio, raio); g2.dispose();
            super.paintComponent(g);
        }
    }
}
