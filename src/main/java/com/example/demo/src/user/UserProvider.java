package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service    // [Business Layer에서 Service를 명시하기 위해서 사용] 비즈니스 로직이나 respository layer 호출하는 함수에 사용된다.
            // [Business Layer]는 컨트롤러와 데이터 베이스를 연결
/**
 * Provider란?
 * Controller에 의해 호출되어 실제 비즈니스 로직과 트랜잭션을 처리: Read의 비즈니스 로직 처리
 * 요청한 작업을 처리하는 관정을 하나의 작업으로 묶음
 * dao를 호출하여 DB CRUD를 처리 후 Controller로 반환
 */
public class UserProvider {


    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final UserDao userDao;
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired //readme 참고
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }
    // ******************************************************************************


    // 로그인(password 검사)
    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException {
        User user = userDao.getPwd(postLoginReq);
        String password;
        try {
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getUser_pw()); // 복호화 (회원가입할 때 비밀번호가 암호화되어 저장되었기 떄문에 로그인을 할때도 암호화된 값끼리 비교)
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }

        if (postLoginReq.getUser_pw().equals(password)) { //비말번호가 일치하는지 확인
            String user_id = userDao.getPwd(postLoginReq).getUser_id();
            String jwt = jwtService.createJwt(user_id);
            return new PostLoginRes(user_id,jwt);
        } else {
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }

    // User들의 정보를 조회
    public List<GetUserRes> getUsers() throws BaseException {
        try {
            List<GetUserRes> getUserRes = userDao.getUsers();
            return getUserRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 병해충 검색 기록 조회
     * @param user_id
     * @return
     * @throws BaseException
     */
    public List<GetHistoryRes> getHistory(String user_id) throws BaseException {
        List<GetHistoryRes> getHistoryRes = userDao.getHistory(user_id);

        return getHistoryRes;
    }

    /**
     * 가까운 농민 조회
     * @param user_id
     * @return
     * @throws BaseException
     */
    public List<String> getNearByUser(String user_id) throws BaseException {
        List<String> users = new ArrayList<String>();

        List<GetUserRes> getUserRes = userDao.getUsers();
        Float longitude = userDao.getlongitude(user_id);
        Float latitude = userDao.getlatitude(user_id);

        for(int i=0;i<getUserRes.size();i++){
            if(getUserRes.get(i).getUser_id().equals(user_id)) continue;
            if(getUserRes.get(i).getLongitude() >= longitude-0.5 && getUserRes.get(i).getLongitude() <= longitude+0.5) users.add(getUserRes.get(i).getUser_id());
        }

        return users;
    }
}
