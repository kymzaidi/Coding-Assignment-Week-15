package pet.store.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pet.store.entity.Customer;

public interface CustomerDao extends JpaRepository<Customer, Long> {
	Optional<Customer> findByCustomerId(Long customerId);	

}
