package com.stempo.api.domain.domain.repository;

import com.stempo.api.domain.domain.model.Board;
import com.stempo.api.domain.domain.model.BoardCategory;

import java.util.List;

public interface BoardRepository {

    Board save(Board board);

    List<Board> findByCategory(BoardCategory category);

    Board findByIdOrThrow(Long boardId);
}
