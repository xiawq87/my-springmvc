package com.xwq.handler;

import com.xwq.annotation.Controller;
import com.xwq.annotation.RequestMapping;
import com.xwq.annotation.RequestParam;

import java.io.File;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MappingHandler {
    private static Map<String, Object[]> urlMappingMap = new ConcurrentHashMap<>();

    static {
        try {
            // 这里写死
            String packageName = "com.xwq.controller";
            String packagePath = packageName.replace(".", "/");
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> urls = loader.getResources(packagePath);

            List<String> classNameList = new ArrayList<String>();

            while(urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if(url == null) continue;

                String path = url.getPath();

                classNameList.addAll(getClassNameFromFile(path));
            }

            for(String className : classNameList) {
                Class<?> clz = Class.forName(className);

                if(clz.isAnnotationPresent(Controller.class)) {
                    Method[] methods = clz.getMethods();

                    for(Method method : methods) {
                        if(method.isAnnotationPresent(RequestMapping.class)) {
                            RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                            String url = annotation.value();
                            urlMappingMap.put(url, new Object[]{clz, method});
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static List<String> getClassNameFromFile(String filePath) {
        List<String> classNameList = new ArrayList<String>();

        File file = new File(filePath);
        File[] subFiles = file.listFiles();

        if(null == subFiles) return classNameList;

        for(File subFile : subFiles) {
            if(subFile.isDirectory()) {
                classNameList.addAll(getClassNameFromFile(subFile.getPath()));
            } else {
                String subFilePath = subFile.getPath();

                if(subFilePath.endsWith(".class")) {
                    String classPath = subFilePath.substring(subFilePath.indexOf("\\classes\\") + 9, subFilePath.lastIndexOf("."));
                    String className = classPath.replace("\\", ".");
                    classNameList.add(className);
                }
            }
        }
        return classNameList;
    }


    public static Object execute(String url, Map<String, Object> paramMap) {
        try {
            Object[] objects = urlMappingMap.get(url);

            if(null != objects) {
                Class<?> clz = (Class<?>) objects[0];
                Method method = (Method) objects[1];

                Object instance = clz.newInstance();

                if(paramMap != null) {
                    int parameterCount = method.getParameterCount();
                    Parameter[] parameters = method.getParameters();
                    Object[] args = new Object[parameterCount];

                    for(int i=0; i< parameterCount; i++) {
                        Parameter parameter = parameters[i];

                        if(parameter.isAnnotationPresent(RequestParam.class)) {
                            RequestParam annotation = parameter.getAnnotation(RequestParam.class);
                            String paramName = annotation.value();
                            Object paramValue = paramMap.get(paramName);
                            args[i] = paramValue;
                        }
                    }

                    Object ret = method.invoke(instance, args);
                    return ret;
                }
                else {
                    Object ret = method.invoke(instance);
                    return ret;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
