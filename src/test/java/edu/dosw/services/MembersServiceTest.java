package edu.dosw.services;

import edu.dosw.model.User;
import edu.dosw.repositories.MembersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link MembersService}.
 */
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
        // Arrange
        User user = new User("U1", "student", "John Doe", "Engineering", "F1");
        when(membersRepository.findById("U1")).thenReturn(Optional.of(user));

        // Act
        User result = membersService.listById("U1");

        // Assert
        assertNotNull(result);
        assertEquals("U1", result.getUserId());
        assertEquals("F1", result.getFacultyId());
        verify(membersRepository).findById("U1");
    }

    @Test
    void listById_shouldThrowWhenUserNotFound() {
        // Arrange
        when(membersRepository.findById("U1")).thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> membersService.listById("U1"));

        assertEquals("User not found with id: U1", exception.getMessage());
        verify(membersRepository).findById("U1");
    }

    @Test
    void getFaculty_shouldReturnFacultyId() {
        // Arrange
        User user = new User("U1", "student", "John Doe", "Engineering", "F1");
        when(membersRepository.findById("U1")).thenReturn(Optional.of(user));

        // Act
        String faculty = membersService.getFaculty("U1");

        // Assert
        assertEquals("F1", faculty);
        verify(membersRepository).findById("U1");
    }

    @Test
    void getFaculty_shouldThrowWhenUserNotFound() {
        // Arrange
        when(membersRepository.findById("U1")).thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> membersService.getFaculty("U1"));

        assertEquals("No value present", exception.getMessage());
        verify(membersRepository).findById("U1");
    }
}
