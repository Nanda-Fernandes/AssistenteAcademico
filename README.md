# Assistente Acadêmico

## Descrição

Aplicação desktop para estudantes universitários organizarem disciplinas, atividades, provas, sessões de estudo e lembretes.

## Problema e objetivo

O sistema centraliza compromissos acadêmicos para reduzir esquecimentos de prazos e facilitar a priorização da rotina.

## Funcionalidades

- Cadastro e login de alunos
- Dashboard com resumo, próximos compromissos, provas, prioridades e lembretes
- CRUD de disciplinas, atividades, provas e estudos
- Alteração de status de atividades e estudos
- Cadastro, conclusão e exclusão de lembretes
- Isolamento dos dados pelo usuário autenticado

## Tecnologias

Java 25, Swing, Maven, JDBC, Microsoft SQL Server e IntelliJ IDEA.

## Banco de dados

O banco utilizado é `AssistenteAcademico`. A estrutura principal é:

```text
Usuario
  └── Disciplina
        ├── Atividade
        ├── Prova
        └── Estudo
```

Antes de usar lembretes, execute no SQL Server o script:

```text
database/migrations/001_associar_lembrete_ao_usuario.sql
```

Essa migração adiciona `id_usuario` à tabela `Lembrete`, associa os registros existentes de atividades aos respectivos usuários e cria a chave estrangeira. Ela é necessária para impedir que um aluno visualize lembretes de outro.

## Como executar

1. Crie o banco e as tabelas conforme o modelo do projeto.
2. Configure no arquivo `src/main/java/br/com/assistenteacademico/Conexao.java` o servidor, usuário e senha do SQL Server local.
3. Execute a classe `br.com.assistenteacademico.Main` pelo IntelliJ IDEA.

O Maven utiliza o driver `mssql-jdbc` definido no `pom.xml`.

## Login de teste

Se o usuário de teste ainda existir no banco:

```text
E-mail: teste@email.com
Senha: Teste123
```

Também é possível criar novos alunos pela tela de cadastro.

## Estrutura do projeto

```text
src/main/java/br/com/assistenteacademico
├── entidades e conexão
├── DAOs de persistência
└── view
    └── telas Swing
```

As telas usam DAOs, que acessam o SQL Server por JDBC com `PreparedStatement`.

## Limitações do MVP

- Senhas ainda são armazenadas conforme a estrutura acadêmica atual, sem hash.
- A conexão é configurada localmente.
- Não há notificações reais por e-mail, calendário ou celular.

## Melhorias futuras

Hash de senha, recuperação de acesso, notificações, calendário, estatísticas, integração com Google Calendar, aplicativo mobile e múltiplos tipos de usuário.
