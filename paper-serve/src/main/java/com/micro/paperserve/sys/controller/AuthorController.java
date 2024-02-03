package com.micro.paperserve.sys.controller;

import java.io.IOException;
import java.util.List;

import com.micro.paperserve.sys.entity.Author;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthorController {
     public List<Author> parseAuthorsJson(String authorsJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(authorsJson, new TypeReference<List<Author>>() {});
    }
}
