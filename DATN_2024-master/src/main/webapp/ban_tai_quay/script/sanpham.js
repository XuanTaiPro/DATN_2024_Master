window.sanPhamCtrl = function ($scope, $http) {

    // Khởi tạo sản phẩm
    $scope.product = {};
    $scope.successMessage = "";
    $scope.errorMessage = "";
    $scope.currentPage = 0;
    $scope.itemsPerPage = 10; // Số sản phẩm trên mỗi trang
    $scope.totalPages = 0;
    $scope.productDetail = $scope.productDetail || {};

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





    $scope.checkDuplicateName = function() {
        if (!$scope.isTenValid()) {
            $scope.isDuplicateName = false;
            return;
        }
        $http.post('http://localhost:8083/san-pham/checkTrung', { tenSP: $scope.product.tenSP.trim() })
            .then(function(response) {
                $scope.isDuplicateName = response.data.isDuplicate;
            })
            .catch(function(error) {
                console.error('Lỗi khi kiểm tra trùng tên:', error);
            });
    };
    const specialCharRegex = /^[a-zA-ZÀ-ỹ\s]+$/;

    $scope.isTenValid = function() {
        if (!$scope.product.tenSP || $scope.product.tenSP.trim() === '') {
            return false;
        } else if (!specialCharRegex.test($scope.product.tenSP.trim())) {
            return false;
        }
        return true;
    };
    $scope.isTenKyTu = function() {
        console.log($scope.product.tenSP )
        if (!$scope.product.tenSP || $scope.product.tenSP .trim() === '') {
            return false;
        }
        return $scope.product.tenSP .trim().length <= 3|| $scope.product.tenSP.trim().length >= 255;
    };

    $scope.isTPKyTu = function() {
        console.log($scope.product.thanhPhan )
        if (!$scope.product.thanhPhan || $scope.product.thanhPhan .trim() === '') {
            return false;
        }
        return $scope.product.thanhPhan .trim().length <= 3|| $scope.product.thanhPhan.trim().length >= 255;
    };
    $scope.isCDKyTu = function() {
        console.log($scope.product.congDung )
        if (!$scope.product.congDung || $scope.product.congDung .trim() === '') {
            return false;
        }
        return $scope.product.congDung .trim().length <= 3|| $scope.product.congDung.trim().length >= 255;
    };
    $scope.isHDSDKyTu = function() {
        console.log($scope.product.hdsd )
        if (!$scope.product.hdsd || $scope.product.hdsd .trim() === '') {
            return false;
        }
        return $scope.product.hdsd .trim().length <= 3|| $scope.product.hdsd.trim().length >= 255;
    };
    // $scope.isTPValid = function() {
    //     return $scope.product.thanhPhan && $scope.product.thanhPhan.trim() !== '';
    // };


    $scope.isDanhMuc = function() {
        return $scope.product.idDanhMuc ;
    };

    // $scope.isCDValid = function() {
    //     return $scope.product.congDung && $scope.product.congDung.trim() !== '';
    // };
    //
    // $scope.ishdsdValid = function() {
    //     return $scope.product.hdsd && $scope.product.hdsd.trim() !== '';
    // };

    let tuoiMax = document.querySelector('#maxAge,#tuoiMaxUD')
    tuoiMax.addEventListener('input',function (){
        tuoiMax.value = tuoiMax.value.replace(/\D/g, '')
        if(tuoiMin.value>tuoiMax.value){
            tuoiMinErrors.style.display='block'
        }else{
            tuoiMinErrors.style.display='none'
        }
        console.log(tuoiMax.value)
    })


    let tuoiMinErrors =document.querySelector('#tMError,#ErrorTuoiUD')
    let tuoiMin = document.querySelector('#minAge,#tuoiMinUD')
    tuoiMin.addEventListener('input',function (){
        tuoiMin.value = tuoiMin.value.replace(/\D/g, '')
        if(tuoiMin.value>tuoiMax.value){
            tuoiMinErrors.style.display='block'
        }else{
            tuoiMinErrors.style.display='none'
        }
        console.log(tuoiMin.value + "-----"+ tuoiMax.value)
    })



    $scope.ages = {
        tuoiMin: null,
        tuoiMax: null
    };
    $scope.isAgeInvalid = function() {
        return $scope.ages.tuoiMin && $scope.ages.tuoiMax && $scope.ages.tuoiMin > $scope.ages.tuoiMax;
    };
    $scope.isAgeGreaterThan100 = function() {
        return ( $scope.ages.tuoiMax<0&&$scope.ages.tuoiMin<0&&$scope.ages.tuoiMax > 100);
    };
    $scope.isAgeGreaterThan1001 = function() {
        return ( $scope.ages.tuoiMin<0&&$scope.ages.tuoiMin > 100);
    };



    $scope.formSubmitted = false;
    $scope.addProduct = function (product) {


        $scope.formSubmitted = true;

        const formData = new FormData();
        formData.append('tenSP', product.tenSP || '');
        formData.append('thanhPhan', product.thanhPhan || '');
        formData.append('congDung', product.congDung || '');
        formData.append('tuoiMin', product.tuoiMin || 0);
        formData.append('tuoiMax', product.tuoiMax || 0);
        formData.append('hdsd', product.hdsd);
        formData.append('moTa', product.moTa || '');
        formData.append('idDanhMuc', product.idDanhMuc || '');
        formData.append('trangThai', 1);
        formData.append('idThuongHieu', "1A227A58");

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
            idThuongHieu: "1A227A58"
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
        showConfirm("Bạn có muốn ngưng sản phẩm này không?",()=>{
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
            });})

    };
    $scope.activateProduct = function (productId) {
       showConfirm("Bạn có muốn kích hoạt lại sản phẩm này không?",()=>{
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
        });})

    };
    $scope.checkDuplicateNameUD = function() {
        if (!$scope.isTenValidUD()) {
            $scope.isDuplicateName = false;
            return;
        }

        const requestData = {
            tenSP: $scope.productDetail.tenSP.trim(),
            id: $scope.productDetail.id
        };

        $http.post('http://localhost:8083/san-pham/checkTrungUD', requestData)
            .then(function(response) {
                $scope.isDuplicateName = response.data.isDuplicate;
            })
            .catch(function(error) {
                console.error('Lỗi khi kiểm tra trùng tên:', error);
            });
    };
    $scope.isTenValidUD = function() {
        if (!$scope.productDetail.tenSP || $scope.productDetail.tenSP.trim() === '') {
            return false;
        } else if (!specialCharRegex.test($scope.productDetail.tenSP.trim())) {
            return false;
        }
        return true;
    };
    $scope.isTPValidUD = function() {
        return $scope.productDetail.thanhPhan && $scope.productDetail.thanhPhan.trim() !== '';
    };

    $scope.isCDValidUD = function() {
        return $scope.productDetail.congDung && $scope.productDetail.congDung.trim() !== '';
    };

    $scope.ishdsdValidUD = function() {
        return $scope.productDetail.hdsd && $scope.productDetail.hdsd.trim() !== '';
    };

    $scope.isTenKyTuUD = function() {
        console.log($scope.productDetail.tenSP )
        if (!$scope.productDetail.tenSP || $scope.productDetail.tenSP .trim() === '') {
            return false;
        }
        return $scope.productDetail.tenSP .trim().length <= 3|| $scope.productDetail.tenSP.trim().length >= 255;
    };

    $scope.isTPKyTuUD = function() {
        console.log($scope.productDetail.thanhPhan )
        if (!$scope.productDetail.thanhPhan || $scope.productDetail.thanhPhan .trim() === '') {
            return false;
        }
        return $scope.productDetail.thanhPhan .trim().length <= 3|| $scope.productDetail.thanhPhan.trim().length >= 255;
    };
    $scope.isCDKyTuUD = function() {
        console.log($scope.productDetail.congDung )
        if (!$scope.productDetail.congDung || $scope.productDetail.congDung .trim() === '') {
            return false;
        }
        return $scope.productDetail.congDung .trim().length <= 3|| $scope.productDetail.congDung.trim().length >= 255;
    };
    $scope.isHDSDKyTuUD = function() {
        console.log($scope.productDetail.hdsd )
        if (!$scope.productDetail.hdsd || $scope.productDetail.hdsd .trim() === '') {
            return false;
        }
        return $scope.productDetail.hdsd .trim().length <= 3|| $scope.productDetail.hdsd.trim().length >= 255;
    };

    $scope.updateProduct = function () {
        $scope.formSubmitted = true;

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
        formData.append('idThuongHieu', "1A227A58");

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
            idThuongHieu: "1A227A58"
        };


        $http.put('http://localhost:8083/san-pham/update', data)
            .then(function (response) {
                $scope.getAllProducts();
                $('#userForm').modal('hide');
                showSuccessAlert(response.data);
            })
            .catch(function (error) {
                showDangerAlert("Thất bại rồi!")
                // $scope.getAllProducts(); // Tải lại danh sách sản phẩm
            });
    };
    $scope.clearForm = function () {
        $scope.productDetail = {}; // Xóa dữ liệu chi tiết sản phẩm
        $scope.product = {};
        $('#userForm').modal('hide'); // Đóng modal
        // $('#productModal').modal('hide'); // Đóng modal
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
        console.log($scope.productDetail);
    };
    // Khởi tạo
    $scope.getAllProducts(0);
    $scope.getAllDanhMuc();
};