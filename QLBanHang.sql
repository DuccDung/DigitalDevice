-- ??t user m?c ??nh
ALTER SESSION SET CURRENT_SCHEMA = QLBanHang;

-- T?o b?ng tblChatLieu
CREATE TABLE tblChatLieu (
    MaChatLieu VARCHAR2(50) NOT NULL,
    TenChatLieu CLOB NOT NULL,
    CONSTRAINT PK_tblChatLieu PRIMARY KEY (MaChatLieu)
) TABLESPACE QLBanHang_TBS;

-- T?o b?ng tblHang
CREATE TABLE tblHang (
    MaHang VARCHAR2(50) NOT NULL,
    TenHang VARCHAR2(50),
    ChatLieu VARCHAR2(50),
    SoLuong NUMBER,
    DonGiaNhap VARCHAR2(50),
    DonGiaBan VARCHAR2(50),
    Anh CLOB,
    CONSTRAINT PK_tblHang PRIMARY KEY (MaHang),
    CONSTRAINT FK_tblHang_tblChatLieu FOREIGN KEY (ChatLieu)
        REFERENCES tblChatLieu (MaChatLieu)
) TABLESPACE QLBanHang_TBS;


-- T?o b?ng tblHDBan
CREATE TABLE tblHDBan (
    MaHDBan VARCHAR2(50) NOT NULL,
    MaNhanVien VARCHAR2(50),
    MaKhach VARCHAR2(50),
    NgayBan DATE,
    TongTien CLOB,
    CONSTRAINT PK_tblHDBan PRIMARY KEY (MaHDBan)
) TABLESPACE QLBanHang_TBS;

-- T?o b?ng tblChiTietHDBan
CREATE TABLE tblChiTietHDBan (
    MaHDBan VARCHAR2(50) NOT NULL,
    MaHang VARCHAR2(50) NOT NULL,
    SoLuong NUMBER,
    GiamGia NUMBER,
    ThanhTien VARCHAR2(50),
    CONSTRAINT PK_tblChiTietHDBan PRIMARY KEY (MaHDBan, MaHang),
    CONSTRAINT FK_tblChiTietHDBan_tblHDBan FOREIGN KEY (MaHDBan)
        REFERENCES tblHDBan (MaHDBan) ON DELETE CASCADE,
    CONSTRAINT FK_tblChiTietHDBan_tblHang FOREIGN KEY (MaHang)
        REFERENCES tblHang (MaHang)
) TABLESPACE QLBanHang_TBS;

-- T?o b?ng tblKhach
CREATE TABLE tblKhach (
    MaKhach VARCHAR2(50) NOT NULL,
    TenKhach VARCHAR2(50) NOT NULL,
    DiaChi CLOB,
    SoDienThoai VARCHAR2(50) NOT NULL,
    NgaySinh DATE,
    GhiChu CLOB,
    CONSTRAINT PK_tblKhach PRIMARY KEY (MaKhach)
) TABLESPACE QLBanHang_TBS;

-- T?o b?ng tblNhanVien
CREATE TABLE tblNhanVien (
    MaNhanVien VARCHAR2(50) NOT NULL,
    TenNhanVien VARCHAR2(50),
    GioiTinh VARCHAR2(50),
    NgayVaoLam DATE,
    ThamNien VARCHAR2(50),
    PhongBan VARCHAR2(50),
    MatKhau VARCHAR2(50),
    CONSTRAINT PK_tblNhanVien PRIMARY KEY (MaNhanVien)
) TABLESPACE QLBanHang_TBS;

-- T?o khóa ngo?i cho tblHDBan
ALTER TABLE tblHDBan ADD CONSTRAINT FK_tblHDBan_tblKhach FOREIGN KEY (MaKhach)
    REFERENCES tblKhach (MaKhach);

ALTER TABLE tblHDBan ADD CONSTRAINT FK_tblHDBan_tblNhanVien FOREIGN KEY (MaNhanVien)
    REFERENCES tblNhanVien (MaNhanVien);
    
-- Thêm d? li?u m?u
INSERT INTO tblChatLieu (MaChatLieu, TenChatLieu) VALUES ('CL01', 'Nh?a');
INSERT INTO tblChatLieu (MaChatLieu, TenChatLieu) VALUES ('CL02', 'Kim lo?i');

INSERT INTO tblHang (MaHang, TenHang, ChatLieu, SoLuong, DonGiaNhap, DonGiaBan, Anh)
VALUES ('01', 'Bút', 'CL01', 935, '3000', '5000', NULL);

INSERT INTO tblHang (MaHang, TenHang, ChatLieu, SoLuong, DonGiaNhap, DonGiaBan, Anh)
VALUES ('02', 'V?', 'CL01', 942, '5000', '10000', NULL);

INSERT INTO tblKhach (MaKhach, TenKhach, DiaChi, SoDienThoai, NgaySinh, GhiChu)
VALUES ('MK1', '?? V?n D?ng', 'Hà N?i', '1234567', NULL, NULL);

INSERT INTO tblNhanVien (MaNhanVien, TenNhanVien, GioiTinh, NgayVaoLam, ThamNien, PhongBan, MatKhau)
VALUES ('001', '?ông', 'Nam', TO_DATE('2022-11-19', 'YYYY-MM-DD'), '2', 'CSKH', '001');

INSERT INTO tblHDBan (MaHDBan, MaNhanVien, MaKhach, NgayBan, TongTien)
VALUES ('HDB_281020249', '001', 'MK1', TO_DATE('2024-10-29', 'YYYY-MM-DD'), '25000.00');

INSERT INTO tblChiTietHDBan (MaHDBan, MaHang, SoLuong, GiamGia, ThanhTien)
VALUES ('HDB_281020249', '01', 5, 0, '25000.00');

-- Ki?m tra các b?ng ?ã t?o
SELECT * FROM tblChatLieu;
SELECT * FROM tblHang;
SELECT * FROM tblKhach;
SELECT * FROM tblNhanVien;
SELECT * FROM tblHDBan;
SELECT * FROM tblChiTietHDBan;

commit;