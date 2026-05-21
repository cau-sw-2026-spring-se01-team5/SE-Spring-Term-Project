-- Schema
CREATE TABLE IF NOT EXISTS projects (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    login_id TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    user_role TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS project_memberships (
    user_id INTEGER NOT NULL,
    project_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, project_id)
);

CREATE TABLE IF NOT EXISTS issues (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    project_id INTEGER NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    priority TEXT NOT NULL,
    status TEXT NOT NULL,
    reporter_id INTEGER NOT NULL,
    assignee_id INTEGER,
    fixer_id INTEGER,
    reported_date TEXT NOT NULL
);

-- Project
INSERT INTO projects(name) VALUES ('테스트 프로젝트');

-- Users  (id: 1=tester, 2=dev1, 3=dev2, 4=pl)
INSERT INTO users(login_id, password, user_role) VALUES ('tester1', '1234', 'TESTER');
INSERT INTO users(login_id, password, user_role) VALUES ('dev1',    '1234', 'DEV');
INSERT INTO users(login_id, password, user_role) VALUES ('dev2',    '1234', 'DEV');
INSERT INTO users(login_id, password, user_role) VALUES ('pl1',     '1234', 'PL');

-- Past fixed issues — used for Lucene indexing
-- NPE 관련: fixer = dev1 (id=2)
INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'Login NullPointerException', 'NullPointerException NPE null pointer login error', 'MAJOR', 'FIXED', 1, 2, 2, '2026-01-01T10:00:00');

INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'Registration NPE error', 'NullPointerException NPE null pointer registration signup', 'MAJOR', 'FIXED', 1, 2, 2, '2026-01-02T10:00:00');

-- DB 관련: fixer = dev2 (id=3)
INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'Database connection timeout', 'database connection timeout DB error unavailable', 'CRITICAL', 'FIXED', 1, 3, 3, '2026-01-03T10:00:00');

INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'Database query failure', 'database query execution SQL DB failure error', 'MAJOR', 'FIXED', 1, 3, 3, '2026-01-04T10:00:00');

-- 추천 대상 이슈 (fixer 없음)
-- issue id=5: NPE 관련 → dev1 추천 기대
INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'NPE on login page', 'NullPointerException null pointer login', 'MAJOR', 'NEW', 1, NULL, NULL, '2026-02-01T10:00:00');

-- issue id=6: DB 관련 → dev2 추천 기대
INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'DB connection refused', 'database connection refused DB unavailable', 'CRITICAL', 'NEW', 1, NULL, NULL, '2026-02-02T10:00:00')
