package pet.store.service;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pet.store.controller.model.PetStoreData;
import pet.store.controller.model.PetStoreData.PetStoreCustomer;
import pet.store.controller.model.PetStoreData.PetStoreEmployee;
import pet.store.dao.EmployeeDao;
import pet.store.dao.CustomerDao;
import pet.store.dao.PetStoreDao;
import pet.store.entity.Customer;
import pet.store.entity.Employee;
import pet.store.entity.PetStore;

@Service
public class PetStoreService {
	
	@Autowired
	private PetStoreDao petStoreDao;
	
	@Autowired
	private EmployeeDao employeeDao;
	
	@Autowired
	private CustomerDao customerDao;


	public PetStoreData savePetStore(PetStoreData petStoreData) {
		
		
		Long petStoreId = petStoreData.getPetStoreId();
		PetStore petStore = findOrCreatePetStore(petStoreId);
		setFieldsInPetStore(petStore, petStoreData);
		return new PetStoreData(petStoreDao.save(petStore));
		
	}
	
	private PetStore findOrCreatePetStore(Long petStoreId) {
		
		PetStore petStore;
		
		if(Objects.isNull(petStoreId)) {
			petStore = new PetStore();
		}
		
		else {
			petStore = findPetStoreById(petStoreId);
			
		}
		return petStore;
	}	

	private PetStore findPetStoreById(Long petStoreId) {
		return petStoreDao.findById(petStoreId)
				.orElseThrow(() -> new NoSuchElementException(
						"Pet store with ID " + petStoreId + " not found."));
	}
	
	private void setFieldsInPetStore(PetStore petStore, PetStoreData petStoreData) {
		petStore.setPetStoreName(petStoreData.getPetStoreName());
		petStore.setPetStoreAddress(petStoreData.getPetStoreAddress());
		petStore.setPetStoreCity(petStoreData.getPetStoreCity());
		petStore.setPetStoreState(petStoreData.getPetStoreState());
		petStore.setPetStoreZip(petStoreData.getPetStoreZip());
		petStore.setPetStorePhoneNumber(petStoreData.getPetStorePhoneNumber());
    }

	@Transactional(readOnly = false)
	public PetStoreEmployee saveEmployee(Long petStoreId, PetStoreEmployee petStoreEmployee) {
		PetStore petStore = findPetStoreById(petStoreId);
		Long employeeId = petStoreEmployee.getEmployeeId();
		Employee employee = findOrCreateEmployee(petStoreId, employeeId);
		copyEmployeeFields(employee, petStoreEmployee);
		employee.setPetStore(petStore);
		petStore.getEmployees().add(employee);
		Employee dbEmployee = employeeDao.save(employee);
		return new PetStoreEmployee(dbEmployee);
	}

	

	private Employee findOrCreateEmployee(Long petStoreId, Long employeeId) {
		if (employeeId == null) {
			return new Employee();
		}
		else {
			return findEmployeeById(petStoreId, employeeId);
		}
	}

	private Employee findEmployeeById(Long petStoreId, Long employeeId) {
		
	    Employee employee = employeeDao.findById(employeeId)
	            .orElseThrow(() -> new NoSuchElementException("Employee with ID = " + employeeId + " was not found."));

	    if (!employee.getPetStore().getPetStoreId().equals(petStoreId)) {
	        throw new IllegalArgumentException("Employee with ID " + employeeId +
	                " does not belong to PetStore with ID " + petStoreId);
	    }

	    return employee;
		
	}
	
	private void copyEmployeeFields(Employee employee, PetStoreEmployee petStoreEmployee) {
		employee.setEmployeeFirstName(petStoreEmployee.getEmployeeFirstName());
		employee.setEmployeeId(petStoreEmployee.getEmployeeId());
	    employee.setEmployeeJobTitle(petStoreEmployee.getEmployeeJobTitle());
	    employee.setEmployeeLastName(petStoreEmployee.getEmployeeLastName());
	    employee.setEmployeePhone(petStoreEmployee.getEmployeePhone());
		
	}

	@Transactional(readOnly = false)
	public PetStoreCustomer saveCustomer(Long petStoreId, PetStoreCustomer petStoreCustomer) {
		PetStore petStore = findPetStoreById(petStoreId);
		Long customerId = petStoreCustomer.getCustomerId();
		Customer customer = findOrCreateCustomer(customerId, petStoreId);
		copyCustomerFields(customer, petStoreCustomer);
		customer.getPetStore().add(petStore);
		petStore.getCustomers().add(customer);
		Customer dbCustomer = customerDao.save(customer);
		return new PetStoreCustomer(dbCustomer);
	}


	private void copyCustomerFields(Customer customer, PetStoreCustomer petStoreCustomer) {
		customer.setCustomerId(petStoreCustomer.getCustomerId());
		customer.setCustomerFirstName(petStoreCustomer.getCustomerFirstName());
		customer.setCustomerLastName(petStoreCustomer.getCustomerLastName());
		customer.setCustomerEmail(petStoreCustomer.getCustomerEmail());
		
	}

	private Customer findOrCreateCustomer(Long customerId, Long petStoreId) {
		if (customerId == null) {
			return new Customer();
		}
		else {
			return findCustomerById(petStoreId, customerId);
		}
	}

	private Customer findCustomerById(Long petStoreId, Long customerId) {
		
	    Customer customer = customerDao.findById(customerId)
	            .orElseThrow(() -> new NoSuchElementException("Customer with ID = " + customerId + " was not found."));

	    boolean foundPetStore = false;
	    for (PetStore petStore : customer.getPetStore()) {
	    	if (petStore.getPetStoreId().equals(petStoreId)) {
	    		foundPetStore = true;
	    		break;
	    	}
	    }
	    if (!foundPetStore) {
	        throw new IllegalArgumentException("Customer with ID " + customerId +
	                " does not belong to PetStore with ID " + petStoreId);
	    }

	    return customer;
	}

	@Transactional(readOnly = true)
	public List<PetStoreData> retrieveAllPetStores() {
		
		List<PetStore> petStores = petStoreDao.findAll();
		
		List<PetStoreData> petStoreDataList = new LinkedList<>();
		
		for(PetStore petStore : petStores) {
			PetStoreData psd = new PetStoreData(petStore);

			psd.getCustomers().clear();
			psd.getEmployees().clear();
			petStoreDataList.add(psd);
			
		}
	
		return 	petStoreDataList;
	
	}
	
	@Transactional(readOnly = true)
	public PetStoreData retrievePetStoreById(Long petStoreId) {
		PetStore petStore = findPetStoreById(petStoreId);
		return new PetStoreData(petStore);
	}

	public void deletePetStoreById(Long petStoreId) {
		PetStore petStore = findPetStoreById(petStoreId);
		petStoreDao.delete(petStore);
		
	}

}	
