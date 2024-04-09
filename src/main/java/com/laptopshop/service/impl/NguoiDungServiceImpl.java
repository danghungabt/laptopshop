package com.laptopshop.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.laptopshop.dto.TaiKhoanDTO;
import com.laptopshop.entities.NguoiDung;
import com.laptopshop.entities.VaiTro;
import com.laptopshop.repository.NguoiDungRepository;
import com.laptopshop.repository.VaiTroRepository;
import com.laptopshop.service.NguoiDungService;

@Service
@Transactional
public class NguoiDungServiceImpl implements NguoiDungService {

	@Autowired
	private NguoiDungRepository nguoiDungRepo;

	@Autowired
	private VaiTroRepository vaiTroRepo;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public NguoiDung findByEmail(String email) {
		return nguoiDungRepo.findByEmail(email);
	}

	@Override
	public NguoiDung findByConfirmationToken(String confirmationToken) {
		return null;
	}

	@Override
	public NguoiDung saveUserForMember(NguoiDung nd) {
		nd.setPassword(bCryptPasswordEncoder.encode(nd.getPassword()));
		Set<VaiTro> setVaiTro = new HashSet<>();
		setVaiTro.add(vaiTroRepo.findByTenVaiTro("ROLE_MEMBER"));
		nd.setVaiTro(setVaiTro);
		return nguoiDungRepo.save(nd);
	}

	@Override
	public NguoiDung findById(long id) {
		NguoiDung nd = nguoiDungRepo.findById(id).get();
		return nd;
	}

	@Override
	public NguoiDung updateUser(NguoiDung nd) {
		return nguoiDungRepo.save(nd);
	}

	@Override
	public void changePass(NguoiDung nd, String newPass) {
		nd.setPassword(bCryptPasswordEncoder.encode(newPass));
		nguoiDungRepo.save(nd);
	}

	public Page<NguoiDung> getNguoiDungByVaiTro(Set<VaiTro> vaiTro, int page) {
		StringBuilder sqlQuery = new StringBuilder("SELECT * FROM nguoi_dung nd WHERE EXISTS (SELECT 1 FROM nguoidung_vaitro vtn WHERE vtn.ma_nguoi_dung = nd.id AND vtn.ma_vai_tro IN (");
		Integer limit = 10;
		// Thêm các ID của VaiTro vào truy vấn
		vaiTro.forEach(vt -> sqlQuery.append(vt.getId()).append(", "));

		// Xóa dấu phẩy cuối cùng và đóng ngoặc đóng
		sqlQuery.delete(sqlQuery.length() - 2, sqlQuery.length()).append("))");

		// Phân trang
		int offset = (page - 1) * limit;
		String pagingClause = " OFFSET " + offset + " ROWS FETCH NEXT " + limit + " ROWS ONLY";

		// Thực hiện truy vấn SQL để lấy dữ liệu
		List<NguoiDung> resultList = entityManager.createNativeQuery(sqlQuery.toString(), NguoiDung.class)
				.getResultList();

		// Đếm tổng số lượng người dùng
		Integer totalCount = Integer.parseInt(entityManager.createNativeQuery("SELECT COUNT(*) FROM nguoi_dung").getSingleResult().toString());

		return new PageImpl<>(resultList, PageRequest.of(page - 1, limit), totalCount);
	}

	@Override
	public List<NguoiDung> getNguoiDungByVaiTro(Set<VaiTro> vaiTro) {
		return nguoiDungRepo.findByVaiTro(vaiTro);
	}

	@Override
	public NguoiDung saveUserForAdmin(TaiKhoanDTO dto) {
		NguoiDung nd = new NguoiDung();
		nd.setHoTen(dto.getHoTen());
		nd.setDiaChi(dto.getDiaChi());
		nd.setEmail(dto.getEmail());
		nd.setSoDienThoai(dto.getSdt());
		nd.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
		
		Set<VaiTro> vaiTro  = new HashSet<>();
		vaiTro.add(vaiTroRepo.findByTenVaiTro(dto.getTenVaiTro()));
		nd.setVaiTro(vaiTro);
		
		return nguoiDungRepo.save(nd);
	}

	@Override
	public void deleteById(long id) {
		nguoiDungRepo.deleteById(id);
	}

}
