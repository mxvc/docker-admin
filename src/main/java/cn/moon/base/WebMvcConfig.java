
package cn.moon.base;

import cn.moon.base.security.AntiClickjackingFilter;
import cn.moon.base.security.CSPFilter;
import cn.moon.base.security.PermissionsPolicyFilter;
import cn.moon.base.security.XContentTypeOptionsFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * web配置
 */
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public FilterRegistrationBean<CSPFilter> cspFilterRegistrationBean() {
        FilterRegistrationBean<CSPFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CSPFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<AntiClickjackingFilter> antClickFilterRegistrationBean() {
        FilterRegistrationBean<AntiClickjackingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AntiClickjackingFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<XContentTypeOptionsFilter> XContentTypeOptionsRegistrationBean() {
        FilterRegistrationBean<XContentTypeOptionsFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new XContentTypeOptionsFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
    @Bean
    public FilterRegistrationBean<PermissionsPolicyFilter> permissionsPolicyRegistrationBean() {
        FilterRegistrationBean<PermissionsPolicyFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new PermissionsPolicyFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }



}
