package com.bookstore.admin.controller;

import java.io.IOException;
import java.util.List;

import com.bookstore.admin.exporter.ProductCSVExporter;
import com.bookstore.admin.service.ProductSaveHelper;
import com.bookstore.admin.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bookstore.admin.service.BrandService;
import com.bookstore.admin.service.CategoryService;
import com.bookstore.admin.model.BookStoreUserDetails;
import com.bookstore.admin.config.FileUploadUtil;
import com.bookstore.entity.Brand;
import com.bookstore.entity.Category;
import com.bookstore.entity.product.Product;
import com.bookstore.exception.ProductNotFoundException;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private BrandService brandService;

    @Autowired
	private CategoryService categoryService;


    @GetMapping("/products")
    public String listFirstPage(Model model) { 
        return listByPage(1, model, "name", "asc", null, 0);
    }

    @GetMapping("/products/page/{pageNum}")
	public String listByPage(
            @PathVariable(name = "pageNum") int pageNum, Model model,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir, 
			@Param("keyword") String keyword,
            @Param("categoryId") Integer categoryId ){

        Page<Product> page = productService.listByPage(pageNum, sortField, sortDir, keyword, categoryId);
        List<Product> listProducts = page.getContent();

        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

		long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
        if (categoryId != null) model.addAttribute("categoryId", categoryId);

		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
        model.addAttribute("listProducts", listProducts);
        model.addAttribute("listCategories", listCategories);

		return "products/products";
	}

    @GetMapping("/products/new")
    public String newProduct(Model model) {
        List<Brand> listBrands = brandService.listAll();

        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);
        
        model.addAttribute("product", product);
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("pageTitle", "Create New Product");
        model.addAttribute("numberOfExistingExtraImages", 0);

        return "products/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes ra,
            @RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipart, 
            @RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultiparts,
            @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
            @RequestParam(name = "detailNames", required = false) String[] detailNames,
            @RequestParam(name = "detailValues", required = false) String[] detailValues,
            @RequestParam(name = "imageIDs", required = false) String[] imageIDs,
            @RequestParam(name = "imageNames", required = false) String[] imageNames,
            @AuthenticationPrincipal BookStoreUserDetails loggedUser
            ) throws IOException{

        if (loggedUser.hasRole("Salesperson")) {
            productService.saveProductPrice(product);
            ra.addFlashAttribute("message", "Sản phẩm đã được lưu thành công!");
            return "redirect:/products";
        }

        ProductSaveHelper.setMainImgeName(mainImageMultipart, product);
        ProductSaveHelper.setExistingExtraImageNames(imageIDs, imageNames, product);
        ProductSaveHelper.setNewExtraImageNames(extraImageMultiparts, product);
        ProductSaveHelper.setProductDetails(detailIDs, detailNames ,detailValues, product);

        Product savedProduct = productService.save(product);

        ProductSaveHelper.saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);

        ProductSaveHelper.deleteExtraImagesWeredRemovedOnForm(product);

        ra.addFlashAttribute("message", "Sản phẩm đã được lưu thành công.");

        return "redirect:/products";
    }

    

    @GetMapping("/products/{id}/enabled/{status}")
    public String updateProductEnabledStatus(@PathVariable("id") Integer id,
            @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) { 
        productService.updateProductEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The Product ID " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);
        
        return "redirect:/products";
    }

	/*
	 * @GetMapping("/products/delete/{id}") public String
	 * deleteProduct(@PathVariable(name = "id") Integer id, Model model,
	 * RedirectAttributes redirectAttributes) { try { productService.delete(id);
	 * String productExtraImagesDir = "../product-images/" + id + "/extras"; String
	 * productImagesDir = "../product-images/" + id;
	 * 
	 * FileUploadUtil.removeDir(productExtraImagesDir);
	 * FileUploadUtil.removeDir(productImagesDir);
	 * 
	 * redirectAttributes.addFlashAttribute("message", "ID Sản Phẩm " + id +
	 * " đã được xóa thành công!"); } catch (ProductNotFoundException ex) {
	 * redirectAttributes.addFlashAttribute("message", ex.getMessage()); } return
	 * "redirect:/products"; }
	 */
    
    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Thực hiện xóa sản phẩm bằng ID
            productService.delete(id);

            // Xóa thư mục chứa hình ảnh của sản phẩm nếu cần
            String productDir = "../product-images/" + id;
            FileUploadUtil.removeDir(productDir);

            // Thêm thông báo thành công vào thuộc tính flash
            redirectAttributes.addFlashAttribute("message", 
                "ID: " + id + " của sản phẩm đã được xóa thành công!");

        } catch (DataIntegrityViolationException ex) {
            // Xử lý lỗi khi không thể xóa do dữ liệu liên quan
            redirectAttributes.addFlashAttribute("message", 
                "ID: " + id + " không thể xóa do liên quan đến dữ liệu khác!");

        } catch (ProductNotFoundException ex) {
            // Xử lý lỗi khi sản phẩm không tồn tại
            redirectAttributes.addFlashAttribute("message", 
                "ID: " + id + " không tồn tại hoặc đã bị xóa!");
        }

        // Chuyển hướng về danh sách sản phẩm
        return "redirect:/products";
    }


    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Product product = productService.get(id);
            List<Brand> listBrands = brandService.listAll();
            Integer numberOfExistingExtraImages = product.getImages().size();

            model.addAttribute("product", product);
            model.addAttribute("listBrands", listBrands);
            model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
            model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);

            return "products/product_form";
        } catch (ProductNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/products";
        }
    }

    @GetMapping("/products/detail/{id}")
    public String viewProductDetails(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Product product = productService.get(id);
            model.addAttribute("product", product);
            
            return "products/product_detail_modal";
        } catch (ProductNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/products";
        }
    }
    @GetMapping("products/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        // Assuming you have a service or repository to fetch brands
        List<Product> listProducts = productService.listAll(); // Replace with your actual logic

        ProductCSVExporter csvExporter = new ProductCSVExporter();
        csvExporter.export(listProducts, response);
    }


}