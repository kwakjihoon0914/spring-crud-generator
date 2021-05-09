package com.gy.tg.generator.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.File;


@Setter
@Getter
public class CreatedModel {


    public String mapperUtilPackagePath;
    public String packagePath;

    public String entityName;
    public String entityClassName;
    public String entityIdTypeName;
    public String entityPluralName;


    public String entityTableName;
    public String entityIdColumnName;
    public String entityIdName;






}
