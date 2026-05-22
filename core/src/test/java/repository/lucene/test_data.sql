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
INSERT INTO projects(name) VALUES ('Issue Tracker Project');

-- Users
-- id=1: tester
-- id=2: dev1  (NPE / null pointer)
-- id=3: dev2  (Database / SQL / connection)
-- id=4: pl
-- id=5: dev3  (UI / frontend / rendering)
-- id=6: dev4  (Performance / memory / timeout)
-- id=7: dev5  (Security / auth / authorization)
INSERT INTO users(login_id, password, user_role) VALUES ('tester1', '1234', 'TESTER');
INSERT INTO users(login_id, password, user_role) VALUES ('dev1',    '1234', 'DEV');
INSERT INTO users(login_id, password, user_role) VALUES ('dev2',    '1234', 'DEV');
INSERT INTO users(login_id, password, user_role) VALUES ('pl1',     '1234', 'PL');
INSERT INTO users(login_id, password, user_role) VALUES ('dev3',    '1234', 'DEV');
INSERT INTO users(login_id, password, user_role) VALUES ('dev4',    '1234', 'DEV');
INSERT INTO users(login_id, password, user_role) VALUES ('dev5',    '1234', 'DEV');

-- =============================================
-- Past fixed issues (Lucene index source)
-- =============================================

-- dev1 (id=2): NullPointerException / NPE
INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'Login NullPointerException', 'NullPointerException thrown during login authentication null pointer error', 'MAJOR', 'FIXED', 1, 2, 2, '2026-01-01T10:00:00');

INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'Registration NPE on submit', 'NullPointerException NPE null pointer when submitting registration form', 'MAJOR', 'FIXED', 1, 2, 2, '2026-01-02T10:00:00');

INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'Session null pointer crash', 'NullPointerException null pointer session object uninitialized NPE crash', 'CRITICAL', 'FIXED', 1, 2, 2, '2026-01-03T10:00:00');

INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'Cart NPE null reference', 'NullPointerException null reference exception in cart service NPE item list', 'MAJOR', 'FIXED', 1, 2, 2, '2026-01-04T10:00:00');

-- dev2 (id=3): Database / SQL / connection
INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'Database connection timeout', 'database connection timeout DB server unreachable connection pool exhausted', 'CRITICAL', 'FIXED', 1, 3, 3, '2026-01-05T10:00:00');

INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'SQL query execution failure', 'database SQL query execution failed invalid syntax DB error', 'MAJOR', 'FIXED', 1, 3, 3, '2026-01-06T10:00:00');

INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'DB transaction deadlock', 'database transaction deadlock DB concurrent write lock conflict SQL', 'CRITICAL', 'FIXED', 1, 3, 3, '2026-01-07T10:00:00');

INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'Database index missing', 'database index missing slow query performance DB table scan SQL', 'MAJOR', 'FIXED', 1, 3, 3, '2026-01-08T10:00:00');

-- dev3 (id=5): UI / frontend / rendering
INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'Button rendering glitch', 'UI button rendering glitch frontend CSS style broken visual artifact', 'MINOR', 'FIXED', 1, 5, 5, '2026-01-09T10:00:00');

INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'CSS layout broken on mobile', 'UI CSS layout broken mobile responsive frontend rendering misaligned', 'MAJOR', 'FIXED', 1, 5, 5, '2026-01-10T10:00:00');

INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'Modal dialog not closing', 'UI modal dialog not closing frontend JavaScript event listener rendering issue', 'MAJOR', 'FIXED', 1, 5, 5, '2026-01-11T10:00:00');

-- dev4 (id=6): Performance / memory / latency
INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'Memory leak in background job', 'memory leak heap out of memory performance degradation background job', 'CRITICAL', 'FIXED', 1, 6, 6, '2026-01-12T10:00:00');

INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'High CPU usage performance issue', 'performance CPU high usage memory bottleneck latency spike throughput', 'MAJOR', 'FIXED', 1, 6, 6, '2026-01-13T10:00:00');

INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'Response latency spike', 'latency spike performance slow response memory allocation CPU profiling', 'MAJOR', 'FIXED', 1, 6, 6, '2026-01-14T10:00:00');

-- dev5 (id=7): Security / authentication / authorization
INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'SQL injection vulnerability', 'security SQL injection vulnerability authentication bypass exploit', 'CRITICAL', 'FIXED', 1, 7, 7, '2026-01-15T10:00:00');

INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'Unauthorized access to admin page', 'security authorization unauthorized access admin privilege escalation', 'CRITICAL', 'FIXED', 1, 7, 7, '2026-01-16T10:00:00');

INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'Session token not invalidated on logout', 'security session token authentication logout invalidation vulnerability', 'MAJOR', 'FIXED', 1, 7, 7, '2026-01-17T10:00:00');

-- =============================================
-- New issues (no fixer) - recommendation targets
-- id=18: NPE   -> expect dev1 (id=2)
-- id=19: DB    -> expect dev2 (id=3)
-- id=20: UI    -> expect dev3 (id=5)
-- id=21: Perf  -> expect dev4 (id=6)
-- id=22: Sec   -> expect dev5 (id=7)
-- =============================================

INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'NPE on checkout page', 'NullPointerException null pointer thrown during checkout process NPE', 'MAJOR', 'NEW', 1, NULL, NULL, '2026-02-01T10:00:00');

INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'Database connection refused', 'database connection refused DB server not responding connection error', 'CRITICAL', 'NEW', 1, NULL, NULL, '2026-02-02T10:00:00');

INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'Dropdown menu rendering bug', 'UI dropdown menu rendering incorrectly frontend CSS visual glitch', 'MINOR', 'NEW', 1, NULL, NULL, '2026-02-03T10:00:00');

INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'Out of memory on large upload', 'memory out of memory performance heap allocation large file upload', 'CRITICAL', 'NEW', 1, NULL, NULL, '2026-02-04T10:00:00');

INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
VALUES (1, 'XSS vulnerability in comment field', 'security XSS cross-site scripting vulnerability authentication bypass', 'CRITICAL', 'NEW', 1, NULL, NULL, '2026-02-05T10:00:00')
