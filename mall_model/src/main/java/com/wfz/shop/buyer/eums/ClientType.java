package com.wfz.shop.buyer.eums;

import java.util.HashMap;
import java.util.Map;

public enum ClientType {

    PC(0,"PC"),
    H5(1,"H5"),
    WAP(2,"WAP"),
    UNKNOWN(3,"UNKNOWN");

    private int code;
    private String message;

    private static final Map<Integer, ClientType> CODE_MAP = new HashMap<>(4);

    static{
        for(ClientType clientType: values()){
            CODE_MAP.put(clientType.getCode(), clientType);
        }
    }

    ClientType(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static ClientType codeOf(int code){
        return CODE_MAP.get(code);
    }
}
