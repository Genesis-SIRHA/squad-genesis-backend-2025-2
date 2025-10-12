package edu.dosw.services;

// class SessionServiceTest {
//
//  private SessionRepository sessionRepository;
//  private SessionService sessionService;

  //  @BeforeEach
  //  void setUp() {
  //    sessionRepository = mock(SessionRepository.class);
  //    sessionService = new SessionService(sessionRepository);
  //  }
  //
  //  @Test
  //  void getSessionsByGroupCode_shouldReturnSessionsWhenFound() {
  //    // Arrange
  //    String groupCode = "G1";
  //    Session session = new Session(groupCode, "RoomA", 1, DayOfWeek.MONDAY, 2025, 1);
  //    ArrayList<Session> sessions = new ArrayList<>(List.of(session));
  //
  //    when(sessionRepository.findByGroupCode(groupCode)).thenReturn(sessions);
  //
  //    // Act
  //    List<Session> result = sessionService.getSessionsByGroupCode(groupCode);
  //
  //    // Assert
  //    assertThat(result).isNotNull().hasSize(1);
  //    assertThat(result.get(0).getGroupCode()).isEqualTo("G1");
  //
  //    verify(sessionRepository).findByGroupCode(groupCode);
  //  }
//
//  @Test
//  void getSessionsByGroupCode_shouldReturnEmptyListWhenNoneFound() {
//    // Arrange
//    String groupCode = "G2";
//    when(sessionRepository.findByGroupCode(groupCode)).thenReturn(new ArrayList<>());
//
//    // Act
//    List<Session> result = sessionService.getSessionsByGroupCode(groupCode);
//
//    // Assert
//    assertThat(result).isNotNull().isEmpty();
//
//    verify(sessionRepository).findByGroupCode(groupCode);
//  }
// }
