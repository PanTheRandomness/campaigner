package com.example.application.data.repositories;

import com.example.application.data.Area;
import com.example.application.data.World;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AreaRepository extends JpaRepository<Area, Long> {

    List<Area> findByWorld(World world);
}
