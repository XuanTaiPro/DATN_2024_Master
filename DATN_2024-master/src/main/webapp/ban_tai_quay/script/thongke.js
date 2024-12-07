window.thongkeCtrl = function ($scope, $http) {
    $scope.chartData = []; // Biến lưu trữ dữ liệu biểu đồ
    $scope.years = []; // Danh sách các năm
    $scope.months = []; // Danh sách các tháng
    $scope.weeks = []; // Danh sách các tuần
    $scope.days = []; // Danh sách các ngày
    $scope.noDataMessage = ''; // Biến để lưu thông báo nếu không có dữ liệu
    $scope.showChart = true; // Biến để điều khiển việc hiển thị biểu đồ

    // Hàm gọi API để lấy dữ liệu biểu đồ
    $scope.loadChartData = function (year, month, week, day) {
        // Cấu hình tham số cho API
        let params = {};
        if (year) params.year = year;
        if (month) params.month = month;
        if (week) params.week = week;
        if (day) params.day = day;

        // Gửi yêu cầu đến API để lấy dữ liệu
        $http.get('http://localhost:8083/chart/duLieu', {params: params})
            .then(function (response) {
                $scope.chartData = response.data; // Lưu dữ liệu nhận được vào $scope.chartData
                console.log($scope.chartData);
                // Vẽ lại biểu đồ với dữ liệu mới
                $scope.renderChart($scope.chartData);
            })
            .catch(function (error) {
                console.error("Lỗi khi lấy dữ liệu biểu đồ: ", error);
            });
    };

    $scope.selectedChartType = 'line'; // Giá trị mặc định là line

    // Hàm để vẽ biểu đồ
    $scope.renderChart = function (data) {
        if (!data || data.length === 0) {
            // Nếu không có dữ liệu, ẩn biểu đồ và hiển thị thông báo
            $scope.showChart = false;
            $scope.noDataMessage = 'Không có dữ liệu để hiển thị';
            return;
        } else {
            // Nếu có dữ liệu, hiển thị biểu đồ
            $scope.showChart = true;
            $scope.noDataMessage = ''; // Xóa thông báo nếu có dữ liệu
        }

        // Tiến hành vẽ biểu đồ nếu có dữ liệu
        var labels = data.map(function (item) {
            return new Date(item.ngayThanhToan).toLocaleDateString(); // Format ngày cho trục X
        });

        var totalAmounts = data.map(function (item) {
            return item.totalAmount; // Dữ liệu cho trục Y
        });

        if (window.chartInstance) {
            window.chartInstance.destroy();
        }

        var ctx = document.getElementById('salesChart').getContext('2d');
        window.chartInstance = new Chart(ctx, {
            type: $scope.selectedChartType,
            data: {
                labels: labels,
                datasets: [{
                    label: 'Tổng số tiền',
                    data: totalAmounts,
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    borderColor: 'rgba(75, 192, 192, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                scales: {
                    x: {
                        title: {
                            display: true,
                            text: 'Ngày thanh toán'
                        }
                    },
                    y: {
                        title: {
                            display: true,
                            text: 'Tổng số tiền'
                        },
                        beginAtZero: true
                    }
                }
            }
        });
    };

    // Hàm gọi API để lấy các giá trị bộ lọc (năm, tháng, tuần, ngày)
    $scope.loadFilterData = function () {
        // API để lấy danh sách năm, tháng, tuần, ngày (ví dụ API này có sẵn)
        $http.get('http://localhost:8083/chart/filters')
            .then(function (response) {
                $scope.years = response.data.years;
                $scope.months = response.data.months;
                $scope.weeks = response.data.weeks;
                $scope.days = response.data.days;
                $scope.loadChartData($scope.selectedYear, $scope.selectedMonth, $scope.selectedWeek, $scope.selectedDay);
            })
            .catch(function (error) {
                console.error("Lỗi khi lấy dữ liệu bộ lọc: ", error);
            });
    };

    // Gọi hàm loadFilterData khi controller được khởi tạo
    $scope.loadFilterData();

    // Gọi hàm loadChartData khi controller được khởi tạo
    $scope.loadChartData();

    // Hàm để xử lý khi người dùng thay đổi lựa chọn (năm, tháng, tuần, ngày)
    $scope.updateChartData = function () {
        // Cập nhật dữ liệu và vẽ lại biểu đồ
        $scope.loadChartData($scope.selectedYear, $scope.selectedMonth, $scope.selectedWeek, $scope.selectedDay);
    };
};
