package cz.cvut.fit.cihlaond.service;

import cz.cvut.fit.cihlaond.DTO.RehearsalCreateDTO;
import cz.cvut.fit.cihlaond.DTO.RehearsalDTO;
import cz.cvut.fit.cihlaond.entity.Band;
import cz.cvut.fit.cihlaond.entity.Rehearsal;
import cz.cvut.fit.cihlaond.repository.RehearsalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RehearsalService {
    private final RehearsalRepository rehearsalRepository;
    private final BandService bandService;

    @Autowired
    public RehearsalService(RehearsalRepository rehearsalRepository, BandService bandService) {
        this.rehearsalRepository = rehearsalRepository;
        this.bandService = bandService;
    }

    public Optional<Rehearsal> findById(int id) {
        return rehearsalRepository.findById(id);
    }

    public Optional<RehearsalDTO> findByIdAsDTO(int id) {
        return toDTO(findById(id));
    }

    public Page<RehearsalDTO> findAll(Pageable pageable) {
        return rehearsalRepository.findAll(pageable).map(this::toDTO);
    }

    public Page<RehearsalDTO> findAllByRehearsalOfBandName(String name, Pageable pageable) {
        return rehearsalRepository.findAllByRehearsalOfBandName(name, pageable).map(this::toDTO);
    }

    public void deleteById(int id) {
        rehearsalRepository.deleteById(id);
    }

    @Transactional
    public RehearsalDTO create(RehearsalCreateDTO rehearsalCreateDTO) throws NoSuchBandException {
        Optional<Band> optionalRehearsalOfBand = bandService.findById(rehearsalCreateDTO.getRehearsalOfBandId());

        if (optionalRehearsalOfBand.isEmpty())
            throw new NoSuchBandException("No such band.");

        Band rehearsalOfBand = optionalRehearsalOfBand.get();
        Rehearsal rehearsal = new Rehearsal(rehearsalCreateDTO.getStartTime(), rehearsalCreateDTO.getEndTime(), rehearsalOfBand);
        return toDTO(rehearsalRepository.save(rehearsal));
    }

    @Transactional
    public RehearsalDTO update(int id, RehearsalCreateDTO rehearsalCreateDTO) throws NoSuchRehearsalException, NoSuchBandException {
        Optional<Rehearsal> optionalRehearsal = findById(id);

        if (optionalRehearsal.isEmpty())
            throw new NoSuchRehearsalException("No such rehearsal.");

        Optional<Band> optionalRehearsalOfBand = bandService.findById(rehearsalCreateDTO.getRehearsalOfBandId());

        if (optionalRehearsalOfBand.isEmpty())
            throw new NoSuchBandException("No such band.");

        Band rehearsalOfBand = optionalRehearsalOfBand.get();
        Rehearsal rehearsal = optionalRehearsal.get();
        rehearsal.setStartTime(rehearsalCreateDTO.getStartTime());
        rehearsal.setEndTime(rehearsalCreateDTO.getEndTime());
        rehearsal.setRehearsalOfBand(rehearsalOfBand);

        return toDTO(rehearsal);
    }

    @Transactional
    public RehearsalDTO makeRehearsal(RehearsalCreateDTO rehearsalCreateDTO) throws InvalidRehearsalTimeException, NoSuchBandException {
        Page<RehearsalDTO> page = findAll(Pageable.unpaged());
        List<RehearsalDTO> rehearsals = page.getContent();

        for ( RehearsalDTO r : rehearsals ) {
            if (!(rehearsalCreateDTO.getStartTime().isBefore(r.getStartTime()) && rehearsalCreateDTO.getEndTime().isBefore(r.getStartTime())) &&
                    !(rehearsalCreateDTO.getStartTime().isAfter(r.getEndTime()) && rehearsalCreateDTO.getEndTime().isAfter(r.getEndTime())))
                throw new InvalidRehearsalTimeException("There cannot be two rehearsals at the same time.");
        }

        return create(rehearsalCreateDTO);
    }

    public RehearsalDTO toDTO(Rehearsal rehearsal) {
        return new RehearsalDTO(rehearsal.getId(), rehearsal.getStartTime(), rehearsal.getEndTime(), rehearsal.getRehearsalOfBand().getId());
    }

    public Optional<RehearsalDTO> toDTO(Optional<Rehearsal> rehearsal) {
        if (rehearsal.isEmpty())
            return Optional.empty();
        return Optional.of(toDTO(rehearsal.get()));
    }
}
