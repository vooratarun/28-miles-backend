package com.quodex._miles.io;

import java.util.Map;

public class MongoInsertRequest {

    private Map<String, Object> data;

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
