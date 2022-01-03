package one.digitalinnovation.beerstock.service;

import one.digitalinnovation.beerstock.builder.BeerDTOBuilder;
import one.digitalinnovation.beerstock.dto.BeerDTO;
import one.digitalinnovation.beerstock.entity.Beer;
import one.digitalinnovation.beerstock.exception.BeerAlreadyRegisteredException;
import one.digitalinnovation.beerstock.exception.BeerNotFoundException;
import one.digitalinnovation.beerstock.exception.BeerStockExceededException;
import one.digitalinnovation.beerstock.mapper.BeerMapper;
import one.digitalinnovation.beerstock.repository.BeerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BeerServiceTest {

    private static final long INVALID_BEER_ID = 1L;

    @Mock
    private BeerRepository beerRepository;

    private BeerMapper beerMapper = BeerMapper.INSTANCE;

    @InjectMocks
    private BeerService beerService;

    @Test
    void quandoCervejaInformadaEntaoDeveSerCriada() throws BeerAlreadyRegisteredException {
        // given
        BeerDTO cervejaEsperadaDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer cervejaSalvaEsperada = beerMapper.toModel(cervejaEsperadaDTO);

        //when
        when(beerRepository.findByName(cervejaEsperadaDTO.getName())).thenReturn(Optional.empty());
        when(beerRepository.save(cervejaSalvaEsperada)).thenReturn(cervejaSalvaEsperada);

        //then
        BeerDTO cervejaCriadaDTO = beerService.createBeer(cervejaEsperadaDTO);

        assertThat(cervejaCriadaDTO.getId(), is(equalTo(cervejaCriadaDTO.getId())));
        assertThat(cervejaCriadaDTO.getName(), is(equalTo(cervejaCriadaDTO.getName())));
        assertThat(cervejaCriadaDTO.getQuantity(), is(equalTo(cervejaCriadaDTO.getQuantity())));
    }

    @Test
    void quandoCervejaInformadaJaEstaRegistradaEntaoExcecaoDeveSerLancada() throws BeerAlreadyRegisteredException {
        // given
        BeerDTO cervejaEsperadaDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer cervejaDuplicada = beerMapper.toModel(cervejaEsperadaDTO);

        //when
        when(beerRepository.findByName(cervejaEsperadaDTO.getName())).thenReturn(Optional.of(cervejaDuplicada));

        //then
        assertThrows(BeerAlreadyRegisteredException.class, () -> beerService.createBeer(cervejaEsperadaDTO));
    }

    @Test
    void quandoNomeCervejaValidoEntaoRetornarCerveja() throws BeerNotFoundException {
        //given
        BeerDTO cervejaEncontradaEsperadaDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer cervejaEncontradaEsperada = beerMapper.toModel(cervejaEncontradaEsperadaDTO);

        //when
        when(beerRepository.findByName(cervejaEncontradaEsperada.getName())).thenReturn(Optional.of(cervejaEncontradaEsperada));

        //then
        BeerDTO cervejaEncontradaDTO = beerService.findByName(cervejaEncontradaEsperadaDTO.getName());

        assertThat(cervejaEncontradaDTO, is(equalTo(cervejaEncontradaEsperadaDTO)));
    }

    @Test
    void quandoNomeCervejaInvalidaEntaoLancarExcecao() {
        //given
        BeerDTO cervejaEncontradaEsperadaDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        //when
        when(beerRepository.findByName(cervejaEncontradaEsperadaDTO.getName())).thenReturn(Optional.empty());

        //then
        assertThrows(BeerNotFoundException.class, () -> beerService.findByName(cervejaEncontradaEsperadaDTO.getName()));
    }

    @Test
    void quandoUmaListaEChamadaEntaoRetornarUmaListaDeCerveja() {
        //given
        BeerDTO cervejaEncontradaEsperadaDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer cervejaEncontradaEsperada = beerMapper.toModel(cervejaEncontradaEsperadaDTO);

        //when
        when(beerRepository.findAll()).thenReturn(Collections.singletonList(cervejaEncontradaEsperada));

        //then
        List<BeerDTO> encontradaListaCervejasDTO = beerService.listAll();

        assertThat(encontradaListaCervejasDTO, is(not(empty())));
        assertThat(encontradaListaCervejasDTO.get(0), is(equalTo(cervejaEncontradaEsperadaDTO)));
    }

    @Test
    void quandoUmaListaDeCervejaEChamadaEntaoRetornarUmaListaVazia() {
        //when
        when(beerRepository.findAll()).thenReturn(Collections.emptyList());

        //then
        List<BeerDTO> encontradaListaCervejasDTO = beerService.listAll();

        assertThat(encontradaListaCervejasDTO, is(empty()));
    }

    @Test
    void quandoUmaExclusaoEChamadaComIdValidoEntaoACervejaDeveSerDeletada() throws BeerNotFoundException {
        //given
        BeerDTO cervejaDeletadaEsperadaDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer cervejaDeletadaEsperada = beerMapper.toModel(cervejaDeletadaEsperadaDTO);

        //when
        when(beerRepository.findById(cervejaDeletadaEsperadaDTO.getId())).thenReturn(Optional.of(cervejaDeletadaEsperada));
        doNothing().when(beerRepository).deleteById(cervejaDeletadaEsperadaDTO.getId());

        //then
        beerService.deleteById(cervejaDeletadaEsperadaDTO.getId());

        verify(beerRepository, times(1)).findById(cervejaDeletadaEsperadaDTO.getId());
        verify(beerRepository, times(1)).deleteById(cervejaDeletadaEsperadaDTO.getId());
    }

    @Test
    void quandoIncrementoEChamadoEntaoIncrementarEstoqueDeCerveja() throws BeerNotFoundException, BeerStockExceededException {
        //given
        BeerDTO cervejaEncontradaEsperadaDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer cervejaEncontradaEsperada = beerMapper.toModel(cervejaEncontradaEsperadaDTO);

        //when
        when(beerRepository.findById(cervejaEncontradaEsperada.getId())).thenReturn(Optional.of(cervejaEncontradaEsperada));
        when(beerRepository.save(cervejaEncontradaEsperada)).thenReturn(cervejaEncontradaEsperada);

        int quantidadeParaIncrementar = 10;
        int quantidadeEsperadaDepoisDoIncremento = cervejaEncontradaEsperadaDTO.getQuantity() + quantidadeParaIncrementar;

        //then
        BeerDTO cervejaEncontradaDTO = beerService.increment(cervejaEncontradaEsperadaDTO.getId(), quantidadeParaIncrementar);

        assertThat(quantidadeEsperadaDepoisDoIncremento, equalTo(cervejaEncontradaDTO.getQuantity()));
        assertThat(quantidadeEsperadaDepoisDoIncremento, lessThan(cervejaEncontradaDTO.getMax()));
    }

    @Test
    void quandoIncrementoEMaiorQueOMaximoEntaoExcecaoELancada() throws BeerNotFoundException, BeerStockExceededException {
        //given
        BeerDTO cervejaEncontradaEsperadaDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer cervejaEncontradaEsperada = beerMapper.toModel(cervejaEncontradaEsperadaDTO);

        //when
        when(beerRepository.findById(cervejaEncontradaEsperada.getId())).thenReturn(Optional.of(cervejaEncontradaEsperada));

        int quantidadeParaIncrementar = 80;

        //then
        assertThrows(BeerStockExceededException.class, () -> beerService.increment(cervejaEncontradaEsperadaDTO.getId(), quantidadeParaIncrementar));
    }

    //
//    @Test
//    void whenDecrementIsCalledThenDecrementBeerStock() throws BeerNotFoundException, BeerStockExceededException {
//        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
//        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);
//
//        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
//        when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);
//
//        int quantityToDecrement = 5;
//        int expectedQuantityAfterDecrement = expectedBeerDTO.getQuantity() - quantityToDecrement;
//        BeerDTO incrementedBeerDTO = beerService.decrement(expectedBeerDTO.getId(), quantityToDecrement);
//
//        assertThat(expectedQuantityAfterDecrement, equalTo(incrementedBeerDTO.getQuantity()));
//        assertThat(expectedQuantityAfterDecrement, greaterThan(0));
//    }
//
//    @Test
//    void whenDecrementIsCalledToEmptyStockThenEmptyBeerStock() throws BeerNotFoundException, BeerStockExceededException {
//        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
//        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);
//
//        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
//        when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);
//
//        int quantityToDecrement = 10;
//        int expectedQuantityAfterDecrement = expectedBeerDTO.getQuantity() - quantityToDecrement;
//        BeerDTO incrementedBeerDTO = beerService.decrement(expectedBeerDTO.getId(), quantityToDecrement);
//
//        assertThat(expectedQuantityAfterDecrement, equalTo(0));
//        assertThat(expectedQuantityAfterDecrement, equalTo(incrementedBeerDTO.getQuantity()));
//    }
//
//    @Test
//    void whenDecrementIsLowerThanZeroThenThrowException() {
//        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
//        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);
//
//        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
//
//        int quantityToDecrement = 80;
//        assertThrows(BeerStockExceededException.class, () -> beerService.decrement(expectedBeerDTO.getId(), quantityToDecrement));
//    }
//
//    @Test
//    void whenDecrementIsCalledWithInvalidIdThenThrowException() {
//        int quantityToDecrement = 10;
//
//        when(beerRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());
//
//        assertThrows(BeerNotFoundException.class, () -> beerService.decrement(INVALID_BEER_ID, quantityToDecrement));
//    }
}
