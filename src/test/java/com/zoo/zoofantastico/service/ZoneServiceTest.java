package com.zoo.zoofantastico.service;

import com.zoo.zoofantastico.model.Zone;
import com.zoo.zoofantastico.repository.ZoneRepository;
import com.zoo.zoofantastico.repository.CreatureRepository;
import com.zoo.zoofantastico.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ZoneServiceTest {

@Mock private ZoneRepository zoneRepository;
@Mock private CreatureRepository creatureRepository;
@InjectMocks private ZoneService service;

@BeforeEach
void setup() { MockitoAnnotations.openMocks(this); }

@Test
void delete_throws_whenZoneHasCreatures() {
Zone z = new Zone(); z.setId(1L); z.setName("Bosque");
when(zoneRepository.findById(1L)).thenReturn(Optional.of(z));
when(creatureRepository.countByZoneId(1L)).thenReturn(5L);

IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.delete(1L));
assertTrue(ex.getMessage().toLowerCase().contains("cannot delete zone"));
verify(zoneRepository, never()).delete(any());
}

@Test
void delete_ok_whenZoneEmpty() {
Zone z = new Zone(); z.setId(2L); z.setName("Desierto");
when(zoneRepository.findById(2L)).thenReturn(Optional.of(z));
when(creatureRepository.countByZoneId(2L)).thenReturn(0L);

assertDoesNotThrow(() -> service.delete(2L));
verify(zoneRepository).delete(z);
}

@Test
void delete_ok_whenNoCreatures() {
Zone z = new Zone(); z.setId(2L); z.setName("Desierto");
when(zoneRepository.findById(2L)).thenReturn(Optional.of(z));
when(creatureRepository.countByZoneId(2L)).thenReturn(0L);

assertDoesNotThrow(() -> service.delete(2L));
verify(zoneRepository).delete(z);
}

@Test
void delete_throws_whenZoneNotFound() {
when(zoneRepository.findById(99L)).thenReturn(Optional.empty());
assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));
}
}
