package org.zstack.sdk;

import java.util.HashMap;
import java.util.Map;

public class CreateDahoVllRemoteAction extends AbstractAction {

    private static final HashMap<String, Parameter> parameterMap = new HashMap<>();

    private static final HashMap<String, Parameter> nonAPIParameterMap = new HashMap<>();

    public static class Result {
        public ErrorCode error;
        public CreateDahoVllRemoteResult value;

        public Result throwExceptionIfError() {
            if (error != null) {
                throw new ApiException(
                    String.format("error[code: %s, description: %s, details: %s]", error.code, error.description, error.details)
                );
            }
            
            return this;
        }
    }

    @Param(required = true, maxLength = 32, nonempty = false, nullElements = false, emptyString = false, noTrim = false)
    public java.lang.String name;

    @Param(required = false, maxLength = 128, nonempty = false, nullElements = false, emptyString = false, noTrim = false)
    public java.lang.String description;

    @Param(required = true, nonempty = false, nullElements = false, emptyString = false, numberRange = {1L,10240L}, noTrim = false)
    public java.lang.Integer bandwidth;

    @Param(required = true, validValues = {"shutdown","renewal"}, nonempty = false, nullElements = false, emptyString = true, noTrim = false)
    public java.lang.String expirePolicy;

    @Param(required = true, nonempty = false, nullElements = false, emptyString = true, noTrim = false)
    public java.lang.String dcConnUuid;

    @Param(required = true, nonempty = false, nullElements = false, emptyString = true, noTrim = false)
    public java.lang.String cloudConnUuid;

    @Param(required = true, nonempty = false, nullElements = false, emptyString = true, noTrim = false)
    public java.lang.Integer vlan;

    @Param(required = false)
    public java.lang.String resourceUuid;

    @Param(required = false)
    public java.util.List systemTags;

    @Param(required = false)
    public java.util.List userTags;

    @Param(required = true)
    public String sessionId;

    @NonAPIParam
    public long timeout = -1;

    @NonAPIParam
    public long pollingInterval = -1;


    private Result makeResult(ApiResult res) {
        Result ret = new Result();
        if (res.error != null) {
            ret.error = res.error;
            return ret;
        }
        
        CreateDahoVllRemoteResult value = res.getResult(CreateDahoVllRemoteResult.class);
        ret.value = value == null ? new CreateDahoVllRemoteResult() : value; 

        return ret;
    }

    public Result call() {
        ApiResult res = ZSClient.call(this);
        return makeResult(res);
    }

    public void call(final Completion<Result> completion) {
        ZSClient.call(this, new InternalCompletion() {
            @Override
            public void complete(ApiResult res) {
                completion.complete(makeResult(res));
            }
        });
    }

    Map<String, Parameter> getParameterMap() {
        return parameterMap;
    }

    Map<String, Parameter> getNonAPIParameterMap() {
        return nonAPIParameterMap;
    }

    RestInfo getRestInfo() {
        RestInfo info = new RestInfo();
        info.httpMethod = "POST";
        info.path = "/hybrid/daho/vlls";
        info.needSession = true;
        info.needPoll = true;
        info.parameterName = "params";
        return info;
    }

}
