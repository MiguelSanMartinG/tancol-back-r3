package com.main.tancol.common.dto;

import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public record CatalogPage(
        int page,
        int pageSize,
        String search,
        Map<String, String> filters,
        String sortKey,
        String sortDirection
) {
    private static final Set<String> PAGE_KEYS = Set.of("page", "pageSize", "search");

    public static CatalogPage parse(Map<String, String> params, Set<String> filterKeys, Set<String> sortKeys) {
        Map<String, String> safeParams = params == null ? Map.of() : params;
        int page = positiveInt(safeParams.get("page"), 1);
        int pageSize = Math.min(100, positiveInt(safeParams.get("pageSize"), 50));
        String search = blankToNull(safeParams.get("search"));
        Map<String, String> filters = new LinkedHashMap<>();
        String sortKey = null;
        String sortDirection = null;
        for (Map.Entry<String, String> entry : safeParams.entrySet()) {
            String key = entry.getKey();
            if (PAGE_KEYS.contains(key)) {
                continue;
            }
            if (key.startsWith("filter_")) {
                String name = key.substring("filter_".length());
                if (!filterKeys.contains(name)) {
                    throw new FilterNotFound("Invalid filter: " + key);
                }
                filters.put(name, entry.getValue());
                continue;
            }
            if (key.startsWith("sort_")) {
                String name = key.substring("sort_".length());
                if (!sortKeys.contains(name)) {
                    throw new FilterNotFound("Invalid filter: " + key);
                }
                if (sortKey == null) {
                    sortKey = name;
                    sortDirection = entry.getValue();
                }
                continue;
            }
            throw new FilterNotFound("Invalid filter: " + key);
        }
        return new CatalogPage(page, pageSize, search, filters, sortKey, sortDirection);
    }

    public Pageable pageable(Sort sort) {
        return PageRequest.of(page - 1, pageSize, sort);
    }

    public Sort sort(Map<String, String> paths, Sort fallback, String idField) {
        Sort tie = Sort.by(Sort.Direction.ASC, idField);
        if (sortKey == null) {
            return fallback.and(tie);
        }
        String direction = sortDirection == null ? "" : sortDirection.trim();
        if (!direction.equals("ASC") && !direction.equals("DESC")) {
            throw new FilterNotFound("Invalid filter: sort_" + sortKey);
        }
        String path = paths.get(sortKey);
        if (path == null) {
            throw new FilterNotFound("Invalid filter: sort_" + sortKey);
        }
        return Sort.by(Sort.Direction.valueOf(direction), path).and(tie);
    }

    private static int positiveInt(String raw, int fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            return Math.max(1, Integer.parseInt(raw.trim()));
        } catch (NumberFormatException ex) {
            throw new FilterNotFound("Invalid filter: " + raw);
        }
    }

    private static String blankToNull(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return raw.trim();
    }
}
