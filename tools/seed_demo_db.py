from pathlib import Path
import sqlite3


ROOT = Path(__file__).resolve().parents[1]
DB_PATH = ROOT / "test.db"
SCHEMA_PATH = ROOT / "core" / "src" / "main" / "java" / "repository" / "sqlite" / "schema.sql"


def main() -> None:
    connection = sqlite3.connect(DB_PATH)
    cursor = connection.cursor()
    cursor.executescript(SCHEMA_PATH.read_text(encoding="utf-8"))
    cursor.execute("PRAGMA foreign_keys = OFF")
    cursor.execute("DELETE FROM comments")
    cursor.execute("DELETE FROM issues")
    cursor.execute("DELETE FROM project_memberships")
    cursor.execute("DELETE FROM users")
    cursor.execute("DELETE FROM projects")
    cursor.execute("DELETE FROM sqlite_sequence")
    cursor.execute("PRAGMA foreign_keys = ON")

    projects = [
        (1, "project1"),
        (2, "project2"),
        (3, "project3"),
        (4, "project4"),
    ]
    for project_id, project_name in projects:
        cursor.execute("INSERT INTO projects(id, name) VALUES (?, ?)", (project_id, project_name))

    users = [
        ("admin", "1234", "ADMIN"),
        ("pl", "1234", "PL"),
        ("dev1", "1234", "DEV"),
        ("dev2", "1234", "DEV"),
        ("tester1", "1234", "TESTER"),
        ("tester2", "1234", "TESTER"),
        ("pl2", "1234", "PL"),
        ("dev3", "1234", "DEV"),
        ("dev4", "1234", "DEV"),
        ("tester3", "1234", "TESTER"),
        ("tester4", "1234", "TESTER"),
    ]

    for login_id, password, role in users:
        cursor.execute(
            "INSERT INTO users(login_id, password, user_role) VALUES (?, ?, ?)",
            (login_id, password, role),
        )

    cursor.execute("SELECT id, login_id FROM users")
    user_ids = {login_id: user_id for user_id, login_id in cursor.fetchall()}

    memberships = {
        1: ["admin", "pl", "dev1", "dev2", "tester1", "tester2"],
        2: ["admin", "pl2", "dev2", "dev3", "tester2", "tester3"],
        3: ["admin", "pl", "dev1", "dev4", "tester1", "tester4"],
        4: ["admin", "pl2", "dev3", "dev4", "tester3", "tester4"],
    }

    for project_id, members in memberships.items():
        for login_id in members:
            cursor.execute(
                "INSERT INTO project_memberships(user_id, project_id) VALUES (?, ?)",
                (user_ids[login_id], project_id),
            )

    issues = [
        {
            "title": "로그인 버튼 클릭 시 400 오류 발생",
            "description": "테스터가 로그인 버튼을 누르면 간헐적으로 400 오류 화면이 표시됩니다.",
            "priority": "CRITICAL",
            "status": "NEW",
            "reporter": "tester1",
            "assignee": None,
            "fixer": None,
            "reported_date": "2026-05-31T09:00:00",
            "comments": [
                ("tester1", "재현 경로: 로그인 화면에서 아이디 입력 후 바로 로그인 클릭"),
            ],
        },
        {
            "title": "대시보드 진입 후 화면이 멈추는 문제",
            "description": "프로젝트 선택 후 대시보드에 들어가면 로딩 이후 응답이 없습니다.",
            "priority": "MAJOR",
            "status": "ASSIGNED",
            "reporter": "tester2",
            "assignee": "dev1",
            "fixer": None,
            "reported_date": "2026-05-31T09:20:00",
            "comments": [
                ("tester2", "프로젝트 진입 직후 재현됩니다."),
                ("pl", "우선 dev1에게 배정했습니다."),
            ],
        },
        {
            "title": "파일 업로드 후 미리보기 이미지가 깨짐",
            "description": "이미지 업로드는 성공하지만 상세 화면의 미리보기가 정상적으로 보이지 않습니다.",
            "priority": "MINOR",
            "status": "FIXED",
            "reporter": "tester1",
            "assignee": "dev2",
            "fixer": "dev2",
            "reported_date": "2026-05-31T09:40:00",
            "comments": [
                ("tester1", "png 파일에서 특히 자주 발생합니다."),
                ("dev2", "이미지 경로 처리 로직을 수정했습니다."),
            ],
        },
        {
            "title": "이슈 검색 결과 정렬 순서가 올바르지 않음",
            "description": "키워드 검색 후 우선순위 기준 정렬이 기대와 다르게 동작합니다.",
            "priority": "MAJOR",
            "status": "RESOLVED",
            "reporter": "tester2",
            "assignee": "dev1",
            "fixer": "dev1",
            "reported_date": "2026-05-31T10:00:00",
            "comments": [
                ("pl", "검색 조건과 함께 확인 부탁드립니다."),
                ("dev1", "정렬 조건을 수정했습니다."),
                ("tester2", "테스트 결과 해결된 것으로 확인했습니다."),
            ],
        },
        {
            "title": "권한이 없는 사용자가 설정 화면에 접근 가능",
            "description": "특정 조건에서 권한이 없는 사용자도 설정 메뉴에 접근할 수 있습니다.",
            "priority": "CRITICAL",
            "status": "CLOSED",
            "reporter": "tester1",
            "assignee": "dev2",
            "fixer": "dev2",
            "reported_date": "2026-05-31T10:20:00",
            "comments": [
                ("tester1", "권한 없는 계정으로도 설정 버튼이 보였습니다."),
                ("dev2", "권한 체크 로직을 추가했습니다."),
                ("pl", "최종 검토 후 종료 처리했습니다."),
            ],
        },
        {
            "title": "프로젝트 사용자 목록에 중복 계정이 표시됨",
            "description": "프로젝트 사용자 탭에서 동일한 사용자가 두 번 보이는 경우가 있습니다.",
            "priority": "MINOR",
            "status": "NEW",
            "reporter": "tester2",
            "assignee": None,
            "fixer": None,
            "reported_date": "2026-05-31T10:40:00",
            "comments": [
                ("tester2", "새로고침 이후 중복 표시가 보입니다."),
            ],
        },
        {
            "title": "결제 승인 후 주문 상태가 갱신되지 않음",
            "description": "project2에서 결제 승인 이후 주문 상세 화면의 상태가 계속 대기중으로 남아 있습니다.",
            "priority": "CRITICAL",
            "status": "ASSIGNED",
            "reporter": "tester3",
            "assignee": "dev3",
            "fixer": None,
            "reported_date": "2026-05-31T11:00:00",
            "project_id": 2,
            "comments": [
                ("tester3", "결제는 성공하지만 상태가 변경되지 않습니다."),
                ("pl2", "dev3에게 우선 배정했습니다."),
            ],
        },
        {
            "title": "프로필 사진 변경 후 썸네일이 업데이트되지 않음",
            "description": "project2 사용자 설정에서 프로필 사진을 바꾼 뒤에도 목록의 썸네일은 이전 이미지로 남습니다.",
            "priority": "MINOR",
            "status": "NEW",
            "reporter": "tester2",
            "assignee": None,
            "fixer": None,
            "reported_date": "2026-05-31T11:15:00",
            "project_id": 2,
            "comments": [
                ("tester2", "브라우저 새로고침 이후에도 그대로입니다."),
            ],
        },
        {
            "title": "알림 센터 진입 시 최근 알림이 비어 보임",
            "description": "project3에서 알림 데이터는 존재하지만 첫 진입 시 빈 목록처럼 보입니다.",
            "priority": "MAJOR",
            "status": "FIXED",
            "reporter": "tester4",
            "assignee": "dev4",
            "fixer": "dev4",
            "reported_date": "2026-05-31T11:30:00",
            "project_id": 3,
            "comments": [
                ("tester4", "첫 진입에서만 재현됩니다."),
                ("dev4", "초기 렌더링 타이밍을 수정했습니다."),
            ],
        },
        {
            "title": "검색창 자동완성 목록이 너무 늦게 표시됨",
            "description": "project3 검색 입력 후 자동완성 목록이 3초 이상 늦게 나타납니다.",
            "priority": "MAJOR",
            "status": "RESOLVED",
            "reporter": "tester1",
            "assignee": "dev1",
            "fixer": "dev1",
            "reported_date": "2026-05-31T11:45:00",
            "project_id": 3,
            "comments": [
                ("pl", "성능 저하 원인을 먼저 확인해 주세요."),
                ("dev1", "쿼리 캐시를 적용했습니다."),
                ("tester1", "응답 속도가 개선된 것을 확인했습니다."),
            ],
        },
        {
            "title": "권한 변경 후 메뉴가 즉시 반영되지 않음",
            "description": "project4에서 사용자 역할을 변경해도 사이드 메뉴 권한이 바로 갱신되지 않습니다.",
            "priority": "CRITICAL",
            "status": "CLOSED",
            "reporter": "tester3",
            "assignee": "dev3",
            "fixer": "dev3",
            "reported_date": "2026-05-31T12:00:00",
            "project_id": 4,
            "comments": [
                ("tester3", "다시 로그인하기 전까지 이전 권한 메뉴가 보입니다."),
                ("dev3", "세션 갱신 시점을 조정했습니다."),
                ("pl2", "최종 확인 후 종료했습니다."),
            ],
        },
        {
            "title": "통계 화면 날짜 필터가 월말 데이터만 누락함",
            "description": "project4에서 월말 날짜가 포함된 통계 조회 시 마지막 날짜 데이터가 빠집니다.",
            "priority": "MAJOR",
            "status": "NEW",
            "reporter": "tester4",
            "assignee": None,
            "fixer": None,
            "reported_date": "2026-05-31T12:20:00",
            "project_id": 4,
            "comments": [
                ("tester4", "5월 31일 데이터가 통계에서 보이지 않습니다."),
            ],
        },
    ]

    for issue in issues:
        cursor.execute(
            """
            INSERT INTO issues(project_id, title, description, priority, status, reporter_id, assignee_id, fixer_id, reported_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
            (
                issue.get("project_id", 1),
                issue["title"],
                issue["description"],
                issue["priority"],
                issue["status"],
                user_ids[issue["reporter"]],
                user_ids[issue["assignee"]] if issue["assignee"] else None,
                user_ids[issue["fixer"]] if issue["fixer"] else None,
                issue["reported_date"],
            ),
        )
        issue_id = cursor.lastrowid
        for author, body in issue["comments"]:
            cursor.execute(
                "INSERT INTO comments(issue_id, author_id, body, created_at) VALUES (?, ?, ?, ?)",
                (issue_id, user_ids[author], body, issue["reported_date"]),
            )

    connection.commit()
    connection.close()


if __name__ == "__main__":
    main()
