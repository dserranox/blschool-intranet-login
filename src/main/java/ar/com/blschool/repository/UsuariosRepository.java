package ar.com.blschool.repository;

import ar.com.blschool.entity.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuariosRepository extends JpaRepository<Usuarios, Long> {
    Optional<Usuarios> findByUsernameIgnoreCase(String username);
}
