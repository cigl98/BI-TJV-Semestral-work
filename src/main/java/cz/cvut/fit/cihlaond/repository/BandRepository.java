package cz.cvut.fit.cihlaond.repository;

import cz.cvut.fit.cihlaond.entity.Band;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BandRepository extends JpaRepository<Band, Integer> {

    Optional<Band> findByName(String name);
}
