package org.jkfm1241.com.r485_demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication()
// 屏蔽数据源启动
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
// 注解指定mapper接口位置
@MapperScan("org.jkfm1241.com.r485_demo.mapper")
public class R485DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(R485DemoApplication.class, args);
    }

}
