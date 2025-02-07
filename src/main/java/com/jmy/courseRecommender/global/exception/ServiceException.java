package com.jmy.courseRecommender.global.exception;

import com.jmy.courseRecommender.global.dto.RsData;


public class ServiceException extends RuntimeException {

    private final RsData<?> rsData;
    private final int statusCode; // 상태 코드를 명시적으로 저장

    // 세 개의 인자를 받는 생성자 추가
    public ServiceException(String code, String msg, int statusCode) {
        super(msg);
        this.statusCode = statusCode;
        // 여기서는 data는 null로 설정합니다.
        this.rsData = new RsData<>(code, msg, null);
    }

    public String getCode() {
        return rsData.getCode();
    }

    public String getMsg() {
        return rsData.getMsg();
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}
