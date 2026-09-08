package com.lost2found.common.api;

import java.util.List;

/**
 * Generic Paginated Response Container for paginated queries.
 *
 * @param <T> Item element type
 */
public class PaginatedResponse<T> {

    private List<T> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private boolean first;
    private boolean empty;

    public PaginatedResponse() {
    }

    public PaginatedResponse(List<T> content, int pageNo, int pageSize, long totalElements, int totalPages, boolean last, boolean first, boolean empty) {
        this.content = content;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
        this.first = first;
        this.empty = empty;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public static <T> PaginatedResponse<T> of(List<T> content, int pageNo, int pageSize, long totalElements) {
        int totalPages = pageSize > 0 ? (int) Math.ceil((double) totalElements / pageSize) : 0;
        boolean first = pageNo == 0;
        boolean last = pageNo >= totalPages - 1;
        boolean empty = content == null || content.isEmpty();

        return new PaginatedResponse<>(
                content,
                pageNo,
                pageSize,
                totalElements,
                totalPages,
                last,
                first,
                empty
        );
    }
}
