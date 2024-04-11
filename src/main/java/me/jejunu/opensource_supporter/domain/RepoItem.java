package me.jejunu.opensource_supporter.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "repo_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RepoItem { //TODO: tag 구현
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String repositoryName;

    private String description;

    private int viewCount;

    @ElementCollection
    private List<String> tags;

    private double totalPoint;

    private String repositoryLink;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "repoItem")
    private List<SupportPoint> supportPointList;

    @Builder
    public RepoItem(String repositoryName, String description, User user) {
        this.repositoryName = repositoryName;
        this.description = description;
        this.viewCount = 0;
        this.totalPoint = 0.0;
        this.repositoryLink = null;
        this.user = user;
    }
}
