window.thanhtoanCtrl = function ($scope, $http, $routeParams) {
    $scope.idHD = $routeParams.idHD;

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

    const overlayLoad = document.querySelector('.overlay-load')
    const loader = document.querySelector('.loader')


    $scope.search = {
        ten: '',
        gioiTinh: '',
        sdt: ''
    }
    $scope.searchKH = function () {
        const params = {
            ten: $scope.search.ten || null,
            gioiTinh: $scope.search.gioiTinh || null,
            sdt: $scope.search.sdt || null
        }
        $http.get('http://localhost:8083/khachhang/search', {params})
            .then(function (response) {
                $scope.listKhachHang = response.data
                if ($scope.listKhachHang == null) {
                    $scope.emptyMessage = response.data.message || "Danh sách trống"
                } else {
                    $scope.emptyMessage = ""
                }
            })
            .catch(function (error) {
                console.log("lỗi khi tìm kiếm" + error)
            })
    }
    $http.get('http://localhost:8083/khachhang').then(function (response) {
        $scope.listKhachHang = response.data;
        console.log("Lấy dữ liệu thành công");
        $scope.loadPage(0)
    }).catch((error) => {
        console.error('Lỗi:', error);
    });

    $scope.khachVangLai = function () {
        $scope.selectedCustomerName = "Không liên hệ";
        $scope.selectedCustomerPhone = "0123456789";
        $scope.selectedCustomerId = "";
        $scope.selectedCustomerEmail = "";

        // Xóa danh sách voucher
        $scope.vouchers = [];
        $scope.appliedVoucherId = null;
        $scope.appliedVoucher = null;

        // Cập nhật lại tổng tiền nếu cần thiết
        if ($scope.previousTotal) {
            $scope.tongTien = $scope.previousTotal; // Khôi phục tổng tiền trước đó
        }
    };


    $scope.selectCustomer = function (ten, sdt, id, email) {
        $scope.selectedCustomerName = ten;
        $scope.selectedCustomerPhone = sdt;
        $scope.selectedCustomerId = id; // Lưu ID khách hàng
        $scope.selectedCustomerEmail = email;

        // Khôi phục tổng tiền về giá trị gốc
        if ($scope.previousTotal) {
            $scope.tongTien = $scope.previousTotal; // Đặt lại tổng tiền chưa giảm
        }

        // Xóa trạng thái voucher cũ
        $scope.appliedVoucher = null;
        $scope.appliedVoucherId = null;
        $scope.suggestedVoucher = null;

        // Gọi API để lấy voucher của khách hàng
        $scope.loadPageVC(0);
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

                if ($scope.appliedVoucherId == null) {
                    $scope.showSuggestedVoucher();
                }
            })
            .catch(function (error) {
                console.error("Lỗi khi lấy voucher:", error);
            });
    };

// Hàm để tạo dãy số trang cho phân trang
    $scope.range = function (totalPagesVC) {
        return Array.from({length: totalPagesVC}, (_, i) => i);
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

    //tìm voucher tốt nhất
    $scope.getBestVoucher = function (vouchers) {
        if (!vouchers || vouchers.length === 0) {
            return null;
        }
        let totalAmount = $scope.tongTien;
        let bestVoucher = null;
        let maxDiscountAmount = 0;

        vouchers.forEach(voucher => {
            if (voucher.trangThai === 1 && voucher.soLuong > 0) { // Voucher hợp lệ
                let discountRate = parseFloat(voucher.giamGia.replace('%', '')) / 100;
                let minAmount = parseInt(voucher.giamMin);
                let maxDiscount = parseInt(voucher.giamMax);

                if (totalAmount >= minAmount) { // Tổng tiền đủ điều kiện để áp dụng voucher
                    let discountAmount = totalAmount * discountRate;
                    if (discountAmount > maxDiscount) {
                        discountAmount = maxDiscount; // Áp dụng giảm giá tối đa
                    }

                    if (discountAmount > maxDiscountAmount) { // Cập nhật voucher tốt nhất
                        maxDiscountAmount = discountAmount;
                        bestVoucher = voucher;
                    }
                }
            }
        });
        return bestVoucher;
    }

    $scope.showSuggestedVoucher = function () {//show form gợi ý voucher
        if (!$scope.vouchers || $scope.vouchers.length === 0) {
            $scope.suggestedVoucher = null; // Không có voucher nào
        } else {
            $scope.suggestedVoucher = $scope.getBestVoucher($scope.vouchers);
        }
        const modalElement = document.getElementById('suggestedVoucherModal');
        const modalInstance = new bootstrap.Modal(modalElement);
        modalInstance.show();
    };

    $scope.applySuggestedVoucher = function () {// áp dụng voucher gợi ý
        if ($scope.suggestedVoucher) {
            $scope.applyVoucher($scope.suggestedVoucher);
        }
        const modalElement = document.getElementById('suggestedVoucherModal');
        modalElement.style.display = 'none'
        const overlay = document.getElementsByClassName('modal-backdrop')
        Array.from(overlay).forEach(item => item.style.display = 'none')
    };


//phân trang cthd
    $scope.currentPage = 0;
    $scope.range = function (totalPages) {
        return Array.from({length: totalPages}, (_, i) => i);
    };

    $scope.loadPage = function (page) {
        $http.get(`http://localhost:8083/chitiethoadon/getCTHD?idHD=${$scope.idHD}&page=0`)
            .then(function (response) {
                let data = response.data;
                $scope.listCTHD = response.data.cthds;
                $scope.totalPages = data.totalPages;
                $scope.totalElements = data.totalElements;
                $scope.currentPage = page;
                console.log(response.data);
                console.log($scope.idHD);
                if ($scope.listCTHD.length === 0) {
                    $scope.emptyMessage = response.data.message || "Danh sách trống!";
                } else {
                    $scope.emptyMessage = ""; // Reset lại thông báo nếu có dữ liệu
                }

                // Tính tổng tiền
                let tt = 0;
                data.cthds.forEach(item => {
                    tt += parseInt(item.soLuong * item.giaSauGiam);
                });
                $scope.tongTien = tt;
            })
            .catch(function (error) {
                console.error("Lỗi:", error);
            });
    }

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
            total += parseInt(item.soLuong * item.giaSauGiam);
        });
        $scope.tongTien = total;
    };

    $scope.fillTongTien = function () {
        $scope.amountPaid = $scope.tongTien
        $scope.calculateChange()
    }
    $scope.selectPaymentMethod = function (method) {
        $scope.paymentMethod = method;
        if (method === 'cash') {
            $scope.fillTongTien();
        }
    };
    $scope.calculateChange = function () {
        const totalAmount = $scope.tongTien; // Tổng tiền cần thanh toán
        const amountPattern = /^[0-9]+$/; // Chỉ cho phép số nguyên dương

        // Kiểm tra tính hợp lệ
        if (!amountPattern.test($scope.amountPaid)) {
            $scope.changeAmount = ""; // Không hiển thị tiền thừa
            $scope.showError = false; // Không hiển thị lỗi tiền không đủ
            $scope.invalidAmount = true; // Hiển thị lỗi số tiền không hợp lệ
            return;
        }

        $scope.invalidAmount = false; // Ẩn lỗi số tiền không hợp lệ nếu hợp lệ
        const amountPaid = parseInt($scope.amountPaid, 10); // Chuyển chuỗi thành số nguyên

        // Kiểm tra tiền không đủ
        if (amountPaid < totalAmount) {
            $scope.changeAmount = ""; // Không hiển thị tiền thừa
            $scope.showError = true; // Hiển thị lỗi số tiền không đủ
        } else {
            $scope.changeAmount = (amountPaid - totalAmount).toLocaleString(); // Tính tiền thừa
            $scope.showError = false; // Ẩn lỗi số tiền không đủ
        }
    };


    $scope.isSubmitted = false;

    $scope.showConfirmation = function () {
        // Đánh dấu form đã submit
        $scope.isSubmitted = true;

        // Kiểm tra nếu tên hoặc số điện thoại bị trống
        if (!$scope.selectedCustomerName || !$scope.selectedCustomerPhone) {
            showWarningAlert("Vui lòng điền đầy đủ Tên khách hàng và Số điện thoại.")
            return; // Chặn không cho chuyển modal
        }

        const amountPattern = /^[0-9]+$/;
        if (!amountPattern.test($scope.amountPaid)) {
            $scope.invalidAmount = true; // Hiển thị lỗi số tiền không hợp lệ
            showWarningAlert("Số tiền phải là số nguyên dương và không chứa ký tự đặc biệt.")
            return;
        } else {
            $scope.invalidAmount = false;
        }

        // Kiểm tra số tiền có đủ để thanh toán không
        if ($scope.amountPaid < $scope.tongTien) {
            $scope.showError = true; // Hiển thị lỗi tiền không đủ
            showWarningAlert("Số tiền cần thanh toán không đủ. Vui lòng kiểm tra lại.")
            return; // Chặn không cho chuyển modal
        } else {
            $scope.showError = false; // Ẩn lỗi tiền không đủ
        }

        $('#cashModal').modal('hide');
        const confirmModal = new bootstrap.Modal(document.getElementById('confirmModal'));
        confirmModal.show();
    };


    // // gừi hóa đơn qua mail
    // $scope.generateAndSendInvoice = function () {
    //
    //     if (!$scope.selectedCustomerEmail || $scope.selectedCustomerEmail === '') {
    //         showDangerAlert("Khách hàng vãng lai không có Email để gửi hóa đơn")
    //         return
    //     }
    //     const invoiceData = {
    //         idHD: $scope.idHD,
    //         customerName: $scope.selectedCustomerName,
    //         amountPaid: $scope.amountPaid,
    //         totalAmount: $scope.tongTien,
    //         discountAmount: $scope.discountAmount,
    //         email: $scope.selectedCustomerEmail // Gán email khách hàng
    //     };
    //
    //     overlayLoad.style.display = 'block';
    //     loader.style.display = 'block';
    //
    //     $('.modal-content').modal('hide')
    //     fetch('http://localhost:8083/hoadon/send-invoice', {
    //         method: 'POST',
    //         headers: {
    //             'Content-Type': 'application/json'
    //         },
    //         body: JSON.stringify(invoiceData)
    //
    //     })
    //         .then(response => {
    //             if (response.ok) {
    //                 overlayLoad.style.display = 'none';
    //                 loader.style.display = 'none';
    //                 // $scope.completePayment();
    //                 // showSuccessAlert("Hóa đơn đã được gửi đến mail của khách hàng")
    //                 Swal.fire({
    //                     icon: 'success',
    //                     title: "Hóa đơn đã được gửi đến mail của khách hàng",
    //                     text: '',
    //                     showConfirmButton: false,
    //                     timer: 2000
    //                 });
    //             } else {
    //                 showDangerAlert("Mail khách hàng không tồn tại!!")
    //             }
    //         })
    //         .catch(error => {
    //             console.error("Lỗi khi gửi hóa đơn:", error);
    //             alert("Đã xảy ra lỗi.");
    //         });
    //
    // };

    $scope.generateAndSendInvoice = function () {
        if (!$scope.selectedCustomerEmail || $scope.selectedCustomerEmail === '') {
            showDangerAlert("Khách hàng vãng lai không có Email để gửi hóa đơn");
            return;
        }

        const invoiceData = {
            idHD: $scope.idHD,
            customerName: $scope.selectedCustomerName,
            amountPaid: $scope.amountPaid,
            totalAmount: $scope.tongTien,
            discountAmount: $scope.discountAmount,
            email: $scope.selectedCustomerEmail
        };

        // Hiển thị loading
        overlayLoad.style.display = 'block';
        loader.style.display = 'block';

        // Đóng modal
        const modalElement = document.getElementById('confirmModal');
        modalElement.style.display = 'none'

        fetch('http://localhost:8083/hoadon/send-invoice', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(invoiceData)
        })
            .then(response => {
                if (response.ok) {
                    overlayLoad.style.display = 'none';
                    loader.style.display = 'none';
                    $scope.completePayment();
                    showSuccessAlert("Hóa đơn đã được gửi đến mail của khách hàng")
                    Swal.fire({
                        icon: 'success',
                        title: "Hóa đơn đã được gửi đến mail của khách hàng",
                        text: '',
                        showConfirmButton: false,
                        timer: 2000
                    });
                } else {
                    showDangerAlert("Mail khách hàng không tồn tại!!");
                }
            })
            .catch(error => {
                console.error("Lỗi khi gửi hóa đơn:", error);
                alert("Đã xảy ra lỗi.");
            })
            .finally(() => {
                overlayLoad.style.display = 'none';
                loader.style.display = 'none';
            });
    };

    // Hoàn tất thanh toán chuyển khoản
    $scope.completePaymentCK = function () {
        $scope.isSubmitted = true;
        // Kiểm tra nếu tên hoặc số điện thoại bị trống
        if (!$scope.selectedCustomerName || !$scope.selectedCustomerPhone) {
            showDangerAlert("Vui lòng điền đầy đủ Tên khách hàng và Số điện thoại.")
            return; // Chặn không cho chuyển modal
        }
        console.log("Thanh toán hoàn tất cho khách hàng:", $scope.selectedCustomerName);
        showSuccessAlert("Thanh toán thành công!")
        new bootstrap.Modal(document.getElementById('confirmModal')).hide();
        location.reload();

    };
    $scope.cancelVoucher = function () {
        if ($scope.appliedVoucher) {
            $scope.tongTien = $scope.previousTotal; // Khôi phục tổng tiền gốc
            $scope.appliedVoucher = null;
            $scope.appliedVoucherId = null; // Đặt lại ID voucher về null để có thể áp dụng voucher khác
            showDangerAlert("Đã hủy áp dụng voucher này , Bạn có thể chọn voucher khác")
        }
    };

    $scope.applyVoucher = function (voucher) {


        // Kiểm tra trạng thái voucher
        if (voucher.soLuong <= 0 || voucher.trangThai === 0) {
            showDangerAlert("Voucher này đã hết số lượng hoặc không còn hoạt động.")
            return;
        }

        // Kiểm tra nếu voucher đã được áp dụng trước đó
        if ($scope.appliedVoucherId && $scope.appliedVoucherId === voucher.id) {

            const alertBox = document.getElementById('success-alert2');
            alertBox.innerHTML =
                `<strong>Mỗi hóa đơn chỉ được áp dụng duy nhất 1 voucher!</strong>
            <p>Bạn có thể thử sử dụng một voucher khác.</p>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>`;

            alertBox.style.display = 'block'; // Hiện alert
            setTimeout(() => alertBox.classList.add('show'), 10); // Thêm hiệu ứng

            setTimeout(() => {
                alertBox.classList.remove('show'); // Ẩn hiệu ứng
                setTimeout(() => (alertBox.style.display = 'none'), 500); // Ẩn hoàn toàn
            }, 3000);

            return;
        }

        // Kiểm tra tổng tiền và điều kiện áp dụng voucher
        let totalAmount = $scope.tongTien;
        let discountRate = parseFloat(voucher.giamGia.replace('%', '')) / 100;
        let minAmount = parseInt(voucher.giamMin);
        let maxDiscount = parseInt(voucher.giamMax);

        if (totalAmount < minAmount) {
            showWarningAlert("Hóa đơn phải có giá trị ít nhất " + minAmount + " VNĐ để áp dụng voucher.");
            return;
        }

        let discountAmount = totalAmount * discountRate;
        if (discountAmount > maxDiscount) {
            discountAmount = maxDiscount;
        }

        if ($scope.appliedVoucherId == null || $scope.appliedVoucherId == '') {
            if (voucher) {
                $scope.appliedVoucher = voucher; // Lưu voucher đang áp dụng
                $scope.previousTotal = $scope.tongTien; // Lưu tổng tiền trước khi giảm
                const discount = Math.min(voucher.giamGia, voucher.giamMax);
                $scope.tongTien -= discount; // Áp dụng giảm giá
            }

            $scope.previousTotal = totalAmount;
            $scope.tongTien = totalAmount - discountAmount;
            sessionStorage.setItem('tongTienDaGiam',$scope.tongTien) // lưu lại tổng tiền đã giảm cho hóa đơn
            const alertBox = document.getElementById('success-alert1');
            alertBox.innerHTML = `
        <strong>Áp dụng thành công voucher!</strong>
        <p>Giảm giá: <strong>${discountAmount.toLocaleString()} VNĐ</strong></p>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
             `;
            alertBox.style.display = 'block'; // Hiện alert
            setTimeout(() => alertBox.classList.add('show'), 10); // Thêm hiệu ứng

            setTimeout(() => {
                alertBox.classList.remove('show'); // Ẩn hiệu ứng
                setTimeout(() => (alertBox.style.display = 'none'), 500); // Ẩn hoàn toàn
            }, 3000);
        }


        // Lưu ID của voucher đã áp dụng để ngăn việc áp dụng lại
        $scope.appliedVoucherId = voucher.id;

    };

    $scope.completePayment = function () {
        if ($scope.amountPaid < $scope.tongTien) {
            showDangerAlert("Không thể hoàn tất thanh toán vì số tiền không đủ.");
            return;
        }

        let hoaDonData = {
            idKH: $scope.selectedCustomerId || null,  // Đặt null nếu không chọn khách hàng
            tenNguoiNhan: $scope.selectedCustomerId ? null : $scope.selectedCustomerName,
            sdtNguoiNhan: $scope.selectedCustomerId ? null : $scope.selectedCustomerPhone,
            tongTien: $scope.tongTien,
            maVoucher: $scope.appliedVoucherId || null
        };
        $http.put(`http://localhost:8083/hoadon/update/${$scope.idHD}`, hoaDonData)
            .then(function (response) {
                showSuccessAlert("Thanh toán thành công!");
                if ($scope.appliedVoucherId) {
                    $http.post(`http://localhost:8083/voucher/apply`, { id: $scope.appliedVoucherId })
                        .then(function (response) {
                            console.log(response.data.message); // Hiển thị thông báo từ phản hồi JSON
                        })
                        .catch(function (error) {
                            if (error.data && error.data.message) {
                                console.error("Lỗi khi áp dụng voucher:", error.data.message);
                            } else {
                                console.error("Lỗi không xác định khi áp dụng voucher:", error);
                            }
                        });
                }
                showSuccessAlert("Thanh toán thành công cho khác hàng : " + $scope.selectedCustomerName)
                new bootstrap.Modal(document.getElementById('confirmModal')).hide();
                setTimeout(() => {
                    window.location.href = 'http://localhost:63342/demo/src/main/webapp/ban_tai_quay/layout.html#!/banhang';
                }, 500);
            })
            .catch(function (error) {
                console.error("Lỗi khi cập nhật hóa đơn:", error);
                alert("Có lỗi xảy ra khi hoàn tất thanh toán.");
            });
    };
// add nhanh khách hàng
    $scope.newKH = {
        ten: '',
        email: '',
        sdt: '',
        gioiTinh: ''
    }
    $scope.addCustomer = function () {
        if ($scope.newKH.ten && $scope.newKH.email && $scope.newKH.sdt && $scope.newKH.gioiTinh) {
            $http({
                method: 'POST',
                url: 'http://localhost:8083/khachhang/add',
                data: $scope.newKH
            })
                .then(function (response) {

                    $scope.selectCustomer(response.data.ten, response.data.sdt)
                    showSuccessAlert("Thêm mới thành công khách hàng")
                    $('#addCustomerModal').modal('hide')
                    $scope.newKH = {}
                }),
                (function (error) {
                    showDangerAlert("khách hàng không thể thêm mới")
                })
        } else {
            showDangerAlert("Vui lòng điền đủ thông tin khách hàng")
        }
    }


    //check ngày kết thúc voucher
    $scope.getDaysLeft = function (ngayKetThuc) {
        var today = new Date(); // Ngày hiện tại
        var endDate = new Date(ngayKetThuc); // Ngày kết thúc từ voucher

        // Tính toán số ngày còn lại
        var timeDiff = endDate - today;
        var daysLeft = Math.ceil(timeDiff / (1000 * 3600 * 24)); // Chuyển đổi từ milliseconds sang ngày

        return daysLeft;
    };

    $scope.closeVCTU = function () {
        $('#suggestedVoucherModal').modal('hide');
    }
}