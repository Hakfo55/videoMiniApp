package com.cyj.enums;

import lombok.Getter;

@Getter
public enum VideoStatusEnum {
    SUCCESS(1,"发布成功"),
    FORBID(2,"禁止播放");

    private Integer status;
    private String msg;

    VideoStatusEnum(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }
}
