package cn.edu.ustc.socketclient.common;

import cn.edu.ustc.socketclient.model.support.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({Exception.class})
    public Object handleGlobalException(Exception e) {
        e.printStackTrace();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ApiResponse<>(status.value(), Constant.errorMsg, e);
    }
}
