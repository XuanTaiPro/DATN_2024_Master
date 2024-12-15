window.hoaDonCtrl = function ($scope, $http) {
    $scope.page = 0; // Initial page
    $scope.hoaDons = {}; // Initialize hoaDons data
    $scope.currentPage = 0;
    $scope.itemsPerPage = 10; // Number of items per page
    $scope.totalPages = 0;
    $scope.errorMessage = ''; // Error message initialization
    $scope.selectedTrangThai = null; // Track selected status
    $scope.searchText = ""; // Tìm kiếm theo tên khách hàng
    $scope.loaiHD = null; // Lọc theo loại hóa đơn
    $scope.nhanVien = ""; // Lọc theo nhân viên
    $scope.selectedLoaiHD = null;
    $scope.selectedNhanVien = null;
    $scope.searchText = '';
    $scope.loaiHDFilters = [
        {value: 1, name: 'Online'},
        {value: 0, name: 'Tại quầy'}
    ];
    $scope.nhanViens = [];
    $http.get('http://localhost:8083/hoadon/listNV').then(function (response) {
        $scope.nhanViens = response.data;
    });

    // $scope.chiTietHoaDons=[];
    $scope.getHoaDonsByTrangThai = function (trangThai, page) {
        $scope.selectedTrangThai = trangThai; // Cập nhật trạng thái đã chọn
        $scope.currentPage = page || 0; // Mặc định trang 0 nếu không có trang nào được cung cấp

        // Bắt đầu xây dựng URL với trang hiện tại

        // const tongTienDaGiam = sessionStorage.getItem('tongTienDaGiam');
        // if (tongTienDaGiam) {
        //     $scope.tongTienDaGiam = parseFloat(tongTienDaGiam); // Gán giá trị tổng tiền vào $scope
        // } else {
        //     $scope.tongTienDaGiam = 0; // Mặc định nếu không tìm thấy
        // }

        let url = 'http://localhost:8083/hoadon/page?page=' + $scope.currentPage;

        // Thêm tham số trạng thái nếu không phải là null
        if (trangThai !== null) {
            url += '&trangThai=' + trangThai;
        }

        // Thêm loại hóa đơn vào URL nếu không phải là null
        if ($scope.selectedLoaiHD !== null) {
            url += '&loaiHD=' + $scope.selectedLoaiHD;
        }

        // Thêm nhân viên vào URL nếu không phải là null
        if ($scope.selectedNhanVien !== null) {
            url += '&nhanVien=' + $scope.selectedNhanVien;
        }
        if ($scope.searchText) {
            url += '&searchText=' + encodeURIComponent($scope.searchText);
        }

        $http.get(url)
            .then(async function (response) {
                const listHD = response.data.hoaDons;

                const requests = listHD.map(async item => {
                    try {
                        const response = await fetch(`http://localhost:8083/chitiethoadon/getAllByOrderId?idHD=` + item.id);
                        const resultCTHD = await response.json();

                        if (item.trangThai === 3) {
                            const tt = $scope.getTotalAmount(resultCTHD);
                            const giaGiamVC = item.giaGiamVC || 0;
                            const giaMax = item.giaMax || 0;

                            let totalAfterVC = tt - tt * (giaGiamVC / 100);
                            item['ttLastMoney'] = tt - Math.min(totalAfterVC, giaMax);
                            item['tt'] = tt;
                        }
                        return item;
                    } catch (error) {
                        console.error(`Lỗi khi xử lý hóa đơn ID: ${item.id}`, error);
                        return item; // Trả về item dù có lỗi để không gián đoạn toàn bộ
                    }
                });

                $scope.hoaDons = await Promise.all(requests);
                $scope.totalPages = response.data.totalPages;
                $scope.pages = Array.from({ length: $scope.totalPages }, (v, i) => i);
            })
            .catch(function (error) {
                $scope.errorMessage = 'Lỗi khi lấy hóa đơn: ' + (error.data?.message || JSON.stringify(error.data));
            });

    };


    // Pagination controls
    $scope.previousPage = function () {
        if ($scope.currentPage > 0) {
            $scope.getHoaDonsByTrangThai($scope.selectedTrangThai, $scope.currentPage - 1);
        }
    };

    $scope.nextPage = function () {
        if ($scope.currentPage < $scope.totalPages - 1) {
            $scope.getHoaDonsByTrangThai($scope.selectedTrangThai, $scope.currentPage + 1);
        }
    };
    $scope.confirmInvoice = function (id) {
        console.log('Confirming invoice with ID:', id);

        // Gọi API để xác nhận hóa đơn
        $http.put('http://localhost:8083/hoadon/xacNhanHD?idHD=' + id)
            .then(function (response) {
                console.log(response.data);
                // Có thể làm gì đó với phản hồi thành công ở đây
                alert("Xác nhận hóa đơn " + id + " thành công.");
                // Tải lại hóa đơn hoặc cập nhật danh sách
                $scope.getHoaDonsByTrangThai($scope.selectedTrangThai, $scope.currentPage - 1);
            })
            .catch(function (error) {
                $scope.getHoaDonsByTrangThai($scope.selectedTrangThai, $scope.currentPage - 1);
            });
    };
    $scope.updateGhiChu = function (hd) {
        const payload = {
            ghiChu: hd.ghiChu // Assuming `hd.ghiChu` contains the updated note
        };
        $http.put('http://localhost:8083/hoadon/update-ghi-chu/' + hd.id, payload)
            .then(function (response) {
                console.log("Update successful:", response);
                $scope.detailHoaDon(hd.id);
            })
            .catch(function (error) {
                console.log("Error:", error);
            });
    };
    $scope.detailHoaDon = function (id) {
        $http.get('http://localhost:8083/hoadon/detail', {params: {idHD: id}})
            .then(function (response) {
                console.log("Full response data:", response.data); // Log toàn bộ dữ liệu phản hồi
                // Lưu hoaDonRep và chiTietHoaDons vào scope
                if (response.data) {
                    $scope.hoaDonDetail = response.data.hoaDonRep; // Lưu thông tin hóa đơn
                    $scope.chiTietHoaDons = response.data.chiTietHoaDons; // Lưu danh sách chi tiết hóa đơn
                }

                $('#readData').modal('show'); // Hiển thị modal
            })
            .catch(function (error) {
                console.error('Error fetching invoice details:', error);
                alert('Không thể lấy thông tin chi tiết của hóa đơn.');
            });
    };
    $scope.calculateTotalAmount = function (cthd) {
        if (!cthd) return 0; // Kiểm tra nếu đối tượng không tồn tại

        const gia = cthd.giaSauGiam || cthd.giaBan; // Lấy giá sau giảm, nếu không có thì dùng giá bán
        const soLuong = cthd.soLuong || 0; // Lấy số lượng, mặc định là 0 nếu không có

        return gia * soLuong; // Tính tổng tiền
    };

    // $scope.calculateInvoiceTotal = function (idHD) {
    //     let total = 0;
    //     // Lọc chi tiết hóa đơn của hóa đơn này
    //     $scope.chiTietHoaDons.forEach(hdct => {
    //         if (hdct.idHD === idHD) {  // Kiểm tra nếu chi tiết hóa đơn thuộc về hóa đơn này
    //             total += hdct.giaSauGiam * hdct.soLuong;  // Tính tổng tiền cho chi tiết
    //         }
    //     });
    //     return total;
    // };
// Call calculateInvoiceTotal for each hoaDon in hoaDons array
//     $scope.hoaDons.forEach(function(hoaDon) {
//         $scope.detailHoaDon(hoaDon.id);
//         console.log("Total for invoice ID " + hoaDon.id + ": " + total);
//     });// Hàm tính tổng tiền cho tất cả chi tiết hóa đơn trong tab hiện tại
    $scope.calculateTotalForList = function (chiTietHoaDons) {
        if (!Array.isArray(chiTietHoaDons)) return 0; // Kiểm tra nếu không phải danh sách

        return chiTietHoaDons.reduce(function (total, cthd) {
            return total + $scope.calculateTotalAmount(cthd); // Sử dụng hàm tính tổng cho từng chi tiết
        }, 0); // Tính tổng tiền bằng cách cộng dồn
    };
    $scope.getTotalAmount = function (items) {
        let total = 0;
        if (items) {
            items.forEach(item => {
                // Ensure both giaSauGiam and giaBan are valid numbers before multiplying
                const giaSauGiam = parseFloat(item.giaSauGiam);
                const giaMax = parseFloat(item.giaMax);
                const giaGiamVC = parseFloat(item.giaGiamVC);
                const soLuong = parseFloat(item.soLuong);
                if (!isNaN(giaSauGiam) && !isNaN(soLuong)) {
                    total += giaSauGiam * soLuong; // Cộng dồn tổng tiền
                }
            });
            total
        }
        return total; // Trả về tổng tiền
    };

    $scope.deleteInvoice = function (idHD) {
        $http({
            method: 'DELETE',
            url: 'http://localhost:8083/hoadon/delete', // Đường dẫn tới API
            data: {id: idHD}, // Gửi id hóa đơn qua request body
            headers: {"Content-Type": "application/json;charset=utf-8"}
        })
            .then(function (response) {
                $scope.getHoaDonsByTrangThai($scope.selectedTrangThai, $scope.currentPage - 1);
            })
            .catch(function (error) {
                // Xử lý lỗi
                if (error.status === 404) {
                    alert('Hóa đơn không tồn tại!');
                } else {
                    alert('Không thể xóa hóa đơn, vui lòng thử lại sau.');
                }
            });

    };

    $scope.getHoaDonsByTrangThai(null, 0);
};
