var app = angular.module("myApp", ['ngRoute'])

app.config(function ($routeProvider) {
    $routeProvider
        .when('/sanpham', {
            templateUrl: 'view/sanpham.html',
            controller: sanphamCtrl
        })
        .when('/giamgia', {
            templateUrl: 'view/giamgia.html',
            controller: giamgiaCtrl
        })
        .when('/banhang', {
            templateUrl: 'view/banhang.html',
            controller: banhangCtrl
        })
        .when('/nhansu', {
            templateUrl: 'view/nhansu.html',
            controller: nhansuCtrl
        })
        .when('/khachhang', {
            templateUrl: 'view/khachhang.html',
            controller: khachhangCtrl
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
        // .otherwise({
        //     redirectTo: '/sanpham',
        // })
})

app.controller('myCtrl', function ($scope, $http) { })

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