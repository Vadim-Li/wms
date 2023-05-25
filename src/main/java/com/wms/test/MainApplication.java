package com.wms.test;

import com.wms.test.bean.Pet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {
        //返回ioc容器
        ConfigurableApplicationContext run= SpringApplication.run(MainApplication.class,args
        );
        //查看容器里面的组件
//        String[] names =run.getBeanDefinitionNames();
//        for(String name :names){
//            System.out.println(name);
//        }
        //从容器中获取组件
        Pet pet02 = run.getBean("pet02", Pet.class);
        Pet pet01 = run.getBean("pet02", Pet.class);
        System.out.println("组件："+(pet01==pet02));

    }
}
