package cn.moon.docker.admin.entity;

import cn.moon.base.Role;
import cn.moon.lang.web.persistence.BaseEntity;
import cn.moon.lang.web.persistence.ConvertToListConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 账号
 */
@Entity
@Getter
@Setter
@ToString
@FieldNameConstants
public class User extends BaseEntity {

    @NotNull
    @Column(unique = true)
    String username;


    // 只允许填，不允许查看
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    String password;


    String name;


    @NotNull
    @Enumerated(EnumType.STRING)
    Role role;





    @Lob
    @Convert(converter = ConvertToListConverter.class)
    List<String> dataPerms;

}
