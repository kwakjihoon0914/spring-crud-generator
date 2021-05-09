package com.gy.tg.generator.controller;


import com.gy.tg.generator.model.CreatedController;
import com.gy.tg.generator.model.CreatedModel;
import com.gy.tg.generator.util.SourceGenerator;
import com.gy.tg.generator.util.TemplateType;
import org.apache.logging.log4j.util.Strings;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.jibx.schema.codegen.extend.DefaultNameConverter;
import org.jibx.schema.codegen.extend.NameConverter;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
public class GeneratorController {
    private final NameConverter nameTools = new DefaultNameConverter();


    @PostMapping("/create")
    public Map<String,String> makeTemplate(HttpServletResponse res, @RequestBody CreatedModel createdModel) throws IOException {


        String entityName = createdModel.getEntityClassName();
        entityName = (String.valueOf(entityName.charAt(0))).toLowerCase() + entityName.substring(1);

        createdModel.setEntityName(entityName);
        createdModel.setPackagePath(createdModel.getPackagePath() + "." + entityName.toLowerCase());
        createdModel.setEntityPluralName(nameTools.pluralize(entityName));


        Map<String,String> map = new HashMap<>();
        map.put(TemplateType.CONTROLLER, SourceGenerator.generate(TemplateType.CONTROLLER,createdModel));
        map.put(TemplateType.REPOSITORY, SourceGenerator.generate(TemplateType.REPOSITORY,createdModel));
        map.put(TemplateType.ENTITY, SourceGenerator.generate(TemplateType.ENTITY,createdModel));
        map.put(TemplateType.DTO, SourceGenerator.generate(TemplateType.DTO,createdModel));
        map.put(TemplateType.SERVICE, SourceGenerator.generate(TemplateType.SERVICE,createdModel));
        map.put(TemplateType.SERVICE_IMPL, SourceGenerator.generate(TemplateType.SERVICE_IMPL,createdModel));


        return map;

    }


    @PostMapping("/create-file")
    public void makeTemplateAsFile(HttpServletResponse res, @RequestBody CreatedModel createdModel) throws IOException {

        String entityName = createdModel.getEntityClassName();
        entityName = (String.valueOf(entityName.charAt(0))).toLowerCase() + entityName.substring(1);

        createdModel.setEntityName(entityName);
        createdModel.setPackagePath(createdModel.getPackagePath() + "." + entityName.toLowerCase());
        createdModel.setEntityPluralName(nameTools.pluralize(entityName));

        Map<String,String> map = new HashMap<>();
        map.put(TemplateType.CONTROLLER, SourceGenerator.generate(TemplateType.CONTROLLER,createdModel));
        map.put(TemplateType.REPOSITORY, SourceGenerator.generate(TemplateType.REPOSITORY,createdModel));
        map.put(TemplateType.ENTITY, SourceGenerator.generate(TemplateType.ENTITY,createdModel));
        map.put(TemplateType.DTO, SourceGenerator.generate(TemplateType.DTO,createdModel));
        map.put(TemplateType.SERVICE, SourceGenerator.generate(TemplateType.SERVICE,createdModel));
        map.put(TemplateType.SERVICE_IMPL, SourceGenerator.generate(TemplateType.SERVICE_IMPL,createdModel));

        res.getOutputStream().write(createTemplate(createdModel,map));
        res.setHeader("content-disposition","filename="+entityName.toLowerCase()+".zip");


    }
    private static byte [] createTemplate(CreatedModel createdModel,Map<String,String> map) throws IOException {

        String prefix = createdModel.getEntityClassName();
        Iterator<String> keys = map.keySet().iterator();

        String filePathPrefix = "src/main/resources/temp/";
        int rand = (int) (Math.random()*1000);
        String filePathPrefixWithRand =  filePathPrefix + rand + "/";

        while (keys.hasNext()){
            String key = keys.next();
            String source = map.get(key);
            String targetDir = filePathPrefixWithRand + key.toLowerCase() + "/";
            byte [] binary = source.getBytes();

            new File(targetDir).mkdirs();

            String fileName = prefix + key + ".java";
            if (key.equals(TemplateType.ENTITY)) fileName = prefix +".java";

            File file = new File(targetDir+fileName);

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(binary);
            fos.close();
        }
        Path src = Paths.get(filePathPrefixWithRand);
        Path dest = Paths.get(filePathPrefix+rand+"."+createdModel.getEntityName().toLowerCase()+".zip");
        pack(src,dest);

        deleteDirectoryJava8Extract(src);


        File finalizeFile = new File(dest.toAbsolutePath().toUri());
        FileInputStream fis = new FileInputStream(finalizeFile);

        byte [] binary = fis.readAllBytes();
        fis.close();

        return binary;
    }

    private static void deleteDirectoryJava8Extract(Path path) {
        try {
            FileUtils.deleteDirectory(new File(path.toAbsolutePath().toUri()));
        } catch (IOException e) {
            System.err.printf("Unable to delete this path : %s%n%s", path, e);
        }
    }

    private static void pack(final Path folder, final Path zipFilePath) throws IOException {
        try (
                FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
                ZipOutputStream zos = new ZipOutputStream(fos)
        ) {
            Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    zos.putNextEntry(new ZipEntry(folder.relativize(file).toString()));
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }

                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    zos.putNextEntry(new ZipEntry(folder.relativize(dir).toString() + "/"));
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }


}
