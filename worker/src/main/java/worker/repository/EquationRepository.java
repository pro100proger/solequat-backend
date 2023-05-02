package worker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.core.entity.Equation;

public interface EquationRepository extends JpaRepository<Equation, String> {

    Equation getEquationById(String id);
    Optional<List<Equation>> getAllEquationsByUserId(String userId);
}
