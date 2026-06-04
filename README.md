# SE-Spring-Term-Project

CAU 2026-1 Software Engineering Team 5 Project

## 팀원

| 학번 | 이름 |
|--------|--------|
| 20220364 | 김태진 |
| 20220807 | 이성재 |
| 20220798 | 정석환 |
| 20226630 | 정원상 |

---

## 프로그램 실행 방법

본 프로젝트는 Gradle 멀티모듈 구조로 구성되어 있으며, `Swing` 버전과 `JavaFX` 버전 중 하나를 실행할 수 있습니다.

### 1. 실행 전 준비사항

#### 1-1. JDK 17 설치

Eclipse Temurin JDK 17 설치를 권장합니다.

- https://adoptium.net/temurin/releases/?version=17

#### 1-2. 프로젝트 압축 해제

제출된 프로젝트 압축 파일을 원하는 위치에 압축 해제합니다.

#### 1-3. PowerShell 실행 (Windows 기준)

Windows PowerShell을 실행합니다.

---

### 2. 프로젝트 폴더로 이동

```powershell
cd <프로젝트_폴더_경로>
```

예시

```powershell
cd C:\Users\User\Downloads\SE-Spring-Term-Project
```

---

### 3. JAVA_HOME 설정 (필요 시)

JDK가 자동으로 설정되지 않은 경우 아래 명령어를 실행합니다.

```powershell
$env:JAVA_HOME="<JDK17 설치 경로>"
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
```

예시

```powershell
$env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-17"
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
```

---

### 4. 설정 확인

```powershell
.\gradlew.bat --version
```

출력 결과의 `Launcher JVM` 항목이 JDK 17로 표시되면 정상입니다.

---

### 5. 전체 빌드

```powershell
.\gradlew.bat clean build
```

또는

```powershell
.\gradlew.bat build
```

---

### 6. Swing 프로그램 실행

```powershell
.\gradlew.bat :swing:run
```

---

### 7. JavaFX 프로그램 실행

```powershell
.\gradlew.bat :javafx:run
```

---

### 8. 전체 테스트 실행

```powershell
.\gradlew.bat clean test
```

또는

```powershell
.\gradlew.bat test
```

---

### 9. Swing 모듈 빌드

```powershell
.\gradlew.bat :swing:build
```

---

### 10. JavaFX 모듈 빌드

```powershell
.\gradlew.bat :javafx:build
```

---

## 문제 발생 시

### Gradle 데몬 종료

```powershell
.\gradlew.bat --stop
```

### 프로젝트 캐시 삭제 후 재빌드

```powershell
Remove-Item -Recurse -Force .gradle
```

이후 다시 빌드합니다.

```powershell
.\gradlew.bat clean build
```
