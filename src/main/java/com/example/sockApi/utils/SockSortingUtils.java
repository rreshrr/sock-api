package com.example.sockApi.utils;

import com.example.sockApi.dto.SockDto;
import com.example.sockApi.enums.SortBy;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;

public class SockSortingUtils {
    private static final Map<SortBy, Comparator<SockDto>> SORTING_MAP = new EnumMap<>(SortBy.class);

    static {
        SORTING_MAP.put(SortBy.COTTON_PERCENTAGE_ASC, Comparator.comparing(SockDto::getCottonPercentage));
        SORTING_MAP.put(SortBy.COLOR_ASC, Comparator.comparing(SockDto::getColor, String.CASE_INSENSITIVE_ORDER));
        SORTING_MAP.put(SortBy.COTTON_PERCENTAGE_DESC, Comparator.comparing(SockDto::getCottonPercentage).reversed());
        SORTING_MAP.put(SortBy.COLOR_DESC, Comparator.comparing(SockDto::getColor, String.CASE_INSENSITIVE_ORDER).reversed());
    }

    public static Comparator<SockDto> getComparator(SortBy sortBy) {
        return SORTING_MAP.get(sortBy);
    }

}
