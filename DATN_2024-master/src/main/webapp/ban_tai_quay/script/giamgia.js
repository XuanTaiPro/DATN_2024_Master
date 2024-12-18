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
    $scope.filters = {
        searchText: '',
        trangThai: '',
        ngayBatDau: '',
        ngayKetThuc: ''
    };
    // Lấy tất cả chi tiết sản phẩm
    $scope.getAllGiamGias = function (page) {
        $scope.currentPage = page || 0; // Nếu không có page được truyền vào, dùng trang 0

        let params = {
            page: $scope.currentPage,
            searchText: $scope.filters.searchText || '', // Mặc định là chuỗi rỗng nếu không có giá trị
            trangThai: $scope.filters.trangThai || '', // Mặc định là chuỗi rỗng nếu không có giá trị
            ngayBatDau: $scope.filters.ngayBatDau ? new Date($scope.filters.ngayBatDau).toISOString() : '', // Convert sang ISO format
            ngayKetThuc: $scope.filters.ngayKetThuc ? new Date($scope.filters.ngayKetThuc).toISOString() : '' // Convert sang ISO format
        };

        $http.get('http://localhost:8083/giam-gia/phanTrang', { params: params })
            .then(function (response) {
                if (response.data.giamGias && response.data.giamGias.length > 0) {
                    $scope.giamGias = response.data.giamGias;
                    $scope.totalPages = response.data.totalPages;
                    $scope.pages = Array.from({ length: $scope.totalPages }, (v, i) => i);
                } else {
                    $scope.giamGias = [];
                    $scope.errorMessage = 'Không có dữ liệu giảm giá!';
                }
            })
            .catch(function (error) {
                $scope.errorMessage = 'Lỗi khi lấy giảm giá: ' + error.data;
                console.error(error);
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
                console.log($scope.listAllSanPham.length);
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



    $scope.isTenValid = function() {
        return !$scope.giamGia.ten || $scope.giamGia.ten.trim() == '';
    };

    $scope.isNgayBatDauValid = function(ngayBatDau) {
        const now = moment().startOf('day');
        const batDau = moment(ngayBatDau, 'YYYY-MM-DD', true);
        return batDau.isValid() && batDau.isSameOrAfter(now);
    };
    $scope.isNgayKetThucValid = function(ngayKetThuc, ngayBatDau) {

        const now = moment().startOf('day'); // Ngày hiện tại
        const ketThuc = moment(ngayKetThuc, 'YYYY-MM-DD', true);
        const batDau = moment(ngayBatDau, 'YYYY-MM-DD', true);

        return (
            ketThuc.isValid() &&
            ketThuc.isAfter(now) &&
            (!ngayBatDau || ketThuc.isAfter(batDau))
        );
    };


    $scope.isGiaGiamValid = function(giaGiam) {
        return giaGiam !== undefined && giaGiam >= 0 && giaGiam<=100;
    };

    let giaGiam = document.querySelector('#giaGiam')
    giaGiam.addEventListener('input',function (){
        giaGiam.value = giaGiam.value.replace(/\D/g, '')
        console.log(giaGiam.value)
    })
    $scope.isMoTaValid = function(moTa) {
        return moTa && moTa.trim() !== '';
    };

    $scope.giamGia = {
        selectedProductsType: '', // Không chọn mặc định
        selectedProducts: []
    };

    $scope.isProductTypeValid = function() {
        return $scope.giamGia.selectedProductsType === 'all' ||
            ($scope.giamGia.selectedProductsType === 'selected' && $scope.giamGia.selectedProducts.length > 0);
    };

    $scope.updateProductSelection = function() {
        if ($scope.giamGia.selectedProductsType === 'all') {
            // Nếu chọn "Tất cả sản phẩm", xóa danh sách sản phẩm được chọn
            $scope.giamGia.selectedProducts = [];
        }

    };
    $scope.formSubmitted = false;

    $scope.addGiamGia = function (giamGia) {

        $scope.formSubmitted = true;

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
                $scope.giamGia.selectedProducts = $scope.giamGia.selectedProducts || [];
            }
            giamGia.selectedProductsIds = $scope.giamGia.selectedProducts.map(sp => sp.id);
            giamGia.selectedProductsIds.forEach(function (productId) {formData.append('selectedProducts', productId);})


            // Gửi FormData lên server
            $http.post('http://localhost:8083/giam-gia/add', formData, {
                headers: {
                    'Content-Type': undefined // Cho phép browser tự động thiết lập boundary
                }
            })
                .then(function (response) {
                    $('#addModal').modal('hide');
                    $scope.giamGia = {};
                    $scope.getAllGiamGias();
                    $scope.clearForm();
                    showSuccessAlert("Thêm giảm giá thành công!");
                })
                .catch(function (error) {
                    $scope.getAllGiamGias(); // Tải lại danh sách giảm giá
                    showDangerAlert("Thêm giảm giá thất bại!");
                    console.error('Lỗi khi thêm giảm giá:', error);
                    if (error.data && error.data.message) {
                        $scope.errorMessage = 'Lỗi khi thêm giảm giá: ' + error.data.message;
                    } else {
                        $scope.errorMessage = 'Lỗi không xác định: ' + JSON.stringify(error);
                    }
                });

    };

    $scope.deleteGiamGia = function (ggId) {
        showConfirm("Bạn có muốn ngưng giảm giá này không?",()=>{
            $http({
                method: 'DELETE',
                url: 'http://localhost:8083/giam-gia/delete', // Đường dẫn đến API
                data: {id: ggId}, // Gửi id sản phẩm qua request body
                headers: {"Content-Type": "application/json;charset=utf-8"}
            }).then(function (response) {
                showSuccessAlert(response.data); // Hiển thị thông báo thành công
                // Cập nhật lại danh sách sản phẩm
                $scope.getAllGiamGias($scope.currentPage);


            }, function (error) {
                $scope.getAllGiamGias($scope.currentPage);
                showDangerAlert("Cập nhật trạng thái thất bại!");
                // alert(response.data); // Hiển thị thông báo thành công
            });
        });
    };
    $scope.activateGiamGia = function (ggId) {
        showConfirm("Bạn có muốn kích hoạt lại giảm giá này không?",()=>{
            $http({
                method: 'DELETE',
                url: 'http://localhost:8083/giam-gia/active', // Đường dẫn đến API
                data: {id: ggId}, // Gửi id sản phẩm qua request body
                headers: {"Content-Type": "application/json;charset=utf-8"}
            }).then(function (response) {
                showSuccessAlert(response.data); // Hiển thị thông báo thành công
                // Cập nhật lại danh sách sản phẩm
                $scope.getAllGiamGias($scope.currentPage);


            }, function (error) {
                $scope.getAllGiamGias($scope.currentPage);
                showDangerAlert("Cập nhật trạng thái thất bại!");
                // alert(response.data); // Hiển thị thông báo thành công
            });
        });
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
        if ($scope.giamGiaDetail.ten.trim().length > 255) {
            return false;
        }
        return true;
    };


    $scope.updateGiamGia = function (giamGiaDetail) {
        $scope.formSubmitted = true;
            const formData = new FormData();
            formData.append('id', giamGiaDetail.id);
            formData.append('ten', giamGiaDetail.ten || '');
            formData.append('ma', giamGiaDetail.ma || '');
            formData.append('ngayBatDau', moment(giamGiaDetail.ngayBatDau).format('YYYY-MM-DDTHH:mm:ss'));
            formData.append('ngayKetThuc', moment(giamGiaDetail.ngayKetThuc).format('YYYY-MM-DDTHH:mm:ss'));
            formData.append('giaGiam', giamGiaDetail.giaGiam || 0);
            formData.append('trangThai', 1);
            formData.append('moTa', giamGiaDetail.moTa || '');
            if ($scope.giamGiaDetail.selectedProductsType === 'all') {
                $scope.giamGiaDetail.selectedProducts = angular.copy($scope.listAllSanPham);
            }
            if ($scope.giamGiaDetail.selectedProductsType === 'selected') {
                $scope.giamGiaDetail.selectedProducts = $scope.giamGiaDetail.listSanPham; // Sử dụng listSanPham
            }
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
                    $scope.clearForm();
                    $scope.ErrorForm();
                    showSuccessAlert("Cập nhật giảm giá thành công!");
                })
                .catch(function (error) {
                    $scope.getAllGiamGias(0);
                    showDangerAlert("Cập nhật giảm giá thất bại!");
                    $scope.errorMessage = error.data && error.data.message
                        ? 'Lỗi khi cập nhật: ' + error.data.message
                        : 'Lỗi không xác định: ' + JSON.stringify(error);
                });
    };
    $('#addModal').on('hidden.bs.modal', function () {
        $scope.clearForm();
    });
    $scope.isSubmitted = false;

    $scope.ErrorForm = function () {
        // $scope.isTenValid= true;
        // $scope.isNgayKetThucValid= true;
        // $scope.isNgayBatDauValid= true;
        // $scope.isGiaGiamValid= true;
        $scope.formSubmitted = false;
        $scope.addGiamGia = true;
    };
    $scope.clearForm = function () {
        $scope.giamGiaDetail = {};
        $scope.giamGia = {};
        // $scope.isTenValid= true;
        // $scope.isNgayKetThucValid= true;
        // $scope.isNgayBatDauValid= true;
        // $scope.isGiaGiamValid= true;
        // $scope.formSubmitted = false;
        // $scope.addGiamGia=false;
        $scope.addGiamGia = true;
        $('#userForm').modal('hide'); // Đóng modal
        // $('#addModal').modal('hide'); // Đóng modal
    };
    // Xem chi tiết sản phẩm
    $scope.viewDetail = function (ggId) {
        const giamGia = $scope.giamGias.find(function (g) {
            return g.id === ggId;
        });

        if (giamGia) {
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
            console.log($scope.giamGiaDetail.listSanPham.length);  // Kiểm tra độ dài của listSanPham trong giamGiaDetail
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