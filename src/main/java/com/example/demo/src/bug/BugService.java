package com.example.demo.src.bug;

import com.example.demo.config.BaseException;
import com.example.demo.src.bug.model.GetBugInfoRes;
import com.example.demo.src.user.UserDao;
import com.example.demo.src.user.UserProvider;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class BugService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BugDao bugDao;
    private final BugProvider bugProvider;
    private final JwtService jwtService;

    @Autowired
    public BugService(BugDao bugDao, BugProvider bugProvider, JwtService jwtService) {
        this.bugDao = bugDao;
        this.bugProvider = bugProvider;
        this.jwtService = jwtService;
    }

    public void saveBugInfo(String user_id, GetBugInfoRes getBugInfoRes) throws BaseException {
        bugDao.saveBugInfo(user_id, getBugInfoRes);
    }
}
