CREATE DATABASE dbunit;
SELECT Name from sys.Databases;
GO

CREATE LOGIN dbunit WITH PASSWORD = 'dbunit', CHECK_POLICY = OFF;
GO

USE dbunit;
GO

CREATE USER dbunit FOR LOGIN dbunit;
GO

EXEC sp_addrolemember 'db_owner', 'dbunit';
GO