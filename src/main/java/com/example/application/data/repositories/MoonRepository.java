package com.example.application.data.repositories;

import com.example.application.data.Moon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoonRepository extends JpaRepository<Moon, Long> {
}
