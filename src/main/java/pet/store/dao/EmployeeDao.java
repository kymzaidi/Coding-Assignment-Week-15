package pet.store.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pet.store.entity.Employee;

public interface EmployeeDao extends JpaRepository<Employee, Long> {
	Optional<Employee> findByEmployeeId(Long employeeId);	

}
