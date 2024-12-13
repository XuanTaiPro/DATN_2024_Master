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
        $scope.errorMessages = {}; // Đặt lại thông báo lỗi
        $scope.successMessage = ''; // Đặt lại thông báo thành công

        // Kiểm tra thông tin đầu vào

        if (!$scope.ten || $scope.ten.trim() === '') {
            $scope.errorMessages.ten = "Họ và tên không được để trống!";
        } else if ($scope.ten.trim().length < 3 || $scope.ten.trim().length > 50) {
            $scope.errorMessages.ten = "Họ và tên không hợp lệ! Tối thiểu 3 kí tự Tối đa 50 ";
        }
        if (!$scope.email || $scope.email.trim() === '') {
            $scope.errorMessages.email = "Email không được để trống!";
            // return;
        }
        if (!$scope.passw || $scope.passw.trim() === '') {
            $scope.errorMessages.passw = "Mật khẩu không được để trống!";
            // return;
        }
        if (!$scope.gioiTinh || $scope.gioiTinh.trim() === '') {
            $scope.errorMessages.gioiTinh = "Phải chọn giới tính!";
            // return;
        }
        if (!$scope.diaChi || $scope.diaChi.trim() === '') {
            $scope.errorMessages.diaChi = "Địa chỉ không được để trống!";
            // return;
        }
        if (!$scope.trangThai || $scope.trangThai.trim() === '') {
            $scope.errorMessages.trangThai = "Trạng thái không được để trống!";
            // return;
        }


        const phoneRegex = /^0\d{9}$/;
        if (!phoneRegex.test($scope.sdt)) {
            $scope.errorMessages.sdt = "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0!";
            return;
        }
        if (!$scope.sdt || $scope.sdt.trim() === '') {
            $scope.errorMessages.sdt = "Số điện thoại không được để trống!";
            // return;
        }

        // Kiểm tra email và số điện thoại trùng lặp
        const checkDuplicatePromises = [
            $http.get('http://localhost:8083/khachhang/check-email', { params: { email: $scope.email } }),
            // $http.get('http://localhost:8083/khachhang/CheckEmail/check-email', { params: { email: $scope.email } }),
            $http.get('http://localhost:8083/khachhang/check-phone', { params: { sdt: $scope.sdt } })
        ];

        Promise.all(checkDuplicatePromises)
            .then(function (responses) {
                const emailExists = responses[0].data;
                const phoneExists = responses[1].data;

                if (emailExists) {
                    $scope.errorMessages.email = "Email này đã được sử dụng!";
                }
                if (phoneExists) {
                    $scope.errorMessages.sdt = "Số điện thoại này đã được sử dụng!";
                    console.log($scope.errorMessages.sdt )
                    $scope.$apply();
                }

                // Nếu không có lỗi, tiếp tục thêm khách hàng
                if (!emailExists && !phoneExists) {
                    const newKhachHang = {
                        ten: $scope.ten,
                        email: $scope.email,
                        passw: $scope.passw,
                        gioiTinh: $scope.gioiTinh,
                        sdt: $scope.sdt,
                        diaChi: $scope.diaChi,
                        trangThai: $scope.trangThai
                    };

                    $http.post('http://localhost:8083/khachhang/add', newKhachHang)
                        .then(function (response) {
                            $scope.listKhachHang.push(response.data);
                            $('#productModal').modal('hide');
                            showSuccessAlert('Thêm thành công!');
                            // $scope.loadPage($scope.currentPage);
                        })
                        .catch(function (error) {
                            $scope.errorMessage = "Thêm thất bại";
                        });
                }
            })
            .catch(function (error) {
                console.error("Lỗi kiểm tra email hoặc số điện thoại:", error);
                $scope.errorMessages.email = "Lỗi khi kiểm tra email!";
                $scope.errorMessages.sdt = "Lỗi khi kiểm tra số điện thoại!";
            });
    };

    $scope.updateKhachHang = function () {
        console.log("Cập nhật Khách Hàng:", $scope.selectedKhachHang);
        $scope.errorMessages = {}; // Đặt lại thông báo lỗi
        if (!$scope.selectedKhachHang.ten || $scope.selectedKhachHang.ten.trim() === '') {
            $scope.errorMessages.ten = "Họ và tên không được để trống!";
        } else if ($scope.selectedKhachHang.ten.trim().length < 3 || $scope.selectedKhachHang.ten.trim().length > 50) {
            $scope.errorMessages.ten = "Họ và tên không hợp lệ! Tối thiểu 3 kí tự, tối đa 50 kí tự.";
        }

        if (!$scope.selectedKhachHang.email || $scope.selectedKhachHang.email.trim() === '') {
            $scope.errorMessages.email = "Email không được để trống!";
        }

        const phoneRegex = /^0\d{9}$/;
        if (!phoneRegex.test($scope.selectedKhachHang.sdt)) {
            $scope.errorMessages.sdt = "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0!";
        }

        // Nếu có lỗi, ngừng thực thi và hiển thị lỗi
        if (Object.keys($scope.errorMessages).length > 0) {
            return; // Dừng lại nếu có lỗi
        }

        // Nếu không có lỗi client-side, kiểm tra trùng lặp trên server
        const checkDuplicatePromises = [
            $http.get('http://localhost:8083/khachhang/khachhangud/check-email', {
                params: { email: $scope.selectedKhachHang.email, id: $scope.selectedKhachHang.id }
            }),
            $http.get('http://localhost:8083/khachhang/khachhangud/check-phone', {
                params: { sdt: $scope.selectedKhachHang.sdt, id: $scope.selectedKhachHang.id }
            })
        ];

        Promise.all(checkDuplicatePromises)
            .then(function (responses) {
                const emailExists = responses[0].data;
                const phoneExists = responses[1].data;

                if (emailExists) {
                    $scope.errorMessages.email = "Email này đã được sử dụng!";
                }
                if (phoneExists) {
                    $scope.errorMessages.sdt = "Số điện thoại này đã được sử dụng!";
                }

                if (!emailExists && !phoneExists) {
                    // Gửi yêu cầu cập nhật nếu không có lỗi
                    $http.put('http://localhost:8083/khachhang/update/' + $scope.selectedKhachHang.id, $scope.selectedKhachHang)
                        .then(function (response) {
                            $('#UpdateForm').modal('hide');
                            showSuccessAlert('Cập nhật thành công!');
                            $scope.loadPage($scope.currentPage);
                        })
                        .catch(function (error) {
                            console.error("Lỗi cập nhật khách hàng:", error);
                            $scope.errorMessage = "Cập nhật thất bại!";
                        });
                }
            })
            .catch(function (error) {
                console.error("Lỗi kiểm tra email hoặc số điện thoại:", error);
                $scope.errorMessages.email = "Lỗi khi kiểm tra email!";
                $scope.errorMessages.sdt = "Lỗi khi kiểm tra số điện thoại!";
            });
    };


    $scope.delete = function (id) {
        console.log("Xóa");
        showConfirm('Bạn có chắc chắn muốn xóa khách hàng này?', () => {
            $http.delete('http://localhost:8083/khachhang/delete/' + id)
                .then(function (response) {
                    // Kiểm tra phản hồi server
                    console.log(response.data);
                    const index = $scope.listKhachHang.findIndex(kh => kh.id === id);
                    if (index !== -1) {
                        $scope.listKhachHang.splice(index, 1);
                    }
                   showSuccessAlert("Xóa thành công")
                })
                .catch(function (error) {
                    console.error("Lỗi khi xóa :", error);
                   showDangerAlert("Xóa thất bại , vui lòng thử lại sau!!")
                });
        })
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

