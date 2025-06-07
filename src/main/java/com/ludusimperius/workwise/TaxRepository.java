package com.ludusimperius.workwise;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TaxRepository extends JpaRepository<Tax, Long> {
    Optional<Tax> findByName(String name);
    boolean existsByName(String name);
} 