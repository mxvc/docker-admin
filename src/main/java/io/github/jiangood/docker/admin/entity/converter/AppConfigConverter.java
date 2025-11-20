package io.github.jiangood.docker.admin.entity.converter;

import cn.hutool.core.util.StrUtil;
import io.admin.common.utils.JsonUtils;
import io.github.jiangood.docker.admin.entity.App;

import jakarta.persistence.AttributeConverter;

public class AppConfigConverter implements AttributeConverter<App.AppConfig, String> {


    @Override
    public String convertToDatabaseColumn(App.AppConfig obj) {
        if (obj == null) {
            return null;
        }

        return JsonUtils.toJsonQuietly(obj);

    }

    @Override
    public App.AppConfig convertToEntityAttribute(String dbData) {
        if (StrUtil.isBlank(dbData)) {
            return null;
        }
        try {
            return JsonUtils.jsonToBean(dbData, App.AppConfig.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
