package com.example.demo.src.bug;

import com.example.demo.src.bug.model.GetBugInfoRes;
import com.example.demo.src.user.model.GetUserRes;
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
    public void saveBugInfo(String user_id, GetBugInfoRes getBugInfoRes, String imgPath, Long search_time) {
        String saveBugInfoQuery = "insert into Crop (user_id, cropName, sickNameKor, sickNameEng, developmentCondition, preventionMethod, symptoms, infectionRoute, imgPath, search_time) VALUES (?,?,?,?,?,?,?,?,?,?)";
        Object[] saveBugInfoParam = new Object[]{
                user_id,
                getBugInfoRes.getCropName(),
                getBugInfoRes.getSickNameKor(),
                getBugInfoRes.getSickNameEng(),
                getBugInfoRes.getDevelopmentCondition(),
                getBugInfoRes.getPreventionMethod(),
                getBugInfoRes.getSymptoms(),
                getBugInfoRes.getInfectionRoute(),
                imgPath,
                search_time
        };
        this.jdbcTemplate.update(saveBugInfoQuery, saveBugInfoParam);
    }

    public GetBugInfoRes getBugInfo(String sickNameEng) {
        String getBugInfoQuery = "select cropName, sickNameKor, sickNameEng, developmentCondition, preventionMethod, symptoms, infectionRoute from Bug where sickNameEng = ?";
        return this.jdbcTemplate.queryForObject(getBugInfoQuery,
                (rs, rowNum) -> new GetBugInfoRes(
                        rs.getString("cropName"),
                        rs.getString("sickNameKor"),
                        rs.getString("sickNameEng"),
                        rs.getString("developmentCondition"),
                        rs.getString("preventionMethod"),
                        rs.getString("symptoms"),
                        rs.getString("infectionRoute")),
                sickNameEng);
    }
}
