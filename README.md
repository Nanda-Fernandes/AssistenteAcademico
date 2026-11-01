# Assistente Acadêmico

Aplicação desktop para estudantes organizarem disciplinas e acompanharem atividades, provas e sessões de estudo em um só lugar.

## Funcionalidades

- Cadastro e autenticação de alunos.
- Dashboard com resumo acadêmico, próximos compromissos e prioridades por data.
- Cadastro, consulta, edição, exclusão, pesquisa e filtros de disciplinas, atividades, provas e estudos.
- Acompanhamento de status para atividades e estudos.
- Associação dos registros às disciplinas e isolamento dos dados por usuário.

## Tecnologias

- Java 25
- Swing
- Maven
- JDBC
- Microsoft SQL Server
- Driver Microsoft JDBC para SQL Server (`mssql-jdbc`)

## Requisitos

- JDK 25 instalado e configurado.
- Maven instalado, ou IntelliJ IDEA com suporte a Maven.
- Uma instância acessível do Microsoft SQL Server, com autenticação SQL habilitada para o usuário da aplicação.
- Permissão para criar o banco e as tabelas durante a configuração inicial.

## Configuração do banco de dados

1. No SQL Server Management Studio (SSMS) ou ferramenta equivalente, crie o banco:

   ```sql
   CREATE DATABASE AssistenteAcademico;
   ```

2. Selecione o banco `AssistenteAcademico` e execute [`database/schema.sql`](database/schema.sql). O script cria as tabelas `Usuario`, `Disciplina`, `Atividade`, `Prova` e `Estudo`, além das chaves primárias, chaves estrangeiras e unicidade do e-mail.

3. Crie ou escolha um login SQL para a aplicação. Depois de criar o login no servidor e selecionar o banco `AssistenteAcademico`, crie o usuário do banco correspondente e conceda acesso aos dados:

   ```sql
   CREATE USER [assistente_academico] FOR LOGIN [assistente_academico];
   GRANT SELECT, INSERT, UPDATE, DELETE ON SCHEMA::dbo TO [assistente_academico];
   ```

   Execute `CREATE USER` somente se o usuário ainda não existir no banco. Se usar outro login, substitua os dois nomes pelo login escolhido e configure `ASSISTENTE_DB_USER` com esse mesmo nome. Para desenvolvimento, a função `db_owner` também funciona; fora desse contexto, prefira as permissões limitadas acima.

O programa espera o SQL Server em `127.0.0.1:1433` por padrão. Configure as variáveis de ambiente:

| Variável | Obrigatória | Padrão | Uso |
|---|---:|---|---|
| `ASSISTENTE_DB_PASSWORD` | Sim | Sem padrão | Senha do login do SQL Server. |
| `ASSISTENTE_DB_USER` | Não | `assistente_academico` | Login do SQL Server. |
| `ASSISTENTE_DB_URL` | Não | `jdbc:sqlserver://127.0.0.1:1433;databaseName=AssistenteAcademico;encrypt=false;loginTimeout=5` | URL JDBC completa para a instância e o banco. |

Exemplo no PowerShell:

```powershell
$env:ASSISTENTE_DB_USER = "assistente_academico"
$env:ASSISTENTE_DB_PASSWORD = "informe-a-senha-localmente"
$env:ASSISTENTE_DB_URL = "jdbc:sqlserver://127.0.0.1:1433;databaseName=AssistenteAcademico;encrypt=false;loginTimeout=5"
```

No IntelliJ IDEA, configure os mesmos nomes em **Run > Edit Configurations > Environment variables**. Não coloque senhas no código, no README ou em arquivos versionados. O valor `encrypt=false` é o padrão para desenvolvimento local; em ambientes compartilhados ou de produção, configure TLS e a validação do certificado conforme a política do servidor.

### Bancos existentes

`database/migrations/001_associar_lembrete_ao_usuario.sql` é uma migração para uma instalação antiga que já tenha a tabela `dbo.Lembrete` e dados de atividades a associar aos usuários. Ela não faz parte da configuração de um banco novo e não deve ser executada sobre o esquema inicial acima: a aplicação atual não usa a tabela `Lembrete`.

## Compilar e executar

Na raiz do projeto:

```powershell
mvn clean package
```

Para iniciar a interface, abra o projeto no IntelliJ IDEA, aguarde a importação das dependências Maven, configure as variáveis de ambiente e execute `br.com.assistenteacademico.Main`.

## Primeiro acesso

Não há uma conta de teste garantida em um banco recém-criado. Inicie o programa, selecione a opção de cadastro e crie um usuário. As credenciais cadastradas são gravadas no banco configurado.

## Estrutura do projeto

```text
.
├── database/
│   ├── migrations/    Migrações para instalações existentes
│   └── schema.sql     Esquema inicial para uma instalação nova
├── src/main/
│   ├── java/          Aplicação Swing, DAOs e classes de domínio
│   └── resources/     Imagens usadas pela interface
├── pom.xml            Configuração Maven e dependência JDBC
└── README.md
```

As telas ficam em `br.com.assistenteacademico.view`. Os DAOs executam consultas parametrizadas com `PreparedStatement` e acessam o banco por meio da classe `Conexao`.

## Limitações e segurança

- As senhas são armazenadas em texto simples no estado atual do projeto. Não use credenciais reais ou dados sensíveis; o sistema ainda não é adequado para produção.
- O projeto não oferece recuperação de senha, notificações externas nem integração com calendários.
- O aplicativo depende de uma instância SQL Server configurada e acessível; o esquema e os dados não são incorporados ao executável.

## Licença

Não há uma licença de distribuição definida neste repositório. Consulte o responsável pelo projeto antes de reutilizar ou redistribuir o código.
