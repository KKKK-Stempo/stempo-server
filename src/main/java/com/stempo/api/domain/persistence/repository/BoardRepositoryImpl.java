package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.domain.model.Board;
import com.stempo.api.domain.domain.model.BoardCategory;
import com.stempo.api.domain.domain.repository.BoardRepository;
import com.stempo.api.domain.persistence.entity.BoardEntity;
import com.stempo.api.domain.persistence.mappper.BoardMapper;
import com.stempo.api.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepository {

    private final BoardJpaRepository repository;
    private final BoardMapper mapper;


    @Override
    public Board save(Board board) {
        BoardEntity jpaEntity = mapper.toEntity(board);
        BoardEntity savedEntity = repository.save(jpaEntity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public List<Board> findByCategory(BoardCategory category) {
        List<BoardEntity> boards = repository.findByCategory(category);
        return boards.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Board findByIdOrThrow(Long boardId) {
        return repository.findByIdAndNotDeleted(boardId)
                .map(mapper::toDomain)
                .orElseThrow(() -> new NotFoundException("[Board] id: " + boardId + " not found"));
    }
}
