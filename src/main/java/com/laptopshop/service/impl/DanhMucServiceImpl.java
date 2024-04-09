package com.laptopshop.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.laptopshop.entities.DanhMuc;
import com.laptopshop.repository.DanhMucRepository;
import com.laptopshop.service.DanhMucService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Service
public class DanhMucServiceImpl implements DanhMucService {

	@Autowired
	private DanhMucRepository repo;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public DanhMuc save(DanhMuc d) {
		return repo.save(d);
	}

	@Override
	public DanhMuc update(DanhMuc d) {
		return repo.save(d);
	}

	@Override
	public void deleteById(long id) {
		repo.deleteById(id);
	}

	@Override
	public Page<DanhMuc> getAllDanhMucForPageable(int page, int size) {
		// Tạo truy vấn SQL native cho phân trang
		String sqlQuery = "SELECT * FROM danh_muc ORDER BY id";
		int offset = (page - 1) * size;
		String pagingClause = " OFFSET " + offset + " ROWS FETCH NEXT " + size + " ROWS ONLY";

		// Thực hiện truy vấn SQL
		List<DanhMuc> danhMucList = entityManager.createNativeQuery(sqlQuery + pagingClause, DanhMuc.class)
				.getResultList();

		// Đếm tổng số lượng danh mục
		Integer totalCount = Integer.parseInt(entityManager.createNativeQuery("SELECT COUNT(*) FROM danh_muc").getSingleResult().toString());

		return new PageImpl<>(danhMucList, PageRequest.of(page - 1, size), totalCount);
	}

	@Override
	public DanhMuc getDanhMucById(long id) {
		return repo.findById(id).get();
	}

	@Override
	public List<DanhMuc> getAllDanhMuc() {
		return repo.findAll();
	}

}
