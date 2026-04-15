package com.quodex._miles.service.Impl;

import com.quodex._miles.io.MongoInsertRequest;
import com.quodex._miles.io.MongoInsertResponse;
import com.quodex._miles.service.MongoDocumentService;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MongoDocumentServiceImpl implements MongoDocumentService {

    private final ObjectProvider<MongoTemplate> mongoTemplateProvider;

    @Override
    public MongoInsertResponse insertDocument(String collectionName, MongoInsertRequest request) {
        if (!StringUtils.hasText(collectionName)) {
            throw new IllegalArgumentException("Collection name is required");
        }

        if (request == null || request.getData() == null || request.getData().isEmpty()) {
            throw new IllegalArgumentException("Document data is required");
        }

        MongoTemplate mongoTemplate = mongoTemplateProvider.getIfAvailable();
        if (mongoTemplate == null) {
            throw new IllegalStateException("MongoDB is not enabled. Set app.mongodb.enabled=true");
        }

        Document document = new Document(request.getData());
        document.putIfAbsent("createdAt", Instant.now().toString());

        Document insertedDocument = mongoTemplate.insert(document, collectionName);

        Map<String, Object> responseData = new LinkedHashMap<>(insertedDocument);
        Object id = responseData.remove("_id");

        MongoInsertResponse response = new MongoInsertResponse();
        response.setId(id != null ? id.toString() : null);
        response.setCollectionName(collectionName);
        response.setData(responseData);
        return response;
    }
}
