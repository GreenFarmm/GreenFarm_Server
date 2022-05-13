package com.example.demo.src.bug.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class GetBugInfoRes {
    private String cropName;                // 작물명
    private String sickNameKor;             // 병 한글명
    private String sickNameEng;             // 병 영문명
    private String developmentCondition;    // 발생생태
    private String preventionMethod;        // 방제방법
    private String symptoms;                // 병 증상
    private String infectionRoute;          // 전염경로
}
