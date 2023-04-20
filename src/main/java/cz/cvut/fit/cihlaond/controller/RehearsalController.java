package cz.cvut.fit.cihlaond.controller;

import cz.cvut.fit.cihlaond.DTO.RehearsalCreateDTO;
import cz.cvut.fit.cihlaond.DTO.RehearsalDTO;
import cz.cvut.fit.cihlaond.service.ETagService;
import cz.cvut.fit.cihlaond.service.exceptions.InvalidRehearsalTimeException;
import cz.cvut.fit.cihlaond.service.exceptions.NoSuchBandException;
import cz.cvut.fit.cihlaond.service.exceptions.NoSuchRehearsalException;
import cz.cvut.fit.cihlaond.service.RehearsalService;
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
@RequestMapping(value = "/rehearsals")
public class    RehearsalController {

    private final RehearsalService rehearsalService;

    @Autowired
    public RehearsalController(RehearsalService rehearsalService) {
        this.rehearsalService = rehearsalService;
    }

    @GetMapping("/{id}")
    public HttpEntity<RehearsalDTO> findById(@PathVariable int id) {
        Optional<RehearsalDTO> rehearsalDTO = rehearsalService.findByIdAsDTO(id);
        if (rehearsalDTO.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).eTag(ETagService.etagFromVersion(rehearsalDTO.get().getVersion())).body(rehearsalDTO.get());
    }

    @GetMapping
    public HttpEntity<Page<RehearsalDTO>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(rehearsalService.findAll(PageRequest.of(page, size)));
    }

    @GetMapping(params = "name")
    public HttpEntity<Page<RehearsalDTO>> findAllByRehearsalOfBandName(@RequestParam String name, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(rehearsalService.findAllByRehearsalOfBandName(name, PageRequest.of(page, size)));
    }

    @PostMapping
    public HttpEntity<RehearsalDTO> create(@RequestBody RehearsalCreateDTO rehearsal) {
        try {
            RehearsalDTO createdRehearsal = rehearsalService.create(rehearsal);
            return ResponseEntity.status(HttpStatus.OK).eTag(ETagService.etagFromVersion(createdRehearsal.getVersion())).body(createdRehearsal);
        } catch (NoSuchBandException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public HttpEntity<RehearsalDTO> update(@PathVariable int id, @RequestBody RehearsalCreateDTO rehearsal,
                               @RequestHeader(name = HttpHeaders.IF_MATCH) String ifMatch) {
        try {
            RehearsalDTO updatedRehearsal = rehearsalService.update(id, rehearsal, ifMatch);
            return ResponseEntity.status(HttpStatus.OK).eTag(ETagService.etagFromVersion(updatedRehearsal.getVersion())).body(updatedRehearsal);
        } catch (NoSuchRehearsalException | NoSuchBandException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (PreconditionFailedException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        } catch (UpdateConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        rehearsalService.deleteById(id);
    }

    @PostMapping("/make")
    public HttpEntity<RehearsalDTO> make(@RequestBody RehearsalCreateDTO rehearsal) {
        try {
            RehearsalDTO rehearsalDTO = rehearsalService.makeRehearsal(rehearsal);
            return ResponseEntity.status(HttpStatus.OK).eTag(ETagService.etagFromVersion(rehearsalDTO.getVersion())).body(rehearsalDTO);
        } catch (InvalidRehearsalTimeException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } catch (NoSuchBandException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
