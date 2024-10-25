window.loaivoucherCtrl = function ($scope, $http) {
    const url = "http://localhost:8080/loaivoucher";

    $scope.listLoaiVoucher = [];

    $http.get(url).then(function (response) {
        $scope.listLoaiVoucher = response.data;
        console.log("Lấy dữ liệu thành công");
    }).catch((error) => {
        console.error('Lỗi:', error);
    });

    // Mở modal
    $scope.openModal = function () {
        var modalElement = new bootstrap.Modal(document.getElementById('lvcForm'), {
            keyboard: false
        });
        modalElement.show();
    };
    $scope.openDetailModal = function (loaiVoucher) {
        $scope.selectedLoaiVoucher = angular.copy(loaiVoucher);
        var modalElement = new bootstrap.Modal(document.getElementById('readData'), {
            keyboard: false
        });
        modalElement.show(); // Hiển thị modal
    };
    $scope.openUpdateModal = function (loaiVoucher) {
        $scope.selectedLoaiVoucher = angular.copy(loaiVoucher);
        var modalElement = new bootstrap.Modal(document.getElementById('updateForm'), {
            keyboard: false
        });
        modalElement.show();
    };


    $scope.addLoaiVoucher = function () {
        const newLoaiVoucher = {
            ten: $scope.ten,
            trangThai: $scope.trangThai,
            moTa: $scope.moTa,
        };
        $http.post(url + '/add', newLoaiVoucher)
            .then(function (response) {
                $scope.listLoaiVoucher.push(response.data);
                alert('Thêm thành công!!')
                resetForm();
            }).catch((error) => {
            $scope.errorMessage = "Thêm thất bại";
        });
        // Reset form
        resetForm();
        var modalElement = new bootstrap.Modal(document.getElementById('lvcForm'));
        modalElement.hide();

    };
    $scope.updateLoaiVoucher = function () {
        console.log("Cập nhật voucher:", $scope.selectedLoaiVoucher);  // Kiểm tra dữ liệu trước khi gửi
        $http.put(url + '/update/' + $scope.selectedLoaiVoucher.id, $scope.selectedLoaiVoucher)
            .then(function (response) {
                console.log("Cập nhật thành công", response.data);
                const index = $scope.listLoaiVoucher.findIndex(lvc => lvc.id === response.data.id);
                if (index !== -1) {
                    $scope.listLoaiVoucher[index] = response.data;
                }
                alert('Cập nhật thành công!!');
                resetUpdateForm();
            })
            .catch(function () {
            });
        var modalElement = new bootstrap.Modal(document.getElementById('updateForm'));
        modalElement.hide();
    };



// Xóa nhân viên
    $scope.deleteLoaiVoucher = function (id) {
        console.log("Xóa");
        if (confirm('Bạn có chắc chắn muốn xóa loại voucher này?')) {
            $http.delete(url + '/delete/' + id)
                .then(function (response) {
                    // Tìm chỉ số của nhân viên đã xóa
                    const index = $scope.listLoaiVoucher.findIndex(lvc => lvc.id === id);
                    if (index !== -1) {
                        $scope.listLoaiVoucher.splice(index, 1);  // Xóa nhân viên khỏi danh sách
                    }
                    alert('Xóa thành công!!');
                })
                .catch(function (error) {
                    console.error("Lỗi khi xóa nhân viên:", error);
                    alert("Xóa thất bại. Vui lòng thử lại sau.");  // Hiển thị thông báo lỗi
                });
        }
    };


    // Reset form
    // Reset form
    function resetForm() {
        $scope.ten = "";
        $scope.moTa ="";
        $scope.trangThai = "";
    }

};

