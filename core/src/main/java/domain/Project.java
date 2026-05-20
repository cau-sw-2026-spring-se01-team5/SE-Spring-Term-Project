package domain;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class Project {
    @Setter
    private Integer id;
    @NonNull
    private String name;
}
