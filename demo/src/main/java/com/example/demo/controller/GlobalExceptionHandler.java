package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// すべてのエラーを統一的な ApiResponse フォーマットで返す
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException ex) {
        String message = ex.getMessage();

        if ("画像ファイルのみアップロードできます。".equals(message)) {
            message = "画像ファイルのみアップロードできます。PNG / JPG / WEBP などの画像を選択してください。";
        }

        if ("画像ファイルを選択してください。".equals(message)) {
            message = "画像ファイルを選択してください。空のファイルはアップロードできません。";
        }

        return ApiResponse.error(message);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        return ApiResponse.error("画像サイズが上限を超えています。15MB 以下の画像をアップロードしてください。");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception ex) {
        return ApiResponse.error("サーバーエラーが発生しました。しばらくしてから再度お試しください。");
    }
}