package cz.cvut.fit.cihlaond.controller;

import cz.cvut.fit.cihlaond.DTO.PlayerCreateDTO;
import cz.cvut.fit.cihlaond.DTO.PlayerDTO;
import cz.cvut.fit.cihlaond.service.ETagService;
import cz.cvut.fit.cihlaond.service.PlayerService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RestController
@RequestMapping(value = "/players")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/{id}")
    public HttpEntity<PlayerDTO> findById(@PathVariable int id) {
        Optional<PlayerDTO> playerDTO = playerService.findByIdAsDTO(id);
        if (playerDTO.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.status(HttpStatus.OK).eTag(ETagService.etagFromVersion(playerDTO.get().getVersion())).body(playerDTO.get());
    }

    @GetMapping
    public HttpEntity<Page<PlayerDTO>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(playerService.findAll(PageRequest.of(page, size)));
    }

    @PostMapping
    public HttpEntity<PlayerDTO> create(@RequestBody PlayerCreateDTO player) {
        PlayerDTO createdPlayer = playerService.create(player);
        return ResponseEntity.status(HttpStatus.OK).eTag(ETagService.etagFromVersion(createdPlayer.getVersion())).body(createdPlayer);
    }

    @PutMapping("/{id}")
    public HttpEntity<PlayerDTO> update(@PathVariable int id, @RequestBody PlayerCreateDTO player,
                                        @RequestHeader(name = HttpHeaders.IF_MATCH) String ifMatch) {

        try {
            PlayerDTO updatedPlayer = playerService.update(id, player, ifMatch);
            return ResponseEntity.status(HttpStatus.OK).eTag(ETagService.etagFromVersion(updatedPlayer.getVersion())).body(updatedPlayer);
        } catch (NoSuchPlayerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (PreconditionFailedException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        } catch (UpdateConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        playerService.deleteById(id);
    }
}
