window.thongbaoCtrl = function ($scope, $http) {
    const url = "http://localhost:8083/thongbao";

    $scope.listThongBao = [];
    $scope.currentPage = 0;
    $scope.totalPages = 1;
    $scope.pageSize = 3;
    $scope.emptyMessage = "";

    $scope.loadPage = function (page) {
        $http.get('http://localhost:8083/thongbao/page?page=' + page)
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


    $scope.listKH = [];
    $http.get("http://localhost:8083/khachhang")
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
                // console.error("Lỗi khi gọi API:", error);
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
        $http.post('http://localhost:8083/thongbao/add', newThongBao)
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
        $http.put('http://localhost:8083/thongbao/update/' + $scope.selectedThongBao.id, $scope.selectedThongBao)
            .then(function (response) {
                location.reload()
            })
            .catch(function () {
            });
    };

    $scope.delete = function (id) {
        console.log("Xóa");
        if (confirm('Bạn có chắc chắn muốn xóa Thông Báo này?')) {
            $http.delete('http://localhost:8083/thongbao/delete/' + id)
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


    $scope.sendEmail = function (thongBao) {
        const emailRequest = {
            emailNguoiNhan: thongBao.emailKH,
            tieuDe: "Thông báo từ Shop bán thực phẩm chức năng Loopy",
            noiDung: thongBao.noiDung
        };

        $http.post('http://localhost:8083/mail/sentKH', emailRequest)
            .then(function (response) {
                // Hiển thị thông báo thành công từ phản hồi JSON
                alert(response.data.message);
            })
            .catch(function (error) {
                // Hiển thị thông báo lỗi từ phản hồi JSON
                const errorMessage = error.data && error.data.message ? error.data.message : "Không thể gửi email. Vui lòng thử lại!";
                alert(errorMessage);
                console.error("Lỗi khi gửi email:", error);
            });
    };


    function resetForm() {
        $scope.noiDung = "";
        $scope.ngayDoc = "";
        $scope.ngayGui = "";
        $scope.idKH = "";
        $scope.trangThai = "";
    }



};

