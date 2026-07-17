-- Chay script nay 1 lan sau khi merge code moi (borrow_histories.status)
USE hsf302_final_project;
GO

IF COL_LENGTH('borrow_histories', 'status') IS NULL
BEGIN
    ALTER TABLE borrow_histories ADD status NVARCHAR(50) NULL;
END
GO

IF COL_LENGTH('borrow_histories', 'issued_by') IS NULL
BEGIN
    ALTER TABLE borrow_histories ADD issued_by INT NULL;
END
GO

IF COL_LENGTH('borrow_histories', 'return_confirmed_by') IS NULL
BEGIN
    ALTER TABLE borrow_histories ADD return_confirmed_by INT NULL;
END
GO

UPDATE borrow_histories
SET status = 'RETURNED'
WHERE return_date IS NOT NULL
  AND (status IS NULL OR status = '');

UPDATE borrow_histories
SET status = 'OVERDUE'
WHERE return_date IS NULL
  AND due_date IS NOT NULL
  AND due_date < SYSDATETIME()
  AND (status IS NULL OR status = '');

UPDATE borrow_histories
SET status = 'BORROWING'
WHERE status IS NULL OR status = '';
GO

IF COL_LENGTH('borrow_histories', 'status') IS NOT NULL
BEGIN
    ALTER TABLE borrow_histories ALTER COLUMN status NVARCHAR(50) NOT NULL;
END
GO
