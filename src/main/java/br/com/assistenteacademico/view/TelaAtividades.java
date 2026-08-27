package br.com.assistenteacademico.view;

import br.com.assistenteacademico.AtividadeDAO;
import br.com.assistenteacademico.DisciplinaDAO;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TelaAtividades extends JFrame {

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
            new String[]{"Não iniciado", "Em andamento", "Concluído"}
    );

    public TelaAtividades(int idUsuario) {
        this.idUsuario = idUsuario;

        setTitle("Minhas Atividades");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        modelo = new DefaultTableModel(new String[]{
                "ID", "ID disciplina", "Disciplina", "Título", "Tipo", "Entrega",
                "Descrição", "Observação", "Status"
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
        tabela.getSelectionModel().addListSelectionListener(this::preencherCamposSelecionados);

        carregarDisciplinas();
        JPanel superior = new JPanel(new BorderLayout(8, 8));
        superior.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        superior.add(criarFormulario(), BorderLayout.CENTER);
        superior.add(criarBotoes(), BorderLayout.SOUTH);
        add(superior, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        carregarAtividades();
        setVisible(true);
    }

    private JPanel criarFormulario() {
        JPanel painel = new JPanel(new GridLayout(4, 4, 8, 8));
        adicionarCampo(painel, "Disciplina:*", campoDisciplina);
        adicionarCampo(painel, "Título:*", campoTitulo);
        adicionarCampo(painel, "Tipo:*", campoTipo);
        adicionarCampo(painel, "Data (AAAA-MM-DD):*", campoData);
        adicionarCampo(painel, "Descrição:*", campoDescricao);
        adicionarCampo(painel, "Observação:", campoObservacao);
        adicionarCampo(painel, "Status:*", campoStatus);
        painel.add(new JLabel("* Campo obrigatório"));
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
        JButton concluir = new JButton("Marcar como concluída");
        JButton excluir = new JButton("Excluir");
        JButton atualizar = new JButton("Atualizar lista");
        JButton limpar = new JButton("Limpar");
        JButton voltar = new JButton("Voltar");
        cadastrar.addActionListener(e -> cadastrar());
        editar.addActionListener(e -> editar());
        concluir.addActionListener(e -> alterarStatus("Concluído"));
        excluir.addActionListener(e -> excluir());
        atualizar.addActionListener(e -> carregarAtividades());
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
        List<Object[]> disciplinas = new DisciplinaDAO().buscarDisciplinasPorUsuario(idUsuario);
        for (Object[] disciplina : disciplinas) {
            campoDisciplina.addItem(new DisciplinaItem(
                    ((Number) disciplina[0]).intValue(), (String) disciplina[1]
            ));
        }
    }

    private void preencherCamposSelecionados(ListSelectionEvent evento) {
        if (evento.getValueIsAdjusting() || tabela.getSelectedRow() < 0) {
            return;
        }
        int linha = tabela.getSelectedRow();
        selecionarDisciplina(((Number) modelo.getValueAt(linha, 1)).intValue());
        campoTitulo.setText(textoDaCelula(linha, 3));
        campoTipo.setText(textoDaCelula(linha, 4));
        campoData.setText(textoDaCelula(linha, 5));
        campoDescricao.setText(textoDaCelula(linha, 6));
        campoObservacao.setText(textoDaCelula(linha, 7));
        campoStatus.setSelectedItem(textoDaCelula(linha, 8));
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
            aviso("Cadastre uma disciplina antes de cadastrar uma atividade.");
            return false;
        }
        if (campoTitulo.getText().trim().isBlank() || campoTipo.getText().trim().isBlank()
                || campoData.getText().trim().isBlank() || campoDescricao.getText().trim().isBlank()) {
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
        boolean cadastrada = atividadeDAO.cadastrarAtividade(
                idUsuario, disciplinaSelecionada().getId(), campoTitulo.getText().trim(), campoTipo.getText().trim(),
                campoData.getText().trim(), campoDescricao.getText().trim(), campoObservacao.getText().trim(),
                (String) campoStatus.getSelectedItem()
        );
        if (cadastrada) {
            JOptionPane.showMessageDialog(this, "Atividade cadastrada com sucesso.");
            limparCampos();
            carregarAtividades();
        } else erro("Não foi possível cadastrar a atividade.");
    }

    private void editar() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            aviso("Selecione uma atividade na tabela para editar.");
            return;
        }
        if (!camposValidos()) return;
        int idAtividade = ((Number) modelo.getValueAt(linha, 0)).intValue();
        boolean editada = atividadeDAO.editarAtividade(
                idAtividade, idUsuario, disciplinaSelecionada().getId(), campoTitulo.getText().trim(),
                campoTipo.getText().trim(), campoData.getText().trim(), campoDescricao.getText().trim(),
                campoObservacao.getText().trim(), (String) campoStatus.getSelectedItem()
        );
        if (editada) {
            JOptionPane.showMessageDialog(this, "Atividade atualizada com sucesso.");
            limparCampos();
            carregarAtividades();
        } else erro("Não foi possível atualizar a atividade.");
    }

    private void alterarStatus(String status) {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            aviso("Selecione uma atividade na tabela.");
            return;
        }
        int idAtividade = ((Number) modelo.getValueAt(linha, 0)).intValue();
        if (atividadeDAO.alterarStatus(idAtividade, idUsuario, status)) {
            JOptionPane.showMessageDialog(this, "Status alterado para " + status + ".");
            carregarAtividades();
        } else erro("Não foi possível alterar o status da atividade.");
    }

    private void excluir() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            aviso("Selecione uma atividade na tabela para excluir.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Deseja excluir a atividade selecionada?", "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        int idAtividade = ((Number) modelo.getValueAt(linha, 0)).intValue();
        if (atividadeDAO.excluirAtividade(idAtividade, idUsuario)) {
            JOptionPane.showMessageDialog(this, "Atividade excluída com sucesso.");
            limparCampos();
            carregarAtividades();
        } else erro("Não foi possível excluir a atividade.");
    }

    private DisciplinaItem disciplinaSelecionada() {
        return (DisciplinaItem) campoDisciplina.getSelectedItem();
    }

    private void carregarAtividades() {
        modelo.setRowCount(0);
        for (Object[] atividade : atividadeDAO.buscarAtividadesPorUsuario(idUsuario)) {
            modelo.addRow(atividade);
        }
    }

    private String textoDaCelula(int linha, int coluna) {
        Object valor = modelo.getValueAt(linha, coluna);
        return valor == null ? "" : valor.toString();
    }

    private void limparCampos() {
        tabela.clearSelection();
        campoTitulo.setText("");
        campoTipo.setText("");
        campoData.setText("");
        campoDescricao.setText("");
        campoObservacao.setText("");
        campoStatus.setSelectedIndex(0);
    }

    private void aviso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Atividades", JOptionPane.WARNING_MESSAGE);
    }

    private void erro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
