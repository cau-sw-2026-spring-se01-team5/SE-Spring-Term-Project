# SE-Spring-Term-Project
CAU 2026-1 spring se01 team5 project

## 실행 방법

$env:JAVA_HOME="C:\Users\seokhwan\.jdks\ms-17.0.19"
현재 PowerShell 세션에서 Java 17 경로를 설정합니다.

`.\gradlew.bat build`
전체 모듈을 빌드합니다.**
**
.\gradlew.bat test
전체 테스트를 실행합니다.

.\gradlew.bat :swing:build
Swing 모듈만 빌드합니다.

.\gradlew.bat :swing:run
Swing 애플리케이션을 실행합니다.

.\gradlew.bat :javafx:build
JavaFX 모듈만 빌드합니다.

.\gradlew.bat :javafx:run
JavaFX 애플리케이션을 실행합니다.
