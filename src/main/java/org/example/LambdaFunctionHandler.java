package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class LambdaFunctionHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {


    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        EventService eventService=new EventService();

        switch (apiGatewayProxyRequestEvent.getHttpMethod()){
            case "POST":
                return eventService.saveDetails(apiGatewayProxyRequestEvent,context);
            case "GET":
                if(apiGatewayProxyRequestEvent.getPathParameters()!=null) {
                    return eventService.getDetailsByID(apiGatewayProxyRequestEvent, context);
                }
                return eventService.getDetails(apiGatewayProxyRequestEvent,context);

            case "DELETE":
                if(apiGatewayProxyRequestEvent.getPathParameters()!=null) {
                    return eventService.deleteByID(apiGatewayProxyRequestEvent, context);
                }
            default:
                throw new Error("unsupportedMethod "+apiGatewayProxyRequestEvent.getHttpMethod());
        }

    }
}
