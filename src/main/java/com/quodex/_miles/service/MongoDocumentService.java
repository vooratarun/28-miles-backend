package com.quodex._miles.service;

import com.quodex._miles.io.MongoInsertRequest;
import com.quodex._miles.io.MongoInsertResponse;

public interface MongoDocumentService {

    MongoInsertResponse insertDocument(String collectionName, MongoInsertRequest request);
}
