package app;

// JavaFX가 사용할 service 묶음을 조립하는 공통 계약.
public interface JavaFxWiring {
    JavaFxServices wire() throws Exception;
}
