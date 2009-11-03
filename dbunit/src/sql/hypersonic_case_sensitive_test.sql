CREATE TABLE "MixedCaseTable"
  (COL1 VARCHAR(32),
   PRIMARY KEY (COL1));
CREATE TABLE UPPER_CASE_TABLE
  (COL1 VARCHAR(32),
   PRIMARY KEY (COL1));

ALTER TABLE UPPER_CASE_TABLE ADD CONSTRAINT FK1 FOREIGN KEY (COL1) REFERENCES "MixedCaseTable" (COL1);