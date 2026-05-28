from pathlib import Path
from xml.sax.saxutils import escape
import zipfile


NS = (
    'xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main" '
    'xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships"'
)


def run(text, bold=False, size=21):
    text = escape(text)
    bold_xml = "<w:b/>" if bold else ""
    return (
        f"<w:r><w:rPr>{bold_xml}<w:sz w:val=\"{size}\"/>"
        f"<w:rFonts w:ascii=\"Malgun Gothic\" w:hAnsi=\"Malgun Gothic\" "
        f"w:eastAsia=\"Malgun Gothic\"/></w:rPr><w:t xml:space=\"preserve\">{text}</w:t></w:r>"
    )


def paragraph(text, bold=False, size=21):
    return f"<w:p>{run(text, bold=bold, size=size)}</w:p>"


def table(headers, rows):
    tbl_pr = (
        "<w:tblPr><w:tblW w:w=\"0\" w:type=\"auto\"/>"
        "<w:tblBorders>"
        "<w:top w:val=\"single\" w:sz=\"8\" w:space=\"0\" w:color=\"auto\"/>"
        "<w:left w:val=\"single\" w:sz=\"8\" w:space=\"0\" w:color=\"auto\"/>"
        "<w:bottom w:val=\"single\" w:sz=\"8\" w:space=\"0\" w:color=\"auto\"/>"
        "<w:right w:val=\"single\" w:sz=\"8\" w:space=\"0\" w:color=\"auto\"/>"
        "<w:insideH w:val=\"single\" w:sz=\"8\" w:space=\"0\" w:color=\"auto\"/>"
        "<w:insideV w:val=\"single\" w:sz=\"8\" w:space=\"0\" w:color=\"auto\"/>"
        "</w:tblBorders></w:tblPr>"
    )
    grid = "<w:tblGrid>" + "".join("<w:gridCol w:w=\"2800\"/>" for _ in headers) + "</w:tblGrid>"

    def cell(text, bold=False):
        return (
            "<w:tc><w:tcPr><w:tcW w:w=\"2800\" w:type=\"dxa\"/></w:tcPr>"
            f"{paragraph(text, bold=bold)}"
            "</w:tc>"
        )

    header_row = "<w:tr>" + "".join(cell(h, bold=True) for h in headers) + "</w:tr>"
    body_rows = "".join("<w:tr>" + "".join(cell(v) for v in row) + "</w:tr>" for row in rows)
    return f"<w:tbl>{tbl_pr}{grid}{header_row}{body_rows}</w:tbl>"


blocks = []
blocks.append(paragraph("d. JavaFx", bold=True, size=24))

blocks.append(paragraph("(1) JavaFX 전체 설계 방향", bold=True, size=22))
blocks.append(paragraph(
    "JavaFX 구현에서도 Swing과 동일하게 Panel은 구체적인 UI 구성과 렌더링에 집중하고, "
    "View는 Panel과 Controller 사이의 Interface 역할을 수행하도록 설계하였다. "
    "이를 통해 순수 UI에 해당하는 Panel이 App의 동작이나 비즈니스 로직을 직접 알지 않도록 하고, "
    "Controller는 이벤트 처리, DTO 구성, Service 호출을 담당하여 JavaFX에서 필요한 동작을 수행하도록 분리하였다."
))

blocks.append(paragraph("(2) 전체 애플리케이션 흐름 및 구조", bold=True, size=22))
blocks.append(table(
    ["파일", "책임", "적용 개념"],
    [
        ("Main.java", "프로그램 진입점, MainApp 실행", "SoC"),
        ("MainApp.java", "JavaFxWiring 선택, AppController 생성 및 시작", "SoC"),
        ("JavaFxWiring.java", "JavaFX가 사용할 Service 조립 계약", "DIP, SoC"),
        ("CoreJavaFxWiring.java", "실제 SQLite와 core 구현체를 연결", "SoC"),
        ("MockJavaFxWiring.java", "공용 mock-support 기반 mock 구현체 연결", "SoC, 재사용성"),
        ("AppController.java", "로그인 → 프로젝트 선택 → 메인 화면 전환 흐름 제어", "GRASP Controller, SoC"),
        ("AppFrame.java", "Stage 관리, 화면 교체", "SRP, SoC"),
    ]
))
blocks.append(paragraph(
    "Main은 JavaFX 애플리케이션 실행을 시작하는 진입점이며, 실제 앱 시작은 MainApp이 담당하도록 분리하였다. "
    "MainApp은 JavaFxWiring을 통해 어떤 구현체를 연결할 것인지 결정하고, 그 결과를 AppController에 전달한다. "
    "이를 통해 MainApp은 구체적인 Service 생성 세부사항을 직접 알 필요 없이 JavaFxWiring이라는 추상화에만 의존하도록 하였다."
))
blocks.append(paragraph(
    "JavaFxWiring은 Swing의 AppWiring과 같은 역할을 수행한다. "
    "CoreJavaFxWiring은 실제 SQLite와 core/controller 구현체를 조립하고, "
    "MockJavaFxWiring은 공용 mock-support 모듈의 mock 구현체를 조립한다. "
    "이를 통해 JavaFX 역시 실제 구현체와 mock 구현체를 선택적으로 연결할 수 있도록 하였다."
))
blocks.append(paragraph(
    "AppController는 로그인 화면, 프로젝트 선택 화면, 메인 화면 사이의 전환을 담당하며, "
    "각 개별 화면의 Controller를 상위에서 조립하는 역할을 수행한다. "
    "AppFrame은 Stage와 최상위 화면 교체만 담당하고, 하위 Panel들은 순수히 UI를 그리는 역할에 집중하도록 하여 SRP와 SoC를 유지하도록 하였다."
))

blocks.append(paragraph("(3) MVC 기반 화면 구조", bold=True, size=22))
blocks.append(table(
    ["구성 요소", "대표 파일", "책임"],
    [
        ("View interface", "LoginView, HeaderView, ProjectView, IssueView", "Controller가 의존하는 화면 계약"),
        ("Panel", "LoginPanel, HeaderPanel, ProjectPanel, IssuePanel, DashboardPanel", "UI 렌더링, 입력값 제공, 이벤트 발행"),
        ("Controller", "LoginController, HeaderController, ProjectController, IssueController, MainController", "이벤트 처리, DTO 생성, Service 호출"),
        ("Service/Model", "Auth, Project, User, RoleResolver, Issue, Statistics 구현체", "도메인 규칙, 상태 전이, 데이터 처리"),
    ]
))
blocks.append(paragraph(
    "Controller는 구체적인 JavaFX 구현체인 Panel을 직접 바라보지 않고 View interface에 의존하도록 설계하였다. "
    "이를 통해 Panel은 버튼, 입력창, 테이블, 다이얼로그 등 UI 구성에 집중하고, "
    "Controller는 버튼 이벤트를 받아 DTO를 만들고 Service interface를 호출하도록 하여 UI 변경과 기능 변경의 수정 범위를 분리할 수 있도록 하였다."
))

blocks.append(paragraph("(4) 메인 화면 및 기능별 책임 분리", bold=True, size=22))
blocks.append(paragraph(
    "JavaFX 역시 기능별로 화면과 제어 책임을 분리하여 응집도를 높이도록 구성하였다. "
    "MainPanel은 Header 영역과 Content 영역을 조합하여 메인 화면 전체 UI를 구성하고, "
    "MainController는 HeaderController, ProjectController, IssueController, DashboardPanel을 조합하여 현재 선택된 기능 화면을 제어하는 상위 조립 Controller 역할을 수행한다."
))
blocks.append(table(
    ["기능 영역", "분리된 클래스", "책임"],
    [
        ("Header", "HeaderPanel / HeaderController", "사용자 정보, 로그아웃, 프로젝트 목록 이동, 통계/이슈 화면 진입"),
        ("Project", "ProjectPanel / ProjectController", "프로젝트 목록 조회, 프로젝트 선택, 프로젝트별 사용자 조회 및 관리"),
        ("Issue List", "IssueTablePanel, IssueFilterPanel", "이슈 목록 표시, 검색 및 필터링"),
        ("Issue Detail", "IssuePanel / IssueController", "상세 정보, 댓글, 상태 변경, 배정, 추천, 통계 표시"),
        ("Dashboard", "DashboardPanel", "선택된 프로젝트 기준 통계 요약 렌더링"),
    ]
))

blocks.append(paragraph("(5) 이슈 기능과 권한 기반 정책 설계", bold=True, size=22))
blocks.append(paragraph(
    "IssuePanel은 내부적으로 IssueTablePanel과 IssueFilterPanel을 조합하고, "
    "IssueController는 선택된 사용자 권한에 따라 이슈 등록, 배정, 수정 완료, 해결 확인, 종료, 재오픈, 통계 기능을 제어한다. "
    "즉 JavaFX에서는 Swing처럼 별도의 Policy 클래스를 두지는 않았지만, 권한에 따른 버튼 노출과 동작 가능 여부를 "
    "IssuePanel과 IssueController에서 일관되게 분리하여 처리하도록 설계하였다."
))
blocks.append(table(
    ["대상 권한", "주요 UI 정책"],
    [
        ("ADMIN", "전체 이슈 조회, 종료 및 재오픈 가능"),
        ("PL", "이슈 배정, 담당자 추천, 종료 가능"),
        ("DEV", "자신에게 배정된 이슈의 FIXED 처리 중심"),
        ("TESTER", "이슈 등록, RESOLVED 처리 중심"),
    ]
))
blocks.append(paragraph(
    "특히 재오픈 규칙은 backend와 동일하게 ADMIN만 수행할 수 있고 CLOSED 상태에서만 REOPENED로 전환되도록 맞추었다. "
    "이를 통해 UI 계층이 backend의 도메인 규칙과 일관되게 동작하도록 하였다."
))

blocks.append(paragraph("(6) 이벤트 처리 구조", bold=True, size=22))
blocks.append(paragraph(
    "JavaFX에서는 일반적으로 Button에 EventHandler를 직접 연결할 수 있지만, 이 경우 UI가 버튼 클릭 이후 실행될 구체 로직을 알게 된다. "
    "이를 분리하기 위해 View interface에 onLogin, onSearch, onRegisterIssue, onEnterProject와 같은 이벤트 등록 메서드를 두고, "
    "Controller가 bind() 단계에서 Runnable 또는 Consumer 기반 콜백을 연결하도록 구성하였다."
))
blocks.append(table(
    ["요소", "역할"],
    [
        ("onXxx(handler)", "이벤트 발생 시 실행할 함수 등록"),
        ("Runnable / Consumer", "UI가 구체 로직을 몰라도 실행할 수 있는 동작 단위"),
        ("bind()", "Controller가 View 이벤트를 Service 호출 흐름으로 연결"),
    ]
))

blocks.append(paragraph("(7) 공통 UI 구성 구조", bold=True, size=22))
blocks.append(paragraph(
    "JavaFX에서는 Swing의 UiTheme과 같은 전역 스타일 클래스 대신, 공통 Panel 조합과 UiDialog를 활용하여 일관된 사용자 경험을 유지하도록 하였다. "
    "예를 들어 UiDialog는 경고, 입력, 상세 정보, 선택 다이얼로그를 공통 방식으로 생성하도록 하여 화면별 중복 코드를 줄인다. "
    "또한 ProjectPanel, IssuePanel, HeaderPanel 등은 공통 레이아웃 규칙을 유지하면서도 기능별 책임을 분리하도록 구성하였다."
))
blocks.append(table(
    ["구성 요소", "적용 대상"],
    [
        ("UiDialog", "경고창, 입력창, 선택 다이얼로그"),
        ("MainPanel", "Header + Content 화면 조합"),
        ("ProjectPanel / IssuePanel", "기능별 상위 조합 패널"),
    ]
))

blocks.append(paragraph("(8) 테스트", bold=True, size=22))
blocks.append(paragraph(
    "JavaFX 모듈에서 사용하는 interface 계약이 실제 화면 계층과 정상적으로 연결되는지 확인하기 위해 "
    "JUnit 5 기반 단위 테스트를 작성하였다. 테스트는 DB 연결이나 실제 Repository, core 구현체를 직접 사용하는 방식이 아니라, "
    "공용 mock-support 모듈에서 제공하는 mock 구현체를 사용하여 수행하였다. "
    "이를 통해 JavaFX 화면 계층이 Auth, Project, User, RoleResolver, Issue, Statistics interface를 기대한 방식으로 사용하는지 검증할 수 있도록 하였다."
))
blocks.append(table(
    ["테스트 항목", "검증 내용"],
    [
        ("Auth + RoleResolver", "admin 로그인, userId 기반 role/loginId 조회"),
        ("Project + User", "프로젝트 목록 조회, 사용자 정보 조회"),
        ("Issue", "TESTER 생성 후 이슈 등록, 상세 조회, 목록 조회"),
        ("Statistics", "현재 조회된 이슈 목록 기반 상태/일별 통계 조회"),
        ("Mock Support", "공용 mock-support 모듈 기반 단위 테스트 수행"),
    ]
))

document_xml = (
    '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>'
    f'<w:document {NS}><w:body>'
    + "".join(blocks)
    + '<w:sectPr><w:pgSz w:w="11906" w:h="16838"/><w:pgMar w:top="1440" w:right="1440" w:bottom="1440" w:left="1440" w:header="708" w:footer="708" w:gutter="0"/></w:sectPr>'
    + '</w:body></w:document>'
)

styles_xml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:styles xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
  <w:style w:type="paragraph" w:default="1" w:styleId="Normal">
    <w:name w:val="Normal"/>
    <w:qFormat/>
    <w:rPr>
      <w:rFonts w:ascii="Malgun Gothic" w:hAnsi="Malgun Gothic" w:eastAsia="Malgun Gothic"/>
      <w:sz w:val="21"/>
    </w:rPr>
  </w:style>
</w:styles>
"""

content_types_xml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
  <Override PartName="/word/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml"/>
  <Override PartName="/docProps/core.xml" ContentType="application/vnd.openxmlformats-package.core-properties+xml"/>
  <Override PartName="/docProps/app.xml" ContentType="application/vnd.openxmlformats-officedocument.extended-properties+xml"/>
</Types>
"""

rels_xml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties" Target="docProps/core.xml"/>
  <Relationship Id="rId3" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties" Target="docProps/app.xml"/>
</Relationships>
"""

doc_rels_xml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
</Relationships>
"""

core_xml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<cp:coreProperties xmlns:cp="http://schemas.openxmlformats.org/package/2006/metadata/core-properties"
 xmlns:dc="http://purl.org/dc/elements/1.1/"
 xmlns:dcterms="http://purl.org/dc/terms/"
 xmlns:dcmitype="http://purl.org/dc/dcmitype/"
 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <dc:title>JavaFX Structure Summary</dc:title>
  <dc:creator>OpenAI Codex</dc:creator>
</cp:coreProperties>
"""

app_xml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Properties xmlns="http://schemas.openxmlformats.org/officeDocument/2006/extended-properties"
 xmlns:vt="http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes">
  <Application>Microsoft Office Word</Application>
</Properties>
"""

output_path = Path("javafx-structure-summary.docx")
with zipfile.ZipFile(output_path, "w", zipfile.ZIP_DEFLATED) as zf:
    zf.writestr("[Content_Types].xml", content_types_xml)
    zf.writestr("_rels/.rels", rels_xml)
    zf.writestr("word/document.xml", document_xml)
    zf.writestr("word/styles.xml", styles_xml)
    zf.writestr("word/_rels/document.xml.rels", doc_rels_xml)
    zf.writestr("docProps/core.xml", core_xml)
    zf.writestr("docProps/app.xml", app_xml)

print(output_path)
