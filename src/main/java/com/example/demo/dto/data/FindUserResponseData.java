package com.example.demo.dto.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "## _유저정보_")
public class FindUserResponseData {

    @Schema(description = "### _유저이름_")
    private String username;
    @Schema(description = "### _생성일자_")
    private LocalDateTime createdAt;

}
