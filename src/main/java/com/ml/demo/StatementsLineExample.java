package com.ml.demo;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.stmt.Statement;
import com.google.common.base.Strings;

import java.io.File;
import java.io.IOException;

public class StatementsLineExample {
    public static void statementsByLine(File projectDir) {
        new DirExplorer(((level, path, file) -> path.endsWith(".java") && !path.contains("link_to_ImageManagerImpl.java")), ((level, path, file) -> {
            System.out.println(path);
            System.out.println(Strings.repeat("=", path.length()));

            try {
                new NodeIterator(node -> {
                    if (node instanceof Statement) {
                        System.out.println(" [Lines " + node.getBegin() + " - " + node.getEnd() + " ] " + node);
                        return false;
                    } else {
                        return true;
                    }
                }).explore(StaticJavaParser.parse(file));
                System.out.println();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        })).explore(projectDir);
    }

    public static void main(String[] args) {
        File projectDir = new File("E:\\workspace\\zstack\\longjob\\src\\main\\java\\org\\zstack\\longjob");
        statementsByLine(projectDir);
    }
}
