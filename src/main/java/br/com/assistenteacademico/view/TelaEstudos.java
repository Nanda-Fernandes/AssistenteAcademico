package br.com.assistenteacademico.view;

import br.com.assistenteacademico.DisciplinaDAO;
import br.com.assistenteacademico.EstudoDAO;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TelaEstudos extends JFrame {

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
            new String[]{"Não iniciado", "Em andamento", "Concluído"}
    );

    public TelaEstudos(int idUsuario) {
        this.idUsuario = idUsuario;
        setTitle("Meus Estudos");
        setSize(1100, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        modelo = new DefaultTableModel(new String[]{
                "ID", "ID disciplina", "Disciplina", "O que estudar", "Data", "Horário", "Observação", "Status"
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

        carregarEstudos();
        setVisible(true);
    }

    private JPanel criarFormulario() {
        JPanel painel = new JPanel(new GridLayout(3, 4, 8, 8));
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

    private JPanel criarBotoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        JButton cadastrar = new JButton("Cadastrar");
        JButton editar = new JButton("Editar");
        JButton concluir = new JButton("Marcar como concluído");
        JButton excluir = new JButton("Excluir");
        JButton atualizar = new JButton("Atualizar lista");
        JButton limpar = new JButton("Limpar");
        JButton voltar = new JButton("Voltar");
        cadastrar.addActionListener(e -> cadastrar());
        editar.addActionListener(e -> editar());
        concluir.addActionListener(e -> alterarStatus("Concluído"));
        excluir.addActionListener(e -> excluir());
        atualizar.addActionListener(e -> carregarEstudos());
        limpar.addActionListener(e -> limparCampos());
        voltar.addActionListener(e -> dispose());
        painel.add(cadastrar);
        painel.add(editar);
        painel.add(concluir);
        painel.add(excluir);
        painel.add(atualizar);
        painel.add(limpar);
        painel.add(voltar);
        return painel;
    }

    private void carregarDisciplinas() {
        for (Object[] disciplina : new DisciplinaDAO().buscarDisciplinasPorUsuario(idUsuario)) {
            campoDisciplina.addItem(new DisciplinaItem(
                    ((Number) disciplina[0]).intValue(), (String) disciplina[1]
            ));
        }
    }

    private void preencherCampos(ListSelectionEvent evento) {
        if (evento.getValueIsAdjusting() || tabela.getSelectedRow() < 0) return;
        int linha = tabela.getSelectedRow();
        selecionarDisciplina(((Number) modelo.getValueAt(linha, 1)).intValue());
        campoAssunto.setText(textoDaCelula(linha, 3));
        campoData.setText(textoDaCelula(linha, 4));
        campoHorario.setText(textoDaCelula(linha, 5));
        campoObservacao.setText(textoDaCelula(linha, 6));
        campoStatus.setSelectedItem(textoDaCelula(linha, 7));
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
            aviso("Cadastre uma disciplina antes de cadastrar um estudo.");
            return false;
        }
        if (campoAssunto.getText().trim().isBlank() || campoData.getText().trim().isBlank()
                || campoHorario.getText().trim().isBlank()) {
            aviso("Preencha todos os campos obrigatórios.");
            return false;
        }
        try {
            LocalDate.parse(campoData.getText().trim());
            LocalTime.parse(campoHorario.getText().trim());
            return true;
        } catch (DateTimeParseException e) {
            aviso("Informe data AAAA-MM-DD e horário HH:MM.");
            return false;
        }
    }

    private void cadastrar() {
        if (!camposValidos()) return;
        if (estudoDAO.cadastrarEstudo(idUsuario, disciplinaSelecionada().getId(), campoAssunto.getText().trim(),
                campoData.getText().trim(), campoHorario.getText().trim(), campoObservacao.getText().trim(),
                (String) campoStatus.getSelectedItem())) {
            JOptionPane.showMessageDialog(this, "Estudo cadastrado com sucesso.");
            limparCampos();
            carregarEstudos();
        } else erro("Não foi possível cadastrar o estudo.");
    }

    private void editar() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            aviso("Selecione um estudo na tabela para editar.");
            return;
        }
        if (!camposValidos()) return;
        int idEstudo = ((Number) modelo.getValueAt(linha, 0)).intValue();
        if (estudoDAO.editarEstudo(idEstudo, idUsuario, disciplinaSelecionada().getId(),
                campoAssunto.getText().trim(), campoData.getText().trim(), campoHorario.getText().trim(),
                campoObservacao.getText().trim(), (String) campoStatus.getSelectedItem())) {
            JOptionPane.showMessageDialog(this, "Estudo atualizado com sucesso.");
            limparCampos();
            carregarEstudos();
        } else erro("Não foi possível atualizar o estudo.");
    }

    private void alterarStatus(String status) {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            aviso("Selecione um estudo na tabela.");
            return;
        }
        int idEstudo = ((Number) modelo.getValueAt(linha, 0)).intValue();
        if (estudoDAO.alterarStatus(idEstudo, idUsuario, status)) {
            JOptionPane.showMessageDialog(this, "Status alterado para " + status + ".");
            carregarEstudos();
        } else erro("Não foi possível alterar o status do estudo.");
    }

    private void excluir() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            aviso("Selecione um estudo na tabela para excluir.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Deseja excluir o estudo selecionado?", "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        int idEstudo = ((Number) modelo.getValueAt(linha, 0)).intValue();
        if (estudoDAO.excluirEstudo(idEstudo, idUsuario)) {
            JOptionPane.showMessageDialog(this, "Estudo excluído com sucesso.");
            limparCampos();
            carregarEstudos();
        } else erro("Não foi possível excluir o estudo.");
    }

    private DisciplinaItem disciplinaSelecionada() {
        return (DisciplinaItem) campoDisciplina.getSelectedItem();
    }

    private void carregarEstudos() {
        modelo.setRowCount(0);
        for (Object[] estudo : estudoDAO.buscarEstudosPorUsuario(idUsuario)) modelo.addRow(estudo);
    }

    private String textoDaCelula(int linha, int coluna) {
        Object valor = modelo.getValueAt(linha, coluna);
        return valor == null ? "" : valor.toString();
    }

    private void limparCampos() {
        tabela.clearSelection();
        campoAssunto.setText("");
        campoData.setText("");
        campoHorario.setText("");
        campoObservacao.setText("");
        campoStatus.setSelectedIndex(0);
    }

    private void aviso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Estudos", JOptionPane.WARNING_MESSAGE);
    }

    private void erro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
