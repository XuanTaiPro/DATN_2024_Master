window.voucherCtrl = function ($scope, $http) {
    const url = "http://localhost:8080/voucher";

    $scope.listVoucher = [];
    $scope.currentPage = 0;
    $scope.totalPages = 1;
    $scope.emptyMessage = "";

    $scope.loadPage = function (page) {
        $http.get('http://localhost:8080/voucher/page?page=' + page)
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

    // $http.get(url).then(function (response) {
    //     $scope.listVoucher = response.data;
    //     console.log("Lấy dữ liệu thành công");
    // }).catch((error) => {
    //     console.error('Lỗi:', error);
    // });

    $scope.listLVC = [];
    $http.get("http://localhost:8080/loaivoucher")
        .then(function (response) {
            $scope.listLVC = response.data;
            console.log("Lấy danh sách LVC thành công", $scope.listLVC);
        })
        .catch(function (error) {
            console.error("Lỗi khi lấy danh sách LVC:", error);
        });


    $scope.viewDetail = function (voucher) {
        $scope.selectedVoucher = angular.copy(voucher);
        $scope.selectedVoucher.trangThai = voucher.trangThai == 1 ? 'Hoạt động' : 'Ngưng hoạt động';
    }
    $scope.openUpdateModal = function (voucher) {
        $scope.selectedVoucher = angular.copy(voucher);
        $scope.selectedLVC = $scope.selectedVoucher.tenLoaiVC
        $scope.idLoaiVC = null
        console.log($scope.selectedLVC)
        fetch('http://localhost:8080/loaivoucher/getId?ten=' + $scope.selectedLVC).then(function (response) {
            return response.text()
        }).then(function (data) {
            $scope.$apply(function () {
                $scope.idLoaiVC = data;  // Gán giá trị trả về vào $scope.idQuyen
                console.log($scope.idLoaiVC);
            });
        }).catch(function (er) {
            console.error(er)
        })

        $scope.selectedVoucher.trangThai = voucher.trangThai.toString();
        console.log("Trạng thái hiện tại:", $scope.selectedVoucher.trangThai);
    };


    $scope.addVoucher = function (e) {
        const newVoucher = {
            ten: $scope.ten,
            giamGia: $scope.giamGia,
            giamMin: $scope.giamMin,
            giamMax: $scope.giamMax,
            dieuKien: $scope.dieuKien,
            ngayKetThuc: $scope.ngayKetThuc,
            soLuong: $scope.soLuong,
            idLoaiVC: $scope.idLoaiVC,
            trangThai: $scope.trangThai
        };

        console.log("Dữ liệu mới:", newVoucher);
        $http.post('http://localhost:8080/voucher/add', newVoucher)
            .then(function (response) {
                // Đóng modal
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

    $scope.updateVoucher = function () {
        if (!$scope.idLoaiVC) {
            alert("Vui lòng chọn quyền cho nhân viên.");
            return;
        }
        $scope.selectedVoucher.idLoaiVC = $scope.idLoaiVC;
        $http.put('http://localhost:8080/voucher/update/' + $scope.selectedVoucher.id, $scope.selectedVoucher)
            .then(function (response) {
                location.reload()
            })
            .catch(function () {
            });
    };


    $scope.deleteVoucher = function (id) {
        console.log("Xóa");
        if (confirm('Bạn có chắc chắn muốn xóa?')) {
            $http.delete('http://localhost:8080/voucher/delete/' + id)
                .then(function (response) {
                    // Kiểm tra phản hồi server
                    console.log(response.data);
                    location.reload()
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


    // Reset form
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
    }

};

