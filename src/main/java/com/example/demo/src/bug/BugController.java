package com.example.demo.src.bug.model;

import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseException;
import com.example.demo.src.bug.BugProvider;
import com.example.demo.src.bug.BugService;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
@RequestMapping("/bug")

public class BugController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final BugProvider bugProvider;
    @Autowired
    private final BugService bugService;
    @Autowired
    private final JwtService jwtService;

    public BugController(BugProvider bugProvider, BugService bugService, JwtService jwtService) {
        this.bugProvider = bugProvider;
        this.bugService = bugService;
        this.jwtService = jwtService;
    }


    @GetMapping("/apitest")
    public String callApiWithXml() {
        StringBuffer result = new StringBuffer();
        try {
            String apiUrl = "http://ncpms.rda.go.kr/npmsAPI/service?" +
                    "apiKey=2022b5d55c3ea9fac00003b87fa2ed6e69f3" +
                    "&serviceCode=SVC05" +
                    "&sickKey=D00001596"; // 팥 - 흰가루병
            URL url = new URL(apiUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

            String returnLine;
            result.append("<xmp>");
            while((returnLine = bufferedReader.readLine()) != null) {
                result.append(returnLine + "\n");
            }
            urlConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result + "</xmp>";
    }


    private GetBugInfoRes getXmlData() throws ParserConfigurationException, IOException, SAXException {
        GetBugInfoRes getBugInfoRes = new GetBugInfoRes();

        String Url = "http://ncpms.rda.go.kr/npmsAPI/service?" +
                "apiKey=2022b5d55c3ea9fac00003b87fa2ed6e69f3" +
                "&serviceCode=SVC05" +
                "&sickKey=D00001596"; // 팥 - 흰가루병

        // 1. 빌더 팩토리 생성.
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        // 2. 빌더 팩토리로부터 빌더 생성
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        // 3. 빌더를 통해 Url를 파싱해서 Document 객체로 가져온다.
        Document document = builder.parse(Url);
        // 문서 구조 안정화 ?
        document.getDocumentElement().normalize();

        // XML 데이터 중 <service> 태그의 내용을 가져온다.
        NodeList serviceTagList = document.getElementsByTagName("service");

        for (int i = 0; i < serviceTagList.getLength(); ++i) {
            NodeList childNodes = serviceTagList.item(i).getChildNodes();

            for (int j = 0; j < childNodes.getLength(); ++j) {
                String nodeName = childNodes.item(j).getNodeName();
                String textContent = childNodes.item(j).getTextContent();

                if(nodeName.equals("cropName")) getBugInfoRes.setCropName(textContent);
                if(nodeName.equals("sickNameKor")) getBugInfoRes.setSickNameKor(textContent);
                if(nodeName.equals("sickNameEng")) getBugInfoRes.setSickNameEng(textContent);
                if(nodeName.equals("developmentCondition")) getBugInfoRes.setDevelopmentCondition(textContent);
                if(nodeName.equals("preventionMethod")) getBugInfoRes.setPreventionMethod(textContent);
                if(nodeName.equals("symptoms")) getBugInfoRes.setSymptoms(textContent);
                if(nodeName.equals("infectionRoute")) getBugInfoRes.setInfectionRoute(textContent);
            }
        }

        return getBugInfoRes;
    }

    @ResponseBody
    @GetMapping("/search")
    public BaseResponse<GetBugInfoRes> getBugInfo() throws BaseException, ParserConfigurationException, IOException, SAXException {

        GetBugInfoRes getBugInfoRes = getXmlData();

        return new BaseResponse<>(getBugInfoRes);
    }


}

