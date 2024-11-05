window.banhangCtrl = function ($scope, $http) {
    $scope.tabs = []; // Khởi tạo danh sách tab
    $scope.selectedTab = 0; // Chỉ số tab đang chọn
    $scope.searchText = ""; // Khởi tạo biến tìm kiếm
    $scope.page = 0; // Initial page
    $scope.ctsps = {};
    $scope.currentPage = 0;
    $scope.itemsPerPage = 10; // Number of items per page
    $scope.totalPages = 0;
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

    // Gọi hàm lấy hóa đơn khi khởi tạo controller
    $scope.getHDTaiQuay();
};
