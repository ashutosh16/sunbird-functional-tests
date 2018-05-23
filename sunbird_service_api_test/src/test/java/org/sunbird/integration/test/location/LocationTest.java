package org.sunbird.integration.test.location;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.builder.HttpClientActionBuilder.HttpClientReceiveActionBuilder;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.CitrusParameters;
import com.consol.citrus.validation.json.JsonMappingValidationCallback;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.response.ResponseCode;
import org.sunbird.common.util.Constants;
import org.sunbird.integration.test.common.BaseCitrusTest;
import org.sunbird.integration.test.user.EndpointConfig.TestGlobalProperty;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Class contains functional test cases for location APIs.
 * @author arvind.
 */
public class LocationTest extends BaseCitrusTest {


  private static String stateLocationId = null;

  private static String districtLocationId = null;
  private static final String CREATE_LOCATION_URI = "/v1/location/create";
  private static final String UPDATE_LOCATION_URI = "/v1/location/update";
  private static final String DELETE_LOCATION_URI = "/v1/location/delete";
  private static final String LOCATION_TEMPLATE_PATH = "templates/location/create/";
  private static final String LOCATION_TEMPLATE_PATH_UPDATE = "templates/location/update/";
  private static final String LOCATION_TEMPLATE_PATH_DELETE = "templates/location/delete/";

  private static final String STATE_CODE= "State-02";
  private static final String STATE_NAME= "State-0001-name";
  private static final String DISTRICT_CODE="District-02";
  private static final String DISTRICT_NAME="District-0001-name";
  private static Stack<String> stack = new Stack();


  @Autowired
  private HttpClient restTestClient;
  @Autowired
  private TestGlobalProperty initGlobalValues;
  private ObjectMapper objectMapper = new ObjectMapper();

  @DataProvider(name = "createStateLocationDynamicDataProvider")
  public Object [] [] createStateLocationDynamicDataProvider () {
    return new Object [] [] {
        new Object[] { createStateLocationMap(), LOCATION_TEMPLATE_PATH +"createLocationSuccessResponse.json", "createState" },
        new Object[] { createStateLocationMap(), LOCATION_TEMPLATE_PATH +"createLocationFailureResponseForDuplicateCode.json", "createLocationStateWithDupicateCode" },
    };
  }

  @DataProvider(name = "updateStateLocationDynamicDataProvider")
  public Object [] [] updateStateLocationDynamicDataProvider () {
    return new Object [] [] {
        new Object[] { updateStateLocationMap(), LOCATION_TEMPLATE_PATH_UPDATE+"update_location_name_success_response.json", "updateLocationNameForState"},
        new Object[] { updateStateLocationTypeMap(), LOCATION_TEMPLATE_PATH_UPDATE+"update_location_type_failure_response.json", "updateLocationNameForState"},

    };
  }

  @DataProvider(name = "createDistrictLocationDynamicDataProvider")
  public Object [] [] createDistrictLocationDynamicDataProvider () {
    return new Object [] [] {

        new Object[] { createDistrictLocationMap(), LOCATION_TEMPLATE_PATH +"createLocationSuccessResponse.json", "createLocationDistrict" },
        new Object[] { createDistrictLocationMap(), LOCATION_TEMPLATE_PATH +"createLocationFailureResponseForDuplicateCode.json", "createLocationDistrictWithDupicateCode" },
    };
  }

  @DataProvider(name = "updateDitrictLocationDynamicDataProvider")
  public Object [] [] updateDitrictLocationDynamicDataProvider () {
    return new Object [] [] {
        new Object[] { updateDistrictNameLocationMap(), LOCATION_TEMPLATE_PATH_UPDATE+"update_location_name_success_response.json", "updateLocationNameForDistrict"},
        new Object[] { updateDistrictLocationTypeMap(), LOCATION_TEMPLATE_PATH_UPDATE+"update_location_type_failure_response.json", "updateLocationNameForDistrict"},

    };
  }

  @DataProvider(name = "deleteStateLocationDynamicDataProvider")
  public Object [] [] deleteStateLocationDynamicDataProvider () {
    return new Object [] [] {
        new Object[] { LOCATION_TEMPLATE_PATH_DELETE +"delete_non_leaf_location_failure_response.json", "deleteNonLeafLocation" },

    };
  }

  @DataProvider(name = "deleteDistrictLocationDynamicDataProvider")
  public Object [] [] deleteDistrictLocationDynamicDataProvider () {
    return new Object [] [] {
        new Object[] { LOCATION_TEMPLATE_PATH_DELETE +"delete_location_with_invalid_id_failure_response.json", "deleteLocationWithInvalidId" },
        new Object[] { LOCATION_TEMPLATE_PATH_DELETE +"delete_location_success_response.json", "deleteLocationSuccess" },
    };
  }

  @Test(dataProvider = "createStateLocationDynamicDataProvider", priority = 1)
  @CitrusParameters({ "requestJson", "responseJson", "testName" })
  @CitrusTest
  /**
   * Method to test the create functionality of State type (root) location .The scenario are as -
   * 1. Successful creation of State type location.
   * 2. Try to create state type location with same location code and expect ABD_REQUEST in response..
   */
  public void testCreateLocationState(String requestJson, String responseJson, String testName) {
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    getTestCase().setName(testName);
    http().client(restTestClient).send().post(CREATE_LOCATION_URI).contentType(Constants.CONTENT_TYPE_APPLICATION_JSON)
        .header(Constants.AUTHORIZATION, Constants.BEARER + initGlobalValues.getApiKey())
        .payload(requestJson);
    if((LOCATION_TEMPLATE_PATH +"createLocationSuccessResponse.json").equals(responseJson)){
      HttpClientReceiveActionBuilder response = http().client(restTestClient).receive();
      stack.push("state");
      handleUserCreationResponse(response);
      System.out.println("State location id is "+stateLocationId);
    }
    if (!(LOCATION_TEMPLATE_PATH +"createLocationSuccessResponse.json").equals(responseJson)) {
      http().client(restTestClient).receive().response(HttpStatus.BAD_REQUEST)
          .payload(new ClassPathResource(responseJson));
    }
  }

  @Test(dataProvider = "updateStateLocationDynamicDataProvider", priority = 2)
  @CitrusParameters({ "requestJson", "responseJson", "testName" })
  @CitrusTest
  /**
   * Method to test the update functionality of State type (root) location .The scenario are as -
   * 1. Successful update of location name.
   * 2. Try to update the type of location and expect ABD_REQUEST in response..
   */
  public void testUpdateStateLocation(String requestJson, String responseJson, String testName) {
    getTestCase().setName(testName);
    http().client(restTestClient).send().patch(UPDATE_LOCATION_URI).contentType(Constants.CONTENT_TYPE_APPLICATION_JSON)
        .header(Constants.AUTHORIZATION, Constants.BEARER + initGlobalValues.getApiKey())
        .payload(requestJson);
    if((LOCATION_TEMPLATE_PATH_UPDATE+"update_location_name_success_response.json").equals(responseJson)){
      http().client(restTestClient).receive().response(HttpStatus.OK).payload(new ClassPathResource(responseJson));
    }else{
      http().client(restTestClient).receive().response(HttpStatus.BAD_REQUEST).payload(new ClassPathResource(responseJson));
    }
  }

  @Test(dataProvider = "createDistrictLocationDynamicDataProvider", priority = 3)
  @CitrusParameters({ "requestJson", "responseJson", "testName" })
  @CitrusTest
  /**
   * Method to test the create functionality of District type (intermediate) location .The scenario are as -
   * 1. Successful creation of District type location.
   * 2. Try to create state type location with same location code and expect ABD_REQUEST in response..
   */
  public void testCreateLocationDistrict(String requestJson, String responseJson, String testName) {
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    getTestCase().setName(testName);
    http().client(restTestClient).send().post(CREATE_LOCATION_URI).contentType(Constants.CONTENT_TYPE_APPLICATION_JSON)
        .header(Constants.AUTHORIZATION, Constants.BEARER + initGlobalValues.getApiKey())
        .payload(requestJson);
    if((LOCATION_TEMPLATE_PATH +"createLocationSuccessResponse.json").equals(responseJson)){
      HttpClientReceiveActionBuilder response = http().client(restTestClient).receive();
      stack.push("district");
      handleUserCreationResponse(response);
    }
    if (!(LOCATION_TEMPLATE_PATH +"createLocationSuccessResponse.json").equals(responseJson)) {
      http().client(restTestClient).receive().response(HttpStatus.BAD_REQUEST)
          .payload(new ClassPathResource(responseJson));
    }
  }

  @Test(dataProvider = "updateDitrictLocationDynamicDataProvider", priority = 4)
  @CitrusParameters({ "requestJson", "responseJson", "testName" })
  @CitrusTest
  /**
   * Method to test the update functionality of District type location .The scenario are as -
   * 1. Successful update of location name.
   * 2. Try to update the type of location and expect ABD_REQUEST in response..
   */
  public void testUpdateDistrictLocation(String requestJson, String responseJson, String testName) {
    getTestCase().setName(testName);
    http().client(restTestClient).send().patch(UPDATE_LOCATION_URI).contentType(Constants.CONTENT_TYPE_APPLICATION_JSON)
        .header(Constants.AUTHORIZATION, Constants.BEARER + initGlobalValues.getApiKey())
        .payload(requestJson);
    if((LOCATION_TEMPLATE_PATH_UPDATE+"update_location_name_success_response.json").equals(responseJson)){
      http().client(restTestClient).receive().response(HttpStatus.OK).payload(new ClassPathResource(responseJson));
    }else{
      http().client(restTestClient).receive().response(HttpStatus.BAD_REQUEST).payload(new ClassPathResource(responseJson));
    }
  }

  @Test(dataProvider = "deleteStateLocationDynamicDataProvider", priority = 5)
  @CitrusParameters({"responseJson", "testName" })
  @CitrusTest
  /**
   * Method to test the delete functionality of State(root) location .The scenario are as -
   * 1. Try to delete the location and expect ABD_REQUEST since there is child node exist for the state.
   */
  public void testDeleteStateLocation(String responseJson, String testName) {
    getTestCase().setName(testName);
    http().client(restTestClient).send().delete(DELETE_LOCATION_URI+"/"+stateLocationId).header(Constants.AUTHORIZATION, Constants.BEARER + initGlobalValues.getApiKey());
      http().client(restTestClient).receive().response(HttpStatus.BAD_REQUEST).payload(new ClassPathResource(responseJson));
  }

  @Test(dataProvider = "deleteDistrictLocationDynamicDataProvider", priority = 6)
  @CitrusParameters({"responseJson", "testName" })
  @CitrusTest
  /**
   * Method to test the delete functionality of District(root) location .The scenario are as -
   * 1. Try to delete the location with invalid location id and expect ABD_REQUEST .
   * 2. Delete district type location with success response.
   */
  public void testDeleteDistrictLocation(String responseJson, String testName) {
    getTestCase().setName(testName);
    if((LOCATION_TEMPLATE_PATH_DELETE +"delete_location_success_response.json").equals(responseJson)){
      http().client(restTestClient).send().delete(DELETE_LOCATION_URI+"/"+districtLocationId).header(Constants.AUTHORIZATION, Constants.BEARER + initGlobalValues.getApiKey());
      http().client(restTestClient).receive().response(HttpStatus.OK).payload(new ClassPathResource(responseJson));
    }else{
      http().client(restTestClient).send().delete(DELETE_LOCATION_URI+"/"+districtLocationId+"invalid").header(Constants.AUTHORIZATION, Constants.BEARER + initGlobalValues.getApiKey());
      http().client(restTestClient).receive().response(HttpStatus.BAD_REQUEST).payload(new ClassPathResource(responseJson));
    }
  }

  private void handleUserCreationResponse(HttpClientReceiveActionBuilder response){

    final String locationType = stack.pop();
    response.response()
        .validationCallback(new JsonMappingValidationCallback<Response>(Response.class, objectMapper) {
          @Override
          public void validate(Response response, Map<String, Object> headers, TestContext context) {
            Assert.assertNotNull(response.getId());
            Assert.assertEquals(response.getResponseCode(), ResponseCode.OK);
            Assert.assertNotNull(response.getResult().get("response"));
            String locationId = (String) response.getResult().get("id");
            Assert.assertNotNull(locationId);
            if(locationType.equalsIgnoreCase("state")){
              stateLocationId = locationId;
            }else if(locationType.equalsIgnoreCase("district")){
              districtLocationId = locationId;
            }
            List<String> cassandraList = toDeleteCassandraRecordsMap.get("location");
            if (cassandraList == null) {
              cassandraList = new ArrayList<>();
            }
            cassandraList.add(locationId);
            toDeleteCassandraRecordsMap.put("location", cassandraList);
            List<String> esList = toDeleteEsRecordsMap.get("location");
            if (esList == null) {
              esList = new ArrayList<>();
            }
            esList.add(locationId);
            toDeleteEsRecordsMap.put("location", esList);
          }
        });
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private String createStateLocationMap() {
    Map<String, Object> requestMap = new HashMap<>();
    Map<String, Object> innerMap = new HashMap<>();
    innerMap.put("code", STATE_CODE);
    innerMap.put("name", STATE_NAME);
    innerMap.put("parentCode", null);
    innerMap.put("type", "state");
    innerMap.put("parentId", null);
    requestMap.put("request", innerMap);
    try {
      return objectMapper.writeValueAsString(requestMap);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  private Object createDistrictLocationMap() {

    Map<String, Object> requestMap = new HashMap<>();
    Map<String, Object> innerMap = new HashMap<>();
    innerMap.put("code", DISTRICT_CODE);
    innerMap.put("name", DISTRICT_NAME);
    innerMap.put("type", "district");
    innerMap.put("parentCode", STATE_CODE);
    requestMap.put("request", innerMap);
    try {
      return objectMapper.writeValueAsString(requestMap);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  private Object updateStateLocationMap() {
    Map<String, Object> requestMap = new HashMap<>();
    Map<String, Object> innerMap = new HashMap<>();
    innerMap.put("name", STATE_NAME+01);
    innerMap.put("id", stateLocationId);
    requestMap.put("request", innerMap);
    try {
      return objectMapper.writeValueAsString(requestMap);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  private Object updateStateLocationTypeMap() {
    Map<String, Object> requestMap = new HashMap<>();
    Map<String, Object> innerMap = new HashMap<>();
    innerMap.put("type", "district");
    innerMap.put("id", stateLocationId);
    requestMap.put("request", innerMap);
    try {
      return objectMapper.writeValueAsString(requestMap);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  private Object updateDistrictNameLocationMap() {
    Map<String, Object> requestMap = new HashMap<>();
    Map<String, Object> innerMap = new HashMap<>();
    innerMap.put("name", DISTRICT_NAME+01);
    innerMap.put("id", districtLocationId);
    requestMap.put("request", innerMap);
    try {
      return objectMapper.writeValueAsString(requestMap);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  private Object updateDistrictLocationTypeMap() {
    Map<String, Object> requestMap = new HashMap<>();
    Map<String, Object> innerMap = new HashMap<>();
    innerMap.put("type", "state");
    innerMap.put("id", districtLocationId);
    requestMap.put("request", innerMap);
    try {
      return objectMapper.writeValueAsString(requestMap);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

}
