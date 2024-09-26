package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.domain.model.Board;
import com.stempo.api.domain.domain.model.BoardCategory;
import com.stempo.api.domain.domain.repository.BoardRepository;
import com.stempo.api.domain.persistence.entity.BoardEntity;
import com.stempo.api.domain.persistence.mappper.BoardMapper;
import com.stempo.api.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public void deleteAll(List<Board> boards) {
        List<BoardEntity> entities = boards.stream()
                .map(mapper::toEntity)
                .toList();
        repository.deleteAll(entities);
    }

    @Override
    public Page<Board> findByCategory(BoardCategory category, Pageable pageable) {
        Page<BoardEntity> boards = repository.findByCategory(category, pageable);
        return boards.map(mapper::toDomain);
    }

    @Override
    public Board findByIdOrThrow(Long boardId) {
        return repository.findByIdAndNotDeleted(boardId)
                .map(mapper::toDomain)
                .orElseThrow(() -> new NotFoundException("[Board] id: " + boardId + " not found"));
    }

    @Override
    public List<Board> findByDeviceTag(String deviceTag) {
        List<BoardEntity> entities = repository.findByDeviceTag(deviceTag);
        return entities.stream()
                .map(mapper::toDomain)
                .toList();
    }
}
