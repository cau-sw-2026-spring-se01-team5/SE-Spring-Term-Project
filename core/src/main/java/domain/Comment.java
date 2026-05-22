package domain;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @RequiredArgsConstructor
public class Comment {
    @Setter
    private Integer id;
    @NonNull
    private LocalDateTime createdAt;
    @NonNull
    private String body;
    @NonNull
    private Integer authorId;
    @NonNull
    private Integer issueId;
}
