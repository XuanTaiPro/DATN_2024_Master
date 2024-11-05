window.loaivoucherCtrl = function ($scope, $http) {
    const url = "http://localhost:8083/loaivoucher";

    $scope.listLoaiVoucher = [];
    $scope.currentPage = 0;
    $scope.totalPages = 1;
    $scope.pageSize = 3;
    $scope.emptyMessage = "";

    $scope.loadPage = function (page) {
        $http.get('http://localhost:8083/loaivoucher/page?page=' + page)
            .then(function (response) {
                $scope.listLoaiVoucher = response.data.loaiVouchers;
                $scope.currentPage = response.data.currentPage;
                $scope.totalPages = response.data.totalPages;

                // Kiểm tra nếu danh sách trống và hiển thị thông báo
                if ($scope.listLoaiVoucher.length === 0) {
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

    $scope.viewDetail = function (loaiVoucher) {
        $scope.selectedLoaiVoucher = angular.copy(loaiVoucher);
        $scope.selectedLoaiVoucher.trangThai = loaiVoucher.trangThai == 1 ? 'Hoạt động' : 'Ngưng hoạt động';
    }
    $scope.openUpdateModal = function(loaiVoucher) {
        $scope.selectedLoaiVoucher = angular.copy(loaiVoucher);
        $scope.selectedLoaiVoucher.trangThai = loaiVoucher.trangThai.toString();
        console.log("Trạng thái hiện tại:", $scope.selectedLoaiVoucher.trangThai);// Sao chép dữ liệu nhân viên cần cập nhật
    };


    $scope.addLoaiVoucher = function () {
        const newLoaiVoucher = {
            ten: $scope.ten,
            trangThai: $scope.trangThai,
            moTa: $scope.moTa,
        };
        console.log("Dữ liệu mới:", newLoaiVoucher);
        $http.post('http://localhost:8083/loaivoucher/add', newLoaiVoucher)
            .then(function (response) {
                $('#productModal').modal('hide');
                setTimeout(function () {
                    location.reload();
                }, 500);
            })
            .catch(function (error) {
                $scope.errorMessage = "Thêm thất bại";
            });
        resetForm();

    };

    $scope.updateLoaiVoucher = function () {
        console.log("Cập nhật Loại Voucher:", $scope.selectedLoaiVoucher);  // Kiểm tra dữ liệu trước khi gửi
        $http.put('http://localhost:8083/loaivoucher/update/' + $scope.selectedLoaiVoucher.id, $scope.selectedLoaiVoucher)
            .then(function (response) {
                location.reload()
            })
            .catch(function (error) {

            });
    };


    $scope.delete = function (id) {
        if (confirm('Bạn có chắc chắn muốn xóa?')) {
            $http.delete('http://localhost:8083/loaivoucher/delete/' + id)
                .then(function (response) {
                    // Kiểm tra phản hồi server
                    console.log(response.data);
                    location.reload()
                    const index = $scope.listLoaiVoucher.findIndex(lvc => lvc.id === id);
                    if (index !== -1) {
                        $scope.listLoaiVoucher.splice(index, 1);
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
    // Reset form
    function resetForm() {
        $scope.ten = "";
        $scope.moTa ="";
        $scope.trangThai = "";
    }

};

