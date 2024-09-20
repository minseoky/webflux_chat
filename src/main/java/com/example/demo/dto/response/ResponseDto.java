package com.example.demo.dto.response;

import com.example.demo.common.ResponseCode;
import com.example.demo.common.ResponseMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "## _응답 본문_")
public class ResponseDto<T> {

    @Schema(description = "### _응답 코드_")
    private final String code;
    @Schema(description = "### _응답 메시지_")
    private final String message;
    @Schema(description = "### _응답 데이터_", nullable = true)
    private T data;

    public ResponseDto() {
        this.code = ResponseCode.SUCCESS;
        this.message = ResponseMessage.SUCCESS;
        this.data = null;
    }

    public static <T> ResponseEntity<ResponseDto<T>> success(T data) {
        ResponseDto<T> responseDto = new ResponseDto<>();
        responseDto.data = data;
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    public static ResponseEntity<ResponseDto<Void>> success() {
        ResponseDto<Void> responseDto = new ResponseDto<>();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    public static ResponseEntity<? extends ResponseDto<?>> authenticationFailed(String message) {
        ResponseDto<?> responseDto = new ResponseDto<>(ResponseCode.AUTHENTICATION_FAILED, ResponseMessage.AUTHENTICATION_FAILED + message, null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDto);
    }

    public static ResponseEntity<? extends ResponseDto<?>> resourceNotFound(String message) {
        ResponseDto<?> responseDto = new ResponseDto<>(ResponseCode.RESOURCE_NOT_FOUND, ResponseMessage.RESOURCE_NOT_FOUND + message, null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDto);
    }

    public static ResponseEntity<? extends ResponseDto<?>> resourceDuplicated(String message) {
        ResponseDto<?> responseDto = new ResponseDto<>(ResponseCode.RESOURCE_DUPLICATED, ResponseMessage.RESOURCE_DUPLICATED + message, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }

    public static ResponseEntity<? extends ResponseDto<?>> internalServerError(String message) {
        ResponseDto<?> responseDto = new ResponseDto<>(ResponseCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR + message, null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
    }

}
