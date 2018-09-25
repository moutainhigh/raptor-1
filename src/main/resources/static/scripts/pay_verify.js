
const log = console.log.bind(console)

function initForm() {

}

function initSubmitBtn() {
  $('.btn-submit').click(function () {
    let params = $('form').serializeArray()
    alert('数据发送了' + JSON.stringify(params))
  })
}
function initSMSBtn() {
  let count = config.smsCount
  let disabled = false
  let interval = 1000
  $('.sms .btn-send').click(function () {
    if (!disabled) {
      let $btn = $(this)
      $btn.addClass('count-down').text(count + 's')
      let timer = setInterval(function () {
        count--
        if (count <= 0) {
          clearInterval(timer)
          $btn.removeClass('count-down').text('重发验证码')
          count = config.smsCount
          disabled = false
        } else {
          $btn.text(count + 's')
        }
      }, interval)

      disabled = true
    }
  })
}

function main(params) {
  initSubmitBtn()
  initSMSBtn()
}

$(document).ready(main)