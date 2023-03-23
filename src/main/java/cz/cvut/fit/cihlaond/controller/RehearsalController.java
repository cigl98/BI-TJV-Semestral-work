package cz.cvut.fit.cihlaond.controller;

import cz.cvut.fit.cihlaond.DTO.RehearsalCreateDTO;
import cz.cvut.fit.cihlaond.DTO.RehearsalDTO;
import cz.cvut.fit.cihlaond.service.InvalidRehearsalTimeException;
import cz.cvut.fit.cihlaond.service.NoSuchBandException;
import cz.cvut.fit.cihlaond.service.NoSuchRehearsalException;
import cz.cvut.fit.cihlaond.service.RehearsalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/rehearsals")
public class RehearsalController {

    private final RehearsalService rehearsalService;

    @Autowired
    public RehearsalController(RehearsalService rehearsalService) {
        this.rehearsalService = rehearsalService;
    }

    @GetMapping("/{id}")
    public RehearsalDTO findById(@PathVariable int id) {
        return rehearsalService.findByIdAsDTO(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Page<RehearsalDTO> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return rehearsalService.findAll(PageRequest.of(page, size));
    }

    @GetMapping(params = "name")
    public Page<RehearsalDTO> findAllByRehearsalOfBandName(@RequestParam String name, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return rehearsalService.findAllByRehearsalOfBandName(name, PageRequest.of(page, size));
    }

    @PostMapping
    public RehearsalDTO create(@RequestBody RehearsalCreateDTO rehearsal) {
        try {
            return rehearsalService.create(rehearsal);
        } catch (NoSuchBandException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public RehearsalDTO update(@PathVariable int id, @RequestBody RehearsalCreateDTO rehearsal) {
        try {
            return rehearsalService.update(id, rehearsal);
        } catch (NoSuchRehearsalException | NoSuchBandException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        rehearsalService.deleteById(id);
    }

    @PostMapping("/make")
    public RehearsalDTO make(@RequestBody RehearsalCreateDTO rehearsal) {
        try {
            return rehearsalService.makeRehearsal(rehearsal);
        } catch (InvalidRehearsalTimeException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } catch (NoSuchBandException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
