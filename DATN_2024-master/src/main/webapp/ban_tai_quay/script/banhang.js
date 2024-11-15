window.banhangCtrl = function ($scope, $http, $document) {
    $scope.tabs = []; // Khởi tạo danh sách tab
    $scope.selectedTab = 0; // Chỉ số tab đang chọn
    $scope.searchText = ""; // Khởi tạo biến tìm kiếm
    $scope.page = 0; // Initial page
    $scope.ctsps = {};
    $scope.currentPage = 0;
    $scope.itemsPerPage = 10; // Number of items per page
    $scope.totalPages = 0;
    $scope.sanPhams = []; // Nếu không có từ khóa tìm kiếm, xóa danh sách sản phẩm
    $scope.searchResultsVisible = false;

    // Hàm thêm tab mới cho hóa đơn
    $scope.addInvoiceTab = function () {
        const formData = new FormData();
        formData.append('idNV', '40E70DA8'); // Thay đổi theo nhu cầu

        // Gọi API để thêm hóa đơn
        $http.post('http://localhost:8083/hoadon/add', formData, {
            transformRequest: angular.identity,
            headers: { 'Content-Type': undefined }
        })
            .then(function(response) {
                // Kiểm tra xem hóa đơn đã được thêm thành công
                alert('Thêm hóa đơn thành công!');

                // Gọi lại API để lấy danh sách hóa đơn đã được sắp xếp
                $scope.getHDTaiQuay(); // Lấy lại danh sách hóa đơn mới nhất
            })
            .catch(function(error) {
                console.error('Lỗi:', error);
                alert('Lỗi khi thêm hóa đơn: ' + (error.data && error.data.message ? error.data.message : 'Không xác định'));
            });
    };

    // Hàm lấy hóa đơn (gọi khi khởi tạo controller)
    $scope.getHDTaiQuay = function () {
        $http.get('http://localhost:8083/hoadon/getHDTaiQuay')
            .then(function(response) {
                console.log(response.data); // Kiểm tra dữ liệu trả về
                response.data.forEach((hoaDon) => {
                    const newTabTitle = 'Hóa Đơn ' + hoaDon.maHD; // Tạo tên tab cho mỗi hóa đơn
                    const newTab = {
                        title: newTabTitle,
                        hoaDons: hoaDon.items,
                        idHD: hoaDon.id || [] // Sử dụng idHD thay vì $scope.idHD
                    };                    // Kiểm tra xem tab đã tồn tại hay chưa
                    if (!$scope.tabs.find(tab => tab.title === newTabTitle)) {
                        $scope.tabs.push(newTab); // Thêm tab mới vào mảng nếu chưa tồn tại
                    }
                });
                // Tự động chọn tab mới nhất được thêm
                if ($scope.tabs.length > 0) {
                    $scope.selectedTab = $scope.tabs.length - 1; // Chọn tab cuối cùng
                }
            })
            .catch(function(error) {
                console.error('Lỗi khi lấy hóa đơn tại quầy:', error.data);
            });
    };
    $scope.getCTSPByIdHD = function (idHD, page) {
        $scope.currentPage = page || 0; // Mặc định trang 0 nếu không có trang nào được cung cấp
        $http.get('http://localhost:8083/chitiethoadon/getCTHD?page=' + $scope.currentPage + '&idHD=' + idHD)
            .then(function (response) {
                $scope.cthds = response.data.cthds; // Dữ liệu chi tiết hóa đơn
                $scope.totalPages = response.data.totalPages; // Tổng số trang
                $scope.pages = Array.from({ length: $scope.totalPages }, (v, i) => i); // Mảng trang
            })
            .catch(function (error) {
                $scope.errorMessage = 'Lỗi khi lấy chi tiết hóa đơn: ' + (error.data?.message || JSON.stringify(error.data));
            });
    };
    // Hàm chọn tab
    $scope.selectTab = function(index) {
        $scope.selectedTab = index;
        const selectedTab = $scope.tabs[index];
        const selectedIdHD = selectedTab.idHD;
        console.log($scope.selectedIdHD)// Lấy mã hóa đơn của tab
        $scope.getCTSPByIdHD(selectedIdHD, 0); // Gọi API để lấy chi tiết hóa đơn
        selectedTab.currentPage = 0; // Đặt lại trang khi chọn lại tab
    };

    // Hàm đóng tab
    $scope.closeTab = function (index) {
        $scope.tabs.splice(index, 1); // Xóa tab theo chỉ số
        if ($scope.selectedTab >= index) {
            $scope.selectedTab = Math.max(0, $scope.selectedTab - 1); // Chọn lại tab trước đó hoặc tab đầu tiên
        }
        $scope.selectTab(index);
    };
    $scope.filterSanPham = function () {
        if ($scope.searchText) {
            $http.get('http://localhost:8083/san-pham/getByTenSP?tenSP=' + $scope.searchText)
                .then(function (response) {
                    $scope.sanPhams = response.data;
                    $scope.searchResultsVisible = true; // Hiện kết quả tìm kiếm
                    console.log($scope.sanPhams);
                })
                .catch(function (error) {
                    console.error("Lỗi khi tìm kiếm sản phẩm:", error);
                });
        } else {
            $scope.sanPhams = []; // Nếu không có từ khóa tìm kiếm, xóa danh sách sản phẩm
        }
    };
    $document.on('click', function (event) {
        var searchResultsElement = angular.element(document.querySelector('.search-results'));
        var inputElement = angular.element(document.querySelector('input[ng-model="searchText"]'));

        // Kiểm tra xem click có phải ngoài div kết quả tìm kiếm hoặc ngoài ô tìm kiếm không
        if (!searchResultsElement[0].contains(event.target) && !inputElement[0].contains(event.target)) {
            $scope.$apply(function() {
                $scope.searchResultsVisible = false; // Ẩn kết quả tìm kiếm
            });
        }
    });
    $scope.updateTotal = function(cthd) {
        const formData = new FormData();
        formData.append('soLuong', cthd.soLuong);
        formData.append('ghiChu', cthd.ghiChu);

        $http.put('http://localhost:8083/chitiethoadon/updateSoLuong?id=' + cthd.id, formData, {
            headers: { 'Content-Type': undefined }
        })
            .then(function(response) {
                console.log("Update successful:", response);

                // Lấy idHD của tab hiện tại
                const selectedTab = $scope.tabs[$scope.selectedTab];
                const selectedIdHD = selectedTab.idHD;

                // Gọi lại hàm để load lại danh sách chi tiết hóa đơn của tab hiện tại
                $scope.getCTSPByIdHD(selectedIdHD, selectedTab.currentPage); // Truyền vào trang hiện tại của tab

                // alert("Cập nhật thành công!");
            })
            .catch(function(error) {
                const selectedTab = $scope.tabs[$scope.selectedTab];
                const selectedIdHD = selectedTab.idHD;
                $scope.getCTSPByIdHD(selectedIdHD, selectedTab.currentPage); // Truyền vào trang hiện tại của tab
            });
    };
    $scope.selectSanPham = function(sanPham) {
        $scope.selectedSanPham = sanPham;  // Lưu sản phẩm đã chọn
        $scope.soLuong = 1; // Đặt lại số lượng
        $scope.currentPage = 0; // Nếu không có page được truyền vào, dùng trang 0
        $http.get(`http://localhost:8083/chi-tiet-san-pham/getAllCTSP?idSP=${sanPham.id}`)
            .then(function(response) {
                console.log(response);
                $scope.products = response.data; // Gán danh sách sản phẩm từ dữ liệu trả về
            })
            .catch(function(error) {data
                $scope.errorMessage = 'Lỗi khi lấy sản phẩm: ' + error.data;
                console.error($scope.errorMessage);
            });
        $('#productModal').modal('show');  // Hiển thị modal
    };
    $scope.addCTHD=function(ctsp){
        const formData = new FormData();
        console.log('Ghi chú hiện tại: ', ctsp.ghiChuCTHD);  // Kiểm tra giá trị ghiChu

        formData.append('soLuong',ctsp.soLuongCTHD);
        formData.append('ghiChu', ctsp.ghiChuCTHD || 'note');  // Ghi chú có thể bị null hoặc undefined
        const selectedTab = $scope.tabs[$scope.selectedTab];
        const selectedIdHD = selectedTab.idHD;
        formData.append('idHD',selectedIdHD);
        formData.append('giaBan',ctsp.gia);
        formData.append('idCTSP',ctsp.id);
        for (let [key, value] of formData.entries()) {
            console.log(`${key}: ${value}`); // In ra key và value
        }
        $http.post('http://localhost:8083/chitiethoadon/add', formData, {
            headers: {
                'Content-Type': undefined // Cho phép browser tự động thiết lập boundary
            }})
            .then(function(response) {
                console.log(response.data);
                alert('Thêm chi tiết hóa đơn thành công!');
                $scope.getCTSPByIdHD(selectedIdHD, selectedTab.currentPage); // Truyền vào trang hiện tại của tab
                $scope.selectSanPham($scope.selectedSanPham);

            })
            .catch(function(error) {
                $scope.getCTSPByIdHD(selectedIdHD, selectedTab.currentPage); // Truyền vào trang hiện tại của tab
                console.error('Lỗi:', error);
                $scope.selectSanPham($scope.selectedSanPham);

            });
    }
    $scope.deleteCTHD=function (idCTHD){
        const selectedTab = $scope.tabs[$scope.selectedTab];
        const selectedIdHD = selectedTab.idHD;
        if (confirm("Bạn có chắc chắn muốn xóa sản phẩm này không?")) {
            $http({
                method: 'DELETE',
                url: 'http://localhost:8083/chitiethoadon/delete', // Đường dẫn đến API
                data: { id: idCTHD }, // Gửi id sản phẩm qua request body
                headers: { "Content-Type": "application/json;charset=utf-8" }
            }).then(function (response) {
                console.log(response.data);
                $scope.getCTSPByIdHD(selectedIdHD, selectedTab.currentPage); // Truyền vào trang hiện tại của tab
            }, function (error) {
                $scope.getCTSPByIdHD(selectedIdHD, selectedTab.currentPage); // Truyền vào trang hiện tại của tab
                // alert(response.data); // Hiển thị thông báo thành công
            });
        }
    }
    // Gọi hàm lấy hóa đơn khi khởi tạo controller
    $scope.getHDTaiQuay();

};
