window.giamGiaCtrl = function ($scope, $http) {

    // Khởi tạo sản phẩm
    $scope.giamGia = {
        selectedProducts: [] // Khởi tạo là một mảng rỗng
    };
    $scope.giamGiaDetail = {
        listSanPham: [] // Khởi tạo là một mảng rỗng
    }
    $scope.successMessage = "";
    $scope.errorMessage = "";
    $scope.currentPage = 0;
    $scope.itemsPerPage = 10; // Số sản phẩm trên mỗi trang
    $scope.totalPages = 0;
    $scope.idDanhMuc = "";
    // Lấy tất cả chi tiết sản phẩm
    $scope.getAllGiamGias = function (page) {
        $scope.currentPage = page || 0; // Nếu không có page được truyền vào, dùng trang 0
        $http.get('http://localhost:8083/giam-gia/phanTrang?page=' + $scope.currentPage)
            .then(function (response) {
                $scope.giamGias = response.data.giamGias; // Gán dữ liệu sản phẩm
                $scope.totalPages = response.data.totalPages; // Lưu tổng số trang
                $scope.pages = Array.from({length: $scope.totalPages}, (v, i) => i); // Tạo danh sách các số trang
            })
            .catch(function (error) {
                $scope.errorMessage = 'Lỗi khi lấy giảm giá: ' + error.data;
                console.error($scope.errorMessage);
            });
    };
    $scope.getAllDanhMuc = function () {
        $http.get('http://localhost:8083/danh-muc')
            .then(function (response) {
                $scope.listDanhMuc = response.data;
            })
            .catch(function (error) {
                $scope.errorMessage = 'Lỗi khi lấy danh mục: ' + error.data;
                // console.error($scope.errorMessage);
            });
    };   // Chuyển sang trang trước
    $scope.getSanPhamByDanhMuc = function (idDanhMuc) {
        $scope.idDanhMucShow = idDanhMuc;
        $http.get('http://localhost:8083/san-pham/getByDanhMuc?idDanhMuc=' + $scope.idDanhMucShow)
            .then(function (response) {
                $scope.listSanPham = response.data;
            })
            .catch(function (error) {
                $scope.errorMessage = 'Lỗi khi lấy sản phẩm qua danh mục: ' + error.data;
                console.error($scope.errorMessage);
            });
    };
    $scope.listAllSanPham = function () {
        $http.get('http://localhost:8083/san-pham')
            .then(function (response) {
                $scope.listAllSanPham = response.data;
            })
            .catch(function (error) {
                $scope.errorMessage = 'Lỗi khi lấy sản phẩm qua danh mục: ' + error.data;
                console.error($scope.errorMessage);
            });
    }
    $scope.addProduct = function () {
        // Kiểm tra sản phẩm đã chọn
        if ($scope.giamGia.selectedProduct &&
            !$scope.giamGia.selectedProducts.includes($scope.giamGia.selectedProduct) &&
            !$scope.giamGiaDetail.listSanPham.includes($scope.giamGia.selectedProduct)) { // Kiểm tra sản phẩm đã có trong danh sách cập nhật
            $scope.giamGia.selectedProducts.push($scope.giamGia.selectedProduct);
        }
        if ($scope.giamGiaDetail.selectedProduct &&
            !$scope.giamGiaDetail.listSanPham.some(product => product.id === $scope.giamGiaDetail.selectedProduct.id) &&
            !$scope.giamGia.selectedProducts.some(product => product.id === $scope.giamGiaDetail.selectedProduct.id)) {
            // Thêm sản phẩm vào listSanPham của giamGiaDetail
            $scope.giamGiaDetail.listSanPham.push($scope.giamGiaDetail.selectedProduct);
        }


        // Reset dropdown
        $scope.giamGia.selectedProduct = null;
        $scope.giamGiaDetail.selectedProduct = null;
    };

    // Hàm loại bỏ sản phẩm khỏi danh sách đã chọn
    $scope.removeProduct = function (product) {
        // Kiểm tra nếu giamGia.selectedProducts tồn tại và không rỗng
        if ($scope.giamGia.selectedProducts && $scope.giamGia.selectedProducts.length > 0) {
            const index = $scope.giamGia.selectedProducts.findIndex(p => p.id === product.id);
            if (index !== -1) {
                $scope.giamGia.selectedProducts.splice(index, 1);
            }
        }

        // Kiểm tra nếu giamGiaDetail.listSanPham tồn tại và không rỗng
        if ($scope.giamGiaDetail.listSanPham && $scope.giamGiaDetail.listSanPham.length > 0) {
            const indexDetail = $scope.giamGiaDetail.listSanPham.findIndex(p => p.id === product.id);
            if (indexDetail !== -1) {
                $scope.giamGiaDetail.listSanPham.splice(indexDetail, 1);
            }
        }
    };
    $scope.previousPage = function () {
        if ($scope.currentPage > 0) {
            $scope.getAllGiamGias($scope.currentPage - 1);
        }
    };

    // Chuyển sang trang sau
    $scope.nextPage = function () {
        if ($scope.currentPage < $scope.totalPages - 1) {
            $scope.getAllGiamGias($scope.currentPage + 1);
        }
    };

    $scope.getAllDanhMuc = function () {
        $http.get('http://localhost:8083/danh-muc')
            .then(function (response) {
                $scope.listDanhMuc = response.data;
            })
            .catch(function (error) {
                $scope.errorMessage = 'Lỗi khi lấy danh mục: ' + error.data;
                console.error($scope.errorMessage);
            });
    };

    $scope.addGiamGia = function (giamGia) {
        const formData = new FormData();
        // Thêm thông tin sản phẩm vào FormData
        formData.append('ten', giamGia.ten || '');
        formData.append('ma', giamGia.ma || '');
        formData.append('ngayBatDau', moment(giamGia.ngayBatDau).format('YYYY-MM-DDTHH:mm:ss'));
        formData.append('ngayKetThuc', moment(giamGia.ngayKetThuc).format('YYYY-MM-DDTHH:mm:ss'));
        formData.append('giaGiam', giamGia.giaGiam || 0);
        formData.append('trangThai', 1);
        formData.append('moTa', giamGia.moTa || '');
        if ($scope.giamGia.selectedProductsType === 'all') {
            $scope.giamGia.selectedProducts = angular.copy($scope.listAllSanPham);
        } else if ($scope.giamGia.selectedProductsType === 'selected') {
            // Danh sách sản phẩm đã chọn sẽ là những sản phẩm được người dùng chọn
            $scope.giamGia.selectedProducts = $scope.giamGia.selectedProducts || [];
        }

        // Lấy ID của các sản phẩm đã chọn và thêm vào FormData
        giamGia.selectedProductsIds = $scope.giamGia.selectedProducts.map(sp => sp.id);
        giamGia.selectedProductsIds.forEach(function (productId) {
            formData.append('selectedProducts', productId);
        });

        // Gửi FormData lên server
        $http.post('http://localhost:8083/giam-gia/add', formData, {
            headers: {
                'Content-Type': undefined // Cho phép browser tự động thiết lập boundary
            }
        })
            .then(function (response) {
                $('#addModal').modal('hide'); // Đóng modal thêm mới sau khi thêm thành công
                $scope.giamGia = {}; // Xóa dữ liệu form sau khi thêm
                $scope.getAllGiamGias(); // Tải lại danh sách giảm giá
                alert('Thêm giảm giá thành công!');
            })
            .catch(function (error) {
                $scope.getAllGiamGias(); // Tải lại danh sách giảm giá
                console.error('Lỗi khi thêm giảm giá:', error);
                if (error.data && error.data.message) {
                    $scope.errorMessage = 'Lỗi khi thêm giảm giá: ' + error.data.message;
                } else {
                    $scope.errorMessage = 'Lỗi không xác định: ' + JSON.stringify(error);
                }
            });
    };

    $scope.deleteGiamGia = function (ggId) {
        if (confirm("Bạn có chắc chắn muốn xóa sản phẩm này không?")) {
            $http({
                method: 'DELETE',
                url: 'http://localhost:8083/giam-gia/delete', // Đường dẫn đến API
                data: {id: ggId}, // Gửi id sản phẩm qua request body
                headers: {"Content-Type": "application/json;charset=utf-8"}
            }).then(function (response) {
                alert(response.data); // Hiển thị thông báo thành công
                // Cập nhật lại danh sách sản phẩm
                $scope.getAllGiamGias();
                alert(response.data); // Hiển thị thông báo thành công

            }, function (error) {
                $scope.getAllGiamGias();
                // alert(response.data); // Hiển thị thông báo thành công
            });
        }
    };

    $scope.updateGiamGia = function (giamGiaDetail) {
        const formData = new FormData();
        formData.append('id', giamGiaDetail.id);
        formData.append('ten', giamGiaDetail.ten || '');
        formData.append('ma', giamGiaDetail.ma || '');
        formData.append('ngayBatDau', moment(giamGiaDetail.ngayBatDau).format('YYYY-MM-DDTHH:mm:ss'));
        formData.append('ngayKetThuc', moment(giamGiaDetail.ngayKetThuc).format('YYYY-MM-DDTHH:mm:ss'));
        formData.append('giaGiam', giamGiaDetail.giaGiam || 0);
        formData.append('trangThai', 1);
        formData.append('moTa', giamGiaDetail.moTa || '');

        // Determine selected products based on type
        if ($scope.giamGiaDetail.selectedProductsType === 'all') {
            $scope.giamGiaDetail.selectedProducts = angular.copy($scope.listAllSanPham);
        }
        if ($scope.giamGiaDetail.selectedProductsType === 'selected') {
            $scope.giamGiaDetail.selectedProducts = $scope.giamGiaDetail.listSanPham; // Sử dụng listSanPham
        }

        // Map selected products' IDs and add them to FormData
        giamGiaDetail.selectedProductsIds = $scope.giamGiaDetail.selectedProducts.map(sp => sp.id);
        console.log($scope.giamGiaDetail.listSanPham);
        giamGiaDetail.selectedProductsIds.forEach(function (productId) {
            formData.append('selectedProducts', productId);
        });

        $http.put('http://localhost:8083/giam-gia/update', formData, {
            transformRequest: angular.identity,
            headers: {
                'Content-Type': undefined
            }
        })
            .then(function (response) {
                $scope.getAllGiamGias(0);
                $('#userForm').modal('hide');
                alert('Cập nhật thành công!');
            })
            .catch(function (error) {
                $scope.getAllGiamGias(0);
                alert('Cập nhật thành công!');
                $scope.errorMessage = error.data && error.data.message
                    ? 'Lỗi khi cập nhật: ' + error.data.message
                    : 'Lỗi không xác định: ' + JSON.stringify(error);
            });
    };


    $scope.clearForm = function () {
        $scope.giamGiaDetail = {}; // Xóa dữ liệu chi tiết sản phẩm
        $scope.giamGia = {};
        $('#userForm').modal('hide'); // Đóng modal
        $('#addModal').modal('hide'); // Đóng modal
    };
    // Xem chi tiết sản phẩm
    $scope.viewDetail = function (ggId) {
        const giamGia = $scope.giamGias.find(function (g) {
            return g.id === ggId; // Sửa lỗi từ 'gg' sang 'g' trong hàm find
        });

        if (giamGia) {
            // Chuyển đổi các trường số thành kiểu số nếu cần
            $scope.giamGiaDetail = {
                ...giamGia,
                id: giamGia.id,
                ngayBatDau: new Date(giamGia.ngayBatDau), // Chuyển đổi thành đối tượng Date
                ngayKetThuc: new Date(giamGia.ngayKetThuc),
                ngayTao: new Date(giamGia.ngayTao),
                giaGiam: Number(giamGia.giaGiam),
                listSanPham: giamGia.listSanPham
            };
            console.log($scope.giamGiaDetail);
            // Mở modal để hiển thị chi tiết giảm giá (nếu cần)
            // $('#readData').modal('show'); // Đảm bảo modal có ID là 'readData'
        } else {
            console.error('Không tìm thấy giảm giá với ID:', ggId);
        }
    };
    // Khởi tạo
    $scope.getAllGiamGias(0);
    $scope.getAllDanhMuc();
    $scope.listAllSanPham();
};