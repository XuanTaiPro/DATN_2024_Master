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

        // Kiểm tra và xử lý khoảng giá
        if ($scope.filters.giaRange) {
            var giaParts = $scope.filters.giaRange.split(',');

            // Chuyển giaMin và giaMax sang kiểu Number, nếu không hợp lệ thì gán null
            $scope.filters.giaMin = !isNaN(giaParts[0]) ? parseFloat(giaParts[0]) : null;
            $scope.filters.giaMax = giaParts[1] === 'null' || giaParts[1] === 'undefined' || isNaN(giaParts[1]) ? null : parseFloat(giaParts[1]);
        }
        else {
            // Nếu không có khoảng giá chọn, gán giaMin và giaMax là null
            $scope.filters.giaMin = 0;
            $scope.filters.giaMax = 1000000;
        }

        // Kiểm tra và xử lý số ngày sử dụng
        var soNgaySuDung = $scope.filters.soNgaySuDung || ''; // Nếu không có giá trị thì là chuỗi rỗng

        // Gọi API với các tham số đã cập nhật
        $http.get(`http://localhost:8083/chi-tiet-san-pham/page?page=${$scope.currentPage}&idSP=${$scope.idSP}&soNgaySuDung=${soNgaySuDung}&giaMin=${$scope.filters.giaMin || ''}&giaMax=${$scope.filters.giaMax || ''}&trangThai=${$scope.filters.trangThai || ''}`)
            .then(function(response) {
                console.log(response.data); // Kiểm tra dữ liệu trả về
                $scope.products = response.data.content; // Gán danh sách sản phẩm từ dữ liệu trả về
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



    $scope.usageDaysError = false;

    $scope.validateUsageDays = function () {
        if (($scope.product.soNgaySuDung && $scope.product.soNgaySuDungInput) ||
            (!$scope.product.soNgaySuDung && !$scope.product.soNgaySuDungInput)) {
            $scope.usageDaysError = true;
        } else {
            $scope.usageDaysError = false;
        }
    };

    $scope.isNsxValid = function(nsx, hsd, ngayNhap) {
        if (!nsx || !hsd || !ngayNhap) return true; // Không kiểm tra nếu thiếu dữ liệu
        const nsxDate = new Date(nsx);
        const hsdDate = new Date(hsd);
        const ngayNhapDate = new Date(ngayNhap);
        const currentDate = new Date();

        return (
            nsxDate <= ngayNhapDate && // NSX phải là ngày trong quá khứ hoặc ngày hiện tại, và trước ngày nhập
            nsxDate <= hsdDate && // NSX không được sau HSD
            nsxDate <= currentDate && // NSX không được sau ngày hiện tại
            nsx !== undefined
        );
    };

    $scope.isNgayNhapValid = function(ngayNhap, nsx, hsd) {
        if (!ngayNhap || !nsx || !hsd) return true; // Không kiểm tra nếu thiếu dữ liệu

        const currentDate = new Date();
        const ngayNhapDate = new Date(ngayNhap);
        const nsxDate = new Date(nsx);

        // Ngày nhập phải là ngày hiện tại hoặc trong quá khứ
        const isNgayNhapValid = ngayNhapDate <= currentDate;

        // Ngày nhập phải là ngày sau ngày sản xuất
        const isNgayNhapAfterNsx = ngayNhapDate >= nsxDate;

        return isNgayNhapValid && isNgayNhapAfterNsx;
    };

    $scope.isHsdValid = function(hsd, nsx, ngayNhap) {
        if (!hsd || !nsx || !ngayNhap) return true; // Không kiểm tra nếu thiếu dữ liệu

        const nsxDate = new Date(nsx);
        const hsdDate = new Date(hsd);
        const ngayNhapDate = new Date(ngayNhap);
        const isHsdAfterNsx = hsdDate > nsxDate;

        return  isHsdAfterNsx;
    };



    $scope.isNgaynhapTrongValid = function(ngayNhap) {
        return ngayNhap !=undefined;
    };
    $scope.ishsdTrongValid = function(hsd) {
        return hsd !=undefined;
    };
    $scope.isnsxTrongValid = function(nsx) {
        return nsx !=undefined;
    };

    $scope.isHsdValid = function(hsd, nsx) {
        if (!hsd || !nsx) return true; // Không kiểm tra nếu thiếu dữ liệu
        return new Date(hsd) > new Date(nsx)&&  hsd !== undefined; // HSD phải sau NSX
    };


    $scope.isGiaValidTrong = function(gia) {
        return gia !== undefined ;
    };
    $scope.isGiaValid = function(gia) {
        return gia !== undefined && gia >= 1 ;
    };
    $scope.isImgValid = function(img) {
        return img !== undefined  ;
    };
    $scope.isSoLuongTrongValid = function(soLuong) {
        return soLuong !=undefined;
    };
    $scope.isSoLuongValid = function(soLuong) {
        return !isNaN(soLuong) && Number(soLuong) >= 1;
    };
    $scope.isImgValid = function(imagePreviews) {
        return imagePreviews && imagePreviews.length > 0;
    };


    $scope.formSubmitted = false;
    $scope.addProduct = function (product) {

        // $scope.formSubmitted = true;
        // $scope.usageDaysError = true;


        const productData = {
            gia: product.gia,
            soNgaySuDung: product.soNgaySuDung != null ? product.soNgaySuDung : product.soNgaySuDungInput,
            ngayNhap: moment(product.ngayNhap).format('YYYY-MM-DDTHH:mm:ss'),
            soLuong: product.soLuong,
            trangThai: 1,
            idSP: $scope.idSP,
            nsx: moment(product.nsx).format('YYYY-MM-DDTHH:mm:ss'),
            hsd: moment(product.hsd).format('YYYY-MM-DDTHH:mm:ss'),
            linkAnhList: product.linkAnhList || [] // Đảm bảo danh sách link không bị null
        };

        console.log('Thông tin sản phẩm gửi đi:', productData);

        // Gửi dữ liệu JSON lên server
        $http.post('http://localhost:8083/chi-tiet-san-pham/add', productData, {
            headers: {'Content-Type': 'application/json'}
        })
            .then(function (response) {
                $('#productModal').modal('hide');
                $scope.product = {};
                $scope.getUniqueSoNgaySuDung();
                $scope.getAllProducts();
                showSuccessAlert("Thêm thành công!");
                // $scope.clearForm();
            })
            .catch(function (error) {
                $scope.getAllProducts($scope.currentPage); // Gọi lại hàm với trang hiện tại
                showDangerAlert("Thêm thất bại!");
                console.error('Lỗi:', error);

                // Hiển thị thông báo lỗi rõ ràng hơn
                if (error.data && error.data.message) {
                    $scope.errorMessage = 'Lỗi khi thêm sản phẩm: ' + error.data.message;
                } else {
                    $scope.errorMessage = 'Lỗi không xác định: ' + JSON.stringify(error);
                }
            });
    };

    // Cập nhật chi tiết sản phẩm
    $scope.deleteProduct = function (productId) {
        showConfirm("Bạn có muốn ngưng sản phẩm này không?",()=>{
            $http({
                method: 'DELETE',
                url: 'http://localhost:8083/chi-tiet-san-pham/delete', // Đường dẫn đến API
                data: {id: productId}, // Gửi id sản phẩm qua request body
                headers: {"Content-Type": "application/json;charset=utf-8"}
            }).then(function (response) {
                showSuccessAlert("Cập nhật trạng thái thành công!");
                $scope.getAllProducts();

            }, function (error) {
                $scope.getAllProducts();
                showDangerAlert("Cập nhật trạng thái thất bại!");
            });})

    };

    $scope.activateProduct = function (productId) {
        showConfirm("Bạn có muốn kích hoạt sản phẩm này không?",()=>{
            $http({
            method: 'PUT',
            url: 'http://localhost:8083/chi-tiet-san-pham/activateProduct', // Đường dẫn đến API
            data: {id: productId}, // Gửi id sản phẩm qua request body
            headers: {"Content-Type": "application/json;charset=utf-8"}
        }).then(function (response) {
            showSuccessAlert("Cập nhật trạng thái thành công!");
            $scope.getAllProducts();

        }, function (error) {
            $scope.getAllProducts();
            showDangerAlert("Cập nhật trạng thái thất bại!");
        });})

    };
    $scope.usageDaysError = false;

    $scope.validateUsageDays = function () {
        if (($scope.product.soNgaySuDung && $scope.product.soNgaySuDungInput) ||
            (!$scope.product.soNgaySuDung && !$scope.product.soNgaySuDungInput)) {
            $scope.usageDaysError = true;
        } else {
            $scope.usageDaysError = false;
        }
    };

    $scope.clearErrors=function (){
        $scope.formSubmitted = true;
        $scope.usageDaysError = true;
        $scope.newLoHangErrors = {
            nsx: null,
            ngayNhap: null,
            hsd: null,
            soLuong: null
        };

    }

    $scope.isGiaValidUD = function() {
        return $scope.productDetail.gia && $scope.productDetail.gia.trim() !== '';
    };
    $scope.updateProduct = function () {
        $scope.formSubmitted = true;

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
                showSuccessAlert("Cập nhật thành công!");
            })
            .catch(function (error) {
                // console.log(productDetail);
                showDangerAlert("Thất bại!");
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
        // $('#productModal').modal('hide'); // Đóng modal
    };
    $scope.calculateTotalPrice = function () {
        const gia = parseFloat($scope.productDetail.gia) || 0;
        const tienGiam = parseFloat($scope.productDetail.tienGiam) || 0;
        $scope.totalPrice = gia + tienGiam;
    };

// Gọi hàm mỗi khi giá trị `productDetail.gia` hoặc `productDetail.tienGiam` thay đổi
    $scope.$watch('productDetail.gia', $scope.calculateTotalPrice);
    $scope.$watch('productDetail.tienGiam', $scope.calculateTotalPrice);
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
                        $scope.product.linkAnhList.push('img/SanPham/' + file.name);

                        // Nếu cần, bạn có thể thêm URL tạm thời vào imagePreviews của productDetail
                        $scope.productDetail.imagePreviews.push(e.target.result);
                        // Thêm vào selectedImages của productDetail nếu cần
                        $scope.productDetail.selectedImages.push(e.target.result);
                        // Thêm đường dẫn vào linkAnhList của productDetail
                        $scope.productDetail.linkAnhList.push('img/SanPham/' + file.name);
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
        if ($scope.showAddLoHangForm) {
            $scope.clearErrors();
        }
    };


    $scope.newLoHangErrors = {};
    $scope.newLoHang = {};

    $scope.validateNgayNhap = function(ngayNhap, nsx, hsd) {
        const now = new Date();
        const ngayNhapDate = new Date(ngayNhap);
        const nsxDate = new Date(nsx);
        const hsdDate = new Date(hsd);

        if (!ngayNhap) {
            $scope.newLoHangErrors.ngayNhap = "Ngày nhập không được bỏ trống.";
        } else if (ngayNhapDate > now) {
            $scope.newLoHangErrors.ngayNhap = "Ngày nhập phải là ngày hiện tại hoặc trong quá khứ.";
        } else if (ngayNhapDate < nsxDate) {
            $scope.newLoHangErrors.ngayNhap = "Ngày nhập không được trước ngày sản xuất.";
        } else if (ngayNhapDate > hsdDate) {
            $scope.newLoHangErrors.ngayNhap = "Ngày nhập không được sau hạn sử dụng.";
        } else {
            $scope.newLoHangErrors.ngayNhap = null; // Không có lỗi
        }
    };

    $scope.validateNSX = function(nsx, ngayNhap) {
        const now = new Date();
        const nsxDate = new Date(nsx);
        const ngayNhapDate = new Date(ngayNhap);

        if (!nsx) {
            $scope.newLoHangErrors.nsx = "Ngày sản xuất không được bỏ trống.";
        } else if (nsxDate > now) {
            $scope.newLoHangErrors.nsx = "Ngày sản xuất không được lớn hơn ngày hiện tại.";
        } else if (ngayNhap && nsxDate > ngayNhapDate) {
            $scope.newLoHangErrors.nsx = "Ngày sản xuất không được sau ngày nhập.";
        } else {
            $scope.newLoHangErrors.nsx = null; // Không có lỗi
        }
    };

    $scope.validateHSD = function(hsd, nsx, ngayNhap) {
        const now = new Date();
        const hsdDate = new Date(hsd);
        const nsxDate = new Date(nsx);
        const ngayNhapDate = new Date(ngayNhap);
        const minDifferenceInDays = 30;

        if (!hsd) {
            $scope.newLoHangErrors.hsd = "Hạn sử dụng không được bỏ trống.";
        } else if (hsdDate < now) {
            $scope.newLoHangErrors.hsd = "Hạn sử dụng phải là ngày hiện tại hoặc trong tương lai.";
        } else if (nsx && hsdDate < nsxDate) {
            $scope.newLoHangErrors.hsd = "Hạn sử dụng không được trước ngày sản xuất.";
        } else if (ngayNhap && hsdDate < ngayNhapDate) {
            $scope.newLoHangErrors.hsd = "Hạn sử dụng không được trước ngày nhập.";
        } else if (nsx && (hsdDate - nsxDate) / (24 * 60 * 60 * 1000) < minDifferenceInDays) {
            $scope.newLoHangErrors.hsd = "Hạn sử dụng phải sau ngày sản xuất ít nhất 30 ngày.";
        } else {
            $scope.newLoHangErrors.hsd = null; // Không có lỗi
        }
    };

    $scope.validateNSXvsHSD = function(nsx, hsd) {
        const nsxDate = new Date(nsx);
        const hsdDate = new Date(hsd);

        if (nsxDate > hsdDate) {
            $scope.newLoHangErrors.nsxHsd = "Ngày sản xuất không được sau hạn sử dụng.";
        } else {
            $scope.newLoHangErrors.nsxHsd = null;
        }
    };
    $scope.$watch('newLoHang.nsx', function(newValue) {
        $scope.validateNSX(newValue, $scope.newLoHang.ngayNhap);
        $scope.validateNSXvsHSD(newValue, $scope.newLoHang.hsd);
    });

    $scope.$watch('newLoHang.hsd', function(newValue) {
        $scope.validateHSD(newValue, $scope.newLoHang.nsx, $scope.newLoHang.ngayNhap);
        $scope.validateNSXvsHSD($scope.newLoHang.nsx, newValue);
    });

    $scope.$watch('newLoHang.ngayNhap', function(newValue) {
        $scope.validateNgayNhap(newValue, $scope.newLoHang.nsx, $scope.newLoHang.hsd);
    });

    $scope.validateSoLuong = function(soLuong) {
        if (!soLuong || isNaN(soLuong) || soLuong <= 0 || !Number.isInteger(+soLuong)) {
            $scope.newLoHangErrors.soLuong = "Số lượng phải là một số nguyên lớn hơn hoặc bằng  1.";
        } else {
            $scope.newLoHangErrors.soLuong = null;
        }
    };
    $scope.$watch('newLoHang.soLuong', function(newValue) {
        $scope.validateSoLuong(newValue);
    });

    $scope.validateLoHang = function() {
        $scope.validateNSX($scope.newLoHang.nsx, $scope.newLoHang.ngayNhap);
        $scope.validateNgayNhap($scope.newLoHang.ngayNhap, $scope.newLoHang.nsx, $scope.newLoHang.hsd);
        $scope.validateHSD($scope.newLoHang.hsd, $scope.newLoHang.nsx, $scope.newLoHang.ngayNhap);
        $scope.validateNSXvsHSD($scope.newLoHang.nsx, $scope.newLoHang.hsd);
        $scope.validateSoLuong($scope.newLoHang.soLuong);
    };




    $scope.showAddLoHangForm = false;

    $scope.addLoHang = function () {
        $scope.formSubmitted = true;

        validateLoHang={};

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
                showSuccessAlert(response.data); // Thông báo từ server
                // Cập nhật lại danh sách lô hàng nếu cần
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

            }, function (error) {
                showDangerAlert("Thêm thất bại!");


            });
    };
    $scope.updateLoHang = function (loHang) {
        $scope.formSubmitted = true;

        validateLoHang={};
        // Kiểm tra thông tin lô hàng trước khi gửi
        if (!loHang.ngayNhap || !loHang.nsx || !loHang.hsd || !loHang.soLuong) {
            // alert("Vui lòng nhập đầy đủ thông tin lô hàng!");
            return;
        }

        var loHangRequest = {
            id: loHang.id,
            idCTSP: $scope.productDetail.id,
            ngayNhap: loHang.ngayNhap,
            nsx: loHang.nsx,
            hsd: loHang.hsd,
            soLuong: loHang.soLuong
        };

        // Gửi yêu cầu PUT đến API
        $http.put('http://localhost:8083/chi-tiet-san-pham/update-loHang', loHangRequest)
            .then(function (response) {
                    showSuccessAlert("Cập nhật lô hàng thành công!"); // Hiển thị thông báo từ server
                    $scope.viewDetail($scope.productDetail.id); // Tải lại chi tiết sản phẩm
                let setOff = document.querySelector(".modal-backdrop")
                setOff.style.display = 'none'
                    $scope.getAllProducts()
                }
            ).catch(function (error) {
            showDangerAlert("Cập nhật thất bại!");
            $scope.viewDetail($scope.productDetail.id);
            let setOff = document.querySelector(".modal-backdrop")
            setOff.style.display = 'none'
            $scope.getAllProducts()

        })

    };

};