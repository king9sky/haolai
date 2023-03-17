package cn.autowit.haolai_take_out;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@ServletComponentScan
public class HaolaiApplication {
    public static void main(String[] args) {
        SpringApplication.run(HaolaiApplication.class,args);
        log.info("项目启动成功...");
    }
}
