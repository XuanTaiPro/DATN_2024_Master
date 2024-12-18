window.nhansuCtrl = function ($scope, $http) {


    $scope.selectedNhanVien = {}; // Khởi tạo đối tượng nếu chưa có


    $scope.listNhanVien = [];
    $scope.currentPage = 0;
    $scope.totalPages = 1;
    $scope.pageSize = 5;
    $scope.emptyMessage = "";

    // Tìm kiếm và lọc
    $scope.searchParams = {
        ten: '',
        gioiTinh: '',
        diaChi: '',
        tenQuyen:'',
        trangThai: ''
    };

    $scope.searchAndFilter = function (page = 0) {
        const params = {
            ...$scope.searchParams,
            page: page,
            size: $scope.pageSize
        };

        $http.get('http://localhost:8083/nhanvien/search-filter', {params})
            .then(function (response) {
                $scope.listNhanVien = response.data.nhanViens;
                $scope.currentPage = response.data.currentPage;
                $scope.totalPages = response.data.totalPages;

                if ($scope.listNhanVien.length === 0) {
                    $scope.emptyMessage = response.data.message || "Không tìm thấy nhân viên!";
                } else {
                    $scope.emptyMessage = ""; // Reset lại thông báo
                }
            })
            .catch(function (error) {
                console.error("Lỗi khi tìm kiếm:", error);
                alert("Đã xảy ra lỗi. Vui lòng thử lại.");
            });
    };

    $scope.resetFilters = function () {
        $scope.searchParams = {
            ten: '',
            gioiTinh: '',
            diaChi: '',
            trangThai: ''
        };
        $scope.loadPage(0);
    };


    $scope.loadPage = function (page) {
        $scope.idNV = sessionStorage.getItem('idNV')
      $scope.tenQuyen =  sessionStorage.getItem('tenQuyen')
        console.log()
        $http.get('http://localhost:8083/nhanvien/page?page=' + page)
            .then(function (response) {
                $scope.listNhanVien = response.data.nhanViens;
                $scope.currentPage = response.data.currentPage;
                $scope.totalPages = response.data.totalPages;

                // Kiểm tra nếu danh sách trống và hiển thị thông báo
                if ($scope.listNhanVien.length === 0) {
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
            if ($scope.searchParams.ten || $scope.searchParams.gioiTinh || $scope.searchParams.diaChi || $scope.searchParams.trangThai) {
                $scope.searchAndFilter(page);
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

    $scope.listQuyen = [];
    $http.get("http://localhost:8083/quyen")
        .then(function (response) {
            $scope.listQuyen = response.data;
            console.log("Lấy danh sách quyền thành công", $scope.listQuyen);
        })
        .catch(function (error) {
            console.error("Lỗi khi lấy danh sách quyền:", error);
        });


    $scope.updateImage = function (files) {
        var file = files[0];
        if (file && file.size < 10000000) {
            var fileName = file.name;
            var filePath = "img/NhanVien/" + fileName;  // Đường dẫn bạn lưu ảnh trong thư mục img

            // Cập nhật đường dẫn ảnh trong Angular
            $scope.$apply(function () {
                $scope.img = filePath;  // Chỉ lưu đường dẫn vào $scope.img
                $scope.selectedNhanVien.img = filePath;  // Đồng thời cập nhật đường dẫn vào selectedNhanVien
            });
            console.log("Đường dẫn ảnh: " + filePath);
        } else {
            alert("This file is too large!");
        }
    };


    $scope.viewDetail = function (nhanVien) {
        $scope.selectedNhanVien = angular.copy(nhanVien);
        $scope.img = nhanVien.img;
        $scope.selectedNhanVien.trangThai = nhanVien.trangThai == 1 ? 'Hoạt động' : 'Ngưng hoạt động';
    }
    $scope.openUpdateModal = function (nhanVien) {
        $scope.selectedNhanVien = angular.copy(nhanVien);
        $scope.selectedQuyen = $scope.selectedNhanVien.tenQuyen
        $scope.idQuyen = null
        console.log($scope.selectedQuyen)
        fetch('http://localhost:8083/quyen/getId?ten=' + $scope.selectedQuyen).then(function (response) {
            return response.text()
        }).then(function (data) {
            $scope.$apply(function () {
                $scope.idQuyen = data;  // Gán giá trị trả về vào $scope.idQuyen
                console.log($scope.idQuyen);
            });
        }).catch(function (er) {
            console.error(er)
        })
        $scope.selectedNhanVien.trangThai = nhanVien.trangThai.toString();
        console.log("Trạng thái hiện tại:", $scope.selectedNhanVien.trangThai);
    };

    $scope.addNhanVien = function () {
        $scope.errorMessage = '';
        $scope.errorMessages = {};
        $scope.successMessage = '';
        $http.get('http://localhost:8083/nhanvien/check-email', { params: { email: $scope.email } })
            .then(function (response) {
                if (response.data) {

                    $scope.errorMessages.email = "Email này đã tồn tại!";
                } else {
                    const newNhanVien = {
                        ten: $scope.ten,
                        email: $scope.email,
                        passw: $scope.passw,
                        gioiTinh: $scope.gioiTinh,
                        diaChi: $scope.diaChi,
                        trangThai: $scope.trangThai,
                        idQuyen: $scope.idQuyen,
                        img: $scope.img
                    };
                    console.log("Dữ liệu nhân viên mới:", newNhanVien);

                    $http.post('http://localhost:8083/nhanvien/add', newNhanVien)
                        .then(function (response) {
                                $scope.listNhanVien.push(response.data);
                                $('#productModal').modal('hide');
                                setTimeout(function () {
                                    $scope.loadPage($scope.currentPage);
                                }, 500);
                                resetForm();
                        })
                        .catch(function (error) {
                            $scope.successMessage = '';
                            if (error.status === 400) {
                                $scope.errorMessages = error.data;
                            } else {
                                $scope.errorMessage = 'Thêm nhân viên thất bại!';
                                $timeout(function () {
                                    $scope.errorMessage = '';
                                }, 2000);
                            }
                        })

                }
            })
            .catch(function (error) {
                // $scope.errorMessages.email = "Lỗi khi kiểm tra email!";
            });
    };




    $scope.updateNhanVien = function () {
        $scope.errorMessages = {};

        if (!$scope.idQuyen) {
            alert("Vui lòng chọn quyền cho nhân viên.");
            return;
        }
        if (!$scope.selectedNhanVien.id) {
            alert("ID nhân viên không hợp lệ.");
            return;
        }

        $http.get('http://localhost:8083/nhanvien/nhavienud/check-email', {
            params: { email: $scope.selectedNhanVien.email,id: $scope.selectedNhanVien.id } })
            .then(function (response) {
                if (response.data) {
                    $scope.errorMessages.email = "Email này đã tồn tại!";
                } else {
                    $scope.selectedNhanVien.idQuyen = $scope.idQuyen;
                    $http.put('http://localhost:8083/nhanvien/update/' + $scope.selectedNhanVien.id, $scope.selectedNhanVien)
                        .then(function (response) {
                            $('#UpdateForm').modal('hide');
                            const alertBox = document.getElementById('success-alert');
                            alertBox.style.display = 'block';
                            setTimeout(() => alertBox.classList.add('show'), 10);
                            setTimeout(() => {
                                alertBox.classList.remove('show');
                                setTimeout(() => (alertBox.style.display = 'none'), 500);
                            }, 3000);

                            $scope.loadPage($scope.currentPage);
                        })
                        .catch(function (error) {
                            console.error("Lỗi khi cập nhật nhân viên:", error);
                        });
                }
            })
            .catch(function (error) {
                console.error("Lỗi khi kiểm tra email:", error);
                $scope.errorMessages.email = "Lỗi khi kiểm tra email!";
            });
    };

// Xóa nhân viên
    $scope.delete = function (id) {
        console.log(id);
        $('#deleteNhanSuModal').modal('hide');
        showConfirm('Bạn có chắc chắn muốn ngưng hoạt động nhân viên này?', () => {
            $http.delete('http://localhost:8083/nhanvien/delete/' + id)
                .then(function (response) {
                    showWarningAlert("Đã cho tạm ngưng hoạt động nhân viên này !!")
                    $scope.loadPage($scope.currentPage);
                })
                .catch(function (error) {
                    if (error.status === 403) {
                        showDangerAlert("Bạn không có quyền thực hiện thao tác này!")
                    } else {
                        showDangerAlert("Xóa thất bại , vui lòng thử lại sau!!")
                    }
                });
        })
    };


    $scope.deleteback = function (id) {
        console.log(id);
        $('#deleteNhanSuModal').modal('hide');
        showConfirm('Bạn có chắc chắn muốn cấp lại hoạt động nhân viên  này?', () => {
        $http.delete('http://localhost:8083/nhanvien/deleteback/' + id)
            .then(function (response) {
                showSuccessAlert("Đã khôi phục hoạt động cho nhân viên")
                $scope.loadPage($scope.currentPage);
            })
            .catch(function (error) {
                if (error.status === 403) {
                    showDangerAlert("Bạn không có quyền thực hiện thao tác này!")
                } else {
                    showDangerAlert("Xóa thất bại , vui lòng thử lại sau!!")
                }
            });
        })
    };

    function resetForm() {
        $scope.name = "";
        $scope.email = "";
        $scope.passw = "";
        $scope.gioiTinh = "";
        $scope.diaChi = "";
        $scope.trangThai = "";
        $scope.ngayTao = "";
        $scope.img = "";
    }
};