package com.example.demo.src.bug;

import com.example.demo.src.bug.model.GetBugInfoRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository

public class BugDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 병해충 정보 저장
    public void saveBugInfo(String user_id, GetBugInfoRes getBugInfoRes) {
        String saveBugInfoQuery = "insert into Crop (user_id, cropName, sickNameKor, sickNameEng, developmentCondition, preventionMethod, symptoms, infectionRoute) VALUES (?,?,?,?,?,?,?,?)";
        Object[] saveBugInfoParam = new Object[]{
                user_id,
                getBugInfoRes.getCropName(),
                getBugInfoRes.getSickNameKor(),
                getBugInfoRes.getSickNameEng(),
                getBugInfoRes.getDevelopmentCondition(),
                getBugInfoRes.getPreventionMethod(),
                getBugInfoRes.getSymptoms(),
                getBugInfoRes.getInfectionRoute()
        };
        this.jdbcTemplate.update(saveBugInfoQuery, saveBugInfoParam);
    }

}
