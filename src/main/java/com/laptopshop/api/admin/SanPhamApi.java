package com.laptopshop.api.admin;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.laptopshop.dto.SanPhamDto;
import com.laptopshop.dto.SearchSanPhamObject;
import com.laptopshop.entities.ResponseObject;
import com.laptopshop.entities.SanPham;
import com.laptopshop.service.SanPhamService;
import com.laptopshop.validator.SanPhamDtoValidator;

@RestController
@RequestMapping("api/san-pham")
public class SanPhamApi {

	@Autowired
	private SanPhamDtoValidator validator;

	@Autowired
	private SanPhamService sanPhamService;

	@InitBinder("sanPhamDto")
	protected void initialiseBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}

	// lấy tất cả san phẩm theo tiêu chí, mặc địch lấy tất cả 
	@GetMapping("/all")
	public Page<SanPham> getAllSanPhamByFilter(@PageableDefault(size = 10) Pageable pageable,
			 @RequestParam String danhMucId, @RequestParam String hangSXId, @RequestParam String donGia, @RequestParam String sapXepTheoGia) {
		SearchSanPhamObject searchObject = new SearchSanPhamObject();
		searchObject.setDanhMucId(danhMucId);
		searchObject.setHangSXId(hangSXId);
		searchObject.setDonGia(donGia);
		searchObject.setSapXepTheoGia(sapXepTheoGia);
		
		return sanPhamService.getAllSanPhamByFilter(searchObject, pageable.getPageNumber(), pageable.getPageSize());
	}
	
	@GetMapping("/latest")
	public List<SanPham> getLatestSanPham(){
		return sanPhamService.getLatestSanPham();
	}

	// lấy sản phẩm theo id
	@GetMapping("/{id}")
	public SanPham getSanPhamById(@PathVariable long id) {
		return sanPhamService.getSanPhamById(id);
	}
	
	
	// lấy sản phẩm theo tên
	@GetMapping("/")
	public Page<SanPham> getSanPhamById(@RequestParam String tenSanPham, @RequestParam(defaultValue = "1") int page) {
		return sanPhamService.getSanPhamByTenSanPhamForAdmin(tenSanPham, page-1, 10 );
	}

	// lưu sản phẩm vào db
	@PostMapping(value = "/save")
	public ResponseObject addSanPham(@ModelAttribute @Valid SanPhamDto newSanPhamDto, BindingResult result,
			HttpServletRequest request) {

		ResponseObject ro = new ResponseObject();

		// nếu có lỗi xảy ra ( validate)
		if (result.hasErrors()) {
			Map<String, String> errors = result.getFieldErrors().stream()
					.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
			errors.forEach((k, v) -> System.out.println(" test: Key : " + k + " Value : " + v));
			ro.setErrorMessages(errors);
			ro.setStatus("fail");
			errors = null;
		} else {
			// lưu sản phẩm
			SanPham sp = sanPhamService.save(newSanPhamDto);
			ro.setData(sp);
			saveImageForProduct(sp, newSanPhamDto, request);
			ro.setStatus("success");
		}
		return ro;
	}
	
	
	@DeleteMapping("/delete/{id}")
	public String deleteSanPham(@PathVariable long id) {
		sanPhamService.deleteById(id);
		return "OK !";
	}

	
	// lưu ảnh của sản phẩm vào thư mục
	public void saveImageForProduct(SanPham sp, SanPhamDto dto, HttpServletRequest request) {

		MultipartFile productImage = dto.getHinhAnh();
		String rootDirectory = request.getSession().getServletContext().getRealPath("/");
		Path path = Paths.get(rootDirectory + "/resources/images/" + sp.getId() + ".png");
		System.out.println(productImage != null && !productImage.isEmpty());
		if (productImage != null && !productImage.isEmpty()) {

			try {
				productImage.transferTo(new File(path.toString()));
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new RuntimeException("Product image saving failed", ex);
			}
		}
	}

	@PutMapping("/updatefield")
	public void update(){
		sanPhamService.updateSanPhamFields();
	}
}
