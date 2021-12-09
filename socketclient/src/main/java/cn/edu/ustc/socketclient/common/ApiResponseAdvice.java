package cn.edu.ustc.socketclient.common;

import cn.edu.ustc.socketclient.model.support.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.validation.constraints.NotNull;

@RestControllerAdvice(basePackages = "cn.edu.ustc.socketclient")
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(@NotNull MethodParameter returnType, @NotNull Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  @NotNull MediaType selectedContentType,
                                  @NotNull Class selectedConverterType,
                                  ServerHttpRequest request,
                                  @NotNull ServerHttpResponse response) {
        String path = request.getURI().getPath();

        if (body instanceof ApiResponse<?>) {
            ApiResponse<?> res = (ApiResponse<?>) body;
            if (res.getPath() == null) {
                res.setPath(path);
            }
            return res;
        }

        HttpStatus code = HttpStatus.OK;
        if (body instanceof HttpStatus) {
            code = (HttpStatus) body;
            body = null;
        }  else if (body == null) {
            code = HttpStatus.NOT_FOUND;
        }

        String message;
        switch (code) {
            case OK: message = Constant.successMsg; break;
            case FORBIDDEN: message = Constant.forbiddenMsg; break;
            case BAD_REQUEST: message = Constant.badRequestMsg; break;
            case NOT_FOUND: message = Constant.notFoundMsg; break;
            default: message = Constant.errorMsg; break;
        }

        response.setStatusCode(HttpStatus.valueOf(code.value()));
        return new ApiResponse<>(path, code.value(), message, body);
    }
}
