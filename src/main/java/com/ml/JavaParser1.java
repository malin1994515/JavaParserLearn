package com.ml;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

import static com.github.javaparser.ParseStart.COMPILATION_UNIT;
import static com.github.javaparser.Providers.provider;

public class JavaParser1 {
    public static void main(String[] args) throws Exception {
        JavaParser javaParser = new JavaParser();
        ParseResult<CompilationUnit> result = javaParser.parse(COMPILATION_UNIT, provider("class x{}"));
        result.ifSuccessful(cu -> {
            System.out.println(cu);
        });
    }
}
