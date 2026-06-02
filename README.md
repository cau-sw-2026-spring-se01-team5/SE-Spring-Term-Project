# SE-Spring-Term-Project

CAU 2026-1 spring se01 team5 project

## 프로그램 실행 방법

이 프로젝트는 Gradle 멀티모듈 구조이며, `Swing` 버전과 `JavaFX` 버전 중 하나를 실행할 수 있습니다.

실행 전 준비사항:

- JDK 17 설치
- PowerShell에서 `JAVA_HOME` 설정

```powershell
$env:JAVA_HOME="C:\Users\seokhwan\.jdks\ms-17.0.19"
```

전체 빌드:

```powershell
.\gradlew.bat build
```

전체 테스트 실행:

```powershell
.\gradlew.bat test
```

Swing 프로그램 실행:

```powershell
.\gradlew.bat :swing:run
```

JavaFX 프로그램 실행:

```powershell
.\gradlew.bat :javafx:run
```

필요 시 개별 모듈만 빌드할 수 있습니다.

```powershell
.\gradlew.bat :swing:build
.\gradlew.bat :javafx:build
```
