package edu.dosw.services;

import edu.dosw.model.Historial;
import edu.dosw.repositories.HistorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Service class that handles business logic related to student historical records.
 * Provides methods for retrieving and managing student academic history information.
 */
@Service
public class HistorialService {

    private final HistorialRepository historialRepository;

    @Autowired
    public HistorialService(HistorialRepository historialRepository) {
        this.historialRepository = historialRepository;
    }

    /**
     * Retrieves the list of group codes for a student's current sessions based on academic period.
     *
     * @param studentId the unique identifier of the student
     * @param year the academic year to filter by
     * @param period the academic period to filter by (e.g., '1' for first semester, '2' for second semester)
     * @return ArrayList of group codes representing the student's current sessions
     */
    public ArrayList<String> getCurrentSessionsByStudentIdAndPeriod(String studentId, String year, String period) {
        ArrayList<Historial> historial = historialRepository.findCurrentSessionsByStudentIdAndYearAndPeriod(studentId, year, period);
        ArrayList<String> groupCodes = new ArrayList<>();
        for (Historial h : historial) {
            groupCodes.add(h.getGroupCode());
        }
        return groupCodes;
    }

    public ArrayList<Historial> getSessionsByStudentIdAndPeriod(String studentId, String year, String period) {
        return historialRepository.findCurrentSessionsByStudentIdAndYearAndPeriod(studentId, year, period);
    }
}
