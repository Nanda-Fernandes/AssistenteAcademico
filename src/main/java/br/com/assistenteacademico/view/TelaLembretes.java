package br.com.assistenteacademico.view;

import br.com.assistenteacademico.LembreteDAO;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TelaLembretes extends JFrame {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final int idUsuario;
    private final LembreteDAO lembreteDAO = new LembreteDAO();
    private final JComboBox<ItemLembrete> campoItem = new JComboBox<>();
    private final JTextField campoData = new JTextField();
    private final JComboBox<String> campoStatus = new JComboBox<>(new String[]{"Pendente", "Concluído"});
    private final DefaultTableModel modelo;
    private final JTable tabela;

    public TelaLembretes(int idUsuario) {
        this.idUsuario = idUsuario;
        setTitle("Meus Lembretes");
        setSize(850, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        modelo = new DefaultTableModel(new String[]{"ID", "Tipo", "Item", "Data", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
        tabela = new JTable(modelo);
        EstiloVisual.configurarTabela(tabela);
        tabela.setAutoCreateRowSorter(true);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(this::preencherCampos);

        JPanel superior = new JPanel(new BorderLayout(8, 8));
        superior.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        superior.add(criarFormulario(), BorderLayout.CENTER);
        superior.add(criarBotoes(), BorderLayout.SOUTH);
        add(superior, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        carregarItens();
        carregarLembretes();
        setVisible(true);
    }

    private JPanel criarFormulario() {
        JPanel painel = new JPanel(new GridLayout(2, 4, 8, 8));
        painel.add(new JLabel("Item:*"));
        painel.add(campoItem);
        painel.add(new JLabel("Data (AAAA-MM-DD HH:MM:SS):*"));
        painel.add(campoData);
        painel.add(new JLabel("Status:*"));
        painel.add(campoStatus);
        painel.add(new JLabel());
        painel.add(new JLabel("* Campos obrigatórios"));
        return painel;
    }

    private JPanel criarBotoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        JButton cadastrar = new JButton("Cadastrar");
        JButton concluir = new JButton("Marcar como concluído");
        JButton excluir = new JButton("Excluir");
        JButton atualizar = new JButton("Atualizar lista");
        JButton limpar = new JButton("Limpar");
        JButton voltar = new JButton("Voltar");
        cadastrar.addActionListener(e -> cadastrar());
        concluir.addActionListener(e -> alterarStatus("Concluído"));
        excluir.addActionListener(e -> excluir());
        atualizar.addActionListener(e -> {
            carregarItens();
            carregarLembretes();
        });
        limpar.addActionListener(e -> limparCampos());
        voltar.addActionListener(e -> dispose());
        painel.add(cadastrar);
        painel.add(concluir);
        painel.add(excluir);
        painel.add(atualizar);
        painel.add(limpar);
        painel.add(voltar);
        return painel;
    }

    private void carregarItens() {
        campoItem.removeAllItems();
        List<Object[]> itens = lembreteDAO.listarItensPorUsuario(idUsuario);
        for (Object[] item : itens) {
            campoItem.addItem(new ItemLembrete((String) item[0], ((Number) item[1]).intValue(), (String) item[2]));
        }
    }

    private void carregarLembretes() {
        modelo.setRowCount(0);
        for (Object[] lembrete : lembreteDAO.buscarLembretesPorUsuario(idUsuario)) {
            modelo.addRow(new Object[]{lembrete[0], lembrete[1], lembrete[2], lembrete[3], lembrete[4]});
        }
    }

    private void preencherCampos(ListSelectionEvent evento) {
        if (evento.getValueIsAdjusting() || tabela.getSelectedRow() < 0) return;
        int linha = tabela.getSelectedRow();
        campoData.setText(String.valueOf(modelo.getValueAt(linha, 3)).replace('T', ' '));
        campoStatus.setSelectedItem(modelo.getValueAt(linha, 4));
    }

    private void cadastrar() {
        ItemLembrete item = (ItemLembrete) campoItem.getSelectedItem();
        if (item == null) {
            aviso("Cadastre uma atividade, prova ou estudo antes de criar um lembrete.");
            return;
        }
        String data = campoData.getText().trim();
        if (data.isBlank()) {
            aviso("Informe a data do lembrete.");
            return;
        }
        try {
            LocalDateTime.parse(data, FORMATO);
        } catch (DateTimeParseException e) {
            aviso("Informe a data no formato AAAA-MM-DD HH:MM:SS.");
            return;
        }
        boolean cadastrado = lembreteDAO.cadastrarLembrete(
                item.tipo, item.id, data, (String) campoStatus.getSelectedItem(), idUsuario);
        if (cadastrado) {
            JOptionPane.showMessageDialog(this, "Lembrete cadastrado com sucesso.");
            limparCampos();
            carregarLembretes();
        } else {
            erro("Não foi possível cadastrar o lembrete.");
        }
    }

    private void alterarStatus(String status) {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            aviso("Selecione um lembrete na tabela.");
            return;
        }
        int id = ((Number) modelo.getValueAt(linha, 0)).intValue();
        if (lembreteDAO.alterarStatus(id, idUsuario, status)) {
            carregarLembretes();
        } else {
            erro("Não foi possível alterar o status do lembrete.");
        }
    }

    private void excluir() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            aviso("Selecione um lembrete na tabela para excluir.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Deseja excluir o lembrete selecionado?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        int id = ((Number) modelo.getValueAt(linha, 0)).intValue();
        if (lembreteDAO.excluirLembrete(id, idUsuario)) {
            limparCampos();
            carregarLembretes();
        } else {
            erro("Não foi possível excluir o lembrete.");
        }
    }

    private void limparCampos() {
        tabela.clearSelection();
        campoData.setText("");
        campoStatus.setSelectedIndex(0);
    }

    private void aviso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Lembretes", JOptionPane.WARNING_MESSAGE);
    }

    private void erro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private static class ItemLembrete {
        private final String tipo;
        private final int id;
        private final String descricao;

        private ItemLembrete(String tipo, int id, String descricao) {
            this.tipo = tipo;
            this.id = id;
            this.descricao = descricao;
        }

        @Override
        public String toString() {
            return tipo + " - " + descricao;
        }
    }
}
