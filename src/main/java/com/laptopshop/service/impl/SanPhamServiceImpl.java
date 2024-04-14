package com.laptopshop.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.laptopshop.dto.SanPhamDto;
import com.laptopshop.dto.SearchSanPhamObject;
import com.laptopshop.entities.QSanPham;
import com.laptopshop.entities.SanPham;
import com.laptopshop.repository.DanhMucRepository;
import com.laptopshop.repository.HangSanXuatRepository;
import com.laptopshop.repository.SanPhamRepository;
import com.laptopshop.service.SanPhamService;
import com.querydsl.core.BooleanBuilder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Service
@Transactional
public class SanPhamServiceImpl implements SanPhamService {

	@Autowired
	private SanPhamRepository sanPhamRepo;

	@Autowired
	private DanhMucRepository danhMucRepo;

	@Autowired
	private HangSanXuatRepository hangSanXuatRepo;

	@PersistenceContext
	private EntityManager entityManager;

	// đổi từ SanPhamDto sang đối tượng SanPham để add vào db
	public SanPham convertFromSanPhamDto(SanPhamDto dto) {
		SanPham sanPham = new SanPham();
		if (!dto.getId().equals("")) {
			sanPham.setId(Long.parseLong(dto.getId()));
		}
		sanPham.setTenSanPham(dto.getTenSanPham());
		sanPham.setCpu(dto.getCpu());
		sanPham.setDanhMuc(danhMucRepo.findById(dto.getDanhMucId()).get());
		sanPham.setHangSanXuat(hangSanXuatRepo.findById(dto.getNhaSXId()).get());
		sanPham.setDonGia(Long.parseLong(dto.getDonGia()));
		sanPham.setThietKe(dto.getThietKe());
		sanPham.setThongTinBaoHanh(dto.getThongTinBaoHanh());
		sanPham.setThongTinChung(dto.getThongTinChung());
		sanPham.setManHinh(dto.getManHinh());
		sanPham.setRam(dto.getRam());
		sanPham.setDungLuongPin(dto.getDungLuongPin());
		sanPham.setDonViKho(Integer.parseInt(dto.getDonViKho()));
		sanPham.setHeDieuHanh(dto.getHeDieuHanh());

		return sanPham;
	}

	@Override
	public SanPham save(SanPhamDto dto) {
		SanPham sp = convertFromSanPhamDto(dto);
		System.out.println(sp);
		return sanPhamRepo.save(sp);
	}

	@Override
	public SanPham update(SanPhamDto dto) {
		return sanPhamRepo.save(convertFromSanPhamDto(dto));
	}

	@Override
	public void deleteById(long id) {
		sanPhamRepo.deleteById(id);

	}

	@Override
	public Page<SanPham> getAllSanPhamByFilter(SearchSanPhamObject object, int page, int limit) {
		String price = object.getDonGia();

		// sắp xếp theo giá
		String sortDirection = object.getSapXepTheoGia().equals("desc") ? "DESC" : "ASC";
		String sortColumn = "don_gia";
		String orderByClause = " ORDER BY " + sortColumn + " " + sortDirection;

		// Tạo câu truy vấn SQL
		StringBuilder sqlQuery = new StringBuilder("SELECT * FROM san_pham WHERE 1=1");
		StringBuilder sqlQueryCount = new StringBuilder("SELECT COUNT(*) FROM san_pham WHERE 1=1");

		if (!object.getDanhMucId().equals("") && object.getDanhMucId() != null) {
			sqlQuery.append(" AND ma_danh_muc = ").append(Long.parseLong(object.getDanhMucId()));
			sqlQueryCount.append(" AND ma_danh_muc = ").append(Long.parseLong(object.getDanhMucId()));
		}

		if (!object.getHangSXId().equals("") && object.getHangSXId() != null) {
			sqlQuery.append(" AND ma_hang_sx = ").append(Long.parseLong(object.getHangSXId()));
			sqlQueryCount.append(" AND ma_hang_sx = ").append(Long.parseLong(object.getHangSXId()));
		}

		// tim theo don gia
		switch (price) {
			case "duoi-2-trieu":
				sqlQuery.append(" AND don_gia < 2000000");
				sqlQueryCount.append(" AND don_gia < 2000000");
				break;

			case "2-trieu-den-4-trieu":
				sqlQuery.append(" AND don_gia BETWEEN 2000000 AND 4000000");
				sqlQueryCount.append(" AND don_gia BETWEEN 2000000 AND 4000000");
				break;

			case "4-trieu-den-6-trieu":
				sqlQuery.append(" AND don_gia BETWEEN 4000000 AND 6000000");
				sqlQueryCount.append(" AND don_gia BETWEEN 4000000 AND 6000000");
				break;

			case "6-trieu-den-10-trieu":
				sqlQuery.append(" AND don_gia BETWEEN 6000000 AND 10000000");
				sqlQueryCount.append(" AND don_gia BETWEEN 6000000 AND 10000000");
				break;

			case "tren-10-trieu":
				sqlQuery.append(" AND don_gia > 10000000");
				sqlQueryCount.append(" AND don_gia > 10000000");
				break;

			default:
				break;
		}

		// Phân trang
		int offset = (page - 1) * limit;
		String pagingClause = " OFFSET " + offset + " ROWS FETCH NEXT " + limit + " ROWS ONLY";

		// Thực hiện truy vấn SQL để lấy dữ liệu
		List<SanPham> resultList = entityManager.createNativeQuery(sqlQuery.toString() + orderByClause + pagingClause, SanPham.class)
				.getResultList();

		// Đếm tổng số lượng sản phẩm
		Integer totalCount =  Integer.parseInt(entityManager.createNativeQuery(sqlQueryCount.toString()).getSingleResult().toString());

		return new PageImpl<>(resultList, PageRequest.of(page- 1, limit), totalCount);
	}

	@Override
	public List<SanPham> getLatestSanPham() {
		return sanPhamRepo.findFirst12ByDanhMucTenDanhMucContainingIgnoreCaseOrderByIdDesc("Laptop");
	}

	public Iterable<SanPham> getSanPhamByTenSanPhamWithoutPaginate(SearchSanPhamObject object) {
		BooleanBuilder builder = new BooleanBuilder();
		int resultPerPage = 12;
		String[] keywords = object.getKeyword();
		String sort = object.getSort();
		String price = object.getDonGia();
		// Keyword
		builder.and(QSanPham.sanPham.tenSanPham.like("%" + keywords[0] + "%"));
		if (keywords.length > 1) {
			for (int i = 1; i < keywords.length; i++) {
				builder.and(QSanPham.sanPham.tenSanPham.like("%" + keywords[i] + "%"));
			}
		}
		// Muc gia
		switch (price) {
		case "duoi-2-trieu":
			builder.and(QSanPham.sanPham.donGia.lt(2000000));
			break;

		case "2-trieu-den-4-trieu":
			builder.and(QSanPham.sanPham.donGia.between(2000000, 4000000));
			break;

		case "4-trieu-den-6-trieu":
			builder.and(QSanPham.sanPham.donGia.between(4000000, 6000000));
			break;

		case "6-trieu-den-10-trieu":
			builder.and(QSanPham.sanPham.donGia.between(6000000, 10000000));
			break;

		case "tren-10-trieu":
			builder.and(QSanPham.sanPham.donGia.gt(10000000));
			break;

		default:
			break;
		}
		return sanPhamRepo.findAll(builder);
	}

	@Override
	public SanPham getSanPhamById(long id) {
		return sanPhamRepo.findById(id).get();
	}

	// Tim kiem san pham theo keyword, sap xep, phan trang, loc theo muc gia, lay 12
	// san pham moi trang
	@Override
	public Page<SanPham> getSanPhamByTenSanPham(SearchSanPhamObject object, int page, int resultPerPage) {
		BooleanBuilder builder = new BooleanBuilder();
//		int resultPerPage = 12;
		String[] keywords = object.getKeyword();
		String sort = object.getSort();
		String price = object.getDonGia();
		String brand = object.getBrand();
		String manufactor = object.getManufactor();
		// Keyword
		builder.and(QSanPham.sanPham.tenSanPham.like("%" + keywords[0] + "%"));
		if (keywords.length > 1) {
			for (int i = 1; i < keywords.length; i++) {
				builder.and(QSanPham.sanPham.tenSanPham.like("%" + keywords[i] + "%"));
			}
		}
		// Muc gia
		switch (price) {
		case "duoi-2-trieu":
			builder.and(QSanPham.sanPham.donGia.lt(2000000));
			break;

		case "2-trieu-den-4-trieu":
			builder.and(QSanPham.sanPham.donGia.between(2000000, 4000000));
			break;

		case "4-trieu-den-6-trieu":
			builder.and(QSanPham.sanPham.donGia.between(4000000, 6000000));
			break;

		case "6-trieu-den-10-trieu":
			builder.and(QSanPham.sanPham.donGia.between(6000000, 10000000));
			break;

		case "tren-10-trieu":
			builder.and(QSanPham.sanPham.donGia.gt(10000000));
			break;

		default:
			break;
		}

		// Danh muc va hang san xuat
		if (brand.length()>1) {
			builder.and(QSanPham.sanPham.danhMuc.tenDanhMuc.eq(brand));
		}
		if (manufactor.length()>1) {
			builder.and(QSanPham.sanPham.hangSanXuat.tenHangSanXuat.eq(manufactor));
		}

		// Sap xep
		if (sort.equals("newest")) {
			return sanPhamRepo.findAll(builder, PageRequest.of(page - 1, resultPerPage, Sort.Direction.DESC, "id"));
		} else if (sort.equals("priceAsc")) {
			return sanPhamRepo.findAll(builder, PageRequest.of(page - 1, resultPerPage, Sort.Direction.ASC, "donGia"));
		} else if (sort.equals("priceDes")) {
			return sanPhamRepo.findAll(builder, PageRequest.of(page - 1, resultPerPage, Sort.Direction.DESC, "donGia"));
		}
		return sanPhamRepo.findAll(builder, PageRequest.of(page - 1, resultPerPage));
	}

	public List<SanPham> getAllSanPhamByList(Set<Long> idList) {
		return sanPhamRepo.findByIdIn(idList);
	}

	@Override
	public Page<SanPham> getSanPhamByTenSanPhamForAdmin(String tenSanPham, int page, int size) {
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(QSanPham.sanPham.tenSanPham.like("%" + tenSanPham + "%"));
		return sanPhamRepo.findAll(builder, PageRequest.of(page, size));
	}
	
	
	@Override
	public Iterable<SanPham> getSanPhamByTenDanhMuc(String brand) {
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(QSanPham.sanPham.danhMuc.tenDanhMuc.eq(brand));
		return sanPhamRepo.findAll(builder);
	}
	
	@Override
	public Page<SanPham> getSanPhamByBrand(SearchSanPhamObject object, int page, int resultPerPage) {
		String price = object.getDonGia();
		String brand = object.getBrand();
		String manufactor = object.getManufactor();
		String os = object.getOs();
		String ram = object.getRam();
		String pin = object.getPin();

		StringBuilder sqlBuilder = new StringBuilder();
		StringBuilder sqlCountBuilder = new StringBuilder();

		sqlBuilder.append("SELECT * FROM san_pham WHERE 1 = 1");
		sqlCountBuilder.append("SELECT COUNT(*) FROM san_pham WHERE 1 = 1");

		// Thêm điều kiện cho mức giá
		switch (price) {
			case "duoi-2-trieu":
				sqlBuilder.append(" AND don_gia < 2000000");
				sqlCountBuilder.append(" AND don_gia < 2000000");
				break;
			case "2-trieu-den-4-trieu":
				sqlBuilder.append(" AND don_gia BETWEEN 2000000 AND 4000000");
				sqlCountBuilder.append(" AND don_gia BETWEEN 2000000 AND 4000000");
				break;
			case "4-trieu-den-6-trieu":
				sqlBuilder.append(" AND don_gia BETWEEN 4000000 AND 6000000");
				sqlCountBuilder.append(" AND don_gia BETWEEN 4000000 AND 6000000");
				break;
			case "6-trieu-den-10-trieu":
				sqlBuilder.append(" AND don_gia BETWEEN 6000000 AND 10000000");
				sqlCountBuilder.append(" AND don_gia BETWEEN 6000000 AND 10000000");
				break;
			case "tren-10-trieu":
				sqlBuilder.append(" AND don_gia > 10000000");
				sqlCountBuilder.append(" AND don_gia > 10000000");
				break;
			default:
				break;
		}

		Long categoryId = null;
		Long manufactorId = null;

// Lấy ID của danh mục nếu tên danh mục được cung cấp
		if (brand.length() > 1) {
			categoryId = getCategoryIdFromName(brand);
		}
// Lấy ID của hãng sản xuất nếu tên hãng sản xuất được cung cấp
		if (manufactor.length() > 1) {
			manufactorId = getManufactorIdFromName(manufactor);
		}

// Xây dựng câu truy vấn chính với ID của danh mục và hãng sản xuất
		if (categoryId != null) {
			sqlBuilder.append(" AND ma_danh_muc = ").append(categoryId);
			sqlCountBuilder.append(" AND ma_danh_muc = ").append(categoryId);
		}
		if (manufactorId != null) {
			sqlBuilder.append(" AND ma_hang_sx = ").append(manufactorId);
			sqlCountBuilder.append(" AND ma_hang_sx = ").append(manufactorId);
		}
		if (os.length() > 1) {
			sqlBuilder.append(" AND he_dieu_hanh LIKE '%").append(os).append("%'");
			sqlCountBuilder.append(" AND he_dieu_hanh LIKE '%").append(os).append("%'");
		}
		if (ram.length() > 1) {
			sqlBuilder.append(" AND ram LIKE '%").append(ram).append("%'");
			sqlCountBuilder.append(" AND ram LIKE '%").append(ram).append("%'");
		}
		if (pin.length() > 1) {
			sqlBuilder.append(" AND dung_luong_pin = '").append(pin).append("'");
			sqlCountBuilder.append(" AND dung_luong_pin = '").append(pin).append("'");
		}

		// Sắp xếp theo một trường nào đó
		sqlBuilder.append(" ORDER BY id");

		// Tính offset
		int offset = (page - 1) * resultPerPage;
		String pagingClause = " OFFSET " + offset + " ROWS FETCH NEXT " + resultPerPage + " ROWS ONLY";

		// Thực hiện truy vấn SQL để lấy dữ liệu
		List<SanPham> resultList = entityManager.createNativeQuery(sqlBuilder.toString() + pagingClause, SanPham.class)
				.getResultList();

		// Đếm tổng số lượng sản phẩm
		String countSql = "SELECT COUNT(*) FROM (" + sqlBuilder.toString() + ") AS subquery";
		Integer totalCount = Integer.parseInt(entityManager.createNativeQuery(sqlCountBuilder.toString()).getSingleResult().toString());

		return new PageImpl<>(resultList, PageRequest.of(page - 1, resultPerPage), totalCount);
	}

	@Override
	public void updateSanPhamFields() {
		String field1Value = "thiết kế không thay đổi, vỏ nhôm sang trọng, siêu mỏng và siêu nhẹ";
		String field2Value = "hiệu năng được nâng cấp, thời lượng pin cực lâu, phù hợp cho nhu cầu làm việc văn phòng nhẹ nhàng, không cần quá chú trọng vào hiển thị của màn hình";
		String sql = "UPDATE san_pham SET thiet_ke = ?, thong_tin_chung = ?";

		entityManager.createNativeQuery(sql)
				.setParameter(1, field1Value)
				.setParameter(2, field2Value)
				.executeUpdate();
	}

	private Long getCategoryIdFromName(String brand) {
		String sql = "SELECT id FROM danh_muc WHERE ten_danh_muc = ?";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter(1, brand);
		List<Object> result = query.getResultList();
		if (!result.isEmpty()) {
			return ((Number) result.get(0)).longValue();
		}
		return null;
	}

	private Long getManufactorIdFromName(String manufactor) {
		String sql = "SELECT id FROM hang_san_xuat WHERE ten_hang_san_xuat = ?";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter(1, manufactor);
		List<Object> result = query.getResultList();
		if (!result.isEmpty()) {
			return ((Number) result.get(0)).longValue();
		}
		return null;
	}
}
