BEGIN TRY
    BEGIN TRANSACTION;

    ALTER TABLE dbo.Lembrete ADD id_usuario INT NULL;

    EXEC(N'
        UPDATE l
        SET l.id_usuario = d.id_usuario
        FROM dbo.Lembrete l
        INNER JOIN dbo.Atividade a ON a.id_atividade = l.id_item
        INNER JOIN dbo.Disciplina d ON d.id_disciplina = a.id_disciplina
        WHERE l.tipo_item = ''ATIVIDADE''
          AND l.id_usuario IS NULL;
    ');

    EXEC(N'
        IF EXISTS (SELECT 1 FROM dbo.Lembrete WHERE id_usuario IS NULL)
            THROW 50001, ''Existem lembretes sem usuário associado.'', 1;
    ');

    EXEC(N'ALTER TABLE dbo.Lembrete ALTER COLUMN id_usuario INT NOT NULL;');
    EXEC(N'ALTER TABLE dbo.Lembrete ADD CONSTRAINT FK_Lembrete_Usuario
            FOREIGN KEY (id_usuario) REFERENCES dbo.Usuario(id_usuario);');

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0
        ROLLBACK TRANSACTION;

    THROW;
END CATCH;
