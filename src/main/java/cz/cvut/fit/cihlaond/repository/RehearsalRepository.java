package cz.cvut.fit.cihlaond.repository;

import cz.cvut.fit.cihlaond.entity.Rehearsal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RehearsalRepository extends JpaRepository<Rehearsal, Integer> {

    Page<Rehearsal> findAllByRehearsalOfBandName(String name, Pageable pageable);
}
