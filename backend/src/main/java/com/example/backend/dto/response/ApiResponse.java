package com.example.backend.dto.response;

import com.example.backend.dto.request.ApiRequest;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
@Accessors(chain = true)
public class ApiResponse {
    public static SuccessResponse success(Map<String, Object> result) {
        var response = new SuccessResponse();
        Optional.ofNullable(result.get("result"))
                .map(resultValue -> (List<Optional<?>>) resultValue)
                .ifPresent(response::setResult);

        Optional.ofNullable(result.get("limit"))
                .map(limit -> (Integer) limit)
                .ifPresent(response::setLimit);

        Optional.ofNullable(result.get("offset"))
                .map(offset -> (Integer) offset)
                .ifPresent(response::setOffset);

        Optional.ofNullable(result.get("nextOffset"))
                .map(nextOffset -> (Integer) nextOffset)
                .ifPresent(response::setNextOffset);

        Optional.ofNullable(result.get("total"))
                .map(total -> (Integer) total)
                .ifPresent(response::setTotal);

        return response;
    }

    public static ErrorResponse error(List<ApiError> errors) {
        var response = new ErrorResponse();
        response.setErrors(errors);
        return response;
    }
}
