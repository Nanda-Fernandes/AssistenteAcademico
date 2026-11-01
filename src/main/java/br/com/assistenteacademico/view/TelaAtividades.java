package br.com.assistenteacademico.view;

import br.com.assistenteacademico.AtividadeDAO;
import br.com.assistenteacademico.DisciplinaDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TelaAtividades extends JFrame {
    private static final Color ROXO = new Color(76, 55, 190);
    private static final Color TEXTO = new Color(25, 30, 49);
    private static final Color SECUNDARIO = new Color(91, 101, 139);
    private static final Color BORDA = new Color(231, 231, 243);
    private final int idUsuario;
    private final AtividadeDAO atividadeDAO = new AtividadeDAO();
    private final DefaultTableModel modelo;
    private final JTable tabela;
    private final JComboBox<DisciplinaItem> campoDisciplina = new JComboBox<>();
    private final JTextField campoTitulo = new JTextField();
    private final JTextField campoTipo = new JTextField();
    private final JTextField campoData = new JTextField();
    private final JTextField campoDescricao = new JTextField();
    private final JTextField campoObservacao = new JTextField();
    private final JComboBox<String> campoStatus = new JComboBox<>(
            new String[]{"Não iniciado", "Em andamento", "Pendente", "Concluído"});
    private final JTextField busca = new JTextField();
    private final JComboBox<String> filtro = new JComboBox<>(new String[]{"Todas as atividades"});
    private List<Object[]> atividades;

    public TelaAtividades(int idUsuario) {
        this.idUsuario = idUsuario;
        setTitle("Assistente Acadêmico | Atividades");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setBackground(new Color(245, 245, 251));
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        modelo = new DefaultTableModel(new String[]{
                "ID", "ID disciplina", "Atividade", "Disciplina", "Tipo",
                "Entrega", "Status", "Prioridade", "Ações"
        }, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(modelo);
        configurarTabela();
        carregarDisciplinas();
        add(criarTela());
        carregarAtividades();
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
        return MenuLateral.criar(this, idUsuario, "Atividades");
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        JPanel titulo = new JPanel();
        titulo.setOpaque(false);
        titulo.setLayout(new BoxLayout(titulo, BoxLayout.Y_AXIS));
        JLabel principal = new JLabel("Atividades");
        principal.setFont(new Font("SansSerif", Font.BOLD, 32));
        principal.setForeground(TEXTO);
        JLabel subtitulo = new JLabel("Gerencie suas atividades acadêmicas");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subtitulo.setForeground(SECUNDARIO);
        titulo.add(principal);
        titulo.add(Box.createVerticalStrut(12));
        titulo.add(subtitulo);
        cabecalho.add(titulo, BorderLayout.WEST);

        JButton nova = new EstiloVisual.BotaoAcao("+  Nova Atividade");
        nova.addActionListener(e -> abrirEditor(-1));
        cabecalho.add(nova, BorderLayout.EAST);
        return cabecalho;
    }

    private JPanel criarFiltros() {
        JPanel filtros = new JPanel(new BorderLayout(18, 0));
        filtros.setOpaque(false);
        filtro.setPreferredSize(new Dimension(300, 54));
        filtro.setBackground(Color.WHITE);
        filtro.setFont(new Font("SansSerif", Font.PLAIN, 15));
        filtro.addActionListener(e -> aplicarFiltros());
        busca.setFont(new Font("SansSerif", Font.PLAIN, 15));
        busca.setForeground(TEXTO);
        busca.setCaretColor(ROXO);
        busca.setBorder(new EmptyBorder(0, 8, 0, 12));
        busca.setPreferredSize(new Dimension(236, 48));
        busca.putClientProperty("JTextField.placeholderText", "Buscar atividade...");
        busca.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltros(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltros(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltros(); }
        });
        JPanel buscaPainel = new JPanel(new BorderLayout());
        buscaPainel.setBackground(Color.WHITE);
        buscaPainel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 190, 215)),
                new EmptyBorder(0, 10, 0, 4)));
        buscaPainel.add(new JLabel(new LupaIcon(20, new Color(75, 75, 110))), BorderLayout.WEST);
        buscaPainel.add(busca, BorderLayout.CENTER);
        buscaPainel.setPreferredSize(new Dimension(280, 48));

        filtros.add(filtro, BorderLayout.WEST);
        filtros.add(buscaPainel, BorderLayout.EAST);
        return filtros;
    }

    private JScrollPane criarTabela() {
        tabela.setShowGrid(true);
        tabela.setGridColor(new Color(242, 242, 248));
        tabela.setRowHeight(94);
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
                    if (e.getX() - tabela.getCellRect(row, col, true).x < 55) abrirEditor(id);
                    else excluirPorId(id);
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
        tabela.getColumnModel().getColumn(0).setMinWidth(0); tabela.getColumnModel().getColumn(0).setMaxWidth(0);
        tabela.getColumnModel().getColumn(1).setMinWidth(0); tabela.getColumnModel().getColumn(1).setMaxWidth(0);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(330);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(170);
        tabela.getColumnModel().getColumn(8).setPreferredWidth(95);
        tabela.setDefaultRenderer(Object.class, new AtividadeRenderer());
        tabela.getColumnModel().getColumn(4).setCellRenderer(new PillRenderer());
        tabela.getColumnModel().getColumn(6).setCellRenderer(new PillRenderer());
        tabela.getColumnModel().getColumn(7).setCellRenderer(new PillRenderer());
        tabela.getColumnModel().getColumn(8).setCellRenderer(new ActionRenderer());
    }

    private void carregarDisciplinas() {
        for (Object[] d : new DisciplinaDAO().buscarDisciplinasPorUsuario(idUsuario)) {
            DisciplinaItem item = new DisciplinaItem(((Number) d[0]).intValue(), (String) d[1]);
            campoDisciplina.addItem(item);
            filtro.addItem(item.toString());
        }
    }

    private void carregarAtividades() {
        atividades = atividadeDAO.buscarAtividadesPorUsuario(idUsuario);
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        modelo.setRowCount(0);
        String termo = busca.getText().trim().toLowerCase();
        String disciplina = filtro.getSelectedItem() == null ? "" : filtro.getSelectedItem().toString();
        for (Object[] a : atividades == null ? List.<Object[]>of() : atividades) {
            String texto = (a[2] + " " + a[3] + " " + a[4] + " " + a[6]).toLowerCase();
            if (!termo.isEmpty() && !texto.contains(termo)) continue;
            if (!disciplina.equals("Todas as atividades") && !disciplina.equals(String.valueOf(a[2]))) continue;
            LocalDate data = ((java.sql.Date) a[5]).toLocalDate();
            modelo.addRow(new Object[]{a[0], a[1], a[3] + "\n" + a[6], a[2], a[4],
                    data.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    a[8], prioridade(String.valueOf(a[8]), data), "✎    🗑"});
        }
    }

    private String prioridade(String status, LocalDate data) {
        if ("Concluído".equals(status)) return "BAIXA";
        long dias = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), data);
        return dias < 4 ? "ALTA" : dias < 8 ? "MÉDIA" : "BAIXA";
    }

    private void abrirEditor(int id) {
        if (id >= 0) preencherCampos(id);
        else limparCampos();
        JDialog dialog = new JDialog(this, id >= 0 ? "Editar atividade" : "Nova atividade", true);
        dialog.setLayout(new BorderLayout(12, 12));
        dialog.add(criarFormulario(), BorderLayout.CENTER);
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelar = new JButton("Cancelar");
        JButton salvar = new JButton(id >= 0 ? "Salvar alterações" : "Cadastrar");
        cancelar.addActionListener(e -> dialog.dispose());
        salvar.addActionListener(e -> {
            if (id >= 0) editar(); else cadastrar();
            if (dialog.isDisplayable()) dialog.dispose();
        });
        botoes.add(cancelar); botoes.add(salvar);
        dialog.add(botoes, BorderLayout.SOUTH);
        dialog.pack(); dialog.setSize(620, 430); dialog.setLocationRelativeTo(this); dialog.setVisible(true);
    }

    private JPanel criarFormulario() {
        JPanel p = new JPanel(new GridLayout(4, 4, 10, 10));
        p.setBorder(new EmptyBorder(18, 18, 18, 18));
        adicionar(p, "Disciplina:*", campoDisciplina);
        adicionar(p, "Título:*", campoTitulo);
        adicionar(p, "Tipo:*", campoTipo);
        adicionar(p, "Entrega (AAAA-MM-DD):*", campoData);
        adicionar(p, "Descrição:*", campoDescricao);
        adicionar(p, "Observação:", campoObservacao);
        adicionar(p, "Status:*", campoStatus);
        return p;
    }

    private void adicionar(JPanel p, String texto, JComponent c) { p.add(new JLabel(texto)); p.add(c); }
    private void preencherCampos(int id) {
        for (Object[] a : atividades) if (((Number) a[0]).intValue() == id) {
            selecionarDisciplina(((Number) a[1]).intValue()); campoTitulo.setText(String.valueOf(a[3]));
            campoTipo.setText(String.valueOf(a[4])); campoData.setText(String.valueOf(a[5]));
            campoDescricao.setText(String.valueOf(a[6])); campoObservacao.setText(String.valueOf(a[7]));
            campoStatus.setSelectedItem(a[8]); return;
        }
    }
    private void selecionarDisciplina(int id) { for (int i=0;i<campoDisciplina.getItemCount();i++) if (campoDisciplina.getItemAt(i).getId()==id) { campoDisciplina.setSelectedIndex(i); return; } }
    private boolean camposValidos() {
        if (campoDisciplina.getSelectedItem() == null || campoTitulo.getText().trim().isBlank() ||
                campoTipo.getText().trim().isBlank() || campoData.getText().trim().isBlank() || campoDescricao.getText().trim().isBlank()) { aviso("Preencha todos os campos obrigatórios."); return false; }
        try { LocalDate.parse(campoData.getText().trim()); return true; } catch (DateTimeParseException e) { aviso("Informe a data no formato AAAA-MM-DD."); return false; }
    }
    private void cadastrar() { if (!camposValidos()) return; if (atividadeDAO.cadastrarAtividade(idUsuario, ((DisciplinaItem)campoDisciplina.getSelectedItem()).getId(), campoTitulo.getText().trim(), campoTipo.getText().trim(), campoData.getText().trim(), campoDescricao.getText().trim(), campoObservacao.getText().trim(), String.valueOf(campoStatus.getSelectedItem()))) { carregarAtividades(); } else erro("Não foi possível cadastrar a atividade."); }
    private void editar() { int row=tabela.getSelectedRow(); if(row<0){aviso("Selecione a atividade na tabela.");return;} if(!camposValidos())return; int id=((Number)modelo.getValueAt(row,0)).intValue(); if(atividadeDAO.editarAtividade(id,idUsuario,((DisciplinaItem)campoDisciplina.getSelectedItem()).getId(),campoTitulo.getText().trim(),campoTipo.getText().trim(),campoData.getText().trim(),campoDescricao.getText().trim(),campoObservacao.getText().trim(),String.valueOf(campoStatus.getSelectedItem()))) carregarAtividades(); else erro("Não foi possível atualizar a atividade."); }
    private void excluirPorId(int id) { if(JOptionPane.showConfirmDialog(this,"Deseja excluir esta atividade?","Confirmar",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION && atividadeDAO.excluirAtividade(id,idUsuario)) carregarAtividades(); }
    private void limparCampos() { campoTitulo.setText("");campoTipo.setText("");campoData.setText("");campoDescricao.setText("");campoObservacao.setText("");campoStatus.setSelectedIndex(0); }
    private void aviso(String m){JOptionPane.showMessageDialog(this,m,"Atividades",JOptionPane.WARNING_MESSAGE);}
    private void erro(String m){JOptionPane.showMessageDialog(this,m,"Erro",JOptionPane.ERROR_MESSAGE);}
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
            int diametro = tamanho - 8;
            g2.drawOval(x + 2, y + 2, diametro, diametro);
            g2.drawLine(x + diametro - 1, y + diametro - 1, x + tamanho - 2, y + tamanho - 2);
            g2.dispose();
        }
    }

    private static class AtividadeRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){
            JLabel l=(JLabel)super.getTableCellRendererComponent(t,v,s,f,r,c); l.setBorder(new EmptyBorder(8,14,8,14)); l.setForeground(s ? t.getSelectionForeground() : TEXTO); l.setBackground(s ? t.getSelectionBackground() : (r % 2 == 0 ? Color.WHITE : new Color(249, 249, 253))); l.setOpaque(true); l.setVerticalAlignment(SwingConstants.CENTER); l.setFont(new Font("SansSerif",Font.PLAIN,14));
            if(c==2){l.setText("<html><b>"+String.valueOf(v).replace("\n","</b><br><font color='#5B658B'>")+"</font></html>");}
            if(c==4||c==6||c==7||c==8){l.setHorizontalAlignment(SwingConstants.CENTER); l.setFont(new Font("SansSerif",Font.BOLD,13));}
            if(c==6){l.setForeground(new Color(220,50,80));}
            if(c==7){l.setForeground(new Color(220,105,20));}
            if(c==8){l.setForeground(ROXO);}
            return l;
        }
    }

    private static class PillRenderer extends DefaultTableCellRenderer {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                PillLabel label = new PillLabel();
                label.setOpaque(false);
                String texto = String.valueOf(v);
                Color cor = c == 6 ? new Color(220, 50, 80) : c == 7 ? new Color(232, 112, 12) : ROXO;
                if ("Em andamento".equals(texto)) cor = new Color(224, 116, 15);
                if ("Pendente".equals(texto)) cor = SECUNDARIO;
                if ("Concluído".equals(texto)) cor = new Color(34, 139, 84);
                if ("BAIXA".equals(texto)) cor = new Color(25, 100, 220);
                label.setText(texto);
                label.setForeground(cor);
                label.setBackground(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 28));
                label.setFont(new Font("SansSerif", Font.BOLD, 13));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                return label;
            }
        }

        private static class PillLabel extends JLabel {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                FontMetrics metrics = g2.getFontMetrics(getFont());
                int pillWidth = metrics.stringWidth(getText()) + 24;
                int pillHeight = metrics.getHeight() + 12;
                int x = Math.max(4, (getWidth() - pillWidth) / 2);
                int y = Math.max(4, (getHeight() - pillHeight) / 2);
                g2.setColor(getBackground());
                g2.fillRoundRect(x, y, pillWidth, pillHeight, 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        }

    private static class ActionRenderer extends DefaultTableCellRenderer {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
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

        @Override
        protected void paintComponent(Graphics g) {
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
