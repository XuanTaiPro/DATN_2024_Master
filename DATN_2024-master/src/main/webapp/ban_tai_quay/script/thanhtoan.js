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


    $http.get('http://localhost:8080/khachhang').then(function (response) {
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
    };

    $scope.getVouchersByCustomer = function (id) {
        $http.get(`http://localhost:8080/voucher/VCkhachHang/${id}`)
            .then(function (response) {
                $scope.vouchers = response.data;
                console.log("Lấy danh sách voucher thành công", $scope.vouchers);
            })
            .catch(function (error) {
                console.error("Lỗi khi lấy voucher:", error);
            });
    };

    $http.get('http://localhost:8080/chitiethoadon/getCTHD?idHD=1C5331D3')
        .then(function (response) {
            $scope.listCTHD = response.data;
            // Gán dữ liệu trả về vào listCTHD
            let listData = response.data;
            let tt = 0
            listData.forEach(item => {
                tt += parseInt(item.tongTien)
            })
            $scope.tongTien = tt
        })
        .catch(function (error) {
            console.error("Lỗi:", error);
        });


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

        $scope.tongTien = totalAmount - discountAmount;
        alert("Đã áp dụng voucher, số tiền được giảm là: " + discountAmount + " VNĐ");

        // Lưu ID của voucher đã áp dụng để ngăn việc áp dụng lại
        $scope.appliedVoucherId = voucher.id;


        $http.post(`http://localhost:8080/voucher/apply`, {id: voucher.id})
            .then(function (response) {
                voucher.soLuong -= 1;
                if (voucher.soLuong == 0) {
                    voucher.trangThai = 0;
                }
                console.log("Cập nhật số lượng voucher thành công.");
                $scope.getVouchersByCustomer($scope.selectedCustomerId);
            })
            .catch(function (error) {
                console.error("Lỗi khi cập nhật số lượng voucher:", error);
                alert("Có lỗi xảy ra khi áp dụng voucher: " + (error.data || error.statusText));
            });
    };




}