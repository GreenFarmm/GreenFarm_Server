package com.example.demo.src.user;

import com.example.demo.src.bug.model.GetBugInfoRes;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/user")

public class UserController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;


    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public Float[] getJsonData(String roadFullAddr) {
        Float[] coordinate = new Float[2];

        String authorization_key = "KakaoAK ef8c0ce6769c009d6a81fecdb782518b";
        String apiUrl = "https://dapi.kakao.com/v2/local/search/address.json";
        String jsonString = null;

        try {
            roadFullAddr = URLEncoder.encode(roadFullAddr, "UTF-8");

            String address = apiUrl + "?query=" + roadFullAddr;

            URL url = new URL(address);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Authorization", authorization_key);

            BufferedReader rd = null;
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuffer docJson = new StringBuffer();

            String line;

            while ((line = rd.readLine()) != null) {
                docJson.append(line);
            }

            jsonString = docJson.toString();
            rd.close();

            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray documentsArray = jsonObject.getJSONArray("documents");
            JSONObject documentsObject = documentsArray.getJSONObject(0);

            String longtitude = documentsObject.getString("x");
            String latitude = documentsObject.getString("y");

            coordinate[0] = Float.parseFloat(longtitude);
            coordinate[1] = Float.parseFloat(latitude);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return coordinate;
    }

    /**
     * 회원가입 API
     * [POST] /user
     */
    // Body
    @ResponseBody
    @PostMapping("/sign-up")
    public BaseResponse<String> createUser(@RequestBody PostUserReq postUserReq) {
        try {
            Float[] coordinate = new Float[2];
            coordinate = getJsonData(postUserReq.getLocation());
            postUserReq.setLongitude(coordinate[0]);
            postUserReq.setLatitude(coordinate[1]);

            userService.createUser(postUserReq);
            return new BaseResponse<>("회원가입 완료");
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 로그인 API
     * [POST] /users/logIn
     */
    @ResponseBody
    @PostMapping("/log-in")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
        try {
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 병해충 검색 기록 조회
     * @param user_id
     * @return
     * @throws BaseException
     */
    @GetMapping("/history")
    public BaseResponse<List<GetHistoryRes>> getHistory(@RequestParam String user_id) throws BaseException {
        jwtService.JwtEffectiveness(user_id, jwtService.getUserId());

        List<GetHistoryRes> getHistoryRes = userProvider.getHistory(user_id);

        return new BaseResponse<>(getHistoryRes);
    }


    /**
     * 모든 회원들의  조회 API
     * [GET] /users
     *
     * 또는
     *
     * 해당 닉네임을 같는 유저들의 정보 조회 API
     * [GET] /users? NickName=
     */
    //Query String
    @ResponseBody   // return되는 자바 객체를 JSON으로 바꿔서 HTTP body에 담는 어노테이션.
    //  JSON은 HTTP 통신 시, 데이터를 주고받을 때 많이 쓰이는 데이터 포맷.
    @GetMapping("") // (GET) 127.0.0.1:9000/app/users
    // GET 방식의 요청을 매핑하기 위한 어노테이션
    public BaseResponse<List<GetUserRes>> getUsers(@RequestParam(required = false) String nickname) {
        //  @RequestParam은, 1개의 HTTP Request 파라미터를 받을 수 있는 어노테이션(?뒤의 값). default로 RequestParam은 반드시 값이 존재해야 하도록 설정되어 있지만, (전송 안되면 400 Error 유발)
        //  지금 예시와 같이 required 설정으로 필수 값에서 제외 시킬 수 있음
        //  defaultValue를 통해, 기본값(파라미터가 없는 경우, 해당 파라미터의 기본값 설정)을 지정할 수 있음
        try {
            if (nickname == null) { // query string인 nickname이 없을 경우, 그냥 전체 유저정보를 불러온다.
                List<GetUserRes> getUsersRes = userProvider.getUsers();
                return new BaseResponse<>(getUsersRes);
            }
            // query string인 nickname이 있을 경우, 조건을 만족하는 유저정보들을 불러온다.
            List<GetUserRes> getUsersRes = userProvider.getUsersByNickname(nickname);
            return new BaseResponse<>(getUsersRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
