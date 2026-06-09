DROP SCHEMA IF EXISTS `language_school` ;
CREATE SCHEMA IF NOT EXISTS `language_school` ;

-- -----------------------------------------------------
-- TABLES
-- -----------------------------------------------------
DROP TABLE IF EXISTS `language_school`.`Insegnante` ;
CREATE TABLE `language_school`.`Insegnante` (
    `Id` INT NOT NULL AUTO_INCREMENT,
    `Nome` VARCHAR(45) NOT NULL,
    `Cognome` VARCHAR(45) NOT NULL,
    `NazioneProvenienza` VARCHAR(45) NOT NULL,
    `Via` VARCHAR(100) NOT NULL,
    `NumeroCivico` VARCHAR(5) NOT NULL,
    `Cap` VARCHAR(5) NOT NULL,
    `Citta` VARCHAR(45) NOT NULL,
    PRIMARY KEY (`Id`))
ENGINE = InnoDB;


DROP TABLE IF EXISTS `language_school`.`Livello` ;
CREATE TABLE `language_school`.`Livello` (
    `Nome` VARCHAR(45) NOT NULL,
    `Libro` VARCHAR(45) NOT NULL,
    `EsameObbligatorio` TINYINT NULL DEFAULT 0,
    PRIMARY KEY (`Nome`))
ENGINE = InnoDB;


DROP TABLE IF EXISTS `language_school`.`Corso` ;
CREATE TABLE `language_school`.`Corso` (
    `NomeLivello` VARCHAR(45) NOT NULL,
    `Codice` INT UNSIGNED NOT NULL,
    `DataAttivazione` DATE NOT NULL,
    `NumAllievi` INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY (`NomeLivello`, `Codice`),
    INDEX `fk_Corso_Livello1_idx` (`NomeLivello` ASC),
    CONSTRAINT `fk_Corso_Livello1`
        FOREIGN KEY (`NomeLivello`)
        REFERENCES `language_school`.`Livello` (`Nome`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION)
ENGINE = InnoDB;


DROP TABLE IF EXISTS `language_school`.`Lezione` ;
CREATE TABLE `language_school`.`Lezione` (
    `Codice` INT NOT NULL AUTO_INCREMENT,
    `Data` DATE NOT NULL,
    `OraInizio` TIME NOT NULL,
    `OraFine` TIME NOT NULL,
    `IdInsegnante` INT NOT NULL,
    `NomeLivelloCorso` VARCHAR(45) NOT NULL,
    `CodiceCorso` INT UNSIGNED NOT NULL,
    PRIMARY KEY (`Codice`),
    INDEX `fk_Lezione_Insegnante1_idx` (`IdInsegnante` ASC),
    INDEX `fk_Lezione_Corso1_idx` (`NomeLivelloCorso` ASC, `CodiceCorso` ASC),
    INDEX `idx_insegnante_data` USING BTREE (`IdInsegnante`, `Data`),
    CONSTRAINT `fk_Lezione_Insegnante1`
        FOREIGN KEY (`IdInsegnante`)
        REFERENCES `language_school`.`Insegnante` (`Id`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION,
    CONSTRAINT `fk_Lezione_Corso1`
        FOREIGN KEY (`NomeLivelloCorso` , `CodiceCorso`)
        REFERENCES `language_school`.`Corso` (`NomeLivello` , `Codice`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION)
ENGINE = InnoDB;


DROP TABLE IF EXISTS `language_school`.`Allievo` ;
CREATE TABLE `language_school`.`Allievo` (
    `Id` INT NOT NULL AUTO_INCREMENT,
    `Nome` VARCHAR(45) NOT NULL,
    `Cognome` VARCHAR(45) NOT NULL,
    `Telefono` VARCHAR(20) NULL,
    `Email` VARCHAR(45) NULL,
    `NomeLivelloCorso` VARCHAR(45) NOT NULL,
    `CodiceCorso` INT UNSIGNED NOT NULL,
    `DataIscrizione` DATE NOT NULL,
    PRIMARY KEY (`Id`),
    INDEX `fk_Allievo_Corso1_idx` (`NomeLivelloCorso` ASC, `CodiceCorso` ASC),
    CONSTRAINT `fk_Allievo_Corso1`
        FOREIGN KEY (`NomeLivelloCorso` , `CodiceCorso`)
        REFERENCES `language_school`.`Corso` (`NomeLivello` , `Codice`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION)
ENGINE = InnoDB;


DROP TABLE IF EXISTS `language_school`.`Assenza` ;
CREATE TABLE `language_school`.`Assenza` (
    `IdAllievo` INT NOT NULL,
    `CodiceLezione` INT NOT NULL,
    PRIMARY KEY (`IdAllievo`, `CodiceLezione`),
    INDEX `fk_Assenza_Lezione1_idx` (`CodiceLezione` ASC),
    CONSTRAINT `fk_Assenza_Allievo`
        FOREIGN KEY (`IdAllievo`)
        REFERENCES `language_school`.`Allievo` (`Id`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION,
    CONSTRAINT `fk_Assenza_Lezione1`
        FOREIGN KEY (`CodiceLezione`)
        REFERENCES `language_school`.`Lezione` (`Codice`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION)
ENGINE = InnoDB;


DROP TABLE IF EXISTS `language_school`.`Utente` ;
CREATE TABLE `language_school`.`Utente` (
    `Username` VARCHAR(45) NOT NULL,
    `Password` VARCHAR(32) NOT NULL,
    `Ruolo` ENUM('segreteria', 'insegnante') NOT NULL,
    `IdInsegnante` INT NULL,
    PRIMARY KEY (`Username`),
    INDEX `fk_Utente_Insegnante1_idx` (`IdInsegnante` ASC),
    CONSTRAINT `fk_Utente_Insegnante1_idx`
        FOREIGN KEY (`IdInsegnante`)
        REFERENCES `language_school`.`Insegnante` (`Id`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- PROCEDURES
-- -----------------------------------------------------

DELIMITER $$


DROP PROCEDURE IF EXISTS `language_school`.`attiva_corso` $$
CREATE PROCEDURE `language_school`.`attiva_corso`(
    in var_nomeLivelloCorso VARCHAR(45),
    in var_lezioni JSON)
BEGIN
    declare var_codiceCorso INT;

    declare exit handler for SQLEXCEPTION
        begin
            rollback;
            resignal;
        end;

    set transaction isolation level serializable;
    start transaction;
        select coalesce(max(`Corso`.`Codice`), 0) + 1
        into var_codiceCorso
        from `language_school`.`Corso`
        where `Corso`.`NomeLivello` = var_nomeLivelloCorso;

        -- Inserimento del corso
        insert into `language_school`.`Corso` (`NomeLivello`, `Codice`, `DataAttivazione`, `NumAllievi`)
            values (var_nomeLivelloCorso, var_codiceCorso + 1, CURDATE(), 0);

        -- Inserimento delle lezioni
        insert into `language_school`.`Lezione` (`Data`, `OraInizio`, `OraFine`, `IdInsegnante`, `NomeLivelloCorso`, `CodiceCorso`)
            select
                `l`.`data`,
                `l`.`ora_inizio`,
                `l`.`ora_fine`,
                `l`.`id_insegnante`,
                var_nomeLivelloCorso,
                var_codiceCorso
            from JSON_TABLE(var_lezioni, '$[*]' COLUMNS (
                `data` DATE path '$.data',
                `ora_inizio` TIME path '$.oraInizio',
                `ora_fine` TIME path '$.oraFine',
                `id_insegnante` INT path '$.idInsegnante'
                )) as `l`;

    commit;
END$$


DROP PROCEDURE IF EXISTS `language_school`.`iscrivi_allievo` $$
CREATE PROCEDURE `language_school`.`iscrivi_allievo`(
    IN var_nome VARCHAR(45),
    IN var_cognome VARCHAR(45),
    IN var_telefono VARCHAR(20),
    IN var_email VARCHAR(45),
    IN var_nomeLivelloCorso VARCHAR(45),
    IN var_codiceCorso INT)
BEGIN
    declare exit handler for SQLEXCEPTION
    begin
        rollback;
        resignal;
    end;

    set transaction isolation level repeatable read;
    start transaction;
        insert into `language_school`.Allievo (`Nome`, `Cognome`, `Telefono`, `Email`, `NomeLivelloCorso`, `CodiceCorso`, `DataIscrizione`)
            values (var_nome, var_cognome, var_telefono, var_email, var_nomeLivelloCorso, var_codiceCorso, CURRENT_DATE());
    commit;
END$$

DROP PROCEDURE IF EXISTS `language_school`.`report_lezioni_svolte` $$
CREATE PROCEDURE `language_school`.`report_lezioni_svolte`(
    in var_mese INT,
    in var_anno INT)
BEGIN
    declare exit handler for SQLEXCEPTION
    begin
        rollback;
        resignal;
    end;

    set transaction isolation level read committed;
    start transaction read only;
        select `Insegnante`.`Id` as `IdInsegnante`,
               `Insegnante`.`Nome`,
               `Insegnante`.`Cognome`,
               `Lezione`.`Codice`,
               `Lezione`.`Data`,
               `Lezione`.`OraInizio`,
               `Lezione`.`OraFine`,
               `Lezione`.`NomeLivelloCorso`,
               `Lezione`.`CodiceCorso`
        from `language_school`.`Lezione`
            join `language_school`.`Insegnante` on `Insegnante`.`Id` = `Lezione`.`IdInsegnante`
        where MONTH(`Lezione`.`Data`) = var_mese
            and YEAR(`Lezione`.`Data`) = var_anno
        order by
            `Insegnante`.`Cognome`,
            `Insegnante`.`Nome`,
            `Lezione`.`Data`,
            `Lezione`.`OraInizio`;
    commit;
END$$

DROP PROCEDURE IF EXISTS `language_school`.`lista_corsi` $$
CREATE PROCEDURE `language_school`.`lista_corsi`()
BEGIN
    declare exit handler for SQLEXCEPTION
    begin
        rollback;
        resignal;
    end;

    set transaction isolation level read committed;
    start transaction read only;

        select `NomeLivello`, `Codice`, `DataAttivazione`, `NumAllievi`, `Livello`.`Libro`, `Livello`.`EsameObbligatorio`
        from `language_school`.`Corso`
            join `language_school`.`Livello` on `Corso`.`NomeLivello` = `Livello`.`Nome`;

    commit;
END$$


DROP PROCEDURE IF EXISTS `language_school`.`consulta_scheda_allievo` $$
CREATE PROCEDURE `language_school`.`consulta_scheda_allievo`(
    in var_idAllievo INT)
BEGIN
    declare exit handler for SQLEXCEPTION
    begin
        rollback;
        resignal;
    end;

    set transaction isolation level read committed;
    start transaction read only;

        select `Allievo`.`Id`, `Allievo`.`Nome`, `Allievo`.`Cognome`, `Allievo`.`Telefono`, `Allievo`.`Email`, `Allievo`.`DataIscrizione`, `Corso`.`NomeLivello`, `Corso`.`Codice`
        from `language_school`.`Allievo`
            join `language_school`.`Corso` on `Allievo`.`NomeLivelloCorso` = `Corso`.`NomeLivello` and `Allievo`.`CodiceCorso` = `Corso`.`Codice`
        where `Allievo`.Id = var_idAllievo;

        select `Lezione`.`Codice`, `Lezione`.`Data`, `Lezione`.`OraInizio`, `Lezione`.`OraFine`
        from `language_school`.`Assenza`
            join `language_school`.`Lezione` on `Assenza`.`CodiceLezione` = `Lezione`.`Codice`
        where `Assenza`.`IdAllievo` = var_idAllievo;

    commit;
END$$

DROP PROCEDURE IF EXISTS `language_school`.`report_agenda_settimanale` $$
CREATE PROCEDURE `language_school`.`report_agenda_settimanale`(
    in var_id_insegnante INT,
    in var_data DATE)
BEGIN
    declare exit handler for SQLEXCEPTION
    begin
        rollback;
        resignal;
    end;

    set transaction isolation level read committed;
    start transaction read only;
        select `Lezione`.`Codice`, `Lezione`.`Data`, `Lezione`.`OraInizio`, `Lezione`.`OraFine`, `Lezione`.`NomeLivelloCorso`, `Lezione`.`CodiceCorso`
        from `language_school`.`Lezione`
        where `Lezione`.`IdInsegnante` = var_id_insegnante
            and `Lezione`.`Data` >= var_data
            and `Lezione`.`Data` <= DATE_ADD(var_data, INTERVAL 7 DAY)
        order by
            `Lezione`.`Data`,
            `Lezione`.`OraInizio`;
    commit;
END$$

DROP PROCEDURE IF EXISTS `language_school`.`elenco_iscritti_corso` $$
CREATE PROCEDURE `language_school`.`elenco_iscritti_corso`(
    in var_id_insegnante INT,
    in var_nomeLivelloCorso VARCHAR(45),
    in var_codiceCorso INT)
BEGIN
    declare var_authorized INT;

    declare exit handler for SQLEXCEPTION
    begin
        rollback;
        resignal;
    end;

    select count(*) into var_authorized
    from `language_school`.`Lezione`
    where `Lezione`.`NomeLivelloCorso` = var_nomeLivelloCorso
        and `Lezione`.`CodiceCorso` = var_codiceCorso
        and `Lezione`.`IdInsegnante` = var_id_insegnante;

    if var_authorized = 0 then
        signal sqlstate '45000' set message_text = 'Insegnante non autorizzato ad accedere alla lista degli iscritti per questo corso';
    end if;

    set transaction isolation level read committed ;
    start transaction read only;
        select `Allievo`.`Id`, `Allievo`.`Nome`, `Allievo`.`Cognome`, `Allievo`.`Telefono`, `Allievo`.`Email`, `Allievo`.`DataIscrizione`
        from `language_school`.`Allievo`
        where `Allievo`.`NomeLivelloCorso` = var_nomeLivelloCorso
            and `Allievo`.`CodiceCorso` = var_codiceCorso;
    commit;

END$$

DROP PROCEDURE IF EXISTS `language_school`.`registra_assenza` $$
CREATE PROCEDURE `language_school`.`registra_assenza`(
    in var_id_insegnante INT,
    in var_id_allievo INT,
    in var_codice_lezione INT,
    out var_gia_presente TINYINT)
BEGIN
    declare var_authorized INT;
    declare var_assenza_esiste INT;

    declare exit handler for SQLEXCEPTION
    begin
        rollback;
        resignal;
    end;

    select count(*) into var_authorized
    from `language_school`.Lezione
    where Lezione.CodiceCorso = var_codice_lezione
        and Lezione.IdInsegnante = var_id_insegnante;

    if var_authorized = 0 then
        signal sqlstate '45000' set message_text = 'Insegnante non autorizzato a registrare un''assenza per questa lezione';
    end if;

    set transaction isolation level repeatable read;
    start transaction;
        select count(*) into var_assenza_esiste
        from `language_school`.`Assenza`
        where `IdAllievo` = var_id_allievo and `CodiceLezione` = var_codice_lezione;

        if var_assenza_esiste > 0 then
            -- l'assenza è già stata registrata
            set var_gia_presente = 1;
        else
            insert into `language_school`.`Assenza` (`IdAllievo`, `CodiceLezione`)
                values (var_id_allievo, var_codice_lezione);

            set var_gia_presente = 0;
        end if;
    commit;
END$$

DROP PROCEDURE IF EXISTS `language_school`.`login` $$
CREATE PROCEDURE `language_school`.`login`(
    in var_username VARCHAR(45),
    in var_password VARCHAR(32),
    out var_ruolo INT,
    out var_id_insegnante INT)
BEGIN
    declare var_user_role ENUM('segreteria', 'insegnante');
    declare var_user_id_insegnante INT;

    select `Ruolo`, `IdInsegnante` from `language_school`.Utente
        where `Username` = var_username
        and `Password` = md5(var_password)
        into var_user_role, var_user_id_insegnante;
    
    if var_user_role = 'segreteria' then
        set var_ruolo = 1;
    elseif var_user_role = 'insegnante' then
        set var_ruolo = 2;
        set var_id_insegnante = var_user_id_insegnante;
    else
        set var_ruolo = 0;
    end if;

END$$


-- -----------------------------------------------------
-- Trigger per regole aziendali
-- -----------------------------------------------------

-- Trigger per l'aggiornamento del numero di allievi nella tabella Corso
DROP TRIGGER IF EXISTS `language_school`.`check_allievo_after_insert` $$
CREATE TRIGGER `language_school`.`check_allievo_after_insert`
    AFTER INSERT ON `language_school`.`Allievo`
    FOR EACH ROW
BEGIN
    update `language_school`.`Corso`
    set `NumAllievi` = `NumAllievi` + 1
    where `NomeLivello` = NEW.NomeLivelloCorso and `Codice` = NEW.CodiceCorso;
END$$

DROP TRIGGER IF EXISTS `language_school`.`check_allievo_after_delete` $$
CREATE TRIGGER `language_school`.`check_allievo_after_delete`
    AFTER DELETE ON `language_school`.`Allievo`
    FOR EACH ROW
BEGIN
    update `language_school`.`Corso`
    set `NumAllievi` = `NumAllievi` - 1
    where `NomeLivello` = OLD.NomeLivelloCorso and `Codice` = OLD.CodiceCorso;
END$$

DROP TRIGGER IF EXISTS `language_school`.`check_allievo_after_update` $$
CREATE TRIGGER `language_school`.`check_allievo_after_update`
    AFTER UPDATE ON `language_school`.`Allievo`
    FOR EACH ROW
BEGIN
    -- Controlliamo se l'allievo ha cambiato corso
    if OLD.NomeLivelloCorso != NEW.NomeLivelloCorso or OLD.CodiceCorso != NEW.CodiceCorso then

        update `language_school`.`Corso`
        set `NumAllievi` = `NumAllievi` - 1
        where `NomeLivello` = OLD.NomeLivelloCorso and `Codice` = OLD.CodiceCorso;

        update `language_school`.`Corso`
        set `NumAllievi` = `NumAllievi` + 1
        where `NomeLivello` = NEW.NomeLivelloCorso and `Codice` = NEW.CodiceCorso;

    end if;
END$$


-- Trigger per la RV1 - L’allievo deve specificare almeno un recapito tra email e telefono
DROP TRIGGER IF EXISTS `language_school`.`check_recapito_before_insert` $$
CREATE TRIGGER `language_school`.`check_recapito_before_insert`
    BEFORE INSERT ON `language_school`.`Allievo`
    FOR EACH ROW
BEGIN
    if NEW.`Telefono` IS NULL and NEW.`Email` IS NULL then
        signal sqlstate '45000' set message_text = 'L''allievo deve specificare almeno un recapito tra email e telefono';
    end if;
END$$

DROP TRIGGER IF EXISTS `language_school`.`check_recapito_before_update` $$
CREATE TRIGGER `language_school`.`check_recapito_before_update`
    BEFORE UPDATE ON `language_school`.`Allievo`
    FOR EACH ROW
BEGIN
    if NEW.`Telefono` IS NULL and NEW.`Email` IS NULL then
        signal sqlstate '45000' set message_text = 'L''allievo deve specificare almeno un recapito tra email e telefono';
    end if;
END$$


-- Trigger per la RV3 - L’allievo non deve avere assenze associate a lezioni di corsi ai quali non è iscritto
DROP TRIGGER IF EXISTS `language_school`.`check_assenza_before_insert` $$
CREATE TRIGGER `language_school`.`check_assenza_before_insert`
    BEFORE INSERT ON `language_school`.`Assenza`
    FOR EACH ROW
BEGIN
    declare var_coerenza INT;

    select count(*)
    into var_coerenza
    from `language_school`.`Allievo`
             join `language_school`.`Lezione`
                  on `Lezione`.`NomeLivelloCorso` = `Allievo`.`NomeLivelloCorso`
                      and `Lezione`.`CodiceCorso` = `Allievo`.`CodiceCorso`
    where `Allievo`.`Id` = NEW.`IdAllievo`
      and `Lezione`.`Codice` = NEW.`CodiceLezione`;


    if var_coerenza = 0 then
        signal sqlstate '45000' set message_text = 'L''allievo non è iscritto al corso per il quale si sta registrando l''assenza';
    end if;
END$$

DROP TRIGGER IF EXISTS `language_school`.`check_assenza_before_update` $$
CREATE TRIGGER `language_school`.`check_assenza_before_update`
    BEFORE UPDATE ON `language_school`.`Assenza`
    FOR EACH ROW
BEGIN
    declare var_coerenza INT;

    select count(*)
    into var_coerenza
    from `language_school`.`Allievo`
             join `language_school`.`Lezione`
                  on `Lezione`.`NomeLivelloCorso` = `Allievo`.`NomeLivelloCorso`
                      and `Lezione`.`CodiceCorso` = `Allievo`.`CodiceCorso`
    where `Allievo`.`Id` = NEW.`IdAllievo`
      and `Lezione`.`Codice` = NEW.`CodiceLezione`;


    if var_coerenza = 0 then
        signal sqlstate '45000' set message_text = 'L''allievo non è iscritto al corso per il quale si sta registrando l''assenza';
    end if;
END$$


-- Trigger per la RV4 - L’ora di inizio di una lezione deve essere precedente alla relativa ora di fine
DROP TRIGGER IF EXISTS `language_school`.`check_orario_before_insert` $$
CREATE TRIGGER `language_school`.`check_orario_before_insert`
    BEFORE INSERT ON `language_school`.`Lezione`
    FOR EACH ROW
BEGIN
    if NEW.`OraInizio` >= NEW.`OraFine` then
        signal sqlstate '45000' set message_text = 'L''ora di inizio di una lezione deve essere precedente alla relativa ora di fine';
    end if;
END$$

DROP TRIGGER IF EXISTS `language_school`.`check_orario_before_update` $$
CREATE TRIGGER `language_school`.`check_orario_before_update`
    BEFORE UPDATE ON `language_school`.`Lezione`
    FOR EACH ROW
BEGIN
    if NEW.`OraInizio` >= NEW.`OraFine` then
        signal sqlstate '45000' set message_text = 'L''ora di inizio di una lezione deve essere precedente alla relativa ora di fine';
    end if;
END$$


-- Trigger per la RV5
-- L’attributo della tabella Utente che funge da chiave esterna verso la relazione Insegnante
-- deve presentare un valore non nullo se e solo se il ruolo dell’utente è ‘insegnante’.
DROP TRIGGER IF EXISTS `language_school`.`check_ruolo_utente` $$
CREATE TRIGGER `language_school`.`check_ruolo_utente`
    BEFORE INSERT ON `language_school`.`Utente`
    FOR EACH ROW
BEGIN
    if NEW.`Ruolo` = 'insegnante' and NEW.`IdInsegnante` IS NULL then
        signal sqlstate '45000' set message_text = 'Un utente con ruolo insegnante deve avere un IdInsegnante';
    elseif NEW.`Ruolo` <> 'insegnante' and NEW.`IdInsegnante` IS NOT NULL then
        signal sqlstate '45000' set message_text = 'Un utente con ruolo segreteria non può avere un IdInsegnante associato';
    end if;
END$$


-- Trigger per la RV2
-- L’insegnante non deve essere assegnato a due o più lezioni che presentano sovrapposizioni temporali nella stessa data
DROP TRIGGER IF EXISTS `language_school`.`check_sovrapposizione_lezioni_before_insert` $$
CREATE TRIGGER `language_school`.`check_sovrapposizione_lezioni_before_insert`
    BEFORE INSERT ON `language_school`.`Lezione`
    FOR EACH ROW
BEGIN
    declare var_codice_conflitto INT;
    declare var_oraInizio TIME;
    declare var_oraFine TIME;

    declare var_nome_insegnante VARCHAR(45);
    declare var_cognome_insegnante VARCHAR(45);

    declare var_msg_errore VARCHAR(255);

    declare continue handler for not found set var_codice_conflitto = NULL;

    -- Controlla se esistono già lezioni per quello stesso insegnante,
    -- nello stesso giorno, con orari che si intersecano
    select `Lezione`.`Codice`, `Lezione`.`OraInizio`, `Lezione`.`OraFine`
    into var_codice_conflitto, var_oraInizio, var_oraFine
    from `language_school`.`Lezione`
    where `Lezione`.`IdInsegnante` = NEW.IdInsegnante
        and `Lezione`.`Data` = NEW.Data
        and (`Lezione`.`OraInizio` < NEW.OraFine and `Lezione`.`OraFine` > NEW.OraInizio)
    limit 1;


    if var_codice_conflitto IS NOT NULL then
        -- è stata rilevata una sovrapposizione di orario
        select `Insegnante`.`Nome`, `Insegnante`.`Cognome`
        into var_nome_insegnante, var_cognome_insegnante
        from `language_school`.`Insegnante`
        where `Insegnante`.`Id` = NEW.IdInsegnante;

        set var_msg_errore = CONCAT(
            'L''insegnante ', var_nome_insegnante, ' ', var_cognome_insegnante,
            ' (ID: ', NEW.IdInsegnante, ') è già occupato/a il ', NEW.Data,
            ' dalle ', var_oraInizio, ' alle ', var_oraFine
        );
        signal sqlstate '45001' set message_text = var_msg_errore;
    end if;

END$$

DROP TRIGGER IF EXISTS `language_school`.`check_sovrapposizione_lezioni_before_update` $$
CREATE TRIGGER `language_school`.`check_sovrapposizione_lezioni_before_update`
    BEFORE UPDATE ON `language_school`.`Lezione`
    FOR EACH ROW
BEGIN
    declare var_codice_conflitto INT;
    declare var_oraInizio TIME;
    declare var_oraFine TIME;

    declare var_nome_insegnante VARCHAR(45);
    declare var_cognome_insegnante VARCHAR(45);

    declare var_msg_errore VARCHAR(255);

    declare continue handler for not found set var_codice_conflitto = NULL;

    select `Lezione`.`Codice`, `Lezione`.`OraInizio`, `Lezione`.`OraFine`
    into var_codice_conflitto, var_oraInizio, var_oraFine
    from `language_school`.`Lezione`
    where `Lezione`.`IdInsegnante` = NEW.IdInsegnante
        and `Lezione`.`Data` = NEW.Data
        and (`Lezione`.`OraInizio` < NEW.OraFine and `Lezione`.`OraFine` > NEW.OraInizio)
        and `Lezione`.`Codice` != NEW.Codice
    limit 1;


    if var_codice_conflitto IS NOT NULL then
        -- è stata rilevata una sovrapposizione di orario
        select `Insegnante`.`Nome`, `Insegnante`.`Cognome`
        into var_nome_insegnante, var_cognome_insegnante
        from `language_school`.`Insegnante`
        where `Insegnante`.`Id` = NEW.IdInsegnante;

        set var_msg_errore = CONCAT(
            'L''insegnante ', var_nome_insegnante, ' ', var_cognome_insegnante,
            ' (ID: ', NEW.IdInsegnante, ') è già occupato/a il ', NEW.Data,
            ' dalle ', var_oraInizio, ' alle ', var_oraFine
        );
        signal sqlstate '45001' set message_text = var_msg_errore;
    end if;

END$$


DELIMITER ;

-- -----------------------------------------------------
-- Users and privileges
-- -----------------------------------------------------

DROP USER IF EXISTS login;
CREATE USER 'login' IDENTIFIED BY 'login_password';
GRANT EXECUTE ON procedure `language_school`.`login` TO 'login';

DROP USER IF EXISTS segreteria;
CREATE USER 'segreteria' IDENTIFIED BY 'segreteria_password';
GRANT EXECUTE ON procedure `language_school`.`attiva_corso` TO 'segreteria';
GRANT EXECUTE ON procedure `language_school`.`iscrivi_allievo` TO 'segreteria';
GRANT EXECUTE ON procedure `language_school`.`report_lezioni_svolte` TO 'segreteria';
GRANT EXECUTE ON procedure `language_school`.`lista_corsi` TO 'segreteria';
GRANT EXECUTE ON procedure `language_school`.`consulta_scheda_allievo` TO 'segreteria';

DROP USER IF EXISTS insegnante;
CREATE USER 'insegnante' IDENTIFIED BY 'insegnante_password';
GRANT EXECUTE ON procedure `language_school`.`report_agenda_settimanale` TO 'insegnante';
GRANT EXECUTE ON procedure `language_school`.`elenco_iscritti_corso` TO 'insegnante';
GRANT EXECUTE ON procedure `language_school`.`registra_assenza` TO 'insegnante';


-- -----------------------------------------------------
-- DEFAULT DATA FOR THE EXAMPLE PROGRAM (Data Seeding)
-- -----------------------------------------------------
START TRANSACTION;

-- Livelli
INSERT INTO `language_school`.`Livello` (`Nome`, `Libro`, `EsameObbligatorio`) VALUES ('Elementary', 'English File Elementary', 0);
INSERT INTO `language_school`.`Livello` (`Nome`, `Libro`, `EsameObbligatorio`) VALUES ('Intermediate', 'Objective PET', 1);
INSERT INTO `language_school`.`Livello` (`Nome`, `Libro`, `EsameObbligatorio`) VALUES ('First Certificate', 'Objective First', 1);
INSERT INTO `language_school`.`Livello` (`Nome`, `Libro`, `EsameObbligatorio`) VALUES ('Advanced', 'Advanced Trainer', 1);
INSERT INTO `language_school`.`Livello` (`Nome`, `Libro`, `EsameObbligatorio`) VALUES ('Proficiency', 'Proficiency Trainer', 1);

-- Insegnanti
INSERT INTO `language_school`.`Insegnante` (`Nome`, `Cognome`, `NazioneProvenienza`, `Via`, `NumeroCivico`, `Cap`, `Citta`)
VALUES ('John', 'Smith', 'Regno Unito', 'Via Roma', '10', '00185', 'Roma'); -- Id 1

INSERT INTO `language_school`.`Insegnante` (`Nome`, `Cognome`, `NazioneProvenienza`, `Via`, `NumeroCivico`, `Cap`, `Citta`)
VALUES ('Sarah', 'Connor', 'Stati Uniti', 'Via Milano', '42', '00184', 'Roma'); -- Id 2

INSERT INTO `language_school`.`Insegnante` (`Nome`, `Cognome`, `NazioneProvenienza`, `Via`, `NumeroCivico`, `Cap`, `Citta`)
VALUES ('Mario', 'Rossi', 'Italia', 'Via Napoli', '5', '00183', 'Roma'); -- Id 3

-- Corsi
INSERT INTO `language_school`.`Corso` (`NomeLivello`, `Codice`, `DataAttivazione`, `NumAllievi`)
VALUES ('First Certificate', 1, '2026-06-01', 0);

INSERT INTO `language_school`.`Corso` (`NomeLivello`, `Codice`, `DataAttivazione`, `NumAllievi`)
VALUES ('Intermediate', 1, '2026-06-01', 0);

-- Lezioni
INSERT INTO `language_school`.`Lezione` (`Data`, `OraInizio`, `OraFine`, `IdInsegnante`, `NomeLivelloCorso`, `CodiceCorso`)
VALUES ('2026-06-15', '14:00:00', '16:00:00', 3, 'First Certificate', 1); -- Codice 1

INSERT INTO `language_school`.`Lezione` (`Data`, `OraInizio`, `OraFine`, `IdInsegnante`, `NomeLivelloCorso`, `CodiceCorso`)
VALUES ('2026-06-17', '14:00:00', '16:00:00', 1, 'First Certificate', 1); -- Codice 2

INSERT INTO `language_school`.`Lezione` (`Data`, `OraInizio`, `OraFine`, `IdInsegnante`, `NomeLivelloCorso`, `CodiceCorso`)
VALUES ('2026-06-16', '10:00:00', '12:00:00', 2, 'Intermediate', 1); -- Codice 3

-- Allievi
INSERT INTO `language_school`.`Allievo` (`Nome`, `Cognome`, `Telefono`, `Email`, `NomeLivelloCorso`, `CodiceCorso`, `DataIscrizione`)
VALUES ('Luca', 'Bianchi', '3331234567', 'luca.b@email.it', 'First Certificate', 1, '2026-06-02');

INSERT INTO `language_school`.`Allievo` (`Nome`, `Cognome`, `Telefono`, `Email`, `NomeLivelloCorso`, `CodiceCorso`, `DataIscrizione`)
VALUES ('Giulia', 'Verdi', NULL, 'giulia.v@email.it', 'First Certificate', 1, '2026-06-03');

INSERT INTO `language_school`.`Allievo` (`Nome`, `Cognome`, `Telefono`, `Email`, `NomeLivelloCorso`, `CodiceCorso`, `DataIscrizione`)
VALUES ('Marco', 'Neri', '3339876543', NULL, 'Intermediate', 1, '2026-06-03');

-- Assenze
INSERT INTO `language_school`.`Assenza` (`IdAllievo`, `CodiceLezione`)
VALUES (1, 1);

-- Utenti per il Login
INSERT INTO `language_school`.`Utente` (`Username`, `Password`, `Ruolo`, `IdInsegnante`)
VALUES ('segreteria_roma', MD5('admin123'), 'segreteria', NULL);

INSERT INTO `language_school`.`Utente` (`Username`, `Password`, `Ruolo`, `IdInsegnante`)
VALUES ('j.smith', MD5('password'), 'insegnante', 1);

INSERT INTO `language_school`.`Utente` (`Username`, `Password`, `Ruolo`, `IdInsegnante`)
VALUES ('s.connor', MD5('password'), 'insegnante', 2);

INSERT INTO `language_school`.`Utente` (`Username`, `Password`, `Ruolo`, `IdInsegnante`)
VALUES ('m.rossi', MD5('password'), 'insegnante', 3);

COMMIT;