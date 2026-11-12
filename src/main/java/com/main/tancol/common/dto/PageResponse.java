package com.main.tancol.common.dto;

import java.util.List;

public record PageResponse<T>(List<T> items, long total, int page, int pageSize) {
}
