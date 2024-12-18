window.thongtingiaohangCtrl = function ($scope, $http) {
    const url = "http://localhost:8083/thongtingiaohang";

    $scope.listTtgh = [];
    $scope.currentPage = 0;
    $scope.totalPages = 1;
    $scope.pageSize = 5;
    $scope.emptyMessage = "";

    $scope.loadPage = function (page) {
        $http.get('http://localhost:8083/thongtingiaohang/page?page=' + page)
            .then(function (response) {
                $scope.listTtgh = response.data.thongTinGiaoHangs;
                $scope.currentPage = response.data.currentPage;
                $scope.totalPages = response.data.totalPages;

                // Kiểm tra nếu danh sách trống và hiển thị thông báo
                if ($scope.listTtgh.length === 0) {
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

    $scope.listKH = [];
    $http.get("http://localhost:8083/khachhang")
        .then(function (response) {
            $scope.listKH = response.data;
            console.log("Lấy danh sách khách hàng thành công", $scope.listKH);
        })
        .catch(function (error) {
            console.error("Lỗi khi lấy danh sách LVC:", error);
        });


    $scope.viewDetail = function (ttgh) {
        $scope.selectedTtgh = angular.copy(ttgh);
        $scope.selectedTtgh.trangThai = ttgh.trangThai == 1 ? 'Hoạt động' : 'Ngưng hoạt động';
    }

    $scope.openUpdateModal = function (thongTinGiaoHang) {
        $scope.selectedTtgh = angular.copy(thongTinGiaoHang);
        $scope.selectedKhachHang = $scope.selectedTtgh.tenKH;
        $scope.idKH = null;
        console.log($scope.selectedKhachHang);

        fetch('http://localhost:8083/khachhang/getId?ten=' + $scope.selectedKhachHang)
            .then(function (response) {
                return response.json();  // Chuyển đổi sang JSON vì API trả về danh sách
            })
            .then(function (data) {
                if (data.length > 0) {
                    $scope.$apply(function () {
                        $scope.idKH = data[0];  // Chọn ID đầu tiên trong danh sách
                        console.log($scope.idKH);
                    });
                } else {
                    console.warn("Không tìm thấy khách hàng nào với tên:", $scope.selectedKhachHang);
                }
            })
            .catch(function (error) {
                console.error("Lỗi khi gọi API:", error);
            });

        $scope.selectedTtgh.trangThai = thongTinGiaoHang.trangThai.toString();
        console.log("Trạng thái hiện tại:", $scope.selectedTtgh.trangThai);
    };

    $scope.validationErrors = {};
    $scope.validateDiscounts = function() {
        $scope.validationErrors = {}; // Xóa lỗi cũ

        // Kiểm tra số điện thoại
        if (isNaN($scope.sdtNguoiNhan)) {
            $scope.validationErrors.sdtNguoiNhan = 'Số điện thoại phải là số.';
        } else if (!/^\d{10}$/.test($scope.sdtNguoiNhan)) {
            $scope.validationErrors.sdtNguoiNhan = 'Số điện thoại phải có 10 chữ số và bắt đầu bằng số 0.';
        } else if ($scope.sdtNguoiNhan[0] !== '0') {
            $scope.validationErrors.sdtNguoiNhan = 'Số điện thoại phải bắt đầu bằng số 0.';
        }

        // Kiểm tra các trường khác
        if (!$scope.tenNguoiNhan) {
            $scope.validationErrors.tenNguoiNhan = 'Tên người nhận không được để trống.';
        }

        if (!$scope.dcNguoiNhan) {
            $scope.validationErrors.dcNguoiNhan = 'Địa chỉ người nhận không được để trống.';
        }

        if (!$scope.trangThai) {
            $scope.validationErrors.trangThai = 'Trạng thái không được để trống.';
        }

        if (!$scope.idKH) {
            $scope.validationErrors.idKH = 'Cần phải chọn khách hàng.';
        }
    };

    $scope.addTtgh = function () {

        $scope.isSubmitted = true;
        $scope.validateDiscounts();


        if (Object.keys($scope.validationErrors).length > 0) {
            return; // Dừng lại nếu có lỗi
        }

        const newTtgh = {
            sdtNguoiNhan: $scope.sdtNguoiNhan,
            tenNguoiNhan: $scope.tenNguoiNhan,
            dcNguoiNhan: $scope.dcNguoiNhan,
            trangThai: $scope.trangThai,
            idKH: $scope.idKH,
        };
        console.log("Dữ liệu thông tin giao hàng mới:", newTtgh);

        $http.post('http://localhost:8083/thongtingiaohang/add', newTtgh)
            .then(function (response) {
                $('#productModal').modal('hide');
            })
            .catch(function (error) {
                // Thông báo thất bại
                $scope.errorMessage = "Thêm thất bại. Vui lòng kiểm tra lại!";
                console.error("Lỗi khi thêm Thông tin:", error);
            });

    };



    $scope.updateTtgh = function () {
        if (!$scope.idKH) {
            alert("Vui lòng chọn khách hàng");
            return;
        }
        $scope.selectedTtgh.idKH = $scope.idKH;
        $http.put('http://localhost:8083/thongtingiaohang/update/' + $scope.selectedTtgh.id, $scope.selectedTtgh)
            .then(function (response) {
                location.reload()
            })
            .catch(function () {
            });
    };

    $scope.delete = function (id) {
        console.log("Xóa");
        if (confirm('Bạn có chắc chắn muốn xóa ?')) {
            $http.delete('http://localhost:8083/thongtingiaohang/delete/' + id)
                .then(function (response) {
                    console.log(response.data);
                    location.reload()
                    const index = $scope.listTtgh.findIndex(ttgh => ttgh.id === id);
                    if (index !== -1) {
                        $scope.listTtgh.splice(index, 1);
                    }
                    alert(response.data.message || 'Xóa thành công!!');  // Sử dụng thông điệp từ server
                })
                .catch(function (error) {
                    console.error("Lỗi khi xóa :", error);
                    alert("Xóa thất bại. Vui lòng thử lại sau.");
                });
        }
    };

    function resetForm() {
        $scope.sdtNguoiNhan = "";
        $scope.tenNguoiNhan = "";
        $scope.dcNguoiNhan = "";
        $scope.idKH = "";
        $scope.trangThai = "";
    }

};

