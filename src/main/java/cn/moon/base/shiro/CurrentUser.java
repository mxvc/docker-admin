package cn.moon.base.shiro;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class CurrentUser {

    String username;

    Map<String, Set<String>> dataPerms = new HashMap<>();




}
