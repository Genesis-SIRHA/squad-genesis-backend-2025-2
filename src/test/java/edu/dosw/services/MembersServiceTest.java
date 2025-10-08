package edu.dosw.services;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//import edu.dosw.exception.BusinessException;
//import edu.dosw.model.User;
//import edu.dosw.repositories.UserCredentialsRepository;
//import java.util.Optional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
///** Unit tests for {@link MembersService}. */
//class MembersServiceTest {
//
//  private UserCredentialsRepository userRepository;
//  private MembersService membersService;
//
//  @BeforeEach
//  void setUp() {
//    userRepository = mock(UserCredentialsRepository.class);
//    membersService = new MembersService(userRepository);
//  }
//
//  @Test
//  void listById_shouldReturnUserWhenExists() {

//    User user = new User("U1", "John Doe");
//    when(userRepository.findById("U1")).thenReturn(Optional.of(user));
//
//    User result = membersService.listById("U1");
//
//    assertNotNull(result);
//    assertEquals("U1", result.getUserId());
//    assertEquals("F1", result.getFacultyName()); // corregido
//    verify(userRepository).findById("U1");
//  }

//  @Test
//  void listById_shouldThrowWhenUserNotFound() {
//
//    when(userRepository.findById("U1")).thenReturn(Optional.empty());
//
//    RuntimeException exception =
//        assertThrows(RuntimeException.class, () -> membersService.listById("U1"));

//    assertEquals("User not found with id: U1", exception.getMessage());
//    verify(userRepository).findById("U1");
//  }
//
//  @Test
//  void getFaculty_shouldReturnFacultyName() {
//
//    User user = new User("U1", "John Doe");
//    when(userRepository.findById("U1")).thenReturn(Optional.of(user));
//
//    String faculty = membersService.getFaculty("U1");
//
//    assertEquals("F1", faculty);
//    verify(userRepository).findById("U1");
//  }
//
//  @Test
//  void getFaculty_shouldThrowWhenUserNotFound() {
//
//    when(userRepository.findById("U1")).thenReturn(Optional.empty());
//    assertThrows(BusinessException.class, () -> membersService.getFaculty("U1"));
//
//    verify(userRepository).findById("U1");
//  }
//}
