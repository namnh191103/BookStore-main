package com.bookstore.admin.service;

import java.util.List;
import java.util.NoSuchElementException;

import com.bookstore.admin.exceptionhandler.ShippingRateAlreadyExistsException;
import com.bookstore.admin.exceptionhandler.ShippingRateNotFoundException;
import com.bookstore.admin.repository.ShippingRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.admin.paging.PagingAndSortingHelper;
import com.bookstore.admin.repository.ProductRepository;
import com.bookstore.admin.repository.CityRepository;
import com.bookstore.entity.City;
import com.bookstore.entity.ShippingRate;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ShippingRateService {

	public static final int RATES_PER_PAGE = 10;

	@Autowired
	private ShippingRateRepository shipRepo;
	@Autowired
	private CityRepository cityRepo;
	@Autowired
	private ProductRepository productRepo;

	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
		helper.listEntities(pageNum, RATES_PER_PAGE, shipRepo);
	}

	public List<City> listAllCities() {
		return cityRepo.findAllByOrderByNameAsc();
	}

	public void save(ShippingRate rateInForm) throws ShippingRateAlreadyExistsException {
		ShippingRate rateInDB = shipRepo.findByCityAndDistrict(
				rateInForm.getCity().getId(), rateInForm.getDistrict());
		boolean foundExistingRateInNewMode = rateInForm.getId() == null && rateInDB != null;
		boolean foundDifferentExistingRateInEditMode = rateInForm.getId() != null && rateInDB != null
				&& !rateInDB.equals(rateInForm);

		if (foundExistingRateInNewMode || foundDifferentExistingRateInEditMode) {
			throw new ShippingRateAlreadyExistsException("..."
					+ rateInForm.getCity().getName() + ", " + rateInForm.getDistrict());
		}
		shipRepo.save(rateInForm);
	}

	public ShippingRate get(Integer id) throws ShippingRateNotFoundException {
		try {
			return shipRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new ShippingRateNotFoundException("no ID " + id);
		}
	}

	public void updateCODSupport(Integer id, boolean codSupported) throws ShippingRateNotFoundException {
		Long count = shipRepo.countById(id);
		if (count == null || count == 0) {
			throw new ShippingRateNotFoundException("Không tìm thấy giá cước vận chuyển với ID " + id);
		}

		shipRepo.updateCODSupport(id, codSupported);
	}

	public void delete(Integer id) throws ShippingRateNotFoundException {
		Long count = shipRepo.countById(id);
		if (count == null || count == 0) {
			throw new ShippingRateNotFoundException("Không tìm thấy giá cước vận chuyển với ID " + id);

		}
		shipRepo.deleteById(id);
	}

	public float calculateShippingCost(Integer productId, Integer cityId, String district) {
		return 0.0f;
	}

}
