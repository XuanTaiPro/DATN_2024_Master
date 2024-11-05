window.khachhangCtrl = function ($scope, $http) {
    const url = "http://localhost:8083/khachhang";

    $scope.listKhachHang = [];
    $scope.currentPage = 0;
    $scope.totalPages = 1;
    $scope.pageSize = 3;
    $scope.emptyMessage = "";

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

    $scope.getSTT = function(index) {
        return index + 1 + ($scope.currentPage * $scope.pageSize);
    };
    $scope.range = function(n) {
        return new Array(n);
    };

    $scope.setPage = function(page) {
        if (page >= 0 && page < $scope.totalPages) {
            $scope.loadPage(page);
        }
    };

    $scope.prevPage = function() {
        if ($scope.currentPage > 0) {
            $scope.setPage($scope.currentPage - 1);
        }
    };

    $scope.nextPage = function() {
        if ($scope.currentPage < $scope.totalPages - 1) {
            $scope.setPage($scope.currentPage + 1);
        }
    };
    $scope.range = function(n) {
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
    $scope.openUpdateModal = function(khachHang) {
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
                $scope.successMessage = "Khách hàng đã được thêm thành công!";
                setTimeout(function() {
                    $scope.successMessage = "";
                    location.reload();
                }, 500);
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
                location.reload()
            })
            .catch(function (error) {

            });
    };

    $scope.delete = function (id) {
        console.log("Xóa");
        if (confirm('Bạn có chắc chắn muốn xóa ?')) {
            $http.delete('http://localhost:8083/khachhang/delete/' + id)
                .then(function (response) {
                    // Kiểm tra phản hồi server
                    console.log(response.data);
                    const index = $scope.listKhachHang.findIndex(kh => kh.id === id);
                    if (index !== -1) {
                        $scope.listKhachHang.splice(index, 1);
                    }
                    alert(response.data.message || 'Xóa thành công!!');  // Sử dụng thông điệp từ server
                })
                .catch(function (error) {
                    console.error("Lỗi khi xóa :", error);
                    alert("Xóa thất bại. Vui lòng thử lại sau.");
                });
        }
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

