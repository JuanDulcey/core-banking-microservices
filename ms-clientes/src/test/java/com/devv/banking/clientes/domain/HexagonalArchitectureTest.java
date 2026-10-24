package com.devv.banking.clientes.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Guardián arquitectónico: verifica que la capa 'domain' permanezca 100% libre de frameworks externos.
 */
class HexagonalArchitectureTest {

    private static final List<String> FORBIDDEN_IMPORTS = List.of(
            "import org.springframework.",
            "import jakarta.persistence.",
            "import org.hibernate.",
            "import com.fasterxml.jackson.",
            "import org.mapstruct."
    );

    @Test
    @DisplayName("La capa domain de ms-clientes no debe importar Spring, JPA, Hibernate ni Jackson")
    void domainDebeSerPuroSinAcoplamientoAFrameworks() throws IOException {
        Path domainDir = Path.of("src/main/java/com/devv/banking/clientes/domain");
        assertThat(Files.exists(domainDir)).isTrue();

        try (Stream<Path> files = Files.walk(domainDir)) {
            List<Path> javaFiles = files.filter(p -> p.toString().endsWith(".java")).toList();
            assertThat(javaFiles).isNotEmpty();

            for (Path javaFile : javaFiles) {
                String source = Files.readString(javaFile);
                for (String forbidden : FORBIDDEN_IMPORTS) {
                    assertThat(source)
                            .withFailMessage("Violación Hexagonal en %s: contiene '%s'", javaFile, forbidden)
                            .doesNotContain(forbidden);
                }
            }
        }
    }
}
