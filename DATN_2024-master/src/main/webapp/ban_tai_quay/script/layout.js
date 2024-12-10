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
        .when('/thanhtoan/:idHD', {  // Add dynamic route for payment
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
    const login = sessionStorage.getItem('loginOk')
    if (!login) {
        window.location.href = 'http://localhost:63342/demo/src/main/webapp/ban_tai_quay/view/login.html?_ijt=rgqkbr1cvcf8at1kk6v46lmcv4'
        return
    }
    const profileButton = document.querySelector('.profile');
    const profileModal = document.querySelector('#profileModal');

    const idNV = sessionStorage.getItem('idNV');
    if (!idNV) {
        console.error('Không tìm thấy ID nhân viên trong sessionStorage');
        return;
    }

    let getNV

    fetch('http://localhost:8083/nhanvien/profile/' + idNV, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
        mode: 'cors'
            })
        .then(response => {
            if (!response.ok) {
                console.error('Không thể lấy thông tin nhân viên');
                return;
            }
            return response.json(); // Chuyển dữ liệu từ response thành JSON
        })
        .then(nhanVien => {
            if (!nhanVien) {
                console.error('Không có dữ liệu nhân viên');
                return;
            }

            getNV = nhanVien

            const imgAcc = document.querySelector("#img-acc")
            imgAcc.src = nhanVien.img
        })
    profileButton.addEventListener('click', async () => {
                const profileModalBody = document.querySelector('#profileModal .modal-body');
                profileModalBody.innerHTML = `
                        <div class="form-group">
                            <label>Tên nhân viên:</label>
                            <p>${getNV.ten || 'Không có thông tin'}</p>
                        </div>
                        <div class="form-group">
                            <label>Email:</label>
                            <p>${getNV.email || 'Không có thông tin'}</p>
                        </div>
                        <div class="form-group">
                            <label>Quyền:</label>
                            <p>${getNV.tenQuyen || 'Không có thông tin'}</p>
                        </div>                     
                    `;

                $('#profileModal').modal('show');
            })


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
        }, 0);
        const backdrop_model = document.querySelector('.modal-backdrop')
        backdrop_model.style.display = 'none'
    };

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

function showSuccessAlert(message) {
    const alertElement = document.getElementById('success-alert');
    const messageElement = document.getElementById('success-message');
    messageElement.textContent = message;

    alertElement.style.display = 'block';

    setTimeout(() => alertElement.classList.add('show'), 10); // Thêm hiệu ứng
    setTimeout(() => {
        alertElement.classList.remove('show'); // Ẩn hiệu ứng
        setTimeout(() => (alertElement.style.display = 'none'), 500); // Ẩn hoàn toàn
    }, 3000);
}


// Hiển thị thông báo lỗi
function showDangerAlert(message) {
    const alertElement = document.getElementById('danger-alert');
    const messageElement = document.getElementById('danger-message');
    messageElement.textContent = message;

    alertElement.style.display = 'block';

    setTimeout(() => alertElement.classList.add('show'), 10); // Thêm hiệu ứng
    setTimeout(() => {
        alertElement.classList.remove('show'); // Ẩn hiệu ứng
        setTimeout(() => (alertElement.style.display = 'none'), 500); // Ẩn hoàn toàn
    }, 3000);
}

