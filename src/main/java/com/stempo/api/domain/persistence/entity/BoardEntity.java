package com.stempo.api.domain.persistence.entity;

import com.stempo.api.domain.domain.model.BoardCategory;
import com.stempo.api.global.util.StringJsonConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "board")
public class BoardEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String deviceTag;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BoardCategory category;

    @Column(nullable = false)
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    private String title;

    @Column(nullable = false)
    @Size(min = 1, max = 10000, message = "Content must be between 1 and 10000 characters")
    private String content;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = StringJsonConverter.class)
    private List<String> fileUrls;

    @Column(nullable = false)
    private boolean deleted;
}
