package edu.dosw.services;

import edu.dosw.model.Historial;
import edu.dosw.repositories.HistorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class HistorialService {

    private final HistorialRepository historialRepository;

    @Autowired
    public HistorialService(HistorialRepository historialRepository) {
        this.historialRepository = historialRepository;
    }

    public ArrayList<String> getCurrentSessionsByStudentIdAndPeriod(String studentId, String year, String period) {
        ArrayList<Historial> historial = historialRepository.findCurrentSessionsByStudentIdAndYearAndPeriod(studentId, year, period);
        ArrayList<String> groupCodes = new ArrayList<>();
        for (Historial h : historial) {
            groupCodes.add(h.getGroupCode());
        }
        return groupCodes;
    }
}
