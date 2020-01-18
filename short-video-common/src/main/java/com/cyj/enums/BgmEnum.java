package com.cyj.enums;

import lombok.Getter;

/**
 * @Author: 陈宇健
 * @Date: 2020/01/01/14:23
 * @Description:
 */
@Getter
public enum BgmEnum {
    ADD("1","添加bgm"),
    DEL("2","删除bgm");

    private String type;
    private String value;

    BgmEnum(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public static String getValueByKey(String key){
        for (BgmEnum bgmEnum:BgmEnum.values()){
            if (bgmEnum.getType().equals(key)){
                return bgmEnum.value;
            }
        }
        return null;
    }
}
