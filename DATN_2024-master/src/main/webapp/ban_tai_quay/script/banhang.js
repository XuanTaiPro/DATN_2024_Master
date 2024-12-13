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
    $scope.qrData = '';
    $scope.isScanning = false;

    // Hàm bắt đầu quét mã QR khi nút được nhấn
    $scope.startQRCodeScan = function () {
        $scope.isScanning = true;

        if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
            navigator.mediaDevices.getUserMedia({ video: true })
                .then(function (stream) {
                    var videoElement = document.getElementById('videoElement');
                    videoElement.srcObject = stream;

                    // Đảm bảo video đã load và có kích thước hợp lệ
                    videoElement.onloadedmetadata = function () {
                        videoElement.play();
                        // Bắt đầu quét mã QR khi video đã sẵn sàng
                        $scope.scanQRCode(videoElement, stream); // Chuyển stream vào hàm scanQRCode
                    };
                })
                .catch(function (err) {
                    console.log('Lỗi khi truy cập camera: ', err);
                });
        } else {
            console.log('Trình duyệt không hỗ trợ API getUserMedia');
        }
    };
    $scope.stopQRCodeScan = function () {
        $scope.isScanning = false;
        let video = document.getElementById('videoElement');
        let stream = video.srcObject;
        if (stream) {
            let tracks = stream.getTracks();
            tracks.forEach(function (track) {
                track.stop();
            });
        }
        video.srcObject = null;
    };
    $scope.scanQRCode = function (videoElement, stream) {
        // Kiểm tra video đã có chiều rộng hợp lệ chưa
        if (videoElement.videoWidth === 0 || videoElement.videoHeight === 0) {
            // Nếu chưa có kích thước, chờ 100ms rồi thử lại
            setTimeout(function () {
                $scope.scanQRCode(videoElement, stream);
            }, 100);
            return;
        }

        // Lấy bức ảnh từ video
        var canvas = document.createElement("canvas");
        var context = canvas.getContext("2d");
        canvas.height = videoElement.videoHeight;
        canvas.width = videoElement.videoWidth;
        context.drawImage(videoElement, 0, 0, canvas.width, canvas.height);

        // Dùng thư viện jsQR để quét mã QR từ ảnh
        var imageData = context.getImageData(0, 0, canvas.width, canvas.height);
        var qrCode = jsQR(imageData.data, canvas.width, canvas.height);

        if (qrCode) {
            // Nếu quét được mã QR, hiển thị dữ liệu
            $scope.$apply(async function () {
                $scope.qrData = qrCode.data; // Lưu dữ liệu quét được vào $scope
                console.log('Dữ liệu mã QR: ', $scope.qrData);

                // Tạo formData để gửi API
                const formData = new FormData();
                formData.append('soLuong', 1);
                const selectedTab = $scope.tabs[$scope.selectedTab];
                const selectedIdHD = selectedTab ? selectedTab.idHD : null;
                // console.log('selectedIdHD: ', selectedIdHD);
                formData.append('idCTSP', $scope.qrData);
                formData.append('idHD', selectedIdHD);

                // In ra key và value trong formData
                for (let [key, value] of formData.entries()) {
                    console.log(`${key}: ${value}`);
                }

                if (selectedIdHD != null) {
                    // Nếu idHD hợp lệ, gọi API để thêm chi tiết hóa đơn
                    $http.post('http://localhost:8083/chitiethoadon/add', formData, {
                        headers: { 'Content-Type': undefined }
                    })
                        .then(function (response) {
                            showSuccessAlert("Thêm thành công!");
                                // Cập nhật lại danh sách sản phẩm
                            $scope.getCTSPByIdHD(selectedIdHD, selectedTab.currentPage);
                        })
                        .catch(function (error) {
                            showDangerAlert("Thêm thất bại!");
                            console.error('Lỗi khi thêm chi tiết hóa đơn:', error.message);
                            $scope.getCTSPByIdHD(selectedIdHD, selectedTab.currentPage);
                        });

                    // Ngừng quét mã QR và dừng video stream
                    stream.getTracks().forEach(track => track.stop());
                    $scope.isScanning = false;
                    videoElement.srcObject = null; // Hủy kết nối video
                } else {
                    const formData = new FormData();
                    formData.append('idNV', '40E70DA8'); // Thay đổi theo nhu cầu

                    // Gọi API để thêm hóa đơn mới
                    $http.post('http://localhost:8083/hoadon/add', formData, {
                        transformRequest: angular.identity,
                        headers: { 'Content-Type': undefined }
                    })
                        .then(function (response) {
                            $scope.getHDTaiQuayAndAddCTHD($scope.qrData); // Lấy lại danh sách hóa đơn mới nhất
                        })
                        .catch(function (error) {
                            showDangerAlert("Thất bại!");
                            console.error('Lỗi:', error);
                            alert('Lỗi khi thêm hóa đơn: ' + (error.data && error.data.message ? error.data.message : 'Không xác định'));
                        });
                }
            });
        } else {
            // Nếu không quét được, tiếp tục quét sau 100ms
            setTimeout(function () {
                $scope.scanQRCode(videoElement, stream);
            }, 100);
        }
    };

    // Hàm thêm hóa đơn
    //     $scope.addInvoiceTabInScanQR = function (idCTSP) {
    //         // $scope.getHDTaiQuay();
    //         const selectedTab = $scope.tabs[$scope.selectedTab];
    //         if (selectedTab && selectedTab.idHD) {
    //             const selectedIdHD = selectedTab.idHD;
    //             console.log(selectedTab);
    //             console.log('Selected idHD:', selectedIdHD);
    //             const formData = new FormData();
    //             formData.append('soLuong', 1);
    //             formData.append('idHD', selectedIdHD);
    //             formData.append('idCTSP', idCTSP);
    //
    //             // Thêm chi tiết hóa đơn sau khi tạo hóa đơn mới
    //             $http.post('http://localhost:8083/chitiethoadon/add', formData, {
    //                 headers: { 'Content-Type': undefined }
    //             })
    //                 .then(function(response) {
    //                     console.log('Chi tiết hóa đơn đã được thêm thành công');
    //                     $scope.getCTSPByIdHD(selectedIdHD, selectedTab.currentPage);
    //                 })
    //                 .catch(function(error) {
    //                     console.log('Lỗi khi thêm chi tiết hóa đơn:', error.message);
    //                 });
    //         } else {
    //             console.log('selectedTab or idHD is undefined');
    //         }
    //     };

    $scope.getHDTaiQuayAndAddCTHD = function (idCTSP) {
        $http.get('http://localhost:8083/hoadon/getHDTaiQuay')
            .then(function (response) {
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
                    console.log(selectedIdHD);
                    // $scope.getCTSPByIdHD(selectedIdHD, 0);
                    if (selectedTab && selectedTab.idHD) {
                        const selectedIdHD = selectedTab.idHD;
                        console.log(selectedTab);
                        console.log('Selected add idHD:', selectedIdHD);
                        const formData = new FormData();
                        formData.append('soLuong', 1);
                        formData.append('idHD', selectedIdHD);
                        formData.append('idCTSP', idCTSP);

                        // Thêm chi tiết hóa đơn sau khi tạo hóa đơn mới
                        $http.post('http://localhost:8083/chitiethoadon/add', formData, {
                            headers: { 'Content-Type': undefined }
                        })
                            .then(function (response) {
                                showSuccessAlert("Thêm thành công!");
                                $scope.getCTSPByIdHD(selectedIdHD, selectedTab.currentPage);
                            })
                            .catch(function (error) {
                                showDangerAlert("Thêm sản phẩm thất bại!");
                                console.log('Lỗi khi thêm chi tiết hóa đơn:', error.message);
                                $scope.getCTSPByIdHD(selectedIdHD, selectedTab.currentPage);

                            });
                    } else {
                        console.log('selectedTab or idHD is undefined');
                    }// Gọi API để lấy chi tiết hóa đơn của tab cuối cùng
                }
            })
            .catch(function (error) {
                console.error('Lỗi khi lấy hóa đơn tại quầy:', error.data);
            });
    };

    $scope.addInvoiceTab = function () {
        $('#confirmAddInvoiceModal').modal('hide');

        const formData = new FormData();
        formData.append('idNV', '40E70DA8'); // Thay đổi theo nhu cầu

        // Gọi API để thêm hóa đơn
        $http.post('http://localhost:8083/hoadon/add', formData, {
            transformRequest: angular.identity,
            headers: { 'Content-Type': undefined }
        })
            .then(function (response) {
                $('#addInvoiceModal').modal('show'); // Hiển thị modal
                $scope.getHDTaiQuay(); // Lấy lại danh sách hóa đơn mới nhất
            })
            .catch(function (error) {
                console.error('Lỗi:', error);
                alert('Lỗi khi thêm hóa đơn: ' + (error.data && error.data.message ? error.data.message : 'Không xác định'));
            });
    };

    $scope.closeProductModal = function () {
        $('#productModal').modal('toggle');
        $('#confirmAddInvoiceModal').modal('hide');
        $('#confirmAddInvoiceDetailModal').modal('hide');
        $('#actionModal').modal('hide'); // Hiển thị modal
        $('#notificationModal').modal('hide');
        let setOff = document.querySelector(".modal-backdrop")
        setOff.classList.remove('show');
        setOff.classList.add('hide');
        setOff.style.display = 'none';
        setOff.style.width = '0px';
        setOff.style.height = '0px'
        $('#productModal').on('hidden.bs.modal', function () {
            $('.modal-backdrop').remove();
        });
    }

    $scope.confirmAddInvoice = function () {
        $('#confirmAddInvoiceModal').modal('show');
    };

    $scope.confirmAddInvoiceDetail = function (ctsp) {
        // Lưu đối tượng ctsp vào biến khi nhấn "Thêm"
        $scope.selectedCTSP = ctsp;
        // Hiển thị modal xác nhận
        $('#confirmAddInvoiceDetailModal').modal('show');
    };

    $scope.closeConfirmModalAddInvoice = function () {
        $('#confirmAddInvoiceModal').modal('hide');
    }

    $scope.closeConfirmModalAddInvoiceDetail = function () {
        $('#confirmAddInvoiceDetailModal').modal('hide');
    }
    $scope.confirmDeleteCTHD = function (idCTHD) {
        console.log('confirmDeleteCTHD is called');
        $scope.idToDelete = idCTHD;
        console.log($scope.idToDelete);
        console.log(idCTHD);
        $('#confirmDeleteInvoiceDetailModal').modal('show');
    }
    $scope.closeConfirmDeleteCTHD = function () {
        $('#confirmDeleteInvoiceDetailModal').modal('hide');
    }
    // Hàm lấy hóa đơn (gọi khi khởi tạo controller)
    $scope.getHDTaiQuay = function () {
        $http.get('http://localhost:8083/hoadon/getHDTaiQuay')
            .then(function (response) {
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
            .catch(function (error) {
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
    $scope.selectTab = function (index) {
        $scope.selectedTab = index;
        const selectedTab = $scope.tabs[index];
        const selectedIdHD = selectedTab.idHD;
        console.log($scope.selectedIdHD)// Lấy mã hóa đơn của tab
        $scope.getCTSPByIdHD(selectedIdHD, 0); // Gọi API để lấy chi tiết hóa đơn
        selectedTab.currentPage = 0; // Đặt lại trang khi chọn lại tab
    };


    $document.on('click', function (event) {
        var searchResultsElement = angular.element(document.querySelector('.search-results'));
        var inputElement = angular.element(document.querySelector('input[ng-model="searchText"]'));

        // Kiểm tra xem click có phải ngoài div kết quả tìm kiếm hoặc ngoài ô tìm kiếm không
        if (!searchResultsElement[0].contains(event.target) && !inputElement[0].contains(event.target)) {
            $scope.$apply(function () {
                $scope.searchResultsVisible = false; // Ẩn kết quả tìm kiếm
            });
        }
    });
    $scope.updateGhiChu = function (hd) {
        const selectedTab = $scope.tabs[$scope.selectedTab];
        const selectedIdHD = selectedTab.idHD;

        const payload = {
            ghiChu: hd.ghiChu // Lấy giá trị ghi chú từ đối tượng `hd`
        };

        $http.put('http://localhost:8083/hoadon/update-ghi-chu/' + selectedIdHD, payload)
            .then(function (response) {
                console.log("Update successful:", response);
                showSuccessAlert("Cập nhật ghi chú thành công!");
                $scope.getCTSPByIdHD(selectedIdHD, selectedTab.currentPage);
            })
            .catch(function (error) {
                console.error("Update failed:", error);
                showDangerAlert("Cập nhật thất bại!");
                $scope.getCTSPByIdHD(selectedIdHD, selectedTab.currentPage);
            });
    };
    $scope.updateTotal = function (cthd) {
        const formData = new FormData();
        formData.append('soLuong', cthd.soLuong);

        // $http.put('http://localhost:8083/chitiethoadon/updateSoLuong?id=' + cthd.id, formData, {
        //     headers: { 'Content-Type': undefined }
        // })
        //     .then(function (response) {
        //         console.log("Update successful:", response);

        //         // Lấy idHD của tab hiện tại
        //         const selectedTab = $scope.tabs[$scope.selectedTab];
        //         const selectedIdHD = selectedTab.idHD;

        //         // Gọi lại hàm để load lại danh sách chi tiết hóa đơn của tab hiện tại
        //         $scope.getCTSPByIdHD(selectedIdHD, selectedTab.currentPage); // Truyền vào trang hiện tại của tab

        //         // alert("Cập nhật thành công!");
        //     })
        //     .catch(function (error) {
        //         const selectedTab = $scope.tabs[$scope.selectedTab];
        //         const selectedIdHD = selectedTab.idHD;
        //         $scope.getCTSPByIdHD(selectedIdHD, selectedTab.currentPage); // Truyền vào trang hiện tại của tab
        //     });

        const request = {
            id: cthd.id,
            soLuong: cthd.soLuong
        }
        fetch(`http://localhost:8083/chitiethoadon/updateSoLuong`, {
            method: "PUT",
            headers: {
                "Content-Type":"application/json"
            },
            body: JSON.stringify(request)
        })
            .then(async response => {
                console.log(response)
                if (response.status== 400){
                    showDangerAlert(await response.text())
                }
                return
                const selectedTab = $scope.tabs[$scope.selectedTab];
                const selectedIdHD = selectedTab.idHD;

                // Gọi lại hàm để load lại danh sách chi tiết hóa đơn của tab hiện tại
                $scope.getCTSPByIdHD(selectedIdHD, selectedTab.currentPage);
            })


    };
    $scope.calculateTotalAmount = function (cthd) {
        if (!cthd) return 0; // Kiểm tra nếu đối tượng không tồn tại

        const gia = cthd.giaSauGiam || cthd.giaBan; // Lấy giá sau giảm, nếu không có thì dùng giá bán
        const soLuong = cthd.soLuong || 0; // Lấy số lượng, mặc định là 0 nếu không có

        return gia * soLuong; // Tính tổng tiền
    };

    // Hàm tính tổng tiền cho tất cả chi tiết hóa đơn trong tab hiện tại
    $scope.calculateTotalForList = function (chiTietHoaDons) {
        if (!Array.isArray(chiTietHoaDons)) return 0; // Kiểm tra nếu không phải danh sách

        return chiTietHoaDons.reduce(function (total, cthd) {
            return total + $scope.calculateTotalAmount(cthd); // Sử dụng hàm tính tổng cho từng chi tiết
        }, 0); // Tính tổng tiền bằng cách cộng dồn
    };
    $scope.selectSanPham = function (sanPham) {
        $scope.selectedSanPham = sanPham;  // Lưu sản phẩm đã chọn
        $scope.soLuong = 1; // Đặt lại số lượng
        $scope.currentPage = 0; // Nếu không có page được truyền vào, dùng trang 0
        $http.get(`http://localhost:8083/chi-tiet-san-pham/getAllCTSP?idSP=${sanPham.id}`)
            .then(function (response) {
                console.log(response);
                $scope.products = response.data; // Gán danh sách sản phẩm từ dữ liệu trả về
            })
            .catch(function (error) {
                data
                $scope.errorMessage = 'Lỗi khi lấy sản phẩm: ' + error.data;
                console.error($scope.errorMessage);
            });
        $('#productModal').modal('show');  // Hiển thị modal
    };

    $scope.addCTHD = function () {
        // Lấy đối tượng ctsp đã chọn từ biến selectedCTSP
        var ctsp = $scope.selectedCTSP;
        console.log("Thêm sản phẩm:", ctsp);

        const formData = new FormData();
        formData.append('soLuong', ctsp.soLuongCTHD);
        formData.append('giaBan', ctsp.gia);
        formData.append('idCTSP', ctsp.id);
        const selectedTab = $scope.tabs[$scope.selectedTab];
        const selectedIdHD = selectedTab.idHD;

        if (selectedTab) {
            const selectedIdHD = selectedTab.idHD;
            formData.append('idHD', selectedIdHD);
        } else {
            console.log("Không có tab đã chọn hoặc idHD không tồn tại");
            return;
        }
        for (let [key, value] of formData.entries()) {
            console.log(`${key}: ${value}`); // In ra key và value
        }
        $http.post('http://localhost:8083/chitiethoadon/add', formData, {

            headers: {
                'Content-Type': undefined
            }
        }
        )
            .then(function (response) {
                console.log(response.data);
                showSuccessAlert("Thêm thành công!");
                if (selectedTab && selectedTab.idHD) {
                    $scope.getCTSPByIdHD(selectedTab.idHD, selectedTab.currentPage);
                }
                $scope.selectSanPham($scope.selectedSanPham);
            })

            .catch(function (error) {
                showDangerAlert("Thêm thất bại!");
                $scope.getCTSPByIdHD(selectedIdHD, selectedTab.currentPage); // Truyền vào trang hiện tại của tab
                console.log('Lỗi:', error.data);
                $scope.selectSanPham($scope.selectedSanPham);
            });
        $('#confirmAddInvoiceDetailModal').modal('hide');
    }
    $scope.deleteCTHD = function (idCTHD) {
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
    $scope.showActionModal = function (tabIndex) {
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
    $scope.closeModal = function () {
        $('#actionModal').modal('hide');  // Đóng modal khi nhấn "Hủy"
    };
    $scope.closeModalAddInvoice = function () {
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
    $scope.test = function () {
        $http.get('http://localhost:8083/hoadon/tuDongXoaHoaDon')
            .then(function (response) {
                console.log('Xóa hóa đơn thành công', response);
            }, function (error) {
                console.error('Có lỗi xảy ra khi xóa hóa đơn', error);
            });
    };
    // Gọi hàm lấy hóa đơn khi khởi tạo controller
    $scope.getHDTaiQuay();

}
