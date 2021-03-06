package org.sunbird.kp.test.content.v3;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import org.springframework.http.HttpStatus;
import org.sunbird.kp.test.common.APIUrl;
import org.sunbird.kp.test.common.BaseCitrusTestRunner;
import org.sunbird.kp.test.common.Constant;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import java.util.Map;

/**
 * Integration Test Cases for Content Read API
 * @author Kumar Gauraw
 */
public class CreateContentTest extends BaseCitrusTestRunner {

    private static final String TEMPLATE_DIR = "templates/content/v3";

    @Test(dataProvider = "createResourceContentWithValidRequest")
    @CitrusParameters({"testName", "requestUrl", "httpStatusCode", "userType","valParams"})
    @CitrusTest
    public void testCreateResourceContentWithValidRequest(
            String testName, String requestUrl, HttpStatus httpStatusCode, String userType, Map<String, Object> valParams) {
        getAuthToken(this, userType);
        performPostTest(
                this,
                TEMPLATE_DIR,
                testName,
                requestUrl,
                null,
                REQUEST_JSON,
                MediaType.APPLICATION_JSON,
                httpStatusCode,
                valParams,
                RESPONSE_JSON
        );
    }

    @DataProvider(name = "createResourceContentWithValidRequest")
    public Object[][] createResourceContentWithValidRequest() {
        return new Object[][]{
                // Sample Request for Dynamic Validation.
                // If validationParams Map Passed, Static Validation based on Response File Will be disabled.
                /*new Object[]{
                        ContentV3Scenario.TEST_CREATE_RESOURCE_PDF_CONTENT_WITH_VALID_REQUEST, APIUrl.CREATE_CONTENT, HttpStatus.OK, "Creator",
                        new HashMap<String, Object>(){{put("node_id",null);put("versionKey",null);put("mimeType","application/pdf");}}
                },*/
                new Object[]{
                        ContentV3Scenario.TEST_CREATE_RESOURCE_PDF_CONTENT_WITH_VALID_REQUEST, APIUrl.CREATE_CONTENT, HttpStatus.OK, Constant.CREATOR, null
                },
                new Object[]{
                        ContentV3Scenario.TEST_CREATE_RESOURCE_ECML_CONTENT_WITH_VALID_REQUEST, APIUrl.CREATE_CONTENT, HttpStatus.OK, Constant.CREATOR, null
                },
                new Object[]{
                        ContentV3Scenario.TEST_CREATE_RESOURCE_HTML_CONTENT_WITH_VALID_REQUEST, APIUrl.CREATE_CONTENT, HttpStatus.OK, Constant.CREATOR, null
                },
                new Object[]{
                        ContentV3Scenario.TEST_CREATE_RESOURCE_H5P_CONTENT_WITH_VALID_REQUEST, APIUrl.CREATE_CONTENT, HttpStatus.OK, Constant.CREATOR, null
                },
                new Object[]{
                        ContentV3Scenario.TEST_CREATE_RESOURCE_YOUTUBE_CONTENT_WITH_VALID_REQUEST, APIUrl.CREATE_CONTENT, HttpStatus.OK, Constant.CREATOR, null
                },
                new Object[]{
                        ContentV3Scenario.TEST_CREATE_RESOURCE_VIDEO_MP4_CONTENT_WITH_VALID_REQUEST, APIUrl.CREATE_CONTENT, HttpStatus.OK, Constant.CREATOR, null
                },
                new Object[]{
                        ContentV3Scenario.TEST_CREATE_RESOURCE_VIDEO_MPEG_CONTENT_WITH_VALID_REQUEST, APIUrl.CREATE_CONTENT, HttpStatus.OK, Constant.CREATOR, null
                },
                new Object[]{
                        ContentV3Scenario.TEST_CREATE_PLUGIN_CONTENT_WITH_VALID_REQUEST, APIUrl.CREATE_CONTENT, HttpStatus.OK, Constant.CREATOR, null
                },
                new Object[]{
                        ContentV3Scenario.TEST_CREATE_ASSET_VIDEO_MP4_CONTENT_WITH_VALID_REQUEST, APIUrl.CREATE_CONTENT, HttpStatus.OK, Constant.CREATOR, null
                },
                new Object[]{
                        ContentV3Scenario.TEST_CREATE_ASSET_VIDEO_MPEG_CONTENT_WITH_VALID_REQUEST, APIUrl.CREATE_CONTENT, HttpStatus.OK, Constant.CREATOR, null
                },
                new Object[]{
                        ContentV3Scenario.TEST_CREATE_ASSET_VIDEO_WEBM_CONTENT_WITH_VALID_REQUEST, APIUrl.CREATE_CONTENT, HttpStatus.OK, Constant.CREATOR, null
                },
                new Object[]{
                        ContentV3Scenario.TEST_CREATE_ASSET_IMAGE_JPEG_CONTENT_WITH_VALID_REQUEST, APIUrl.CREATE_CONTENT, HttpStatus.OK, Constant.CREATOR, null
                },
                new Object[]{
                        ContentV3Scenario.TEST_CREATE_ASSET_IMAGE_PNG_CONTENT_WITH_VALID_REQUEST, APIUrl.CREATE_CONTENT, HttpStatus.OK, Constant.CREATOR, null
                }
        };
    }
}
