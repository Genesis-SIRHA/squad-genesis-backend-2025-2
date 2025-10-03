package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.exception.BusinessException;
import edu.dosw.model.User;
import edu.dosw.repositories.MembersRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link MembersService}. */
class MembersServiceTest {

  private MembersRepository membersRepository;
  private MembersService membersService;

  @BeforeEach
  void setUp() {
    membersRepository = mock(MembersRepository.class);
    membersService = new MembersService(membersRepository);
  }

  @Test
  void listById_shouldReturnUserWhenExists() {

    User user = new User("U1", "student", "John Doe", "Engineering", "F1");
    when(membersRepository.findById("U1")).thenReturn(Optional.of(user));

    User result = membersService.listById("U1");

    assertNotNull(result);
    assertEquals("U1", result.getUserId());
    assertEquals("F1", result.getFacultyName()); // corregido
    verify(membersRepository).findById("U1");
  }

  @Test
  void listById_shouldThrowWhenUserNotFound() {

    when(membersRepository.findById("U1")).thenReturn(Optional.empty());

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> membersService.listById("U1"));

    assertEquals("User not found with id: U1", exception.getMessage());
    verify(membersRepository).findById("U1");
  }

  @Test
  void getFaculty_shouldReturnFacultyName() {

    User user = new User("U1", "student", "John Doe", "Engineering", "F1");
    when(membersRepository.findById("U1")).thenReturn(Optional.of(user));

    String faculty = membersService.getFaculty("U1");

    assertEquals("F1", faculty);
    verify(membersRepository).findById("U1");
  }

  @Test
  void getFaculty_shouldThrowWhenUserNotFound() {

    when(membersRepository.findById("U1")).thenReturn(Optional.empty());
    assertThrows(
        BusinessException.class,
        () -> membersService.getFaculty("U1"));

    verify(membersRepository).findById("U1");
  }
}
