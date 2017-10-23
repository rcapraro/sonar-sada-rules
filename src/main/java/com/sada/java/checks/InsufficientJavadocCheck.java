/*
 * Sada Assurances Java Custom Rules
 * Copyright (C) 2017 Sada assurances
 * mailto:rcapraro AT sada DOT fr
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
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
 * A sufficient javadoc should not be made of only one line containing the tag <pre>@author</pre>
 */
@Rule(
        key = "InsufficientJavadoc",
        name = "Insufficient Javadoc content",
        description = "For a class, interface or enum, the javadoc should not be empty and should not begin only with the @author tag.",
        priority = Priority.MAJOR,
        tags = {"convention"})
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
