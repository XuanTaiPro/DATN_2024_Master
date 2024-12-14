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
        const data = {
            tenSP: product.tenSP || '',
            thanhPhan: product.thanhPhan || '',
            congDung: product.congDung || '',
            tuoiMin: product.tuoiMin || 0,
            tuoiMax: product.tuoiMax || 0,
            hdsd: product.hdsd,
            moTa: product.moTa || '',
            idDanhMuc: product.idDanhMuc || '',
            trangThai: 1,
            idThuongHieu: "95B16137"
        };

        $http.post('http://localhost:8083/san-pham/add', data,
        )
            .then(function (response) {
                $('#productModal').modal('hide');
                $scope.product = {};
                $scope.getAllProducts();
                console.log(response.data); // "Thêm sản phẩm thành công!"
                showSuccessAlert(response.data);            })
            .catch(function (error) {
                showDangerAlert("Thất bại rồi!");
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
        // showConfirm("Bạn có muốn ngưng sản phẩm này không?",()=>{
            $http({
                method: 'DELETE',
                url: 'http://localhost:8083/san-pham/delete', // Đường dẫn đến API
                data: {id: productId}, // Gửi id sản phẩm qua request body
                headers: {"Content-Type": "application/json;charset=utf-8"}
            }).then(function (response) {
                $scope.getAllProducts($scope.currentPage);
                showSuccessAlert(response.data);
            }, function (error) {
                $scope.getAllProducts();
                showDangerAlert("Thất bại!");
            });

    };
    $scope.activateProduct = function (productId) {
       // showConfirm("Bạn có muốn kích hoạt lại sản phẩm này không?",()=>{
        $http({
            method: 'PUT',
            url: 'http://localhost:8083/san-pham/activateProduct', // Đường dẫn đến API
            data: {id: productId}, // Gửi id sản phẩm qua request body
            headers: {"Content-Type": "application/json;charset=utf-8"}
        }).then(function (response) {
            $scope.getAllProducts($scope.currentPage);
            showSuccessAlert(response.data);
        }, function (error) {
            $scope.getAllProducts();
            showDangerAlert("Thất bại!");
        });

    };

    $scope.updateProduct = function () {
        const data = {
            id: $scope.productDetail.id || '',
            tenSP: $scope.productDetail.tenSP || '',
            thanhPhan: $scope.productDetail.thanhPhan || '',
            congDung: $scope.productDetail.congDung || '',
            tuoiMin: $scope.productDetail.tuoiMin || 0,
            tuoiMax: $scope.productDetail.tuoiMax || 0,
            hdsd: $scope.productDetail.hdsd || '',
            moTa: $scope.productDetail.moTa || '',
            idDanhMuc: $scope.productDetail.idDanhMuc || '',
            trangThai: 1,
            idThuongHieu: "95B16137"
        };

        $http.put('http://localhost:8083/san-pham/update', data)
            .then(function (response) {
                $scope.getAllProducts(); // Tải lại danh sách sản phẩm
                $('#userForm').modal('hide'); // Đóng modal sau khi cập nhật
                showSuccessAlert(response.data);
            })
            .catch(function (error) {
                showDangerAlert("Thất bại rồi!")
                $scope.getAllProducts(); // Tải lại danh sách sản phẩm
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