package com.vladimirpandurov.invoiceManager01B.utils;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladimirpandurov.invoiceManager01B.domain.HttpResponse;
import com.vladimirpandurov.invoiceManager01B.exception.ApiException;
import io.jsonwebtoken.InvalidClaimException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

import java.io.OutputStream;
import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
@Slf4j
public class ExceptionUtils {


    public static void processError(HttpServletRequest request, HttpServletResponse response, Exception exception){
        if(exception instanceof ApiException ||
                exception instanceof DisabledException ||
                exception instanceof LockedException ||
                exception instanceof BadCredentialsException ||
                exception instanceof InvalidClaimException ||
                exception instanceof TokenExpiredException) {
            HttpResponse httpResponse = getHttpResponse(response, exception.getMessage(), BAD_REQUEST);
            writeResponse(response, httpResponse);
        }else {
            HttpResponse httpResponse = getHttpResponse(response, "An error occurred. Please try again.", INTERNAL_SERVER_ERROR);
            writeResponse(response, httpResponse);
        }
        log.error(exception.getMessage());

    }

    private static void writeResponse(HttpServletResponse response, HttpResponse httpResponse) {
        OutputStream out;
        try {
            out = response.getOutputStream();
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(out, httpResponse);
            out.flush();
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    private static HttpResponse getHttpResponse(HttpServletResponse response, String message, HttpStatus httpStatus) {
        HttpResponse httpResponse = HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .reason(message)
                .status(httpStatus)
                .statusCode(httpStatus.value())
                .build();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(httpStatus.value());
        return httpResponse;

    }
}
