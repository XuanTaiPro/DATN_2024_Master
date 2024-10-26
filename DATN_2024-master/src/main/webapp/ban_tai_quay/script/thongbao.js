window.thongbaoCtrl = function ($scope, $http) {
    const url = "http://localhost:8080/thongbao";

    $scope.listThongBao = [];
    $scope.currentPage = 0;
    $scope.totalPages = 1;
    $scope.emptyMessage = "";

    $scope.loadPage = function (page) {
        $http.get('http://localhost:8080/thongbao/page?page=' + page)
            .then(function (response) {
                $scope.listThongBao = response.data.thongBaos;
                $scope.currentPage = response.data.currentPage;
                $scope.totalPages = response.data.totalPages;

                // Kiểm tra nếu danh sách trống và hiển thị thông báo
                if ($scope.listThongBao.length === 0) {
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


    $scope.listKH = [];
    $http.get("http://localhost:8080/khachhang")
        .then(function (response) {
            $scope.listKH = response.data;
            console.log("Lấy danh sách khách hàng thành công", $scope.listKH);
        })
        .catch(function (error) {
            console.error("Lỗi khi lấy danh sách LVC:", error);
        });

    $scope.formatDate = function(dateString) {
        // Kiểm tra nếu dateString là null hoặc rỗng
        if (!dateString) {
            return ''; // Trả về chuỗi rỗng nếu không có ngày
        }
        try {
            return new Date(dateString).toISOString().split('T')[0];
        } catch (e) {
            console.error("Lỗi khi định dạng ngày:", e);
            return ''; // Trả về chuỗi rỗng nếu xảy ra lỗi
        }
    };

    $scope.viewDetail = function (thongBao) {
        $scope.selectedThongBao = angular.copy(thongBao);
        $scope.selectedThongBao.trangThai = thongBao.trangThai == 1 ? 'Hoạt động' : 'Ngưng hoạt động';
        $scope.formattedNgayGui = new Date(thongBao.ngayGui);
        $scope.formattedNgayDoc = new Date(thongBao.ngayDoc);
    };

    $scope.openUpdateModal = function (thongBao) {
        $scope.selectedThongBao = angular.copy(thongBao);
        $scope.selectedKhachHang = $scope.selectedThongBao.tenKH;
        $scope.idKH = null;
        console.log($scope.selectedKhachHang);

        fetch('http://localhost:8080/khachhang/getId?ten=' + $scope.selectedKhachHang)
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

        $scope.selectedThongBao.trangThai = thongBao.trangThai.toString();
        console.log("Trạng thái hiện tại:", $scope.selectedThongBao.trangThai);
    };


    $scope.addThongBao = function () {
        const newThongBao = {
            noiDung: $scope.noiDung,
            idKH: $scope.idKH,
            trangThai: $scope.trangThai
        };

        console.log("Dữ liệu mới:", newThongBao);
        $http.post('http://localhost:8080/thongbao/add', newThongBao)
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

    $scope.updateThongBao = function () {
        if (!$scope.idKH) {
            alert("Vui lòng chọn khách hàng");
            return;
        }
        $scope.selectedThongBao.idKH = $scope.idKH;
        $http.put('http://localhost:8080/thongbao/update/' + $scope.selectedThongBao.id, $scope.selectedThongBao)
            .then(function (response) {
                location.reload()
            })
            .catch(function () {
            });
    };

    $scope.delete = function (id) {
        console.log("Xóa");
        if (confirm('Bạn có chắc chắn muốn xóa Thông Báo này?')) {
            $http.delete('http://localhost:8080/thongbao/delete/' + id)
                .then(function (response) {
                    console.log(response.data);
                    location.reload()
                    const index = $scope.listThongBao.findIndex(tb => tb.id === id);
                    if (index !== -1) {
                        $scope.listThongBao.splice(index, 1);
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
        $scope.noiDung = "";
        $scope.ngayDoc = "";
        $scope.ngayGui = "";
        $scope.idKH = "";
        $scope.trangThai = "";
    }

};

