window.sanPhamCtrl = function ($scope, $http) {

    // Khởi tạo sản phẩm
    $scope.product = {};
    $scope.successMessage = "";
    $scope.errorMessage = "";
    $scope.currentPage = 0;
    $scope.itemsPerPage = 10; // Số sản phẩm trên mỗi trang
    $scope.totalPages = 0;
    $scope.ageRanges = [
        { label: "Dưới 18", min: null, max: 18 },
        { label: "18 - 25", min: 18, max: 25 },
        { label: "26 - 35", min: 26, max: 35 },
        { label: "36 - 50", min: 36, max: 50 },
        { label: "Trên 50", min: 50, max: null }
    ];

    $scope.filters = {
        searchText: "",
        tenDanhMuc: null,
        trangThai: null,
        tuoiRange: null // Sẽ lưu tùy chọn khoảng tuổi
    };
    $scope.getAllProducts = function(page) {
        $scope.currentPage = page >= 0 ? page : 0;

        // Lấy giá trị min và max từ tùy chọn tuổi
        const selectedRange = $scope.filters.tuoiRange || {};
        const tuoiMin = selectedRange.min;
        const tuoiMax = selectedRange.max;

        // Chuẩn bị tham số gửi lên server
        const params = {
            page: $scope.currentPage,
            searchText: $scope.filters.searchText || null,
            tenDanhMuc: $scope.filters.tenDanhMuc || null,
            trangThai: $scope.filters.trangThai || null,
            tuoiMin: tuoiMin,
            tuoiMax: tuoiMax
        };
        console.log(params);
        // Gửi request
        $http({
            method: 'GET',
            url: 'http://localhost:8083/san-pham/phanTrang',
            params: params
        }).then(function(response) {
            $scope.products = response.data.products || [];
            $scope.totalPages = response.data.totalPages || 0;
            $scope.pages = Array.from({ length: $scope.totalPages }, (_, i) => i);
        }).catch(function(error) {
            $scope.errorMessage = 'Lỗi khi lấy sản phẩm: ' + (error.data || 'Không rõ lỗi');
            console.error("Chi tiết lỗi:", error);
        });

    };

    // Chuyển sang trang trước
    $scope.previousPage = function () {
        if ($scope.currentPage > 0) {
            $scope.getAllProducts($scope.currentPage - 1);
        }
    };

    // Chuyển sang trang sau
    $scope.nextPage = function () {
        if ($scope.currentPage < $scope.totalPages - 1) {
            $scope.getAllProducts($scope.currentPage + 1);
        }
    };

    $scope.getAllDanhMuc=function(){

        $http.get('http://localhost:8083/danh-muc')
            .then(function (response) {
                $scope.listDanhMuc = response.data;
            })
            .catch(function (error) {
                $scope.errorMessage = 'Lỗi khi lấy danh mục: ' + error.data;
                console.error($scope.errorMessage);
            });
    };

    $scope.addProduct = function (product) {
        const formData = new FormData();
        // Thêm thông tin sản phẩm vào FormData
        formData.append('tenSP', product.tenSP || '');
        formData.append('thanhPhan', product.thanhPhan || '');
        formData.append('congDung', product.congDung || '');
        formData.append('tuoiMin', product.tuoiMin || 0);
        formData.append('tuoiMax', product.tuoiMax || 0);
        formData.append('hdsd', product.hdsd);
        formData.append('moTa', product.moTa || '');
        formData.append('idDanhMuc', product.idDanhMuc || '');
        formData.append('trangThai', 1);
        formData.append('idThuongHieu', "95B16137");

        console.log('Thông tin sản phẩm:', product);
        for (var pair of formData.entries()) {
            console.log(pair[0] + ': ' + pair[1]);
        }

        // Gửi FormData lên server
        $http.post('http://localhost:8083/san-pham/add', formData, {
            headers: {
                'Content-Type': undefined // Cho phép browser tự động thiết lập boundary
            }
        })
            .then(function (response) {
                $('#productModal').modal('hide');
                $scope.product = {};
                $scope.getAllProducts();
                alert('Thêm sản phẩm thành công!');
            })
            .catch(function (error) {
                $scope.getAllProducts();
                console.error('Lỗi:', error);
                if (error.data && error.data.message) {
                    $scope.errorMessage = 'Lỗi khi thêm sản phẩm: ' + error.data.message;
                } else {
                    $scope.errorMessage = 'Lỗi không xác định: ' + JSON.stringify(error);
                }
            });
    };
    $scope.deleteProduct = function (productId) {
        if (confirm("Bạn có chắc chắn muốn xóa sản phẩm này không?")) {
            $http({
                method: 'DELETE',
                url: 'http://localhost:8083/san-pham/delete', // Đường dẫn đến API
                data: {id: productId}, // Gửi id sản phẩm qua request body
                headers: {"Content-Type": "application/json;charset=utf-8"}
            }).then(function (response) {
                alert(response.data); // Hiển thị thông báo thành công
                // Cập nhật lại danh sách sản phẩm
                $scope.getAllProducts();
                alert(response.data); // Hiển thị thông báo thành công

            }, function (error) {
                $scope.getAllProducts();
                // alert(response.data); // Hiển thị thông báo thành công
            });
        }
    };

    $scope.updateProduct = function () {
        const formData = new FormData();
        formData.append('id', $scope.productDetail.id || '');
        formData.append('tenSP', $scope.productDetail.tenSP || '');
        formData.append('thanhPhan', $scope.productDetail.thanhPhan || '');
        formData.append('congDung', $scope.productDetail.congDung || '');
        formData.append('tuoiMin', $scope.productDetail.tuoiMin || 0);
        formData.append('tuoiMax', $scope.productDetail.tuoiMax || 0);
        formData.append('hdsd', $scope.productDetail.hdsd);
        formData.append('moTa', $scope.productDetail.moTa || '');
        formData.append('idDanhMuc', $scope.productDetail.idDanhMuc || '');
        formData.append('trangThai', 1);
        formData.append('idThuongHieu', "95B16137");

        // Gửi FormData lên server
        $http.put('http://localhost:8083/san-pham/update', formData, {
            transformRequest: angular.identity,
            headers: {
                'Content-Type': undefined // Cho phép browser tự động thiết lập boundary
            }
        })
            .then(function (response) {
                $scope.getAllProducts(); // Tải lại danh sách sản phẩm
                $('#userForm').modal('hide'); // Đóng modal sau khi cập nhật
                alert('Cập nhật thành công!');
            })
            .catch(function (error) {
                $scope.getAllProducts(); // Tải lại danh sách sản phẩm
                $scope.errorMessage = error.data && error.data.message ? 'Lỗi khi cập nhật sản phẩm: ' + error.data.message : 'Lỗi không xác định: ' + JSON.stringify(error);
            });
    };
    $scope.clearForm = function () {
        $scope.productDetail = {}; // Xóa dữ liệu chi tiết sản phẩm
        $scope.product = {};
        $('#userForm').modal('hide'); // Đóng modal
        $('#productModal').modal('hide'); // Đóng modal
    };
    // Xem chi tiết sản phẩm
    $scope.viewDetail = function (productId) {
        const product = $scope.products.find(function (p) {
            return p.id === productId;
        });

        if (product) {
            // Chuyển đổi các trường số thành kiểu số
            $scope.productDetail = {
                ...product,
            };
            // $('#readData').modal('show');
        } else {
            console.error('Không tìm thấy sản phẩm với ID:', productId);
        }
    };
    // Khởi tạo
    $scope.getAllProducts(0);
    $scope.getAllDanhMuc();
};