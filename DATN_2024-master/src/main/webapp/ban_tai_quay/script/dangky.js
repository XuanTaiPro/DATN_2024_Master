document.getElementById('registerForm')?.addEventListener('submit', function (event) {
    event.preventDefault(); // Ngăn chặn form gửi đi

    // Reset các lỗi trước đó
    resetErrors();

    // Lấy giá trị từ các trường
    var name = document.getElementById('name')?.value.trim();
    var email = document.getElementById('email')?.value.trim();
    var phone = document.getElementById('phone')?.value.trim();
    var address = document.getElementById('address')?.value.trim();
    var gender = document.querySelector('input[name="gender"]:checked')?.value;
    var passw = document.getElementById('password')?.value.trim();

    // Kiểm tra tính hợp lệ của các trường
    var isValid = true;

    // Kiểm tra tên
    if (!name) {
        showError('NAME', 'Họ và tên không được để trống');
        isValid = false;
    }

    // Kiểm tra email
    if (!email || !validateEmail(email)) {
        showError('EMAIL', 'Email không hợp lệ');
        isValid = false;
    }

    // Kiểm tra số điện thoại
    if (!phone || !validatePhone(phone)) {
        showError('PHONE', 'Số điện thoại không hợp lệ');
        isValid = false;
    }

    // Kiểm tra địa chỉ
    if (!address) {
        showError('ADDRESS', 'Địa chỉ không được để trống');
        isValid = false;
    }

    // Kiểm tra mật khẩu
    if (!passw || passw.length < 6) {
        showError('PASSWORD', 'Mật khẩu phải từ 6 ký tự trở lên');
        isValid = false;
    }

    // Nếu tất cả hợp lệ, gửi dữ liệu đến server
    if (isValid) {
        var khachHang = {
            ten: name,
            email: email,
            sdt: phone,
            diaChi: address,
            gioiTinh: gender,
            passw: passw,
        };

        // Gửi dữ liệu đến server qua API
        fetch('http://localhost:8083/khachhang/dangKy', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(khachHang),
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error('Lỗi: ' + response.status);
                }
            })
            .then(data => {
                console.log("Dữ liệu gửi đi: ", JSON.stringify(khachHang));
                alert('Đăng ký thành công!');
                document.getElementById('registerForm')?.reset();
            })
            .catch(error => alert('Lỗi đăng ký: ' + error.message))
            .finally(() => {
                console.log('Đăng ký hoàn tất.');
            });
    }
});

function showError(field, message) {
    var errorElement = document.getElementById('error-' + field);
    if (errorElement) {
        errorElement.textContent = message;
        errorElement.style.color = 'red'; // Thêm CSS tô đỏ
    }
}

function resetErrors() {
    var errorMessages = document.querySelectorAll('.error-message');
    errorMessages.forEach(function (msg) {
        msg.textContent = '';
        msg.style.color = ''; // Xóa màu khi reset
    });
}

function validateEmail(email) {
    var re = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
    return re.test(email);
}

function validatePhone(phone) {
    var re = /^[0-9]{10,15}$/;
    return re.test(phone);
}
