package com.ml;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MethodCallFinder {

    private static final String TARGET_METHOD_NAME = "setError";
    private static final String TARGET_CLASS_NAME = "APIEvent";

    public static void main(String[] args) {
        // 示例参数
        String directoryPath = "E:\\workspace\\zstack\\image"; // 目录路径
        String targetClass = "APIEvent"; // 目标类
        String targetMethod = "setError"; // 目标方法

        // 调用方法
        findMethodCalls(directoryPath, targetClass, targetMethod);
    }

    public static void findMethodCalls(String directoryPath, String targetClass, String targetMethod) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("无效的目录路径: " + directoryPath);
            return;
        }

        // 获取目标类及其子类
        Set<String> targetClasses = getTargetClasses(directory, targetClass);

        // 遍历目录下的所有 Java 文件
        traverseDirectory(directory, targetClasses, targetMethod);
    }

    private static Set<String> getTargetClasses(File directory, String targetClass) {
        Set<String> targetClasses = new HashSet<>();
        targetClasses.add(targetClass);

        /*// 查找目标类的子类
        List<ClassOrInterfaceDeclaration> classes = new ArrayList<>();
        findClasses(directory, classes);

        for (ClassOrInterfaceDeclaration clazz : classes) {
            if (clazz.getExtendedTypes().stream().anyMatch(t -> t.resolve().toString().equals(targetClass))) {
                targetClasses.add(clazz.resolve().getQualifiedName());
            }

        }*/

        return targetClasses;
    }

    private static void findClasses(File directory, List<ClassOrInterfaceDeclaration> classes) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    findClasses(file, classes);
                } else if (file.getName().endsWith(".java")) {
                    try {
                        CompilationUnit cu = StaticJavaParser.parse(file);
                        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(classes::add);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void traverseDirectory(File directory, Set<String> targetClasses, String targetMethod) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    traverseDirectory(file, targetClasses, targetMethod);
                } else if (file.getName().endsWith(".java")) {
                    processFile(file, targetClasses, targetMethod);
                }
            }
        }
    }

    private static void processFile(File file, Set<String> targetClasses, String targetMethod) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(file);
            List<MethodCallExpr> methodCalls = cu.findAll(MethodCallExpr.class);

            for (MethodCallExpr methodCall : methodCalls) {
                if (methodCall.getNameAsString().equals(targetMethod)) {
                    // 获取方法调用所在的类
                    Optional<ClassOrInterfaceDeclaration> enclosingClass = methodCall.findAncestor(ClassOrInterfaceDeclaration.class);
                    if (enclosingClass.isPresent()) {
                        String className = enclosingClass.get().resolve().getQualifiedName();
                        if (targetClasses.contains(className)) {
                            // 打印调用的位置和代码行
                            System.out.println("文件: " + file.getPath());
                            System.out.println("行号: " + methodCall.getBegin().get().line);
                            System.out.println("代码: " + methodCall);
                            System.out.println();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}