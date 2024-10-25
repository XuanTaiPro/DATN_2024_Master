var form = document.getElementById("myForm"),
    imgInput = document.querySelector(".img"),
    file = document.getElementById("imgInput"),
    submitBtn = document.querySelector(".submit");

file.onchange = function() {
    if (file.files[0].size < 1000000) {
        var fileReader = new FileReader();

        fileReader.onload = function(e) {
            imgUrl = e.target.result;
            imgInput.src = imgUrl;
        }

        fileReader.readAsDataURL(file.files[0]);
    } else {
        alert("This file is too large!");
    }
}



window.nhanvienCtrl = function ($scope) {
    const apiNhanVien = "http://localhost:8080/nhanvien";

    $scope.listNhanVien = [];
    $scope.nhanVien = {};
    $scope.selectedNhanVien = {};
    $scope.loadNhanVien = function() {
        $http.get(apiNhanVien)
            .then(function(response) {
                $scope.listNhanVien = response.data;
                console.log('oke rồi')
            })
            .catch(function(error) {
                console.error("Error loading employee data:", error);
            });
    };
    $scope.loadNhanVien();

}


