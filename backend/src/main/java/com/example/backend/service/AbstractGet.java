package com.example.backend.service;

import com.example.backend.service.filters.FilterCondition;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractGet extends AbstractMethod {
    private Integer limit;
    private Integer offset;
    private List<FilterCondition> filters;
    private Integer totalCount; // Для хранения общего количества записей до пагинации

    @Override
    protected abstract List<Optional<?>> exec();

    @Override
    public List<Optional<?>> call(Map<String, Object> params, Map<String, Object> selectorParams) {
        // Извлекаем параметры пагинации и фильтров из selectorParams
        this.limit = (Integer) selectorParams.get("limit");
        this.offset = (Integer) selectorParams.get("offset");
        this.filters = parseFilters(selectorParams.get("filters"));

        // Вызываем родительский метод для стандартной обработки
        List<Optional<?>> result = super.call(params, selectorParams);

        // Применяем фильтрацию и пагинацию
        return applyFiltersAndPagination(result);
    }

    // Фильтры
    @SuppressWarnings("unchecked")
    private List<FilterCondition> parseFilters(Object filtersParam) {
        List<FilterCondition> result = new ArrayList<>();

        if (filtersParam instanceof List) {
            for (Object filterObj : (List<?>) filtersParam) {
                if (filterObj instanceof Map) {
                    Map<String, Object> filterMap = (Map<String, Object>) filterObj;
                    String field = (String) filterMap.get("field");
                    String operator = (String) filterMap.get("operator");
                    List<Object> values = (List<Object>) filterMap.get("values");

                    result.add(new FilterCondition(field, operator, values));
                }
            }
        }

        return result;
    }

    protected List<Optional<?>> applyFiltersAndPagination(List<Optional<?>> data) {
        // Применяем фильтры
        List<Optional<?>> filteredData = data.stream()
                .filter(Optional::isPresent)
                .filter(item -> matchesAllFilters(item.get()))
                .collect(Collectors.toList());

        // Сохраняем общее количество после фильтр ации
        this.totalCount = filteredData.size();

        // Применяем пагинацию
        int start = offset != null ? offset : 0;
        int end = limit != null ? Math.min(start + limit, filteredData.size()) : filteredData.size();

        return filteredData.subList(start, end);
    }

    private boolean matchesAllFilters(Object entity) {
        if (filters == null || filters.isEmpty()) {
            return true;
        }

        return filters.stream().allMatch(filter -> matchesFilter(entity, filter));
    }

    private boolean matchesFilter(Object entity, FilterCondition filter) {
        try {
            // Получаем значение поля с помощью рефлексии
            Object fieldValue = getFieldValue(entity, filter.getField());

            if (fieldValue == null) {
                return false;
            }

            // Применяем оператор фильтра
            return switch (filter.getOperator().toUpperCase()) {
                case "EQUALS" -> fieldValue.equals(filter.getValues().get(0));
                case "NOT_EQUALS" -> !fieldValue.equals(filter.getValues().get(0));
                case "IN" -> filter.getValues().contains(fieldValue);
                case "NOT_IN" -> !filter.getValues().contains(fieldValue);
                case "MORE_THAN" -> compareNumbers(fieldValue, filter.getValues().get(0)) > 0;
                case "LESS_THAN" -> compareNumbers(fieldValue, filter.getValues().get(0)) < 0;
                case "BETWEEN" ->
                        compareNumbers(fieldValue, filter.getValues().get(0)) >= 0 &&
                                compareNumbers(fieldValue, filter.getValues().get(1)) <= 0;
                case "LIKE" -> fieldValue.toString().toLowerCase()
                        .contains(filter.getValues().get(0).toString().toLowerCase());
                case "IS_NULL" -> fieldValue == null;
                case "IS_NOT_NULL" -> fieldValue != null;
                default -> false;
            };
        } catch (Exception e) {
            return false;
        }
    }

    private Object getFieldValue(Object entity, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        // Поддержка вложенных полей (например, "user.name")
        String[] fieldPath = fieldName.split("\\.");
        Object currentValue = entity;

        for (String field : fieldPath) {
            Field declaredField = currentValue.getClass().getDeclaredField(field);
            declaredField.setAccessible(true);
            currentValue = declaredField.get(currentValue);

            if (currentValue == null) {
                return null;
            }
        }

        return currentValue;
    }

    private int compareNumbers(Object value1, Object value2) {
        // Приведение чисел к Double для сравнения
        double num1 = value1 instanceof Number ? ((Number) value1).doubleValue() : Double.parseDouble(value1.toString());
        double num2 = value2 instanceof Number ? ((Number) value2).doubleValue() : Double.parseDouble(value2.toString());

        return Double.compare(num1, num2);
    }

}
