window.khachhangCtrl = function ($scope, $http) {
    const url = "http://localhost:8080/khachhang/findAllNotPW"

    $scope.ds = []

    $http.get(url).then(function (response) {
        $scope.ds = response.data
        console.log("get ok")
    }).catch((error) => {
        console.error('Error:', error);
    })
}