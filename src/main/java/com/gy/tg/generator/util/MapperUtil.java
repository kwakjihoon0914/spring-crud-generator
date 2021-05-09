package com.gy.tg.generator.util;

import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;

public class MapperUtil {

    private static final ModelMapper modelMapper = new ModelMapper();

    public static <D> D map(Object src, Type targetClass){
        return modelMapper.map(src, targetClass);
    }
}
