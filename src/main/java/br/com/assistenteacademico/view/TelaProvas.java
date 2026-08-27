package br.com.assistenteacademico.view;

import br.com.assistenteacademico.DisciplinaDAO;
import br.com.assistenteacademico.ProvaDAO;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TelaProvas extends JFrame {

    private final int idUsuario;
    private final ProvaDAO provaDAO = new ProvaDAO();
    private final DefaultTableModel modelo;
    private final JTable tabela;
    private final JComboBox<DisciplinaItem> campoDisciplina = new JComboBox<>();
    private final JTextField campoData = new JTextField();
    private final JTextField campoConteudo = new JTextField();
    private final JTextField campoObservacao = new JTextField();

    public TelaProvas(int idUsuario) {
        this.idUsuario = idUsuario;
        setTitle("Minhas Provas");
        setSize(950, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        modelo = new DefaultTableModel(new String[]{
                "ID", "ID disciplina", "Disciplina", "Data", "Conteúdo", "Observação"
        }, 0) {
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

        carregarDisciplinas();
        JPanel superior = new JPanel(new BorderLayout(8, 8));
        superior.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        superior.add(criarFormulario(), BorderLayout.CENTER);
        superior.add(criarBotoes(), BorderLayout.SOUTH);
        add(superior, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        carregarProvas();
        setVisible(true);
    }

    private JPanel criarFormulario() {
        JPanel painel = new JPanel(new GridLayout(2, 4, 8, 8));
        adicionarCampo(painel, "Disciplina:*", campoDisciplina);
        adicionarCampo(painel, "Data (AAAA-MM-DD):*", campoData);
        adicionarCampo(painel, "Conteúdo:*", campoConteudo);
        adicionarCampo(painel, "Observação:", campoObservacao);
        return painel;
    }

    private void adicionarCampo(JPanel painel, String rotulo, JComponent campo) {
        painel.add(new JLabel(rotulo));
        painel.add(campo);
    }

    private JPanel criarBotoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        JButton cadastrar = new JButton("Cadastrar");
        JButton editar = new JButton("Editar");
        JButton excluir = new JButton("Excluir");
        JButton atualizar = new JButton("Atualizar lista");
        JButton limpar = new JButton("Limpar");
        JButton voltar = new JButton("Voltar");
        cadastrar.addActionListener(e -> cadastrar());
        editar.addActionListener(e -> editar());
        excluir.addActionListener(e -> excluir());
        atualizar.addActionListener(e -> carregarProvas());
        limpar.addActionListener(e -> limparCampos());
        voltar.addActionListener(e -> dispose());
        painel.add(cadastrar);
        painel.add(editar);
        painel.add(excluir);
        painel.add(atualizar);
        painel.add(limpar);
        painel.add(voltar);
        return painel;
    }

    private void carregarDisciplinas() {
        List<Object[]> disciplinas = new DisciplinaDAO().buscarDisciplinasPorUsuario(idUsuario);
        for (Object[] disciplina : disciplinas) {
            campoDisciplina.addItem(new DisciplinaItem(
                    ((Number) disciplina[0]).intValue(), (String) disciplina[1]
            ));
        }
    }

    private void preencherCampos(ListSelectionEvent evento) {
        if (evento.getValueIsAdjusting() || tabela.getSelectedRow() < 0) return;
        int linha = tabela.getSelectedRow();
        selecionarDisciplina(((Number) modelo.getValueAt(linha, 1)).intValue());
        campoData.setText(textoDaCelula(linha, 3));
        campoConteudo.setText(textoDaCelula(linha, 4));
        campoObservacao.setText(textoDaCelula(linha, 5));
    }

    private void selecionarDisciplina(int idDisciplina) {
        for (int i = 0; i < campoDisciplina.getItemCount(); i++) {
            if (campoDisciplina.getItemAt(i).getId() == idDisciplina) {
                campoDisciplina.setSelectedIndex(i);
                return;
            }
        }
    }

    private boolean camposValidos() {
        if (campoDisciplina.getSelectedItem() == null) {
            aviso("Cadastre uma disciplina antes de cadastrar uma prova.");
            return false;
        }
        if (campoData.getText().trim().isBlank() || campoConteudo.getText().trim().isBlank()) {
            aviso("Preencha todos os campos obrigatórios.");
            return false;
        }
        try {
            LocalDate.parse(campoData.getText().trim());
            return true;
        } catch (DateTimeParseException e) {
            aviso("Informe a data no formato AAAA-MM-DD.");
            return false;
        }
    }

    private void cadastrar() {
        if (!camposValidos()) return;
        if (provaDAO.cadastrarProva(idUsuario, disciplinaSelecionada().getId(), campoData.getText().trim(),
                campoConteudo.getText().trim(), campoObservacao.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Prova cadastrada com sucesso.");
            limparCampos();
            carregarProvas();
        } else erro("Não foi possível cadastrar a prova.");
    }

    private void editar() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            aviso("Selecione uma prova na tabela para editar.");
            return;
        }
        if (!camposValidos()) return;
        int idProva = ((Number) modelo.getValueAt(linha, 0)).intValue();
        if (provaDAO.editarProva(idProva, idUsuario, disciplinaSelecionada().getId(),
                campoData.getText().trim(), campoConteudo.getText().trim(), campoObservacao.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Prova atualizada com sucesso.");
            limparCampos();
            carregarProvas();
        } else erro("Não foi possível atualizar a prova.");
    }

    private void excluir() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            aviso("Selecione uma prova na tabela para excluir.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Deseja excluir a prova selecionada?", "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        int idProva = ((Number) modelo.getValueAt(linha, 0)).intValue();
        if (provaDAO.excluirProva(idProva, idUsuario)) {
            JOptionPane.showMessageDialog(this, "Prova excluída com sucesso.");
            limparCampos();
            carregarProvas();
        } else erro("Não foi possível excluir a prova.");
    }

    private DisciplinaItem disciplinaSelecionada() {
        return (DisciplinaItem) campoDisciplina.getSelectedItem();
    }

    private void carregarProvas() {
        modelo.setRowCount(0);
        for (Object[] prova : provaDAO.buscarProvasPorUsuario(idUsuario)) modelo.addRow(prova);
    }

    private String textoDaCelula(int linha, int coluna) {
        Object valor = modelo.getValueAt(linha, coluna);
        return valor == null ? "" : valor.toString();
    }

    private void limparCampos() {
        tabela.clearSelection();
        campoData.setText("");
        campoConteudo.setText("");
        campoObservacao.setText("");
    }

    private void aviso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Provas", JOptionPane.WARNING_MESSAGE);
    }

    private void erro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
