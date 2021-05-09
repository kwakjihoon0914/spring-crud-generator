package com.gy.tg.generator.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.gy.tg.generator.model.CreatedController;
import com.gy.tg.generator.model.CreatedModel;
import org.apache.commons.text.StringSubstitutor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class SourceGenerator {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String getTemplateSource(String type){
        return TemplateSource.get(type);
    }

    public static String generate(String type, CreatedModel createdModel) {

        String codeTemplate = getTemplateSource(type);

        // object to map
        Map<String,String> model = objectMapper.convertValue(createdModel,Map.class);

        // replace to params
        StringSubstitutor sub = new StringSubstitutor(model);
        String source = sub.replace(codeTemplate);
        String formattedSource ="";

        try{
            formattedSource = new Formatter().formatSource(source);
        }catch (Exception ex){

            System.out.println(source);
            ex.printStackTrace();
        }


        return formattedSource;
    }



}
