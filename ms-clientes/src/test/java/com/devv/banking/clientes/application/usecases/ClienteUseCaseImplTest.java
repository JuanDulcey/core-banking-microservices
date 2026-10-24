package com.devv.banking.clientes.application.usecases;

import com.devv.banking.clientes.domain.exception.DuplicateResourceException;
import com.devv.banking.clientes.domain.model.Cliente;
import com.devv.banking.clientes.domain.model.TipoEventoCliente;
import com.devv.banking.clientes.domain.ports.in.ClienteUseCase.CrearClienteCommand;
import com.devv.banking.clientes.domain.ports.out.ClienteEventPublisherPort;
import com.devv.banking.clientes.domain.ports.out.ClienteRepositoryPort;
import com.devv.banking.clientes.domain.ports.out.PasswordEncoderPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteUseCaseImplTest {

    @Mock
    private ClienteRepositoryPort clienteRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private ClienteEventPublisherPort eventPublisher;

    @InjectMocks
    private ClienteUseCaseImpl clienteUseCase;

    @Test
    @DisplayName("Debe encriptar contraseña y publicar evento CREATED al registrar un cliente nuevo")
    void debeEncriptarYPublicarEventoAlCrear() {
        CrearClienteCommand cmd = new CrearClienteCommand(
                "Jose Lema", "Masculino", 30, "171001", "Otavalo sn y principal", "098254785", "CLI-JOSE", "1234", true
        );
        when(clienteRepository.existsByClienteId("CLI-JOSE")).thenReturn(false);
        when(clienteRepository.existsByIdentificacion("171001")).thenReturn(false);
        when(passwordEncoder.encode("1234")).thenReturn("$2a$10$encrypted1234");
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        Cliente resultado = clienteUseCase.crear(cmd);

        assertThat(resultado.getContrasena()).isEqualTo("$2a$10$encrypted1234");
        verify(eventPublisher).publish(eq(resultado), eq(TipoEventoCliente.CREATED));
    }

    @Test
    @DisplayName("Debe impedir la creación cuando la identificación ya está registrada")
    void debeImpedirIdentificacionDuplicada() {
        CrearClienteCommand cmd = new CrearClienteCommand(
                "Jose Lema", "Masculino", 30, "171001", "Otavalo", "098254785", null, "1234", true
        );
        when(clienteRepository.existsByIdentificacion("171001")).thenReturn(true);

        assertThatThrownBy(() -> clienteUseCase.crear(cmd))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("171001");

        verify(clienteRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any(), any());
    }
}
