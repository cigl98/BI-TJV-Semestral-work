package cz.cvut.fit.cihlaond.service;

import cz.cvut.fit.cihlaond.DTO.BandCreateDTO;
import cz.cvut.fit.cihlaond.DTO.BandDTO;
import cz.cvut.fit.cihlaond.entity.Band;
import cz.cvut.fit.cihlaond.entity.Player;
import cz.cvut.fit.cihlaond.repository.BandRepository;
import cz.cvut.fit.cihlaond.service.exceptions.NoSuchBandException;
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
import java.util.stream.Collectors;

@Service
public class BandService {
    private final BandRepository bandRepository;
    private final PlayerService playerService;

    @Autowired
    public BandService(BandRepository bandRepository, PlayerService playerService) {
        this.bandRepository = bandRepository;
        this.playerService = playerService;
    }

    public Optional<Band> findById(int id) {
        return bandRepository.findById(id);
    }

    public Optional<BandDTO> findByIdAsDTO(int id) {
        return toDTO(findById(id));
    }

    public Page<BandDTO> findAll(Pageable pageable) {
        return bandRepository.findAll(pageable).map(this::toDTO);
    }

    public Optional<BandDTO> findByName(String name) {
        return toDTO(bandRepository.findByName(name));
    }

    public void deleteById(int id) {
        bandRepository.deleteById(id);
    }

    @Transactional
    public BandDTO create(BandCreateDTO bandCreateDTO) throws NoSuchPlayerException {
        List<Player> players = playerService.findByIds(bandCreateDTO.getPlayerIds());

        if (players.size() != bandCreateDTO.getPlayerIds().size())
            throw new NoSuchPlayerException("Some players not found.");

        Band band = new Band(bandCreateDTO.getName(), players);

        return toDTO(bandRepository.save(band));
    }

    public BandDTO update(int id, BandCreateDTO bandCreateDTO, String ifMatch) throws NoSuchBandException, NoSuchPlayerException, PreconditionFailedException, UpdateConflictException {
        Optional<Band> optionalBand = findById(id);

        if (optionalBand.isEmpty())
            throw new NoSuchBandException("No such band.");

        List<Player> players = playerService.findByIds(bandCreateDTO.getPlayerIds());

        if (players.size() != bandCreateDTO.getPlayerIds().size())
            throw new NoSuchPlayerException("Some players not found.");

        Band band = optionalBand.get();

        if (!ifMatch.equals("\"" + band.getVersion() + "\""))
            throw new PreconditionFailedException();

        try {
            band.setName(bandCreateDTO.getName());
            band.setPlayers(players);
            band = bandRepository.save(band);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new UpdateConflictException();
        }

        return toDTO(band);
    }

    public BandDTO toDTO(Band band) {
        return new BandDTO(
                band.getId(),
                band.getName(),
                band.getPlayers().stream()
                        .map(Player::getId)
                        .collect(Collectors.toList()),
                band.getVersion());
    }

    public Optional<BandDTO> toDTO(Optional<Band> band) {
        if (band.isEmpty())
            return Optional.empty();
        return Optional.of(toDTO(band.get()));
    }
}
