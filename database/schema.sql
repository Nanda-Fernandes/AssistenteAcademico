CREATE TABLE dbo.Usuario (
    id_usuario INT IDENTITY(1,1) NOT NULL,
    nome NVARCHAR(150) NOT NULL,
    email NVARCHAR(254) NOT NULL,
    senha NVARCHAR(255) NOT NULL,
    curso NVARCHAR(150) NOT NULL,
    CONSTRAINT PK_Usuario PRIMARY KEY (id_usuario),
    CONSTRAINT UQ_Usuario_email UNIQUE (email)
);
GO

CREATE TABLE dbo.Disciplina (
    id_disciplina INT IDENTITY(1,1) NOT NULL,
    id_usuario INT NOT NULL,
    nome NVARCHAR(200) NOT NULL,
    professor NVARCHAR(200) NOT NULL,
    curso NVARCHAR(150) NOT NULL,
    semestre NVARCHAR(50) NOT NULL,
    horario NVARCHAR(100) NOT NULL,
    sala NVARCHAR(100) NOT NULL,
    observacao NVARCHAR(MAX) NULL,
    CONSTRAINT PK_Disciplina PRIMARY KEY (id_disciplina),
    CONSTRAINT FK_Disciplina_Usuario FOREIGN KEY (id_usuario)
        REFERENCES dbo.Usuario (id_usuario)
);
GO

CREATE TABLE dbo.Atividade (
    id_atividade INT IDENTITY(1,1) NOT NULL,
    id_disciplina INT NOT NULL,
    titulo NVARCHAR(250) NOT NULL,
    tipo NVARCHAR(100) NOT NULL,
    data_entrega DATE NOT NULL,
    descricao NVARCHAR(MAX) NOT NULL,
    observacao NVARCHAR(MAX) NULL,
    status NVARCHAR(40) NOT NULL,
    CONSTRAINT PK_Atividade PRIMARY KEY (id_atividade),
    CONSTRAINT FK_Atividade_Disciplina FOREIGN KEY (id_disciplina)
        REFERENCES dbo.Disciplina (id_disciplina)
);
GO

CREATE TABLE dbo.Prova (
    id_prova INT IDENTITY(1,1) NOT NULL,
    id_disciplina INT NOT NULL,
    data_prova DATE NOT NULL,
    conteudo NVARCHAR(MAX) NOT NULL,
    observacao NVARCHAR(MAX) NULL,
    CONSTRAINT PK_Prova PRIMARY KEY (id_prova),
    CONSTRAINT FK_Prova_Disciplina FOREIGN KEY (id_disciplina)
        REFERENCES dbo.Disciplina (id_disciplina)
);
GO

CREATE TABLE dbo.Estudo (
    id_estudo INT IDENTITY(1,1) NOT NULL,
    id_disciplina INT NOT NULL,
    o_que_estudar NVARCHAR(500) NOT NULL,
    data_estudo DATE NOT NULL,
    horario NVARCHAR(50) NOT NULL,
    observacao NVARCHAR(MAX) NULL,
    status NVARCHAR(40) NOT NULL,
    CONSTRAINT PK_Estudo PRIMARY KEY (id_estudo),
    CONSTRAINT FK_Estudo_Disciplina FOREIGN KEY (id_disciplina)
        REFERENCES dbo.Disciplina (id_disciplina)
);
GO
