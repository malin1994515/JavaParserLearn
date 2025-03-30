package com.ml.demo;

import com.github.javaparser.ParseException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.base.Strings;

import java.io.File;
import java.io.IOException;

public class MethodCallsExample {
    public static void listMethodCalls(File projectDir) {
        new DirExplorer(((level, path, file) -> path.endsWith(".java") && !path.contains("link_to_ImageManagerImpl.java")), ((level, path, file) -> {
            System.out.println(path);
            System.out.println(Strings.repeat("=", path.length()));
            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(MethodCallExpr n, Object arg) {
                        super.visit(n, arg);
                        System.out.println(" [L " + n.getBegin() + "] " + n);
                    }
                }.visit(StaticJavaParser.parse(file), null);
                System.out.println(); // empty line
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        })).explore(projectDir);
    }

    public static void main(String[] args) {
        File projectDir = new File("E:\\workspace\\zstack");
        listMethodCalls(projectDir);
    }
}
