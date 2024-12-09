window.chiTietSanPhamCtrl = function ($scope, $routeParams, $http) {
    $scope.idSP = $routeParams.idSP;
    // Khởi tạo sản phẩm
    $scope.product = {
        imagePreviews: [], // Khởi tạo danh sách hình ảnh hiện tại
        selectedImages: [], // Khởi tạo danh sách các hình ảnh đã chọn
        linkAnhList: [], // Khởi tạo danh sách đường dẫn hình ảnh
        loHangListUpdate: []
    };
    $scope.successMessage = "";
    $scope.errorMessage = "";
    $scope.productDetail = {
        id: null,
        gia: null,
        soNgaySuDung: null,
        soNgaySuDungInput: null,
        ngayNhap: null,
        soLuong: null,
        imagePreviews: [], // Khởi tạo danh sách hình ảnh hiện tại
        linkAnhList: [],   // Khởi tạo danh sách đường dẫn hình ảnh
        selectedImages: [], // Khởi tạo danh sách các hình ảnh đã chọn
        trangThai: null // Trạng thái
    };
    $scope.pageSize = 10;  // Số sản phẩm hiển thị trên mỗi trang
    $scope.currentPage = 0; // Trang hiện tại
    $scope.products = []; // Danh sách sản phẩm
    $scope.totalPages = 0; // Tổng số trang
    $scope.totalElements = 0; //
    // Lấy tất cả chi tiết sản phẩm
    $scope.filters = {
        soNgaySuDung: '', // Khởi tạo giá trị mặc định cho số ngày sử dụng
        giaRange: '',      // Khởi tạo giá trị mặc định cho khoảng giá
        trangThai: ''      // Khởi tạo giá trị mặc định cho trạng thái
    };

    $scope.getAllProducts = function (page) {
        $scope.currentPage = page || 0; // Nếu không có page được truyền vào, dùng trang 0

        // Kiểm tra nếu đã chọn khoảng giá
        if ($scope.filters.giaRange) {
            var giaParts = $scope.filters.giaRange.split(',');

            // Kiểm tra và chuyển giaMin và giaMax sang kiểu Number, nếu không hợp lệ thì gán null
            $scope.filters.giaMin = giaParts[0] !== 'null' && giaParts[0] !== 'undefined' ? parseFloat(giaParts[0]) : null;
            $scope.filters.giaMax = giaParts[1] === 'null' || giaParts[1] === 'undefined' ? null : parseFloat(giaParts[1]);
        }

        // Kiểm tra nếu đã chọn số ngày sử dụng
        var soNgaySuDung = $scope.filters.soNgaySuDung || '';

        // Gọi API với các tham số đã cập nhật
        $http.get(`http://localhost:8083/chi-tiet-san-pham/page?page=${$scope.currentPage}&idSP=${$scope.idSP}&soNgaySuDung=${soNgaySuDung}&giaMin=${$scope.filters.giaMin}&giaMax=${$scope.filters.giaMax}&trangThai=${$scope.filters.trangThai}`)
            .then(function(response) {
                console.log(response.data); // Kiểm tra dữ liệu trả về
                $scope.products = response.data.content; // Gán danh sách sản phẩm từ dữ liệu trả về
                // $scope.uniqueSoNgaySuDung = [...new Set($scope.products.map(p => p.soNgaySuDung))]; // Lọc các giá trị số ngày sử dụng độc nhất
                $scope.totalPages = response.data.totalPages; // Lưu tổng số trang
                $scope.pages = Array.from({length: $scope.totalPages}, (v, i) => i); // Tạo danh sách các số trang
                console.log($scope.pages);
            })
            .catch(function(error) {
                $scope.errorMessage = 'Lỗi khi lấy sản phẩm: ' + error.data;
                console.error($scope.errorMessage);
            });
    };
    $scope.getUniqueSoNgaySuDung = function () {
        $http.get(`http://localhost:8083/chi-tiet-san-pham/unique-so-ngay-su-dung?idSP=${$scope.idSP}`)
            .then(function(response) {
                $scope.uniqueSoNgaySuDung = response.data; // Lưu các giá trị duy nhất
            })
            .catch(function(error) {
                console.error('Lỗi khi lấy số ngày sử dụng: ' + error.data);
            });
    };

// Gọi hàm để lấy danh sách số ngày sử dụng duy nhất
    $scope.getUniqueSoNgaySuDung();
    // Chuyển sang trang trước
    $scope.previousPage = function () {
        if ($scope.currentPage > 0) {
            $scope.getAllProducts($scope.currentPage - 1);
        }
    };

    // Chuyển sang trang sau
    $scope.nextPage = function () {
        if ($scope.currentPage < $scope.totalPages - 1) {
            $scope.getAllProducts($scope.currentPage + 1);
        }
    };

    $scope.addProduct = function (product) {
        const formData = new FormData();
        // Thêm thông tin sản phẩm vào FormData
        formData.append('gia', product.gia);
        if (product.soNgaySuDung != null) {
            formData.append('soNgaySuDung', product.soNgaySuDung);
        } else if (product.soNgaySuDung == null) {
            formData.append('soNgaySuDung', product.soNgaySuDungInput);
        }
        formData.append('ngayNhap', moment(product.ngayNhap).format('YYYY-MM-DDTHH:mm:ss'));
        formData.append('soLuong', product.soLuong);
        formData.append('trangThai', 1);
        formData.append('idSP', $scope.idSP);
        formData.append('nsx', moment(product.nsx).format('YYYY-MM-DDTHH:mm:ss'));
        formData.append('hsd', moment(product.hsd).format('YYYY-MM-DDTHH:mm:ss'));
        console.log('Thông tin sản phẩm:', product);
        // Gửi danh sách linkAnhList
        if (product.linkAnhList) {
            product.linkAnhList.forEach(function (link) {
                formData.append('linkAnhList', link); // Thêm đường dẫn vào FormData
            });
        }
        for (var pair of formData.entries()) {
            console.log(pair[0] + ': ' + pair[1]);
        }
        // Gửi FormData lên server
        $http.post('http://localhost:8083/chi-tiet-san-pham/add', formData, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}
        })
            .then(function (response) {
                $('#productModal').modal('hide');
                $scope.product = {};
                $scope.getAllProducts();
                alert('Thêm thành công!');

            })
            .catch(function (error) {
                // Kiểm tra nội dung của đối tượng lỗi
                $scope.getAllProducts($scope.currentPage); // Gọi lại hàm với trang hiện tại

                console.error('Lỗi:', error);

                // Cách hiển thị thông báo lỗi rõ ràng hơn
                if (error.data && error.data.message) {
                    $scope.getAllProducts();

                    $scope.errorMessage = 'Lỗi khi thêm sản phẩm: ' + error.data.message;
                } else {
                    $scope.getAllProducts();

                    $scope.errorMessage = 'Lỗi không xác định: ' + JSON.stringify(error);
                }
            });
    };
    // Cập nhật chi tiết sản phẩm
    $scope.deleteProduct = function (productId) {
        if (confirm("Bạn có chắc chắn muốn xóa sản phẩm này không?")) {
            $http({
                method: 'DELETE',
                url: 'http://localhost:8083/chi-tiet-san-pham/delete', // Đường dẫn đến API
                data: {id: productId}, // Gửi id sản phẩm qua request body
                headers: {"Content-Type": "application/json;charset=utf-8"}
            }).then(function (response) {
                alert(response.data); // Hiển thị thông báo thành công
                // Cập nhật lại danh sách sản phẩm
                $scope.getAllProducts();
                alert(response.data); // Hiển thị thông báo thành công

            }, function (error) {
                $scope.getAllProducts();
                // alert(response.data); // Hiển thị thông báo thành công
            });
        }
    };

    $scope.updateProduct = function () {
        const formData = new FormData();
        // Thêm thông tin sản phẩm vào FormData
        formData.append('id', $scope.productDetail.id);
        formData.append('gia', $scope.productDetail.gia);
        if ($scope.productDetail.soNgaySuDung != null) {
            formData.append('soNgaySuDung', $scope.productDetail.soNgaySuDung);
        } else if ($scope.productDetail.soNgaySuDung == null) {
            formData.append('soNgaySuDung', $scope.productDetail.soNgaySuDungInput);
        }
        formData.append('ngayNhap', moment($scope.productDetail.ngayNhap).format('YYYY-MM-DDTHH:mm:ss'));
        formData.append('soLuong', $scope.productDetail.soLuong);
        formData.append('trangThai', $scope.productDetail.trangThai);
        formData.append('idSP', "1AB2B600");


        // Gửi danh sách linkAnhList
        $scope.productDetail.linkAnhList.forEach(function (link) {
            formData.append('linkAnhList', link);
        });

        for (var pair of formData.entries()) {
            console.log(pair[0] + ': ' + pair[1]);
        }
        // Gửi FormData lên server
        $http.put('http://localhost:8083/chi-tiet-san-pham/update', formData, {
            transformRequest: angular.identity,
            headers: {
                'Content-Type': undefined
            }
        })
            .then(function (response) {
                console.log('Cập nhật sản phẩm thành công: ', response.data);
                // Cập nhật lại danh sách sản phẩm
                $scope.getAllProducts(); // Tải lại danh sách sản phẩm
                $('#userForm').modal('hide'); // Đóng modal
                alert('Cập nhật thành công!'); // Thông báo thành công
            })
            .catch(function (error) {
                // console.log(productDetail);
                console.error('Lỗi:', error);
                $scope.getAllProducts($scope.currentPage); // Gọi lại hàm với trang hiện tại

                if (error.data && error.data.message) {
                    console.log(productDetail);
                    $scope.errorMessage = 'Lỗi khi cập nhật sản phẩm: ' + error.data.message;
                } else {
                    $scope.errorMessage = 'Lỗi không xác định: ' + JSON.stringify(error);
                }
            });
    };
    $scope.clearForm = function () {
        // Xóa mọi dữ liệu trong product
        $scope.productDetail = {}; // Xóa dữ liệu chi tiết sản phẩm
        $scope.product = {
            imagePreviews: [], // Reset danh sách hình ảnh đã chọn
            selectedImages: [], // Reset danh sách tệp hình ảnh
            linkAnhList: [] // Reset danh sách lưu trữ đường dẫn
        };
        $('#userForm').modal('hide'); // Đóng modal
        $('#productModal').modal('hide'); // Đóng modal
    };
    // Xem chi tiết sản phẩm
    $scope.viewDetail = function (productId) {
        const product = $scope.products.find(function (p) {
            return p.id === productId;
        });

        if (product) {
            // Chuyển đổi các trường số thành kiểu số
            $scope.productDetail = {
                ...product,
                gia: Number(product.gia), // Đảm bảo là số
                soNgaySuDung: product.soNgaySuDung, // Gán soNgaySuDung vào productDetail
                soLuong: Number(product.soLuong), // Đảm bảo là số
                // Chuyển đổi các trường khác nếu cần
                // ngaySanXuat: new Date(product.ngaySanXuat),
                // hsd: new Date(product.hsd),
                ngayNhap: new Date(product.ngayNhap),
                trangThai: product.trangThai, // Gán giá trị trangThai
                loHangList: product.listLoHang
            };
            if ($scope.productDetail && $scope.productDetail.loHangList) {
                $scope.productDetail.loHangList.forEach(function (loHang) {
                    loHang.ngayNhap = loHang.ngayNhap ? new Date(loHang.ngayNhap) : null;
                    loHang.nsx = loHang.nsx ? new Date(loHang.nsx) : null;
                    loHang.hsd = loHang.hsd ? new Date(loHang.hsd) : null;
                    loHang.soLuong = loHang.soLuong;
                });
            }
            if ($scope.productDetail.linkAnhList) {
                $scope.productDetail.imagePreviews = $scope.productDetail.linkAnhList;
            } else {
                $scope.productDetail.imagePreviews = []; // Nếu không có hình ảnh
            }
            // Hiển thị modal chi tiết sản phẩm
            // $('#readData').modal('show');
        } else {
            console.error('Không tìm thấy sản phẩm với ID:', productId);
        }
    };
    // Khởi tạo
    $scope.getAllProducts();

    $scope.selectImages = function (element) {
        const files = element.files; // Lấy tất cả các tệp
        $scope.product.imagePreviews = []; // Reset danh sách hình ảnh trong product
        $scope.productDetail.imagePreviews = []; // Reset danh sách hình ảnh trong productDetail
        $scope.product.selectedImages = []; // Reset danh sách hình ảnh đã chọn trong product
        $scope.productDetail.selectedImages = []; // Reset danh sách hình ảnh đã chọn trong productDetail
        $scope.product.linkAnhList = []; // Danh sách lưu trữ đường dẫn trong product
        $scope.productDetail.linkAnhList = []; // Danh sách lưu trữ đường dẫn trong productDetail

        if (files.length > 0) {
            Array.from(files).forEach(function (file) {
                const reader = new FileReader();
                reader.onload = function (e) {
                    $scope.$apply(function () {
                        // Thêm URL tạm thời để hiển thị ảnh vào danh sách hình ảnh của product
                        $scope.product.imagePreviews.push(e.target.result);
                        // Thêm URL tạm thời vào selectedImages của product
                        $scope.product.selectedImages.push(e.target.result);
                        // Thêm đường dẫn vào linkAnhList của product
                        $scope.product.linkAnhList.push('img/' + file.name);

                        // Nếu cần, bạn có thể thêm URL tạm thời vào imagePreviews của productDetail
                        $scope.productDetail.imagePreviews.push(e.target.result);
                        // Thêm vào selectedImages của productDetail nếu cần
                        $scope.productDetail.selectedImages.push(e.target.result);
                        // Thêm đường dẫn vào linkAnhList của productDetail
                        $scope.productDetail.linkAnhList.push('img/' + file.name);
                    });
                };
                reader.readAsDataURL(file);
            });
        }
    };


    $scope.removeImage = function (index, isDetail) {
        if (isDetail) {
            // Xóa hình ảnh trong productDetail
            if (index >= 0 && index < $scope.productDetail.imagePreviews.length) {
                $scope.productDetail.imagePreviews.splice(index, 1);
                if ($scope.productDetail.selectedImages && $scope.productDetail.selectedImages.length > index) {
                    $scope.productDetail.selectedImages.splice(index, 1);
                }
                if ($scope.productDetail.linkAnhList && $scope.productDetail.linkAnhList.length > index) {
                    $scope.productDetail.linkAnhList.splice(index, 1);
                }
            }
        } else {
            // Xóa hình ảnh trong product
            if (index >= 0 && index < $scope.product.imagePreviews.length) {
                $scope.product.imagePreviews.splice(index, 1);
                if ($scope.product.selectedImages && $scope.product.selectedImages.length > index) {
                    $scope.product.selectedImages.splice(index, 1);
                }
                if ($scope.product.linkAnhList && $scope.product.linkAnhList.length > index) {
                    $scope.product.linkAnhList.splice(index, 1);
                }
            }
        }
    };
    $scope.showAddLoHangForm = false; // Ẩn form thêm lô hàng mặc định

// Toggle hiển thị form thêm lô hàng
    $scope.toggleAddLoHangForm = function () {
        $scope.showAddLoHangForm = !$scope.showAddLoHangForm;
    };

    $scope.newLoHang = {}; // Biến chứa thông tin lô hàng mới
    $scope.addLoHang = function () {
        if (!$scope.newLoHang.ngayNhap || !$scope.newLoHang.nsx || !$scope.newLoHang.hsd || !$scope.newLoHang.soLuong) {
            alert("Vui lòng nhập đầy đủ thông tin lô hàng!");
            return;
        }

        // Tạo đối tượng để gửi lên server
        var loHangRequest = {
            idCTSP: $scope.productDetail.id, // Giả sử productDetail có idCTSP
            ngayNhap: $scope.newLoHang.ngayNhap,
            nsx: $scope.newLoHang.nsx,
            hsd: $scope.newLoHang.hsd,
            soLuong: $scope.newLoHang.soLuong
        };

        // Gửi request đến API Spring Boot
        $http.post('http://localhost:8083/chi-tiet-san-pham/add-lohang', loHangRequest)
            .then(function (response) {
                alert(response.data); // Thông báo từ server
                // Cập nhật lại danh sách lô hàng nếu cần
                // $scope.productDetail.loHangList.push({
                //     ngayNhap: new Date($scope.newLoHang.ngayNhap),
                //     nsx: new Date($scope.newLoHang.nsx),
                //     hsd: new Date($scope.newLoHang.hsd),
                //     soLuong: $scope.newLoHang.soLuong
                // });

                $scope.viewDetail($scope.productDetail.id);
                $scope.newLoHang = {}; // Reset form
                $scope.showAddLoHangForm = false; // Ẩn form
            }, function (error) {
                // alert("Lỗi khi thêm lô hàng: " + error);
                // $scope.productDetail.loHangList.push({
                //     ngayNhap: new Date($scope.newLoHang.ngayNhap),
                //     nsx: new Date($scope.newLoHang.nsx),
                //     hsd: new Date($scope.newLoHang.hsd),
                //     soLuong: $scope.newLoHang.soLuong
                // });
                $scope.viewDetail($scope.productDetail.id);
                $scope.newLoHang = {}; // Reset form
                $scope.showAddLoHangForm = false; // Ẩn
                $http.get(`http://localhost:8083/chi-tiet-san-pham/page?page=${$scope.currentPage}&idSP=${$scope.idSP}`)
                    .then(function (response) {
                        console.log(response.data); // Kiểm tra dữ liệu trả về
                        $scope.products = response.data.content; // Gán danh sách sản phẩm từ dữ liệu trả về
                        $scope.uniqueSoNgaySuDung = [...new Set($scope.products.map(p => p.soNgaySuDung))];
                        $scope.totalPages = response.data.totalPages; // Lưu tổng số trang
                        $scope.pages = Array.from({length: $scope.totalPages}, (v, i) => i); // Tạo danh sách các số trang
                        console.log($scope.pages);
                    })
                let setOff = document.querySelector(".modal-backdrop")
                setOff.style.display = 'none'


            });
    };
    $scope.updateLoHang = function (loHang) {
        // Kiểm tra thông tin lô hàng trước khi gửi
        if (!loHang.ngayNhap || !loHang.nsx || !loHang.hsd || !loHang.soLuong) {
            alert("Vui lòng nhập đầy đủ thông tin lô hàng!");
            return;
        }

        // Tạo đối tượng yêu cầu để gửi đến server
        var loHangRequest = {
            id: loHang.id, // ID của lô hàng cần cập nhật
            idCTSP: $scope.productDetail.id, // ID sản phẩm chi tiết
            ngayNhap: loHang.ngayNhap,
            nsx: loHang.nsx,
            hsd: loHang.hsd,
            soLuong: loHang.soLuong
        };

        // Gửi yêu cầu PUT đến API
        $http.put('http://localhost:8083/chi-tiet-san-pham/update-loHang', loHangRequest)
            .then(function (response) {
                    // Xử lý thành công
                    alert(response.data); // Hiển thị thông báo từ server
                    $scope.viewDetail($scope.productDetail.id); // Tải lại chi tiết sản phẩm
                let setOff = document.querySelector(".modal-backdrop")
                setOff.style.display = 'none'
                    $scope.getAllProducts()
                }
            ).catch(function (error) {
            console.log(error);
            $scope.viewDetail($scope.productDetail.id);
            let setOff = document.querySelector(".modal-backdrop")
            setOff.style.display = 'none'
            $scope.getAllProducts()

        })

    };

};