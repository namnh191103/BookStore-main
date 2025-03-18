package com.bookstore.service;

import java.util.Date;
import java.util.List;

import com.bookstore.repository.CustomerRepository;
import com.bookstore.entity.AuthenticationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bookstore.entity.City;
import com.bookstore.entity.Customer;
import com.bookstore.exception.CustomerNotFoundException;
import com.bookstore.repository.CityRepository;

import jakarta.transaction.Transactional;
import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class CustomerService {

	@Autowired
	private CityRepository cityRepository;
	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	public List<City> listAllCity() {
		return cityRepository.findAllByOrderByNameAsc();
	}

	public boolean isEmailUnique(String email) {
		Customer customer = customerRepository.findByEmail(email);

		return customer == null;
	}

	public void registerCustomer(Customer customer) {
		try {
			String pass = customer.getPassword();
			System.out.println(pass);

			encodePassword(customer);

			checkPassword(customer, pass);

			customer.setEnabled(false);
			customer.setCreatedTime(new Date());
			customer.setAuthenticationType(AuthenticationType.BOOKSTORE);

			String randomCode = RandomString.make(64);
			customer.setVerificationCode(randomCode);

			customerRepository.save(customer);
		} catch (Exception e) {
			// Log lỗi
			e.printStackTrace();
		}
	}

	public Customer getCustomerByEmail(String email) {
		return customerRepository.findByEmail(email);
	}

	public boolean checkPassword(Customer customer, String rawPassword) {
		// So sánh mật khẩu nhập vào với mật khẩu đã mã hóa
		return new BCryptPasswordEncoder().matches(rawPassword, customer.getPassword());
	}

	private void encodePassword(Customer customer) {
		String encodedPassword = passwordEncoder.encode(customer.getPassword());
		customer.setPassword(encodedPassword);
	}

	public boolean verify(String verificationCode) {
		Customer customer = customerRepository.findByVerificationCode(verificationCode);

		if (customer == null || customer.isEnabled()) {
			return false;
		} else {
			customerRepository.enable(customer.getId());
			return true;
		}
	}

	public void updateAuthenticationType(Customer customer, AuthenticationType type) {
		if (!customer.getAuthenticationType().equals(type)) {
			customerRepository.updateAuthenticationType(customer.getId(), type);
		}
	}

	public void addNewCustomerUponOAuthLogin(String name, String email, AuthenticationType authenticationType) {
		Customer customer = new Customer();
		customer.setEmail(email);

		setName(name, customer);

		customer.setFirstName(name);
		customer.setEnabled(true);
		customer.setCreatedTime(new Date());
		customer.setAuthenticationType(authenticationType);
		customer.setPassword("");
		customer.setAddressLine("");
		customer.setPhoneNumber("");
		customer.setCity(cityRepository.findByCode("DN"));
		;
		customer.setDistrict("");
		customer.setWard("");

		customerRepository.save(customer);
	}

	private void setName(String name, Customer customer) {
		String[] nameArray = name.split(" ");
		if (nameArray.length < 2) {
			customer.setFirstName(name);
			customer.setLastName("");
		} else {
			String firstName = nameArray[0];
			customer.setFirstName(firstName);

			String lastName = name.replaceFirst(firstName + " ", " ");
			customer.setLastName(lastName);
		}
	}

	public void update(Customer customerInForm) {
		Customer customerInDB = customerRepository.findById(customerInForm.getId()).get();

		if (customerInDB.getAuthenticationType().equals(AuthenticationType.BOOKSTORE)) {
			if (!customerInForm.getPassword().isEmpty()) {
				String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
				customerInForm.setPassword(encodedPassword);
			} else {
				customerInForm.setPassword(customerInDB.getPassword());
			}
		} else {
			customerInForm.setPassword(customerInDB.getPassword());
		}

		customerInForm.setEnabled(customerInDB.isEnabled());
		customerInForm.setCreatedTime(customerInDB.getCreatedTime());
		customerInForm.setVerificationCode(customerInDB.getVerificationCode());
		customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());
		customerInForm.setResetPasswordToken(customerInDB.getResetPasswordToken());

		customerRepository.save(customerInForm);
	}

	public String updateResetPasswordToken(String email) throws CustomerNotFoundException {
		Customer customer = customerRepository.findByEmail(email);
		if (customer != null) {
			String token = RandomString.make(30);
			customer.setResetPasswordToken(token);
			customerRepository.save(customer);

			return token;
		} else {
			throw new CustomerNotFoundException("Không tìm thấy khách hàng với e-mail: " + email);
		}
	}

	public Customer getByResetPasswordToken(String token) {
		return customerRepository.findByResetPasswordToken(token);
	}

	public void updatePassword(String token, String password) throws CustomerNotFoundException {
		Customer customer = customerRepository.findByResetPasswordToken(token);
		if (customer == null) {
			throw new CustomerNotFoundException("Không tìm thấy khách hàng: Không hợp lệ"); 
		}
		customer.setPassword(password);
		customer.setResetPasswordToken(null);
		encodePassword(customer);
		customerRepository.save(customer);
	}

	
}
