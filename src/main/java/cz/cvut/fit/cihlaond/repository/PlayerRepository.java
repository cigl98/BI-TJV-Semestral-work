package cz.cvut.fit.cihlaond.repository;

import cz.cvut.fit.cihlaond.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {

}
