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

    $scope.test = function() {
        $http.get('http://localhost:8083/hoadon/tuDongXoaHoaDon')
            .then(function(response) {
                console.log('Xóa hóa đơn thành công', response);
            }, function(error) {
                console.error('Có lỗi xảy ra khi xóa hóa đơn', error);
            });
    };
    $scope.addInvoiceTab = function () {
        $('#confirmAddInvoiceModal').modal('hide');
        // Gọi API để thêm hóa đơn
        $http.post('http://localhost:8083/hoadon/add'
        )
            .then(function(response) {
                // Kiểm tra xem hóa đơn đã được thêm thành công
                $('#addInvoiceModal').modal('show'); // Hiển thị modal

                // Gọi lại API để lấy danh sách hóa đơn đã được sắp xếp
                $scope.getHDTaiQuay(); // Lấy lại danh sách hóa đơn mới nhất
            })
            .catch(function(error) {
                console.error('Lỗi:', error);
                alert('Lỗi khi thêm hóa đơn: ' + (error.data && error.data.message ? error.data.message : 'Không xác định'));
            });
    };
    $scope.updateGhiChu = function(hd) {
        const formData = new FormData();
        const selectedTab = $scope.tabs[$scope.selectedTab];
        const selectedIdHD = selectedTab.idHD;

        $http.put('http://localhost:8083/hoadon/update-ghi-chu' + selectedIdHD, formData, {
            headers: { 'Content-Type': undefined }
        })
            .then(function(response) {
                console.log("Update successful:", response);

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
    $scope.closeProductModal=function (){
        $('#productModal').modal('toggle');
        $('#confirmAddInvoiceModal').modal('hide');
        $('#confirmAddInvoiceDetailModal').modal('hide');
        $('#actionModal').modal('hide'); // Hiển thị modal
        $('#notificationModal').modal('hide');
        let setOff=document.querySelector(".modal-backdrop")
        setOff.classList.remove('show');
        setOff.classList.add('hide');
        setOff.style.display='none';
        setOff.style.width='0px';
        setOff.style.height='0px'
        $('#productModal').on('hidden.bs.modal', function () {
            $('.modal-backdrop').remove();
        });
    }

    $scope.confirmAddInvoice = function() {
        $('#confirmAddInvoiceModal').modal('show');
    };

    $scope.confirmAddInvoiceDetail = function() {
        $('#confirmAddInvoiceDetailModal').modal('show');
    };

    $scope.closeConfirmModalAddInvoice=function (){
        $('#confirmAddInvoiceModal').modal('hide');
    }

    $scope.closeConfirmModalAddInvoiceDetail=function (){
        $('#confirmAddInvoiceDetailModal').modal('hide');
    }
    $scope.confirmDeleteCTHD = function (idCTHD) {
        console.log('confirmDeleteCTHD is called');
        $scope.idToDelete = idCTHD;
        console.log($scope.idToDelete);
        console.log(idCTHD);
        $('#confirmDeleteInvoiceDetailModal').modal('show');
    }
    $scope.closeConfirmDeleteCTHD=function (){
        $('#confirmDeleteInvoiceDetailModal').modal('hide');
    }
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
                        ghiChu: hoaDon.ghiChu || '', // Đảm bảo giá trị ghiChu được gán đúng
                        idHD: hoaDon.id || [] // Sử dụng idHD thay vì $scope.idHD
                    };
                    // Kiểm tra xem tab đã tồn tại hay chưa
                    if (!$scope.tabs.find(tab => tab.title === newTabTitle)) {
                        $scope.tabs.push(newTab); // Thêm tab mới vào mảng nếu chưa tồn tại
                    }
                });
                // Tự động chọn tab mới nhất được thêm
                if ($scope.tabs.length > 0) {
                    $scope.selectedTab = $scope.tabs.length - 1; // Chọn tab cuối cùng
                    const selectedTab = $scope.tabs[$scope.selectedTab];
                    const selectedIdHD = selectedTab.idHD;
                    $scope.getCTSPByIdHD(selectedIdHD, 0); // Gọi API để lấy chi tiết hóa đơn của tab cuối cùng
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
                $scope.cthds = response.data.cthds;
                console.log(response.data.cthds)// Dữ liệu chi tiết hóa đơn
                $scope.totalPages = response.data.totalPages; // Tổng số trang
                $scope.pages = Array.from({ length: $scope.totalPages }, (v, i) => i); // Mảng trang
            })
            .catch(function (error) {
                $scope.errorMessage = 'Lỗi khi lấy chi tiết hóa đơn: ' + (error.data?.message || JSON.stringify(error.data));
            });
    };

    $scope.selectTab = function(index) {
        $scope.selectedTab = index;
        const selectedTab = $scope.tabs[index];
        const selectedIdHD = selectedTab.idHD;
        console.log($scope.selectedIdHD)// Lấy mã hóa đơn của tab
        $scope.getCTSPByIdHD(selectedIdHD, 0); // Gọi API để lấy chi tiết hóa đơn
        selectedTab.currentPage = 0; // Đặt lại trang khi chọn lại tab
    };

    // Hàm đóng tab
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
    $scope.updateGhiChu = function(hd) {
        const selectedTab = $scope.tabs[$scope.selectedTab];
        const selectedIdHD = selectedTab.idHD;

        const payload = {
            ghiChu: hd.ghiChu // Lấy giá trị ghi chú từ đối tượng `hd`
        };

        $http.put('http://localhost:8083/hoadon/update-ghi-chu/' + selectedIdHD, payload)
            .then(function(response) {
                console.log("Update successful:", response);
                // Gọi lại hàm để load danh sách chi tiết hóa đơn của tab hiện tại
                $scope.getCTSPByIdHD(selectedIdHD, selectedTab.currentPage);
            })
            .catch(function(error) {
                console.error("Update failed:", error);
                // Xử lý lỗi hoặc gọi lại hàm load danh sách
                $scope.getCTSPByIdHD(selectedIdHD, selectedTab.currentPage);
            });
    };
    $scope.updateTotal = function(cthd) {
        const formData = new FormData();
        formData.append('soLuong', cthd.soLuong);

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
    $scope.calculateTotalAmount = function(cthd) {
        if (!cthd) return 0; // Kiểm tra nếu đối tượng không tồn tại

        const gia = cthd.giaSauGiam || cthd.giaBan; // Lấy giá sau giảm, nếu không có thì dùng giá bán
        const soLuong = cthd.soLuong || 0; // Lấy số lượng, mặc định là 0 nếu không có

        return gia * soLuong; // Tính tổng tiền
    };

// Hàm tính tổng tiền cho tất cả chi tiết hóa đơn trong tab hiện tại
    $scope.calculateTotalForList = function(chiTietHoaDons) {
        if (!Array.isArray(chiTietHoaDons)) return 0; // Kiểm tra nếu không phải danh sách

        return chiTietHoaDons.reduce(function(total, cthd) {
            return total + $scope.calculateTotalAmount(cthd); // Sử dụng hàm tính tổng cho từng chi tiết
        }, 0); // Tính tổng tiền bằng cách cộng dồn
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

        formData.append('soLuong',ctsp.soLuongCTHD);
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
                'Content-Type': undefined
            }}
            )
            .then(function(response) {
                console.log(response.data);
                // $('#addInvoiceModal').modal('show'); // Hiển thị modal

                $scope.getCTSPByIdHD(selectedIdHD, selectedTab.currentPage); // Truyền vào trang hiện tại của tab
                $scope.selectSanPham($scope.selectedSanPham);

            })
            .catch(function(error) {
                $scope.getCTSPByIdHD(selectedIdHD, selectedTab.currentPage); // Truyền vào trang hiện tại của tab
                console.log('Lỗi:', error.message);
                $scope.selectSanPham($scope.selectedSanPham);

            });
    }
    $scope.deleteCTHD=function (idCTHD){
        const selectedTab = $scope.tabs[$scope.selectedTab];
        const selectedIdHD = selectedTab.idHD;
            $http({
                method: 'DELETE',
                url: 'http://localhost:8083/chitiethoadon/delete', // Đường dẫn đến API
                data: { id: idCTHD }, // Gửi id sản phẩm qua request body
                headers: { "Content-Type": "application/json;charset=utf-8" }
            }).then(function (response) {
                console.log(response.data);
                $('#confirmDeleteInvoiceDetailModal').modal('hide');
                $scope.getCTSPByIdHD(selectedIdHD, selectedTab.currentPage); // Truyền vào trang hiện tại của tab
            }, function (error) {
                $('#confirmDeleteInvoiceDetailModal').modal('hide');
                $scope.getCTSPByIdHD(selectedIdHD, selectedTab.currentPage); // Truyền vào trang hiện tại của tab
                // alert(response.data); // Hiển thị thông báo thành công
            });

    }
    $scope.selectedTabIndex = 0; // Chỉ số tab hiện tại
    $scope.selectedInvoiceId = null; // ID hóa đơn đang chọn

    // Hàm mở modal khi click vào button
    $scope.showActionModal = function(tabIndex) {
        $scope.selectedTabIndex = tabIndex; // Lưu chỉ số tab hiện tại
        $scope.selectedInvoiceId = $scope.tabs[tabIndex].invoiceId || null; // Lưu ID hóa đơn, nếu có
        $('#actionModal').modal('show'); // Hiển thị modal
    };
    $scope.closeTab = function (index) {
        const selectedTab = $scope.tabs[index];
        $scope.tabs.splice(index, 1); // Xóa tab theo chỉ số

        // Kiểm tra xem tab hiện tại có bị xóa không, nếu có thì chọn lại tab trước đó hoặc tab đầu tiên
        if ($scope.selectedTab >= index) {
            $scope.selectedTab = Math.max(0, $scope.selectedTab - 1);
            $('#actionModal').modal('hide');  // Đóng modal khi nhấn "Hủy"
        }
        if ($scope.tabs.length > 0) {
            $scope.selectTab($scope.selectedTab);
            $('#actionModal').modal('hide');  // Đóng modal khi nhấn "Hủy"

        }
    };
    $scope.closeModal = function() {
        $('#actionModal').modal('hide');  // Đóng modal khi nhấn "Hủy"
    };
    $scope.closeModalAddInvoice = function() {
        $('#addInvoiceModal').modal('hide');  // Đóng modal khi nhấn "Hủy"
    };
    $scope.deleteInvoice = function (idHD, index) {
            const selectedTab = $scope.tabs[$scope.selectedTab];
            const selectedIdHD = selectedTab.idHD;

            // Gửi yêu cầu DELETE tới API với id của hóa đơn
            $http({
                method: 'DELETE',
                url: 'http://localhost:8083/hoadon/delete', // Đường dẫn tới API
                data: { id: selectedIdHD }, // Gửi id hóa đơn qua request body
                headers: { "Content-Type": "application/json;charset=utf-8" }
            })
                .then(function (response) {
                    // Xử lý khi xóa thành công
                    $scope.closeTab(index); // Đóng tab của hóa đơn
                    $scope.getCTSPByIdHD(selectedIdHD, selectedTab.currentPage); // Lấy lại danh sách sản phẩm cho tab hiện tại
                })
                .catch(function (error) {
                    // Xử lý lỗi
                    if (error.status === 404) {
                        alert('Hóa đơn không tồn tại!');
                    } else {
                        alert('Không thể xóa hóa đơn, vui lòng thử lại sau.');
                    }
                });

    };

    // Gọi hàm lấy hóa đơn khi khởi tạo controller
    $scope.getHDTaiQuay();

}
