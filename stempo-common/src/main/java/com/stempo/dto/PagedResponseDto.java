package com.stempo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Getter
public class PagedResponseDto<T> {

    @Schema(description = "현재 페이지 번호", example = "0")
    private final int currentPage;

    @Schema(description = "이전 페이지 존재 여부", example = "false")
    private final boolean hasPrevious;

    @Schema(description = "다음 페이지 존재 여부", example = "true")
    private final boolean hasNext;

    @Schema(description = "전체 페이지 수", example = "10")
    private final int totalPages;

    @Schema(description = "전체 아이템 수", example = "100")
    private final long totalItems;

    @Schema(description = "현재 페이지 아이템 수", example = "10")
    private final int take;

    @Schema(description = "현재 페이지 아이템 목록")
    private final List<T> items;

    public PagedResponseDto(Page<T> page) {
        this.currentPage = page.getNumber();
        this.hasPrevious = page.hasPrevious();
        this.hasNext = page.hasNext();
        this.totalPages = page.getTotalPages();
        this.totalItems = page.getTotalElements();
        this.take = page.getNumberOfElements();
        this.items = page.getContent();
    }

    public PagedResponseDto(Page<T> page, long totalItems, int numberOfElements) {
        this.currentPage = page.getNumber();
        this.hasPrevious = page.hasPrevious();
        this.hasNext = page.hasNext();
        this.totalPages = page.getTotalPages();
        this.totalItems = totalItems;
        this.take = numberOfElements;
        this.items = page.getContent();
    }

    public PagedResponseDto(List<T> ts, Pageable pageable, int totalItems) {
        this.currentPage = pageable.getPageNumber();
        this.hasPrevious = pageable.getPageNumber() > 0;
        this.hasNext = pageable.getOffset() + pageable.getPageSize() < totalItems && !ts.isEmpty();
        this.totalPages = (int) Math.ceil((double) totalItems / pageable.getPageSize());
        this.totalItems = totalItems;
        this.take = ts.size();
        this.items = ts;
    }
}
