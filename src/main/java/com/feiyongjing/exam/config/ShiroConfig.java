package com.feiyongjing.exam.config;

import com.feiyongjing.exam.service.ShiroRealm;
import com.feiyongjing.exam.service.UserLoginInterceptor;
import com.feiyongjing.exam.service.UserService;
import com.feiyongjing.exam.utils.Encryption;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig implements WebMvcConfigurer {
    private static final String COOKIE_NAME = "rememberMe"; //  cookie name

    private static final int EXPIRY_TIME = 86400;
    private ShiroLoginFilter shiroLoginFilter;
    private UserService userService;
    private UserLoginInterceptor userLoginInterceptor;

    @Autowired
    public ShiroConfig(ShiroLoginFilter shiroLoginFilter, UserService userService, UserLoginInterceptor userLoginInterceptor) {
        this.shiroLoginFilter = shiroLoginFilter;
        this.userService = userService;
        this.userLoginInterceptor = userLoginInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userLoginInterceptor);
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, String> pattern = new HashMap<>();

        pattern.put("/api/v1/logon", "anon");
        pattern.put("/api/v1/login", "anon");
        pattern.put("/api/v1/status", "anon");
        pattern.put("/api/v1/logout", "anon");
        pattern.put("/api/v1/login.html","anon");
        pattern.put("/api/v1/logon.html","anon");
        pattern.put("/**", "authc");
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("shiroLoginFilter", shiroLoginFilter);
        shiroFilterFactoryBean.setFilters(filterMap);
        shiroFilterFactoryBean.setFilterChainDefinitionMap(pattern);
        return shiroFilterFactoryBean;
    }

    @Bean
    public SecurityManager securityManager(ShiroRealm shiroRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        securityManager.setRealm(shiroRealm);
        securityManager.setCacheManager(new MemoryConstrainedCacheManager());
        securityManager.setSessionManager(new DefaultWebSessionManager());
        securityManager.setRememberMeManager(rememberMeManager());
        SecurityUtils.setSecurityManager(securityManager);
        return securityManager;
    }
    public CookieRememberMeManager rememberMeManager() {
        SimpleCookie cookie = new SimpleCookie(COOKIE_NAME);
        cookie.setMaxAge(EXPIRY_TIME);
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(cookie);
        cookieRememberMeManager.setCipherKey(Base64.decode("3AvVhmFLUs0KTA3KaTHGFg=="));  // RememberMe cookie encryption key default AES algorithm of key length (128, 256, 512)
        return cookieRememberMeManager;
    }

    @Bean
    public ShiroRealm myShiroRealm() {
        return new ShiroRealm(userService);
    }

    @Bean
    public Encryption myEncryption() {
        return new Encryption();
    }
}
