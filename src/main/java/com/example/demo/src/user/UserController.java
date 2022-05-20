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
     * 가까운 농민 조회
     * @param user_id
     * @return
     * @throws BaseException
     */
    @GetMapping("/nearby")
    public BaseResponse<List<String>> getNearByUser(@RequestParam String user_id) throws BaseException{
        jwtService.JwtEffectiveness(user_id, jwtService.getUserId());

        List<String> users = userProvider.getNearByUser(user_id);

        return new BaseResponse<>(users);
    }

}
