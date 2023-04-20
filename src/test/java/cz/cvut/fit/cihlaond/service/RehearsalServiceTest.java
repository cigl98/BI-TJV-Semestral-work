package cz.cvut.fit.cihlaond.service;

import cz.cvut.fit.cihlaond.DTO.RehearsalCreateDTO;
import cz.cvut.fit.cihlaond.DTO.RehearsalDTO;
import cz.cvut.fit.cihlaond.entity.Band;
import cz.cvut.fit.cihlaond.entity.Player;
import cz.cvut.fit.cihlaond.entity.Rehearsal;
import cz.cvut.fit.cihlaond.repository.RehearsalRepository;
import cz.cvut.fit.cihlaond.service.exceptions.InvalidRehearsalTimeException;
import cz.cvut.fit.cihlaond.service.exceptions.NoSuchBandException;
import cz.cvut.fit.cihlaond.service.exceptions.NoSuchRehearsalException;
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
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class RehearsalServiceTest {

    @Autowired
    private RehearsalService rehearsalService;

    @MockBean
    private RehearsalRepository rehearsalRepositoryMock;

    @MockBean
    private BandService bandServiceMock;

    @Test
    void findById() {
        Player playerMick = new Player("Mick", "Thomson", "Guitar");
        ReflectionTestUtils.setField(playerMick, "id", 0);
        Player playerJoey = new Player("Joey", "Jordison", "Drums");
        ReflectionTestUtils.setField(playerJoey, "id", 1);
        List<Player> players = List.of(playerMick, playerJoey);
        Band bandSlipknot = new Band("Slipknot", players);
        ReflectionTestUtils.setField(bandSlipknot, "id", 5);
        Rehearsal rehearsalSlipknot = new Rehearsal(LocalDateTime.parse("2020-01-01T15:00:00"), LocalDateTime.parse("2020-01-01T18:00:00"), bandSlipknot);
        ReflectionTestUtils.setField(rehearsalSlipknot, "id", 3);
        BDDMockito.given(rehearsalRepositoryMock.findById(3)).willReturn(Optional.of(rehearsalSlipknot));

        Optional<Rehearsal> optionalReturnedRehearsal = rehearsalService.findById(3);
        assertFalse(optionalReturnedRehearsal.isEmpty());
        Rehearsal returnedRehearsal = optionalReturnedRehearsal.get();

        assertEquals(rehearsalSlipknot, returnedRehearsal);
        Mockito.verify(rehearsalRepositoryMock, Mockito.atLeastOnce()).findById(3);
    }

    @Test
    void findAllByRehearsalOfBandName() {
        Player playerMick = new Player("Mick", "Thomson", "Guitar");
        ReflectionTestUtils.setField(playerMick, "id", 0);
        Player playerJoey = new Player("Joey", "Jordison", "Drums");
        ReflectionTestUtils.setField(playerJoey, "id", 1);
        List<Player> players = List.of(playerMick, playerJoey);
        Band bandSlipknot = new Band("Slipknot", players);
        ReflectionTestUtils.setField(bandSlipknot, "id", 5);
        Rehearsal rehearsalSlipknot1 = new Rehearsal(LocalDateTime.parse("2020-01-01T15:00:00"), LocalDateTime.parse("2020-01-01T18:00:00"), bandSlipknot);
        ReflectionTestUtils.setField(rehearsalSlipknot1, "id", 3);
        Rehearsal rehearsalSlipknot2 = new Rehearsal(LocalDateTime.parse("2020-02-01T15:00:00"), LocalDateTime.parse("2020-02-01T18:00:00"), bandSlipknot);
        ReflectionTestUtils.setField(rehearsalSlipknot2, "id", 4);

        Page<Rehearsal> pageExpected = new PageImpl<>(List.of(rehearsalSlipknot1, rehearsalSlipknot2));

        BDDMockito.given(rehearsalRepositoryMock.findAllByRehearsalOfBandName("Slipknot", Pageable.unpaged())).willReturn(pageExpected);

        List<RehearsalDTO> returnedList = rehearsalService.findAllByRehearsalOfBandName("Slipknot", Pageable.unpaged()).getContent();
        List<RehearsalDTO> expectedList = List.of(new RehearsalDTO(3, LocalDateTime.parse("2020-01-01T15:00:00"), LocalDateTime.parse("2020-01-01T18:00:00"), 5, 0L),
                new RehearsalDTO(4, LocalDateTime.parse("2020-02-01T15:00:00"), LocalDateTime.parse("2020-02-01T18:00:00"), 5, 0L));

        assertFalse(returnedList.isEmpty());
        assertEquals(expectedList, returnedList);

        Mockito.verify(rehearsalRepositoryMock, Mockito.atLeastOnce()).findAllByRehearsalOfBandName("Slipknot", Pageable.unpaged());
    }

    @Test
    void create() throws NoSuchBandException {
        Player playerMick = new Player("Mick", "Thomson", "Guitar");
        ReflectionTestUtils.setField(playerMick, "id", 0);
        Player playerJoey = new Player("Joey", "Jordison", "Drums");
        ReflectionTestUtils.setField(playerJoey, "id", 1);
        List<Player> players = List.of(playerMick, playerJoey);
        Band bandSlipknot = new Band("Slipknot", players);
        ReflectionTestUtils.setField(bandSlipknot, "id", 5);
        Rehearsal rehearsalSlipknot1 = new Rehearsal(LocalDateTime.parse("2020-01-01T15:00:00"), LocalDateTime.parse("2020-01-01T18:00:00"), bandSlipknot);
        ReflectionTestUtils.setField(rehearsalSlipknot1, "id", 3);

        RehearsalCreateDTO rehearsalCreateDTO = new RehearsalCreateDTO(LocalDateTime.parse("2020-01-01T15:00:00"), LocalDateTime.parse("2020-01-01T18:00:00"), 5);

        BDDMockito.given(rehearsalRepositoryMock.save(any(Rehearsal.class))).willReturn(rehearsalSlipknot1);
        BDDMockito.given(bandServiceMock.findById(5)).willReturn(Optional.of(bandSlipknot));

        RehearsalDTO returnedRehearsalDTO = rehearsalService.create(rehearsalCreateDTO);
        RehearsalDTO expectedRehearsalDTO = new RehearsalDTO(3, LocalDateTime.parse("2020-01-01T15:00:00"), LocalDateTime.parse("2020-01-01T18:00:00"), 5, 0L);
        assertEquals(returnedRehearsalDTO, expectedRehearsalDTO);

        ArgumentCaptor<Rehearsal> argumentCaptor = ArgumentCaptor.forClass(Rehearsal.class);
        Mockito.verify(rehearsalRepositoryMock, Mockito.atLeastOnce()).save(argumentCaptor.capture());
        Rehearsal rehearsalProvidedToSave = argumentCaptor.getValue();
        assertEquals(rehearsalProvidedToSave.getStartTime(), LocalDateTime.parse("2020-01-01T15:00:00"));
        assertEquals(rehearsalProvidedToSave.getEndTime(), LocalDateTime.parse("2020-01-01T18:00:00"));
        assertEquals(rehearsalProvidedToSave.getRehearsalOfBand(), bandSlipknot);
    }

    @Test
    void update() throws NoSuchRehearsalException, NoSuchBandException, UpdateConflictException, PreconditionFailedException {
        Player playerMick = new Player("Mick", "Thomson", "Guitar");
        ReflectionTestUtils.setField(playerMick, "id", 0);
        Player playerJoey = new Player("Joey", "Jordison", "Drums");
        ReflectionTestUtils.setField(playerJoey, "id", 1);
        List<Player> players = List.of(playerMick, playerJoey);
        Band bandSlipknot = new Band("Slipknot", players);
        ReflectionTestUtils.setField(bandSlipknot, "id", 5);
        Rehearsal rehearsalSlipknot1 = new Rehearsal(LocalDateTime.parse("2020-01-01T15:00:00"), LocalDateTime.parse("2020-01-01T18:00:00"), bandSlipknot);
        ReflectionTestUtils.setField(rehearsalSlipknot1, "id", 3);

        RehearsalCreateDTO rehearsalCreateDTO = new RehearsalCreateDTO(LocalDateTime.parse("2020-01-01T20:00:00"), LocalDateTime.parse("2020-01-01T23:00:00"), 5);

        BDDMockito.given(rehearsalRepositoryMock.findById(3)).willReturn(Optional.of(rehearsalSlipknot1));
        BDDMockito.given(bandServiceMock.findById(5)).willReturn(Optional.of(bandSlipknot));

        RehearsalDTO expectedRehearsalDTO = new RehearsalDTO(3, LocalDateTime.parse("2020-01-01T20:00:00"), LocalDateTime.parse("2020-01-01T23:00:00"), 5, 0L);

        RehearsalDTO returnedRehearsalDTO = rehearsalService.update(3, rehearsalCreateDTO, "0");

        assertEquals(expectedRehearsalDTO, returnedRehearsalDTO);
        Mockito.verify(rehearsalRepositoryMock, Mockito.atLeastOnce()).findById(3);
    }

    @Test
     void makeRehearsal() {
        Player playerMick = new Player("Mick", "Thomson", "Guitar");
        ReflectionTestUtils.setField(playerMick, "id", 0);
        Player playerJoey = new Player("Joey", "Jordison", "Drums");
        ReflectionTestUtils.setField(playerJoey, "id", 1);
        List<Player> players = List.of(playerMick, playerJoey);
        Band bandSlipknot = new Band("Slipknot", players);
        ReflectionTestUtils.setField(bandSlipknot, "id", 5);
        Rehearsal rehearsalSlipknot1 = new Rehearsal(LocalDateTime.parse("2020-01-01T15:00:00"), LocalDateTime.parse("2020-01-01T18:00:00"), bandSlipknot);
        ReflectionTestUtils.setField(rehearsalSlipknot1, "id", 3);
        Rehearsal rehearsalSlipknot2 = new Rehearsal(LocalDateTime.parse("2020-02-01T15:00:00"), LocalDateTime.parse("2020-02-01T18:00:00"), bandSlipknot);
        ReflectionTestUtils.setField(rehearsalSlipknot2, "id", 4);

        Page<Rehearsal> pageExpected = new PageImpl<>(List.of(rehearsalSlipknot1, rehearsalSlipknot2));

        BDDMockito.given(rehearsalRepositoryMock.findAll(Pageable.unpaged())).willReturn(pageExpected);
        BDDMockito.given(bandServiceMock.findById(5)).willReturn(Optional.of(bandSlipknot));

        RehearsalCreateDTO rehearsalCreateDTO = new RehearsalCreateDTO(LocalDateTime.parse("2020-01-01T17:00:00"), LocalDateTime.parse("2020-01-01T23:00:00"), 5);

        InvalidRehearsalTimeException e = assertThrows(InvalidRehearsalTimeException.class, () -> {
            rehearsalService.makeRehearsal(rehearsalCreateDTO); });

        String expectedMessage = "There cannot be two rehearsals at the same time.";
        String actualMessage = e.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void findAll() {
        Player playerMick = new Player("Mick", "Thomson", "Guitar");
        ReflectionTestUtils.setField(playerMick, "id", 0);
        Player playerJoey = new Player("Joey", "Jordison", "Drums");
        ReflectionTestUtils.setField(playerJoey, "id", 1);
        List<Player> players = List.of(playerMick, playerJoey);
        Band bandSlipknot = new Band("Slipknot", players);
        ReflectionTestUtils.setField(bandSlipknot, "id", 5);
        Rehearsal rehearsalSlipknot1 = new Rehearsal(LocalDateTime.parse("2020-01-01T15:00:00"), LocalDateTime.parse("2020-01-01T18:00:00"), bandSlipknot);
        ReflectionTestUtils.setField(rehearsalSlipknot1, "id", 3);
        Rehearsal rehearsalSlipknot2 = new Rehearsal(LocalDateTime.parse("2020-02-01T15:00:00"), LocalDateTime.parse("2020-02-01T18:00:00"), bandSlipknot);
        ReflectionTestUtils.setField(rehearsalSlipknot2, "id", 4);

        Page<Rehearsal> pageExpected = new PageImpl<>(List.of(rehearsalSlipknot1, rehearsalSlipknot2));

        BDDMockito.given(rehearsalRepositoryMock.findAll(Pageable.unpaged())).willReturn(pageExpected);

        List<RehearsalDTO> returnedList = rehearsalService.findAll(Pageable.unpaged()).getContent();
        List<RehearsalDTO> expectedList = List.of(new RehearsalDTO(3, LocalDateTime.parse("2020-01-01T15:00:00"), LocalDateTime.parse("2020-01-01T18:00:00"), 5, 0L),
                new RehearsalDTO(4, LocalDateTime.parse("2020-02-01T15:00:00"), LocalDateTime.parse("2020-02-01T18:00:00"), 5, 0L));

        assertFalse(returnedList.isEmpty());
        assertEquals(expectedList, returnedList);

        Mockito.verify(rehearsalRepositoryMock, Mockito.atLeastOnce()).findAll(Pageable.unpaged());
    }

    @Test
    void deleteById() {
        rehearsalService.deleteById(0);
        Mockito.verify(rehearsalRepositoryMock, Mockito.atLeastOnce()).deleteById(0);
    }
}