package cz.cvut.fit.cihlaond.service;

import cz.cvut.fit.cihlaond.DTO.PlayerCreateDTO;
import cz.cvut.fit.cihlaond.DTO.PlayerDTO;
import cz.cvut.fit.cihlaond.entity.Player;
import cz.cvut.fit.cihlaond.repository.PlayerRepository;
import cz.cvut.fit.cihlaond.service.exceptions.NoSuchPlayerException;
import cz.cvut.fit.cihlaond.service.exceptions.PreconditionFailedException;
import cz.cvut.fit.cihlaond.service.exceptions.UpdateConflictException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class PlayerServiceTest {

    @Autowired
    private PlayerService playerService;

    @MockBean
    private PlayerRepository playerRepositoryMock;

    @Test
    void findById() {
        Player playerMick = new Player("Mick", "Thomson", "Guitar");
        ReflectionTestUtils.setField(playerMick, "id", 0);
        BDDMockito.given(playerRepositoryMock.findById(0)).willReturn(Optional.of(playerMick));
        Optional<Player> optionalReturnedPlayer = playerService.findById(0);

        assertFalse(optionalReturnedPlayer.isEmpty());

        Player returnedPlayer = optionalReturnedPlayer.get();

        assertEquals(playerMick, returnedPlayer);

        Mockito.verify(playerRepositoryMock, Mockito.atLeastOnce()).findById(playerMick.getId());
    }

    @Test
    void findAll() {
        int page = 0;
        int size = 2;
        int total = 3;
        Pageable pageable = PageRequest.of(page, size);

        List<Player> data = List.of(
                new Player("Till", "Lindemann", "vocals"),
                new Player("Richard", "Kruspe", "guitar"));
        ReflectionTestUtils.setField(data.get(0), "id", 0);
        ReflectionTestUtils.setField(data.get(1), "id", 1);
        Page<Player> pageExpected = new PageImpl<>(data, pageable, total);

        BDDMockito.given(playerRepositoryMock.findAll(pageable)).willReturn(pageExpected);

        List<PlayerDTO> pageReturned = playerService.findAll(pageable).getContent();

        assertEquals(data.get(0).getFirstName(), pageReturned.get(0).getFirstName());
        assertEquals(data.get(0).getLastName(), pageReturned.get(0).getLastName());
        assertEquals(data.get(0).getId(), pageReturned.get(0).getId());
        assertEquals(data.get(0).getId(), pageReturned.get(0).getId());

        assertEquals(data.get(1).getFirstName(), pageReturned.get(1).getFirstName());
        assertEquals(data.get(1).getLastName(), pageReturned.get(1).getLastName());
        assertEquals(data.get(1).getId(), pageReturned.get(1).getId());
        assertEquals(data.get(1).getId(), pageReturned.get(1).getId());

        Mockito.verify(playerRepositoryMock, Mockito.atLeastOnce()).findAll(pageable);

        ArgumentCaptor<Pageable> argumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        Mockito.verify(playerRepositoryMock, Mockito.atLeastOnce()).findAll(argumentCaptor.capture());
        Pageable pageableSent = argumentCaptor.getValue();
        assertEquals(pageableSent, pageable);
    }

    @Test
    void deleteById() {
        playerService.deleteById(0);
        Mockito.verify(playerRepositoryMock, Mockito.atLeastOnce()).deleteById(0);
    }

    @Test
    void findByIds() {
        Player playerMick = new Player("Mick", "Thomson", "Guitar");
        ReflectionTestUtils.setField(playerMick, "id", 0);
        Player playerJoey = new Player("Joey", "Jordison", "Drums");
        ReflectionTestUtils.setField(playerJoey, "id", 1);
        List<Integer> ids = List.of(0, 1);

        BDDMockito.given(playerRepositoryMock.findAllById(ids)).willReturn(List.of(playerMick, playerJoey));
        List<Player> returnedList = playerService.findByIds(ids);

        assertFalse(returnedList.isEmpty());

        assertEquals(returnedList.size(), 2);
        assertTrue(returnedList.contains(playerMick));
        assertTrue(returnedList.contains(playerJoey));

        Mockito.verify(playerRepositoryMock, Mockito.atLeastOnce()).findAllById(ids);

    }

    @Test
    void create() {
        Player playerMick = new Player("Mick", "Thomson", "Guitar");
        ReflectionTestUtils.setField(playerMick, "id", 5);
        PlayerCreateDTO playerCreateDTO = new PlayerCreateDTO("Mick", "Thomson", "Guitar");

        BDDMockito.given(playerRepositoryMock.save(any(Player.class))).willReturn(playerMick);

        PlayerDTO returnedPlayerDTO = playerService.create(playerCreateDTO);

        PlayerDTO expectedPlayerDTO = new PlayerDTO(5, "Mick", "Thomson", "Guitar", 0L);
        assertEquals(expectedPlayerDTO, returnedPlayerDTO);

        ArgumentCaptor<Player> argumentCaptor = ArgumentCaptor.forClass(Player.class);
        Mockito.verify(playerRepositoryMock, Mockito.atLeastOnce()).save(argumentCaptor.capture());
        Player playerProvidedToSave = argumentCaptor.getValue();
        assertEquals("Mick", playerProvidedToSave.getFirstName());
        assertEquals("Thomson", playerProvidedToSave.getLastName());
        assertEquals("Guitar", playerProvidedToSave.getInstrument());
    }

    @Test
    void update() throws NoSuchPlayerException, UpdateConflictException, PreconditionFailedException {
        Player playerMick = new Player("Mick", "Thomson", "Guitar");
        ReflectionTestUtils.setField(playerMick, "id", 0);
        PlayerCreateDTO playerCreateDTO = new PlayerCreateDTO("Mick", "Thomson", "Electric guitar");
        BDDMockito.given(playerRepositoryMock.findById(0)).willReturn(Optional.of(playerMick));
        PlayerDTO expectedPlayerDTO = new PlayerDTO(0, "Mick", "Thomson", "Electric guitar", 0L);

        PlayerDTO returnedPlayerDTO = playerService.update(0, playerCreateDTO, "0");
        assertEquals(expectedPlayerDTO, returnedPlayerDTO);
        Mockito.verify(playerRepositoryMock, Mockito.atLeastOnce()).findById(0);

    }
}