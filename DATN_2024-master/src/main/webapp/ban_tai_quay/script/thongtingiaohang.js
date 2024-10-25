window.thongtingiaohangCtrl = function ($scope, $http) {
    const url = "http://localhost:8080/thongtingiaohang";

    $scope.listTtgh = [];

    $http.get(url).then(function (response) {
        $scope.listTtgh = response.data;
        console.log("Lấy dữ liệu thành công");
    }).catch((error) => {
        console.error('Lỗi:', error);
    });

    // Mở modal
    $scope.openModal = function () {
        var modalElement = new bootstrap.Modal(document.getElementById('ttghForm'), {
            keyboard: false
        });
        modalElement.show();
    };
    $scope.openDetailModal = function (ttgh) {
        $scope.selectedTtgh = angular.copy(ttgh);
        var modalElement = new bootstrap.Modal(document.getElementById('readData'), {
            keyboard: false
        });
        modalElement.show(); // Hiển thị modal
    };
    $scope.openUpdateModal = function (ttgh) {
        $scope.selectedTtgh = angular.copy(ttgh);
        var modalElement = new bootstrap.Modal(document.getElementById('updateForm'), {
            keyboard: false
        });
        modalElement.show();
    };


    $scope.addTtgh = function () {
        console.log("Thêm Khách hàng được gọi!");
        const newTtgh = {
            sdtNguoiNhan: $scope.sdtNguoiNhan,
            tenNguoiNhan: $scope.tenNguoiNhan,
            dcNguoiNhan: $scope.dcNguoiNhan,
            trangThai: $scope.trangThai,
            idKH: $scope.idKH,
        };
        console.log("Dữ liệu voucher mới:", newTtgh);

        $http.post(url + '/add', newTtgh)
            .then(function (response) {
                $scope.listTtgh.push(response.data);
                alert('Thêm thành công!!')
                resetForm();
            }).catch((error) => {
            $scope.errorMessage = "Thêm thất bại";
        });
        // Reset form
        resetForm();
        var modalElement = new bootstrap.Modal(document.getElementById('ttghForm'));
        modalElement.hide();

    };
    $scope.updateTtgh = function () {
        console.log("Cập nhật TTGH:", $scope.selectedTtgh);  // Kiểm tra dữ liệu trước khi gửi
        $http.put(url + '/update/' + $scope.selectedTtgh.id, $scope.selectedTtgh)
            .then(function (response) {
                console.log("Cập nhật thành công", response.data);
                // Cập nhật danh sách nhân viên sau khi update thành công
                const index = $scope.listTtgh.findIndex(ttgh => ttgh.id === response.data.id);
                if (index !== -1) {
                    $scope.listTtgh[index] = response.data;
                }
                alert('Cập nhật thành công!!');
                resetUpdateForm();
            })
            .catch(function (error) {
                console.error("Lỗi khi cập nhật:", error);
                alert("Cập nhật thất bại. Vui lòng thử lại sau.");
            });
        var modalElement = new bootstrap.Modal(document.getElementById('updateForm'));
        modalElement.hide();
    };



// Xóa nhân viên
    $scope.deleteTtgh = function (id) {
        console.log("Xóa");
        if (confirm('Bạn có chắc chắn muốn xóa TTgh này?')) {
            $http.delete(url + '/delete/' + id)
                .then(function (response) {
                    // Tìm chỉ số của nhân viên đã xóa
                    const index = $scope.listTtgh.findIndex(ttgh => ttgh.id === id);
                    if (index !== -1) {
                        $scope.listTtgh.splice(index, 1);  // Xóa nhân viên khỏi danh sách
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
        $scope.sdtNguoiNhan = "";
        $scope.tenNguoiNhan = "";
        $scope.dcNguoiNhan = "";
        $scope.idKH = "";
        $scope.trangThai = "";
    }

};

