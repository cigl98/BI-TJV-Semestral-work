package cz.cvut.fit.cihlaond.controller;

import cz.cvut.fit.cihlaond.DTO.PlayerCreateDTO;
import cz.cvut.fit.cihlaond.DTO.PlayerDTO;
import cz.cvut.fit.cihlaond.service.NoSuchPlayerException;
import cz.cvut.fit.cihlaond.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/players")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/{id}")
    public PlayerDTO findById(@PathVariable int id) {
        return playerService.findByIdAsDTO(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Page<PlayerDTO> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return playerService.findAll(PageRequest.of(page, size));
    }

    @PostMapping
    public PlayerDTO create(@RequestBody PlayerCreateDTO player) {
        return playerService.create(player);
    }

    @PutMapping("/{id}")
    public PlayerDTO update(@PathVariable int id, @RequestBody PlayerCreateDTO player) {

        try {
            return playerService.update(id, player);
        } catch (NoSuchPlayerException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        playerService.deleteById(id);
    }

}
