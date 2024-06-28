package org.example;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.List;
import java.util.Map;

public class EventService {

    private DynamoDBMapper dynamoDBMapper;
    private static  String jsonBody = null;

    public APIGatewayProxyResponseEvent saveDetails(APIGatewayProxyRequestEvent apiGatewayRequest, Context context){
        initDynamoDB();
        Entity entity = Utility.convertStringToObj(apiGatewayRequest.getBody(),context);
        dynamoDBMapper.save(entity);
        jsonBody = Utility.convertObjToString(entity,context) ;
        context.getLogger().log("data saved successfully to dynamodb:::" + jsonBody);
        return createAPIResponse(jsonBody,201,Utility.createHeaders());
    }
    public APIGatewayProxyResponseEvent getDetailsByID(APIGatewayProxyRequestEvent apiGatewayRequest, Context context){
        initDynamoDB();
        String scheduleId = apiGatewayRequest.getPathParameters().get("scheduleId");
        Entity employee =   dynamoDBMapper.load(Entity.class,scheduleId)  ;
        if(employee!=null) {
            jsonBody = Utility.convertObjToString(employee, context);
            context.getLogger().log("fetch employee By ID:::" + jsonBody);
            return createAPIResponse(jsonBody,200,Utility.createHeaders());
        }else{
            jsonBody = "Employee Not Found Exception :" + scheduleId;
            return createAPIResponse(jsonBody,400,Utility.createHeaders());
        }

    }

    public APIGatewayProxyResponseEvent getDetails(APIGatewayProxyRequestEvent apiGatewayRequest, Context context){
        initDynamoDB();
        List<Entity> entityList = dynamoDBMapper.scan(Entity.class,new DynamoDBScanExpression());
        jsonBody =  Utility.convertListOfObjToString(entityList,context);
        context.getLogger().log("fetch employee List:::" + jsonBody);
        return createAPIResponse(jsonBody,200,Utility.createHeaders());
    }
    public APIGatewayProxyResponseEvent deleteByID(APIGatewayProxyRequestEvent apiGatewayRequest, Context context){
        initDynamoDB();
        String scheduleId = apiGatewayRequest.getPathParameters().get("scheduleId");
        Entity load =  dynamoDBMapper.load(Entity.class,scheduleId)  ;
        if(load!=null) {
            dynamoDBMapper.delete(load);
            context.getLogger().log("data deleted successfully :::" + scheduleId);
            return createAPIResponse("data deleted successfully." + scheduleId,200,Utility.createHeaders());
        }else{
            jsonBody = "Employee Not Found Exception :" + scheduleId;
            return createAPIResponse(jsonBody,400,Utility.createHeaders());
        }
    }


    private void initDynamoDB(){
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        dynamoDBMapper = new DynamoDBMapper(client);
    }
    private APIGatewayProxyResponseEvent createAPIResponse(String body, int statusCode, Map<String,String> headers ){
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.setBody(body);
        responseEvent.setHeaders(headers);
        responseEvent.setStatusCode(statusCode);
        return responseEvent;
    }
}
