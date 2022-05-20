package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetHistoryRes {
    private int crop_num;
    private String cropName;
    private String sickNameKor;
    private String sickNameEng;
    private String developmentCondition;
    private String preventionMethod;
    private String symptoms;
    private String infectionRoute;
    private String imgPath;
}
