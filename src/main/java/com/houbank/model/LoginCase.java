package com.houbank.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginCase {
    private Integer id;
    private String url;
    private String mobile;
    private String password;
    private String smsCode;
    private String loginType;
    private String status;
    private String actualResult;


    @Override
    public String toString(){
        return(
                "{id:"+id+","+
                        "url:"+url+","+
                        "mobile:"+mobile+","+
                        "password:"+password+","+
                        "smsCode:"+smsCode+","+
                        "loginType:"+loginType+","+
                        "status:"+status+","+
                        "actualResult:"+actualResult+"}"
        );
    }
}
