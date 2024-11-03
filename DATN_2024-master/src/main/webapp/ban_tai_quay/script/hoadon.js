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
        { value: 1, name: 'Online' },
        { value: 2, name: 'Tại quầy' }
    ];
    $scope.nhanViens = [];
    $http.get('http://localhost:8083/hoadon/listNV').then(function (response) {
        $scope.nhanViens = response.data;
    });

    $scope.getHoaDonsByTrangThai = function (trangThai, page) {
        $scope.selectedTrangThai = trangThai; // Cập nhật trạng thái đã chọn
        $scope.currentPage = page || 0; // Mặc định trang 0 nếu không có trang nào được cung cấp

        // Bắt đầu xây dựng URL với trang hiện tại
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

        // Thêm tìm kiếm vào URL nếu không phải là chuỗi rỗng
        if ($scope.searchText) {
            url += '&searchText=' + encodeURIComponent($scope.searchText);
        }

        $http.get(url)
            .then(function (response) {
                $scope.hoaDons = response.data.hoaDons;
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
                location.reload(true);
                $scope.getHoaDonsByTrangThai(2, 0);

            })
            .catch(function (error) {
                location.reload(true);
                $scope.getHoaDonsByTrangThai(2, 0);
            });
    };
    $scope.cancelInvoice = function (id) {
        console.log('Cancel invoice with ID:', id);
        // Gọi API để xác nhận hóa đơn
        $http.put('http://localhost:8083/hoadon/huyHD?idHD=' + id)
            .then(function (response) {
                console.log(response.data);
                // Có thể làm gì đó với phản hồi thành công ở đây
                alert("Hủy hóa đơn " + id + " thành công.");
                // Tải lại hóa đơn hoặc cập nhật danh sách
                location.reload();
                $scope.getHoaDonsByTrangThai(4, 0);
            })
            .catch(function (error) {
                location.reload();
                $scope.getHoaDonsByTrangThai(4, 0);
            });
    };
    $scope.detailHoaDon = function (id) {
        $http.get('http://localhost:8083/hoadon/detail', { params: { idHD: id } })
            .then(function (response) {
                console.log("Full response data:", response.data); // Log toàn bộ dữ liệu phản hồi
                // Lưu hoaDonRep và chiTietHoaDons vào scope
                if (response.data) {
                    $scope.hoaDonDetail = response.data.hoaDonRep; // Lưu thông tin hóa đơn
                    $scope.chiTietHoaDons = response.data.chiTietHoaDons; // Lưu danh sách chi tiết hóa đơn

                    console.log("hoaDonDetail:", $scope.hoaDonDetail);
                    console.log("Chi tiết hóa đơn:", $scope.chiTietHoaDons);
                }

                $('#readData').modal('show'); // Hiển thị modal
            })
            .catch(function (error) {
                console.error('Error fetching invoice details:', error);
                alert('Không thể lấy thông tin chi tiết của hóa đơn.');
            });
    };
    $scope.getTotalAmount = function(items) {
        let total = 0;
        if (items) {
            items.forEach(item => {
                total += Number(item.tongTien); // Cộng dồn tổng tiền
            });
        }
        return total; // Trả về tổng tiền
    };

    $scope.getHoaDonsByTrangThai(null, 0);
};
