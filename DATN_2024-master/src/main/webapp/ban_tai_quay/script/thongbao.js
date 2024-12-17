window.thongbaoCtrl = function ($scope, $http) {
    const url = "http://localhost:8083/thongbao";

    $scope.listThongBao = [];
    $scope.currentPage = 0;
    $scope.totalPages = 1;
    $scope.pageSize = 5;
    $scope.emptyMessage = "";

    const overlayLoad = document.querySelector('.overlay-load')
    const loader = document.querySelector('.loader')

    $scope.searchParams ={
        noiDung : '',
        trangThai :''
    }
    $scope.searchAndFilter = function (page = 0){
        const params = {
            ...$scope.searchParams,
            page:page,
            size :$scope.pageSize
        }
        $http.get(`http://localhost:8083/thongbao/searchTB`, {params})
            .then(function (response){
                $scope.listThongBao = response.data.thongBaos
                $scope.currentPage = response.data.currentPage
                $scope.totalPages = response.data.totalPages

                if($scope.listThongBao.length ===0 ){
                    $scope.emptyMessage = response.data.message || "Không tìm thấy thông báo"
                }else {
                    $scope.emptyMessage = ''
                }
            })
            .catch(function (error){
                console.error("Lỗi khi tìm kiếm",error)
            })
    }
    $scope.resetFilters = function (){
        $scope.searchParams ={
            noiDung : '',
            trangThai : ''
        }
        $scope.loadPage(0)
    }
    $scope.loadPage = function (page) {
        $http.get('http://localhost:8083/thongbao/page?page=' + page)
            .then(function (response) {
                $scope.listThongBao = response.data.thongBaos;
                $scope.currentPage = response.data.currentPage;
                $scope.totalPages = response.data.totalPages;

                // Kiểm tra nếu danh sách trống và hiển thị thông báo
                if ($scope.listThongBao.length === 0) {
                    $scope.emptyMessage = response.data.message || "Danh sách trống!";
                } else {
                    $scope.emptyMessage = ""; // Reset lại thông báo nếu có dữ liệu
                }
            })
            .catch(function (error) {
                console.error('Lỗi:', error);
            });
    };

    $scope.getSTT = function (index) {
        return index + 1 + ($scope.currentPage * $scope.pageSize);
    };

    $scope.range = function (n) {
        return new Array(n);
    };

    $scope.setPage = function (page) {
        if (page >= 0 && page < $scope.totalPages) {
            $scope.loadPage(page);
        }
    };

    $scope.prevPage = function () {
        if ($scope.currentPage > 0) {
            $scope.setPage($scope.currentPage - 1);
        }
    };

    $scope.nextPage = function () {
        if ($scope.currentPage < $scope.totalPages - 1) {
            $scope.setPage($scope.currentPage + 1);
        }
    };
    $scope.range = function (n) {
        var arr = [];
        for (var i = 0; i < n; i++) {
            arr.push(i);
        }
        return arr;
    };

    // Load initial page
    $scope.loadPage(0);


    $scope.listKH = [];
    $http.get("http://localhost:8083/khachhang")
        .then(function (response) {
            $scope.listKH = response.data;
            console.log("Lấy danh sách khách hàng thành công", $scope.listKH);
        })
        .catch(function (error) {
            console.error("Lỗi khi lấy danh sách LVC:", error);
        });

    $scope.formatDate = function (dateString) {
        // Kiểm tra nếu dateString là null hoặc rỗng
        if (!dateString) {
            return ''; // Trả về chuỗi rỗng nếu không có ngày
        }
        try {
            return new Date(dateString).toISOString().split('T')[0];
        } catch (e) {
            console.error("Lỗi khi định dạng ngày:", e);
            return ''; // Trả về chuỗi rỗng nếu xảy ra lỗi
        }
    };

    $scope.viewDetail = function (thongBao) {
        $scope.selectedThongBao = angular.copy(thongBao);
        $scope.selectedThongBao.trangThai = thongBao.trangThai == 1 ? 'Hoạt động' : 'Ngưng hoạt động';
        $scope.formattedNgayGui = new Date(thongBao.ngayGui);
        $scope.formattedNgayDoc = new Date(thongBao.ngayDoc);

        fetch(`http://localhost:8083/thongbao/` + thongBao.id)
            .then(respponse => respponse.json())
            .then(data => {
                $scope.$apply(() =>{
                    $scope.listCustomer = data
                })
            })
            .catch(function (error){
                console.error(error)
            })
    };

    $scope.openUpdateModal = function (thongBao) {
        $scope.selectedThongBao = angular.copy(thongBao);
        $scope.selectedKhachHang = $scope.selectedThongBao.tenKH;
        $scope.idKH = null;
        console.log($scope.selectedKhachHang);

        // fetch('http://localhost:8083/khachhang/getId?ten=' + $scope.selectedKhachHang)
        //     .then(function (response) {
        //         return response.json();  // Chuyển đổi sang JSON vì API trả về danh sách
        //     })
        //     .then(function (data) {
        //         if (data.length > 0) {
        //             $scope.$apply(function () {
        //                 $scope.idKH = data[0];  // Chọn ID đầu tiên trong danh sách
        //                 console.log($scope.idKH);
        //             });
        //         } else {
        //             console.warn("Không tìm thấy khách hàng nào với tên:", $scope.selectedKhachHang);
        //         }
        //     })
        //     .catch(function (error) {
        //         // console.error("Lỗi khi gọi API:", error);
        //     });
        fetch(`http://localhost:8083/thongbao/` + thongBao.id)
            .then(response => response.json())
            .then(data => {
                $scope.$apply(() =>{
                    $scope.selectedCustomersForUpdate = data
                })
            })
            .catch(function (error){
                console.error(error)
            })

        $scope.selectedThongBao.trangThai = thongBao.trangThai.toString();
        console.log("Trạng thái hiện tại:", $scope.selectedThongBao.trangThai);
    };

    $scope.selectedCustomers = [];  // Mảng lưu khách hàng đã chọn

// Cập nhật khách hàng đã chọn
    $scope.updateSelectedCustomers = function (kh) {
        if (kh.selected) {
            $scope.selectedCustomers.push(kh); // Thêm khách hàng vào mảng đã chọn
        } else {
            const index = $scope.selectedCustomers.indexOf(kh);
            if (index > -1) {
                $scope.selectedCustomers.splice(index, 1); // Xóa khách hàng khỏi mảng đã chọn
            }
        }
    };

// Hàm chọn tất cả khách hàng
    $scope.selectAllCustomers = function () {
        $scope.selectedCustomers = angular.copy($scope.listKH); // Chọn tất cả khách hàng
        $scope.listKH.forEach(function (kh) {
            kh.selected = true;
        });
    };

// Bỏ chọn tất cả khách hàng
    $scope.deselectAllCustomers = function () {
        $scope.selectedCustomers = []; // Bỏ chọn tất cả khách hàng
        $scope.listKH.forEach(function (kh) {
            kh.selected = false;
        });
    };

// Xác nhận chọn khách hàng
    $scope.confirmSelection = function () {
        // Đóng modal sau khi xác nhận
        $('#customerModal').modal('hide');
        $('#productModal').modal('show');

    };

// Hàm thêm thông báo
    $scope.addThongBao = function () {
        const newThongBao = {
            noiDung: $scope.noiDung,
            idKHs: $scope.selectedCustomers.map(kh => kh.id), // Gửi danh sách ID khách hàng
            trangThai: $scope.trangThai
        };

        console.log("Dữ liệu gửi lên:", newThongBao);

        $http.post('http://localhost:8083/thongbao/add', newThongBao)
            .then(function (response) {
               showSuccessAlert("Thêm thành công thông báo")
                $('#productModal').modal('hide');
                setTimeout(function () {
                    $scope.loadPage($scope.currentPage)
                }, 500);
            })
            .catch(function (error) {
                console.error("Lỗi khi thêm thông báo:", error);
                $scope.errorMessage = error.data.message || "Thêm thất bại, vui lòng thử lại!";
            });
        // Đặt lại form
        resetForm();
    };


    $scope.selectedCustomersForUpdate = []; // Danh sách khách hàng đã chọn

// Hàm cập nhật danh sách khách hàng
    $scope.updateSelectedCustomersForUpdate = function (kh) {
        if (kh.selectedForUpdate) {
            $scope.selectedCustomersForUpdate.push(kh);
        } else {
            $scope.selectedCustomersForUpdate = $scope.selectedCustomersForUpdate.filter(c => c.id !== kh.id);
        }
    };

// Hàm chọn tất cả khách hàng
    $scope.selectAllCustomersForUpdate = function () {
        $scope.listKH.forEach(kh => {
            kh.selectedForUpdate = true;
            if (!$scope.selectedCustomersForUpdate.some(c => c.id === kh.id)) {
                $scope.selectedCustomersForUpdate.push(kh);
            }
        });
    };

// Hàm bỏ chọn tất cả khách hàng
    $scope.deselectAllCustomersForUpdate = function () {
        $scope.listKH.forEach(kh => (kh.selectedForUpdate = false));
        $scope.selectedCustomersForUpdate = [];
    };

// Hàm xác nhận lựa chọn khách hàng
    $scope.confirmSelectionForUpdate = function () {
        $('#customerUpdateModal').modal('hide'); // Đóng modal chọn khách hàng
        $scope.selectedCustomersForUpdate = $scope.listKH.filter(kh => kh.selectedForUpdate);
        $('#UpdateForm').modal('show');
    };

// Hàm cập nhật thông báo
    $scope.updateThongBao = function () {
        const updatedThongBao = {
            id: $scope.selectedThongBao.id,
            noiDung: $scope.selectedThongBao.noiDung,
            trangThai: $scope.selectedThongBao.trangThai,
            idKHs: $scope.selectedCustomersForUpdate.map(kh => kh.id)
        };

        $http.put(`http://localhost:8083/thongbao/update/${updatedThongBao.id}`, updatedThongBao)
            .then(function (response) {
                showSuccessAlert("Update thành công")
                $('#UpdateForm').modal('hide');
                $scope.loadPage($scope.currentPage)
            })
            .catch(function (error) {
                $scope.errorMessage = error.data || "Cập nhật thất bại!";
            });
    };

    $scope.delete = function (id) {
        console.log("Xóa");
        if (confirm('Bạn có chắc chắn muốn xóa Thông Báo này?')) {
            $http.delete('http://localhost:8083/thongbao/delete/' + id)
                .then(function (response) {
                    console.log(response.data);
                    $scope.loadPage($scope.currentPage)
                    const index = $scope.listThongBao.findIndex(tb => tb.id === id);
                    if (index !== -1) {
                        $scope.listThongBao.splice(index, 1);
                    }
                    alert(response.data.message || 'Xóa thành công!!');  // Sử dụng thông điệp từ server
                })
                .catch(function (error) {
                    console.error("Lỗi khi xóa :", error);
                    alert("Xóa thất bại. Vui lòng thử lại sau.");
                });
        }
    };


    $scope.sendEmailsToAll = function (thongBao) {
        if (!thongBao || !thongBao.emailKH) {
            alert("Danh sách khách hàng không hợp lệ hoặc không có dữ liệu!");
            console.error("Dữ liệu thông báo không hợp lệ:", thongBao);
            return;
        }
        // Tách chuỗi email thành mảng
        const emailList = thongBao.emailKH.split(",").map(email => email.trim()); // Tách email và loại bỏ khoảng trắng

        const emailRequest = {
            emailNguoiNhan: emailList, // Danh sách email khách hàng
            tieuDe: "Thông báo từ Shop bán thực phẩm chức năng Loopy",
            noiDung: thongBao.noiDung
        };
        overlayLoad.style.display = 'block';
        loader.style.display = 'block';

        $http.post('http://localhost:8083/mail/sentAllKH', emailRequest)
            .then(function (response) {
                overlayLoad.style.display ='none'
                loader.style.display = 'none'
                showSuccessAlert(response.data.message)
                console.log("Email đã gửi thành công:", response.data);
            })
            .catch(function (error) {
                const errorMessage = error.data && error.data.message ? error.data.message : "Không thể gửi email. Vui lòng thử lại!";
                alert(errorMessage);
                console.error("Lỗi khi gửi email:", error);
            })
            .finally(() =>{
                overlayLoad.style.display = 'none'
                loader.style.display = 'none'
            })
    };





    function resetForm() {
        $scope.noiDung = "";
        $scope.ngayDoc = "";
        $scope.ngayGui = "";
        $scope.idKH = "";
        $scope.trangThai = "";
    }


};

