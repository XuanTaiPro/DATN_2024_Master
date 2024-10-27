window.thanhtoanCtrl= function($scope, $http) {

    $scope.listCTHD = [];
    $scope.listCTSP = [];


    $http.get('http://localhost:8080/chitiethoadon/getCTHD?idHD=1C5331D3')
        .then(function(response) {
            $scope.listCTHD = response.data;
            // Gán dữ liệu trả về vào listCTHD
            let listData = response.data;
            let tt =0
            listData.forEach(item => {
                tt += parseInt(item.tongTien)
            })
            $scope.tongTien = tt
        })
        .catch(function(error) {
            console.error("Lỗi:", error);
        });

}