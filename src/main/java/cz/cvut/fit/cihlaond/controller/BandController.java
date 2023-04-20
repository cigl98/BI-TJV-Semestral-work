package cz.cvut.fit.cihlaond.controller;

import cz.cvut.fit.cihlaond.DTO.BandCreateDTO;
import cz.cvut.fit.cihlaond.DTO.BandDTO;
import cz.cvut.fit.cihlaond.service.BandService;
import cz.cvut.fit.cihlaond.service.ETagService;
import cz.cvut.fit.cihlaond.service.exceptions.NoSuchBandException;
import cz.cvut.fit.cihlaond.service.exceptions.NoSuchPlayerException;
import cz.cvut.fit.cihlaond.service.exceptions.PreconditionFailedException;
import cz.cvut.fit.cihlaond.service.exceptions.UpdateConflictException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@RestController
@RequestMapping(value = "/bands")
public class BandController {

    private final BandService bandService;

    @Autowired
    public BandController(BandService bandService) {
        this.bandService = bandService;
    }

    @GetMapping("/{id}")
    public HttpEntity<BandDTO> findById(@PathVariable int id) {
        Optional<BandDTO> bandDTO = bandService.findByIdAsDTO(id);
        if (bandDTO.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.status(HttpStatus.OK).eTag(ETagService.etagFromVersion(bandDTO.get().getVersion())).body(bandDTO.get());
    }

    @GetMapping
    public HttpEntity<Page<BandDTO>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(bandService.findAll(PageRequest.of(page, size)));
    }

    @GetMapping(params = "name")
    public HttpEntity<BandDTO> findByName(@RequestParam String name) {
        Optional<BandDTO> bandDTO = bandService.findByName(name);
        if (bandDTO.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.status(HttpStatus.OK).eTag(ETagService.etagFromVersion(bandDTO.get().getVersion())).body(bandDTO.get());
    }

    @PostMapping
    public HttpEntity<BandDTO> create(@RequestBody BandCreateDTO band) {
        try {
            BandDTO createdBand = bandService.create(band);
            return ResponseEntity.status(HttpStatus.OK).eTag(ETagService.etagFromVersion(createdBand.getVersion())).body(createdBand);
        } catch (NoSuchPlayerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public HttpEntity<BandDTO> update(@PathVariable int id, @RequestBody BandCreateDTO band,
                          @RequestHeader(name = HttpHeaders.IF_MATCH) String ifMatch) {
        try {
            BandDTO updatedBand = bandService.update(id ,band, ifMatch);
            return ResponseEntity.status(HttpStatus.OK).eTag(ETagService.etagFromVersion(updatedBand.getVersion())).body(bandService.update(id ,band, ifMatch));
        } catch (NoSuchBandException | NoSuchPlayerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (PreconditionFailedException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        } catch (UpdateConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        bandService.deleteById(id);
    }
}
