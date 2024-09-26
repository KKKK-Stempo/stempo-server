package com.stempo.api.domain.persistence.mappper;

import com.stempo.api.domain.domain.model.Board;
import com.stempo.api.domain.persistence.entity.BoardEntity;
import org.springframework.stereotype.Component;

@Component
public class BoardMapper {

    public BoardEntity toEntity(Board board) {
        return BoardEntity.builder()
                .id(board.getId())
                .deviceTag(board.getDeviceTag())
                .category(board.getCategory())
                .title(board.getTitle())
                .content(board.getContent())
                .fileUrls(board.getFileUrls())
                .build();
    }

    public Board toDomain(BoardEntity entity) {
        return Board.builder()
                .id(entity.getId())
                .deviceTag(entity.getDeviceTag())
                .category(entity.getCategory())
                .title(entity.getTitle())
                .content(entity.getContent())
                .fileUrls(entity.getFileUrls())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
