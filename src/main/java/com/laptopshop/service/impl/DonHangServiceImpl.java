package com.laptopshop.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.laptopshop.ulti.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.laptopshop.dto.SearchDonHangObject;
import com.laptopshop.entities.DonHang;
import com.laptopshop.entities.NguoiDung;
import com.laptopshop.entities.QDonHang;
import com.laptopshop.repository.DonHangRepository;
import com.laptopshop.service.DonHangService;
import com.querydsl.core.BooleanBuilder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Service
public class DonHangServiceImpl implements DonHangService {

	@Autowired
	private DonHangRepository donHangRepo;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Page<DonHang> getAllDonHangByFilter(SearchDonHangObject object, int page) throws ParseException {
//		BooleanBuilder builder = new BooleanBuilder();
//
//		String trangThaiDon = object.getTrangThaiDon();
//		String tuNgay = object.getTuNgay();
//		String denNgay = object.getDenNgay();
//		SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
//
//		if (!trangThaiDon.equals("")) {
//			builder.and(QDonHang.donHang.trangThaiDonHang.eq(trangThaiDon));
//		}
//
//		if (!tuNgay.equals("") && tuNgay != null) {
//			if (trangThaiDon.equals("") || trangThaiDon.equals("dang-cho-giao") || trangThaiDon.equals("da-bi-huy")) {
//				builder.and(QDonHang.donHang.ngayDatHang.goe(formatDate.parse(tuNgay)));
//			} else if (trangThaiDon.equals("dang-giao")) {
//				builder.and(QDonHang.donHang.ngayGiaoHang.goe(formatDate.parse(tuNgay)));
//			} else { // hoàn thành
//				builder.and(QDonHang.donHang.ngayNhanHang.goe(formatDate.parse(tuNgay)));
//			}
//		}
//
//		if (!denNgay.equals("") && denNgay != null) {
//			if (trangThaiDon.equals("") || trangThaiDon.equals("dang-cho-giao") || trangThaiDon.equals("da-bi-huy")) {
//				builder.and(QDonHang.donHang.ngayDatHang.loe(formatDate.parse(denNgay)));
//			} else if (trangThaiDon.equals("dang-giao")) {
//				builder.and(QDonHang.donHang.ngayGiaoHang.loe(formatDate.parse(denNgay)));
//			} else { // hoàn thành
//				builder.and(QDonHang.donHang.ngayNhanHang.loe(formatDate.parse(denNgay)));
//			}
//		}
//		Page<DonHang> result = donHangRepo.findAll(builder, PageRequest.of(page - 1, 6));
//		for(int i = 0; i < result.getContent().size(); i++){
//			result.getContent().get(i).setTrangThaiDonHang(ConvertUtils.convertStateOrder(result.getContent().get(i).getTrangThaiDonHang()));
//		}
//		return result;
//		return donHangRepo.findAll(builder, PageRequest.of(page - 1, 6));

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM don_hang WHERE 1 = 1 ");

		String trangThaiDon = object.getTrangThaiDon();
		String tuNgay = object.getTuNgay();
		String denNgay = object.getDenNgay();
		SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");

		if (!trangThaiDon.equals("")) {
			sql.append("AND trang_thai_don_hang = '" + trangThaiDon + "'");
		}

		if (!tuNgay.equals("") && tuNgay != null) {
			if (trangThaiDon.equals("") || trangThaiDon.equals("dang-cho-giao") || trangThaiDon.equals("da-bi-huy")) {
				sql.append("AND ngay_dat_hang >= " + formatDate.parse(tuNgay));
			} else if (trangThaiDon.equals("dang-giao")) {
				sql.append("AND ngay_giao_hang >= " + formatDate.parse(tuNgay));
			} else { // hoàn thành
				sql.append("AND ngay_nhan_hang >= " + formatDate.parse(tuNgay));
			}
		}

		if (!denNgay.equals("") && denNgay != null) {
			if (trangThaiDon.equals("") || trangThaiDon.equals("dang-cho-giao") || trangThaiDon.equals("da-bi-huy")) {
				sql.append("AND ngay_dat_hang <= " + formatDate.parse(denNgay));
			} else if (trangThaiDon.equals("dang-giao")) {
				sql.append("AND ngay_giao_hang <= " + formatDate.parse(denNgay));
			} else { // hoàn thành
				sql.append("AND ngay_nhan_hang <= " + formatDate.parse(denNgay));
			}
		}

		Query query = entityManager.createNativeQuery(sql.toString(), DonHang.class);

		int pageSize = 6;
		query.setFirstResult((page - 1) * pageSize);
		query.setMaxResults(pageSize);

		List<DonHang> resultList = query.getResultList();
		for (DonHang donHang : resultList) {
			donHang.setTrangThaiDonHang(ConvertUtils.convertStateOrder(donHang.getTrangThaiDonHang()));
		}

		int totalResults = entityManager.createNativeQuery("SELECT COUNT(*) FROM DonHang WHERE 1 = 1 " + sql.toString())
				.getFirstResult();
		int totalPages = (int) Math.ceil((double) totalResults / pageSize);

		return new PageImpl<>(resultList, PageRequest.of(page - 1, pageSize), totalPages);

	}

	@Override
	public DonHang update(DonHang dh) {
		return donHangRepo.save(dh);
	}

	@Override
	public DonHang findById(long id) {

		DonHang result = donHangRepo.findById(id).get();

		result.setTrangThaiDonHang(ConvertUtils.convertStateOrder(result.getTrangThaiDonHang()));

		return result;
	}

	@Override
	public List<DonHang> findByTrangThaiDonHangAndShipper(String trangThai, NguoiDung shipper) {
		return donHangRepo.findByTrangThaiDonHangAndShipper(trangThai, shipper);
	}

	@Override
	public DonHang save(DonHang dh) {
		return donHangRepo.save(dh);
	}

	@Override
	public List<Object> layDonHangTheoThangVaNam() {
		return donHangRepo.layDonHangTheoThangVaNam();
	}
	
	@Override
	public List<DonHang> getDonHangByNguoiDung(NguoiDung ng) {
		return donHangRepo.findByNguoiDat(ng);
	}

	@Override
	public Page<DonHang> findDonHangByShipper(SearchDonHangObject object, int page, int size, NguoiDung shipper) throws ParseException {
		BooleanBuilder builder = new BooleanBuilder();

		String trangThaiDon = object.getTrangThaiDon();
		String tuNgay = object.getTuNgay();
		String denNgay = object.getDenNgay();
		SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
		
		builder.and(QDonHang.donHang.shipper.eq(shipper));

		if (!trangThaiDon.equals("")) {
			builder.and(QDonHang.donHang.trangThaiDonHang.eq(trangThaiDon));
		}

		if (!tuNgay.equals("") && tuNgay != null) {
			if (trangThaiDon.equals("dang-giao")) {
				builder.and(QDonHang.donHang.ngayGiaoHang.goe(formatDate.parse(tuNgay)));
			} else { // hoàn thành
				builder.and(QDonHang.donHang.ngayNhanHang.goe(formatDate.parse(tuNgay)));
			}
		}

		if (!denNgay.equals("") && denNgay != null) {
			if (trangThaiDon.equals("dang-giao")) {
				builder.and(QDonHang.donHang.ngayGiaoHang.loe(formatDate.parse(denNgay)));
			} else { // hoàn thành
				builder.and(QDonHang.donHang.ngayNhanHang.loe(formatDate.parse(denNgay)));
			}
		}

		Page<DonHang> result = donHangRepo.findAll(builder, PageRequest.of(page - 1, size));
		for(int i = 0; i < result.getContent().size(); i++){
			result.getContent().get(i).setTrangThaiDonHang(ConvertUtils.convertStateOrder(result.getContent().get(i).getTrangThaiDonHang()));
		}
		return result;
	}

	@Override
	public int countByTrangThaiDonHang(String trangThaiDonHang) {
		return donHangRepo.countByTrangThaiDonHang(trangThaiDonHang);
	}

	@Override
	public List<Object[]> reportPareto(String type) {
		StringBuilder sql = new StringBuilder("SELECT dm.ten_danh_muc AS doanhMuc, " +
				"SUM(ctdh.so_luong_dat * ctdh.don_gia) AS doanhThu, " +
				"SUM(ctdh.so_luong_dat) AS soLuong " +
				"FROM san_pham sp " +
				"INNER JOIN danh_muc dm ON sp.ma_danh_muc = dm.id " +
				"INNER JOIN chi_tiet_don_hang ctdh ON sp.id = ctdh.ma_san_pham " +
				"INNER JOIN don_hang dh ON ctdh.ma_don_hang = dh.id ");
		switch (type){
			case "thang":
				sql.append("WHERE \n" +
						"    dh.ngay_dat_hang >= DATEADD(month, DATEDIFF(month, 0, GETDATE()) - 1, 0)\n" +
						"    AND dh.ngay_dat_hang < DATEADD(month, DATEDIFF(month, 0, GETDATE()), 0)");
				break;
			case  "quy":
				sql.append("WHERE \n" +
						"    dh.ngay_dat_hang >= DATEADD(month, DATEDIFF(month, 0, GETDATE()) - 3, 0)\n" +
						"    AND dh.ngay_dat_hang < DATEADD(month, DATEDIFF(month, 0, GETDATE()), 0)");
				break;
			case "nam":
				sql.append("WHERE \n" +
						"    dh.ngay_dat_hang >= DATEADD(month, DATEDIFF(month, 0, GETDATE()) - 12, 0)\n" +
						"    AND dh.ngay_dat_hang < DATEADD(month, DATEDIFF(month, 0, GETDATE()), 0)");
				break;
			default:
				break;
		}

		sql.append("GROUP BY dm.ten_danh_muc");

		Query query = entityManager.createNativeQuery(sql.toString());
		List<Object[]> results = query.getResultList();
		return results;
	}

	@Override
	public List<Object[]> getTop5ProductsByMonth(int month) {
		StringBuilder sql = new StringBuilder("SELECT TOP 5\n" +
				"    sp.ten_san_pham AS tenSanPham,\n" +
				"    SUM(ctdh.so_luong_dat * ctdh.don_gia) AS doanhThu\n" +
				"FROM\n" +
				"    chi_tiet_don_hang ctdh\n" +
				"INNER JOIN\n" +
				"    san_pham sp ON ctdh.ma_san_pham = sp.id\n "+
				"INNER JOIN don_hang dh ON ctdh.ma_don_hang = dh.id "
				);
		sql.append("WHERE MONTH(dh.ngay_dat_hang) = " + month +
				" AND YEAR(dh.ngay_dat_hang) = YEAR(GETDATE()) ");
		sql.append("GROUP BY\n" +
				"    sp.ten_san_pham\n" +
				"ORDER BY\n" +
				"    doanhThu DESC;");
		Query query = entityManager.createNativeQuery(sql.toString());
		List<Object[]> results = query.getResultList();
		return results;
	}

}
