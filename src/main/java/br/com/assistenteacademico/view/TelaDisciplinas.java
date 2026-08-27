package br.com.assistenteacademico.view;

import br.com.assistenteacademico.DisciplinaDAO;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaDisciplinas extends JFrame {

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

    public TelaDisciplinas(int idUsuario) {
        this.idUsuario = idUsuario;

        setTitle("Minhas Disciplinas");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] colunas = {"ID", "Disciplina", "Professor", "Curso", "Semestre", "Horário", "Sala", "Observação"};
        modelo = new DefaultTableModel(colunas, 0) {
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

        JPanel formulario = criarFormulario();
        JPanel botoes = criarBotoes();
        JPanel painelSuperior = new JPanel(new BorderLayout(10, 10));
        painelSuperior.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        painelSuperior.add(formulario, BorderLayout.CENTER);
        painelSuperior.add(botoes, BorderLayout.SOUTH);

        add(painelSuperior, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        carregarDisciplinas();
        setVisible(true);
    }

    private JPanel criarFormulario() {
        JPanel painel = new JPanel(new GridLayout(4, 4, 8, 8));
        adicionarCampo(painel, "Nome:*", campoNome);
        adicionarCampo(painel, "Professor:*", campoProfessor);
        adicionarCampo(painel, "Curso:*", campoCurso);
        adicionarCampo(painel, "Semestre:*", campoSemestre);
        adicionarCampo(painel, "Horário:*", campoHorario);
        adicionarCampo(painel, "Sala:*", campoSala);
        adicionarCampo(painel, "Observação:", campoObservacao);
        painel.add(new JLabel("* Campo obrigatório"));
        return painel;
    }

    private void adicionarCampo(JPanel painel, String rotulo, JTextField campo) {
        painel.add(new JLabel(rotulo));
        painel.add(campo);
    }

    private JPanel criarBotoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        JButton botaoCadastrar = new JButton("Cadastrar");
        JButton botaoEditar = new JButton("Editar");
        JButton botaoExcluir = new JButton("Excluir");
        JButton botaoAtualizar = new JButton("Atualizar lista");
        JButton botaoLimpar = new JButton("Limpar");
        JButton botaoVoltar = new JButton("Voltar");

        botaoCadastrar.addActionListener(e -> cadastrar());
        botaoEditar.addActionListener(e -> editar());
        botaoExcluir.addActionListener(e -> excluir());
        botaoAtualizar.addActionListener(e -> carregarDisciplinas());
        botaoLimpar.addActionListener(e -> limparCampos());
        botaoVoltar.addActionListener(e -> dispose());

        painel.add(botaoCadastrar);
        painel.add(botaoEditar);
        painel.add(botaoExcluir);
        painel.add(botaoAtualizar);
        painel.add(botaoLimpar);
        painel.add(botaoVoltar);
        return painel;
    }

    private void preencherCamposSelecionados(ListSelectionEvent evento) {
        if (evento.getValueIsAdjusting() || tabela.getSelectedRow() < 0) {
            return;
        }

        int linha = tabela.getSelectedRow();
        campoNome.setText(textoDaCelula(linha, 1));
        campoProfessor.setText(textoDaCelula(linha, 2));
        campoCurso.setText(textoDaCelula(linha, 3));
        campoSemestre.setText(textoDaCelula(linha, 4));
        campoHorario.setText(textoDaCelula(linha, 5));
        campoSala.setText(textoDaCelula(linha, 6));
        campoObservacao.setText(textoDaCelula(linha, 7));
    }

    private String textoDaCelula(int linha, int coluna) {
        Object valor = modelo.getValueAt(linha, coluna);
        return valor == null ? "" : valor.toString();
    }

    private boolean camposObrigatoriosPreenchidos() {
        if (campoNome.getText().trim().isBlank()) {
            mostrarAviso("Informe o nome da disciplina.");
            return false;
        }
        if (campoProfessor.getText().trim().isBlank()) {
            mostrarAviso("Informe o professor.");
            return false;
        }
        if (campoCurso.getText().trim().isBlank()) {
            mostrarAviso("Informe o curso.");
            return false;
        }
        if (campoSemestre.getText().trim().isBlank()) {
            mostrarAviso("Informe o semestre.");
            return false;
        }
        if (campoHorario.getText().trim().isBlank()) {
            mostrarAviso("Informe o horário.");
            return false;
        }
        if (campoSala.getText().trim().isBlank()) {
            mostrarAviso("Informe a sala.");
            return false;
        }
        return true;
    }

    private void cadastrar() {
        if (!camposObrigatoriosPreenchidos()) {
            return;
        }

        boolean cadastrado = disciplinaDAO.cadastrarDisciplina(
                idUsuario, campoNome.getText().trim(), campoProfessor.getText().trim(),
                campoCurso.getText().trim(), campoSemestre.getText().trim(),
                campoHorario.getText().trim(), campoSala.getText().trim(),
                campoObservacao.getText().trim()
        );

        if (cadastrado) {
            JOptionPane.showMessageDialog(this, "Disciplina cadastrada com sucesso.");
            limparCampos();
            carregarDisciplinas();
        } else {
            mostrarErro("Não foi possível cadastrar a disciplina.");
        }
    }

    private void editar() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            mostrarAviso("Selecione uma disciplina na tabela para editar.");
            return;
        }
        if (!camposObrigatoriosPreenchidos()) {
            return;
        }

        int idDisciplina = ((Number) modelo.getValueAt(linha, 0)).intValue();
        boolean editado = disciplinaDAO.editarDisciplina(
                idDisciplina, idUsuario, campoNome.getText().trim(), campoProfessor.getText().trim(),
                campoCurso.getText().trim(), campoSemestre.getText().trim(), campoHorario.getText().trim(),
                campoSala.getText().trim(), campoObservacao.getText().trim()
        );

        if (editado) {
            JOptionPane.showMessageDialog(this, "Disciplina atualizada com sucesso.");
            limparCampos();
            carregarDisciplinas();
        } else {
            mostrarErro("Não foi possível atualizar a disciplina.");
        }
    }

    private void excluir() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            mostrarAviso("Selecione uma disciplina na tabela para excluir.");
            return;
        }

        int idDisciplina = ((Number) modelo.getValueAt(linha, 0)).intValue();
        if (disciplinaDAO.possuiRegistrosRelacionados(idDisciplina, idUsuario)) {
            mostrarAviso("Esta disciplina possui atividades, provas ou estudos vinculados e não pode ser excluída.");
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(
                this, "Deseja excluir a disciplina selecionada?", "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
        );

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        if (disciplinaDAO.excluirDisciplina(idDisciplina, idUsuario)) {
            JOptionPane.showMessageDialog(this, "Disciplina excluída com sucesso.");
            limparCampos();
            carregarDisciplinas();
        } else {
            mostrarErro("Não foi possível excluir a disciplina.");
        }
    }

    private void carregarDisciplinas() {
        modelo.setRowCount(0);
        List<Object[]> disciplinas = disciplinaDAO.buscarDisciplinasPorUsuario(idUsuario);
        for (Object[] disciplina : disciplinas) {
            modelo.addRow(disciplina);
        }
    }

    private void limparCampos() {
        tabela.clearSelection();
        campoNome.setText("");
        campoProfessor.setText("");
        campoCurso.setText("");
        campoSemestre.setText("");
        campoHorario.setText("");
        campoSala.setText("");
        campoObservacao.setText("");
    }

    private void mostrarAviso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Disciplinas", JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
