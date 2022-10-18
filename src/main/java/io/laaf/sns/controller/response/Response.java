package io.laaf.sns.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 컨트롤러 응답 DTO
 * 일종의 ResponseEntity<?> 클래스와 같다.
 */
@Getter
@AllArgsConstructor
public class Response<T> {

    private String resultCode;
    private T result;

    public static Response<Void> error(String errorCode) {
       return new Response<>(errorCode, null);
    }

    public static Response<Void> success() {
        return new Response<>("SUCCESS", null);
    }


    public static <T> Response<T> success(T result) {
        return new Response<>("SUCCESS", result);
    }

    public String toStream() {
        if (result == null) {
            return "{" +
                    "\"resultCode\":" + "\"" + resultCode + "\"," +
                    "\"result\":" + "\"" + null + "\"";
        }

        return "{" +
                "\"resultCode\":" + "\"" + resultCode + "\"," +
                "\"result\":" + "\"" + result + "\"" + "}";
    }
}
