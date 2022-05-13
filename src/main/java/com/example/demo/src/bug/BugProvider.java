package com.example.demo.src.bug;

import com.example.demo.src.bug.model.GetBugInfoRes;
import com.example.demo.src.user.UserDao;
import com.example.demo.src.user.model.GetUserRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class BugProvider {
    private final BugDao bugDao;
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public BugProvider(BugDao bugDao, JwtService jwtService) {
        this.bugDao = bugDao;
        this.jwtService = jwtService;
    }

    public GetBugInfoRes getBugInfo(String sickNameEng) {
        GetBugInfoRes getBugInfoRes = bugDao.getBugInfo(sickNameEng);
        return getBugInfoRes;
    }
}
