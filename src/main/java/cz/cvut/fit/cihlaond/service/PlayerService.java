package cz.cvut.fit.cihlaond.service;

import cz.cvut.fit.cihlaond.DTO.PlayerCreateDTO;
import cz.cvut.fit.cihlaond.DTO.PlayerDTO;
import cz.cvut.fit.cihlaond.entity.Player;
import cz.cvut.fit.cihlaond.repository.PlayerRepository;
import cz.cvut.fit.cihlaond.service.exceptions.NoSuchPlayerException;
import cz.cvut.fit.cihlaond.service.exceptions.PreconditionFailedException;
import cz.cvut.fit.cihlaond.service.exceptions.UpdateConflictException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Optional<Player> findById(int id) {
        return playerRepository.findById(id);
    }

    public Optional<PlayerDTO> findByIdAsDTO(int id) {
        return toDTO(findById(id));
    }

    public List<Player> findByIds(List<Integer> ids) {
        return playerRepository.findAllById(ids);
    }

    public Page<PlayerDTO> findAll(Pageable pageable) {
        return playerRepository.findAll(pageable).map(this::toDTO);
    }

    public void deleteById( int id) {
        playerRepository.deleteById(id);
    }

    @Transactional
    public PlayerDTO create(PlayerCreateDTO playerCreateDTO) {
        return toDTO(playerRepository.save(
                new Player(
                        playerCreateDTO.getFirstName(),
                        playerCreateDTO.getLastName(),
                        playerCreateDTO.getInstrument()
                )
        ));
    }

    public PlayerDTO update(int id, PlayerCreateDTO playerCreateDTO, String ifMatch) throws NoSuchPlayerException, UpdateConflictException, PreconditionFailedException {
        Optional<Player> optionalPlayer = findById(id);

        if (optionalPlayer.isEmpty())
            throw new NoSuchPlayerException("No such player.");

        Player player = optionalPlayer.get();

        if (!ifMatch.equals("\"" + player.getVersion() + "\""))
            throw new PreconditionFailedException();

        try {
            player.setFirstName(playerCreateDTO.getFirstName());
            player.setLastName(playerCreateDTO.getLastName());
            player.setInstrument(playerCreateDTO.getInstrument());
            player = playerRepository.save(player);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new UpdateConflictException();
        }

        return toDTO(player);
    }

    public PlayerDTO toDTO(Player player) {
        return new PlayerDTO(
                player.getId(),
                player.getFirstName(),
                player.getLastName(),
                player.getInstrument(),
                player.getVersion()
        );
    }

    public Optional<PlayerDTO> toDTO(Optional<Player> player) {
        if (player.isEmpty())
            return Optional.empty();
        return Optional.of(toDTO(player.get()));
    }
}
