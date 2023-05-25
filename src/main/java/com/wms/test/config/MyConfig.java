package com.wms.test.config;

import com.wms.test.bean.Pet;
import com.wms.test.bean.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//配置类里面使用@Bean标注在方法上给容器注册组件，默认是单实例
//配置类本身也是组件
//proxyBeanMethods:代理bean的方法
@Configuration(proxyBeanMethods = true)
//告诉SpringBoot这是一个配置类
public class MyConfig {
    @Bean
    public User user01(){
        return new User("zhangzhang",18);
    }

    @Bean
    public Pet pet02(){
        return new Pet("ss");
    }

}
