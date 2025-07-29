package io.github.mxvc.docker.admin.entity.converter;

import cn.hutool.core.util.StrUtil;
import io.github.mxvc.docker.admin.entity.App;

import io.tmgg.jackson.JsonTool;
import jakarta.persistence.AttributeConverter;

public class AppConfigConverter implements AttributeConverter<App.AppConfig, String> {


    @Override
    public String convertToDatabaseColumn(App.AppConfig obj) {
        if (obj == null) {
            return null;
        }

        return JsonTool.toJsonQuietly(obj);

    }

    @Override
    public App.AppConfig convertToEntityAttribute(String dbData) {
        if (StrUtil.isBlank(dbData)) {
            return null;
        }
        try {
            return JsonTool.jsonToBean(dbData, App.AppConfig.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
