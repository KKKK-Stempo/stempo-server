package com.stempo.repository;

import com.stempo.model.Board;
import com.stempo.model.BoardCategory;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardRepository {

    Board save(Board board);

    void delete(Board board);

    void deleteAll(List<Board> boards);

    Page<Board> findByCategory(BoardCategory category, Pageable pageable);

    Board findByIdOrThrow(Long boardId);

    List<Board> findByDeviceTag(String deviceTag);
}
