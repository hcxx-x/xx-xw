package com.xx.web.client;

import com.dtflys.forest.annotation.Request;
import com.xx.web.interceptor.forest.SimpleInterceptor;

/**
 * @author hanyangyang
 */

public interface ForestTestClient {
    @Request(url="http://localhost:6606/forest/getValue", interceptor = {SimpleInterceptor.class})
    String helloForest();
}
