package cz.cvut.fit.cihlaond.service;

import cz.cvut.fit.cihlaond.DTO.BandCreateDTO;
import cz.cvut.fit.cihlaond.DTO.BandDTO;
import cz.cvut.fit.cihlaond.entity.Band;
import cz.cvut.fit.cihlaond.entity.Player;
import cz.cvut.fit.cihlaond.repository.BandRepository;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class BandServiceTest {

    @Autowired
    private BandService bandService;

    @MockBean
    private BandRepository bandRepositoryMock;

    @MockBean
    private PlayerService playerServiceMock;

    @Test
    void findById() {
        Player playerMick = new Player("Mick", "Thomson", "Guitar");
        ReflectionTestUtils.setField(playerMick, "id", 0);
        Player playerJoey = new Player("Joey", "Jordison", "Drums");
        ReflectionTestUtils.setField(playerJoey, "id", 1);
        List<Player> players = List.of(playerMick, playerJoey);
        Band bandSlipknot = new Band("Slipknot", players);
        ReflectionTestUtils.setField(bandSlipknot, "id", 5);

        BDDMockito.given(bandRepositoryMock.findById(5)).willReturn(Optional.of(bandSlipknot));
        Optional<Band> optionalReturnedBand = bandService.findById(5);
        assertFalse(optionalReturnedBand.isEmpty());

        Band returnedBand = optionalReturnedBand.get();
        assertEquals(returnedBand, bandSlipknot);
        Mockito.verify(bandRepositoryMock, Mockito.atLeastOnce()).findById(bandSlipknot.getId());

    }

    @Test
    void findByName() {
        Player playerMick = new Player("Mick", "Thomson", "Guitar");
        ReflectionTestUtils.setField(playerMick, "id", 0);
        Player playerJoey = new Player("Joey", "Jordison", "Drums");
        ReflectionTestUtils.setField(playerJoey, "id", 1);
        List<Player> players = List.of(playerMick, playerJoey);
        Band bandSlipknot = new Band("Slipknot", players);
        ReflectionTestUtils.setField(bandSlipknot, "id", 5);

        BDDMockito.given(bandRepositoryMock.findByName("Slipknot")).willReturn(Optional.of(bandSlipknot));
        Optional<BandDTO> optionalReturnedBandDTO = bandService.findByName("Slipknot");
        assertFalse(optionalReturnedBandDTO.isEmpty());

        BandDTO expectedBandDTO = new BandDTO(5, "Slipknot", List.of(0, 1));
        BandDTO returnedBandDTO = optionalReturnedBandDTO.get();
        assertEquals(returnedBandDTO, expectedBandDTO);
        Mockito.verify(bandRepositoryMock, Mockito.atLeastOnce()).findByName("Slipknot");

    }

    @Test
    void create() throws NoSuchPlayerException {
        Player playerMick = new Player("Mick", "Thomson", "Guitar");
        ReflectionTestUtils.setField(playerMick, "id", 0);
        Player playerJoey = new Player("Joey", "Jordison", "Drums");
        ReflectionTestUtils.setField(playerJoey, "id", 1);
        List<Player> players = List.of(playerMick, playerJoey);
        Band bandSlipknot = new Band("Slipknot", players);
        ReflectionTestUtils.setField(bandSlipknot, "id", 5);

        BandCreateDTO bandCreateDTO = new BandCreateDTO("Slipknot", List.of(0, 1));
        BDDMockito.given(bandRepositoryMock.save(any(Band.class))).willReturn(bandSlipknot);
        BDDMockito.given(playerServiceMock.findByIds(List.of(0, 1))).willReturn(List.of(playerMick, playerJoey));

        BandDTO returnedBandDTO = bandService.create(bandCreateDTO);
        BandDTO expectedBandDTO = new BandDTO(5, "Slipknot", List.of(0, 1));
        assertEquals(expectedBandDTO, returnedBandDTO);

        ArgumentCaptor<Band> argumentCaptor = ArgumentCaptor.forClass(Band.class);
        Mockito.verify(bandRepositoryMock, Mockito.atLeastOnce()).save(argumentCaptor.capture());
        Band bandProvidedToSave = argumentCaptor.getValue();
        assertEquals(bandProvidedToSave.getName(), "Slipknot");
        assertEquals(bandProvidedToSave.getPlayers(), List.of(playerMick, playerJoey));


    }

    @Test
    void update() throws NoSuchBandException, NoSuchPlayerException {
        Player playerMick = new Player("Mick", "Thomson", "Guitar");
        ReflectionTestUtils.setField(playerMick, "id", 0);
        Player playerJoey = new Player("Joey", "Jordison", "Drums");
        ReflectionTestUtils.setField(playerJoey, "id", 1);
        List<Player> players = List.of(playerMick, playerJoey);
        Band bandSlipknot = new Band("Slipknot", players);
        ReflectionTestUtils.setField(bandSlipknot, "id", 5);

        BandCreateDTO bandCreateDTO = new BandCreateDTO("Stone Sour", List.of(0, 1));
        BDDMockito.given(bandRepositoryMock.findById(5)).willReturn(Optional.of(bandSlipknot));
        BDDMockito.given(playerServiceMock.findByIds(List.of(0, 1))).willReturn(List.of(playerMick, playerJoey));

        BandDTO expectedBandDTO = new BandDTO(5, "Stone Sour", List.of(0, 1));

        BandDTO returnedBandDTO = bandService.update(5, bandCreateDTO);
        assertEquals(expectedBandDTO, returnedBandDTO);

        Mockito.verify(bandRepositoryMock, Mockito.atLeastOnce()).findById(bandSlipknot.getId());
    }

    @Test
    void findAll() {
        Player playerMick = new Player("Mick", "Thomson", "Guitar");
        ReflectionTestUtils.setField(playerMick, "id", 0);
        Player playerJoey = new Player("Joey", "Jordison", "Drums");
        ReflectionTestUtils.setField(playerJoey, "id", 1);
        List<Player> playersSlipknot = List.of(playerMick, playerJoey);
        Band bandSlipknot = new Band("Slipknot", playersSlipknot);
        ReflectionTestUtils.setField(bandSlipknot, "id", 5);

        Player playerTill = new Player("Mick", "Thomson", "Guitar");
        ReflectionTestUtils.setField(playerMick, "id", 0);
        Player playerRichard = new Player("Joey", "Jordison", "Drums");
        ReflectionTestUtils.setField(playerJoey, "id", 1);
        List<Player> playersRammstein = List.of(playerMick, playerJoey);
        Band bandRammstein = new Band("Slipknot", playersRammstein);
        ReflectionTestUtils.setField(bandSlipknot, "id", 5);

        List<Band> data = List.of(bandSlipknot, bandRammstein);

        Page<Band> pageExpected = new PageImpl<>(data);

        BDDMockito.given(bandRepositoryMock.findAll(Pageable.unpaged())).willReturn(pageExpected);

        List<Band> returnedData = bandRepositoryMock.findAll(Pageable.unpaged()).getContent();

        for ( int i = 0; i < data.size(); i++ ) {
            assertEquals(data.get(i).getId(), returnedData.get(i).getId());
            assertEquals(data.get(i).getName(), returnedData.get(i).getName());
        }

        Mockito.verify(bandRepositoryMock, Mockito.atLeastOnce()).findAll(Pageable.unpaged());
    }

    @Test
    void deleteById() {
        bandService.deleteById(0);
        Mockito.verify(bandRepositoryMock, Mockito.atLeastOnce()).deleteById(0);
    }
}