window.thanhtoanCtrl = function ($scope, $http) {

    $scope.listCTHD = [];
    $scope.listKhachHang = [];
    $scope.selectedCustomerName = "";
    $scope.selectedCustomerPhone = "";
    $scope.selectedCustomerId = "";
    $scope.amountPaid = 0;
    $scope.changeAmount = 0;
    $scope.showError = false;
    $scope.tongTien = 0;
    $scope.previousTotal = 0; // Thêm biến để lưu tổng tiền trước đó


    $http.get('http://localhost:8083/khachhang').then(function (response) {
        $scope.listKhachHang = response.data;
        console.log("Lấy dữ liệu thành công");
    }).catch((error) => {
        console.error('Lỗi:', error);
    });

    $scope.selectCustomer = function (ten, sdt, id) {
        $scope.selectedCustomerName = ten;
        $scope.selectedCustomerPhone = sdt;
        $scope.selectedCustomerId = id; // Lưu ID khách hàng

        // Gọi API để lấy voucher của khách hàng
        $scope.loadPageVC(0);

        // Nếu không có voucher đã áp dụng, thì cập nhật lại tổng tiền
        if (!$scope.appliedVoucherId) {
            $scope.previousTotal = $scope.tongTien; // Lưu tổng tiền hiện tại
            $scope.calculateTotalAmount(); // Cập nhật tổng tiền về như cũ
        } else {
            // Khôi phục tổng tiền đã giảm
            $scope.tongTien = $scope.previousTotal;
        }
    };

    $scope.currentPageVC = 0;
    $scope.pageSizeVC = 2;

    $scope.loadPageVC = function (page) {
        $scope.currentPageVC = page; // Cập nhật trang hiện tại trước khi gọi API
        if (!$scope.selectedCustomerId) {
            console.warn('Chưa chọn khách hàng');
            return;
        }
        $http.get(`http://localhost:8083/voucher/VCkhachHang/${$scope.selectedCustomerId}?page=${$scope.currentPageVC}&size=${$scope.pageSizeVC}`)
            .then(function (response) {
                // Cập nhật dữ liệu voucher và thông tin phân trang
                $scope.vouchers = response.data.vouchers;
                $scope.totalPagesVC = response.data.totalPagesVC;
                $scope.totalItemsVC = response.data.totalItemsVC;
            })
            .catch(function (error) {
                console.error("Lỗi khi lấy voucher:", error);
            });
    };

// Hàm để tạo dãy số trang cho phân trang
    $scope.range = function (totalPagesVC) {
        return Array.from({ length: totalPagesVC }, (_, i) => i);
    };

// Gọi lần đầu để tải trang đầu tiên (nếu có khách hàng được chọn)
    $scope.$watch('selectedCustomerId', function (newVal, oldVal) {
        if (newVal) {
            $scope.loadPageVC(0); // Tải trang đầu tiên khi khách hàng được chọn
        }
    });

// Hàm chuyển đến trang trước
    $scope.prevPageVC = function () {
        if ($scope.currentPageVC > 0) {
            $scope.loadPageVC($scope.currentPageVC - 1);
        }
    };

// Hàm chuyển đến trang tiếp theo
    $scope.nextPageVC = function () {
        if ($scope.currentPageVC < $scope.totalPagesVC - 1) {
            $scope.loadPageVC($scope.currentPageVC + 1);
        }
    };

// Hàm chuyển đến một trang cụ thể
    $scope.setPageVC = function (page) {
        $scope.loadPageVC(page);
    };


//phân trang cthd
    $scope.currentPage = 0;
    $scope.range = function (totalPages) {
        return Array.from({ length: totalPages }, (_, i) => i);
    };

    $scope.loadPage = function (page) {
        $http.get(`http://localhost:8083/chitiethoadon/getCTHD?idHD=A117B65E&page=0`)
            .then(function (response) {
                let data = response.data;
                $scope.listCTHD = response.data.cthds;
                $scope.totalPages = data.totalPages;
                $scope.totalElements = data.totalElements;
                $scope.currentPage = page;

                if ($scope.listCTHD.length === 0) {
                    $scope.emptyMessage = response.data.message || "Danh sách trống!";
                } else {
                    $scope.emptyMessage = ""; // Reset lại thông báo nếu có dữ liệu
                }

                // Tính tổng tiền
                let tt = 0;
                data.cthds.forEach(item => {
                    tt += parseInt(item.tongTien);
                });
                $scope.tongTien = tt;
            })
            .catch(function (error) {
                console.error("Lỗi:", error);
            });
    };

// Gọi lần đầu để tải trang đầu tiên
    $scope.loadPage($scope.currentPage);

// Chuyển đến trang trước
    $scope.prevPage = function () {
        if ($scope.currentPage > 0) {
            $scope.loadPage($scope.currentPage - 1);
        }
    };

// Chuyển đến trang tiếp theo
    $scope.nextPage = function () {
        if ($scope.currentPage < $scope.totalPages - 1) {
            $scope.loadPage($scope.currentPage + 1);
        }
    };

// Chuyển đến một trang cụ thể
    $scope.setPage = function (page) {
        $scope.loadPage(page);
    };



//cập nhật lại tổng tiền về ban đầu khi đổi khách hàng
    $scope.calculateTotalAmount = function () {
        let total = 0;
        $scope.listCTHD.forEach(item => {
            total += parseInt(item.tongTien);
        });
        $scope.tongTien = total;
    };

    $scope.calculateChange = function () {
        const totalAmount = $scope.tongTien; // Tổng tiền cần thanh toán
        const amountPaid = parseFloat($scope.amountPaid) || 0; // Lấy số tiền khách đưa

        $scope.changeAmount = amountPaid - totalAmount;
        if ($scope.changeAmount < 0) {
            $scope.showError = true;
        } else {
            $scope.showError = false;
        }
    };

    $scope.showConfirmation = function () {
        if ($scope.amountPaid < $scope.tongTien) {
            $scope.showError = true;
            alert("Số tiền cần thanh toán không đủ. Vui lòng kiểm tra lại.");
        } else {
            $scope.showError = false; // Ẩn thông báo lỗi
            new bootstrap.Modal(document.getElementById('confirmModal')).show();
        }
    };

// Hoàn tất thanh toán
    $scope.completePayment = function () {
        if ($scope.amountPaid < $scope.tongTien) {
            alert("Không thể hoàn tất thanh toán vì số tiền không đủ.");
            return;
        }
        console.log("Thanh toán hoàn tất cho khách hàng:", $scope.selectedCustomerName);
        alert("Thanh toán thành công!");
        new bootstrap.Modal(document.getElementById('confirmModal')).hide();
        location.reload();

    };
    $scope.applyVoucher = function (voucher) {
        // Kiểm tra trạng thái voucher
        if (voucher.soLuong <= 0 || voucher.trangThai === 0) {
            alert("Voucher này đã hết số lượng hoặc không còn hoạt động.");
            return;
        }

        // Kiểm tra nếu voucher đã được áp dụng trước đó
        if ($scope.appliedVoucherId && $scope.appliedVoucherId === voucher.id) {
            alert("Voucher này đã được áp dụng.");
            return;
        }

        // Kiểm tra tổng tiền và điều kiện áp dụng voucher
        let totalAmount = $scope.tongTien;
        let discountRate = parseFloat(voucher.giamGia.replace('%', '')) / 100;
        let minAmount = parseInt(voucher.giamMin);
        let maxDiscount = parseInt(voucher.giamMax);

        if (totalAmount < minAmount) {
            alert("Hóa đơn phải có giá trị ít nhất " + minAmount + " VNĐ để áp dụng voucher.");
            return;
        }

        let discountAmount = totalAmount * discountRate;
        if (discountAmount > maxDiscount) {
            discountAmount = maxDiscount;
        }

        $scope.previousTotal = totalAmount;
        $scope.tongTien = totalAmount - discountAmount;
        alert("Đã áp dụng voucher, số tiền được giảm là: " + discountAmount + " VNĐ");

        // Lưu ID của voucher đã áp dụng để ngăn việc áp dụng lại
        $scope.appliedVoucherId = voucher.id;


        // $http.post(`http://localhost:8080/voucher/apply`, {id: voucher.id})
        //     .then(function (response) {
        //         voucher.soLuong -= 1;
        //         if (voucher.soLuong == 0) {
        //             voucher.trangThai = 0;
        //         }
        //         console.log("Cập nhật số lượng voucher thành công.");
        //         $scope.getVouchersByCustomer($scope.selectedCustomerId);
        //     })
        //     .catch(function (error) {
        //         console.error("Lỗi khi cập nhật số lượng voucher:", error);
        //         alert("Có lỗi xảy ra khi áp dụng voucher: " + (error.data || error.statusText));
        //     });
    };

    $scope.completePayment = function () {
        if ($scope.amountPaid < $scope.tongTien) {
            alert("Không thể hoàn tất thanh toán vì số tiền không đủ.");
            return;
        }

        if ($scope.appliedVoucherId) {
            $http.post(`http://localhost:8083/voucher/apply`, { id: $scope.appliedVoucherId })
                .then(function (response) {
                    console.log("Voucher đã được trừ số lượng thực sự.");
                })
                .catch(function (error) {
                    console.log("Lỗi khi cập nhật số lượng voucher:", error);
                    alert("Có lỗi xảy ra khi áp dụng voucher: " + (error.data || error.statusText));
                });
        }
        alert("Thanh toán thành công!");
        new bootstrap.Modal(document.getElementById('confirmModal')).hide();
        location.reload();
    };
    //check ngày kết thúc voucher
    $scope.getDaysLeft = function (ngayKetThuc) {
        var today = new Date(); // Ngày hiện tại
        var endDate = new Date(ngayKetThuc); // Ngày kết thúc từ voucher

        // Tính toán số ngày còn lại
        var timeDiff = endDate - today;
        var daysLeft = Math.ceil(timeDiff / (1000 * 3600 * 24)); // Chuyển đổi từ milliseconds sang ngày

        return daysLeft;
    };


}