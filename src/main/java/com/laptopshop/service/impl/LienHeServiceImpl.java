package com.laptopshop.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.laptopshop.dto.LienHeDTO;
import com.laptopshop.dto.SearchLienHeObject;
import com.laptopshop.entities.LienHe;
import com.laptopshop.entities.QLienHe;
import com.laptopshop.repository.LienHeRepository;
import com.laptopshop.service.LienHeService;
import com.querydsl.core.BooleanBuilder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class LienHeServiceImpl implements LienHeService {

	@Autowired
	private LienHeRepository lienHeRepo;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Page<LienHe> getLienHeByFilter(SearchLienHeObject object, int page) throws ParseException {
		StringBuilder sqlQuery = new StringBuilder("SELECT * FROM lien_he WHERE 1=1");
		Integer limit = 10;
		// Xây dựng điều kiện cho truy vấn
		if (!object.getTrangThaiLienHe().isEmpty()) {
			sqlQuery.append(" AND trang_thai = '").append(object.getTrangThaiLienHe()).append("'");
		}

		if (!object.getTuNgay().isEmpty()) {
			sqlQuery.append(" AND ngay_lien_he >= '").append(object.getTuNgay()).append("'");
		}

		if (!object.getDenNgay().isEmpty()) {
			sqlQuery.append(" AND ngay_lien_he <= '").append(object.getDenNgay()).append("'");
		}

		// Xây dựng điều kiện sắp xếp
		String orderByClause = " ORDER BY ngay_lien_he DESC"; // Sắp xếp theo ngày liên hệ giảm dần

		// Phân trang
		int offset = (page - 1) * limit;
		String pagingClause = " OFFSET " + offset + " ROWS FETCH NEXT " + limit + " ROWS ONLY";

		// Thực hiện truy vấn SQL để lấy dữ liệu
		List<LienHe> resultList = entityManager.createNativeQuery(sqlQuery.toString() + orderByClause + pagingClause, LienHe.class)
				.getResultList();

		// Đếm tổng số lượng liên hệ
		Integer totalCount = Integer.parseInt(entityManager.createNativeQuery("SELECT COUNT(*) FROM lien_he WHERE 1=1").getSingleResult().toString());

		return new PageImpl<>(resultList, PageRequest.of(page - 1, limit), totalCount);
	}

	@Override
	public LienHe findById(long id) {
		return lienHeRepo.findById(id).get();
	}

	@Override
	public LienHe save(LienHe lh) {
		return lienHeRepo.save(lh);
	}

	@Override
	public int countByTrangThai(String trangThai) {
		return lienHeRepo.countByTrangThai(trangThai);
	}

}
