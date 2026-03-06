package com.bookvillage.backend.common;

import java.util.List;

public class PageResponse<T> {
    public List<T> data;
    public int total;
    public int page;
    public int pageSize;

    public PageResponse(List<T> data, int total, int page, int pageSize) {
        this.data = data;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
    }
}
