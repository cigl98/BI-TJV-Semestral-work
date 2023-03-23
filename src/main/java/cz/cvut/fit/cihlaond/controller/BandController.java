package cz.cvut.fit.cihlaond.controller;

import cz.cvut.fit.cihlaond.DTO.BandCreateDTO;
import cz.cvut.fit.cihlaond.DTO.BandDTO;
import cz.cvut.fit.cihlaond.service.BandService;
import cz.cvut.fit.cihlaond.service.NoSuchBandException;
import cz.cvut.fit.cihlaond.service.NoSuchPlayerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/bands")
public class BandController {

    private final BandService bandService;

    @Autowired
    public BandController(BandService bandService) {
        this.bandService = bandService;
    }

    @GetMapping("/{id}")
    public BandDTO findById(@PathVariable int id) {
        return bandService.findByIdAsDTO(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Page<BandDTO> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return bandService.findAll(PageRequest.of(page, size));
    }

    @GetMapping(params = "name")
    public BandDTO findByName(@RequestParam String name) {
        return bandService.findByName(name).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public BandDTO create(@RequestBody BandCreateDTO band) {
        try {
            return bandService.create(band);
        } catch (NoSuchPlayerException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public BandDTO update(@PathVariable int id, @RequestBody BandCreateDTO band) {
        try {
            return bandService.update(id ,band);
        } catch (NoSuchBandException | NoSuchPlayerException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        bandService.deleteById(id);
    }
}
