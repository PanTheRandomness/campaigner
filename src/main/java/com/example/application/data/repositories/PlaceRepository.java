package com.example.application.data.repositories;

import com.example.application.data.Place;
import com.example.application.data.World;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Query("SELECT p FROM Place p WHERE p.area.world IN :worlds")
    List<Place> findPlacesByWorlds(@Param("worlds") List<World> worlds);
}
