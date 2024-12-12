window.nhansuCtrl = function ($scope, $http) {


    $scope.selectedNhanVien = {}; // Khởi tạo đối tượng nếu chưa có


    $scope.listNhanVien = [];
    $scope.currentPage = 0;
    $scope.totalPages = 1;
    $scope.pageSize = 3;
    $scope.emptyMessage = "";

    // Tìm kiếm và lọc
    $scope.searchParams = {
        ten: '',
        gioiTinh: '',
        diaChi: '',
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


    // $http.get('http://localhost:8080/nhanvien').then(function (response) {
    //     $scope.listNhanVien = response.data;
    //     console.log("Lấy dữ liệu thành công");
    // }).catch((error) => {
    //     console.error('Lỗi:', error);
    // });
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
        if (file && file.size < 10000000) {  // Kiểm tra kích thước file < 1MB
            var fileName = file.name;  // Lấy tên file
            var filePath = "img/" + fileName;  // Đường dẫn bạn lưu ảnh trong thư mục img

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
                            console.log("Phản hồi từ backend:", response);
                            if (response.data && response.data.message) {
                                $scope.successMessage = response.data.message;
                                $scope.errorMessage = '';

                                if (!$scope.$$phase) {
                                    $scope.$apply(function () {
                                        $('#productModal').modal('hide');
                                    });
                                } else {
                                    $('#productModal').modal('hide');
                                }

                                $timeout(function () {
                                    $scope.successMessage = '';
                                }, 2000);
                            }

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
                        });

                }
            })
            .catch(function (error) {
                $scope.errorMessages.email = "Lỗi khi kiểm tra email!";
            });
    };




    $scope.updateNhanVien = function () {
        if (!$scope.idQuyen) {
            alert("Vui lòng chọn quyền cho nhân viên.");
            return;
        }

        $http.get('http://localhost:8083/nhanvien/nhavienud/check-email', {
            params: {
                email: $scope.email,
                id: $scope.selectedNhanVien.id
            }
        })
            .then(function (response) {
                if (response.data) {
                    $scope.errorMessages.email = "Email này đã tồn tại!";
                } else {
                    $scope.selectedNhanVien.idQuyen = $scope.idQuyen;

                    $http.put('http://localhost:8083/nhanvien/update/' + $scope.selectedNhanVien.id, $scope.selectedNhanVien)
                        .then(function (response) {
                            $('#UpdateForm').modal('hide');
                            const alertBox = document.getElementById('success-alert');
                            alertBox.style.display = 'block'; // Hiện alert
                            setTimeout(() => alertBox.classList.add('show'), 10);
                            setTimeout(() => {
                                alertBox.classList.remove('show'); // Ẩn hiệu ứng
                                setTimeout(() => (alertBox.style.display = 'none'), 500); // Ẩn hoàn toàn
                            }, 3000);

                            $scope.loadPage($scope.currentPage);
                        })
                        .catch(function (error) {
                            console.error("Lỗi khi cập nhật nhân viên:", error);
                            if (error.status === 403) {
                                alert(error.data.message || "Bạn không có quyền thực hiện thao tác này!");
                            } else {
                                alert(error.data.message || "Cập nhật nhân viên thất bại. Vui lòng thử lại sau.");
                            }
                        });
                }
            })
            .catch(function (error) {
                $scope.errorMessages.email = "Lỗi khi kiểm tra email!";
            });
    };


// Xóa nhân viên
    $scope.deleteNhanVien = function (id) {
        console.log("Xóa");
        $('#deleteNhanSuModal').modal('hide');
        $http.delete('http://localhost:8083/nhanvien/delete/' + id)
            .then(function (response) {
                // Kiểm tra phản hồi server
                console.log(response.data);
                const index = $scope.listNhanVien.findIndex(nv => nv.id === id);
                if (index !== -1) {
                    $scope.listNhanVien.splice(index, 1);
                    $('#deleteSuccessModal').modal('show');
                }
            })
            .catch(function (error) {
                console.error("Lỗi khi xóa nhân viên:", error);
                if (error.status === 403) {
                    alert(error.data.message || "Bạn không có quyền thực hiện thao tác này!");
                } else {
                    alert(error.data.message || "Xóa thất bại. Vui lòng thử lại sau.");
                }
            });
    };

    $scope.confirmDeleteNhanSu = function () {
        $('#deleteNhanSuModal').modal('show');
    }
    $scope.closeModalDeleteSuccess = function () {
        $('#deleteSuccessModal').modal('hide');
        console.log("ok")
    }
    $scope.closeDeleteNhanSuModal = function () {
        $('#deleteNhanSuModal').modal('hide');
    }

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