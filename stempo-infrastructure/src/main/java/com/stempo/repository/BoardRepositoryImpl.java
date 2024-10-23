package com.stempo.repository;

import com.stempo.entity.BoardEntity;
import com.stempo.exception.NotFoundException;
import com.stempo.mapper.BoardMapper;
import com.stempo.model.Board;
import com.stempo.model.BoardCategory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepository {

    private final BoardJpaRepository repository;
    private final BoardMapper mapper;

    @Override
    public Board save(Board board) {
        BoardEntity entity = mapper.toEntity(board);
        BoardEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void delete(Board board) {
        BoardEntity entity = mapper.toEntity(board);
        repository.delete(entity);
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
        return repository.findById(boardId)
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
