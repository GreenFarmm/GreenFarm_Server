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

    void getXmlData(GetBugInfoRes getBugInfoRes, String sickKey) throws ParserConfigurationException, IOException, SAXException {

        String Url = "http://ncpms.rda.go.kr/npmsAPI/service?" +
                "apiKey=2022b5d55c3ea9fac00003b87fa2ed6e69f3" +
                "&serviceCode=SVC05" +
                "&sickKey=" +
                sickKey;

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
    }

    /**
     * 병해충 정보 조회 및 db 저장
     * @param getBugInfoReq
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    @ResponseBody
    @GetMapping("/search")
    public BaseResponse<GetBugInfoRes> getBugInfo(@RequestParam String user_id, @RequestBody GetBugInfoReq getBugInfoReq) throws ParserConfigurationException, IOException, SAXException, BaseException {

        /*
        * sickCode (병해충 코드)
        * < 팥 >
        * 1. 흰가루병 (powdery mildew) : D00001596
        * 2. 세균잎마름병 (Bacterial leaf blight) : 지원 x
        * 3. Rhizopus :
        * < 참깨 >
        * 1. 세균성점무늬병 (Bacterial leaf spo) : D00002210
        * 2. 흰가루병 (Powdery mildew) : D00002218
        * < 콩 >
        * 1. 노균병 (Downy mildew) : D00001463
        * 2. 불마름병 (Bacterial pustule) : 지원 x
        * */

        // jwt token 검증
        jwtService.JwtEffectiveness(user_id, jwtService.getUserId());

        GetBugInfoRes getBugInfoRes = new GetBugInfoRes();

        // 병해충 정보 조회
        if(getBugInfoReq.getSickName().equals("powdery mildew"))  getXmlData(getBugInfoRes, "D00001596");

        // db에 저장
        bugService.saveBugInfo(user_id,getBugInfoRes);

        return new BaseResponse<>(getBugInfoRes);
    }


}

