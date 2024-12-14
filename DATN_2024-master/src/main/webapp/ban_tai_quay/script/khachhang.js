window.khachhangCtrl = function ($scope, $http) {
    const url = "http://localhost:8083/khachhang";

    $scope.listKhachHang = [];
    $scope.currentPage = 0;
    $scope.totalPages = 1;
    $scope.pageSize = 3;
    $scope.emptyMessage = "";

    $scope.search = {
        ten: '',
        gioiTinh: '',
        diaChi: '',
        trangThai: ''
    }
    $scope.searchAndFilter = function (page = 0) {
        const params = {
            ...$scope.search,
            page: page,
            size: $scope.pageSize
        };

        $http.get('http://localhost:8083/khachhang/search-filter', {params})
            .then(function (response) {
                $scope.listKhachHang = response.data.khachHangs;
                $scope.currentPage = response.data.currentPage;
                $scope.totalPages = response.data.totalPages;

                if ($scope.listKhachHang.length === 0) {
                    $scope.emptyMessage = response.data.message || "không tìm thấy khách hàng với tiêu chí lọc";
                } else {
                    $scope.emptyMessage = "";
                }
            }).catch(function (error) {
            console.log("Lỗi khi tìm kiếm " + error)
            alert(("Xảy ra lỗi khi tìm kiếm!!"))
        });
    };

    $scope.resetFilters = function () {
        $scope.search = {
            ten: '',
            gioiTinh: '',
            diaChi: '',
            trangThai: ''
        }
        $scope.loadPage(0);
    }

    $scope.loadPage = function (page) {
        $http.get('http://localhost:8083/khachhang/page?page=' + page)
            .then(function (response) {
                $scope.listKhachHang = response.data.khachHangs;
                $scope.currentPage = response.data.currentPage;
                $scope.totalPages = response.data.totalPages;

                // Kiểm tra nếu danh sách trống và hiển thị thông báo
                if ($scope.listKhachHang.length === 0) {
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
            if ($scope.search.ten || $scope.search.gioiTinh || $scope.search.diaChi || $scope.search.trangThai) {
                $scope.seacrhKH(page);
            } else {
                $scope.loadPage(page);
            }
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


    // $http.get('http://localhost:8080/khachhang').then(function (response) {
    //     $scope.listKhachHang = response.data;
    //     console.log("Lấy dữ liệu thành công");
    // }).catch((error) => {
    //     console.error('Lỗi:', error);
    // });

    $scope.viewDetail = function (khachHang) {
        $scope.selectedKhachHang = angular.copy(khachHang);
        $scope.selectedKhachHang.trangThai = khachHang.trangThai == 1 ? 'Hoạt động' : 'Ngưng hoạt động';
    }
    $scope.openUpdateModal = function (khachHang) {
        $scope.selectedKhachHang = angular.copy(khachHang);
        $scope.selectedKhachHang.trangThai = khachHang.trangThai.toString();
        console.log("Trạng thái hiện tại:", $scope.selectedKhachHang.trangThai);// Sao chép dữ liệu nhân viên cần cập nhật
    };


    $scope.addKhachHang = function () {
        if (!$scope.gioiTinh) {
            $scope.gioiTinh = "Nam";
        }
        const newKhachHang = {
            ten: $scope.ten,
            email: $scope.email,
            passw: $scope.passw,
            gioiTinh: $scope.gioiTinh,
            sdt: $scope.sdt,
            diaChi: $scope.diaChi,
            trangThai: $scope.trangThai

        };
        console.log("Dữ liệu nhân viên mới:", newKhachHang);
        $http.post('http://localhost:8083/khachhang/add', newKhachHang)
            .then(function (response) {
                $scope.listKhachHang.push(response.data);
                // Đóng modal
                $('#productModal').modal('hide');
                showSuccessAlert('Thêm thành công!');
                $scope.loadPage($scope.currentPage);
            })
            .catch(function (error) {
                $scope.errorMessage = "Thêm thất bại";
            });
        resetForm();
    };

    $scope.updateKhachHang = function () {
        console.log("Cập nhật Khách Hàng:", $scope.selectedKhachHang);  // Kiểm tra dữ liệu trước khi gửi
        $http.put('http://localhost:8083/khachhang/update/' + $scope.selectedKhachHang.id, $scope.selectedKhachHang)
            .then(function (response) {
                $('#UpdateForm').modal('hide');
                showSuccessAlert('Update thành công!');
                $scope.loadPage($scope.currentPage);
            })
            .catch(function (error) {

            });
    };

    $scope.delete = function (id) {
        console.log("Xóa");
        // showConfirm('Bạn có chắc chắn muốn xóa khách hàng này?', () => {
            $http.delete('http://localhost:8083/khachhang/delete/' + id)
                .then(function (response) {
                   showWarningAlert("Đã cho tạm ngưng hoạt động khách hàng này !!")
                    $scope.loadPage($scope.currentPage);
                })
                .catch(function (error) {
                    console.error("Lỗi khi xóa :", error);
                   showDangerAlert("Xóa thất bại , vui lòng thử lại sau!!")
                });
        // })
    };

    $scope.deleteback = function (id) {
        console.log("Xóa");
        // showConfirm('Bạn có chắc chắn muốn xóa khách hàng này?', () => {
        $http.delete('http://localhost:8083/khachhang/deleteback/' + id)
            .then(function (response) {
                showSuccessAlert("Đã khôi phục hoạt động cho khách hàng.")
                $scope.loadPage($scope.currentPage);
            })
            .catch(function (error) {
                console.error("Lỗi khi xóa :", error);
                showDangerAlert("Xóa thất bại , vui lòng thử lại sau!!")
            });
        // })
    };

//     // Reset form
    function resetForm() {
        $scope.ten = "";
        $scope.email = "";
        $scope.passw = "";
        $scope.gioiTinh = "";
        $scope.sdt = "";
        $scope.diaChi = "";
        $scope.trangThai = "";
    }

};

