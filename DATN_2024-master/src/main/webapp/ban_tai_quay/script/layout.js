var app = angular.module("myApp", ['ngRoute'])

app.config(function ($routeProvider) {
    $routeProvider
        .when('/sanpham', {
            templateUrl: 'view/sanpham.html',
            controller: sanPhamCtrl
        })
        .when('/giamgia', {
            templateUrl: 'view/giamgia.html',
            controller: giamGiaCtrl
        })
        .when('/banhang', {
            templateUrl: 'view/banhang.html',
            controller: banhangCtrl
        })
        .when('/chitietsanpham/:idSP', {
            templateUrl: 'view/chitietsanpham.html',
            controller: chiTietSanPhamCtrl
        })
        .when('/hoadon', {
            templateUrl: 'view/hoadon.html',
            controller: hoaDonCtrl
        })
        .when('/nhansu', {
            templateUrl: 'view/nhansu.html',
            controller: nhansuCtrl
        })
        .when('/khachhang', {
            templateUrl: 'view/khachhang.html',
            controller: khachhangCtrl
        })
        .when('/thanhtoan', {  // Add dynamic route for payment
            templateUrl: 'view/thanhtoan.html',
            controller: thanhtoanCtrl
        })
        .when('/thongke', {
            templateUrl: 'view/thongke.html',
            controller: thongkeCtrl
        })
        .when('/voucher', {
            templateUrl: 'view/voucher.html',
            controller: voucherCtrl
        })
        .when('/loaivoucher', {
            templateUrl: 'view/loaivoucher.html',
            controller: loaivoucherCtrl
        })
        .when('/thongbao', {
            templateUrl: 'view/thongbao.html',
            controller: thongbaoCtrl
        })
        .when('/thongtingiaohang', {
            templateUrl: 'view/thongtingiaohang.html',
            controller: thongtingiaohangCtrl
        })
        .otherwise({
            redirectTo: '/login',
        })
})

app.controller('myCtrl', function ($scope, $http) {
    // const login = sessionStorage.getItem('loginOk')
    // if(!login){
    //     window.location.href = 'http://localhost:63342/demo/src/main/webapp/ban_tai_quay/view/login.html?_ijt=rgqkbr1cvcf8at1kk6v46lmcv4'
    // }

    $scope.modalMessage = ""; // Nội dung thông báo

    // Hàm kiểm tra cập nhật giảm giá
    $scope.inspection = function () {
        $http.get('http://localhost:8083/giam-gia/inspection')
            .then(function (response) {
                $scope.modalMessage = "Các giảm giá đã được cập nhật: " + (response.data.join(', ') || "Không có gì để cập nhật");
                console.log($scope.modalMessage);
                setTimeout(function () {
                    $('#notificationModal').modal('show');
                }, 0);
            })
            .catch(function () {
                $scope.modalMessage = "Đã xảy ra lỗi khi kiểm tra giảm giá!";
                setTimeout(function () {
                    $('#notificationModal').modal('show');
                }, 0);
            });
    };

    $scope.closeNotificationModal = function () {
        setTimeout(function () {
            $('#notificationModal').modal('hide');
        }, 0);    };

    $scope.inspection();
})

document.addEventListener('DOMContentLoaded', function () {
    const allSideMenu = document.querySelectorAll('#sidebar .side-menu.top li a')

    allSideMenu.forEach(item => {
        const li = item.parentElement

        item.addEventListener('click', function () {
            allSideMenu.forEach(i => {
                i.parentElement.classList.remove('active')
            })
            li.classList.add('active')
        })
    })

    const menuBar = document.querySelector('#bx_menu')
    const sidebar = document.getElementById('sidebar')

    menuBar.addEventListener('click', function () {
        sidebar.classList.toggle('hide');
    })
})