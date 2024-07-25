package com.stempo.api.domain.domain.repository;

import com.stempo.api.domain.domain.model.Board;
import com.stempo.api.domain.domain.model.BoardCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardRepository {

    Board save(Board board);

    Page<Board> findByCategory(BoardCategory category, Pageable pageable);

    Board findByIdOrThrow(Long boardId);
}
