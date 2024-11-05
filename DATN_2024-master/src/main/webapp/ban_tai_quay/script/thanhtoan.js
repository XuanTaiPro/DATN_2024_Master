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
        $scope.getVouchersByCustomer(id);

        // Nếu không có voucher đã áp dụng, thì cập nhật lại tổng tiền
        if (!$scope.appliedVoucherId) {
            $scope.previousTotal = $scope.tongTien; // Lưu tổng tiền hiện tại
            $scope.calculateTotalAmount(); // Cập nhật tổng tiền về như cũ
        } else {
            // Khôi phục tổng tiền đã giảm
            $scope.tongTien = $scope.previousTotal;
        }
    };

    $scope.getVouchersByCustomer = function (id) {
        $http.get(`http://localhost:8083/voucher/VCkhachHang/${id}`)
            .then(function (response) {
                $scope.vouchers = response.data;
                console.log("Lấy danh sách voucher thành công", $scope.vouchers);
            })
            .catch(function (error) {
                console.error("Lỗi khi lấy voucher:", error);
            });
    };

    $http.get('http://localhost:8083/chitiethoadon/getCTHD?idHD=1C5331D3')
        .then(function (response) {
            $scope.listCTHD = response.data;
            // Gán dữ liệu trả về vào listCTHD
            let listData = response.data;
            let tt = 0
            listData.forEach(item => {
                tt += parseInt(item.tongTien)
            })
            $scope.tongTien = tt
            $scope.calculateTotalAmount();//cập nhật tổng tiền về như cũ
        })
        .catch(function (error) {
            console.error("Lỗi:", error);
        });

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
}