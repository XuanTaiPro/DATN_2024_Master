window.thanhtoanCtrl= function($scope, $http) {

    $scope.listCTHD = [];
    $scope.listCTSP = [];


    $http.get('http://localhost:8080/chiTietHoaDon/list/1C5331D3')
        .then(function(response) {
            $scope.listCTHD = response.data; // Gán dữ liệu trả về vào listCTHD
        })
        .catch(function(error) {
            console.error("Lỗi:", error);
        });
}