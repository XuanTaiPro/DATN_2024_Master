let email = document.querySelector("#username")
let passw = document.querySelector("#password")
let submit = document.querySelector(".btn-next-step")

const getData = () => {
    const loginNV = fetch('http://localhost:8083/login/manager')
    const result = loginNV.then((resp) => {
        return resp.json()
    }).then(data => {
        return data
    })

    console.log(result)
}

submit.addEventListener("click", getData())