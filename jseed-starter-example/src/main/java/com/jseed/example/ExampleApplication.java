// SPDX-License-Identifier: Apache-2.0
package com.jseed.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author cyrus
 */
@SpringBootApplication
@MapperScan(
        basePackageClasses = ExampleApplication.class,
        annotationClass = org.apache.ibatis.annotations.Mapper.class)
public class ExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }
}
