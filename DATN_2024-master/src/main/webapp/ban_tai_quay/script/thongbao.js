window.thongbaoCtrl = function ($scope, $http) {
    const url = "http://localhost:8080/thongbao";

    $scope.listThongBao = [];

    $http.get(url).then(function (response) {
        $scope.listThongBao = response.data;
        console.log("Lấy dữ liệu thành công");
    }).catch((error) => {
        console.error('Lỗi:', error);
    });

    // Mở modal
    $scope.openModal = function () {
        var modalElement = new bootstrap.Modal(document.getElementById('tbForm'), {
            keyboard: false
        });
        modalElement.show();
    };
    $scope.openDetailModal = function (thongBao) {
        $scope.selectedThongBao = angular.copy(thongBao);
        var modalElement = new bootstrap.Modal(document.getElementById('readData'), {
            keyboard: false
        });
        modalElement.show(); // Hiển thị modal
    };
    $scope.openUpdateModal = function (thongBao) {
        $scope.selectedThongBao = angular.copy(thongBao);
        // Chuyển đổi chuỗi ngày sang đối tượng Date
        if ($scope.selectedThongBao.ngayDoc) {
            $scope.selectedThongBao.ngayDoc = new Date($scope.selectedThongBao.ngayDoc);
        }
        if ($scope.selectedThongBao.ngayGui) {
            $scope.selectedThongBao.ngayGui = new Date($scope.selectedThongBao.ngayGui);
        }
        var modalElement = new bootstrap.Modal(document.getElementById('updateForm'), {
            keyboard: false
        });
        modalElement.show();
    };


    $scope.addThongBao = function () {
        const newThongBao = {
            noiDung: $scope.noiDung,
            ngayDoc: $scope.ngayDoc,
            ngayGui: $scope.ngayGui,
            idKH: $scope.idKH,
            trangThai: $scope.trangThai
        };

        $http.post(url + '/add', newThongBao)
            .then(function (response) {
                $scope.listThongBao.push(response.data);
                alert('Thêm thành công!!')
                resetForm();
            }).catch((error) => {
            $scope.errorMessage = "Thêm thất bại";
        });
        // Reset form
        resetForm();
        var modalElement = new bootstrap.Modal(document.getElementById('tbForm'));
        modalElement.hide();

    };
    $scope.updateThongBao = function () {
        console.log("Cập nhật voucher:", $scope.selectedThongBao);  // Kiểm tra dữ liệu trước khi gửi
        $http.put(url + '/update/' + $scope.selectedThongBao.id, $scope.selectedThongBao)
            .then(function (response) {
                console.log("Cập nhật thành công", response.data);
                // Cập nhật danh sách nhân viên sau khi update thành công
                const index = $scope.listThongBao.findIndex(tb => tb.id === response.data.id);
                if (index !== -1) {
                    $scope.listThongBao[index] = response.data;
                }
                alert('Cập nhật thành công!!');
                resetUpdateForm();
            })
            .catch(function (error) {
                console.error("Lỗi khi cập nhật nhân viên:", error);
                alert("Cập nhật thất bại. Vui lòng thử lại sau.");
            });
        var modalElement = new bootstrap.Modal(document.getElementById('updateForm'));
        modalElement.hide();
    };



// Xóa nhân viên
    $scope.deleteThongBao = function (id) {
        console.log("Xóa");
        if (confirm('Bạn có chắc chắn muốn xóa Thông Báo này?')) {
            $http.delete(url + '/delete/' + id)
                .then(function (response) {
                    // Tìm chỉ số của nhân viên đã xóa
                    const index = $scope.listThongBao.findIndex(tb => tb.id === id);
                    if (index !== -1) {
                        $scope.listThongBao.splice(index, 1);  // Xóa nhân viên khỏi danh sách
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
        $scope.noiDung = "";
        $scope.ngayDoc = "";
        $scope.ngayGui = "";
        $scope.idKH = "";
        $scope.trangThai = "";
    }

};

