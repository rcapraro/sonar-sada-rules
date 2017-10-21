package com.sada.checks.java;

import com.sada.java.checks.InsufficientJavadocCheck;
import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class InsufficientJavadocCheckTest {

    @Test
    public void testEmptyJavadoc() {
        JavaCheckVerifier.verify("src/test/files/EmptyJavadocCheck.java", new InsufficientJavadocCheck());
    }

    @Test
    public void testJavadocWithOnlyAuthor() {
        JavaCheckVerifier.verify("src/test/files/JavadocWithOnlyAuthorCheck.java", new InsufficientJavadocCheck());
    }
}
