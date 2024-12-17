window.voucherCtrl = function ($scope, $http,$timeout) {
    const url = "http://localhost:8083/voucher";

    $scope.listVoucher = [];
    $scope.listKhachHang = [];
    $scope.currentPage = 0;
    $scope.totalPages = 1;
    $scope.pageSize = 5;
    $scope.emptyMessage = "";

    $scope.isSubmitted = false;
    $scope.loadPage = function (page) {
        $http.get('http://localhost:8083/voucher/page?page=' + page)
            .then(function (response) {
                $scope.listVoucher = response.data.vouchers;
                $scope.currentPage = response.data.currentPage;
                $scope.totalPages = response.data.totalPages;

                // Kiểm tra nếu danh sách trống và hiển thị thông báo
                if ($scope.listVoucher.length === 0) {
                    $scope.emptyMessage = response.data.message || "Danh sách trống!";
                } else {
                    $scope.emptyMessage = ""; // Reset lại thông báo nếu có dữ liệu
                }
            })
            .catch(function (error) {
                console.error('Lỗi:', error);
            });
    };

    $scope.getSTT = function (index) {
        return index + 1 + ($scope.currentPage * $scope.pageSize);
    };

    $scope.range = function (n) {
        return new Array(n);
    };

    $scope.setPage = function (page) {
        if (page >= 0 && page < $scope.totalPages) {
            $scope.loadPage(page);
        }
    };

    $scope.prevPage = function () {
        if ($scope.currentPage > 0) {
            $scope.setPage($scope.currentPage - 1);
        }
    };

    $scope.nextPage = function () {
        if ($scope.currentPage < $scope.totalPages - 1) {
            $scope.setPage($scope.currentPage + 1);
        }
    };
    $scope.range = function (n) {
        var arr = [];
        for (var i = 0; i < n; i++) {
            arr.push(i);
        }
        return arr;
    };

    // Load initial page
    $scope.loadPage(0);

    $scope.listLVC = [];
    $http.get("http://localhost:8083/loaivoucher")
        .then(function (response) {
            $scope.listLVC = response.data;
            console.log("Lấy danh sách LVC thành công", $scope.listLVC);
        })
        .catch(function (error) {
            console.error("Lỗi khi lấy danh sách LVC:", error);
        });

    $http.get('http://localhost:8083/khachhang').then(function (response) {
        $scope.listKhachHang = response.data;
        console.log("Lấy dữ liệu thành công");
    }).catch((error) => {
        console.error('Lỗi:', error);
    });

    $scope.selectedCustomers = [];
    $scope.selectedCustomersUpdate = [];
    $scope.selectAll = false;
    $scope.selectAllUpdate = false;

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
// Hàm toggle cho từng khách hàng
    $scope.toggleCustomerSelection = function (khachHang) {
        if (khachHang.selected) {
            $scope.selectedCustomers.push(khachHang);
        } else {
            const index = $scope.selectedCustomers.indexOf(khachHang);
            if (index > -1) {
                $scope.selectedCustomers.splice(index, 1);
            }
        }
        // Cập nhật trạng thái của selectAll
        $scope.selectAll = $scope.listKhachHang.length > 0 && $scope.selectedCustomers.length === $scope.listKhachHang.length;
    };
    $scope.toggleCustomerSelectionUpdate = function (khachHang) {
        if (khachHang.selectedUpdate) {
            $scope.selectedCustomersUpdate.push(khachHang);
        } else {
            const index = $scope.selectedCustomersUpdate.indexOf(khachHang);
            if (index > -1) {
                $scope.selectedCustomersUpdate.splice(index, 1);
            }
        }
        // Cập nhật trạng thái của selectAll
        $scope.selectAllUpdate = $scope.listKhachHang.length > 0 && $scope.selectedCustomersUpdate.length === $scope.listKhachHang.length;
    };

// Hàm chọn tất cả hoặc bỏ chọn tất cả
    $scope.toggleAllCheckboxes = function () {
        $scope.selectAll = !$scope.selectAll; // Đảo ngược trạng thái của selectAll
        $scope.selectedCustomers = []; // Reset danh sách selectedCustomers
        angular.forEach($scope.listKhachHang, function (khachHang) {
            khachHang.selected = $scope.selectAll; // Đặt trạng thái selected cho từng khách hàng
            if ($scope.selectAll) {
                $scope.selectedCustomers.push(khachHang); // Nếu chọn tất cả, thêm vào danh sách
            }
        });
    };
    $scope.toggleAllCheckboxesUpdate = function () {
        $scope.selectAllUpdate = !$scope.selectAllUpdate; // Đảo ngược trạng thái của selectAll
        $scope.selectedCustomersUpdate = []; // Reset danh sách selectedCustomers
        angular.forEach($scope.listKhachHang, function (khachHang) {
            khachHang.selectedUpdate = $scope.selectAllUpdate; // Đặt trạng thái selected cho từng khách hàng
            if ($scope.selectAllUpdate) {
                $scope.selectedCustomersUpdate.push(khachHang); // Nếu chọn tất cả, thêm vào danh sách
            }
        });
    };

    $scope.openCustomerModal = function () {
        $('#customerModal').modal('show');
    };
    $scope.openCustomerModalUpdate = function () {
        $('#customerModalUpdate').modal('show');
    };

    $scope.confirmSelection = function () {
        $('#customerModal').modal('hide');
        $('#productModal').modal('show');
    };
    $scope.confirmSelectionUpdate = function () {
        $('#customerModalUpdate').modal('hide');
        $scope.selectedCustomersUpdate = $scope.listKhachHang.filter(kh => kh.selectedUpdate);
        $('#UpdateForm').modal('show');
    };


    $scope.toggleCustomerSelection = function (khachHang) {
        if (khachHang.selected) {
            $scope.selectedCustomers.push(khachHang);
        } else {
            const index = $scope.selectedCustomers.indexOf(khachHang);
            if (index > -1) {
                $scope.selectedCustomers.splice(index, 1);
            }
        }
    };
    $scope.toggleCustomerSelectionUpdate = function (khachHang) {
        if (khachHang.selectedUpdate) {
            if (!$scope.selectedCustomersUpdate.includes(khachHang)) {
                $scope.selectedCustomersUpdate.push(khachHang);
            }
        } else {
            const index = $scope.selectedCustomersUpdate.indexOf(khachHang);
            if (index > -1) {
                $scope.selectedCustomersUpdate.splice(index, 1);
            }
        }
    };


    $scope.viewDetail = function (voucher) {
        $scope.selectedVoucher = angular.copy(voucher);
        $scope.selectedVoucher.trangThai = voucher.trangThai == 1 ? 'Hoạt động' : 'Ngưng hoạt động';

        fetch('http://localhost:8083/voucher/' + voucher.id + '/customers')
            .then(response => response.json())
            .then(data => {
                $scope.$apply(() => {
                    $scope.selectedVoucherCustomers = data; // Gán danh sách khách hàng vào scope
                });
            })
            .catch(error => console.error("Error fetching customers:", error));
    }
    $scope.openUpdateModal = function (voucher) {
        $scope.selectedVoucher = angular.copy(voucher);

        $scope.selectedLVC = $scope.selectedVoucher.tenLoaiVC
        $scope.idLoaiVC = null
        console.log($scope.selectedLVC)
        fetch('http://localhost:8083/loaivoucher/getId?ten=' + $scope.selectedLVC).then(function (response) {
            return response.text()
        }).then(function (data) {
            $scope.$apply(function () {
                $scope.idLoaiVC = data;  // Gán giá trị trả về vào $scope.idQuyen
                console.log($scope.idLoaiVC);
            });
        }).catch(function (er) {
            console.error(er)
        })
        //lấy khách hàng theo id voucher liên kết với chiTietVoucher
        fetch('http://localhost:8083/voucher/' + voucher.id + '/customers')
            .then(response => response.json())
            .then(data => {
                $scope.$apply(() => {
                        $scope.selectedCustomersUpdate = data; // Gán danh sách khách hàng vào scope

                    if ($scope.selectedCustomersUpdate.length > 0) {
                        $scope.selectedCustomerUpdate = $scope.selectedCustomersUpdate[0].id;
                    }
                });
            })
            .catch(error => console.error("Error fetching customers:", error));

        $scope.selectedVoucher.trangThai = voucher.trangThai.toString();
        console.log("Trạng thái hiện tại:", $scope.selectedVoucher.trangThai);
    };


    $scope.validationErrors = {};

    $scope.validateDiscounts = function() {
        $scope.validationErrors = {};
        const giamGiaRegex = /^\d+%$/;
        if (!giamGiaRegex.test($scope.giamGia)) {
            $scope.validationErrors.giamGia = 'Giá giảm phải là số và kết thúc bằng "%", ví dụ: 10%.';
        }
        if (isNaN($scope.giamMin)) {
            $scope.validationErrors.giamMin = 'Giảm min phải là số.';
        }
        if (isNaN($scope.giamMax)) {
            $scope.validationErrors.giamMax = 'Giảm max phải là số.';
        }
        // const giamGia = parseFloat($scope.giamGia);
        const giamMin = parseFloat($scope.giamMin);
        const giamMax = parseFloat($scope.giamMax);
        // if (!isNaN(giamGia) && !isNaN(giamMin) && giamGia < giamMin) {
        //     $scope.validationErrors.giamGia = 'Giá giảm không được nhỏ hơn giá min.';
        // }
        // if (!isNaN(giamGia) && !isNaN(giamMax) && giamGia > giamMax) {
        //     $scope.validationErrors.giamGia = 'Giá giảm không được lớn hơn giá max.';
        // }
        if (!isNaN(giamMin) && !isNaN(giamMax) && giamMin >= giamMax) {
            $scope.validationErrors.giamMin = 'Giảm min phải nhỏ hơn giảm max.';
            $scope.validationErrors.giamMax = 'Giảm max phải lớn hơn giảm min.';
        }
        if ($scope.ngayKetThuc) {
            const today = new Date();
            const selectedDate = new Date($scope.ngayKetThuc);
            if (selectedDate <= today) {
                $scope.validationErrors.ngayKetThuc = 'Ngày kết thúc phải là ngày trong tương lai.';
            }
        }
        if ($scope.ten) {
            const isDuplicate = $scope.listVoucher.some(function(voucher) {
                return voucher.ten.toLowerCase() === $scope.ten.trim().toLowerCase();
            });
            if (isDuplicate) {
                $scope.validationErrors.ten = 'Tên voucher đã tồn tại. Vui lòng chọn tên khác.';
            }
        }


    };

    $scope.addVoucher = function () {
        $scope.isSubmitted = true;
        $scope.validateDiscounts();
        const newVoucher = {
            ten: $scope.ten,
            giamGia: $scope.giamGia,
            giamMin: $scope.giamMin,
            giamMax: $scope.giamMax,
            dieuKien: $scope.dieuKien,
            ngayKetThuc: $scope.ngayKetThuc,
            soLuong: $scope.soLuong,
            idLoaiVC: $scope.idLoaiVC,
            trangThai: $scope.trangThai,
            idKH: $scope.selectedCustomers.map(c => c.id)
        };

        console.log("Dữ liệu mới:", newVoucher);
        // Kiểm tra nếu form hợp lệ trước khi gửi request
        if ($scope.voucherForm.$valid) {
            $http.post('http://localhost:8083/voucher/add', newVoucher)
                .then(function (response) {
                    $('#productModal').modal('hide');
                    showSuccessAlert('Thêm thành công!');
                    $scope.loadPage($scope.currentPage);
                })
                .catch(function (error) {
                    $scope.errorMessage = "Thêm thất bại";
                });
        } else {
            $scope.errorMessage = "Vui lòng điền đầy đủ thông tin!";
        }

        resetForm();
    };



    $scope.validateDiscountsud = function() {
        $scope.validationErrors = {};
        const giamGiaRegex = /^\d+%$/;
        if (!giamGiaRegex.test($scope.giamGia)) {
            $scope.validationErrors.giamGia = 'Giá giảm phải là số và kết thúc bằng "%", ví dụ: 10%.';
        }
        if (isNaN($scope.giamMin)) {
            $scope.validationErrors.giamMin = 'Giảm min phải là số.';
        }
        if (isNaN($scope.giamMax)) {
            $scope.validationErrors.giamMax = 'Giảm max phải là số.';
        }
        const giamMin = parseFloat($scope.giamMin);
        const giamMax = parseFloat($scope.giamMax);
        if (!isNaN(giamMin) && !isNaN(giamMax) && giamMin >= giamMax) {
            $scope.validationErrors.giamMin = 'Giảm min phải nhỏ hơn giảm max.';
            $scope.validationErrors.giamMax = 'Giảm max phải lớn hơn giảm min.';
        }
        if ($scope.ngayKetThuc) {
            const today = new Date();
            const selectedDate = new Date($scope.ngayKetThuc);
            if (selectedDate <= today) {
                $scope.validationErrors.ngayKetThuc = 'Ngày kết thúc phải là ngày trong tương lai.';
            }
        }
        if ($scope.ten) {
            const isDuplicate = $scope.listVoucher.some(function(voucher) {
                // Kiểm tra trùng tên và loại trừ id đang chọn
                return voucher.ten.toLowerCase() === $scope.ten.trim().toLowerCase() &&
                    voucher.id !== $scope.currentVoucherId; // Loại trừ ID đang chọn
            });

            if (isDuplicate) {
                $scope.validationErrors.ten = 'Tên voucher đã tồn tại. Vui lòng chọn tên khác.';
            } else {
                delete $scope.validationErrors.ten; // Xóa lỗi nếu hợp lệ
            }
        }
        if (!$scope.selectedCustomersUpdate || $scope.selectedCustomersUpdate.length === 0) {
            $scope.validationErrors.customer = 'Cần thêm ít nhất một khách hàng.';
        }
    };
    $scope.updateVoucher = function () {
        $scope.isSubmitted = true;
        $scope.validateDiscountsud();
        if (!$scope.idLoaiVC) {
            alert("Vui lòng chọn quyền cho nhân viên.");
            return;
        }

        const updatedVoucher = {
            ...$scope.selectedVoucher,
            idLoaiVC: $scope.idLoaiVC,
            idKH: $scope.selectedCustomersUpdate.map(c => c.id)
        };

        console.log("Dữ liệu cập nhật:", updatedVoucher);

        $http.put('http://localhost:8083/voucher/update/' + $scope.selectedVoucher.id, updatedVoucher)
            .then(function (response) {
                $('#UpdateForm').modal('hide');

                // Hiển thị alert với hiệu ứng slide down
                const alertBox = document.getElementById('success-alert');
                alertBox.style.display = 'block'; // Hiện alert
                setTimeout(() => alertBox.classList.add('show'), 10); // Thêm hiệu ứng

                // Tự động ẩn alert sau 3 giây
                setTimeout(() => {
                    alertBox.classList.remove('show'); // Ẩn hiệu ứng
                    setTimeout(() => (alertBox.style.display = 'none'), 500); // Ẩn hoàn toàn
                }, 3000);

                // Reload danh sách
                $scope.loadPage($scope.currentPage);
            })
            .catch(function (error) {
                console.error("Lỗi khi cập nhật voucher:", error);
            });
    };



    $scope.deleteVoucher = function (id) {
        console.log("Xóa");
        if (confirm('Bạn có chắc chắn muốn xóa?')) {
            $http.delete('http://localhost:8083/voucher/delete/' + id)
                .then(function (response) {
                    // Kiểm tra phản hồi server
                    console.log(response.data);
                    $scope.loadPage($scope.currentPage)
                    const index = $scope.listVoucher.findIndex(vc => vc.id === id);
                    if (index !== -1) {
                        $scope.listVoucher.splice(index, 1);
                    }
                    alert(response.data.message || 'Xóa thành công!!');  // Sử dụng thông điệp từ server
                })
                .catch(function (error) {
                    console.error("Lỗi khi xóa :", error);
                    alert("Xóa thất bại. Vui lòng thử lại sau.");
                });
        }
    };

    $scope.searchParams = {
        ten: '',
        giamGia: '',
        trangThai: ''
    }
    $scope.searchAndFilter = function (page = 0) {
        const params = {
            ...$scope.searchParams,
            page: page,
            size: $scope.pageSize
        }
        $http.get(`http://localhost:8083/voucher/filter`, {params})
            .then(function (response) {
                $scope.listVoucher = response.data.vouchers
                $scope.currentPage = response.data.currentPage
                $scope.totalPages = response.data.totalPages

                if ($scope.listVoucher.length === 0) {
                    $scope.emptyMessage = response.data.message || "Không tìm thấy voucher!!"
                } else {
                    $scope.emptyMessage = ''
                }
            })
            .catch(function (error) {
              console.error("xảy ra lỗi :" + error )

            })
        }


        $scope.resetFilters = function (){
          $scope.searchParams = {
              ten :'',
              giamGia :'',
              trangThai :''
          }
          $scope.loadPage(0)
        }
    // Reset form
    function resetForm() {
        $scope.ten = "";
        $scope.giamGia = "";
        $scope.giamMin = "";
        $scope.giamMax = "";
        $scope.dieuKien = "";
        $scope.ngayKetThuc = "";
        $scope.soLuong = "";
        $scope.idLoaiVC = "";
        $scope.trangThai = "";
        $scope.selectedCustomerNames = ""; // Xóa tên khách hàng đã chọn
        $scope.selectedCustomers = [];
    }
};

