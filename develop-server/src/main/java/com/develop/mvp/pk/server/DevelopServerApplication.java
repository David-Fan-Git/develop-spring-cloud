package com.develop.mvp.pk.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * develop-server 是默认单体启动容器，通过 Maven 依赖组合实际启用的业务模块。
 * 启动问题优先参考项目根目录 CLAUDE.md 中的 Maven 命令与当前 profile 配置。
 *
 * @author David
 */
@SuppressWarnings("SpringComponentScan") // IDEA 无法识别 ${develop.info.base-package} 占位符，运行时由 Spring 正常解析
@SpringBootApplication(scanBasePackages = {"${develop.info.base-package}.server", "${develop.info.base-package}.module"},
        excludeName = {
            // RPC 相关
//            "org.springframework.cloud.openfeign.FeignAutoConfiguration",
//            "com.develop.mvp.pk.module.system.framework.rpc.config.RpcConfiguration"
        })
public class DevelopServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevelopServerApplication.class, args);
//        new SpringApplicationBuilder(DevelopServerApplication.class)
//                .applicationStartup(new BufferingApplicationStartup(20480))
//                .run(args);
    }

}
