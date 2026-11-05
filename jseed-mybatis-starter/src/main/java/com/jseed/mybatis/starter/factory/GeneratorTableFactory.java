package com.jseed.mybatis.starter.factory;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;
import java.util.Objects;

/**
 * MyBatis code generator factory
 *
 * @author cyrus
 * @date: 2024-07-19
 */
public class GeneratorTableFactory {
    /**
     * Gets the calling class.
     *
     * @return
     * @throws ClassNotFoundException
     */
    private static Class<?> getTopClass() throws ClassNotFoundException {
        StackTraceElement[] stackTraceElement = new Exception().getStackTrace();
        // Get the caller.
        StackTraceElement top = stackTraceElement[2];
        return Class.forName(top.getClassName());
    }

    /**
     * Gets the output path.
     *
     * @param clazz
     * @return
     */
    private static String getGeneratorPath(Class<?> clazz) {
        String path = Objects.requireNonNull(clazz.getResource("/")).getPath();
        path = path.substring(0, path.indexOf("/target/test-classes/"));
        System.out.println("输出模块路径：" + path);
        return path;
    }

    /**
     * Gets the base package of the current module.
     *
     * @param clazz
     * @return
     */
    private static String getBasePackage(Class<?> clazz) {
        try {
            String basePackageName = clazz.getPackage().getName();
            if (!basePackageName.contains(".test")) {
                throw new RuntimeException("请在test包下进行代码生成");
            }
            basePackageName = basePackageName.substring(0, basePackageName.lastIndexOf(".test"));
            System.out.println("输出包路径：" + basePackageName);
            return basePackageName;
        } catch (Exception e) {
            throw new RuntimeException("请在test包下进行代码生成");
        }
    }

    /**
     * Generates MyBatis code.
     * Run from a test package.
     *
     * @param host     database host
     * @param port     database port
     * @param dbName   database name
     * @param userName database username
     * @param password database password
     */
    public static void generate(String host, int port, String dbName, String userName, String password) throws ClassNotFoundException {
        // Gets the calling class.
        Class<?> topClass = getTopClass();
        String generatorPath = getGeneratorPath(topClass);
        String basePackage = getBasePackage(topClass);
        //Configure the MyBatis code generator.
        FastAutoGenerator.create(
                        "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8&allowMultiQueries=true",
                        userName,
                        password
                )
                //Global settings
                .globalConfig(it -> it.author("cyrus")
                        .disableOpenDir()
                        // Overwrite generated files.
                        .outputDir(generatorPath + "/src/main/java"))
                //Package settings
                .packageConfig(it -> {
                    // Set the package for generated entities.
                    it.entity("po")
                            //Set the parent package.
                            .parent(basePackage).pathInfo(
                                    Collections.singletonMap(
                                            OutputFile.xml,
                                            generatorPath + "/src/main/resources/mapper"
                                    ));
                })
                .injectionConfig(it -> {
                })
                //Generation strategy
                .strategyConfig(
                        (scanner, builder) -> builder.addInclude(scanner.apply("输入表名称，多的请用逗号分隔").split(","))
                                .addTablePrefix("t_", "f_", "p_")
                                .entityBuilder()
                                .disableSerialVersionUID()
                                .enableLombok()
                                // Set the generated entity name.
                                .formatFileName("%sPO")
                                .build()
                                // Generate common field mappings and select columns in the mapper.
                                .mapperBuilder()
                                .enableBaseResultMap()
                                .enableBaseColumnList())
                //Use FreeMarker templates. The default engine is Velocity.
                .templateEngine(new FreemarkerTemplateEngine())
                // Skip controller generation.
                .templateConfig(builder -> builder.controller(""))
                // Run the generator.
                .execute();
    }
}
