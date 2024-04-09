package com.laptopshop.service.impl;

import java.util.List;

import com.laptopshop.entities.DanhMuc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.laptopshop.entities.HangSanXuat;
import com.laptopshop.repository.HangSanXuatRepository;
import com.laptopshop.service.HangSanXuatService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class HangSanXuatServiceImpl implements HangSanXuatService {

	@Autowired
	private HangSanXuatRepository repo;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<HangSanXuat> getALlHangSX() {
		return repo.findAll();
	}

	@Override
	public HangSanXuat getHSXById(long id) {
		return repo.findById(id).get();
	}

	@Override
	public HangSanXuat save(HangSanXuat h) {
		return repo.save(h);
	}

	@Override
	public HangSanXuat update(HangSanXuat h) {
		return repo.save(h);
	}

	@Override
	public void deleteById(long id) {
		repo.deleteById(id);
	}

	@Override
	public Page<HangSanXuat> getALlHangSX(int page, int size) {
		// Tạo truy vấn SQL native cho phân trang
		String sqlQuery = "SELECT * FROM hang_san_xuat ORDER BY id";
		int offset = (page - 1) * size;
		String pagingClause = " OFFSET " + offset + " ROWS FETCH NEXT " + size + " ROWS ONLY";

		// Thực hiện truy vấn SQL
		List<HangSanXuat> danhMucList = entityManager.createNativeQuery(sqlQuery + pagingClause, HangSanXuat.class)
				.getResultList();

		// Đếm tổng số lượng danh mục
		Integer totalCount = Integer.parseInt(entityManager.createNativeQuery("SELECT COUNT(*) FROM hang_san_xuat").getSingleResult().toString());

		return new PageImpl<>(danhMucList, PageRequest.of(page - 1, size), totalCount);
	}

}
