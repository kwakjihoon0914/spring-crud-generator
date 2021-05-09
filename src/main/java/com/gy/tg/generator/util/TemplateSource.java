package com.gy.tg.generator.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class TemplateSource {


    private static HashMap<String,String> sourceMap;
    static {
        sourceMap = new HashMap<>();

        final String CONTROLLER_TEMPLATE = readTemplateFile("src/main/resources/static/templates/ControllerTemplate.txt");
        final String REPOSITORY_TEMPLATE = readTemplateFile("src/main/resources/static/templates/RepositoryTemplate.txt");
        final String ENTITY_TEMPLATE = readTemplateFile("src/main/resources/static/templates/EntityTemplate.txt");
        final String DTO_TEMPLATE = readTemplateFile("src/main/resources/static/templates/DtoTemplate.txt");
        final String SERVICE_TEMPLATE = readTemplateFile("src/main/resources/static/templates/ServiceTemplate.txt");
        final String SERVICE_IMPL_TEMPLATE = readTemplateFile("src/main/resources/static/templates/ServiceImplTemplate.txt");


        sourceMap.put(TemplateType.SERVICE, SERVICE_TEMPLATE);
        sourceMap.put(TemplateType.SERVICE_IMPL, SERVICE_IMPL_TEMPLATE);

        sourceMap.put(TemplateType.CONTROLLER, CONTROLLER_TEMPLATE);
        sourceMap.put(TemplateType.ENTITY, ENTITY_TEMPLATE);
        sourceMap.put(TemplateType.REPOSITORY, REPOSITORY_TEMPLATE);
        sourceMap.put(TemplateType.DTO, DTO_TEMPLATE);

    }
    private static String readTemplateFile(String path) {
        File file = new File(path);
        StringBuilder source = new StringBuilder();
        try{
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            while ((line = br.readLine())!=null) {
                source.append(line);
                source.append(System.getProperty("line.separator"));
            }
            br.close();
        }catch (Exception ex){

            ex.printStackTrace();
        }
        return source.toString();
    }

    public static String get(String type){
        return sourceMap.containsKey(type) ? sourceMap.get(type) : "";
    }
}
