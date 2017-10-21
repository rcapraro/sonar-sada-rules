package com.sada.java.checks;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.java.ast.visitors.PublicApiChecker;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Checks the presence of at least one line of javadoc.
 * A javadoc should not contain only one line containing the tag <pre>@author</pre>
 */
@Rule(
        key = "InsufficientJavadoc",
        name = "Insufficient Javadoc content",
        description = "For a class, interface or enum, the javadoc should not be empty and should not begin only with the @author tag.",
        priority = Priority.MAJOR,
        tags = {"bug"})
public class InsufficientJavadocCheck extends BaseTreeVisitor implements JavaFileScanner {

    private JavaFileScannerContext context;

    private static List<String> cleanedlines(@Nullable String javadoc) {
        if (javadoc == null) {
            return Collections.emptyList();
        }
        String[] lines = cleanupJavadoc(javadoc).split("\\r?\\n");
        return Arrays.stream(lines).map(String::trim).collect(Collectors.toList());
    }

    private static String cleanupJavadoc(String javadoc) {
        // remove start and end of Javadoc as well as stars.
        return javadoc.trim().substring(3).replace("*/", "").replace("*", "").trim();
    }

    @Override
    public void scanFile(JavaFileScannerContext javaFileScannerContext) {
        // The call to the scan method on the root of the tree triggers the visit of the AST by this visitor
        this.context = javaFileScannerContext;
        scan(context.getTree());
    }

    @Override
    public void visitClass(ClassTree tree) {
        String javadoc = PublicApiChecker.getApiJavadoc(tree);
        List<String> lines = cleanedlines(javadoc);

        if (lines.isEmpty()) {
            context.reportIssue(this, tree, "Javadoc should not be empty");
        } else {
            String firstLine = lines.get(0);
            if (firstLine.equals("")) {
                context.reportIssue(this, tree, "Javadoc should not be empty");
            }
            if (firstLine.contains("@author")) {
                context.reportIssue(this, tree, "Javadoc should not begin only with @author");
            }
        }
    }
}
