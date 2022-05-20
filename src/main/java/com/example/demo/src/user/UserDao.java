package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository

public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 회원가입
    public void createUser(PostUserReq postUserReq) {
        String createUserQuery = "insert into User (user_id, user_pw, location, longitude, latitude) VALUES (?,?,?,?,?)";
        Object[] createUserParams = new Object[]{
                postUserReq.getUser_id(),
                postUserReq.getUser_pw(),
                postUserReq.getLocation(),
                postUserReq.getLongitude(),
                postUserReq.getLatitude()
        };
        this.jdbcTemplate.update(createUserQuery, createUserParams);
    }

    // 회원정보 변경
    public int modifyUserName(PatchUserReq patchUserReq) {
        String modifyUserNameQuery = "update User set nickname = ? where userIdx = ? "; // 해당 userIdx를 만족하는 User를 해당 nickname으로 변경한다.
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getNickname(), patchUserReq.getUserIdx()}; // 주입될 값들(nickname, userIdx) 순

        return this.jdbcTemplate.update(modifyUserNameQuery, modifyUserNameParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }


    // 로그인: 해당 id에 해당되는 user의 암호화된 비밀번호 값을 가져온다.
    public User getPwd(PostLoginReq postLoginReq) {
        String getPwdQuery = "select user_id, user_pw from User where user_id = ?";
        String getPwdParams = postLoginReq.getUser_id();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs, rowNum) -> User.builder().user_id(rs.getString("user_id")).
                        user_pw(rs.getString("user_pw")).build(), getPwdParams);
    }

    // User 테이블에 존재하는 전체 유저들의 정보 조회
    public List<GetUserRes> getUsers() {
        String getUsersQuery = "select * from User"; //User 테이블에 존재하는 모든 회원들의 정보를 조회하는 쿼리
        return this.jdbcTemplate.query(getUsersQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getString("user_id"),
                        rs.getString("user_pw"),
                        rs.getString("location"),
                        rs.getFloat("longitude"),
                        rs.getFloat("latitude")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        ); // 복수개의 회원정보들을 얻기 위해 jdbcTemplate 함수(Query, 객체 매핑 정보)의 결과 반환(동적쿼리가 아니므로 Parmas부분이 없음)
    }

    // 병해충 검색 기록 조회
    public List<GetHistoryRes> getHistory(String user_id) {
        String getHistoryQuery = "select * from Crop where user_id = ?";
        String getHistoryParam = user_id;
        return this.jdbcTemplate.query(getHistoryQuery,
                (rs, rowNum) -> new GetHistoryRes(
                        rs.getInt("crop_num"),
                        rs.getString("cropName"),
                        rs.getString("sickNameKor"),
                        rs.getString("sickNameEng"),
                        rs.getString("developmentCondition"),
                        rs.getString("preventionMethod"),
                        rs.getString("symptoms"),
                        rs.getString("infectionRoute"),
                        rs.getString("imgPath")),
                getHistoryParam);
    }

    // 경도 조회
    public Float getlongitude(String user_id) {
        String getUserQuery = "select longitude from User where user_id = ?";
        return this.jdbcTemplate.queryForObject(getUserQuery,Float.class, user_id);
    }

    // 위도 조회
    public Float getlatitude(String user_id) {
        String getUserQuery = "select latitude from User where user_id = ?";
        return this.jdbcTemplate.queryForObject(getUserQuery,Float.class, user_id);
    }
}
