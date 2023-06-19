package pet.store.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pet.store.entity.PetStore;

public interface PetStoreDao extends JpaRepository<PetStore, Long> {
	Optional<PetStore> findByPetStoreId(Long petStoreId);	

	}


