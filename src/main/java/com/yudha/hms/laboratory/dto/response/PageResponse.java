package com.yudha.hms.laboratory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Page Response DTO.
 *
 * Generic wrapper for paginated responses.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    /**
     * Page content
     */
    private List<T> content;

    /**
     * Current page number (0-indexed)
     */
    private int pageNumber;

    /**
     * Page size
     */
    private int pageSize;

    /**
     * Total number of elements across all pages
     */
    private long totalElements;

    /**
     * Total number of pages
     */
    private int totalPages;

    /**
     * Is first page
     */
    private boolean first;

    /**
     * Is last page
     */
    private boolean last;

    /**
     * Has previous page
     */
    private boolean hasPrevious;

    /**
     * Has next page
     */
    private boolean hasNext;

    /**
     * Number of elements in current page
     */
    private int numberOfElements;

    /**
     * Is empty page
     */
    private boolean empty;

    /**
     * Create PageResponse from Spring Data Page
     */
    public static <T> PageResponse<T> of(org.springframework.data.domain.Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasPrevious(page.hasPrevious())
                .hasNext(page.hasNext())
                .numberOfElements(page.getNumberOfElements())
                .empty(page.isEmpty())
                .build();
    }

    /**
     * Create PageResponse with manual values
     */
    public static <T> PageResponse<T> of(List<T> content, int pageNumber, int pageSize, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        return PageResponse.<T>builder()
                .content(content)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(pageNumber == 0)
                .last(pageNumber >= totalPages - 1)
                .hasPrevious(pageNumber > 0)
                .hasNext(pageNumber < totalPages - 1)
                .numberOfElements(content.size())
                .empty(content.isEmpty())
                .build();
    }
}
